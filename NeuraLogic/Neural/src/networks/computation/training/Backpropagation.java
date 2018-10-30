package networks.computation.training;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.DFSstack;
import networks.computation.iteration.PureNeuronVisitor;
import networks.computation.iteration.TopDown;
import networks.computation.iteration.Topologic;
import networks.computation.iteration.actions.Backproper;
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

    /**
     * Backproper is a StateVisitor, takes care of the action taken during iteration
     */
    Backproper backproper;

    public Backpropagation(Settings settings) {
        this.settings = settings;
    }

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

        TopDown topDownPropagator = getTopDownPropagator(settings, network, outputNeuron, backproper);
        topDownPropagator.topdown();
    }
}
