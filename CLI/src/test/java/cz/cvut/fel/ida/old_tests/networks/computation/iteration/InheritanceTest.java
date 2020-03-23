package cz.cvut.fel.ida.old_tests.networks.computation.iteration;

import cz.cvut.fel.ida.neural.networks.computation.iteration.*;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class InheritanceTest {
    private static final Logger LOG = Logger.getLogger(InheritanceTest.class.getName());


    public class A extends IterationStrategy implements BottomUp, TopDown {

        public A(StateVisiting stateVisitor, NeuralNetwork network, BaseNeuron outputNeuron) {
            super(network, outputNeuron);
        }

        @Override
        public Object bottomUp() {
            return null;
        }

        @Override
        public void topdown() {

        }

        @Override
        public void iterate() {
            topdown();
        }
    }

    public class B extends NeuronVisiting implements BottomUp {

        public B(StateVisiting stateVisitor, NeuralNetwork network, BaseNeuron outputNeuron) {
            super(network, outputNeuron);
        }

        @Override
        public Object bottomUp() {
            return null;
        }

        @Override
        public void visit(BaseNeuron neuron) {

        }

        public void visit(WeightedNeuron neuron) {

        }
    }

    public class C extends NeuronIterating implements TopDown {

        public C(StateVisiting stateVisitor, NeuralNetwork network, BaseNeuron outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(network, outputNeuron, pureNeuronVisitor);
        }

        @Override
        public BaseNeuron<Neurons, State.Neural> next() {
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
