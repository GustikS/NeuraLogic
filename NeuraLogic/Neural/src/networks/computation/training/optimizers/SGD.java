package networks.computation.training.optimizers;

import evaluation.values.Value;
import networks.computation.iteration.visitors.weights.WeightUpdater;
import networks.computation.training.NeuralModel;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;

public class SGD implements Optimizer {
    private static final Logger LOG = Logger.getLogger(SGD.class.getName());

    Value learningRate; //todo now check any direct declaration of Value subclasses for DD

    public SGD(Value learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void performGradientStep(NeuralModel neuralModel, WeightUpdater weightUpdater) {
        performGradientStep(neuralModel.weights, weightUpdater.weightUpdates);
    }

    public void performGradientStep(List<Weight> weights, Value[] weightUpdates) {
        for (Weight weight : weights) {
            if (!weight.isLearnable) {
                //fixed weights and constants
            } else {  //todo speedup - update only those weights that have been changed? (i.e. no zero increment calls - i.e. hold list of updates somewhere?)
//                if (weightUpdates[weight.index] == null){
//                    LOG.severe("Weight had no update slot prepared, probably mismatch in weight indexing!");
//                }
                //todo now check if we cannot just multiply once at beginning of backprop! probably yes...at least with SGD it's just a chained multiplication, but it doesnt seem to improve speed performance much
                Value update = weightUpdates[weight.index];//.times(learningRate); //and it's clearer to do it here for each parameter separately

                weight.value.incrementBy(update);
                LOG.finest("Incrementing " + weight + " with " + update);
            }
        }
    }

    @Override
    public void restart(Settings settings) {
        //pass
    }
}
