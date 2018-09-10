package pipelines.bulding;

import constructs.template.Template;
import grounding.GroundTemplate;
import grounding.Grounder;
import grounding.GroundingSample;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import networks.structure.transforming.CycleBreaking;
import networks.structure.transforming.NetworkReducing;
import pipelines.ConnectAfter;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

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
     * 2) Sharing between samples (side effect) not to ground them independently (to save time and memory)
     * <p>
     * A possible solution would be to group examples that require sharing in advance, but that would:
     * 1) require terminating the stream
     * 2) or change LogicSample to carry unique LiftedExample instead of QueryAtom, which breaks the idea of the independent LearningSample(s)
     * 3) maybe custom SplitIterator could work here - todo that's the correct solution without side-effects (at least for sequential processing)
     *
     * todo check if sequential processing per-element doesnt break the desired side effects (when something gets deleted after sample is processed, but required for the next one) or blow up memory (when each stage with a side effect stores a separate huge Map)
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
                    if (sample.query.evidence.equals(lastGroundingWrap.example)) {
                        groundingSample.grounding = lastGroundingWrap;
                        groundingSample.groundingComplete = true;
                    } else {
                        lastGroundingWrap.example = sample.query.evidence;
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

        ConnectAfter<Stream<GroundingSample>> nextPipe00;
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
            nextPipe00 = templateReducing;
        } else {
            nextPipe00 = nextPipe;
        }

        ConnectAfter<Stream<GroundingSample>> nextPipe0;
        if (settings.sequentiallySharedGroundings) {    //contradicts parallel grounding - checked in settings
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("SequentiallySharedGroundingPipe") {
                GroundTemplate stored = null;

                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    if (pairStream.isParallel())
                        LOG.severe("Sequential sharing in a parallel grounding stream.");
                    return pairStream.map(gs -> {
                        if (gs.grounding.grounding == null || !gs.groundingComplete) {
                            gs.grounding.grounding = grounder.groundRulesAndFacts(gs.query.evidence, gs.template, stored);  //todo test for case with multiple queries on 1 example with sequential sharing (do we still increment against the last query here?)
                        }
                        return gs;
                    });
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe0 = groundingPipe;
        } else if (settings.globallySharedGroundings) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("GlobalSharingGroundingPipe") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    return grounder.globalGround(pairStream);
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe0 = groundingPipe;
        } else {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("NormalGroundingPipe") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    return pairStream.map(gs -> {
                        if (gs.grounding.grounding == null) {
                            gs.grounding.grounding = grounder.groundRulesAndFacts(gs.query.evidence, gs.template);
                        } else if (!gs.groundingComplete) {
                            gs.grounding.grounding = grounder.groundRulesAndFacts(gs.query.evidence, gs.template, gs.grounding.grounding);
                            return gs;
                        }
                        return gs;
                    });
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe0 = groundingPipe;
        }

        ConnectAfter<Stream<GroundingSample>> nextPipe1;
        if (settings.explicitSupervisedGroundTemplatePruning) {
            Pipe<Stream<GroundingSample>, Stream<GroundingSample>> groundReducingPipe = pipeline.register(new Pipe<Stream<GroundingSample>, Stream<GroundingSample>>("SupervisedGroundReducingPipe") {
                @Override
                public Stream<GroundingSample> apply(Stream<GroundingSample> pairStream) {
                    return pairStream.map(p -> {
                        p.grounding.grounding = p.grounding.grounding.prune(p.query);
                        return p;
                    });
                }
            });
            nextPipe0.connectAfter(groundReducingPipe);
            nextPipe1 = groundReducingPipe;
        } else {
            nextPipe1 = nextPipe0;
        }

        Pipe<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipe = new Pipe<Stream<GroundingSample>, Stream<NeuralSample>>("SupervisedNeuralizationPipe") {
            @Override
            public Stream<NeuralSample> apply(Stream<GroundingSample> pairStream) {
                return pairStream.map(pair -> grounder.ground(pair).stream()).flatMap(f -> f);
            }
        };
        nextPipe1.connectAfter(neuralizationPipe);

        ConnectAfter<Stream<NeuralSample>> nextPipe2;
        if (settings.neuralNetsPostProcessing) {
            Pipeline<Stream<NeuralSample>, Stream<NeuralSample>> postProccess = pipeline.register(neuralPostprocessingPipeline());
            neuralizationPipe.connectAfter(postProccess);
            nextPipe2 = postProccess;
        } else {
            nextPipe2 = neuralizationPipe;
        }

        return pipeline;
    }

    private Pipeline<Stream<NeuralSample>, Stream<NeuralSample>> neuralPostprocessingPipeline() {
        Pipeline<Stream<NeuralSample>, Stream<NeuralSample>> pipeline = new Pipeline<Stream<NeuralSample>, Stream<NeuralSample>>("NeuralNetsPostprocessing");
        Pipe<Stream<NeuralSample>, Stream<NeuralSample>> first = null;
        Pipe<Stream<NeuralSample>, Stream<NeuralSample>> last = null;

        if (settings.reduceNetworks) {
            NetworkReducing reducer = NetworkReducing.getReducer(settings);
            first = last = pipeline.register(new Pipe<Stream<NeuralSample>, Stream<NeuralSample>>("NetworkReducingPipe") {
                @Override
                public Stream<NeuralSample> apply(Stream<NeuralSample> neuralSampleStream) {
                    return neuralSampleStream.map(net -> {
                        net.query.evidence = reducer.reduce(net.query.evidence);
                        return net;
                    });
                }
            });
        }
        if (settings.isoValueCompression) { //todo add branch at the beginning of this pipeline to extract all posible weights (over all samples) from the start template
            NetworkReducing compressor = NetworkReducing.getCompressor(settings);
            last = pipeline.register(new Pipe<Stream<NeuralSample>, Stream<NeuralSample>>("IsoValueCompressionPipe") {
                @Override
                public Stream<NeuralSample> apply(Stream<NeuralSample> neuralSampleStream) {
                    return neuralSampleStream.map(net -> {
                        net.query.evidence = compressor.reduce(net.query.evidence);
                        return net;
                    });
                }
            });
            if (first == null) first = last;
        }
        if (settings.isoGradientCompression){
            //todo
        }
        if (settings.cycleBreaking) {
            CycleBreaking breaker = CycleBreaking.getBreaker(settings);
            last = pipeline.register(new Pipe<Stream<NeuralSample>, Stream<NeuralSample>>("CycleBreakingPipe") {
                @Override
                public Stream<NeuralSample> apply(Stream<NeuralSample> neuralSampleStream) {
                    return neuralSampleStream.map(net -> {
                        net.query.evidence = breaker.breakCycles(net.query.evidence);
                        return net;
                    });
                }
            });
            if (first == null) first = last;
        }
        if (settings.expandEmbeddings){
            //todo at the very end of all pruning, expand the networks to full size with vectorized nodes
        }
        pipeline.registerStart(first);
        pipeline.registerEnd(last);
        return pipeline;
    }

}