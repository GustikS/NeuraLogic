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
    public final boolean isFixed;
    public boolean isLearnable = true;
    public boolean learnableSet = false;
    public boolean manualInitialization = false;   //todo add the init weight value to weight metadata

    public boolean isShared;

    /**
     * How much wider to draw this weight for the activation its own output passes through - see
     * {@link cz.cvut.fel.ida.algebra.values.inits.ActivationGain}. It is set from the rule carrying the
     * weight and read only by the initializers that already scale with shape.
     * <p>
     * The first one to be set wins, and the case where that matters is narrow. A <em>lifted</em> rule
     * grounding many times spreads its weight across the network but takes the same activation to every one
     * of those places, so they never disagree; only the same named weight written into two rules with
     * different activations does, and that is rare enough that picking one beats making anyone think about
     * it.
     */
    public double activationGain = 1.0;

    private boolean activationGainSet = false;

    /**
     * Whether a recursive rule applies this weight, so that the initializer draws it orthonormal and it
     * keeps the length of what flows through it over every application rather than on average.
     * <p>
     * Unlike {@link #activationGain} this is an or, not a first-wins: a weight used recurrently anywhere is
     * reused at depth, whatever else it also does.
     */
    public boolean onRecurrentRule = false;

    public void setActivationGain(double gain) {
        if (activationGainSet) {
            return;
        }
        this.activationGain = gain;
        this.activationGainSet = true;
    }

    /**
     * The flag needs to be set by an external routine.
     */
    public boolean dropout = false;

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
            this.isLearnable = false;
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
        if (learnableSet) {
            return isLearnable;
        }

        if (!isLearnable) {
            learnableSet = true;
            return false;
        }

        if (value == Value.ONE || value == Value.ZERO) {
            isLearnable = false;
        }

        learnableSet = true;
        return isLearnable;
    }
}