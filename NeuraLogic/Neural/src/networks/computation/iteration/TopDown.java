package networks.computation.iteration;

public interface TopDown extends DirectedIteration {

    @Override
    default void iterate(){
        topdown();
    }

    void topdown();
}