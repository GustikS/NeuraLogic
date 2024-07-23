package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Utilities;
import cz.cvut.fel.ida.utils.math.collections.MultiList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SameQueryAggregationPipe extends Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(SameQueryAggregationPipe.class.getName());

    Aggregation aggregation;

    public SameQueryAggregationPipe(Settings settings) {
        super("QueryFilteringPipe", settings);
        aggregation = Aggregation.getFunction(settings.factMergeActivation);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        if (settings.oneQueryPerExample) {  // this only applies if multiple queries per example are detected
            return neuralProcessingSampleStream;
        }

        List<NeuralProcessingSample> processingSamples = Utilities.terminateSampleStream(neuralProcessingSampleStream);
        List<NeuralProcessingSample> outputProcessingSamples = new LinkedList<>();
        MultiList<DetailedNetwork, NeuralProcessingSample> sampleMap = new MultiList<>();
        for (NeuralProcessingSample processingSample : processingSamples) {  // merge samples with the same example
            sampleMap.put(processingSample.detailedNetwork, processingSample);
        }
        for (Map.Entry<DetailedNetwork, List<NeuralProcessingSample>> entry : sampleMap.entrySet()) {
            List<NeuralProcessingSample> samples = entry.getValue();
            MultiList<AtomNeurons, NeuralProcessingSample> singleExampleMap = new MultiList<>();
            for (NeuralProcessingSample sample : samples) {
                AtomNeurons neuron = sample.query.neuron;
                if (neuron == null) {
                    LOG.info("Samples without query neurons encountered during SameQueryAggregation");
                    neuron = new AtomNeuron(sample.query.ID, sample.query.position, null);
                }
                singleExampleMap.put(neuron, sample);
            }
            for (Map.Entry<AtomNeurons, List<NeuralProcessingSample>> atomNeuronsListEntry : singleExampleMap.entrySet()) {
                outputProcessingSamples.add(mergeSamples(atomNeuronsListEntry.getValue()));
            }
        }
        return outputProcessingSamples.stream();
    }

    private NeuralProcessingSample mergeSamples(List<NeuralProcessingSample> sameQuerySamples) {
        final List<Value> values = sameQuerySamples.stream().map(s -> s.target).collect(Collectors.toList());
        Value merge = aggregation.evaluate(values);
        NeuralProcessingSample result = sameQuerySamples.get(0);
        result.target = merge;
        return result;
    }
}