package networks.computation.evaluation;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.BottomUp;
import networks.computation.iteration.DFSstack;
import networks.computation.iteration.PureNeuronVisitor;
import networks.computation.iteration.Topologic;
import networks.computation.iteration.actions.Evaluator;
import networks.computation.evaluation.results.Result;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Evaluation {
    private static final Logger LOG = Logger.getLogger(Evaluation.class.getName());
    Settings settings;

    /**
     * Evaluator is a StateVisitor, takes care of the action taken during iteration
     */
    Evaluator evaluator;

    public Evaluation(Settings settings) {
        this.settings = settings;
        this.evaluator = getEvaluator(settings);
    }

    /**
     * Get possibly different StateVisitors of Evaluator's type to manipulate Neurons' States
     *
     * @param settings
     * @return
     */
    public Evaluator getEvaluator(Settings settings) {
        return new Evaluator(-1);
    }

    /**
     * Get multiple evaluators with different state access views/indices
     *
     * @param settings
     * @param count
     * @return
     */
    public List<Evaluator> getParallelEvaluators(Settings settings, int count) {
        List<Evaluator> evaluators = new ArrayList<>(count);
        for (int i = 0; i < evaluators.size(); i++) {
            evaluators.add(i, new Evaluator(i));
        }
        return evaluators;
    }

    /**
     * todo make the choice complete
     * Get the best mode of BottomUp iteration through this NeuralNetwork given the target of Evaluation of the output Neuron.
     *
     * @param settings
     * @param network
     * @param evaluator
     * @return
     */
    private BottomUp<Value> getBottomUpIterationStrategy(Settings settings, NeuralNetwork<State.Structure> network, Neuron outputNeuron, Evaluator evaluator) {
        if (network instanceof TopologicNetwork) {
            PureNeuronVisitor.Up up = new PureNeuronVisitor().new Up(evaluator, network);
            new Topologic((TopologicNetwork<State.Structure>) network, evaluator).new BUpVisitor(outputNeuron, up);
        } else {
            new DFSstack(network, evaluator).new BUpVisitor(outputNeuron);
        }
        return null;
    }

    public Result evaluate(NeuralModel model, NeuralSample sample) {
        NeuralNetwork<State.Structure> network = sample.query.evidence;
        AtomNeuron<State.Computation> outputNeuron = sample.query.neuron;

        BottomUp<Value> propagator = getBottomUpIterationStrategy(settings, network, outputNeuron, evaluator);
        Value output = propagator.bottomUp();
        Result result = new Result(sample.target, output);
        return result;
    }
}

