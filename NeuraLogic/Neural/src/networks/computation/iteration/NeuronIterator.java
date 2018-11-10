package networks.computation.iteration;

import networks.structure.components.neurons.Neuron;
import networks.structure.metadata.states.State;

import java.util.Iterator;


/**
 * For "Passive" Iteration strategy - no action taken, just returning next Neuron (for processing, e.g. activation and expansion). (PureIterator)
 * @return
 */
public interface NeuronIterator extends Iterator<Neuron<Neuron, State.Neural>>{

    @Override
    Neuron<Neuron, State.Neural> next();

    @Override
    boolean hasNext();

}