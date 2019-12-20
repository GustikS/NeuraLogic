package pipelines.pipes.specific;

import networks.computation.evaluation.results.ClassificationResults;
import networks.computation.evaluation.results.Result;
import networks.computation.evaluation.results.Results;
import networks.computation.iteration.actions.Evaluation;
import networks.computation.iteration.actions.IndependentNeuronProcessing;
import networks.computation.iteration.visitors.states.neurons.Invalidator;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.types.AtomNeurons;
import networks.structure.metadata.states.State;
import pipelines.Pipe;
import settings.Settings;
import utils.generic.Pair;

import java.util.ArrayList;
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

    /**
     * Terminating operation (obviously)!
     *
     * @param neuralModelStreamPair
     * @return
     */
    @Override
    public Results apply(Pair<NeuralModel, Stream<NeuralSample>> neuralModelStreamPair) {
        IndependentNeuronProcessing invalidation = new IndependentNeuronProcessing(settings, new Invalidator(-1));  //todo now the index should be passed from the testing pipeline! This will not work in parallel...
        Evaluation evaluation = new Evaluation(settings);
        List<NeuralSample> collect = neuralModelStreamPair.s.collect(Collectors.toList());

        List<Result> resultList = new ArrayList<>();
        for (NeuralSample neuralSample : collect) {
            NeuralNetwork<State.Structure> neuralNetwork = neuralSample.query.evidence;
            AtomNeurons<State.Neural> neuron = neuralSample.query.neuron;

            neuralSample.query.evidence.initializeStatesCache(-1);    //here we can transfer information from Structure to Computation
            invalidation.process(neuralNetwork, neuron);
            Result evaluate = evaluation.evaluate(neuralSample);
            resultList.add(evaluate);
        }

        Results.Factory factory = Results.Factory.getFrom(settings);
        Results results = factory.createFrom(resultList);
        if (neuralModelStreamPair.r.threshold != null && results instanceof ClassificationResults) {    // pass the trained threshold from train to test set
            ((ClassificationResults) results).getBestAccuracy(results.evaluations, neuralModelStreamPair.r.threshold);
        }
        LOG.finer("Testing outputs");
        LOG.fine(results.toString(settings));
        results.printOutputs();
        return results;
    }
}