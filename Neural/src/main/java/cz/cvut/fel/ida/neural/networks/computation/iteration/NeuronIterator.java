package cz.cvut.fel.ida.neural.networks.computation.iteration;

import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.Iterator;

/**
 * A specification interface for an {@link Iterator} pattern over {@link BaseNeuron neurons}.
 * <p>
 * remarks:
 * this is not Iteration strategy - no action taken, just returning next Neuron (for processing) (=PureIterator)
 *
 * @see NeuronIterating for the active iteration version
 * @see IterationStrategy
 */
public interface NeuronIterator<T extends Neurons, S extends State.Neural> extends Iterator<BaseNeuron<T, S>> {

    @Override
    BaseNeuron<T, S> next();

    @Override
    boolean hasNext();

}