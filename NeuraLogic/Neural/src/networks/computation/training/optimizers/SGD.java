package networks.computation.training.optimizers;

import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;

import java.util.List;
import java.util.logging.Logger;

public class SGD implements Optimizer {
    private static final Logger LOG = Logger.getLogger(SGD.class.getName());

    ScalarValue learningRate;

    public SGD(double learningRate) {
        this.learningRate = new ScalarValue(learningRate);
    }

    @Override
    public void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater) {
        performGradientStep(neuralModel.weights, weightUpdater.weightUpdates);
    }

    public void performGradientStep(List<Weight> weights, Value[] weightUpdates) {
        for (Weight weight : weights) {
            if (weight.isFixed || weight.index < 0) {
                //fixed weights and constants
            } else {  //todo speedup - update only those weights that have been changed? (i.e. no zero increment calls - i.e. hold list of updates somewhere?)
                if (weightUpdates[weight.index] == null){
                    System.out.println();
                }
                Value update = weightUpdates[weight.index].times(learningRate);
                weight.value.incrementBy(update); //todo check if we cannot just multiply once at beginning of backprop!
                LOG.finest("Incrementing " + weight + " with " + update);
            }
        }
    }
}
