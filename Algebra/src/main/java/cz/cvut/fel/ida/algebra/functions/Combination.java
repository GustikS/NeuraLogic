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
            //this used to return `this`, reading "there is no transformation here" as "a transformation
            //cannot be put here". The two are not the same, and only the output-function inference calls
            //this: a queried atom whose template says IDENTITY gets a plain Combination.State, so asking it
            //for the sigmoid that CrossEntropy(with_logits=False) wants did nothing at all. Whether the
            //queried value came back squashed then depended on how many rules defined the predicate - with
            //one, StateInitializer had already turned the state into a Transformation.State, which does take
            //one; with two or more it stayed a Combination.State and the inference was silently dropped.
            if (transformation == null || transformation instanceof cz.cvut.fel.ida.algebra.functions.transformation.joint.Identity) {
                return this;    //identity on top of a combination is that combination - the same rule
                                //ActivationFcn.State.getState follows when it builds these in the first place
            }
            return new CompoundState(this, (Transformation.State) transformation.getState(true));
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