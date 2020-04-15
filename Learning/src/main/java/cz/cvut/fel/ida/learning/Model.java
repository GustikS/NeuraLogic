package cz.cvut.fel.ida.learning;


import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;

import java.util.List;

public interface Model<T extends Query>  {
    String getName();

    Value evaluate(T query);
    List<Weight> getAllWeights();
}