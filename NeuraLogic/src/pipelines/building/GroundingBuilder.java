package pipelines.building;

import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundTemplate;
import grounding.Grounder;
import grounding.GroundingSample;
import networks.structure.building.Neuralizer;
import ida.utils.tuples.Pair;
import pipelines.ConnectAfter;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;
import networks.computation.training.NeuralSample;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds a pipeline performing grounding and a possible series of transformations of the Template and GroundedTemplate (GroundingSample)
 */
public class GroundingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(GroundingBuilder.class.getName());

    Grounder grounder;

    public GroundingBuilder(Settings settings) {
        super(settings);
        grounder = Grounder.getGrounder(settings);
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
     * todo must create separate pipe classes for each here
     * @return
     */
    @Override
    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> pipeline = new Pipeline<>("GroundingPipeline");

        Pipe<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingSamples = pipeline.registerStart(new Pipe<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>>("GroundingSamplesStreaming") {
            @Override
            public Stream<GroundingSample> apply(Pair<Template, Stream<LogicSample>> templateStreamPair) {
                if (templateStreamPair.s.isParallel()) {
                    LOG.warning("Samples come in parallel into grounder already, this may have negative effect on shared grounding."); //can be: https://stackoverflow.com/questions/29216588/how-to-ensure-order-of-processing-in-java8-streams
                    if (!settings.oneQueryPerExample)
                        templateStreamPair.s = templateStreamPair.s.sequential();
                }
                final GroundingSample.Wrap lastGroundingWrap = new GroundingSample.Wrap(null);
                templateStreamPair.s.map(sample -> {
                    GroundingSample groundingSample = new GroundingSample(sample, templateStreamPair.r);
                    if (sample.query.evidence.equals(lastGroundingWrap.getExample())) {
                        groundingSample.grounding = lastGroundingWrap;
                        groundingSample.groundingComplete = true;
                    } else {
                        lastGroundingWrap.setExample(sample.query.evidence);
                    }
                    return groundingSample;
                });
                return null;
            }
        });

        ConnectAfter<Stream<GroundingSample>> nextPipe;
        if (settings.parallelGrounding) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> parallelPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("ParallelizationPipe") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    return pairStream.parallel();
                }
            });
            groundingSamples.connectAfter(parallelPipe);
            nextPipe = parallelPipe;
        } else {
            nextPipe = groundingSamples;
        }

        if (settings.supervisedTemplateGraphPruning) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> templateReducing = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("SupervisedTemplateReducing") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> templateSamples) {
                    return templateSamples.map(sample -> {
                        sample.template = sample.template.prune(sample.query);
                        sample.groundingComplete = false;
                        return sample;
                    });
                }
            });
            nextPipe.connectAfter(templateReducing);
            nextPipe = templateReducing;
        }

        if (settings.sequentiallySharedGroundings) {    //contradicts parallel grounding - checked in settings
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("SequentiallySharedGroundingPipe") {
                GroundTemplate stored = null;

                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    if (pairStream.isParallel()) {
                        LOG.severe("Sequential sharing in a parallel grounding stream.");
                        pairStream.sequential();
                    }
                    return pairStream.map(gs -> {
                        if (gs.grounding.getGroundTemplate() == null || !gs.groundingComplete) {
                            gs.grounding.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template, stored));  //todo test for case with multiple queries on 1 example with sequential sharing (do we still increment against the last query here?)
                        }
                        return gs;
                    });
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe = groundingPipe;
        } else if (settings.globallySharedGroundings) {

            //Stream TERMINATING operation!
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("GlobalSharingGroundingPipe") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> groundingSampleStream) {
                    List<GroundingSample> groundingSampleList = groundingSampleStream.collect(Collectors.toList());
                    groundingSampleList = grounder.globalGroundingSample(groundingSampleList);
                    return groundingSampleList.stream();
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe = groundingPipe;
        } else {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("NormalGroundingPipe") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    return pairStream.map(gs -> {
                        if (gs.grounding.getGroundTemplate() == null) {
                            gs.grounding.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template));
                        } else if (!gs.groundingComplete) {
                            gs.grounding.setGroundTemplate(grounder.groundRulesAndFacts(gs.query.evidence, gs.template, gs.grounding.getGroundTemplate()));
                            return gs;
                        }
                        return gs;
                    });
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe = groundingPipe;
        }

        if (settings.explicitSupervisedGroundTemplatePruning) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundReducingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("SupervisedGroundReducingPipe") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    return pairStream.map(p -> {
                        p.grounding.setGroundTemplate(p.grounding.getGroundTemplate().prune(p.query));
                        return p;
                    });
                }
            });
            nextPipe.connectAfter(groundReducingPipe);
            nextPipe = groundReducingPipe;
        }

        NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, new Neuralizer(grounder));
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.registerEnd(neuralNetsBuilder.buildPipeline());

        nextPipe.connectAfter(neuralizationPipeline);

        return pipeline;
    }
}