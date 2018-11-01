package networks.computation.training;

import learning.Model;
import networks.computation.evaluation.values.Value;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.weights.Weight;

import java.io.Reader;
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
        return null;
    }

    public void resetWeights() {
        for (Weight weight : weights) {
            if (!weight.isFixed) {
                weight.init();
            }
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