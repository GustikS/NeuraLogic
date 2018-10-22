package networks.computation.training.evaluation;

import networks.computation.iteration.actions.Evaluator;
import networks.computation.results.Result;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.types.AtomNeuron;
import settings.Settings;

import java.util.logging.Logger;

public class Evaluation {
    private static final Logger LOG = Logger.getLogger(Evaluation.class.getName());
    Settings settings;

    Evaluator evaluator;

    public Evaluation(Settings settings) {
        this.settings = settings;
        evaluator = getEvaluator(settings);
    }

    private Evaluator getEvaluator(Settings settings) {
        return null;
    }

    public Result evaluate(NeuralModel model, NeuralSample sample) {
        NeuralNetwork<State.Structure> network = sample.query.evidence;
        AtomNeuron outputNeuron = sample.query.neuron;
        Value output = evaluator.evaluate(network, outputNeuron);
        //todo invalidate all neurons + set values to offset!
        Result result = new Result(sample.target, output);
        return result;
    }
}
