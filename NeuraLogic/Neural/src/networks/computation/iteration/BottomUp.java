package networks.computation.iteration;

/**
 * Annotation interface to signify the top-down direction of ({@link DirectedIteration}).
 *
 * In the neural world:
 * Going from leaf nodes (i.e. {@link networks.structure.components.neurons.types.FactNeuron}) towards the outputNeuron.
 * Given the output neuron as a method input,  the corresponding recursive implementation cannot use tail-recursion,
 * it is a POST-ORDER iteration. Alternatively it may use topologic ordering.
 *
 * @see DirectedIteration
 * @see IterationStrategy
 */
public interface BottomUp<V> extends DirectedIteration {

    @Override
    default void iterate() {
        bottomUp();
    }

    V bottomUp();
}