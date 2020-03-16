package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.transforming.CycleBreaking;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;

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
