package networks.structure.components.weights;

import networks.computation.evaluation.values.Value;

import java.util.logging.Logger;

/**
 * Replaced with WeightUpdater which carries the state - the updates - which is probably clearer design (todo check same speed also?)
 */
@Deprecated
public class StatefulWeight extends Weight {
    private static final Logger LOG = Logger.getLogger(StatefulWeight.class.getName());

    private Value accumulatedUpdate;

    public StatefulWeight(int index, String name, Value value, boolean fixed) {
        super(index, name, value, fixed);
    }

    synchronized public Value getAccumulatedUpdate() {
        return accumulatedUpdate;
    }

    synchronized public void setAccumulatedUpdate(Value accumulatedUpdate) {
        this.accumulatedUpdate = accumulatedUpdate;
    }
}