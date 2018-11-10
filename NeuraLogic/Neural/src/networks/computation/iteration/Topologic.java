package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisiting;
import networks.computation.evaluation.values.Value;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * A container for IterationStrategies that are based on a topologic ordering of neurons (in a TopologicNetwork).
 */
public class Topologic {
    private static final Logger LOG = Logger.getLogger(Topologic.class.getName());

    TopologicNetwork<State.Neural.Structure> network;
    StateVisiting<Value> stateVisitor;

    public Topologic(TopologicNetwork<State.Neural.Structure> network, StateVisiting<Value> stateVisitor) {
        this.network = network;
        this.stateVisitor = stateVisitor;
    }


    public class TDownVisitor extends WeightedNeuronVisiting<Value> implements TopDown {
        NeuronVisitor.Weighted neuronVisitor;

        public TDownVisitor(Neuron<Neuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.stateVisitor, Topologic.this.network, outputNeuron);
            this.neuronVisitor = pureNeuronVisitor;
        }

        @Override
        public void topdown() {
            int idx = Topologic.this.network.allNeuronsTopologic.size() - 1;
            while (Topologic.this.network.allNeuronsTopologic.get(idx) != outputNeuron){
                idx--;
            }
            while (idx > 0) {
                Neuron<Neuron, State.Neural> actual = Topologic.this.network.allNeuronsTopologic.get(idx);
                actual.visit(this);
                idx--;
            }
        }

        @Override
        public void visit(Neuron<Neuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public void visit(WeightedNeuron<Neuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }
    }

    public class BUpVisitor extends WeightedNeuronVisiting<Value> implements BottomUp<Value> {
        NeuronVisitor.Weighted neuronVisitor;

        public BUpVisitor(Neuron<Neuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.stateVisitor, Topologic.this.network, outputNeuron);
            this.neuronVisitor = pureNeuronVisitor;
        }

        /**
         * Simple left -> right iteration over the stored topologically sorted array of neurons from TopologicNetwork
         *
         * @return
         */
        public Value bottomUp() {
            for (int i = 0, len = Topologic.this.network.allNeuronsTopologic.size(); i < len; i++) {
                Neuron<Neuron, State.Neural> actual = Topologic.this.network.allNeuronsTopologic.get(i);
                actual.visit(this);
                if (actual == outputNeuron)
                    break;
            }
            return outputNeuron.getStateView(stateVisitor.stateIndex).getResult(stateVisitor);
        }

        @Override
        public void visit(Neuron<Neuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public void visit(WeightedNeuron<Neuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }
    }

    public class TDownIterator extends NeuronIterating<Value> implements TopDown {

        int i = Topologic.this.network.allNeuronsTopologic.size() - 1;

        public TDownIterator(Neuron outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.stateVisitor, Topologic.this.network, outputNeuron, pureNeuronVisitor);
            while (Topologic.this.network.allNeuronsTopologic.get(i) != outputNeuron){
                i--;
            }
        }

        @Override
        public Neuron<Neuron, State.Neural> next() {
            return Topologic.this.network.allNeuronsTopologic.get(i--);
        }

        @Override
        public boolean hasNext() {
            return i >= 0;
        }

        @Override
        public void topdown() {
            iterate();
        }
    }

    public class BUpIterator extends NeuronIterating<Value> implements BottomUp<Value> {

        int i = 0;

        public BUpIterator(Neuron<Neuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.stateVisitor, Topologic.this.network, outputNeuron, pureNeuronVisitor);
        }

        @Override
        public Neuron<Neuron, State.Neural> next() {
            return Topologic.this.network.allNeuronsTopologic.get(i++);
        }

        @Override
        public boolean hasNext() {
            return Topologic.this.network.allNeuronsTopologic.get(i) != outputNeuron;
        }

        @Override
        public Value bottomUp() {
            iterate();
            return outputNeuron.getStateView(stateVisitor.stateIndex).getResult(stateVisitor);
        }
    }
}