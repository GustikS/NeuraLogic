package networks.computation.iteration;

public interface DirectedIteration {

    /**
     * Active "Propagator" version - takes care of neighbours expansion and ALSO propagation of Values at the same time.
     * In visitor this can be more efficient than just returning next neuron for processing in iterator, which has to repeat the neighbour exploration.
     */
    void iterate();
}
