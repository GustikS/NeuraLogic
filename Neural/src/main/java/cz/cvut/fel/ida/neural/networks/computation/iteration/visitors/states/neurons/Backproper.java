package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.setup.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * - todo to be removed - the fcnStates do this directly now
 * - this is now only valid for symmetric functions (deprecated)
 * Created by gusta on 8.3.17.
 */
@Deprecated
public class Backproper extends StateVisiting.Computation {
    private static final Logger LOG = Logger.getLogger(Backproper.class.getName());

    public Backproper(int stateIndex) {
        super(stateIndex);
    }

    /**
     * Get possibly different StateVisitors of Backproper's type to manipulate Neurons' States
     *
     * @param settings
     * @param i
     * @return
     */
    public static Backproper getFrom(Settings settings, int i) {
        return new Backproper(i);   //todo base on settings
    }

    /**
     * Get multiple evaluators with different state access views/indices
     *
     * @param settings
     * @param count
     * @return
     */
    @Deprecated
    public static List<Backproper> getParallelEvaluators(Settings settings, int count) {
        List<Backproper> backpropers = new ArrayList<>(count);
        for (int i = 0; i < backpropers.size(); i++) {
            backpropers.add(i, new Backproper(i));
        }
        return backpropers;
    }

    @Override
    public Value visit(State.Neural.Computation state) {
        LOG.warning("Calling obsolete Backproper...");
        Value acumGradient = state.getGradient(); //top-down accumulation //todo test add check if non-zero and cut otherwise?

        state.getFcnState().ingestTopGradient(acumGradient);

        Value inputFcnDerivative = state.getFcnState().nextInputGradient(); // todo WARNING - only valid for symmetric functions (deprecated)

        return inputFcnDerivative;

//        Value currentLevelDerivative;
//        //todo add case where inputFcnDerivative is just one (no change in gradient, e.g. concat, transp...)
//        if (inputFcnDerivative instanceof One) {
//            currentLevelDerivative = acumGradient;
//        } else if (state.getFcnState().getCombination() instanceof Transposition){
//            currentLevelDerivative = acumGradient.transposedView();
//        } else if (acumGradient.getClass().equals(inputFcnDerivative.getClass()) || inputFcnDerivative instanceof ScalarValue) {
//            currentLevelDerivative = acumGradient.elementTimes(inputFcnDerivative);  //elementTimes here - since the fcn to be differentiated was applied element-wise on a vector
//        } else {
////            currentLevelDerivative = acumGradient.transposedView().times(inputFcnDerivative);  //times here - since the fcn was a complex vector function (e.g. softmax) and has a matrix derivative (Jacobian)
//            currentLevelDerivative = inputFcnDerivative.times(acumGradient);
//        }
//
//        //there is no setting (remembering) of the calculated gradient (as opposed to output, which is reused), it is just returned
//        return currentLevelDerivative;
    }
}