package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;

/**
 * Created by gusta on 14.3.17.
 */
public class SimpleCycleBreaker extends CycleBreaking {

    @Override
    public NeuralNetwork breakCycles(NeuralNetwork inet) {
        if (processedNets.containsKey(inet.getId())){
            return inet;
        }
        processedNets.put(inet.getId(),inet);
        //TODO
        return inet;
    }
}
