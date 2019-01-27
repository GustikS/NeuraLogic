package networks.computation.iteration.actions;

import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.modes.DFSrecursion;
import networks.computation.iteration.modes.Topologic;
import networks.computation.iteration.visitors.neurons.StandardNeuronVisitors;
import networks.computation.iteration.visitors.states.Backproper;
import networks.computation.iteration.visitors.states.Evaluator;
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
     * @param network
     * @param outputNeuron
     * @return
     */
    public TopDown getTopDownPropagator(NeuralNetwork<State.Neural.Structure> network, Neuron outputNeuron) {
        if (network instanceof TopologicNetwork) {
            StandardNeuronVisitors.Down down = new StandardNeuronVisitors.Down(network, backproper, evaluator, weightUpdater);
            new Topologic((TopologicNetwork<State.Neural.Structure>) network).new TDownVisitor(outputNeuron, down);
        } else {
            new DFSrecursion().new TDonwVisitor(network, outputNeuron, backproper, evaluator, weightUpdater);
        }
        return null;
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
