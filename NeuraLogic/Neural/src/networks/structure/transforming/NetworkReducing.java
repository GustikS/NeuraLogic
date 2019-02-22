package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import settings.Settings;

/**
 * Created by gusta on 9.3.17.
 */
public interface NetworkReducing {

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
