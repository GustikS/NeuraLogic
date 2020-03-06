package building;

import constructs.building.factories.WeightFactory;
import grounding.GroundTemplate;
import grounding.GroundingSample;
import networks.computation.training.NeuralSample;
import networks.structure.building.NeuralProcessingSample;
import networks.structure.building.Neuralizer;
import networks.structure.building.debugging.NeuralDebugger;
import networks.structure.components.types.DetailedNetwork;
import pipelines.ConnectAfter;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.bulding.AbstractPipelineBuilder;
import pipelines.pipes.generic.ExportingPipe;
import pipelines.pipes.generic.IdentityGenPipe;
import pipes.specific.*;
import settings.Settings;
import utils.Utilities;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralNetsBuilder extends AbstractPipelineBuilder<Stream<GroundingSample>, Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(NeuralNetsBuilder.class.getName());

    private WeightFactory weightFactory;

    public NeuralNetsBuilder(Settings settings) {
        super(settings);
    }

    public NeuralNetsBuilder(Settings settings, WeightFactory weightFactory) {
        super(settings);
        this.weightFactory = weightFactory;
    }


    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildPipeline() {
        Neuralizer neuralizer = new Neuralizer(settings, weightFactory);

        Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> pipeline = new Pipeline<>("NeuralizationPipeline", this);

        ConnectAfter<Stream<NeuralProcessingSample>> nextPipe = null;

        nextPipe = pipeline.registerStart(new Pipe<Stream<GroundingSample>, Stream<NeuralProcessingSample>>("SupervisedNeuralizationPipe", settings) {
            @Override
            public Stream<NeuralProcessingSample> apply(Stream<GroundingSample> groundingSampleStream) {
                if (settings.groundingMode == Settings.GroundingMode.GLOBAL) {
                    List<GroundingSample> groundingSamples = Utilities.terminateSampleStream(groundingSampleStream);
                    GroundTemplate groundTemplate = groundingSamples.get(0).groundingWrap.getGroundTemplate();
                    LOG.info("Neuralizing GLOBAL sample " + groundTemplate.toString());
                    List<NeuralProcessingSample> neuralizedSamples = neuralizer.neuralize(groundTemplate, groundingSamples);
                    DetailedNetwork detailedNetwork = neuralizedSamples.get(0).detailedNetwork;
                    LOG.info("GLOBAL NeuralNet created: " + detailedNetwork.toString());
                    return neuralizedSamples.stream();
                } else {
                    return groundingSampleStream
                            .peek(s -> LOG.info("Neuralizing sample " + s.toString()))
                            .map(sample -> neuralizer.neuralize(sample).stream())
                            .flatMap(f -> f)
                            .peek(s -> LOG.info("NeuralNet created: " + s.toString()));
                }
            }
        });

        if (pipeline.exporter != null) {
            ExportingPipe<NeuralProcessingSample> exportingPipe = pipeline.register(new ExportingPipe<>(neuralizer, pipeline.exporter, neuralizer.timing, settings));
            nextPipe.connectAfter(exportingPipe);
            nextPipe = exportingPipe;
        }

        if (settings.neuralNetsPostProcessing) {
            Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> postprocessingPipeline = pipeline.register(buildProcessingPipeline());
            nextPipe.connectAfter(postprocessingPipeline);
            nextPipe = postprocessingPipeline;
        } else {
            LOG.warning("Skipping all neural optimization steps!");
        }

        Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> finalizationPipe = pipeline.register(new NetworkFinalizationPipe(settings, neuralizer.neuralNetBuilder));
        nextPipe.connectAfter(finalizationPipe);

        Pipe<Stream<NeuralProcessingSample>, Stream<NeuralSample>> cutoffPipe = pipeline.registerEnd(new Pipe<Stream<NeuralProcessingSample>, Stream<NeuralSample>>("ReleaseMemoryPipe", settings) {
            @Override
            public Stream<NeuralSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
                return neuralProcessingSampleStream.map(s -> {
                    s.query.evidence = neuralizer.neuralNetBuilder.neuralBuilder.networkFactory.extractOptimizedNetwork(s.detailedNetwork);
                    if (settings.groundingMode != Settings.GroundingMode.GLOBAL)
                        Utilities.logMemory();
                    return new NeuralSample(s.target, s.query);
                });
            }
        });
        finalizationPipe.connectAfter(cutoffPipe);

        if (settings.debugNeuralization) {
            new NeuralDebugger(settings).addDebugStream(pipeline);
        }
        return pipeline;
    }

    /**
     * @return
     */
    public Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> buildProcessingPipeline() {
        Pipeline<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pipeline = new Pipeline<>("NeuralNetsPostprocessingPipeline", settings);

        ConnectAfter<Stream<NeuralProcessingSample>> nextPipe = pipeline.registerStart(new IdentityGenPipe<>("NNBuildingInit"));

        if (settings.neuralNetsSupervisedPruning) {
            //todo
        }

        if (settings.copyOutInputOvermapping) {
            //todo - remove maps by recursive copying (here?)
        }

        if (settings.chainPruning) {
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

        if (settings.mergeIdenticalWeightedInputs || settings.removeIdenticalUnweightedInputs) {
            EdgeMergerPipe edgeMergerPipe = pipeline.registerEnd(new EdgeMergerPipe(settings));
            nextPipe.connectAfter(edgeMergerPipe);
            nextPipe = edgeMergerPipe;
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
