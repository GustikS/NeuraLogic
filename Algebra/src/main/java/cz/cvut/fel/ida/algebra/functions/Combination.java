package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.aggregation.Sum;
import cz.cvut.fel.ida.algebra.functions.combination.*;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.SharpMax;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.SharpMin;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing combination functions
 */
public abstract class Combination extends Aggregation {

    private static final Logger LOG = Logger.getLogger(Combination.class.getName());

    /**
     * Default gradient update is nothing happens = inert Value.ONE w.r.t. multiplication (chain rule)
     * @param inputs
     * @return
     */
    @Override
    public Value differentiate(List<Value> inputs) {
        return Value.ONE;
    }

    public static Aggregation getCombinationFunction(Settings.CombinationFcn combinationFcn) {
        switch (combinationFcn) {
            case SUM:
                return Singletons.sum;
            case PRODUCT:
                return Singletons.product;
            case ELPRODUCT:
                return Singletons.elementProduct;
            case CROSSSUM:
                return Singletons.crossSum;
            case CONCAT:
                return Singletons.concatenation;
            case MAX:
                return Singletons.max;
            case MIN:
                return Singletons.min;
            case COSSIM:
                return Singletons.cosineSim;
            default:
                LOG.severe("Unimplemented combination function");
                return null;
        }
    }

    public static Aggregation parseActivation(String comb) {
        switch (comb) {
            case "sum":
                return Combination.Singletons.sum;
            case "prod":
                return Singletons.product;
            case "elprod":
                return Singletons.elementProduct;
            case "cross":
                return Singletons.crossSum;
            case "concat":
                return Singletons.concatenation;
            case "max":
                return Singletons.max;
            case "min":
                return Singletons.min;
            case "cossim":
                return Singletons.cosineSim;
            default:
                throw new RuntimeException("Unable to parse combination function: " + comb);
        }
    }

    public static class Singletons {
        public static Sum sum = new Sum();
        public static Product product = new Product();
        public static ElementProduct elementProduct = new ElementProduct();
        public static CrossSum crossSum = new CrossSum();
        public static Concatenation concatenation = new Concatenation();
        public static SharpMax max = new SharpMax();
        public static SharpMin min = new SharpMin();
        public static CosineSim cosineSim = new CosineSim();
    }
}