package networks.structure.metadata;

import ida.utils.tuples.Pair;
import networks.structure.Neuron;
import networks.structure.Weight;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class LinkedNeuronMapping<T extends Neuron> implements NeuronMapping<T> {
    private static final Logger LOG = Logger.getLogger(LinkedNeuronMapping.class.getName());

    List<Pair<T, Weight>> inputs;
    public LinkedNeuronMapping<T> previous;

    public LinkedNeuronMapping(List<Pair<T, Weight>> inputs) {
        this.previous = new LinkedNeuronMapping<>();
        this.previous.inputs = new ArrayList<>(inputs);
        this.inputs = new ArrayList<>();
    }

    public LinkedNeuronMapping(LinkedNeuronMapping<T> previous) {
        this.previous = previous;
        this.inputs = new ArrayList<>();
    }

    public LinkedNeuronMapping() {
        this.inputs = new ArrayList<>();
    }

    @NotNull
    @Override
    public Iterator<Pair<T, Weight>> iterator() {
        return new InputIterator<>(this);
    }

    @Override
    public void addLink(Pair<T, Weight> input) {
        inputs.add(input);
    }

    private class InputIterator<T extends Neuron> implements Iterator<Pair<T, Weight>> {

        LinkedNeuronMapping<T> actual;
        int current;

        public InputIterator(LinkedNeuronMapping<T> inputMapping) {
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