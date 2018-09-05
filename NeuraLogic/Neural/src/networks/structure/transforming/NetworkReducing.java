package networks.structure.transforming;

import networks.structure.NeuralNetwork;
import settings.Settings;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by gusta on 9.3.17.
 */
public abstract class NetworkReducing {
    HashSet<String> processedNets;

    public NetworkReducing(){
        processedNets = new HashSet<>();
    }
    public abstract NeuralNetwork reduce(NeuralNetwork inet);

    public static NetworkReducing getReducer(Settings settings) {
        //todo add more
        return new LinearChainReducer();
    }

    public static NetworkReducing getCompressor(Settings settings) {
        //todo add more
        return new IsoValueNetworkCompressor();
    }
}
