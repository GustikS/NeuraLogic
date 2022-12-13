package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.combination.Softmax;
import cz.cvut.fel.ida.algebra.functions.combination.Sparsemax;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.ConstantOne;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.Identity;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.Normalization;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.Transposition;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class representing (arbitrary) transformation of a single Value to a single Value.
 * It can be evaluated and differentiated on the input Value, returning also a single Value.
 *
 * If it can be also used as a Combination fcn, it is placed in that package preferably (but implements both interfaces, e.g. Softmax)
 */
public interface Transformation extends ActivationFcn, Exportable {

    static final Logger LOG = Logger.getLogger(Transformation.class.getName());

    /**
     * The evaluation operates on a single input Value
     *
     * @param combinedInputs
     * @return
     */
    public abstract Value evaluate(Value combinedInputs);

    /**
     * The differentiation returns a single output Value
     *
     * @param combinedInputs
     * @return
     */
    public abstract Value differentiate(Value combinedInputs);

    /**
     * Checks whether the shape of the transformation function output value differs from the shape of its input value.
     *
     * @return
     */
    default boolean changesShape() {
        return false;
    }

    public static Transformation getFunction(Settings.TransformationFcn transformation) {
        ElementWise function = ElementWise.getFunction(transformation);
        if (function != null) {
            return function;
        }
        switch (transformation) {
            case IDENTITY:
                return Singletons.identity;
            case TRANSP:
                return Singletons.transposition;
            case NORM:
                return Singletons.normalization;
            case SOFTMAX:
                return Singletons.softmax;
            case SPARSEMAX:
                return Singletons.sparsemax;
            default:
                LOG.severe("Unimplemented Transformation function");
                return null;
        }
    }

    public static class Singletons {
        public static Softmax softmax = new Softmax();
        public static Sparsemax sparsemax = new Sparsemax();
        public static Normalization normalization = new Normalization();

        public static Transposition transposition = new Transposition();
        public static Identity identity = new Identity();
        public static ConstantOne constantOne = new ConstantOne();

    }


    public static abstract class State implements ActivationFcn.State {

        protected Transformation transformation;

        protected Value input;
        protected Value processedGradient;

        public State(Transformation transformation){
            this.transformation = transformation;
        }

        @Override
        public void cumulate(Value value) {
            if (input != null){
                LOG.severe("Resetting input in Transformation.State (this should probably be Combination.State instead!)");
            }
            input = value;  // there should be only a single input value for this state type!!
        }

        @Override
        public void invalidate() {
            input = null;
            processedGradient = null;
        }

        @Override
        public Value evaluate() {
            return transformation.evaluate(input);
        }

        @Override
        public Value initEval(List<Value> inputValues) {
            if (inputValues.size() != 1){
                LOG.severe("Setting up Transformation.State with more than one Value.");
            }
            input = inputValues.get(0);
            return evaluate();
        }

        public Value gradient() {
            return transformation.differentiate(input);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            Value inputFcnDerivative = gradient();
            processedGradient = inputFcnDerivative.times(topGradient);  //times here - since the fcn was a complex vector function (e.g. softmax) and has a matrix derivative (Jacobian)
        }

        @Override
        public Value nextInputGradient() {
            return processedGradient;
        }

        @Override
        public Transformation getTransformation() {
            return transformation;
        }

        @Override
        public ActivationFcn.State changeTransformationState(Transformation transformation) {
            if (transformation.getClass().equals(this.transformation.getClass())){
                return this;    // no change
            } else {
                return transformation.getState(true);
            }
        }

        @Override
        public Combination getCombination() {
            return null;
        }
    }


}