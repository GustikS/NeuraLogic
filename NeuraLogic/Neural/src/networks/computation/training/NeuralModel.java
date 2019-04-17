package networks.computation.training;

import learning.Model;
import networks.computation.evaluation.values.Value;
import networks.computation.evaluation.values.distributions.ValueInitializer;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.weights.Weight;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralModel implements Model<QueryNeuron> {
    public List<Weight> weights;

    public NeuralModel(List<Weight> weights) {
        this.weights = weights;
    }

    public NeuralModel cloneWeights() {
        List<Weight> clone = new ArrayList<>(weights);
        return new NeuralModel(clone);
    }

    public void resetWeights(ValueInitializer valueInitializer) {
        for (Weight weight : weights) {
            weight.init(valueInitializer);
        }
    }

    public void loadWeights(NeuralModel neuralModel) {

    }

    public void dropoutWeights() {
        //go through weights and set them randomly off
    }

    /**
     * For external training, map all weights to UNIQUE strings
     *
     * @param weights
     * @return
     */
    public Map<String, Weight> mapWeightsToStrings(List<Weight> weights) {
        //TODO
        return null;
    }

    /**
     * Load weight values from unique strings after external training
     *
     * @param tensorflow
     */
    public void importWeights(Reader tensorflow, Map<String, Weight> mapping) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate(QueryNeuron query) {
        return null;
    }


    @Override
    public List<Weight> getAllWeights() {
        return weights;
    }
}