package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;

/**
 * Created by gusta on 14.3.17.
 */
public class SimpleCycleBreaker implements CycleBreaking {

    @Override
    public NeuralNetwork breakCycles(NeuralNetwork inet) {

        return inet;
    }
}
