package networks.computation.iteration.visitors.states;

import networks.computation.evaluation.values.Value;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
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
        return new Backproper(i);   //todo
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

    //todo next - should return a mask over inputs if something like MAX is used, or -1 otherwise
    @Override
    public Value visit(State.Neural.Computation state) {
        Value acumGradient = state.getResult(this); //top-down accumulation
        Value inputDerivative = state.getAggregationState().gradient(); //bottom-up accumulation

        Value currentLevelDerivative = acumGradient.times(inputDerivative);
        //there is no setting (remembering) of the calculated gradient (as opposed to output, which is reused), it is just returned
        return currentLevelDerivative;//todo next - where should we invalidate?
    }

}