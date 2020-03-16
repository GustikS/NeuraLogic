package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.export.NeuralSerializer;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralSerializerPipe extends Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Pair<List<NeuralSerializer.SerializedWeight>, Stream<NeuralSerializer.SerializedSample>>> {
    private static final Logger LOG = Logger.getLogger(NeuralSerializerPipe.class.getName());

    NeuralSerializer neuralSerializer = new NeuralSerializer();

    public NeuralSerializerPipe() {
        super("NetworkSerializerPipe");
    }

    @Override
    public Pair<List<NeuralSerializer.SerializedWeight>, Stream<NeuralSerializer.SerializedSample>> apply(Pair<NeuralModel, Stream<NeuralSample>> neuralModelStreamPair) {
        NeuralModel model = neuralModelStreamPair.r;
        Stream<NeuralSample> samples = neuralModelStreamPair.s;
        List<NeuralSerializer.SerializedWeight> serializedWeightList = neuralSerializer.serialize(model);

        Stream<NeuralSerializer.SerializedSample> serializedSampleStream = samples.map(neuralSerializer::serialize);
        return new Pair<>(serializedWeightList, serializedSampleStream);

    }
}
