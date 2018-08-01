package pipelines.bulding;

import ida.utils.tuples.Pair;
import pipelines.Pipeline;
import settings.Settings;
import training.NeuralModel;
import training.NeuralSample;
import networks.evaluation.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralTrainingBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> {
    private static final Logger LOG = Logger.getLogger(NeuralTrainingBuilder.class.getName());

    public NeuralTrainingBuilder(Settings settings) {
        super(settings);
    }


    @Override
    public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Results>> buildPipeline() {
        return null;
    }
}
