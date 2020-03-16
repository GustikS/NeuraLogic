package cz.cvut.fel.ida.algebra.functions.specific;

import cz.cvut.fel.ida.algebra.functions.Activation;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A type of Sigmoidal activation function approximating Lukasiewicz more tightly (steeper gradient and offset -0.5)
 */
public class LukasiewiczSigmoid extends Activation {
    private static final Logger LOG = Logger.getLogger(LukasiewiczSigmoid.class.getName());

    @Override
    public String getName() {
        return "Lukasiewicz";
    }

    static Function<Double, Double> logist = in -> in > 100 ? 1 : (in < -100 ? 0 : 1 / (1 + Math.exp(-6 * in - 0.5)));

    public LukasiewiczSigmoid() {
        super(logist,
                in -> in > 100 || in < -100 ? 0 : logist.apply(in) * (1 - logist.apply(in))
        );
    }

    public LukasiewiczSigmoid replaceWithSingleton() {
        return Singletons.lukasiewiczSigmoid;
    }

    @Override
    public Pair<Double, Double> getSaturationRange() {
        return new Pair<>(0.01, 0.99);
    }

    @Override
    public boolean isInputSymmetric() {
        return true;
    }
}
