package cz.cvut.fel.ida.algebra.weights;

import cz.cvut.fel.ida.algebra.utils.metadata.WeightMetadata;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;

/**
 * Created by gusta on 8.3.17.
 */
public class Weight implements Exportable {

    /**
     * Weights should be created via factory => this is a unique identifier (for learnable weights), but can be reindexed
     */
    public final int index;

    /**
     * This is a globally unique identifier, contains the index at construction time, cannot be changed
     */
    public final String name;

    public Value value;
    public boolean isFixed = false;
    public Boolean isLearnable;
    public boolean manualInitialization = false;   //todo add the init weight value to weight metadata

    public boolean isShared;

    /**
     * The flag needs to be set by an external routine.
     */
    public boolean dropout = false;

    @Nullable
    double learningRate;
    //public String originalString;

    public boolean isOffset;
    public Value momentum;  //todo move these to some map within Adam?
    public Value velocity;

    public WeightMetadata metadata;

    public static Weight unitWeight = new Weight(-1, "one", Value.ONE, true, true);
    public static Weight zeroWeight = new Weight(-2, "zero", Value.ZERO, true, true);

    public Weight(int index, String name, Value value, boolean fixed, boolean isInitialized) {
        this.index = index;
        this.name = name;
        this.value = value;
        this.isFixed = fixed;
        this.manualInitialization = isInitialized;
        if (isInitialized) {
            this.metadata = new WeightMetadata(value);
        }
        if (isFixed || index < 0) {
            isLearnable = false;
        }
    }

    @Override
    public Weight clone() {
        Weight clone = new Weight(this.index, this.name, this.value.clone(), this.isFixed, this.manualInitialization);
        clone.metadata = this.metadata;
        clone.dropout = this.dropout;
        clone.isShared = this.isShared;
        if (momentum != null) { //adam
            clone.momentum = this.momentum.clone();
            clone.velocity = this.velocity.clone();
        }
        return clone;
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
        if (this.isFixed) {
            return;
        }
        if (this.velocity != null)
            this.velocity.zero();
        if (this.momentum != null)
            this.momentum.zero();
        if (this.manualInitialization) {
            this.value = (Value) metadata.getByName("initValue");
            return;
        }
        value.initialize(valueInitializer);
    }

    @Override
    public String toString() {
        return toString(Settings.shortNumberFormat);
    }

    public String toString(NumberFormat nf) {
        StringBuilder sb = new StringBuilder();
        if (manualInitialization || isFixed || isShared) {
            sb.append("<" + name + ">:");
        } else {
            sb.append(name + ":");
        }
        sb.append(value.toString(nf));
        return sb.toString();
    }

    public boolean isLearnable() {
        if (isLearnable != null) {
            return isLearnable;
        }
        if (isFixed) {
            isLearnable = false;
        } else if (index < 0) {
            isLearnable = false;
        } else if (value == Value.ONE || value == Value.ZERO) {
            isLearnable = false;
        } else {
            isLearnable = true;
        }
        return isLearnable;
    }
}