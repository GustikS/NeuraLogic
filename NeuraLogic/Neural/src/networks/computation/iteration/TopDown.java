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
public interface TopDown<V> extends DirectedIteration {

    @Override
    default void iterate(){
        topdown();
    }

    /**
     * Return the result of this top-down iteration, typically the list of updates found on the way ({@link networks.computation.iteration.actions.WeightUpdater})
     * @return
     */
    V topdown();
}