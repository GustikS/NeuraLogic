package networks.computation.iteration;

import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;

import java.util.logging.Logger;

/**
 * No recursive expansion of nodes for processing, just an immediate propagation of values to neighbours (PureVisitor)
 */
public interface NeuronVisitor {

    /**
     * Default double dispatch, some of the specific Neuron classes should be passed as an argument instead!
     *
     * @param neuron
     */
    default void visit(Neurons neuron) {
        Logger LOG = Logger.getLogger(NeuronVisitor.class.getName());
        LOG.severe("Error: Visitor calling a default method through double dispatch.");
    }

    /**
     * Expand neighbours, Propagate result of this neuron into the neighbours (inputs or outputs).
     *
     * @param neuron
     */
    void visit(Neuron neuron);


    interface Weighted extends NeuronVisitor {

        /**
         * Expand neighbours and Propagate result of this neuron into the neighbours (inputs or outputs) with the corresponding edge weight.
         *
         * @param neuron
         */
        void visit(WeightedNeuron neuron);
    }
}