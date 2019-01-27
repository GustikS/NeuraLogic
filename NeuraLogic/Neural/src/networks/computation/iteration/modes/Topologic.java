package networks.computation.iteration.modes;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.*;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;

import java.util.logging.Logger;

/**
 * A container class for {@link IterationStrategy iteration strategies} that work with a topologic ordering of neurons (in a {@link TopologicNetwork}).
 * That is following the @{@link NeuronVisiting visitor} or {@link NeuronIterating iterator} patterns for both going
 * {@link BottomUp} and {@link TopDown}, which in a topologically ordered network simply corresponds to iterating left->right and vice versa.
 *
 * @see TopologicNetwork
 */
public class Topologic {
    private static final Logger LOG = Logger.getLogger(Topologic.class.getName());

    TopologicNetwork<State.Neural.Structure> network;

    public Topologic(TopologicNetwork<State.Neural.Structure> network) {
        this.network = network;
    }

    public class TDownVisitor extends NeuronVisiting.Weighted implements TopDown {
        NeuronVisitor neuronVisitor;

        public TDownVisitor(Neuron<Neuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.network, outputNeuron);
            this.neuronVisitor = pureNeuronVisitor;
        }

        @Override
        public void topdown() {
            int idx = Topologic.this.network.allNeuronsTopologic.size() - 1;
            while (Topologic.this.network.allNeuronsTopologic.get(idx) != outputNeuron) {
                idx--;
            }
            while (idx > 0) {
                Neuron<Neuron, State.Neural> actualNeuron = Topologic.this.network.allNeuronsTopologic.get(idx);
                actualNeuron.visit(neuronVisitor);      //skips 1 function call as opposed to actualNeuron.visit(this);
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

    public class BUpVisitor extends NeuronVisiting.Weighted implements BottomUp<Value> {
        NeuronVisitor neuronVisitor;

        public BUpVisitor(Neuron<Neuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.network, outputNeuron);
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
                actual.visit(neuronVisitor);    //skips 1 function call as opposed to actualNeuron.visit(this);
                if (actual == outputNeuron)
                    break;
            }
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getResult(neuronVisitor.stateVisitor);
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

    public class TDownIterator extends NeuronIterating implements TopDown {

        int i = Topologic.this.network.allNeuronsTopologic.size() - 1;

        public TDownIterator(Neuron outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.network, outputNeuron, pureNeuronVisitor);
            while (Topologic.this.network.allNeuronsTopologic.get(i) != outputNeuron) {
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

    public class BUpIterator extends NeuronIterating implements BottomUp<Value> {

        int i = 0;

        public BUpIterator(Neuron<Neuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.network, outputNeuron, pureNeuronVisitor);
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
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getResult(neuronVisitor.stateVisitor);
        }
    }
}