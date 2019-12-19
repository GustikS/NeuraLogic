package pipelines.pipes.specific;

import networks.computation.evaluation.results.ClassificationResults;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import pipelines.Pipe;
import settings.Settings;
import utils.generic.Pair;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NeuralEvaluationPipe extends Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Results> {
    private static final Logger LOG = Logger.getLogger(NeuralEvaluationPipe.class.getName());
    Settings settings;

    public NeuralEvaluationPipe(Settings settings) {
        super("NeuralEvaluationPipe");
        this.settings = settings;
    }

    protected NeuralEvaluationPipe(String id) {
        super(id);
    }

    /**
     * Terminating operation (obviously)!
     *
     * @param neuralModelStreamPair
     * @return
     */
    @Override
    public Results apply(Pair<NeuralModel, Stream<NeuralSample>> neuralModelStreamPair) {
        Evaluation evaluator = new Evaluation(settings);
        Stream<Result> resultStream = neuralModelStreamPair.s.map(evaluator::evaluate);
        List<Result> outputList = resultStream.collect(Collectors.toList());
        Results.Factory factory = Results.Factory.getFrom(settings);
        Results results = factory.createFrom(outputList);
        if (neuralModelStreamPair.r.threshold != null && results instanceof ClassificationResults) {    // pass the trained threshold from train to test set
            ((ClassificationResults) results).getBestAccuracy(results.evaluations, neuralModelStreamPair.r.threshold);
        }
        LOG.finer("Testing outputs");
        LOG.fine(results.toString(settings));
        results.printOutputs();
        return results;
    }
}