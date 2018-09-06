package pipelines.bulding;

import constructs.template.Template;
import grounding.GroundTemplate;
import grounding.Grounder;
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

    @Override
    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> pipeline = new Pipeline<>("GroundingPipeline");

        Pipe<Pair<Template, Stream<LogicSample>>, Stream<Pair<Template, LogicSample>>> templateReducing;
        if (settings.supervisedTemplateGraphPruning) {
            templateReducing = pipeline.registerStart(new Pipe<Pair<Template, Stream<LogicSample>>, Stream<Pair<Template, LogicSample>>>("SupervisedTemplateReducing") {
                @Override
                public Stream<Pair<Template, LogicSample>> apply(Pair<Template, Stream<LogicSample>> templateSamples) {
                    return templateSamples.s.map(sample -> new Pair<>(templateSamples.r.prune(sample.query), sample));
                }
            });
        } else {
            templateReducing = pipeline.registerStart(new Pipe<Pair<Template, Stream<LogicSample>>, Stream<Pair<Template, LogicSample>>>("SimpleTemplateSamplePairing") {
                @Override
                public Stream<Pair<Template, LogicSample>> apply(Pair<Template, Stream<LogicSample>> templateSamples) {
                    return templateSamples.s.map(sample -> new Pair<>(templateSamples.r, sample));
                }
            });
        }

        ConnectAfter<Stream<Pair<Template, LogicSample>>> nextPipe;
        if (settings.parallelGrounding) {
            Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<Template, LogicSample>>> parallelPipe = pipeline.register(new Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<Template, LogicSample>>>("ParallelizationPipe") { //todo move parallelziation all the way up?
                @Override
                public Stream<Pair<Template, LogicSample>> apply(Stream<Pair<Template, LogicSample>> pairStream) {
                    return pairStream.parallel();
                }
            });
            templateReducing.connectAfter(parallelPipe);
            nextPipe = parallelPipe;
        } else {
            nextPipe = templateReducing;
        }

        ConnectAfter<Stream<Pair<LogicSample, GroundTemplate>>> nextPipe0;
        if (settings.sequentiallySharedGroundings) {    //contradicts parallel grounding - checked in settings
            Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>> groundingPipe = pipeline.register(new Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>>("SequentiallySharedGroundingPipe") {
                GroundTemplate stored = null;
                @Override
                public Stream<Pair<LogicSample, GroundTemplate>> apply(Stream<Pair<Template, LogicSample>> pairStream) {
                    if (pairStream.isParallel())
                        LOG.severe("Sequential sharing in a parallel grounding stream.");
                    return pairStream.map(pair -> {
                        GroundTemplate groundTemplate = grounder.groundRulesAndFacts(pair.s.query.evidence, pair.r, stored);
                        return new Pair<>(pair.s, groundTemplate);
                    });
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe0 = groundingPipe;
        } else if (settings.globallySharedGroundings) {
            Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>> groundingPipe = pipeline.register(new Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>>("GlobalSharingGroundingPipe") {
                @Override
                public Stream<Pair<LogicSample, GroundTemplate>> apply(Stream<Pair<Template, LogicSample>> pairStream) {
                    return grounder.globalGround(pairStream);
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe0 = groundingPipe;
        } else {
            Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>> groundingPipe = pipeline.register(new Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>>("NormalGroundingPipe") {
                @Override
                public Stream<Pair<LogicSample, GroundTemplate>> apply(Stream<Pair<Template, LogicSample>> pairStream) {
                    return pairStream.map(pair -> new Pair<>(pair.s, grounder.groundRulesAndFacts(pair.s.query.evidence, pair.r)));
                }
            });
            nextPipe.connectAfter(groundingPipe);
            nextPipe0 = groundingPipe;
        }

        ConnectAfter<Stream<Pair<LogicSample, GroundTemplate>>> nextPipe1;
        if (settings.explicitSupervisedGroundTemplatePruning) {
            Pipe<Stream<Pair<LogicSample, GroundTemplate>>, Stream<Pair<LogicSample, GroundTemplate>>> groundReducingPipe = pipeline.register(new Pipe<Stream<Pair<LogicSample, GroundTemplate>>, Stream<Pair<LogicSample, GroundTemplate>>>("SupervisedGroundReducingPipe") {
                @Override
                public Stream<Pair<LogicSample, GroundTemplate>> apply(Stream<Pair<LogicSample, GroundTemplate>> pairStream) {
                    return pairStream.map(p -> new Pair<>(p.r, p.s.prune(p.r.query)));
                }
            });
            nextPipe0.connectAfter(groundReducingPipe);
            nextPipe1 = groundReducingPipe;
        } else {
            nextPipe1 = nextPipe0;
        }

        Pipe<Stream<Pair<LogicSample, GroundTemplate>>, Stream<NeuralSample>> neuralizationPipe = new Pipe<Stream<Pair<LogicSample, GroundTemplate>>, Stream<NeuralSample>>("SupervisedNeuralizationPipe") {
            @Override
            public Stream<NeuralSample> apply(Stream<Pair<LogicSample, GroundTemplate>> pairStream) {
                return pairStream.map(pair -> grounder.ground(pair.r, pair.s).stream()).flatMap(f -> f);
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
        if (settings.compressNetworks) {
            NetworkReducing compressor = NetworkReducing.getCompressor(settings);
            last = pipeline.register(new Pipe<Stream<NeuralSample>, Stream<NeuralSample>>("NetworkCompressingPipe") {
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
        pipeline.registerStart(first);
        pipeline.registerEnd(last);
        return pipeline;
    }

}
