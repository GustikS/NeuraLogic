package cz.cvut.fel.ida.neural.networks.computation.iteration.modes;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.*;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;

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
        NeuronVisitor.Weighted neuronVisitor;

        public TDownVisitor(Neurons outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
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
                BaseNeuron<Neurons, State.Neural> actualNeuron = Topologic.this.network.allNeuronsTopologic.get(idx);
                int index = actualNeuron.index; //todo test performance of removing this indexation here
                actualNeuron.index = idx;
                actualNeuron.visit(neuronVisitor);      //skips 1 function call as opposed to actualNeuron.visit(this);
                actualNeuron.index = index;
                idx--;
            }
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            neuronVisitor.visit(neuron);
        }
    }

    public class BUpVisitor extends NeuronVisiting.Weighted implements BottomUp<Value> {
        NeuronVisitor.Weighted neuronVisitor;

        public BUpVisitor(Neurons outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
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
                BaseNeuron<Neurons, State.Neural> actualNeuron = Topologic.this.network.allNeuronsTopologic.get(i);
                int index = actualNeuron.index;
                actualNeuron.index = i;
                actualNeuron.visit(neuronVisitor);    //skips 1 function call as opposed to actualNeuron.visit(this);
                actualNeuron.index = index;
                if (actualNeuron == outputNeuron) break;

            }
            if (outputNeuron == null) {
                LOG.warning("No output neuron detected, don't know which Value to return from evaluation (returning dummy 0).");
                return new ScalarValue(0);
            }
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getValue();
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            neuronVisitor.visit(neuron);
        }
    }

    public class TDownIterator extends NeuronIterating implements TopDown {

        int i = Topologic.this.network.allNeuronsTopologic.size() - 1;

        public TDownIterator(BaseNeuron outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.network, outputNeuron, pureNeuronVisitor);
            while (Topologic.this.network.allNeuronsTopologic.get(i) != outputNeuron) {
                i--;
            }
        }

        @Override
        public BaseNeuron<Neurons, State.Neural> next() {
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

        public BUpIterator(Neurons outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.network, outputNeuron, pureNeuronVisitor);
        }

        @Override
        public BaseNeuron<Neurons, State.Neural> next() {
            return Topologic.this.network.allNeuronsTopologic.get(i++);
        }

        @Override
        public boolean hasNext() {
            if (Topologic.this.network.allNeuronsTopologic.isEmpty()) {
                return false;
            }
            if (i == 0) {
                return true;
            }
            return i < Topologic.this.network.allNeuronsTopologic.size() && Topologic.this.network.allNeuronsTopologic.get(i - 1) != outputNeuron; //we need the output neuron to be processed, too!
        }

        @Override
        public Value bottomUp() {
            iterate();
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getValue();
        }
    }
}