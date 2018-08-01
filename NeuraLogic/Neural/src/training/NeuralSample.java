package training;

import learning.Example;
import learning.LearningSample;
import learning.Query;
import networks.evaluation.values.Value;
import networks.structure.NeuralNetwork;
import networks.structure.lrnnTypes.QueryNeuron;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralSample implements LearningSample {
    double importance;
    QueryNeuron queryNeuron;
    NeuralNetwork neuralNetwork;

    Value targetValue;

    @Override
    public Double getImportance() {
        return importance;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value getTarget() {
        return targetValue;
    }

    @Override
    public Query getQuery() {
        return queryNeuron;
    }

    @Override
    public Example getExample() {
        return neuralNetwork;
    }
}