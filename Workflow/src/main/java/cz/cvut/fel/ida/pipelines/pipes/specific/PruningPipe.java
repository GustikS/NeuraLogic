package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.utils.generic.Utilities;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.neural.networks.structure.transforming.NetworkReducing;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * todo remove unnecessary edges, bot long chains and merging of unnecessary parallel edges
 */
public class PruningPipe extends Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(PruningPipe.class.getName());

    NetworkReducing reducer;

    public PruningPipe(Settings settings) {
        super("NetworkPruningPipe", settings);
        reducer = NetworkReducing.getReducer(settings);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        if (settings.groundingMode == Settings.GroundingMode.GLOBAL) {
            List<NeuralProcessingSample> neuralProcessingSamples = Utilities.terminateSampleStream(neuralProcessingSampleStream);
            DetailedNetwork detailedNetwork = neuralProcessingSamples.get(0).detailedNetwork;
            List<QueryNeuron> queryNeurons = neuralProcessingSamples.stream().map(s -> s.query).collect(Collectors.toList());
            NeuralNetwork reducedNetwork = reducer.reduce(detailedNetwork, queryNeurons);
            trueExport();
            return neuralProcessingSamples.stream().map(s -> {
                s.query.evidence = reducedNetwork;
                return s;
            });
        }

        if (this.exporter != null)
            neuralProcessingSampleStream.onClose(() -> trueExport());   //We export after the stream finishes!

        return neuralProcessingSampleStream.map(sample -> {
            if (!sample.detailedNetwork.pruned) {
                sample.query.evidence = reducer.reduce(sample.detailedNetwork, sample.query);
                sample.detailedNetwork.pruned = true;
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
        LOG.info("Pruning stats export");
        if (exporter != null) {
            reducer.finish();
            this.exporter.export(reducer);
        }
    }

}
