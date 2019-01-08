package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * The most abstract class for performing some mode (Visitor/Iterator) of directed ( {@link TopDown} / {@link BottomUp})
 * iteration ({@link DirectedIteration}) over {@link NeuralNetwork}, with actions ({@link NeuronVisitor})
 * performed on neurons of a given network {@link #network} starting/ending at {@link #outputNeuron}.
 *
 * Directions of iteration are contracted by corresponding interfaces (subclassing {@link DirectedIteration}):
 * - BottomUp {@link BottomUp} (may also "start" from query neuron, but returns neurons to perform the action when going up (postorder), e.g. {@link networks.computation.evaluation.Evaluation})
 * - TDown {@link TopDown} (starting from queryNeuron and going down, the natural direction when storing references to inputs, e.g. {@link networks.computation.training.Backpropagation})
 *
 * Iteration strategy can generally follow either Iterator or Visitor design patterns:
 * - some {@link NeuronIterating iterator} with actions carried by some {@link NeuronVisitor}
 *     - e.g. {@link networks.computation.iteration.Topologic.BUpIterator}
 * - some {@link NeuronVisiting visitor} with actions carried by some {@link NeuronVisitor}
 *     - e.g. {@link networks.computation.iteration.Topologic.TDownVisitor}
 *
 * During the iteration, we are visiting individual elements (e.g. {@link Neuron}) with their neighbors and performing
 * actions the logic of which is carried by various {@link NeuronVisitor NeuronVisitors}
 *     - e.g. {@link PureNeuronVisitor}
 *
 * Each neuron-visitor operates on the local structure of the network (neuron + neighbors), and it further carries
 * a {@link StateVisiting state-visitor} which then performs actions solely on the individual neuron level by updating
 * its internal {@link State state}, which may carry information such as value, gradient, dropout, and other intermediate results.
 * Exammple state visitors are:
 * - {@link networks.computation.iteration.actions.Evaluator} - updates values of corresponding states
 * - {@link networks.computation.iteration.actions.Backproper} - updates gradients of corresponding states
 *
 * In the case of neural computation (e.g. eval or backprop), each neuron's {@link networks.structure.metadata.states.State.Neural.Computation computation-state}
 * further caries {@link networks.structure.metadata.states.AggregationState aggregation-state} specifically designed for
 * accumulation of information for each {@link networks.computation.evaluation.functions.Aggregation activation function}.
 *
 * remarks: Iterators are cleaner design patterns, but performing actions via Visitors might be more efficient in TDown direction (when neurons have input links):
 * - it will save calling for the inputs of a neuron twice, since inputs() are needed to both update the state (propagate gradient) and expand to next neurons.
 * - in bottomup it can just return the next neuron (with a ready state), and on that it will simply propagate the state info further neurons
 */
public abstract class IterationStrategy<V> implements DirectedIteration {
    private static final Logger LOG = Logger.getLogger(IterationStrategy.class.getName());

    StateVisiting<V> stateVisitor;
    NeuralNetwork<State.Neural.Structure> network;
    Neuron<Neuron, State.Neural> outputNeuron;

    public IterationStrategy(StateVisiting<V> stateVisitor, NeuralNetwork<State.Neural.Structure> network, Neuron<Neuron, State.Neural> outputNeuron) {
        this.stateVisitor = stateVisitor;
        this.network = network;
        this.outputNeuron = outputNeuron;
    }
}