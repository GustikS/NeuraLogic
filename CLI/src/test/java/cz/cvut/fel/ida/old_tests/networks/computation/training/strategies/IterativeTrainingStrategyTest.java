package cz.cvut.fel.ida.old_tests.networks.computation.training.strategies;

import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.IterativeTrainingStrategy;
import cz.cvut.fel.ida.neural.networks.computation.training.strategies.TrainingStrategy;
import cz.cvut.fel.ida.setup.Settings;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class IterativeTrainingStrategyTest {

    @Test
    public void exportToJson() {
        TrainingStrategy trainingStrategy = new IterativeTrainingStrategy(new Settings(),new NeuralModel(new ArrayList<>(), new Settings()), new ArrayList<>());
        String json = trainingStrategy.exportToJson();
        System.out.println(json);
    }
}