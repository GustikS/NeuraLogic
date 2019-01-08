package networks.computation.iteration;

/**
 * Annotation interface to signify the top-down direction of ({@link DirectedIteration})
 *
 * In the neural world:
 * Going from some given outputNeuron towards the leave nodes (i.e. {@link networks.structure.components.neurons.types.FactNeuron})
 * with no further inputs.
 *
 * @see DirectedIteration
 * @see IterationStrategy
 */
public interface TopDown extends DirectedIteration {

    @Override
    default void iterate(){
        topdown();
    }

    void topdown();
}