package learning;

import networks.evaluation.values.Value;
import networks.structure.Weight;

import java.util.List;

public interface Model {
    String getId();

    <T extends Query> Value evaluate(T query);
    List<Weight> getAllWeights();
}
