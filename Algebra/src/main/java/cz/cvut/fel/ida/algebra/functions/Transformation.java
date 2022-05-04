package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.transformation.joint.*;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing transformation of a single Value to a single Value
 */
public abstract class Transformation extends Combination implements Exportable {

    private static final Logger LOG = Logger.getLogger(Transformation.class.getName());

    public abstract Value evaluate(Value combinedInputs);

    public abstract Value differentiate(Value combinedInputs);

    /**
     * Default aggregation is SUM
     * @param inputs
     * @return
     */
    @Override
    public Value evaluate(List<Value> inputs) {
        Value sum = inputs.get(0).clone();
        for (int i = 1, len = inputs.size(); i < len; i++) {
            sum.incrementBy(inputs.get(i));
        }
        return sum;
    }

    public static Aggregation getActivationFunction(Settings.ActivationFcn combinationFcn) {
        switch (combinationFcn) {
            case TRANSP:
                return Singletons.transposition;
            case SIZE:
                return Singletons.size;
            case SOFTMAX:
                return Singletons.softmax;
            case SPARSEMAX:
                return Singletons.sparsemax;
            default:
                LOG.severe("Unimplemented Transformation function");
                return null;
        }
    }

    public static Aggregation parseActivation(String comb) {
        switch (comb) {
            case "softmax":
                return Singletons.softmax;
            case "sparsemax":
                return Singletons.sparsemax;
            case "transpose":
                return Singletons.transposition;
            case "size":
                return Singletons.size;
            default:
                throw new RuntimeException("Unable to parse Transformation function: " + comb);
        }
    }

    public static class Singletons {
        public static Softmax softmax = new Softmax();
        public static Sparsemax sparsemax = new Sparsemax();

        public static SharpMax sharpmax = new SharpMax();
        public static SharpMin sharpmin = new SharpMin();

        public static Size size = new Size();
        public static Transposition transposition = new Transposition();
    }

    @Override
    public boolean isInputSymmetric() {
        return false;
    }
}