package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.math.collections.MultiList;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static cz.cvut.fel.ida.pipelines.utils.WorkflowUtils.consecutiveGroupsIterator;

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

        Stream<List<NeuralProcessingSample>> groupStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(consecutiveGroupsIterator(neuralProcessingSampleStream.iterator(), a -> a.detailedNetwork), Spliterator.ORDERED), false);
        Stream<NeuralProcessingSample> flatStream = groupStream.flatMap(list -> {
            if (list.isEmpty()) {
                return Stream.empty();
            }

            List<NeuralProcessingSample> outputProcessingSamples = new LinkedList<>();
            MultiList<AtomNeurons, NeuralProcessingSample> singleExampleMap = new MultiList<>();
            for (NeuralProcessingSample sample : list) {
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

            return outputProcessingSamples.stream();
        });

        return flatStream;
    }

    private NeuralProcessingSample mergeSamples(List<NeuralProcessingSample> sameQuerySamples) {
        final List<Value> values = sameQuerySamples.stream().map(s -> s.target).collect(Collectors.toList());
        Value merge = aggregation.evaluate(values);
        NeuralProcessingSample result = sameQuerySamples.get(0);
        result.target = merge;
        return result;
    }
}