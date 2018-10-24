package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisitor;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public class InheritanceTest {
    private static final Logger LOG = Logger.getLogger(InheritanceTest.class.getName());


    public class A extends IterationStrategy implements networks.computation.iteration.BottomUp, networks.computation.iteration.TopDown {

        public A(StateVisitor stateVisitor, NeuralNetwork network, Neuron outputNeuron) {
            super(stateVisitor, network, outputNeuron);
        }

        @Override
        public Object bottomUp() {
            return null;
        }

        @Override
        public void topdown() {

        }
    }

    public class B extends NeuronVisiting implements networks.computation.iteration.BottomUp {

        public B(StateVisitor stateVisitor, NeuralNetwork network, Neuron outputNeuron) {
            super(stateVisitor, network, outputNeuron);
        }

        @Override
        public Object bottomUp() {
            return null;
        }

        @Override
        public void expand(Neuron neuron) {

        }

        @Override
        public void expand(WeightedNeuron neuron) {

        }
    }

    public class C extends NeuronIterating implements networks.computation.iteration.TopDown {

        public C(StateVisitor stateVisitor, NeuralNetwork network, Neuron outputNeuron, NeuronVisitor pureNeuronVisitor) {
            super(stateVisitor, network, outputNeuron, pureNeuronVisitor);
        }

        @Override
        public Neuron<Neuron, State.Computation> next() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public void topdown() {
            super.iterate();
        }

    }
}
