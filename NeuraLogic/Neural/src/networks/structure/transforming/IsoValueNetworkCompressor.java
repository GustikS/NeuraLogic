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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 14.3.17.
 */
public class IsoValueNetworkCompressor implements NetworkReducing, NetworkMerging {
    private static final Logger LOG = Logger.getLogger(IsoValueNetworkCompressor.class.getName());

    private final IndependentNeuronProcessing invalidation;
    private final Evaluation evaluation;
    private final Settings settings;
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
        Map<Neurons, List<Value>> isoValues = new HashMap<>();
        List<Weight> allWeights = inet.getAllWeights();
        QueryNeuron queryNeuron = new QueryNeuron("", -1, 1.0, outputStart, inet);

        isoIteration(inet, allWeights, queryNeuron, isoValues);
        Map<List<Value>, List<Neurons>> isoNeurons = mergeNeurons(inet, isoValues);
        LOG.info("IsoValue neuron compression from " + inet.allNeuronsTopologic.size() + " down to " + isoNeurons.size());
        settings.exporter.tmpLine(inet.allNeuronsTopologic.size() + "," + isoNeurons.size());
        return inet;
    }

    private Map<List<Value>, List<Neurons>> mergeNeurons(DetailedNetwork<State.Structure> inet, Map<Neurons, List<Value>> isoValues) {
        Map<List<Value>, List<Neurons>> isoNeurons = new HashMap<>();
        for (Map.Entry<Neurons, List<Value>> neuronListEntry : isoValues.entrySet()) {
            Neurons neuron = neuronListEntry.getKey();
            List<Value> values = neuronListEntry.getValue();
            List<Neurons> neurons = isoNeurons.computeIfAbsent(values, k -> new ArrayList<>());
            neurons.add(neuron);
        }
        //todo next merge them actually
        return isoNeurons;
    }

    private void isoIteration(DetailedNetwork<State.Structure> inet, List<Weight> allWeights, QueryNeuron queryNeuron, Map<Neurons, List<Value>> isoValues) {
        for (int i = 0; i < precision; i++) {
            for (Weight weight : allWeights) {
                valueInitializer.initWeight(weight);
            }
            inet.initializeStatesCache(-1);    //here we can transfer information from Structure to Computation
            invalidation.process(inet, queryNeuron.neuron);
            evaluation.evaluate(queryNeuron);

            for (BaseNeuron<Neurons, State.Neural> neuron : inet.allNeuronsTopologic) {
                Value value = neuron.getComputationView(-1).getValue();
                List<Value> values = isoValues.computeIfAbsent(neuron, k -> new ArrayList<>());
                values.add(value);
            }
        }
    }
}