package cz.cvut.fel.ida.neural.networks.computation.iteration.actions;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.BottomUp;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSrecursion;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSstack;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.StandardNeuronVisitors;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class Evaluation {
    private static final Logger LOG = Logger.getLogger(Evaluation.class.getName());
    Settings settings;

    /**
     * Evaluator is a StateVisitor, takes care of the action taken during iteration
     */
    Evaluator evaluator;

    Result.Factory resultFactory;

    public Evaluation(Settings settings) {
        this(settings, -1);
    }

    public Evaluation(Settings settings, int index) {
        this.settings = settings;
        this.evaluator = Evaluator.getFrom(settings, index);
        this.resultFactory = new Result.Factory(settings);
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
        StandardNeuronVisitors.Up up = new StandardNeuronVisitors.Up(network, evaluator);
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
}

