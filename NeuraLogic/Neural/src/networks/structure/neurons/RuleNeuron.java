package networks.structure.neurons;

import constructs.template.WeightedRule;
import networks.structure.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class RuleNeuron extends Neuron<AtomFact> implements RuleNeurons{

    public RuleNeuron(WeightedRule grounding) {
        super(grounding.toString());
        this.activation = grounding.activationFcn;
    }
}