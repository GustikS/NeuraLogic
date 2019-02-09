package networks.structure.metadata.states;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.states.*;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.inputMappings.LinkedMapping;
import settings.Settings;

import java.util.logging.Logger;

/**
 * An agglomeration of classes for storing computational and structural States (see interface State) of Neurons.
 * <p>
 * Similarly to Neuron(s), States are (almost) pure containers, and the various logics of operations upon them (evaluation, backprop, etc.)
 * are carried out by separate StateVisitors, for the same reasons of clarity (at the slight expense of speed).
 */
public abstract class States implements State {
    private static final Logger LOG = Logger.getLogger(States.class.getName());


    /**
     * Storing a State that is an array of other States, e.g. for parallel mini-batch processing, where different views of the same neuron may operate with different states over that single neuron.
     *
     * @param <T>
     */
    public static final class ComputationStateComposite<T extends State.Neural.Computation> implements State.Neural<Value> {
        public final T[] states;
        Aggregation aggregation;

        public ComputationStateComposite(T[] states) {
            this.states = states;
        }

        @Override
        public State.Neural.Computation getComputationView(int index) {
            return states[index];
        }

        @Override
        public Aggregation getAggregation() {
            return aggregation;
        }

        //todo implement all the methods redirection here, or create separate visitors for each method.
        @Override
        public Value accept(StateVisiting<Value> visitor) {
            return states[visitor.stateIndex].accept(visitor);
        }

        @Override
        public void invalidate() {
            for (int i = 0; i < states.length; i++) {
                states[i].invalidate();
            }
        }
    }

    /**
     * A typical, minimal, lightweight State that consists of aggregationState (before activation), output value (after activation), and gradient (before activation).
     * Typical use is for Topologic iteration, where no extra information is needed.
     * <p>
     * Even though Evaluation and Backprop are always carried out separately, and so it seems that a single Value placeholder
     * could be stored here, value and gradient must be held as two separate Values, since {@link networks.computation.iteration.actions.Backpropagation} needs both to calculate gradient.
     */
    public static class ComputationStateStandard implements Neural.Computation {

        AggregationState aggregationState;
        Value outputValue;
        Value acumGradient;

        @Override
        public void invalidate() {
            outputValue.zero();
            acumGradient.zero();
            aggregationState.invalidate();
        }

        @Override
        public Aggregation getAggregation() {
            return aggregationState.getAggregation();
        }

        @Override
        public AggregationState getAggregationState() {
            return aggregationState;
        }

        @Override
        public Value getResult(StateVisiting<Value> visitor) {
            LOG.severe("Error: Visitor calling a default method through dynamic dispatch.");
            return null;
        }

        @Override
        public void setResult(StateVisiting<Value> visitor, Value value) {
            LOG.severe("Error: Visitor calling a default method through dynamic dispatch.");
        }

        public void setResult(Evaluator visitor, Value value) {
            outputValue = value;
        }

        public void setResult(Backproper visitor, Value value) {
            acumGradient = value;
        }

        public Value getResult(Evaluator visitor) {
            return outputValue;
        }

        public Value getResult(Backproper visitor) {
            return acumGradient;
        }

        @Override
        public void store(StateVisiting<Value> visitor, Value value) {
            LOG.severe("Error: Visitor calling a default method through dynamic dispatch.");
        }

        public void store(Evaluator visitor, Value value) {
            aggregationState.cumulate(value);
        }

        public void store(Backproper visitor, Value value) {
            acumGradient.increment(value);
        }
    }

    /**
     * Simple storage of parent count for efficient backprop computation with DFS (may vary due to neuron sharing in different contexts).
     */
    public static class ParentCounter extends ComputationStateStandard implements Neural.Computation.HasParents {
        public final int count; //todo next - this should be part of a structure state as the number of parents may vary from network to network!
        public int checked = 0;
        /**
         * A simple flag to signify whether the result of this state can be reused already (= is finished, instead of checking whether its zero as in the previous version).
         */
        boolean calculated;

        public ParentCounter(int count) {
            this.count = count;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            checked = 0;
            calculated = false;
        }

        @Override
        public void store(Backproper visitor, Value value) {
            super.store(visitor, value);
            checked++;
        }

        @Override
        public boolean ready4expansion(StateVisiting visitor) {
            LOG.warning("Default double dispatch call.");
            return true;
        }

        public boolean ready4expansion(Backproper visitor) {
            return checked == count;
        }

        public boolean ready4expansion(Evaluator visitor) {
            return calculated;
        }

        public boolean ready4expansion(Invalidator visitor) {
            return calculated;
        }

        @Override
        public int getParents(StateVisiting visitor) {
            return count;
        }

        @Override
        public int getChecked(StateVisiting visitor) {
            return checked;
        }

        @Override
        public void setChecked(StateVisiting visitor, int checked) {
            this.checked = checked;
        }

        public Value getResult(Evaluator visitor) {
            calculated = true;
            return super.getResult(visitor);
        }
    }

    public static final class DropoutStore extends ComputationStateStandard implements Neural.Computation.HasDropout {

        public double dropoutRate;
        public boolean isDropped;
        private boolean dropoutProcessed;
        private Settings settings;

        public DropoutStore(Settings settings, double dropoutRate) {
            this.settings = settings;
            this.dropoutRate = dropoutRate;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            isDropped = false;
            dropoutProcessed = false;
        }

        public boolean ready4expansion(Dropouter visitor) {
            return !dropoutProcessed;
        }

        @Override
        public double getDropout(StateVisiting visitor) {
            return dropoutRate;
        }

        @Override
        public void setDropout(StateVisiting visitor) {
            if (settings.random.nextDouble() < settings.dropoutRate)
                isDropped = true;
            else
                isDropped = false;
            dropoutProcessed = true;
        }

        public final class ParentsDropoutStore extends ParentCounter implements Neural.Computation.HasDropout {

            public ParentsDropoutStore(Settings settings, int count, double dropoutRate) {
                super(count);
                DropoutStore.this.settings = settings;
                DropoutStore.this.dropoutRate = dropoutRate;
            }

            @Override
            public double getDropout(StateVisiting visitor) {
                return DropoutStore.this.getDropout(visitor);
            }

            @Override
            public void setDropout(StateVisiting visitor) {
                DropoutStore.this.setDropout(visitor);
            }
        }
    }


    /**
     * Nothing but a Value. E.g. for Fact Neurons.
     */
    public static class SimpleValue implements Neural.Computation {

        Value value;

        public SimpleValue(Value factValue) {
            this.value = factValue;
        }

        @Override
        public void invalidate() {
            //void
        }

        @Override
        public AggregationState getAggregationState() {
            return null;
        }

        @Override
        public Value getResult(StateVisiting<Value> visitor) {
            return value;
        }

        @Override
        public void setResult(StateVisiting<Value> visitor, Value value) {
            //void
        }

        @Override
        public void store(StateVisiting<Value> visitor, Value value) {
            //void
        }

        @Override
        public Aggregation getAggregation() {
            return null;
        }
    }
    //-------------------

    /**
     * Storing inputs and outputs of each neuron (may vary due to neuron sharing in different contexts).
     * This information should be stored in a Network (not Neuron).
     */
    public static final class InputsOutputsPair implements Neural.Structure {
        LinkedMapping<Neuron> inputs;
        LinkedMapping<Neuron> outputs;


        public final LinkedMapping getOutputs() {
            return outputs;
        }

        public LinkedMapping<Neuron> getInputs(Neuron neuron) {
            return inputs;
        }

        @Override
        public void invalidate() {
            //void
        }
    }

}