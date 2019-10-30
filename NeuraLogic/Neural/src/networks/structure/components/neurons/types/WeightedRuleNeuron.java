package networks.structure.components.neurons.types;

import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public class WeightedRuleNeuron<S extends State.Neural> extends WeightedNeuron<AtomFact, S> implements RuleNeurons<AtomFact, S> {
    private static final Logger LOG = Logger.getLogger(WeightedRuleNeuron.class.getName());

    public WeightedRuleNeuron(String grounding, Weight offset, int index, S state) {
        super(grounding, index, state, offset);
    }

    @Override
    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}
