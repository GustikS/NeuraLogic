package building;

import constructs.building.factories.WeightFactory;
import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.Grounder;
import grounding.GroundingSample;
import grounding.debugging.GroundingDebugger;
import pipelines.ConnectAfter;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.bulding.AbstractPipelineBuilder;
import pipelines.pipes.generic.ExportingPipe;
import pipelines.pipes.generic.StreamParallelizationPipe;
import pipes.specific.*;
import settings.Settings;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Builds a pipeline performing grounding and a possible series of transformations of the Template and GroundedTemplate (GroundingSample)
 */
public class GroundingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> {
    private static final Logger LOG = Logger.getLogger(GroundingBuilder.class.getName());

    protected WeightFactory weightFactory;

    public GroundingBuilder(Settings settings) {
        super(settings);
    }

    public GroundingBuilder(Settings settings, WeightFactory weightFactory) {
        this(settings);
        this.weightFactory = weightFactory;
    }

    /**
     * For efficient grounding, expects ordered stream where samples with the same example come in sequence.
     * That should be the case if samples are created by any standard means (SamplesBuilder).
     * <p>
     * This is a nasty side-effect Stream misuse, but necessary if we want both:
     * 1) Streaming, not to hold all samples/groundings in memory
     * 2) Sharing between samples (side effect) not to ground them independently (to save time and memory when grounding the same thing)
     * <p>
     * A possible solution would be to group examples that require sharing in advance, but that would:
     * 1) require terminating the stream
     * 2) or change LogicSample to carry unique LiftedExample instead of QueryAtom, which breaks the idea of the independent LearningSample(s) (labels)
     * 3) maybe custom SplitIterator could work here - that's the correct solution without side-effects for the sequential processing - todo maybe
     * <p>
     * todo check if sequential processing per-element doesnt break the desired side effects (when something gets deleted after sample is processed, but required for the next one) or blow up memory (when each stage with a side effect stores a separate huge Map)
     *
     * @return
     */
    @Override
    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> buildPipeline() {
        Grounder grounder = Grounder.getGrounder(settings);
        if (this.weightFactory != null) {
            grounder.weightFactory = this.weightFactory;
        }
        this.weightFactory = grounder.weightFactory;

        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> pipeline = new Pipeline<>("GroundingPipeline", this);

        Pipe<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingSamples = pipeline.registerStart(new GroundingSampleWrappingPipe(settings));

        ConnectAfter<Stream<GroundingSample>> nextPipe;
        if (settings.parallelGrounding) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> parallelPipe = pipeline.register(new StreamParallelizationPipe<>());

            groundingSamples.connectAfter(parallelPipe);
            nextPipe = parallelPipe;
        } else {
            nextPipe = groundingSamples;
        }

        if (settings.supervisedTemplateGraphPruning) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> templateReducing = pipeline.register(new SupervisedTemplateReducingPipe());

            nextPipe.connectAfter(templateReducing);
            nextPipe = templateReducing;
        }

        if (settings.groundingMode == Settings.GroundingMode.SEQUENTIAL) {  //sequential = contradicts parallel grounding - checked in settings
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.registerEnd(new SequentiallySharedGroundingPipe(grounder));

            nextPipe.connectAfter(groundingPipe);
            nextPipe = groundingPipe;
        } else if (settings.groundingMode == Settings.GroundingMode.GLOBAL) { //Stream TERMINATING operation!
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.registerEnd(new GlobalSharingGroundingPipe(grounder));

            nextPipe.connectAfter(groundingPipe);
            nextPipe = groundingPipe;
        } else {   //standard grounding of each example individually
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.registerEnd(new StandardGroundingPipe(grounder));

            nextPipe.connectAfter(groundingPipe);
            nextPipe = groundingPipe;
        }

        if (pipeline.exporter != null) {
            ExportingPipe<GroundingSample> exportingPipe = pipeline.registerEnd(new ExportingPipe<>(grounder, pipeline.exporter, grounder.timing, settings));
            nextPipe.connectAfter(exportingPipe);
            nextPipe = exportingPipe;
        }

        if (settings.explicitSupervisedGroundTemplatePruning) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundReducingPipe = pipeline.registerEnd(new SupervisedGroundTemplatePruning());

            nextPipe.connectAfter(groundReducingPipe);
            nextPipe = groundReducingPipe;
        }

        if (settings.debugGrounding) {
            new GroundingDebugger(settings).addDebugStream(pipeline);
        }

        return pipeline;
    }
}