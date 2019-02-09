package networks.structure.metadata.inputMappings;

import ida.utils.tuples.Pair;
import networks.structure.components.weights.Weight;
import networks.structure.components.neurons.Neurons;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class WeightedNeuronMapping<T extends Neurons> implements LinkedMapping<Pair<T, Weight>> {
    private static final Logger LOG = Logger.getLogger(WeightedNeuronMapping.class.getName());

    List<Pair<T, Weight>> inputs;   //todo next make this return pair of iterators, i.e. hold 2 lists just like in neuron classes
    public WeightedNeuronMapping<T> previous;

    public WeightedNeuronMapping(List<Pair<T, Weight>> inputs) {
        this.previous = new WeightedNeuronMapping<>();
        this.previous.inputs = new ArrayList<>(inputs);
        this.inputs = new ArrayList<>();
    }

    public WeightedNeuronMapping(WeightedNeuronMapping<T> previous) {
        this.previous = previous;
        this.inputs = new ArrayList<>();
    }

    public WeightedNeuronMapping() {
        this.inputs = new ArrayList<>();
    }

    @NotNull
    @Override
    public Iterator<Pair<T, Weight>> iterator() {
        return new InputIterator<>(this);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void addLink(Pair<T, Weight> input) {
        inputs.add(input);
    }

    private class InputIterator<T extends Neurons> implements Iterator<Pair<T, Weight>> {

        WeightedNeuronMapping<T> actual;
        int current;

        public InputIterator(WeightedNeuronMapping<T> inputMapping) {
            this.actual = inputMapping;
            this.current = actual.inputs.size() - 1;
        }

        @Override
        public boolean hasNext() {
            return !(actual.previous == null && current <= 0);
        }

        @Override
        public Pair<T, Weight> next() {
            if (current >= 0)
                return actual.inputs.get(current--);
            else if (actual.previous != null) {
                actual = actual.previous;
                current = actual.inputs.size() - 1;
                return actual.inputs.get(current--);
            } else {
                return null;
            }
        }
    }

}