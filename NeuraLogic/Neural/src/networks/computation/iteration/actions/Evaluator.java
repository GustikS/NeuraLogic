package networks.computation.iteration.actions;

import ida.utils.tuples.Pair;
import networks.computation.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.values.ScalarValue;
import networks.computation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.networks.NeuralNetwork;
import networks.structure.networks.types.TopologicNetwork;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.weights.Weight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Evaluator implements NeuronVisitor<Value> {
    private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());


    public Value evaluate(NeuralModel model, NeuralSample sample) {
        NeuralNetwork<State.Structure> network = sample.query.evidence;
        //todo invalidate all neurons + set values to offset!
    }

    public Results evaluate(NeuralModel model, Stream<NeuralSample> sampleStream) {
        sampleStream.forEach(s -> new Pair<>(evaluate(s.query.neuron, s.query.evidence), s.target));
        return null;
    }

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


    Value getSummedInputs(Neuron<T, S> neuron, NeuralNetwork<State.Structure> network) {
        Value summedInputs = new ScalarValue(0);
        Iterator<T> inputs1 = network.getInputs(neuron);    //todo next use this instead
        ArrayList<T> inputs = neuron.getInputs();
        for (Neuron input : inputs) {
            Value value = input.state.getInputSum(this);
            summedInputs.add(value);
        }
        return summedInputs;
    }

    Value getSummedInputs(WeightedNeuron<T, S> neuron, NeuralNetwork<State.Structure> network) {
        Value summedInputs = new ScalarValue(0);
        ArrayList<T> inputs = neuron.getInputs();
        Iterator<Pair<T, Weight>> inputs1 = network.getInputs(neuron);  //todo next use this instead
        ArrayList<Weight> weights = neuron.getWeights();
        for (int i = 0, length = inputs.size(); i < length; i++) {
            summedInputs.add(inputs.get(i).state.getInputSum(this).multiplyBy(weights.get(i).value));
        }
        return summedInputs;
    }


    public Value activateValue(Neuron neuron){
        Value activatedSumOfInputs = neuron.activation.evaluate(neuron.state.getInputSum(this));
        neuron.state.setOutputValue(this, activatedSumOfInputs);
        return activatedSumOfInputs;
    }

    @Override
    public int getIndex() {
        return 0;
    }
}