package pipelines.building;

import grounding.GroundingSample;
import networks.computation.training.NeuralSample;
import networks.structure.building.NeuralProcessingSample;
import networks.structure.building.Neuralizer;
import pipelines.ConnectAfter;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.IdentityGenPipe;
import pipelines.pipes.specific.CompressionPipe;
import pipelines.pipes.specific.CycleBreakingPipe;
import pipelines.pipes.specific.NetworkFinalizationPipe;
import pipelines.pipes.specific.PruningPipe;
import settings.Settings;
import utils.Utilities;

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
        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> pipeline = new Pipeline<Stream<GroundingSample>, Stream<NeuralSample>>("NeuralNetsCreationPipeline", this);

        Pipe<Stream<GroundingSample>, Stream<NeuralProcessingSample>> neuralizationPipe = pipeline.registerStart(new Pipe<Stream<GroundingSample>, Stream<NeuralProcessingSample>>("SupervisedNeuralizationPipe") {
            @Override
            public Stream<NeuralProcessingSample> apply(Stream<GroundingSample> groundingSampleStream) {
                return groundingSampleStream
                        .peek(s -> LOG.info("Neuralizing sample " + s.toString()))
                        .map(sample -> neuralizer.neuralize(sample).stream())
                        .flatMap(f -> f)
                        .peek(s -> LOG.info("NeuralNet created: " + s.toString()));
            }
        });

        ConnectAfter<Stream<NeuralProcessingSample>> nextPipe = neuralizationPipe;
        if (settings.neuralNetsPostProcessing) {
            Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> postprocessingPipeline = buildProcessingPipeline();
            nextPipe.connectAfter(postprocessingPipeline);
            nextPipe = postprocessingPipeline;
        } else {
            LOG.warning("Skipping all neural optimization steps!");
        }

        Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> finalizationPipe = pipeline.register(new NetworkFinalizationPipe(settings, neuralizer.neuralNetBuilder));
        nextPipe.connectAfter(finalizationPipe);

        Pipe<Stream<NeuralProcessingSample>, Stream<NeuralSample>> cutoffPipe = pipeline.registerEnd(new Pipe<Stream<NeuralProcessingSample>, Stream<NeuralSample>>("ReleaseMemoryPipe") {
            @Override
            public Stream<NeuralSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
                return neuralProcessingSampleStream.map(s -> {
                    s.query.evidence = neuralizer.neuralNetBuilder.neuralBuilder.networkFactory.extractOptimizedNetwork(s.detailedNetwork);
                    Utilities.logMemory();
                    return new NeuralSample(s.target, s.query);
                });
            }
        });
        finalizationPipe.connectAfter(cutoffPipe);
        return pipeline;
    }

    /**
     * @return
     */
    public Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> buildProcessingPipeline() {
        Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pipeline = new Pipeline<>("NeuralNetsPostprocessing", settings);

        ConnectAfter<Stream<NeuralProcessingSample>> nextPipe = pipeline.registerStart(new IdentityGenPipe<>());

        if (settings.neuralNetsSupervisedPruning) {
            //todo
        }

        if (settings.copyOutInputOvermapping) {
            //todo - remove maps by recursive copying (here?)
        }

        if (settings.pruneNetworks) {
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pruningPipe = pipeline.registerEnd(new PruningPipe(settings));
            nextPipe.connectAfter(pruningPipe);
            nextPipe = pruningPipe;
        }
        if (settings.isoValueCompression) { //todo add branch at the beginning of this pipeline to extract all posible weights (over all samples) from the start template
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> isoValuePipe = pipeline.registerEnd(new CompressionPipe(settings));
            nextPipe.connectAfter(isoValuePipe);
            nextPipe = isoValuePipe;
        }
        if (settings.isoGradientCompression) {
            //todo
        }
        if (settings.cycleBreaking) {
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pipe = pipeline.registerEnd(new CycleBreakingPipe(settings));
            nextPipe.connectAfter(pipe);
            nextPipe = pipe;
        }

        if (settings.collapseWeights) {
            //maybe not
        }

        if (settings.expandEmbeddings) {
            //todo at the very end of all pruning, expand the networks to full size with vectorized nodes
        }
        //todo check wieghts dimensions

        return pipeline;
    }
}
