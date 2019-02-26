package networks.structure.metadata.inputMappings;

import com.sun.istack.internal.NotNull;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neuron;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class NeuronMapping<T extends Neuron> implements LinkedMapping<T> {
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

    private class InputIterator<T extends Neuron> implements Iterator<T> {

        NeuronMapping<T> actual;
        int current;

        public InputIterator(NeuronMapping<T> inputMapping) {
            this.actual = inputMapping;
            this.current = actual.inputs.size() - 1;
        }

        @Override
        public boolean hasNext() {
            return !(actual.previous == null && current <= 0);
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
