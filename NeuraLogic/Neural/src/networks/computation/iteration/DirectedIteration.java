package networks.computation.iteration;

import networks.structure.components.NeuralNetwork;

/**
 * Highest level interface representing all iteration strategies {@link IterationStrategy} that traverse structures, e.g.
 * neural nets ({@link NeuralNetwork}) in a directed fashion ({@link BottomUp} / {@link TopDown})
 * to possibly perform some actions on the individual elements, e.g. neurons ({@link networks.structure.components.neurons.Neuron})
 */
public interface DirectedIteration {

    /**
     * Overall (active) iteration - iterates AND performs all the actions.
     */
    void iterate();
}
