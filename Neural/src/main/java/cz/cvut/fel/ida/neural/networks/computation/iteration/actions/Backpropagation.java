package cz.cvut.fel.ida.neural.networks.computation.iteration.actions;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.neural.networks.computation.iteration.TopDown;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.BFS;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSrecursion;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSstack;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.Down;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Backproper;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class Backpropagation {
    private static final Logger LOG = Logger.getLogger(Backpropagation.class.getName());
    private final Settings settings;

    public WeightUpdater weightUpdater;

    /**
     * Backproper is a StateVisitor, takes care of the action taken during iteration
     */
    public Backproper backproper;

    public Backpropagation(Settings settings, NeuralModel model, int index) {
        this.settings = settings;
        this.backproper = Backproper.getFrom(settings, index);
        this.weightUpdater = new WeightUpdater(model.learnableWeights, model.maxWeightIndex);
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

        if (network instanceof TopologicNetwork && (!network.containsInputMasking || settings.iterationMode == Settings.IterationMode.TOPOLOGIC)) {
            NeuronVisitor.Weighted down = new Down(network, backproper, weightUpdater);
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
        return this.backpropagate(neuralSample, evaluatedResult.errorGradient());
    }

    public WeightUpdater backpropagate(NeuralSample neuralSample, Value errorGradient) {
        NeuralNetwork<State.Neural.Structure> neuralNetwork = neuralSample.query.evidence;
        AtomNeurons<State.Neural> outputNeuron = neuralSample.query.neuron;

//        errorGradient = errorGradient.times(new ScalarValue(settings.initLearningRate));//rather do not use this old trick, it does not help performance-wise that much and isn't too readable (and only usable for SGD, not ADAM)

        weightUpdater.clearUpdates();
        outputNeuron.getComputationView(backproper.stateIndex).storeGradient(errorGradient); //store the error gradient into the output neuron, from where it will be further propagated

        TopDown topDownPropagator = getTopDownPropagator(neuralNetwork, outputNeuron);
        topDownPropagator.topdown();
        return weightUpdater;   //as a side effect of the iteration the weightUpdater will get filled (functional way only makes sense in bottom-up evaluation, this is stateful)
    }

}
