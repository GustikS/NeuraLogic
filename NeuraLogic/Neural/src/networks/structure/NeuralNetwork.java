package networks.structure;

import learning.Example;

import java.util.Set;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralNetwork implements Example{
    String id;
    Set<Neuron> neurons;

    boolean isRecursive(){
        return false;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Integer getSize() {
        return null;
    }
}