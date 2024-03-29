package cz.cvut.fel.ida.algebra.functions.transformation.elementwise;

import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A type of Sigmoidal activation function approximating Lukasiewicz more tightly (steeper gradient and offset -0.5)
 */
public class LukasiewiczSigmoid extends ElementWise {
    private static final Logger LOG = Logger.getLogger(LukasiewiczSigmoid.class.getName());


    static DoubleUnaryOperator logist = in -> in > 100 ? 1 : (in < -100 ? 0 : 1 / (1 + Math.exp(-6 * in - 0.5)));

    public LukasiewiczSigmoid() {
        super(logist,
                in -> in > 100 || in < -100 ? 0 : logist.applyAsDouble(in) * (1 - logist.applyAsDouble(in))
        );
    }

    public LukasiewiczSigmoid replaceWithSingleton() {
        return Singletons.lukasiewiczSigmoid;
    }

    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(0.01, 0.99);
    }
}
