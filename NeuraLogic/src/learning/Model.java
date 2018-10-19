package learning;

import networks.computation.values.Value;
import networks.structure.weights.Weight;

import java.util.List;

public interface Model<T extends Query>  {
    String getId();

    Value evaluate(T query);
    List<Weight> getAllWeights();
}