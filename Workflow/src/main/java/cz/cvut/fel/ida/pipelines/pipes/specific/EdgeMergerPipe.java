package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.transforming.NetworkReducing;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;

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
