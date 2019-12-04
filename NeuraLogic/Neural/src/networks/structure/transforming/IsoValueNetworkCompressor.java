package networks.structure.transforming;

import networks.computation.evaluation.values.Value;
import networks.computation.evaluation.values.distributions.ValueInitializer;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.iteration.actions.IndependentNeuronProcessing;
import networks.computation.iteration.visitors.states.neurons.Invalidator;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.neurons.types.AtomNeurons;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by gusta on 14.3.17.
 */
public class IsoValueNetworkCompressor implements NetworkReducing, NetworkMerging {
    private static final Logger LOG = Logger.getLogger(IsoValueNetworkCompressor.class.getName());

    private final IndependentNeuronProcessing invalidation;
    private final Evaluation evaluation;
    final Settings settings;
    private ValueInitializer valueInitializer;
    public int precision;

    public IsoValueNetworkCompressor(Settings settings) {
        this.settings = settings;
        this.valueInitializer = ValueInitializer.getInitializer(settings);
        this.invalidation = new IndependentNeuronProcessing(settings, new Invalidator(-1));
        this.evaluation = new Evaluation(settings, -1);
        this.precision = settings.isoValuePrecision;
    }

    @Override
    public NeuralNetwork merge(NeuralNetwork a, NeuralNetwork b) {
        return null;
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, AtomNeurons<State.Neural> outputStart) {
        Map<Neurons, ValueList> isoValues = new LinkedHashMap<>();
        List<Weight> allWeights = inet.getAllWeights();
        QueryNeuron queryNeuron = new QueryNeuron("", -1, 1.0, outputStart, inet);

        int sizeBefore = inet.allNeuronsTopologic.size();

        isoIteration(inet, allWeights, queryNeuron, isoValues);
        Map<Neurons, Neurons> isoNeurons = mergeNeurons(inet, isoValues);
        LinkedHashSet<Neurons> etalons = new LinkedHashSet<>(isoNeurons.values());

        //lastly remove all the dead (pruned) neurons by building a new topologic sort starting from output neuron
        NetworkReducing.supervisedNetPruning(inet, (BaseNeuron) outputStart);

        LOG.info("IsoValue neuron compression from " + sizeBefore + " down to " + etalons.size() + " (topologic-check: " + inet.allNeuronsTopologic.size() + ")");
        if (etalons.size() != inet.allNeuronsTopologic.size()){
            LOG.warning("Some inconsistencies appeared during iso-value compression of neurons! (size of unique values != size of topologic reconstruction)");
        }
        return inet;
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

        for (BaseNeuron<Neurons, State.Neural> neuron : inet.allNeuronsTopologic) { // over all neurons
            Neurons etalonReplacement = etalonMap.get(neuron);
            Iterator<Neurons> outputs = inet.getOutputs(neuron);
            if (outputs == null) {
                continue;
            }
            while (outputs.hasNext()) {   // over all its outputs
                Neurons output = outputs.next();
                inet.replaceInput((BaseNeuron<Neurons, State.Neural>) output, neuron, etalonReplacement);
                inet.outputMapping.remove(neuron);  //just to make sure
            }
        }
        return etalonMap;
    }

    private void isoIteration(DetailedNetwork<State.Structure> inet, List<Weight> allWeights, QueryNeuron queryNeuron, Map<Neurons, ValueList> isoValues) {
        for (int i = 0; i < precision; i++) {
            for (Weight weight : allWeights) {
                valueInitializer.initWeight(weight);
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
            length = IsoValueNetworkCompressor.this.precision;
            values = new Value[length];
        }

        public void add(Value value) {
            values[index++] = roundUp(value);
        }

        public Value roundUp(Value value) {
            Value clone = value.getForm();
            Iterator<Double> iterator = value.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Double next = iterator.next();
                BigDecimal bigDecimal = new BigDecimal(next).setScale(10, BigDecimal.ROUND_HALF_UP);    // 10 decimal digits are about max precision, 15 are already not deterministic mess!
                clone.set(i, bigDecimal.doubleValue());
                i++;
            }
            return clone;
        }

        @Override
        public int hashCode() {
            if (hashCode != -1) {
                return hashCode;
            } else {
                int hashCode = 1;
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