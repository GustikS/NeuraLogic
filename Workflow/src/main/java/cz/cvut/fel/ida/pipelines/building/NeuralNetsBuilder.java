package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.building.Neuralizer;
import cz.cvut.fel.ida.pipelines.ConnectAfter;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.pipelines.pipes.generic.ExportingPipe;
import cz.cvut.fel.ida.pipelines.pipes.generic.IdentityGenPipe;
import cz.cvut.fel.ida.pipelines.pipes.specific.*;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Utilities;

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

        nextPipe = pipeline.registerStart(new SupervisedNeuralizationPipe(settings, neuralizer));

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
                    return new NeuralSample(s.target, s.query, s.type);
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

        if (settings.aggregateConflictingQueries){
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pipe = pipeline.registerEnd(new SameQueryAggregationPipe(settings));
            nextPipe.connectAfter(pipe);
            nextPipe = pipe;
        }

        if (settings.neuralNetsSupervisedPruning) {
            //todo
        }

        if (settings.copyOutInputOvermapping) {
            //todo - remove maps by recursive copying (here?)
        }

        if (settings.cycleBreaking) {
            Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> pipe = pipeline.registerEnd(new CycleBreakingPipe(settings));
            nextPipe.connectAfter(pipe);
            nextPipe = pipe;
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
