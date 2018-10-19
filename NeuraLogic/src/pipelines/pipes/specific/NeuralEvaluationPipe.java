package pipelines.pipes.specific;

import ida.utils.tuples.Pair;
import networks.computation.iteration.actions.Evaluator;
import networks.computation.results.Results;
import pipelines.Pipe;
import settings.Settings;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralEvaluationPipe extends Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Results> {
    private static final Logger LOG = Logger.getLogger(NeuralEvaluationPipe.class.getName());
    Settings settings;

    public NeuralEvaluationPipe(Settings settings){
        super("NeuralEvaluationPipe");
        this.settings = settings;
    }

    protected NeuralEvaluationPipe(String id) {
        super(id);
    }

    @Override
    public Results apply(Pair<NeuralModel, Stream<NeuralSample>> neuralModelStreamPair) {
        Evaluator evaluator = new Evaluator(settings);
        return evaluator.evaluate(neuralModelStreamPair.r, neuralModelStreamPair.s);
    }
}
