package networks.computation.training.trainingStrategies;

import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.computation.training.Training;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class SimpleTrainer extends Training {

    protected void train(NeuralModel model, List<NeuralSample> sampleList) {
        throw new NotImplementedException();
    }
}
