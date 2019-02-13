package pipelines.building;

import grounding.GroundingSample;
import networks.structure.building.Neuralizer;
import networks.structure.building.NeuralProcessingSample;
import networks.structure.transforming.CycleBreaking;
import networks.structure.transforming.NetworkReducing;
import pipelines.ConnectAfter;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;
import networks.computation.training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralNetsBuilder extends AbstractPipelineBuilder<Stream<GroundingSample>, Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(NeuralNetsBuilder.class.getName());

    Neuralizer neuralizer;

    public NeuralNetsBuilder(Settings settings) {
        super(settings);
    }

    public NeuralNetsBuilder(Settings settings, Neuralizer neuralizer) {
        super(settings);
        this.neuralizer = neuralizer;
    }

    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> pipeline = new Pipeline<Stream<GroundingSample>, Stream<NeuralSample>>("NeuralNetsCreation");

        Pipe<Stream<GroundingSample>, Stream<NeuralProcessingSample>> neuralizationPipe = pipeline.registerStart(new Pipe<Stream<GroundingSample>, Stream<NeuralProcessingSample>>("SupervisedNeuralizationPipe") {
            @Override
            public Stream<NeuralProcessingSample> apply(Stream<GroundingSample> pairStream) {
                return pairStream.map(pair -> neuralizer.neuralize(pair).stream()).flatMap(f -> f);
            }
        });

        ConnectAfter<Stream<NeuralProcessingSample>> nextPipe = neuralizationPipe;
        if (settings.neuralNetsPostProcessing) {
            Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> postprocessingPipeline = buildProcessingPipeline();
            nextPipe.connectAfter(postprocessingPipeline);
            nextPipe = postprocessingPipeline;
        } else {
            LOG.info("Warning - skipping all neural optimization steps!");
        }

        Pipe<Stream<NeuralProcessingSample>, Stream<NeuralSample>> cutoffPipe = pipeline.registerEnd(new Pipe<Stream<NeuralProcessingSample>, Stream<NeuralSample>>("ReleaseMemoryPipe") {
            @Override
            public Stream<NeuralSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
                return neuralProcessingSampleStream.map(s -> new NeuralSample(s.target, s.query));
            }
        });

        return pipeline;
    }

    /**
     * @return
     */
    public Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> buildProcessingPipeline() {
        Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pipeline = new Pipeline<>("NeuralNetsPostprocessing");

        ConnectAfter<Stream<NeuralProcessingSample>> nextPipe = null;

        if (settings.neuralNetsSupervisedPruning) {
            //todo
        }

        if (settings.copyOutInputOvermapping) {
            //todo - remove maps by recursive copying (here?)
        }

        if (settings.pruneNetworks) {
            NetworkReducing reducer = NetworkReducing.getReducer(settings);
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pruningPipe = pipeline.registerEnd(new Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>>("NetworkPruningPipe") {
                @Override
                public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> NeuralProcessingSampleStream) {
                    return NeuralProcessingSampleStream.map(net -> {
                        net.query.evidence = reducer.reduce(net.query.evidence);
                        return net;
                    });
                }
            });
            nextPipe.connectAfter(pruningPipe);
            nextPipe = pruningPipe;
        }
        if (settings.isoValueCompression) { //todo add branch at the beginning of this pipeline to extract all posible weights (over all samples) from the start template
            NetworkReducing compressor = NetworkReducing.getCompressor(settings);
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> isoValuePipe = pipeline.registerEnd(new Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>>("IsoValueCompressionPipe") {
                @Override
                public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> NeuralProcessingSampleStream) {
                    return NeuralProcessingSampleStream.map(net -> {
                        net.query.evidence = compressor.reduce(net.query.evidence);
                        return net;
                    });
                }
            });
            nextPipe.connectAfter(isoValuePipe);
            nextPipe = isoValuePipe;
        }
        if (settings.isoGradientCompression) {
            //todo
        }
        if (settings.cycleBreaking) {
            CycleBreaking breaker = CycleBreaking.getBreaker(settings);
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pipe = pipeline.registerEnd(new Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>>("CycleBreakingPipe") {
                @Override
                public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralSampleStream) {
                    return neuralSampleStream.map(net -> {
                        net.query.evidence = breaker.breakCycles(net.query.evidence);
                        return net;
                    });
                }
            });
            nextPipe.connectAfter(pipe);
            nextPipe = pipe;
        }

        if (settings.collapseWeights) {
            //maybe
        }

        if (settings.expandEmbeddings) {
            //todo at the very end of all pruning, expand the networks to full size with vectorized nodes
        }
        //todo check wieghts dimensions
        //todo process neuron sharing flags from neurons to networks - need to terminate the stream (since a neuron may become shared later on)
        return pipeline;
    }
}
