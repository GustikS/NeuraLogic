package networks.computation.iteration;

import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

public class InheritanceTest {
    private static final Logger LOG = Logger.getLogger(InheritanceTest.class.getName());


    public class A extends IterationStrategy implements networks.computation.iteration.BottomUp, networks.computation.iteration.TopDown {

        public A(StateVisiting stateVisitor, NeuralNetwork network, Neuron outputNeuron) {
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

        public B(StateVisiting stateVisitor, NeuralNetwork network, Neuron outputNeuron) {
            super(stateVisitor, network, outputNeuron);
        }

        @Override
        public Object bottomUp() {
            return null;
        }

        @Override
        public void visit(Neuron neuron) {

        }

        @Override
        public void visit(WeightedNeuron neuron) {

        }
    }

    public class C extends NeuronIterating implements networks.computation.iteration.TopDown {

        public C(StateVisiting stateVisitor, NeuralNetwork network, Neuron outputNeuron, NeuronVisitorWeighted pureNeuronVisitor) {
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
