package cz.cvut.fel.ida.neural.networks.computation.iteration.actions;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.BottomUp;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSrecursion;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSstack;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PythonEvaluation extends Evaluation {
    public Set<String> hooks = new HashSet<>();

    public PythonHookHandler hookHandler;

    public PythonEvaluation(Settings settings, int index) {
        super(settings, index);
    }

    /**
     * Get the best mode of BottomUp iteration through this NeuralNetwork given the target of Evaluation of the output Neuron.
     * - todo check with inputMapping for topologic network
     *
     * @param settings
     * @param network
     * @param evaluator
     * @return
     */
    private BottomUp<Value> getBottomUpIterationStrategy(Settings settings, NeuralNetwork<State.Neural.Structure> network, Neurons outputNeuron, Evaluator evaluator) {
        PythonUp up = new PythonUp(network, evaluator);
        if (network instanceof TopologicNetwork) {
            return new Topologic((TopologicNetwork<State.Neural.Structure>) network).new BUpVisitor(outputNeuron, up);
        } else if (settings.iterationMode == Settings.IterationMode.DFS_RECURSIVE) {
            return new DFSrecursion().new BUpVisitor(network, outputNeuron, up);
        } else {
            return new DFSstack().new BUpIterator(network, outputNeuron, up);
        } // no BFS for bottomUp
    }

    public Result evaluate(NeuralSample sample) {
        Value output = evaluate(sample.query);
        Result result = resultFactory.create(sample.getId(), sample.position, sample.target, output);
        return result;
    }

    public Value evaluate(QueryNeuron queryNeuron) {
        NeuralNetwork<State.Neural.Structure> network = queryNeuron.evidence;
        AtomNeurons<State.Neural> outputNeuron = queryNeuron.neuron;

        BottomUp<Value> propagator = getBottomUpIterationStrategy(settings, network, outputNeuron, evaluator);
        Value output = propagator.bottomUp().clone();
        return output;
    }

    public class PythonUp extends NeuronVisitor.Weighted {

        /**
         * Up = loading some values from inputs of actual neuron into its State - e.g. for {@link Evaluation}
         *
         * @param network
         * @param computationVisitor
         */
        public PythonUp(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor) {
            super(network, computationVisitor, null);
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Iterator<T> inputs = network.getInputs(neuron);
            T input;
            while (inputs.hasNext()) {
                input = inputs.next();
                state.cumulateValue(input.getComputationView(stateVisitor.stateIndex).getValue());
            }
            Value value = stateVisitor.visit(state);

            if (hooks.contains(neuron.name)) {
                hookHandler.handleHook(neuron.name, value.toString());
            }
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);
            Iterator<T> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            T input;
            Weight weight;

            if (neuron.offset.value != Value.ZERO)  // only store offset if it is not void - otherwise there may be problems with its behavior w.r.t. some functions
                state.cumulateValue(neuron.offset.value);

            while (inputNeurons.hasNext()) {
                input = inputNeurons.next();
                weight = inputWeights.next();
                state.cumulateValue(weight.value.times(input.getComputationView(stateVisitor.stateIndex).getValue()));
            }

            Value value = stateVisitor.visit(state);

            if (hooks.contains(neuron.name)) {
                hookHandler.handleHook(neuron.name, value.toString());
            }
        }
    }
}

