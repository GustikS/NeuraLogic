package cz.cvut.fel.ida.neural.networks.computation.training.strategies;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.learning.results.Results;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.Timing;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.terminateSampleStream;

public abstract class TrainingStrategy implements Exportable {
    private static final Logger LOG = Logger.getLogger(TrainingStrategy.class.getName());

    transient Settings settings;

    /**
     * To be stored at the beginning and recovered after training, so that the training doesnt change
     */
    private transient NeuralModel initModel;

    transient NeuralModel currentModel;

    ScalarValue learningRate;

    transient Results.Factory trainOnlineResultsFactory;
    transient Results.Factory trainRecalculationResultsFactory;
    transient Results.Factory validationResultsFactory;

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
        this.trainOnlineResultsFactory = Results.Factory.getFrom(settings.trainOnlineResultsType, settings);
        trainRecalculationResultsFactory = Results.Factory.getFrom(settings.trainRecalculationResultsType, settings);
        this.validationResultsFactory = Results.Factory.getFrom(settings.validationResultsType, settings);
        this.timing = new Timing();
        this.trainingDebugCallback = model.templateDebugCallback;
    }

    protected void setupExporter() {
        this.exporter = Exporter.getExporter(settings.exportDir, "progress/training" + counter++ + "restart" + restart, settings.exportType.name());
        exporter.delimitStart();

        if (settings.plotProgress > 0) {
            LOG.fine("Will try to call the Python progress plotter...");
            List<String> cmd = Arrays.asList(settings.pythonPath, settings.progressPlotterPath, settings.exportDir, "" + settings.plotProgress);
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.redirectOutput(new File(settings.exportDir + "/progress/plotter_output.txt"));
            processBuilder.redirectError(new File(settings.exportDir + "/progress/plotter_output.txt"));
            try {
//                Map<String, String> environment = processBuilder.environment();
                progressPlotter = processBuilder.start();
                LOG.fine("Successfully called command: " + String.join(" ", cmd));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LOG.finer("End of exporter setup");
    }

    private void storeParametersState(NeuralModel inputModel) {
        this.initModel = inputModel.cloneWeights();
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