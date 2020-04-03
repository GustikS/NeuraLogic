package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.learning.results.DetailedClassificationResults;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.learning.results.Results;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.Evaluation;
import cz.cvut.fel.ida.neural.networks.computation.iteration.actions.IndependentNeuronProcessing;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Invalidator;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

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
        if (neuralModelStreamPair.r.threshold != null && results instanceof DetailedClassificationResults) {    // pass the trained threshold from train to test set
            ((DetailedClassificationResults) results).computeDetailedAccuracy(results.evaluations);
            ((DetailedClassificationResults) results).computeDetailedAccuracy(results.evaluations, neuralModelStreamPair.r.threshold);
        }
        LOG.finer("Testing outputs");
        LOG.fine(results.toString(settings));
        results.printOutputs();
        return results;
    }
}