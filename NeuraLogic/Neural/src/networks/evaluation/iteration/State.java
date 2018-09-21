package networks.evaluation.iteration;

import networks.evaluation.values.Value;

public interface State {

    interface Evaluation extends State {
    }

    interface Gradient extends State {
    }

    interface Pair extends State {
        Value getValue();
        Value getGradient();
    }
}