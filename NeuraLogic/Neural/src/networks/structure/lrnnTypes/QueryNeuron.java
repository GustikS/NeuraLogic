package networks.structure.lrnnTypes;

import constructs.template.Template;
import learning.Example;
import learning.Query;
import networks.evaluation.values.Value;
import networks.structure.Neuron;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Neuron implements Query{

    @Override
    public Example getEvidence() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate(Template template) {
        return null;
    }

    public Value evaluate() {
        return null;
    }
}