package networks.structure.metadata;

import ida.utils.tuples.Pair;
import networks.structure.Neuron;
import networks.structure.Weight;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class LinkedInputMapping<T extends Neuron> implements InputMapping<T> {
    private static final Logger LOG = Logger.getLogger(LinkedInputMapping.class.getName());

    List<Pair<T, Weight>> inputs;
    public LinkedInputMapping<T> previous;

    public LinkedInputMapping(List<Pair<T, Weight>> inputs) {
        this.inputs = new ArrayList<>(inputs);
    }

    public LinkedInputMapping(LinkedInputMapping<T> previous) {
        this.previous = previous;
    }

    @NotNull
    @Override
    public Iterator<Pair<T, Weight>> iterator() {
        return new InputIterator<>(this);
    }

    @Override
    public void addInput(Pair<T, Weight> input) {
        inputs.add(input);
    }

    public class InputIterator<T extends Neuron> implements Iterator<Pair<T, Weight>> {

        LinkedInputMapping<T> actual;
        int current;

        public InputIterator(LinkedInputMapping<T> inputMapping) {
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