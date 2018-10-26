package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * The most abstract class for performing some mode (Visitor/Iterator) of directed (TDown/BUp) iteration
 * with actions (stateVisitor) performed on neurons of a given network (network) starting/ending at outputNeuron.
 *
 * Directions are contracted by corresponding interfaces:
 * - TDown (starting from queryNeuron and going down, e.g. backpropagation)
 * - BottomUp (may also "start" from query neuron, but returns neuron to perform the action when going up (postorder), e.g. evaluation)
 *
 * Actions are carried by a StateVisitor object, subclassing to e.g.:
 * - Evaluator - updates values of corresponding states
 * - Backproper - updates gradients of corresponding states
 *
 * remarks: Iterators are cleaner design patterns, but performing actions via Visitors might be more efficient in TDown direction (when neurons have input links):
 * - it will save calling for the inputs of a neuron twice, since inputs() are needed to both update the state (propagate gradient) and expand to next neurons.
 * - in bottomup it can just return the next neuron (with a ready() state), and on that it would propagate the state info further ones
 */
public abstract class IterationStrategy<V> implements DirectedIteration {
    private static final Logger LOG = Logger.getLogger(IterationStrategy.class.getName());

    StateVisitor<V> stateVisitor;
    NeuralNetwork<State.Structure> network;
    Neuron<Neuron, State.Computation> outputNeuron;

    public IterationStrategy(StateVisitor<V> stateVisitor, NeuralNetwork<State.Structure> network, Neuron<Neuron, State.Computation> outputNeuron) {
        this.stateVisitor = stateVisitor;
        this.network = network;
        this.outputNeuron = outputNeuron;
    }
}