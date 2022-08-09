package cz.cvut.fel.ida.neural.networks.structure.components.neurons.states;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Backpropagation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.networks.ParentsTransfer;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Backproper;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Dropouter;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Invalidator;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.NeuronMapping;
import cz.cvut.fel.ida.neural.networks.structure.metadata.inputMappings.WeightedNeuronMapping;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Arrays;
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
    public static final class ComputationStateComposite<T extends Neural.Computation> implements Neural<Value> {
        public final T[] states;
        Combination combination;
        Transformation transformation;

        public ComputationStateComposite(T[] states) {
            this.states = states;
        }

        @Override
        public Neural.Computation getComputationView(int index) {
            return states[index];
        }

        @Override
        public Combination getCombination() {
            return combination;
        }

        @Override
        public Transformation getTransformation() {
            return transformation;
        }


        public Value accept(StateVisiting.Computation visitor) {
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
     * could be stored here, value and gradient must be held as two separate Values, since {@link Backpropagation} needs both to calculate gradient.
     */
    public static class ComputationStateStandard implements Neural.Computation {

        public ActivationFcn.State fcnState;
        public Value outputValue;
        public Value acumGradient;

        public ComputationStateStandard(Combination combination, Transformation transformation) {
            fcnState = ActivationFcn.State.getState(combination, transformation);
        }

        @Override
        public void invalidate() {
            outputValue.zero();
            acumGradient.zero();
            fcnState.invalidate();
        }

        @Override
        public Combination getCombination() {
            return fcnState.getCombination();
        }

        @Override
        public Transformation getTransformation() {
            return fcnState.getTransformation();
        }

        @Override
        public ComputationStateStandard clone() {
            ComputationStateStandard clone = new ComputationStateStandard(fcnState.getCombination(), fcnState.getTransformation());
            clone.outputValue = this.outputValue.clone();
            clone.acumGradient = this.acumGradient.clone();
            return clone;
        }

        @Override
        public ActivationFcn.State getFcnState() {
            return fcnState;
        }

        public void setFcnState(ActivationFcn.State fcnState) {
            this.fcnState = fcnState;
        }

        public void setValue(Value value) {
            outputValue = value;
        }

        public void setGradient(Value gradient) {
            acumGradient = gradient;
        }

        public Value getValue() {
            return outputValue;
        }

        public Value getGradient() {
            return acumGradient;
        }

        public void storeValue(Value value) {
            fcnState.cumulate(value);
        }

        public void storeGradient(Value value) {
            acumGradient.incrementBy(value);
        }

        @Override
        public Value evaluate() {
            return fcnState.evaluate();
        }

        @Override
        public Value initEval(List<Value> inputValues) {
            if (fcnState == null){
                LOG.severe("No fcnState created (initialized) in Neuron State.");
            }
            Value value = fcnState.initEval(inputValues);
            if (outputValue != null) {
                LOG.warning("Repeated initEval initialization of computation State.");
                if (!Arrays.equals(value.size(), outputValue.size()))
                    LOG.severe("Collision with previously inferred Value dimensions!");
            }
            outputValue = value;
            acumGradient = value.getForm();
            return outputValue;
        }

    }

    /**
     * A simple Value. E.g. for Fact Neurons. May be learnable (e.g. an embedding)
     */
    public static class SimpleValue implements Neural.Computation {

        Value outputValue;  // the Value is stored right here
        Value acumGradient; // also the gradient
        ActivationFcn.SimpleValueState fcnState;  // this state is just dummy (no computation there)
        public boolean isLearnable = false;     //not learnable by default

        public SimpleValue(Value factValue) {
            outputValue = factValue;
            acumGradient = factValue.getForm();
            fcnState = new ActivationFcn.SimpleValueState(factValue);
        }

        @Override
        public void invalidate() {
//            outputValue.zero();   // NO - this is not some intermediate result value, but a stored value (or weight to be continuously learned...)
            acumGradient.zero();
        }

        @Override
        public Computation clone() {
            return new SimpleValue(outputValue.clone());
        }

        @Override
        public ActivationFcn.SimpleValueState getFcnState() {
            return fcnState;
        }

        public void setFcnState(ActivationFcn.State fcnState) {
            this.fcnState = (ActivationFcn.SimpleValueState) fcnState;
        }

        @Override
        public Value getValue() {
            return outputValue;
        }

        @Override
        public Value getGradient() {
            return acumGradient;
        }

        @Override
        public void setValue(Value value) {
            outputValue = value;
        }

        @Override
        public void setGradient(Value gradient) {
            acumGradient = gradient;
        }

        @Override
        public void storeValue(Value value) {
//            LOG.warning("FactNeurons storeValue call");   // - this is ok, they receive the offset if learnable
            outputValue = value;    //there is no accumulation of values here (from inputs) as there are no inputs
        }

        @Override
        public void storeGradient(Value gradient) {
            if (isLearnable) {
                acumGradient.incrementBy(gradient);
            }
        }

        @Override
        public Value evaluate() {
            return outputValue;
        }

        @Override
        public Value initEval(List<Value> inputValues) {
            Value value = fcnState.initEval(inputValues);
            outputValue = value;
            acumGradient = value.getForm();
            return outputValue;
        }


        @Override
        public Combination getCombination() {
            return null;
        }

        @Override
        public Transformation getTransformation() {
            return null;
        }

    }


    //---------------------------


    /**
     * Simple storage of parent count for efficient backprop computation with DFS (may vary due to neuron sharing in different contexts).
     * <p>
     * IF THE NEURONS ARE SHARED WITH DIFFERENT PARENTS, use {@link NetworkParents} it in a {@link StatesCache} for each each neuron to transfer the correct parentCount
     */
    public static class ParentCounter extends ComputationStateStandard implements Neural.Computation.HasParents {
        public int parentCount;
        public int checked = 0;
        /**
         * A simple flag to signify whether the result of this state can be reused already (= is finished, instead of checking whether its zero as in the previous version).
         */
        boolean calculated;

        public ParentCounter(Combination combination, Transformation transformation, int count) {
            super(combination, transformation);
            this.parentCount = count;
        }

        /**
         * If we do not know the parentCount in advance
         */
        public ParentCounter(Combination combination, Transformation transformation) {
            super(combination, transformation);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            checked = 0;
            calculated = false;
        }

        public ParentCounter clone() {
            ParentCounter clone = (ParentCounter) super.clone();
            clone.parentCount = this.parentCount;
            clone.checked = this.checked;
            clone.calculated = this.calculated;
            return clone;
        }

        @Override
        public void storeGradient(Value gradient) {
            super.storeGradient(gradient);
            checked++;
        }

        @Override
        public boolean ready4expansion(StateVisiting visitor) {
            if (visitor instanceof Backproper) { //todo next make nicer
                return ready4expansion((Backproper) visitor);
            } else if (visitor instanceof Evaluator) {
                return ready4expansion((Evaluator) visitor);
            } else if (visitor instanceof Invalidator) {
                return ready4expansion((Invalidator) visitor);
            }
            return true;
        }

        public boolean ready4expansion(Backproper visitor) {
            return checked == parentCount;
        }

        public boolean ready4expansion(Evaluator visitor) {
            return calculated;
        }

        public boolean ready4expansion(Invalidator visitor) {
            return true;
        }

        @Override
        public int getParents(StateVisiting visitor) {
            return parentCount;
        }

        @Override
        public int getChecked(StateVisiting visitor) {
            return checked;
        }

        @Override
        public void setChecked(StateVisiting visitor, int checked) {
            this.checked = checked;
        }

        @Override
        public void setParents(StateVisiting visitor, int parentCount) {
            this.parentCount = parentCount;
        }

        @Override
        public void setValue(Value value) {
            super.setValue(value);
            calculated = true;
        }
    }

    public static final class DropoutStore extends ComputationStateStandard implements Neural.Computation.HasDropout {
        public double dropoutRate;
        public boolean isDropped;
        private boolean dropoutProcessed;
        private Settings settings;

        public DropoutStore(Settings settings, double dropoutRate, Combination combination, Transformation transformation) {
            super(combination, transformation);
            this.settings = settings;
            this.dropoutRate = dropoutRate;
        }

        public DropoutStore(Settings settings, Combination combination, Transformation transformation) {
            super(combination, transformation);
            this.settings = settings;
            this.dropoutRate = settings.dropoutRate;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            isDropped = false;
            dropoutProcessed = false;
        }

        public DropoutStore clone() {
            DropoutStore clone = (DropoutStore) super.clone();
            clone.dropoutRate = this.dropoutRate;
            clone.isDropped = this.isDropped;
            clone.dropoutProcessed = this.dropoutProcessed;
            clone.settings = this.settings;
            return clone;
        }

        public boolean ready4expansion(Dropouter visitor) {
            return !dropoutProcessed;
        }

        @Override
        public double getDropoutRate(StateVisiting visitor) {
            return dropoutRate;
        }

        @Override
        public void setDropoutRate(double rate) {
            this.dropoutRate = rate;
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

            public ParentsDropoutStore(Settings settings, Combination combination, Transformation transformation) {
                super(combination, transformation);
                DropoutStore.this.settings = settings;
            }

            public ParentsDropoutStore(Combination combination, Transformation transformation) {
                super(combination, transformation);
            }

            public ParentsDropoutStore(Settings settings, double dropoutRate, Combination combination, Transformation transformation) {
                super(combination, transformation);
                DropoutStore.this.settings = settings;
                DropoutStore.this.dropoutRate = dropoutRate;
            }

            public ParentsDropoutStore clone() {
                ParentsDropoutStore clone = new ParentsDropoutStore(DropoutStore.this.settings, DropoutStore.this.dropoutRate, this.fcnState.getCombination(), this.fcnState.getTransformation());
                clone.parentCount = this.parentCount;
                clone.checked = this.checked;
                clone.calculated = this.calculated; //todo check
                return clone;
            }

            @Override
            public double getDropoutRate(StateVisiting visitor) {
                return DropoutStore.this.getDropoutRate(visitor);
            }

            @Override
            public void setDropoutRate(double rate) {
                dropoutRate = rate;
            }

            @Override
            public void setDropout(StateVisiting visitor) {
                DropoutStore.this.setDropout(visitor);
            }

            @Override
            public void setParents(StateVisiting visitor, int parentCount) {
                this.parentCount = parentCount;
            }
        }
    }


    //-------------------

    /**
     * Storing inputs of each neuron (may vary due to neuron sharing in different contexts).
     * This information should be stored in a Network (not Neuron).
     */
    public static class Inputs implements Structure.InputNeuronMap {
        NeuronMapping<Neurons> inputs;

        public Inputs(NeuronMapping<Neurons> inputs) {
            this.inputs = inputs;
        }

        public NeuronMapping<Neurons> getInputMapping() {
            return inputs;
        }

        @Override
        public void invalidate() {
            //void
        }

    }

    public static class WeightedInputs implements Structure.WeightedInputsMap {
        WeightedNeuronMapping<Neurons> inputs;

        public WeightedInputs(WeightedNeuronMapping<Neurons> inputs) {
            this.inputs = inputs;
        }

        public WeightedNeuronMapping<Neurons> getWeightedMapping() {
            return inputs;
        }

        @Override
        public void invalidate() {
            //void
        }
    }

    public static class Outputs implements Structure.OutputNeuronMap {
        NeuronMapping<Neurons> outputs;

        public NeuronMapping<Neurons> getOutputMapping() {
            return outputs;
        }

        @Override
        public void invalidate() {
            //void
        }
    }

    public static class NetworkParents implements Structure<Value>, Structure.Parents {
        int parentCount;
        /**
         * This can possibly be a CompositeState of {@link ParentCounter}!
         */
        Neural<Value> parentCounter;

        public Value accept(ParentsTransfer visitor) {  //todo test this call
            visitor.parentsCount = parentCount;
            parentCounter.accept(visitor);
            return null;
        }

        public NetworkParents(Neural<Value> parentCounter, int parentCount) {
            this.parentCounter = parentCounter;
            this.parentCount = parentCount;
        }

        @Override
        public int getParentCount() {
            return parentCount;
        }

        @Override
        public void setParentCount(int parentCount) {
            this.parentCount = parentCount;
        }

        @Override
        public Neural<Value> getParentCounter() {
            return parentCounter;
        }

        @Override
        public void invalidate() {
            //void
        }

        public class InputsParents extends Inputs implements Structure.Parents {

            public InputsParents(NeuronMapping<Neurons> inputs) {
                super(inputs);
            }

            public NeuronMapping<Neurons> getInputMapping() {
                return inputs;
            }

            @Override
            public void invalidate() {
                //void
            }

            @Override
            public int getParentCount() {
                return parentCount;
            }

            @Override
            public void setParentCount(int parentCount) {
                NetworkParents.this.parentCount = parentCount;
            }

            @Override
            public Neural<Value> getParentCounter() {
                return parentCounter;
            }
        }

        public class WeightedInputsParents extends WeightedInputs implements Structure.Parents {

            public WeightedInputsParents(WeightedNeuronMapping<Neurons> inputs) {
                super(inputs);
            }

            public WeightedNeuronMapping<Neurons> getWeightedMapping() {
                return inputs;
            }

            @Override
            public void invalidate() {
                //void
            }

            @Override
            public int getParentCount() {
                return parentCount;
            }

            @Override
            public void setParentCount(int parentCount) {
                NetworkParents.this.parentCount = parentCount;
            }

            @Override
            public Neural<Value> getParentCounter() {
                return parentCounter;
            }
        }

    }

}