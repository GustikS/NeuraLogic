package training;

import networks.structure.Weight;

import java.util.List;

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
}