package networks.structure.transforming;

import networks.structure.NeuralNetwork;
import settings.Settings;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by gusta on 14.3.17.
 */
public abstract class CycleBreaking {

    HashSet<String> processedNets;

    public CycleBreaking(){
        processedNets = new HashSet<>();
    }

    public abstract NeuralNetwork breakCycles(NeuralNetwork inet);

    public static CycleBreaking getBreaker(Settings settings){
        //todo add more
        return new SimpleCycleBreaker();
    }
}