package cz.cvut.fel.ida.neural.networks.structure.transforming;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.setup.Settings;

import java.util.*;
import java.util.logging.Logger;

/**
 * if possible kill permutations of body literals here - return only truly unique ground rule bodies and merge corresponding weights appropriately
 * - careful for conjunction weights - aggregate them only if not shared - same problem in Grounder merging different WeightedRules with same hornClauses
 * https://docs.google.com/document/d/1k4wj62geSyC1sdB-ETsWCMk3nNpNR2bNlxbbdlFCI34/edit#heading=h.34cecqcfg6i1
 * <p>
 * after investigation - pretty much all the weights in LRNNs are shared, possibly in complex patterns, so this is not possible/efficient
 * - the only good thing is to prune unweighted inputs, e.g. multiple identical neurons going into AVG or MAX aggregations...
 */
public class ParallelEdgeMerger implements NetworkReducing {    //todo repair - this corrupts gradient computation
    private static final Logger LOG = Logger.getLogger(ParallelEdgeMerger.class.getName());
    private Settings settings;

    public ParallelEdgeMerger(Settings settings) {
        this.settings = settings;
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, QueryNeuron outputStart) {
        MergingVisitor mergingVisitor = new MergingVisitor(inet);
        Topologic.TDownVisitor bUpVisitor = new Topologic(inet).new TDownVisitor(outputStart.neuron, mergingVisitor);
        bUpVisitor.topdown();
        return inet;
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, List<QueryNeuron> outputStart) {
        return null;
    }

    @Override
    public void finish() {

    }

    class MergingVisitor extends NeuronVisitor.Weighted {

        public MergingVisitor(NeuralNetwork<State.Structure> network) {
            super(network, null, null);
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            //merging inputs of unweighted neurons, i.e. neurons with unit input weights, would require to change them into
            //weighted neurons, which would cause more overhead than reduction, so we either just prune them or skip them completely

            if (settings.removeIdenticalUnweightedInputs) {
                if (!neuron.getCombination().isInputSymmetric()) {
                    return;
                }
                Set<Neurons> inputNeurons = new HashSet<>();
                Iterator<T> inputs = network.getInputs(neuron);
                while (inputs.hasNext()) {
                    T next = inputs.next();
                    if (inputNeurons.contains(next)) {
                        inputs.remove();
                    } else {
                        inputNeurons.add(next);
                    }
                }
            }
        }

        /**
         * In LRNNs, pretty much all the weights are going to be shared somewhere after grounding (amongst the rule neurons)
         * and for shared weights, the merging is impossible (or extremely problematic).
         *
         * This is only possible for fixed weights.
         *
         * @param neuron
         * @param <T>
         * @param <S>
         */
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            if (!settings.mergeIdenticalWeightedInputs){
                return;
            }
            if (!neuron.getCombination().isInputSymmetric()) {
                return;
            }
            Pair<Iterator<T>, Iterator<Weight>> pair = network.getInputs(neuron);
            Iterator<T> inputNeurons = pair.r;
            Iterator<Weight> inputWeights = pair.s;

            LinkedHashMap<T, List<Weight>> allInputs = new LinkedHashMap<>();

            while (inputNeurons.hasNext()) {
                T next = inputNeurons.next();
                Weight nextWeight = inputWeights.next();
                if (nextWeight.isLearnable()) {
                    return; // learnable (shared) weights cannot be merged by any means...
                }
                List<Weight> acumWeight = allInputs.getOrDefault(next, new ArrayList<>());
                acumWeight.add(nextWeight);
                allInputs.put(next, acumWeight);
            }

            Set<Neurons> visited = new HashSet<>();
            pair = network.getInputs(neuron);
            inputNeurons = pair.r;
            inputWeights = pair.s;

            while (inputNeurons.hasNext()) {
                T next = inputNeurons.next();
                Weight nextWeight = inputWeights.next();
                if (visited.contains(next)) {   //we have seen this input before, remove it completely
                    inputNeurons.remove();
                    inputWeights.remove();
                } else {    // merge all the weights corresponding to this input
                    Weight finalWeight = mergeWeights(allInputs.get(next));
                    if (finalWeight == null) {
                        continue;   // no merging
                    }
                    if (finalWeight.equals(nextWeight)) {
                        continue;
                    }
                    ((DetailedNetwork) network).replaceInputWeight(neuron, next, finalWeight);
                    visited.add(next);
                }
            }
        }

        /**
         * It is only possible to merge fixed/constant weights!
         * @param weightList
         * @return
         */
        private Weight mergeWeights(List<Weight> weightList) { //todo test this
            if (weightList.size() == 1) {
                return weightList.get(0);   // no merging
            }

            Value sum = null;
            StringBuilder sb = new StringBuilder("MERGED:");

            for (Weight weight : weightList) {
                if (weight.isShared) {
                    return null;    //we rather do not touch sets with explicitly shared weights
                }

                if (weight.isLearnable()) {
                    return null;
                }

                sb.append("_").append(weight.name);

                if (sum == null) {
                    if (weight.value == Value.ONE) {
                        sum = new ScalarValue(1);
                    } else {
                        sum = weight.value.clone();
                    }
                } else {
                    sum.incrementBy(weight.value);
                }
            }

            //we skip the weightFactory since this is just going to be dead fixed weights
            Weight finalWeight = new Weight(-2, sb.toString(), sum, true, true);
//            finalWeight.metadata.addValidateMetadatum("initValue", sum);

            return finalWeight;
        }
    }
}
