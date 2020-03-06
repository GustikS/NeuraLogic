package networks.computation.training.strategies;

import networks.computation.training.NeuralModel;
import org.junit.Test;
import settings.Settings;

import java.util.ArrayList;

public class IterativeTrainingStrategyTest {

    @Test
    public void exportToJson() {
        TrainingStrategy trainingStrategy = new IterativeTrainingStrategy(new Settings(),new NeuralModel(new ArrayList<>(), new Settings()), new ArrayList<>());
        String json = trainingStrategy.exportToJson();
        System.out.println(json);
    }
}