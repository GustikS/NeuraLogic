package networks.structure.networks;

import settings.Settings;

import java.util.logging.Logger;

public class NetworkFactory {   //todo next use in Grounder
    private static final Logger LOG = Logger.getLogger(NetworkFactory.class.getName());
    Settings settings;

    public NetworkFactory(Settings settings){
        this.settings = settings;
    }

    public void setBST(int limit) {
        if (getSize() < limit)
            BST = true;
        else
            BST = false;
    }
}
