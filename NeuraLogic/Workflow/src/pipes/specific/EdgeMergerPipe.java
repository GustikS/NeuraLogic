package pipes.specific;

import networks.structure.building.NeuralProcessingSample;
import networks.structure.transforming.NetworkReducing;
import pipelines.Pipe;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * todo must implement the iso-value compression at least
 */
public class EdgeMergerPipe extends Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(EdgeMergerPipe.class.getName());
    private final Settings settings;
    NetworkReducing merger;

    public EdgeMergerPipe(Settings settings) {
        super("EdgeMergerPipe");
        this.settings = settings;
        merger = NetworkReducing.getEdgeMerger(settings);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        return neuralProcessingSampleStream.map(net -> {
            net.query.evidence = merger.reduce(net.detailedNetwork, net.query);
            return net;
        });
    }
}
