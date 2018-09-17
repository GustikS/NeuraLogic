package pipelines.bulding;

import grounding.Grounder;
import grounding.GroundingSample;
import networks.structure.transforming.CycleBreaking;
import networks.structure.transforming.NetworkReducing;
import pipelines.ConnectAfter;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralNetsBuilder extends AbstractPipelineBuilder<Stream<GroundingSample>, Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(NeuralNetsBuilder.class.getName());

    Grounder grounder;

    public NeuralNetsBuilder(Settings settings) {
        super(settings);
    }

    public NeuralNetsBuilder(Settings settings, Grounder grounder) {
        super(settings);
        this.grounder = grounder;
    }

    /**
     *
     *
     * @return
     */
    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> pipeline = new Pipeline<Stream<GroundingSample>, Stream<NeuralSample>>("NeuralNetsPostprocessing");

        Pipe<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipe = pipeline.registerStart(new Pipe<Stream<GroundingSample>, Stream<NeuralSample>>("SupervisedNeuralizationPipe") {
            @Override
            public Stream<NeuralSample> apply(Stream<GroundingSample> pairStream) {
                return pairStream.map(pair -> grounder.neuralize(pair).stream()).flatMap(f -> f);
            }
        });

        if (!settings.neuralNetsPostProcessing) {
            pipeline.registerEnd(neuralizationPipe);
            return pipeline;
        }

        ConnectAfter<Stream<NeuralSample>> nextPipe = neuralizationPipe;

        if (settings.removeInputOvermapping){
            //todo next - remove maps by recursive copying (here?)
        }

        if (settings.pruneNetworks) {
            NetworkReducing reducer = NetworkReducing.getReducer(settings);
            Pipe<Stream<NeuralSample>, Stream<NeuralSample>> pruningPipe = pipeline.registerEnd(new Pipe<Stream<NeuralSample>, Stream<NeuralSample>>("NetworkPruningPipe") {
                @Override
                public Stream<NeuralSample> apply(Stream<NeuralSample> neuralSampleStream) {
                    return neuralSampleStream.map(net -> {
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
            Pipe<Stream<NeuralSample>, Stream<NeuralSample>> isoValuePipe = pipeline.registerEnd(new Pipe<Stream<NeuralSample>, Stream<NeuralSample>>("IsoValueCompressionPipe") {
                @Override
                public Stream<NeuralSample> apply(Stream<NeuralSample> neuralSampleStream) {
                    return neuralSampleStream.map(net -> {
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
            Pipe<Stream<NeuralSample>, Stream<NeuralSample>> pipe = pipeline.registerEnd(new Pipe<Stream<NeuralSample>, Stream<NeuralSample>>("CycleBreakingPipe") {
                @Override
                public Stream<NeuralSample> apply(Stream<NeuralSample> neuralSampleStream) {
                    return neuralSampleStream.map(net -> {
                        net.query.evidence = breaker.breakCycles(net.query.evidence);
                        return net;
                    });
                }
            });
            nextPipe.connectAfter(pipe);
            nextPipe = pipe;
        }
        if (settings.expandEmbeddings) {
            //todo at the very end of all pruning, expand the networks to full size with vectorized nodes
        }
        return pipeline;
    }
}
