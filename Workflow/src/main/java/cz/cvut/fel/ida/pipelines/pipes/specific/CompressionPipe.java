package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.neural.networks.structure.transforming.NetworkReducing;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Utilities;
import cz.cvut.fel.ida.utils.math.collections.MultiList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.terminateSampleStream;

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
            List<NeuralProcessingSample> neuralProcessingSamples = terminateSampleStream(neuralProcessingSampleStream);
            DetailedNetwork detailedNetwork = neuralProcessingSamples.get(0).detailedNetwork;
            List<QueryNeuron> queryNeurons = terminateSampleStream(neuralProcessingSamples.stream().map(s -> s.query));
            NeuralNetwork reducedNetwork = compressor.reduce(detailedNetwork, queryNeurons);
            trueExport();
            return neuralProcessingSamples.stream().map(s -> {
                s.query.evidence = reducedNetwork;
                return s;
            });
        } else if (!settings.oneQueryPerExample) {
            List<NeuralProcessingSample> processingSamples = Utilities.terminateSampleStream(neuralProcessingSampleStream);
            List<NeuralProcessingSample> allProcessingSamples = new LinkedList<>();
            MultiList<DetailedNetwork, NeuralProcessingSample> sampleMap = new MultiList<>();
            for (NeuralProcessingSample processingSample : processingSamples) {  // merge samples with the same example
                sampleMap.put(processingSample.detailedNetwork, processingSample);
            }
            for (Map.Entry<DetailedNetwork, List<NeuralProcessingSample>> entry : sampleMap.entrySet()) {
                DetailedNetwork detailedNetwork = entry.getKey();
                List<NeuralProcessingSample> samples = entry.getValue();
                List<QueryNeuron> queryNeurons = samples.stream().map(s -> s.query).collect(Collectors.toList());
                NeuralNetwork reducedNetwork = compressor.reduce(detailedNetwork, queryNeurons);
                for (NeuralProcessingSample sample : samples) {
                    sample.query.evidence = reducedNetwork;
                }
                allProcessingSamples.addAll(samples);
            }
            trueExport();
            return allProcessingSamples.stream();
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
        LOG.info("Compression stats export");
        if (exporter != null) {
            compressor.finish();
            this.exporter.export(compressor);
        }
    }
}
