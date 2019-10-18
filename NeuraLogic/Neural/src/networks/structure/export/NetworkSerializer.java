package networks.structure.export;

import networks.structure.components.types.TopologicNetwork;

import java.util.logging.Logger;

public class NetworkSerializer {
    private static final Logger LOG = Logger.getLogger(NetworkSerializer.class.getName());

    public String toDynet(TopologicNetwork network){
        // todo next - create most suitable serialization format for Dynet
        return null;
    }

    public String toGraphviz(TopologicNetwork network){
        //todo next drawing
        return null;
    }
}
