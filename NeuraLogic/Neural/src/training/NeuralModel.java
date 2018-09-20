package training;

import learning.Model;
import networks.evaluation.values.Value;
import networks.structure.Weight;
import networks.structure.neurons.QueryNeuron;

import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralModel implements Model<QueryNeuron> {
    List<Weight> weights;

    public  NeuralModel(List<Weight> weights){
        this.weights = weights;
    }

    void resetWeights(){
        for (Weight weight : weights) {
            if (!weight.isFixed){
                weight.init();
            }
        }
    }

    /**
     * For external training, map all weights to UNIQUE strings
     * @param weights
     * @return
     */
    public Map<String, Weight> mapWeightsToStrings(List<Weight> weights){
        //TODO
        return null;
    }

    /**
     * Load weight values from unique strings after external training
     * @param tensorflow
     */
    public void importWeights(Reader tensorflow, Map<String, Weight> mapping){

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