package cz.cvut.fel.ida.algebra.values;

import java.util.logging.Logger;

public class Tensor {
    private static final Logger LOG = Logger.getLogger(Tensor.class.getName());

    double[] values;
    public int[] dimensions;

    public Tensor(int[] dimensions){
        this.values = allocate(dimensions);
        this.dimensions = dimensions;
    }

    private double[] allocate(int[] dimensions) {
        int totalElements = 1;
        for (int i = 0; i < dimensions.length; i++) {
            totalElements *= dimensions[i];
        }
        return new double[totalElements];
    }

    public double get(int[] indicies) {
        int index = getIndex(indicies);
        return values[index];
    }

    public void set(int[] indicies, double value) {
        int index = getIndex(indicies);
        values[index] = value;
    }

    /**
     * Can be reused externally for faster/direct iteration
     * @param indicies
     * @return
     */
    public int getIndex(int[] indicies) {
        int index = 0;
        for (int i = 0; i < dimensions.length; i++) {
            index += Math.pow(dimensions[i], i) * indicies[dimensions.length - (i + 1)];
        }
        return index;
    }
}