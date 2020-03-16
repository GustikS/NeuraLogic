package cz.cvut.fel.ida.neural.networks.structure.components.neurons;

import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

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
public abstract class WeightedNeuron<T extends Neurons, S extends State.Neural> extends BaseNeuron<T, S> {
    private static final Logger LOG = Logger.getLogger(WeightedNeuron.class.getName());

    public Weight offset;

    /**
     * This class is responsible to protect correct ordering of weights w.r.t. inputs.
     */
    protected ArrayList<Weight> weights;

    public WeightedNeuron(String id, int index, S state, Weight offset) {
        super(index, id, state);
        if (offset == null) {
            this.offset = Weight.zeroWeight;
        } else {
            this.offset = offset;
        }
        weights = new ArrayList<>();
    }

    public final void addInput(T input) {
        LOG.warning("Adding unnecessarily weighted input. Use plain Neuron Object for unweighted inputs instead!");
        inputs.add(input);
        weights.add(Weight.unitWeight);
    }

    public final void addInput(T input, Weight weight) {
        inputs.add(input);
        weights.add(weight);
    }

    /**
     * Fastest access to weighted inputs is the separate access
     *
     * @return
     */
    public final ArrayList<Weight> getWeights() {
        return weights;
    }

    public final Weight getOffset() {
        return offset;
    }

    /**
     * Efficient call for weighted inputs, returns them as-is (just a single Pair object is created).
     *
     * @return
     */
    public final Pair<List<T>, List<Weight>> getInputsWeights() {
        return new Pair<>(inputs, weights);
    }

    /**
     * Semi-expensive - needs to create iterator and Pairs.
     *
     * @return
     */
    public final Iterator<Pair<T, Weight>> getWeightedInputs() {
        return new WeightedInputsIterator(inputs, weights);
    }

    private final class WeightedInputsIterator implements Iterator<Pair<T, Weight>> {

        int index = 0;
        int size;

        ArrayList<T> inputs;
        ArrayList<Weight> weights;

        public WeightedInputsIterator(ArrayList<T> inputs, ArrayList<Weight> weights) {
            this.inputs = inputs;
            this.weights = weights;
            this.size = inputs.size();
        }

        @Override
        public boolean hasNext() {
            return index < size - 1;
        }

        @Override
        public Pair<T, Weight> next() {
            return new Pair<>(inputs.get(index), weights.get(index));
        }
    }

    /**
     * Expensive call - needs to create new List and Pairs!
     *
     * @return
     */
    public final List<Pair<T, Weight>> createWeightedInputs() {
        List<Pair<T, Weight>> pairedInputs = new ArrayList<>(weights.size());
        for (int i = 0; i < weights.size(); i++) {
            pairedInputs.add(new Pair<>(inputs.get(i), weights.get(i)));
        }
        return pairedInputs;
    }

    public void visit(NeuronVisitor.Weighted visitor) {
        visitor.visit(this);
    }

    public void visit(NeuronVisiting.Weighted visitor) {
        visitor.visit(this);
    }
}