package networks.computation.training.evaluation;

import networks.computation.iteration.BottomUp;
import networks.computation.iteration.actions.Evaluator;
import networks.computation.results.Result;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.evaluation.values.Value;
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
     * @param settings
     * @param count
     * @return
     */
    public List<Evaluator> getParallelEvaluators(Settings settings, int count) {
        List<Evaluator> evaluators = new ArrayList<>(count);
        for (int i = 0; i < evaluators.size(); i++) {
            evaluators.add(i,new Evaluator(i));
        }
        return evaluators;
    }

    /**
     * todo move to BottomUp
     * Get the best mode of BottomUp iteration through this NeuralNetwork given the target of Evaluation of the output Neuron.
     *
     * @param settings
     * @param network
     * @param evaluator
     * @return
     */
    private BottomUp<Value> getBottomUpIterationStrategy(Settings settings, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> outputNeuron, Evaluator evaluator) {
        if (network instanceof TopologicNetwork) {
            return new BottomUpTopologicIterator(network, outputNeuron, evaluator);
        } else return new BottomUpDFSstackVisitor(network, outputNeuron, evaluator);
    }

    public Result evaluate(NeuralModel model, NeuralSample sample) {
        NeuralNetwork<State.Structure> network = sample.query.evidence;
        AtomNeuron outputNeuron = sample.query.neuron;

        /**
         * Takes care of the logic of iteration
         */
        BottomUp propagator = getBottomUpIterationStrategy(settings, network, outputNeuron, evaluator);
        Value output = propagator.iterate();
        //todo invalidate all neurons + set values to offset!
        Result result = new Result(sample.target, output);
        return result;
    }
}
