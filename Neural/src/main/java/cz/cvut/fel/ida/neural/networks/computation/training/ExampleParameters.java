package cz.cvut.fel.ida.neural.networks.computation.training;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.FactNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The learnable values a set of samples carries that the {@link NeuralModel} does not.
 * <p>
 * A value written on an *example* fact and made learnable is a real parameter - it trains - but its
 * {@link Weight} comes from a factory run after the model was built, so it is absent from
 * {@link NeuralModel#allWeights} and therefore from anything that saves or restores a model. It lives on the
 * built samples instead, which is what this reaches.
 * <p>
 * Keyed by the ground literal, because the alternatives do not survive a rebuild: the weight's *index*
 * continues a counter that keeps running, so building the same dataset twice on one model gives one set of
 * parameters the indices `1, 2` and the next `5, 6` - **measured** - and the weight's generated name is
 * `w` plus that index, so it moves with it. The literal is what the parameter is actually about.
 * <p>
 * Template facts are left out on purpose. Theirs is a model parameter, already in `allWeights` and already
 * saved; {@link FactNeuron#factLiteral} is set only on the example path, which is what separates the two.
 */
public class ExampleParameters {
    private static final Logger LOG = Logger.getLogger(ExampleParameters.class.getName());

    /**
     * Ground literal to the weight holding that fact's learnable value, over all the given samples.
     * <p>
     * Insertion-ordered, so the same dataset always reports its parameters in the same order. One weight can
     * appear under more than one literal - a *named* weight written into several examples is one shared
     * parameter, not one per example - and the caller is the one that has to care, so this does not
     * de-duplicate.
     */
    public static Map<String, Weight> of(Iterable<NeuralSample> samples) {
        Map<String, Weight> parameters = new LinkedHashMap<>();

        for (NeuralSample sample : samples) {
            if (sample == null || sample.query == null || !(sample.query.evidence instanceof TopologicNetwork)) {
                continue;
            }
            //raw, and through Object on the way to FactNeuron: allNeuronsTopologic is declared over
            //BaseNeuron<Neurons, State.Neural> while a FactNeuron is a WeightedNeuron<BaseNeuron,
            //States.SimpleValue>, so the two are unrelated to the compiler even though every element that
            //answers the instanceof is one. DetailedNetwork casts its way around the same thing.
            TopologicNetwork network = (TopologicNetwork) sample.query.evidence;

            for (Object item : network.allNeuronsTopologic) {
                if (!(item instanceof FactNeuron)) {
                    continue;
                }
                FactNeuron factNeuron = (FactNeuron) item;
                Weight offset = factNeuron.getOffset();

                if (factNeuron.factLiteral == null || offset == null || !offset.isLearnable()) {
                    continue;
                }
                Weight previous = parameters.put(factNeuron.factLiteral, offset);

                if (previous != null && previous != offset) {
                    //two different parameters claiming one literal would make the key meaningless, and the
                    //caller would silently restore whichever came last
                    LOG.severe("Two different learnable weights on one ground fact: " + factNeuron.factLiteral);
                }
            }
        }
        return parameters;
    }
}
