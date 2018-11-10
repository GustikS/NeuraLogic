package networks.structure.metadata.states;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.actions.StateVisiting;

import java.util.List;

/**
 * Mainly annotation interfaces used for clarity of stateful processing. It is an interface (not abstract class) so that existing classes (e.g. Value) can be used directly as a state.
 * For other cases, special class States contains particular formations of useful states.
 */
public interface State<V> {

    void invalidate();

    default V accept(StateVisiting<V> visitor){
        return visitor.visit(this);
    }

    interface Neural<V> extends State<V> {

        /**
         * Get Computation view on this neural State of a Neuron
         * @param index
         * @return
         */
        Computation getView(int index);

        Aggregation getActivation();

        /**
         * Stateful values held by a Neuron for use during neural computation, i.e. Evaluation and Backpropagation
         */
        interface Computation extends Neural<Value> {

            ActivationState getActivationState();

            Value getResult(StateVisiting<Value> visitor);

            void store(StateVisiting<Value> visitor, Value value);

            /**
             * tunnel for composite state
             *
             * @param index
             * @return
             */
            @Override
            default Computation getView(int index) {
                return this;
            }

            interface HasParents {

                int getParents(StateVisiting visitor);

                int getChecked(StateVisiting visitor);

                void setChecked(StateVisiting visitor, int checked);
            }

            interface HasDropout {
                double getDropout(StateVisiting visitor);

                void setDropout(StateVisiting visitor, boolean isDropped);
            }

            interface Detailed {

                List<Value> getMessages();

                void setMessages(List<Value> values);

            }

        }

        /**
         * Structural changes to neurons held as a state corresponding to each Neuron in a neural network
         */
        interface Structure extends State {

        }

    }

}