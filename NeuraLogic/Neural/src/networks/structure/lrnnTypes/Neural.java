package networks.structure.lrnnTypes;

import networks.evaluation.values.Value;

public interface Neural {

    /**
     * Forward pass
     * @return
     */
    Value evaluate();

    /**
     * Backward pass
     * @return
     */
    Value gradient();

}
