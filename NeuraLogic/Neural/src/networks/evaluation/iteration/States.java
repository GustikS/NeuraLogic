package networks.evaluation.iteration;

import networks.evaluation.values.Value;

import java.util.logging.Logger;

public class States {
    private static final Logger LOG = Logger.getLogger(States.class.getName());

    public static final class Pair implements State.Pair {
        Value value;
        Value gradient;

        @Override
        public final Value getValue() {
            return value;
        }

        @Override
        public final Value getGradient() {
            return gradient;
        }
    }
}