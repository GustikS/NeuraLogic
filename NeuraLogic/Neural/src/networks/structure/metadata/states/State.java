package networks.structure.metadata.states;

import networks.evaluation.values.Value;
import networks.structure.metadata.inputMappings.NeuronMapping;

/**
 * Annotation interfaces for clarity of stateful processing. It is an interface so that existing classes can be used directly in respective places.
 * For other cases, special class States is created.
 */
public interface State {

    //todo different states may accept visitors to perform actions on them, e.g. GradientUpdate, which will get particular state-type  through this double dispatch (or just use instanceOf...)
    default void accept(StateVisitor visitor){
        visitor.visit(this);
    }

    /**
     * Stateful value storing during computation
     */
    interface Computation extends State {
        interface Evaluation extends Computation {
        }

        interface Gradient extends Computation {
        }

        interface ValuePair extends Computation {
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

        interface StructPair extends Structure {
            NeuronMapping getInputs();

            NeuronMapping getOutputs();
        }
    }

}