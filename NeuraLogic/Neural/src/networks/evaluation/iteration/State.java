package networks.evaluation.iteration;

import networks.evaluation.values.Value;
import networks.structure.metadata.NeuronMapping;

/**
 * Annotation interfaces for clarity of stateful processing. It is an interface so that existing classes can be used directly in respective places.
 * For other cases, special class States is created.
 */
public interface State {

    /**
     * Stateful value storing during computation
     */
    interface Computation extends State {
        interface Evaluation extends Computation {
        }

        interface Gradient extends Computation {
        }

        interface Pair extends Computation {
            Value getValue();

            Value getGradient();
        }
    }

    /**
     * Structural changes to neurons held as a state of each network
     */
    interface Structure extends State {
        interface InputOvermap extends Structure {
        }

        interface OutputOvermap extends Structure {
        }

        interface Pair extends Structure {
            NeuronMapping getInputs();

            NeuronMapping getOutputs();
        }
    }

}