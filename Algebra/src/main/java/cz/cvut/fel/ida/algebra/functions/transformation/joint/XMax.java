package cz.cvut.fel.ida.algebra.functions.transformation.joint;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;

/**
 * SparseMax + SoftMax
 */
public interface XMax {

    double[] getProbabilities(double[] input);

    double[] getProbabilities(List<Value> inputs);

    double[] getGradient(double[] exps);

}