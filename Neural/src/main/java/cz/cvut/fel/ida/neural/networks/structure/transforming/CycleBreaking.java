package cz.cvut.fel.ida.neural.networks.structure.transforming;

import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.setup.Settings;

/**
 * Created by gusta on 14.3.17.
 */
public interface CycleBreaking {

    public abstract NeuralNetwork breakCycles(NeuralNetwork inet);

    public static CycleBreaking getBreaker(Settings settings){
        //todo add more
        return new SimpleCycleBreaker();
    }
}