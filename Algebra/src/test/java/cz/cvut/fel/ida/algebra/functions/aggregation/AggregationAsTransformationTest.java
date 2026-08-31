package cz.cvut.fel.ida.algebra.functions.aggregation;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link cz.cvut.fel.ida.algebra.functions.Aggregation} extends
 * {@link cz.cvut.fel.ida.algebra.functions.Transformation}, so each of these can also be applied across the
 * components of one vector instead of across a list of inputs. That second reading has its own pair of
 * evaluate/differentiate methods, and nothing exercised them - the PyNeuraLogic frontend refuses an
 * aggregation in a transformation slot, so no test reached them from that side either.
 */
class AggregationAsTransformationTest {

    @TestAnnotations.Fast
    public void averageOverComponentsDifferentiatesToOneOverTheirCount() {
        Value gradient = new Average().differentiate(new VectorValue(new double[]{0.1, 0.5, -0.3, 0.7}));

        assertEquals(0.25, gradient.get(0), 1e-12);
    }

    @TestAnnotations.Fast
    public void averageOverASingleComponentDifferentiatesToOne() {
        Value gradient = new Average().differentiate(new VectorValue(new double[]{0.6}));

        assertEquals(1.0, gradient.get(0), 1e-12);
    }

    @TestAnnotations.Fast
    public void maximumOverNegativeComponentsFindsTheLargestOfThem() {
        double[] values = {-3.0, -0.5, -7.0};

        assertEquals(1, new Maximum().getMaxValue(values).r);
        assertEquals(-0.5, new Maximum().getMaxValue(values).s[1], 1e-12);
    }

    @TestAnnotations.Fast
    public void minimumOverPositiveComponentsFindsTheSmallestOfThem() {
        double[] values = {3.0, 0.5, 7.0};

        assertEquals(1, new Minimum().getMinValue(values).r);
        assertEquals(0.5, new Minimum().getMinValue(values).s[1], 1e-12);
    }
}
