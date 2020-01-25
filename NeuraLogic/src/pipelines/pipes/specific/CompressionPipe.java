package pipelines.pipes.specific;

import networks.structure.building.NeuralProcessingSample;
import networks.structure.transforming.NetworkReducing;
import pipelines.Pipe;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 */
public class CompressionPipe extends Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(CompressionPipe.class.getName());

    NetworkReducing compressor;

    public CompressionPipe(Settings settings) {
        super("CompressionPipe", settings);
        compressor = NetworkReducing.getCompressor(settings);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        if (this.exporter != null)
            neuralProcessingSampleStream.onClose(() -> trueExport());   //We export after the stream finishes!
        return neuralProcessingSampleStream.map(net -> {
            net.query.evidence = compressor.reduce(net.detailedNetwork, net.query.neuron);
            return net;
        });
    }

    /**
     * Overriding export functionality - here we want to export stats, not the actual output networks
     *
     * @param outputReady
     * @param <T>
     */
    protected <T> void export(T outputReady) {
        //void
    }

    /**
     * We export after the stream finishes!
     */
    protected void trueExport() {
//        if (this.exporter == null && this.parent != null) {
//            this.exporter = Exporter.getFrom(this.ID, parent.settings);
//        }
        if (exporter != null) {
            compressor.finish();
            this.exporter.export(compressor);
        }
    }
}
