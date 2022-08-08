package cz.cvut.fel.ida.algebra.functions;

import cz.cvut.fel.ida.algebra.functions.combination.Concatenation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.List;

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

//        ReLu reLu = new ReLu();
        Concatenation.State concatState = new Concatenation.State(Combination.Singletons.concatenation);

        concatState.ingestTopGradient(new VectorValue(new double[8]));

        for (int i = 0; i < 8; i++) {
            Value scalar = concatState.nextInputGradient();
            assertEquals(scalar.getClass(), ScalarValue.class);
        }

        try {
            concatState.nextInputGradient();
        } catch (Exception e){
            System.out.println("correct no more");
        }


    }
}