package networks.evaluation.iteration;

import networks.evaluation.values.Value;
import networks.structure.NeuralNetwork;
import networks.structure.Neuron;

public interface IterationStrategy<T extends NeuralNetwork, V extends Value> {

    public V visitNode(T network, Neuron neuron);
}