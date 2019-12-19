package networks.computation.iteration.actions;

import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.modes.BFS;
import networks.computation.iteration.modes.DFSrecursion;
import networks.computation.iteration.modes.DFSstack;
import networks.computation.iteration.modes.Topologic;
import networks.computation.iteration.visitors.neurons.CrossDown;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.computation.iteration.visitors.neurons.StandardNeuronVisitors;
import networks.computation.iteration.visitors.states.neurons.Backproper;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.types.AtomNeurons;
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

    public Backpropagation(Settings settings, NeuralModel model, int index) {
        this.settings = settings;
        this.backproper = Backproper.getFrom(settings, index);
        this.weightUpdater = new WeightUpdater(model.weights);
    }

    public Backpropagation(Settings settings, NeuralModel neuralModel) {
        this(settings, neuralModel, -1);

    }

    /**
     * The most efficient network iteration depends on its structure, so it's to be decided on the run.
     * <p>
     * For Max and MaxK functions, i.e. those with input mask, choose visitor instead of iterator, so that the inputs are expanded through the mask.
     * - todo or add the mask filtering of inputs to iterators, too?
     * <p>
     * - todo check with inputMapping for topologic network
     * Also input masking cannot work with topologic ordering.
     *
     * @param network
     * @param outputNeuron
     * @return
     */
    public TopDown getTopDownPropagator(NeuralNetwork<State.Neural.Structure> network, Neurons outputNeuron) {
//        return new DFSrecursion().new TDownVisitor(network, outputNeuron, backproper, weightUpdater);

        if (network instanceof TopologicNetwork && !network.containsInputMasking) {
            NeuronVisitor.Weighted down = new StandardNeuronVisitors.Down(network, backproper, weightUpdater);
            if (network.containsCrossProducts) {
                down = new CrossDown(down);
            }
            return new Topologic((TopologicNetwork<State.Neural.Structure>) network).new TDownVisitor(outputNeuron, down);
        } else if (settings.iterationMode == Settings.IterationMode.DFS_RECURSIVE) {
            return new DFSrecursion().new TDownVisitor(network, outputNeuron, backproper, weightUpdater);
        } else if (settings.iterationMode == Settings.IterationMode.DFS_STACK) {
            return new DFSstack().new TDownVisitor(network, outputNeuron, backproper, weightUpdater);
        } else {
            return new BFS().new TDownVisitor(network, outputNeuron, backproper, weightUpdater);
        }
    }

    public WeightUpdater backpropagate(NeuralSample neuralSample, Result evaluatedResult) {
        NeuralNetwork<State.Neural.Structure> neuralNetwork = neuralSample.query.evidence;
        AtomNeurons<State.Neural> outputNeuron = neuralSample.query.neuron;

        Value errorGradient = evaluatedResult.errorGradient();

        errorGradient = errorGradient.times(new ScalarValue(settings.initLearningRate));//todo now measure properly the performance of this single multiplication by learningRate and remove if no help

        weightUpdater.clearUpdates();
        outputNeuron.getComputationView(backproper.stateIndex).storeGradient(errorGradient); //store the error gradient into the output neuron, from where it will be further propagated

        TopDown topDownPropagator = getTopDownPropagator(neuralNetwork, outputNeuron);
        topDownPropagator.topdown();
        return weightUpdater;   //as a side effect of the iteration the weightUpdater will get filled (functional way only makes sense in bottom-up evaluation, this is stateful)
    }

}
