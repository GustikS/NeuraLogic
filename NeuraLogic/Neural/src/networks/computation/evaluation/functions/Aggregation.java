package networks.computation.evaluation.functions;

import networks.computation.evaluation.values.Value;
import networks.structure.components.weights.Weight;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public interface Aggregation {

    Value evaluate(ArrayList<Value> inputs);

    @Deprecated
    Value evaluate(ArrayList<Value> inputs, ArrayList<Weight> weights);

    Value differentiateAt(Value x);

    /**
     * This should be optional.
     * @return
     */
    @Nullable
    Activation differentiateGlobally();

}