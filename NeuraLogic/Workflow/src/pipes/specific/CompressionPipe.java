package pipes.specific;

import networks.structure.building.NeuralProcessingSample;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.transforming.NetworkReducing;
import pipelines.Pipe;
import settings.Settings;
import utils.Utilities;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

        if (settings.groundingMode == Settings.GroundingMode.GLOBAL) {
            List<NeuralProcessingSample> neuralProcessingSamples = Utilities.terminateSampleStream(neuralProcessingSampleStream);
            DetailedNetwork detailedNetwork = neuralProcessingSamples.get(0).detailedNetwork;
            List<QueryNeuron> queryNeurons = neuralProcessingSamples.stream().map(s -> s.query).collect(Collectors.toList());
            NeuralNetwork reducedNetwork = compressor.reduce(detailedNetwork, queryNeurons);
            trueExport();
            return neuralProcessingSamples.stream().map(s -> {
                s.query.evidence = reducedNetwork;
                return s;
            });
        }

        if (this.exporter != null)
            neuralProcessingSampleStream.onClose(() -> trueExport());   //We export after the stream finishes!

        return neuralProcessingSampleStream.map(sample -> {
            if (!sample.detailedNetwork.compressed) {  // skip if the same network has already been compressed!
                sample.query.evidence = compressor.reduce(sample.detailedNetwork, sample.query);
                sample.detailedNetwork.compressed = true;
            }

            return sample;
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
