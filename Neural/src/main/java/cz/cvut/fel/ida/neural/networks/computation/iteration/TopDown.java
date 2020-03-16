package cz.cvut.fel.ida.neural.networks.computation.iteration;

import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;

/**
 * Annotation interface to signify the top-down direction of ({@link DirectedIteration})
 *
 * In the neural world:
 * Going from some given outputNeuron towards the leave nodes (i.e. {@link cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.FactNeuron})
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

    /**
     * Return the result of this top-down iteration, typically the list of updates found on the way ({@link WeightUpdater})
     * @return
     */
    void topdown();
}