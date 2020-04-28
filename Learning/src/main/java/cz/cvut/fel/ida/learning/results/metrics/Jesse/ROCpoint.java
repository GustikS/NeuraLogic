package cz.cvut.fel.ida.learning.results.metrics.Jesse;

import java.util.logging.Logger;

public class ROCpoint implements Comparable {
    private static final Logger LOG = Logger.getLogger(ROCpoint.class.getName());

    public double posPosition;
    public double negPosition;

    private static double threshold = 0.001;

    public ROCpoint(double pos, double neg) {
        this.posPosition = pos;
        this.negPosition = neg;
    }

    public int compareTo(Object obj) {
        if (obj instanceof ROCpoint) {
            ROCpoint var2 = (ROCpoint) obj;
            if (this.posPosition - var2.posPosition > 0) {
                return 1;
            } else if (this.posPosition - var2.posPosition < 0) {
                return -1;
            } else if (this.negPosition - var2.negPosition > 0) {
                return 1;
            } else {
                return this.negPosition - var2.negPosition < 0 ? -1 : 0;
            }
        } else {
            return -1;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof ROCpoint) {
            ROCpoint var2 = (ROCpoint) obj;
            if (Math.abs(this.posPosition - var2
                    .posPosition) > threshold) {
                return false;
            } else {
                return Math.abs(this.negPosition - var2.negPosition) <= threshold;
            }
        } else {
            return false;
        }
    }
}