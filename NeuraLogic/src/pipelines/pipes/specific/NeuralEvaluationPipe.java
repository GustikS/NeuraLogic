package pipelines.pipes.specific;

import ida.utils.tuples.Pair;
import networks.computation.training.evaluation.Evaluation;
import networks.computation.results.Results;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import pipelines.Pipe;
import settings.Settings;

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

    /**
     * Terminating operation!
     * @param neuralModelStreamPair
     * @return
     */
    @Override
    public Results apply(Pair<NeuralModel, Stream<NeuralSample>> neuralModelStreamPair) {
        Evaluation evaluator = new Evaluation(settings);
        neuralModelStreamPair.s.map(s -> evaluator.evaluate(neuralModelStreamPair.r, s));

    }
}