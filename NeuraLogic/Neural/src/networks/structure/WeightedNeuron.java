package networks.structure;

import ida.utils.tuples.Pair;
import networks.evaluation.values.Value;
import networks.structure.neurons.Neurons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is responsible to protect correct ordering of weights and inputs.
 * Created by gusta on 8.3.17.
 * <p>
 * Solved question - is it better to store inputs (better for iso-value) or outputs (better for iso-gradient) of a neuron?
 * - inputs representation is better since it follows the process of calculation more naturally
 * - staying with this representation for everything then
 */
public class WeightedNeuron<T extends Neurons> extends Neuron<T> {
    private static final Logger LOG = Logger.getLogger(WeightedNeuron.class.getName());

    protected Weight offset;

    /**
     * This class is responsible to protect correct ordering of weights and inputs.
     */
    protected ArrayList<Weight> weights;

    public WeightedNeuron(String id) {
        super(id);
        weights = new ArrayList<>();
    }

    public void addInput(T input) {
        LOG.warning("Adding unnecessarily weighted input. Use plain Neuron Object for unweighted inputs instead!");
        inputs.add(input);
        weights.add(new Weight(Value.ONE));
    }

    public void addInput(T input, Weight weight) {
        inputs.add(input);
        weights.add(weight);
    }

    public ArrayList<Weight> getWeights() {
        return weights;
    }

    /**
     * Expensive call - needs to create new objects!
     *
     * @return
     */
    public List<Pair<T, Weight>> createWeightedInputs() {
        List<Pair<T, Weight>> pairedInputs = new ArrayList<>(weights.size());
        for (int i = 0; i < weights.size(); i++) {
            pairedInputs.add(new Pair<>(inputs.get(i), weights.get(i)));
        }
        return pairedInputs;
    }

    public Iterator<Pair<T, Weight>> getWeightedInputs() {
        return new WeightedInputsIterator(inputs, weights);
    }

    private class WeightedInputsIterator implements Iterator<Pair<T, Weight>> {

        int index = 0;
        ArrayList<T> inputs;
        ArrayList<Weight> weights;

        public WeightedInputsIterator(ArrayList<T> inputs, ArrayList<Weight> weights) {
            this.inputs = inputs;
            this.weights = weights;
        }

        @Override
        public boolean hasNext() {
            return index < inputs.size() - 1;
        }

        @Override
        public Pair<T, Weight> next() {
            return new Pair<>(inputs.get(index), weights.get(index));
        }
    }
}