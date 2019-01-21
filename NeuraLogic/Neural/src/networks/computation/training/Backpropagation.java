package networks.computation.training;

import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.DFSstack;
import networks.computation.iteration.StandardNeuronVisitors;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.Topologic;
import networks.computation.iteration.actions.Backproper;
import networks.computation.iteration.actions.Evaluator;
import networks.computation.iteration.actions.WeightUpdater;
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
    public TopDown<WeightUpdater> getTopDownPropagator(NeuralNetwork<State.Neural.Structure> network, Neuron outputNeuron) {
        if (network instanceof TopologicNetwork) {
            StandardNeuronVisitors.Down down = new StandardNeuronVisitors.Down(network, backproper, evaluator, weightUpdater);
            new Topologic((TopologicNetwork<State.Neural.Structure>) network).new TDownVisitor(outputNeuron, down);
        } else {
            new DFSstack(network, backproper).new TDownVisitor(outputNeuron);
        }
        return null;
    }


    public WeightUpdater backpropagate(NeuralSample neuralSample, Result evaluatedResult) {
        NeuralNetwork<State.Neural.Structure> neuralNetwork = neuralSample.query.evidence;
        AtomNeuron<State.Neural> outputNeuron = neuralSample.query.neuron;
        Value errorGradient = evaluatedResult.errorGradient();

        outputNeuron.getComputationView(backproper.stateIndex).store(backproper, errorGradient); //store the error gradient into the output neuron, from where it will be further propagated

        TopDown<WeightUpdater> topDownPropagator = getTopDownPropagator(neuralNetwork, outputNeuron);
        WeightUpdater weightUpdater = topDownPropagator.topdown();
        return weightUpdater;
    }

}
