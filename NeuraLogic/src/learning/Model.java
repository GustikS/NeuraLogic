package learning;

import networks.evaluation.values.Value;

public interface Model {
    String getId();

    <T extends Query> Value evaluate(T query);
}
