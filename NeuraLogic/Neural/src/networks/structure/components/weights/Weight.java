package networks.structure.components.weights;

import com.sun.istack.internal.Nullable;
import constructs.template.metadata.WeightMetadata;
import networks.computation.evaluation.values.Value;
import networks.computation.evaluation.values.distributions.ValueInitializer;

/**
 * Created by gusta on 8.3.17.
 */
public class Weight {
    /**
     * Weights should be created via factory => this is a unique identifier
     */
    public final int index;

    public String name;
    public Value value;
    public boolean isFixed = false;

    public boolean isShared;

    /**
     * The flag needs to be set by an external routine.
     */
    public boolean dropout = false;

    @Nullable
    double learningRate;
    //public String originalString;

    public WeightMetadata metadata;

    public static Weight unitWeight = new Weight(-1, "unitWeight", Value.ONE, true);
    public static Weight zeroWeight = new Weight(-1, "zeroWeight", Value.ZERO, true);

    public Weight(int index, String name, Value value, boolean fixed) {
        this.index = index;
        this.name = name;
        this.value = value;
        this.isFixed = fixed;
    }

    protected Weight(int index, Value value) {
        this.index = index;
        this.value = value;
        this.isFixed = true;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Weight)) {
            return false;
        }
        Weight obj1 = (Weight) obj;
        return index == obj1.index;
    }

    public void init(ValueInitializer valueInitializer) {
        value.initialize(valueInitializer);
    }

    @Override
    public String toString() {
        return name + " " + value.toString();
    }
}