package cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class NeuronMapping<T extends Neurons> implements LinkedMapping<T> {
    private static final Logger LOG = Logger.getLogger(NeuronMapping.class.getName());

    List<T> inputs;
    public NeuronMapping<T> previous;

    public NeuronMapping(List<T> inputs) {
        this.previous = new NeuronMapping<>();
        this.previous.inputs = new ArrayList<>(inputs);
        this.inputs = new ArrayList<>();
    }

    public NeuronMapping(NeuronMapping<T> previous) {
        this.previous = previous;
        this.inputs = new ArrayList<>();
    }

    public NeuronMapping() {
        this.inputs = new ArrayList<>();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new InputIterator<>(this);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public List<T> getLastList() {
        return inputs;
    }

    @Override
    public void addLink(T input) {
        inputs.add(input);
    }

    public LinkedMapping<T> getInputs(BaseNeuron neuron) {
        return this;
    }

    public void replace(T toReplace, T replaceWith) {
        InputIterator<T> inputIterator = new InputIterator<>(this);
        while (inputIterator.hasNext()) {
            NeuronMapping<T> lastMapping = inputIterator.actual;
            T next = inputIterator.next();
            if (next.equals(toReplace)) {
                if (inputIterator.current == inputIterator.actual.inputs.size() - 1) {   //if it was the last element in the previous list (iterator jumped already onto a new list)
                    lastMapping.replaceLocal(0, replaceWith);   //recall the last list and replace the last element (going decreasingly)
                } else {
                    inputIterator.actual.replaceLocal(inputIterator.current + 1, replaceWith);  //otherwise simply replace the previous element from the actual list
                }
            }
        }
    }

    private void replaceLocal(int i, T replaceWith) {
        if (replaceWith == null) {
            inputs.remove(i);
        } else {
            inputs.set(i, replaceWith);
        }
    }

    private class InputIterator<T extends Neurons> implements Iterator<T> {

        NeuronMapping<T> actual;
        int current;

        public InputIterator(NeuronMapping<T> inputMapping) {
            this.actual = inputMapping;
            this.current = actual.inputs.size() - 1;
        }

        @Override
        public boolean hasNext() {
            return !(actual.previous == null && current < 0);
        }

        @Override
        public T next() {
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
