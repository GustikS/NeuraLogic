package learning;

import networks.evaluation.values.Value;

public interface Model {
    <T extends Query> Value evaluate(T query);
}
