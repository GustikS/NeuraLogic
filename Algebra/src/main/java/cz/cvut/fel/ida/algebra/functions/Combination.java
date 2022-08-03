package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.combination.*;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.SharpMax;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.SharpMin;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing combination functions
 */
public interface Combination extends ActivationFcn, Exportable {

    static final Logger LOG = Logger.getLogger(Combination.class.getName());

    /**
     * Return the result of corresponding Aggregation function applied to the list of inputs.
     *
     * @param inputs
     * @return
     */
    public abstract Value evaluate(List<Value> inputs);

    public abstract Value differentiate(List<Value> inputs);

    /**
     * The inputs can be permuted without affecting the result?
     * This may cause some neurons to be equivalent and thus be effectively pruned as such.
     *
     * @return
     */
    public abstract boolean isInputSymmetric();

    public static Combination getFunction(Settings.CombinationFcn combinationFcn) {
        Aggregation function = Aggregation.getFunction(combinationFcn);
        if (function != null) {
            return function;
        }
        switch (combinationFcn) {
            case PRODUCT:
                return Singletons.product;
            case ELPRODUCT:
                return Singletons.elementProduct;
            case SOFTMAX:
                return Transformation.Singletons.softmax;
            case SPARSEMAX:
                return Transformation.Singletons.sparsemax;
            case CROSSSUM:
                return Singletons.crossSum;
            case CONCAT:
                return Singletons.concatenation;
            case COSSIM:
                return Singletons.cosineSim;
            default:
                LOG.severe("Unimplemented combination function");
                return null;
        }
    }

    public static class Singletons {
        public static Product product = new Product();
        public static ElementProduct elementProduct = new ElementProduct();
        public static CrossSum crossSum = new CrossSum();
        public static Concatenation concatenation = new Concatenation();
        public static SharpMax max = new SharpMax();
        public static SharpMin min = new SharpMin();
        public static CosineSim cosineSim = new CosineSim();
    }

    public abstract class State implements ActivationFcn.State {
        protected Combination combination;

        protected Value combinedInputs;
        protected Value processedGradient;

        public State(Combination combination){
            this.combination = combination;
        }

        @Override
        public void invalidate() {
            combinedInputs = null;
            processedGradient = null;
        }

        @Override
        public void setupDimensions(Value value) {
            combinedInputs = value.getForm();
        }

        @Override
        public Combination getCombination() {
            return combination;
        }

        @Override
        public void setCombination(Combination combination) {
            this.combination = combination;
        }

        @Override
        public Transformation getTransformation() {
            return null;
        }

        @Override
        public void setTransformation(Transformation transformation) {
            LOG.severe("Trying to set Transformation in Combination.State");
        }
    }
}