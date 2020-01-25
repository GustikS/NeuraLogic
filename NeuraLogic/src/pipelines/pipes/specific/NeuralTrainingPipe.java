package pipelines.pipes.specific;

import networks.computation.evaluation.results.Progress;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.strategies.TrainingStrategy;
import pipelines.Pipe;
import settings.Settings;
import utils.exporting.Exporter;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralTrainingPipe extends Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> {
    private static final Logger LOG = Logger.getLogger(NeuralTrainingPipe.class.getName());
    Settings settings;

    TrainingStrategy trainingStrategy;

    public NeuralTrainingPipe(Settings settings) {
        super("NeuralTrainingPipe");
        this.settings = settings;
    }

    protected NeuralTrainingPipe(String id) {
        super(id);
    }

    /**
     * Terminating operation (obviously)!
     *
     * @param neuralModelStreamPair
     * @return
     */
    @Override
    public Pair<NeuralModel, Progress> apply(Pair<NeuralModel, Stream<NeuralSample>> neuralModelStreamPair) {
        NeuralModel model = neuralModelStreamPair.r;
        Stream<NeuralSample> sampleStream = neuralModelStreamPair.s;

        trainingStrategy = TrainingStrategy.getFrom(settings, model, sampleStream);
        Pair<NeuralModel, Progress> training = trainingStrategy.train();
        return training;
    }

    @Override
    protected <T> void export(T outputReady) {
        if (exporter == null && parent != null) {
            exporter = Exporter.getFrom(this.ID, parent.settings);
        }
        if (exporter != null) {
            trainingStrategy.export(exporter);
        }
    }
}
