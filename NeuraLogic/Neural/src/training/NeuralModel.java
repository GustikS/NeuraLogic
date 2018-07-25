package training;

import networks.structure.Weight;

import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralModel {
    List<Weight> weights;

    public  NeuralModel(List<Weight> weights){
        this.weights = weights;
    }

    void resetWeights(){
        for (Weight weight : weights) {
            if (weight.isLearnable){
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
}