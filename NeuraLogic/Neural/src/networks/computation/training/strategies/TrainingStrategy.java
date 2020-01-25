package networks.computation.training.strategies;

import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import settings.Settings;
import utils.Timing;
import utils.exporting.Exportable;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static utils.Utilities.terminateSampleStream;

public abstract class TrainingStrategy implements Exportable {
    private static final Logger LOG = Logger.getLogger(TrainingStrategy.class.getName());

    transient Settings settings;

    /**
     * To be stored at the beginning and recovered after training, so that the training doesnt change
     */
    private transient NeuralModel initModel;

    transient NeuralModel currentModel;

    ScalarValue learningRate;

    transient Results.Factory resultsFactory;

    Timing timing;

    public TrainingStrategy(Settings settings, NeuralModel model) {
        this.settings = settings;
        this.learningRate = new ScalarValue(settings.initLearningRate);
        this.currentModel = model;
        storeParametersState(model);
        this.resultsFactory = Results.Factory.getFrom(settings);

        this.timing = new Timing();
    }

    private void storeParametersState(NeuralModel inputModel) {
        this.initModel = inputModel.cloneValues();
    }

    private void loadParametersState() {
        currentModel.loadWeightValues(initModel);
    }

    public abstract Pair<NeuralModel, Progress> train();

    public static TrainingStrategy getFrom(Settings settings, NeuralModel model, Stream<NeuralSample> sampleStream) {
        if (settings.neuralStreaming) {
            return new StreamTrainingStrategy(settings, model, sampleStream);
        } else {
            List<NeuralSample> collect = terminateSampleStream(sampleStream);
            return new IterativeTrainingStrategy(settings, model, collect);
        }
    }

    protected void endTrainingStrategy() {
        if (settings.undoWeightTrainingChanges) {
            loadParametersState();
        }
    }


    protected class TrainVal {
        List<Result> training;
        List<Result> validation;

        public TrainVal(List<Result> train, List<Result> val) {
            this.training = train;
            this.validation = val;
        }
    }
}