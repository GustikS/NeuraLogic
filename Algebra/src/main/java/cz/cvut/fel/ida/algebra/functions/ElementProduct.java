package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.values.Value;

import java.util.List;
import java.util.logging.Logger;

/**
 * Element-wise product of all the values with some activation function applied on top
 */
public class ElementProduct extends Product {
    private static final Logger LOG = Logger.getLogger(ElementProduct.class.getName());

    public ElementProduct(Activation activation) {
        super(activation);
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public Aggregation replaceWithSingleton() {
        LOG.severe("ElementProduct cannot be singleton.");
        return null;
    }

    protected Value multiplyInputs(List<Value> inputs) {
        int[] size = inputs.get(0).size();
        /*
        for (int i = 0; i < inputs.size(); i++) {
            if (!Arrays.equals(size,inputs.get(i).size())) {
                LOG.severe("ScalarProduct dimensions mismatch!");   //get maximal dimension here instead (zero-pad the rest) ? -> no, invalid vector/matrix operation anyway, rather do not misuse it
                return null;
            }
        }
        */
        Value mult = inputs.get(0).clone();
        for (int i = 1; i < inputs.size(); i++) {
            mult.elementMultiplyBy(inputs.get(i));
        }
        return mult;
    }
}
