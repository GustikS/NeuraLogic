package networks.structure.lrnnTypes;

import learning.Example;
import learning.Query;
import networks.evaluation.values.Value;
import networks.structure.Neuron;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Neuron implements Query{
    @Override
    public Example getExample() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate() {
        return null;
    }
}