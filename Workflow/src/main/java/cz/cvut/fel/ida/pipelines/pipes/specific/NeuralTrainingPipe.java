package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.TrainingStrategy;
import cz.cvut.fel.ida.pipelines.debugging.NeuralDebugger;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

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
        trainingStrategy.setupDebugger(new NeuralDebugger(settings));
        Pair<NeuralModel, Progress> training = trainingStrategy.train();
        return training;
    }

    @Override
    protected <T> void export(T outputReady) {
        if (exporter == null && parent != null) {
            exporter = createExporter(this.ID, parent.settings);
        }
        if (exporter != null) {
            trainingStrategy.export(exporter);
        }
    }
}


