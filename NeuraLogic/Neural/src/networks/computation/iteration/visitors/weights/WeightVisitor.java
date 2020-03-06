package networks.computation.iteration.visitors.weights;

import evaluation.values.Value;
import networks.structure.components.weights.Weight;

public interface WeightVisitor {

    public abstract void visit(Weight weight, Value value);

}
