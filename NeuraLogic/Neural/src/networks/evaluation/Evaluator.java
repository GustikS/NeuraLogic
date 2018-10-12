package networks.evaluation;

import networks.evaluation.results.Results;
import networks.evaluation.values.Value;
import networks.structure.networks.NeuralNetwork;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.networks.types.TopologicNetwork;
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

    public Value evaluate(NeuralModel model, NeuralSample sample) {
        //TODO
        return null;
    }

    public Results evaluate(NeuralModel model, Stream<NeuralSample> sample) {
        //TODO
        return null;
    }

    /**
     * Evaluate neuron output by recursively evaluating its inputs
     *
     * @param neuron
     * @return
     */
    public Value evaluate(WeightedNeuron neuron) {

    }

    /**
     * Evaluate neuron output by recursively evaluating its inputs.
     * Inputs can be overmapped in a given network.
     *
     * @param neuron
     * @param network
     * @return
     */
    public Value evaluate(WeightedNeuron neuron, NeuralNetwork network) {
        return null;
    }

    /**
     * Evaluate neuron output. Iteration can go linearly over topologically sorted array.
     *
     * @param neuron
     * @param network
     * @return
     */
    public Value evaluate(WeightedNeuron neuron, TopologicNetwork network) {
        return null;
    }
}