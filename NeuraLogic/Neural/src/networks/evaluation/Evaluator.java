package networks.evaluation;

import networks.evaluation.results.Results;
import networks.evaluation.values.Value;
import settings.Settings;
import training.NeuralModel;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class Evaluator {
    private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());
    Settings settings;

    public Evaluator(Settings settings) {
        this.settings = settings;
    }

    public Value evaluate(NeuralModel model, NeuralSample sample){
        //TODO
        return null;
    }

    public Results evaluate(NeuralModel model, Stream<NeuralSample> sample){
        //TODO
        return null;
    }
}
