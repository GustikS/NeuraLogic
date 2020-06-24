package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;

/**
 * SparseMax + SoftMax
 */
public interface XMax {

    double[] getProbabilities(List<Value> inputs);

    double[] getGradient(double[] exps);

}
