package networks.structure.metadata.states;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.actions.Backproper;
import networks.computation.iteration.actions.Evaluator;
import networks.computation.iteration.actions.StateVisiting;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.inputMappings.LinkedMapping;

import java.util.ArrayList;
import java.util.List;
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

        public void setResult(StateVisiting<Value> visitor, Value value) {
            //void
        }

        public Value getState(StateVisiting<Value> visitor) {
            return value;
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




    /**
     * A typical, minimal, lightweight State that consists of summed inputs (before activation), output value (after activation), and gradient (before activation) todo check
     * Even though Evaluation and Backprop are always carried out separately, and so it seems that a single Value placeholder
     * could be stored here, value and gradient must be held as two separate Values, since Backprop needs both to calculate gradient.
     *
     * todo check why this does not contain AggregationState - on purpose or OBSOLETE (replaced with ComputationStateStandard)?
     */
    public static class InputsOutputGradientDetailed implements Neural.Computation, Neural.Computation.Detailed {
        /**
         * In the most general case, we need to feed the whole list of inputs into the activation function, not just the sum.
         * E.g. for max/avg, although they could still have functions to calculate cumulatively without storing all the inputs,
         * there could possibly be functions that cannot be calculated in a cumulative fashion, e.g. when we need to process all elements twice (e.g. normalize/softmax ?).
         */
        List<Value> accumulatedInputs;
        /**
         * this temp results might still be handy for majority of functions
         */
        Value summedInputs;
        Value outputValue;
        Value acumGradient;

        @Override
        public void invalidate() {
            accumulatedInputs = new ArrayList<>(accumulatedInputs.size());
            outputValue.zero();
            acumGradient.zero();
        }


        @Override
        public AggregationState getAggregationState() {
            return null;
        }

        @Override
        public Value getResult(StateVisiting<Value> visitor) {
            LOG.severe("Error: Visitor calling a default method through dynamic dispatch.");
            return null;
        }

        public void setResult(StateVisiting<Value> visitor, Value value) {
            LOG.severe("Error: Visitor calling a default method through dynamic dispatch.");
        }

        public Value getState(StateVisiting<Value> visitor) {
            LOG.severe("Error: Visitor calling a default method through dynamic dispatch.");
            return null;
        }

        @Override
        public void store(StateVisiting<Value> visitor, Value value) {
            LOG.severe("Error: Visitor calling a default method through dynamic dispatch.");
        }

        public Value getOutput(Evaluator visitor) {
            return outputValue;
        }

        public void setOutput(Evaluator visitor, Value value) {
            this.outputValue = value;
        }

        public Value getOutput(Backproper visitor) {
            return acumGradient;
        }

        public void setOutput(Backproper visitor, Value value) {
            this.acumGradient = value;
        }

        @Override
        public List<Value> getMessages() {
            return accumulatedInputs;
        }

        public Value getCumulation(Evaluator visitor) {
            return summedInputs;
        }

        public Value getCumulation(Backproper visitor) {
            return acumGradient;
        }

        @Override
        public void setMessages(List<Value> values) {
            accumulatedInputs = values;
        }

        public void store(Evaluator visitor, Value value) {
            accumulatedInputs.add(value);
        }

        public void store(Backproper visitor, Value value) {
            acumGradient.increment(value);
        }

        @Override
        public Aggregation getAggregation() {
            return null;
        }
    }

    /**
     * Simple storage of parent count for efficient backprop computation with DFS (may vary due to neuron sharing in different contexts).
     */
    public static class ParentCounter extends InputsOutputGradientDetailed implements Neural.Computation.HasParents {
        public final int count;
        public int checked = 0;

        public ParentCounter(int count) {
            this.count = count;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            checked = 0;
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
    }

    public static final class DropoutStore extends InputsOutputGradientDetailed implements Neural.Computation.HasDropout {

        public final double dropoutRate;
        public boolean isDropped;

        public DropoutStore(double dropoutRate) {
            this.dropoutRate = dropoutRate;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            isDropped = false;
        }

        @Override
        public double getDropout(StateVisiting visitor) {
            return dropoutRate;
        }

        @Override
        public void setDropout(StateVisiting visitor, boolean isDropped) {
            this.isDropped = isDropped;
        }
    }

    public static final class ParentsDropoutStore extends ParentCounter implements Neural.Computation.HasDropout {
        public final double dropoutRate;
        public boolean isDropped;

        public ParentsDropoutStore(int count, double dropoutRate) {
            super(count);
            this.dropoutRate = dropoutRate;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            isDropped = false;
        }

        @Override
        public double getDropout(StateVisiting visitor) {
            return dropoutRate;
        }

        @Override
        public void setDropout(StateVisiting visitor, boolean isDropped) {
            this.isDropped = isDropped;
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