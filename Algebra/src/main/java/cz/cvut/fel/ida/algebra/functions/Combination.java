package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.combination.*;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing general Combination functions from a List of Values to a single Value.
 *
 * Also the gradient cannot generaly be just a single Value (see {@link Aggregation for that}.
 * These Combination functions commonly have a fixed-size list of inputs (otherwise see {@link Aggregation for variable-sized input lists}.
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

    /**
     * The inputs can be permuted without affecting the result?
     * This may cause some neurons to be equivalent and thus be effectively pruned as such.
     *
     * @return
     */
    public abstract boolean isPermutationInvariant();

    /**
     * Most of the function will behave like an Identity when only a single input is presented
     * @return
     */
    default Transformation singleInputVersion(){
        return Transformation.Singletons.identity;
    }


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
        public static CosineSim cosineSim = new CosineSim();
        public static Softmax softmax = new Softmax();
    }

    public static abstract class State implements ActivationFcn.State {
        protected Combination combination;

        public State(Combination combination) {
            this.combination = combination;
        }

        @Override
        public Combination getCombination() {
            return combination;
        }

        @Override
        public Transformation getTransformation() {
            return null;
        }

        @Override
        public ActivationFcn.State changeTransformationState(Transformation transformation) {
            // no transformation = no change
            return this;
        }

    }

    abstract class InputArrayState extends State {
        protected Value processedGradient;
        protected ArrayList<Value> accumulatedInputs;
        protected int i;

        public InputArrayState(Combination combination) {
            super(combination);
        }


        @Override
        public void cumulate(Value value) {
            accumulatedInputs.add(value);
        }

        @Override
        public void invalidate() {
            accumulatedInputs.clear();
            i = 0;
        }

        @Override
        public Value initEval(List<Value> values) {
            accumulatedInputs = (ArrayList<Value>) values;
            accumulatedInputs.trimToSize();
            i = 0;
            return evaluate();
        }
    }
}