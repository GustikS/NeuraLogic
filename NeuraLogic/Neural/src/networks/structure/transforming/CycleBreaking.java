package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import settings.Settings;

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