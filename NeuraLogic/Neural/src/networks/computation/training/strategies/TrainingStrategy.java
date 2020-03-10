package networks.computation.training.strategies;

import evaluation.values.ScalarValue;
import exporting.Exportable;
import exporting.Exporter;
import learning.results.Progress;
import learning.results.Result;
import learning.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.strategies.debugging.NeuralDebugging;
import networks.structure.components.weights.Weight;
import settings.Settings;
import utils.Timing;
import utils.generic.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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

    transient Exporter exporter;
    protected int restart;
    transient Process progressPlotter;

    static int counter = 0;

    transient Consumer<Map<Integer, Weight>> trainingDebugCallback;

    public TrainingStrategy(Settings settings, NeuralModel model) {
        this.settings = settings;
        this.learningRate = new ScalarValue(settings.initLearningRate);
        this.currentModel = model;
        storeParametersState(model);
        this.resultsFactory = Results.Factory.getFrom(settings);
        this.timing = new Timing();
        this.trainingDebugCallback = model.templateDebugCallback;
    }

    protected void setupExporter() {
        this.exporter = Exporter.getExporter(settings.exportDir, "progress/training" + counter++ + "restart" + restart, "json");
        exporter.exportLine("[");

        if (settings.plotProgress > 0) {
            ProcessBuilder processBuilder = new ProcessBuilder(settings.pythonPath, settings.progressPlotterPath, settings.exportDir, "" + settings.plotProgress);
            try {
                progressPlotter = processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void storeParametersState(NeuralModel inputModel) {
        this.initModel = inputModel.cloneValues();
    }

    private void loadParametersState() {
        currentModel.loadWeightValues(initModel);
    }

    public abstract Pair<NeuralModel, Progress> train();

    public abstract void setupDebugger(NeuralDebugging neuralDebugger);

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
        if (progressPlotter != null)
            progressPlotter.destroy();
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