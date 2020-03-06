package networks.computation.training.strategies.trainers;

import networks.computation.training.strategies.debugging.NeuralDebugging;

public interface Training {

    void setupDebugger(NeuralDebugging trainingDebugger);
}
