package networks.computation.iteration;

/**
 * Going from leaf nodes (FactNeurons) towards the outputNeuron. Given the output neuron as a method input,
 * the corresponding recursive implementation cannot use tail-recursion! It is a POST-ORDER iteration.
 */
public interface BottomUp<V> extends DirectedIteration {

    @Override
    default void iterate() {
        bottomUp();
    }

    V bottomUp();
}