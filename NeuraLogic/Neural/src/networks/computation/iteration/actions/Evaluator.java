package networks.computation.iteration.actions;

import networks.computation.iteration.BottomUp;
import networks.computation.training.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.WeightedNeuron;

import java.util.logging.Logger;

/**
 * Serves 3 purposes: (split into separate classes?)
 * 1) a high level invocation of evaluation of a neural network with query neuron.
 * 2) a neuron (state) visitor - propagating output value into parents - is inherited
 * 3) a bottom-up iterator of neural networks - is outsourced via composition
 */
public class Evaluator extends NeuronVisitor<Value> {
    private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());

    BottomUp<State.Structure,Value> iterator;


    /**
     * Evaluate neuron output by recursively evaluating its inputs.
     * Inputs can be overmapped in a given network.
     *
     * @param neuron
     * @param network
     * @return
     */
    public Value evaluate(WeightedNeuron<T, S> neuron, NeuralNetwork<State.Structure> network) {

        Value value = neuron.state.getInputSum(this);

        if (value.valid) { //if already evaluated
            return value;
        }

        if (!neuron.hasNoInputs()) { //if no inputs to be evaluated
            return value;
        }
        Value summedInputs = getSummedInputs(neuron, network);
        return neuron.getActivation().evaluate(summedInputs);
    }

}