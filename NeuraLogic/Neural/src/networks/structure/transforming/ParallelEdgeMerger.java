package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.types.AtomNeuron;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * todo if possible kill permutations of body literals here - return only truly unique ground rule bodies and merge corresponding weights appropriately
 *    - careful for conjunction weights - aggregate them only if not shared - same problem in Grounder merging different WeightedRules with same hornClauses
 *    https://docs.google.com/document/d/1k4wj62geSyC1sdB-ETsWCMk3nNpNR2bNlxbbdlFCI34/edit#heading=h.34cecqcfg6i1
 */
public class ParallelEdgeMerger implements NetworkReducing {
    private static final Logger LOG = Logger.getLogger(ParallelEdgeMerger.class.getName());

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, AtomNeuron<State.Neural> outputStart) {
        return null;
    }
}
