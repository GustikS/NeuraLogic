package networks.structure.metadata.states;

import networks.evaluation.values.Value;

public abstract class StateVisitor {
    //todo for all the different actions over all different states
    abstract void visit(State state);

    abstract void visit(State.Computation.ValuePair computationPair);

    abstract void visit(Value value);
}
