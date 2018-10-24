package networks.computation.iteration;

import networks.computation.training.evaluation.values.Value;
import networks.structure.metadata.states.State;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;

import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Logger;

//todo this class is not used BFS would only be used for true bottom up evaluation as it cannot do post-order evaluation like DFS
public class BFS<T extends Neuron, S extends State.Computation> extends NeuronIterating<NeuralNetwork<State.Structure>, T, S> {
    private static final Logger LOG = Logger.getLogger(BFS.class.getName());

    Queue<Neuron<T, S>> queue;

    public BFS(StateVisitor<Value> stateVisitor) {
        super(stateVisitor);
        queue = super.neuronLinkedList;
    }

    public S topDown(Neuron<T, S> neuron, NeuralNetwork<State.Structure> network) {
        while (!queue.isEmpty()) {
            Neuron<T, S> poll = queue.poll();
            if (neuronVisitor.ready(poll.state)) {
                Iterator<T> inputs = network.getInputs(poll);
                T next;
                while ((next = inputs.next()) != null) {
                    queue.add(next);
                }
            }
        }
        return null;
    }
}