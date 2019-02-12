package networks.computation.iteration.actions;

import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.modes.BFS;
import networks.computation.iteration.modes.DFSrecursion;
import networks.computation.iteration.modes.DFSstack;
import networks.computation.iteration.modes.Topologic;
import networks.computation.iteration.visitors.neurons.StandardNeuronVisitors;
import networks.computation.iteration.visitors.states.neurons.Backproper;
import networks.computation.iteration.visitors.states.neurons.Evaluator;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.logging.Logger;

public class Backpropagation {
    private static final Logger LOG = Logger.getLogger(Backpropagation.class.getName());
    private final Settings settings;

    WeightUpdater weightUpdater;

    /**
     * Backproper is a StateVisitor, takes care of the action taken during iteration
     */
    Backproper backproper;

    /**
     * We will need evaluator as well for backpropagation as the gradient computation requires values, too
     */
    Evaluator evaluator;

    public Backpropagation(Settings settings, NeuralModel model, int index) {
        this.settings = settings;
        this.backproper = Backproper.getFrom(settings, index);
        this.evaluator = Evaluator.getFrom(settings, index);
        this.weightUpdater = new WeightUpdater(model.weights);
    }

    public Backpropagation(Settings settings, NeuralModel neuralModel) {
        this(settings, neuralModel, -1);

    }

    /**
     * The most efficient network iteration depends on its structure, so it's to be decided on the run.
     *
     * For Max and MaxK functions, i.e. those with input mask, choose visitor instead of iterator, so that the inputs are expanded through the mask.
     *    - todo or add the mask filtering of inputs to iterators, too?
     *
     *    - todo check with inputMapping for topologic network
     * Also input masking cannot work with topologic ordering.
     *
     * @param network
     * @param outputNeuron
     * @return
     */
    public TopDown getTopDownPropagator(NeuralNetwork<State.Neural.Structure> network, Neuron outputNeuron) {
        if (network instanceof TopologicNetwork && !network.containsPooling) {
            StandardNeuronVisitors.Down down = new StandardNeuronVisitors.Down(network, backproper, evaluator, weightUpdater);
            return new Topologic((TopologicNetwork<State.Neural.Structure>) network).new TDownVisitor(outputNeuron, down);
        } else if (settings.iterationMode == Settings.IterationMode.DFS_RECURSIVE) {
            return new DFSrecursion().new TDownVisitor(network, outputNeuron, backproper, evaluator, weightUpdater);
        } else if (settings.iterationMode == Settings.IterationMode.DFS_STACK) {
            return new DFSstack().new TDownVisitor(network, outputNeuron, backproper, evaluator, weightUpdater);
        } else {
            return new BFS().new TDownVisitor(network, outputNeuron, backproper, evaluator, weightUpdater);
        }
    }

    public WeightUpdater backpropagate(NeuralSample neuralSample, Result evaluatedResult) {
        NeuralNetwork<State.Neural.Structure> neuralNetwork = neuralSample.query.evidence;
        AtomNeuron<State.Neural> outputNeuron = neuralSample.query.neuron;
        Value errorGradient = evaluatedResult.errorGradient();

        outputNeuron.getComputationView(backproper.stateIndex).store(backproper, errorGradient); //store the error gradient into the output neuron, from where it will be further propagated

        TopDown topDownPropagator = getTopDownPropagator(neuralNetwork, outputNeuron);
        topDownPropagator.topdown();
        return weightUpdater;   //as a side effect of the iteration the weightUpdater will get filled (functional way only makes sense in bottom-up evaluation, this is stateful)
    }

}
