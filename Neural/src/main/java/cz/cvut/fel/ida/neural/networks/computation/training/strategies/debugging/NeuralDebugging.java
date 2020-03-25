package cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging;

import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;

import java.io.Serializable;

public interface NeuralDebugging extends Serializable {
    void debug(NeuralSample neuralSample);
}
