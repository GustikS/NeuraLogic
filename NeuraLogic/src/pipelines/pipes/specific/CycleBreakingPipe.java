package pipelines.pipes.specific;

import networks.structure.building.NeuralProcessingSample;
import networks.structure.transforming.CycleBreaking;
import pipelines.Pipe;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class CycleBreakingPipe extends Pipe<Stream<NeuralProcessingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(CycleBreakingPipe.class.getName());
    CycleBreaking breaker;

    public CycleBreakingPipe(Settings settings) {
        super("CycleBreakingPipe");
        breaker = CycleBreaking.getBreaker(settings);
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<NeuralProcessingSample> neuralProcessingSampleStream) {
        return neuralProcessingSampleStream.map(net -> {
            net.query.evidence = breaker.breakCycles(net.query.evidence);
            return net;
        });
    }
}
