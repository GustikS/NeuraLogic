package pipelines.pipes.specific;

import networks.structure.building.NeuralProcessingSample;
import networks.structure.transforming.NetworkReducing;
import pipelines.Pipe;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * todo must implement the iso-value compression at least
 */
public class CompressionPipe extends Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(CompressionPipe.class.getName());
    private final Settings settings;
    NetworkReducing compressor;

    public CompressionPipe(Settings settings) {
        super("IsoValueCompressionPipe");
        this.settings = settings;
        compressor = NetworkReducing.getCompressor(settings);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        return neuralProcessingSampleStream.map(net -> {
            net.query.evidence = compressor.reduce(net.query.evidence);
            return net;
        });
    }
}
