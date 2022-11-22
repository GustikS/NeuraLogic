package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.combination.Concatenation;
import cz.cvut.fel.ida.algebra.values.MatrixValue;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcatenationTest {

    @TestAnnotations.Fast
    public void eval() {
        double[] x1 = {0.1, 0.5, 0.7, -0.1, 0.2};
        double[] x2 = {0, 0.35, -2};
        List<Value> vals = new ArrayList<>();
        vals.add(new VectorValue(x1));
        vals.add(new VectorValue(x2));

//        ReLu reLu = new ReLu();
        Concatenation concat = new Concatenation();

        Value evaluation = concat.evaluate(vals);

        System.out.println(evaluation);

        assertEquals(evaluation.getClass(),VectorValue.class);
        assertEquals(((VectorValue) evaluation).values.length, x1.length + x2.length);

    }

    @TestAnnotations.Fast
    public void diff() {
        double[] x1 = {0.1, 0.5, 0.7, -0.1, 0.2};
        double[] x2 = {0, 0.35, -2};
        ArrayList<Value> vals = new ArrayList<>();

        vals.add(new VectorValue(x1));
        vals.add(new VectorValue(x2));
        vals.add(new ScalarValue(0.0));

//        ReLu reLu = new ReLu();
        Concatenation.State concatState = new Concatenation.State(Combination.Singletons.concatenation);
        concatState.accumulatedInputs = vals;

        concatState.ingestTopGradient(new VectorValue(new double[9]));

        for (int i = 0; i < 3; i++) {
            Value scalar = concatState.nextInputGradient();
            assertEquals(scalar.getClass(), vals.get(i).getClass());
        }

        try {
            concatState.nextInputGradient();
        } catch (Exception e){
            System.out.println("correct no more");
        }
    }

    @TestAnnotations.Fast
    public void evalAxisZero() {
        double[] x1 = {1, 2};
        double[] x2 = {3, 4};
        double[] x3 = {5, 6, 7, 8};

        List<Value> vals = new ArrayList<>();
        vals.add(new VectorValue(x1, true));
        vals.add(new VectorValue(x2, true));
        vals.add(new MatrixValue(x3, 2, 2));

//        ReLu reLu = new ReLu();
        Concatenation concat = new Concatenation(0);
        Value evaluation = concat.evaluate(vals);

        System.out.println(evaluation);

        assertEquals(evaluation.getClass(), MatrixValue.class);
        assertArrayEquals(evaluation.getAsArray(), new double[] {1, 2, 3, 4, 5, 6, 7, 8});
        assertEquals(((MatrixValue) evaluation).rows, 4);
        assertEquals(((MatrixValue) evaluation).cols, 2);
    }
}