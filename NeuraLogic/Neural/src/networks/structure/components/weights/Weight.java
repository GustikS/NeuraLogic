package networks.structure.components.weights;

import constructs.template.metadata.WeightMetadata;
import networks.computation.evaluation.values.Value;
import networks.computation.evaluation.values.distributions.ValueInitializer;
import org.jetbrains.annotations.Nullable;

/**
 * Created by gusta on 8.3.17.
 */
public class Weight {
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
    public static Weight zeroWeight = new Weight(-1, "zeroWeight" ,Value.ZERO, true);

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
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return name.equals(obj);
    }

    public void init(ValueInitializer valueInitializer) {
        value.initialize(valueInitializer);
    }

}