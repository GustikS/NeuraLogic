package cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers;

import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;

public interface Training {

    void setupDebugger(NeuralDebugging trainingDebugger);
}
