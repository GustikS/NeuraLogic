package cz.cvut.fel.ida.neural.networks.structure.transforming;

import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;

/**
 * Created by gusta on 14.3.17.
 */
public class SimpleCycleBreaker implements CycleBreaking {

    @Override
    public NeuralNetwork breakCycles(NeuralNetwork inet) {

        return inet;
    }
}
