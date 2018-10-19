package networks.computation.iteration.actions;

import networks.structure.neurons.Neurons;

/**
 * @param <V>
 */
public abstract class NeuronVisitor<V> {

    public StateVisitor<V> stateVisitor;

    public V visit(Neurons neuron) {
        if (ready4activation(neuron)) {
            V activation = activate(neuron);
            expand(neuron);
            return activation;
        }
        return null;
    }

    /**
     * This one is for sure
     * @param neuron
     * @return
     */
    public abstract boolean ready4activation(Neurons neuron);



    public abstract V activate(Neurons neuron);

    public abstract void expand(Neurons neuron);

    public abstract void expand(Neurons from, Neurons to);

}