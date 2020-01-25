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
        super("NetworkPruningPipe", settings);
        reducer = NetworkReducing.getReducer(settings);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        if (this.exporter != null)
            neuralProcessingSampleStream.onClose(() -> trueExport());   //We export after the stream finishes!
        return neuralProcessingSampleStream.map(net -> {
            net.query.evidence = reducer.reduce(net.detailedNetwork, net.query.neuron);
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
            reducer.finish();
            this.exporter.export(reducer);
        }
    }

}
