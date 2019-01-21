package networks.computation.iteration;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;

public interface WeightVisitor {

    public abstract void visit(Weight weight, Value value);

}
