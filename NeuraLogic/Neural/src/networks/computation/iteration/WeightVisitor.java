package networks.computation.iteration;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.actions.Evaluator;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

public abstract class WeightVisitor {

    protected Evaluator evaluator;

    public WeightVisitor(Evaluator evaluator){
        this.evaluator = evaluator;
    }

    public abstract void visit(Weight weight, Value value);

    public abstract void visit(Weight weight, Value gradient, State.Neural.Computation input);
}
