package pipelines.prepared.pipes.specific;

import ida.utils.tuples.Pair;
import networks.evaluation.Evaluator;
import pipelines.Pipe;
import training.NeuralModel;
import training.NeuralSample;
import networks.evaluation.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class NeuralEvaluationPipe extends Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Results> {
    private static final Logger LOG = Logger.getLogger(NeuralEvaluationPipe.class.getName());

    protected NeuralEvaluationPipe(String id) {
        super(id);
    }

    @Override
    public Results apply(Pair<NeuralModel, Stream<NeuralSample>> neuralModelStreamPair) {
        Evaluator evaluator = new Evaluator();
        evaluator.evaluate(neuralModelStreamPair.r,neuralModelStreamPair.s);
    }
}
