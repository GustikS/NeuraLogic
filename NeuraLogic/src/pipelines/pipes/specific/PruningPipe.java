package pipelines.pipes.specific;

import networks.structure.building.NeuralProcessingSample;
import networks.structure.transforming.NetworkReducing;
import pipelines.Pipe;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * todo remove unnecessary edges, bot long chains and merging of unnecessary parallel edges
 */
public class PruningPipe extends Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(PruningPipe.class.getName());

    NetworkReducing reducer;

    public PruningPipe(Settings settings) {
        super("NetworkPruningPipe");
        reducer = NetworkReducing.getReducer(settings);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        return neuralProcessingSampleStream.map(net -> {
            net.query.evidence = reducer.reduce(net.detailedNetwork, net.query.neuron);
            return net;
        });
    }

}
