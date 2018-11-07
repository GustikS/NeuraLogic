package networks.computation.training;

import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.DFSstack;
import networks.computation.iteration.PureNeuronVisitor;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.Topologic;
import networks.computation.iteration.actions.Backproper;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;

public class Backpropagation {
    private static final Logger LOG = Logger.getLogger(Backpropagation.class.getName());
    private final Settings settings;

    /**
     * Backproper is a StateVisitor, takes care of the action taken during iteration
     */
    Backproper backproper;

    public Backpropagation(Settings settings) {
        this.settings = settings;
    }

    /**
     * The most efficient network iteration depends on its structure, so it's to be decided ont the run.
     *
     * @param settings
     * @param network
     * @param outputNeuron
     * @param backproper
     * @return
     */
    public static TopDown getTopDownPropagator(Settings settings, NeuralNetwork<State.Structure> network, Neuron outputNeuron, Backproper backproper) {
        if (network instanceof TopologicNetwork) {
            PureNeuronVisitor.Down down = new PureNeuronVisitor().new Down(backproper, network);
            new Topologic((TopologicNetwork<State.Structure>) network, backproper).new TDownVisitor(outputNeuron, down);
        } else {
            new DFSstack(network, backproper).new TDownVisitor(outputNeuron);
        }
        return null;
    }

    public void backpropagate(NeuralModel model, NeuralSample sample) {
        NeuralNetwork<State.Structure> network = sample.query.evidence;
        AtomNeuron<State.Computation> outputNeuron = sample.query.neuron;


    }

    public void backpropagate(NeuralModel model, NeuralNetwork network, Neuron outputNeuron, Result result) {
        Value gradient = result.gradient();
        outputNeuron.state.invalidate();
        outputNeuron.state.cumulate(backproper, gradient);

        TopDown topDownPropagator = getTopDownPropagator(settings, network, outputNeuron, backproper);
        topDownPropagator.topdown();
    }

    public void backpropagate(Neuron outputNeuron, Value gradient) {

    }

    public List<Value> getWeightUpdates(NeuralModel model, NeuralSample sample) {

    }
}
