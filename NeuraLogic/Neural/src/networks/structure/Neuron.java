package networks.structure;

import ida.utils.tuples.Pair;
import networks.evaluation.functions.Activation;
import networks.evaluation.values.Value;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Neuron<T> {
    /**
     * Unique id within a network - todo consider int
     */
    protected String id;


    protected Weight offset;

    /**
     * We want fast iteration over inputs
     */
    protected ArrayList<Pair<T, Weight>> inputs;
    protected Activation activation;

    public Neuron(String id){
        this.id = id;
        inputs = new ArrayList<>();
    }

    /**
     * Forward pass
     * @return
     */
    public abstract Value evaluate();

    /**
     * Backward pass
     * @return
     */
    public abstract Value gradient();

    @Override
    public int hashCode() {
        return super.hashCode(); //TODO use id
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);   //TODO use id
    }

    public void addInput(T input, Weight weight){
        inputs.add(new Pair<>(input,weight));
    }

    public boolean hasInputs() {
        return !inputs.isEmpty();
    }

    public int inputCount() {
        return inputs.size();
    }
}