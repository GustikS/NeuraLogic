package networks.structure.metadata.inputMappings;

import networks.structure.neurons.Neuron;
import networks.structure.neurons.Neurons;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class LinkedNeuronMapping<T extends Neurons> implements NeuronMapping<T> {
    private static final Logger LOG = Logger.getLogger(LinkedNeuronMapping.class.getName());

    List<T> inputs;
    public LinkedNeuronMapping<T> previous;

    public LinkedNeuronMapping(List<T> inputs) {
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
    public Iterator<T> iterator() {
        return new InputIterator<>(this);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void addLink(T input) {
        inputs.add(input);
    }

    @Override
    public NeuronMapping<Neuron> getInputs(Neuron neuron) {
        return this;
    }

    private class InputIterator<T extends Neurons> implements Iterator<T> {

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
