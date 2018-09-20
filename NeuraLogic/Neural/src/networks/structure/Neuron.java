package networks.structure;

import networks.evaluation.functions.Activation;
import networks.structure.neurons.Neurons;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Neuron<T extends Neurons> implements Neurons {
    private static final Logger LOG = Logger.getLogger(Neuron.class.getName());

    /**
     * Should be unique across ALL the networks (due to sharing)
     */
    protected String id;
    /**
     * We want fast iteration over inputs
     */
    protected ArrayList<T> inputs;
    /**
     * Activation function
     */
    protected Activation activation;

    public Neuron(String id){
        this.id = id;
        inputs = new ArrayList<>();
    }

    public void addInput(T input){
        inputs.add(input);
    }

    @Override
    public ArrayList<T> getInputs() {
        return inputs;
    }

    public boolean hasInputs() {
        return !inputs.isEmpty();
    }

    public int inputCount() {
        return inputs.size();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        WeightedNeuron obj1 = (WeightedNeuron) obj;
        return this.id.equals(obj1.id);
    }

    @Override
    public Activation getActivation() {
        return activation;
    }

    @Override
    public String getId() {
        return id;
    }
}