package cz.cvut.fel.ida.neural.networks.structure.transforming;

import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.IndependentNeuronProcessing;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Invalidator;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.Timing;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gusta on 14.3.17.
 */
public class IsoValueNetworkCompressor implements NetworkReducing, NetworkMerging {
    private static final Logger LOG = Logger.getLogger(IsoValueNetworkCompressor.class.getName());
    // todo next it eats too much memory with low iso (1-2)
    private transient final IndependentNeuronProcessing invalidation;
    private transient final Evaluation evaluation;
    private transient Settings settings;
    private transient ValueInitializer valueInitializer;

    public int repetitions;
    public int decimals;

    Timing timing;

    public int allNeuronCount = 0;
    public int compressedNeuronCount = 0;
    public int preventedByIsoCheck = 0;

    public IsoValueNetworkCompressor(Settings settings) {
        this.settings = settings;
        this.valueInitializer = ValueInitializer.getInitializer(settings);
        this.invalidation = new IndependentNeuronProcessing(settings, new Invalidator(-1));
        this.evaluation = new Evaluation(settings, -1);
        this.repetitions = settings.isoValueInits;
        this.decimals = settings.isoDecimals;
        this.timing = new Timing();
    }

    @Override
    public NeuralNetwork merge(NeuralNetwork a, NeuralNetwork b) {
        return null;
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, List<QueryNeuron> outputs) {
        timing.tic();

        if (inet.allNeuronsTopologic.isEmpty()){
            return inet;    // cannot compress an empty network
        }

        Map<Neurons, ValueList> isoValues = new LinkedHashMap<>();
        List<Weight> allWeights = inet.getAllWeights();

        Map<Weight, Value> originalValues = allWeights.stream().collect(Collectors.toMap(w -> w, w -> w.value.clone()));    //remember the original values of weights before reinits!

        QueryNeuron queryNeuron;
        if (outputs.size() > 1) {
            States.ComputationStateStandard dummyState = new States.ComputationStateStandard(null, Transformation.Singletons.identity);
            dummyState.setValue(Value.ZERO);
            AtomNeuron dummy = new AtomNeuron("dummy", -1, dummyState);
            queryNeuron = new QueryNeuron("", -1, 1.0, dummy, inet);
        } else {
            queryNeuron = new QueryNeuron("", -1, 1.0, outputs.get(0).neuron, inet);
        }

        int sizeBefore = inet.allNeuronsTopologic.size();

        isoIteration(inet, allWeights, queryNeuron, isoValues);
        Map<Neurons, Neurons> etalonMap = mergeNeurons(inet, isoValues);

        LinkedHashSet<Neurons> etalons = new LinkedHashSet<>(etalonMap.values());

        //lastly remove all the dead (pruned) neurons by building a new topologic sort starting from output neuron
        if (outputs.size() > 1) {
            List<Neurons> collect = outputs.stream().map(s -> s.neuron).collect(Collectors.toList());
            NetworkReducing.supervisedNetReconstruction(inet, collect);
        } else {
            NetworkReducing.supervisedNetReconstruction(inet, Collections.singletonList(outputs.get(0).neuron));
        }

        this.allNeuronCount += sizeBefore;
        this.compressedNeuronCount += inet.allNeuronsTopologic.size();
        LOG.fine(() -> "IsoValue neuron compression from " + sizeBefore + " down to " + inet.allNeuronsTopologic.size() + "(etalon values: " + etalons.size() + ")");
        reportVanishedIsoValues(inet, etalonMap, etalons);
        //Note this one compares the same two quantities the other way round, so it is open to the same
        //objection - it is left alone because it is guarded and says something narrower.
        if (!settings.structuralIsoCompression && etalons.size() < inet.allNeuronsTopologic.size() - 1) {
            LOG.warning("There are more neurons than iso-values (some neurons have not been pruned despite having the same value) - e.g. output neurons.");
//            for (Neurons neuron : inet.allNeuronsTopologic) {
//                if (!etalons.contains(neuron)){
//                    System.out.println(neuron);
//                }
//            }
        }

        allWeights.forEach(weight -> {
            weight.value = originalValues.get(weight);  //load back the original value (i.e. so that the iso compression does not change the network weights!)
        });

        timing.toc();
        return inet;
    }

    /**
     * An iso-value class can end up with no representative in the network, and there are two quite different
     * reasons for that. Telling them apart is the whole point here - the old check could not, and so reported
     * the harmless one as "lossy compression".
     * <p>
     * <b>The harmless one.</b> Every member of the class was unreachable from the query, so the
     * {@link NetworkReducing#supervisedNetReconstruction} above dropped the lot. Nothing the output depends on
     * has gone: this is dead-neuron removal, which is what that reconstruction is for. It is common on
     * templates that seed the Herbrand domain with facts no rule body then consumes - measured on the
     * recurrent reproducer, the two classes that vanished were both a single unread <code>exists(...)</code>
     * fact, and across the whole Java test suite it never happened at all.
     * <p>
     * <b>The real one.</b> The etalon is gone while some other member of its class survived. Merging rewires
     * the members' outputs onto the etalon, so a surviving member means the class is still in use - and its
     * representative being absent is then a genuine loss, not pruning. Nothing observed has ever hit this,
     * which is why it is worth saying loudly if it ever does.
     */
    private void reportVanishedIsoValues(DetailedNetwork<State.Structure> inet, Map<Neurons, Neurons> etalonMap, Set<Neurons> etalons) {
        if (!settings.debugAll && !settings.debugNeuralization) {
            return;     //off by default: this runs per sample on the grounding path, and telling the two
        }               //cases apart costs a set of the surviving neurons plus two scans over them

        if (etalons.size() <= inet.allNeuronsTopologic.size()) {
            return;     //the same int comparison the old check used, so nothing it caught is missed
        }

        Set<Neurons> surviving = new HashSet<>(inet.allNeuronsTopologic);

        Set<Neurons> vanished = new LinkedHashSet<>();
        for (Neurons etalon : etalons) {
            if (!surviving.contains(etalon)) {
                vanished.add(etalon);
            }
        }
        if (vanished.isEmpty()) {
            return;
        }

        Set<Neurons> stillInUse = new LinkedHashSet<>();
        for (Map.Entry<Neurons, Neurons> entry : etalonMap.entrySet()) {
            if (vanished.contains(entry.getValue()) && surviving.contains(entry.getKey())) {
                stillInUse.add(entry.getValue());
            }
        }

        if (!stillInUse.isEmpty()) {
            LOG.severe("Lossy compression: " + stillInUse.size() + " iso-value class(es) lost their etalon while "
                    + "neurons merged into it are still in the network, e.g. " + stillInUse.iterator().next());
        }
        int prunedAsDead = vanished.size() - stillInUse.size();
        if (prunedAsDead > 0) {
            LOG.fine(() -> prunedAsDead + " iso-value class(es) dropped with their neurons as unreachable from the query");
        }
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, QueryNeuron outputStart) {
        return reduce(inet, Arrays.asList(outputStart));
    }

    @Override
    public void finish() {
        timing.finish();
    }

    private Map<Neurons, Neurons> mergeNeurons(DetailedNetwork<State.Structure> inet, Map<Neurons, ValueList> isoValues) {
        Map<ValueList, List<Neurons>> isoNeurons = new HashMap<>();
        for (Map.Entry<Neurons, ValueList> neuronListEntry : isoValues.entrySet()) {
            Neurons neuron = neuronListEntry.getKey();
            ValueList values = neuronListEntry.getValue();
            List<Neurons> neurons = isoNeurons.computeIfAbsent(values, k -> new ArrayList<>());
            neurons.add(neuron);
        }
        //make a single etalon for each iso-class of neurons
        Map<Neurons, Neurons> etalonMap = new HashMap<>();
        for (Map.Entry<ValueList, List<Neurons>> entry : isoNeurons.entrySet()) {
            Neurons etalon = entry.getValue().get(0);
            for (Neurons neuron : entry.getValue()) {
                etalonMap.put(neuron, etalon);
            }
        }

        if (settings.structuralIsoCompression)
            oversafeCompression(inet, isoValues, isoNeurons, etalonMap);
        else
            unsafeCompression(inet, etalonMap);


        return etalonMap;
    }

    private void unsafeCompression(DetailedNetwork<State.Structure> inet, Map<Neurons, Neurons> etalonMap) {
        for (BaseNeuron<Neurons, State.Neural> neuron : inet.allNeuronsTopologic) { // over all neurons
            Neurons etalonReplacement = etalonMap.get(neuron);
            if (etalonReplacement == neuron) {
                continue;
            }
            Iterator<Neurons> outputs = inet.getOutputs(neuron);
            if (outputs == null) {
                continue;
            }
            while (outputs.hasNext()) {   // over all its outputs
                Neurons output = outputs.next();
                inet.replaceInput((BaseNeuron<Neurons, State.Neural>) output, neuron, etalonReplacement);

//                inet.replaceOutput((BaseNeuron<Neurons, State.Neural>) etalonReplacement,neuron,output);

                inet.outputMapping.remove(neuron);//just to make sure
            }
        }
    }

    private void oversafeCompression(DetailedNetwork<State.Structure> inet, Map<Neurons, ValueList> isoValues, Map<ValueList, List<Neurons>> isoNeurons, Map<Neurons, Neurons> etalonMap) {
        for (BaseNeuron<Neurons, State.Neural> neuron : inet.allNeuronsTopologic) { // over all neurons
            Neurons etalonReplacement = etalonMap.get(neuron);
            ValueList valueList = isoValues.get(neuron);
            List<Neurons> equivalentNeurons = isoNeurons.get(valueList);
            if (equivalentNeurons != null && equivalentNeurons.size() > 1)
                for (Neurons sameNeuron : equivalentNeurons) {
                    Iterator<Neurons> outputs = inet.getOutputs((BaseNeuron<Neurons, State.Neural>) sameNeuron);
                    if (outputs == null) {
                        continue;
                    }
                    if (!equivalent(inet, sameNeuron, etalonReplacement)) {
                        LOG.warning("Trying to replace a neuron with a structurally non-equivalent etalon!");   // this may also happen for completely functionally equivalent neurons, e.g. due to void aggregation
//                        LOG.info(((BaseNeuron<?, ?>) sameNeuron).index + " vs " + etalonReplacement.getIndex());
                        preventedByIsoCheck += 1;
                        continue;
                    }
                    while (outputs.hasNext()) {   // over all its outputs
                        Neurons output = outputs.next();
                        inet.replaceInput((BaseNeuron<Neurons, State.Neural>) output, sameNeuron, etalonReplacement);
                        inet.outputMapping.remove(sameNeuron);  //just to make sure
                    }
                }
            isoNeurons.remove(valueList);
        }
    }

    public boolean equivalent(DetailedNetwork<State.Structure> inet, Neurons<Neurons, State.Neural> a, Neurons<Neurons, State.Neural> b) {

        if (a.equals(b)) {
            return true;
        }

        if (!inet.getInputs(a).hasNext() && !inet.getInputs(b).hasNext()) {
            Value valA = a.getComputationView(-1).getValue();
            Value valB = b.getComputationView(-1).getValue();

            if (valA.equals(valB)) {
                return true;    //both neurons have same output value and no inputs -> they are necessarily equivalent
            } else {
                return false;
            }
        }

        if (a instanceof WeightedNeuron && b instanceof WeightedNeuron) {
            Pair<Iterator<Neurons>, Iterator<Weight>> inputsA = inet.getInputs((WeightedNeuron<Neurons, State.Neural>) a);
            List<Pair<Weight, Neurons>> inputListA = new ArrayList<>();
            while (inputsA.r.hasNext()) {
                Neurons neuronA = inputsA.r.next();
                Weight weightA = inputsA.s.next();
                inputListA.add(new Pair<>(weightA, neuronA));
            }
            Pair<Iterator<Neurons>, Iterator<Weight>> inputsB = inet.getInputs((WeightedNeuron<Neurons, State.Neural>) b);
            List<Pair<Weight, Neurons>> inputListB = new ArrayList<>();
            while (inputsB.r.hasNext()) {
                Neurons neuronB = inputsB.r.next();
                Weight weightB = inputsB.s.next();
                inputListB.add(new Pair<>(weightB, neuronB));
            }

            if (inputListA.size() != inputListB.size()) {
                return false;
            }
            for (Pair<Weight, Neurons> inputPair : inputListA) {
                boolean remove = inputListB.remove(inputPair);
                if (!remove) {
                    return false;
                }
            }
            return true;

        } else if (a instanceof WeightedNeuron || b instanceof WeightedNeuron) {
            return false;
        } else {
            Iterator<Neurons> inputsA = inet.getInputs(a);
            List<Neurons> inputListA = new ArrayList<>();
            inputsA.forEachRemaining(inputListA::add);

            Iterator<Neurons> inputsB = inet.getInputs(a);
            List<Neurons> inputListB = new ArrayList<>();
            inputsB.forEachRemaining(inputListB::add);

            if (inputListA.size() != inputListB.size()) {
                return false;
            }
            for (Neurons neuronA : inputListA) {
                boolean remove = inputListB.remove(neuronA);
                if (!remove) {
                    return false;
                }
            }
            return true;
        }
    }


    private void isoIteration(DetailedNetwork<State.Structure> inet, List<Weight> allWeights, QueryNeuron queryNeuron, Map<Neurons, ValueList> isoValues) {
        for (int i = 0; i < repetitions; i++) {
            for (Weight weight : allWeights) {  //todo next it would be much more efficient to do the re-initialization jointly over all the networks! (which would require the template as input)
                weight.init(valueInitializer);
            }
            inet.initializeStatesCache(-1);    //here we can transfer information from Structure to Computation
            invalidation.process(inet, queryNeuron.neuron);
            evaluation.evaluate(queryNeuron);

            for (BaseNeuron<Neurons, State.Neural> neuron : inet.allNeuronsTopologic) {
                Value value = neuron.getComputationView(-1).getValue();
                ValueList values = isoValues.computeIfAbsent(neuron, k -> new ValueList());
                values.add(value);
            }
        }
    }

    private class ValueList {

        int length;
        Value[] values;
        int index = 0;

        int hashCode = -1;


        public ValueList() {
            length = IsoValueNetworkCompressor.this.repetitions;
            values = new Value[length];
        }

        public void add(Value value) {
            values[index++] = roundUp(value);
        }

        public Value roundUp(Value value) {
            Value clone = value.getForm();
            double[]  values = value.getAsArray();

            for (int i = 0; i < values.length; i++) {
                final double rounded = new BigDecimal(values[i]).setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
                clone.set(i, rounded);
            }
            return clone;
        }

        @Override
        public int hashCode() {
            if (hashCode != -1) {
                return hashCode;
            } else {
                hashCode = 1;
                for (int i = 0; i < values.length; i++)
                    hashCode = 31 * hashCode + values[i].hashCode();
                return hashCode;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ValueList) {
                ValueList valueList = (ValueList) obj;
                for (int i = 0; i < length; i++) {
                    if (!values[i].equals(valueList.values[i])) {
                        return false;
                    }
                }
            } else {
                return false;
            }
            return true;
        }
    }
}