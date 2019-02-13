package networks.computation.iteration.modes;

import networks.computation.evaluation.values.Value;
import networks.computation.iteration.*;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.neurons.BaseNeuron;
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

        public TDownVisitor(BaseNeuron<BaseNeuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
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
                BaseNeuron<BaseNeuron, State.Neural> actualNeuron = Topologic.this.network.allNeuronsTopologic.get(idx);
                int index = actualNeuron.index; //todo test performance of removing this indexation here
                actualNeuron.index = idx;
                actualNeuron.visit(neuronVisitor);      //skips 1 function call as opposed to actualNeuron.visit(this);
                actualNeuron.index = index;
                idx--;
            }
        }

        @Override
        public void visit(BaseNeuron<BaseNeuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public void visit(WeightedNeuron<BaseNeuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }
    }

    public class BUpVisitor extends NeuronVisiting.Weighted implements BottomUp<Value> {
        NeuronVisitor neuronVisitor;

        public BUpVisitor(BaseNeuron<BaseNeuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
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
                BaseNeuron<BaseNeuron, State.Neural> actualNeuron = Topologic.this.network.allNeuronsTopologic.get(i);
                int index = actualNeuron.index;
                actualNeuron.index = i;
                actualNeuron.visit(neuronVisitor);    //skips 1 function call as opposed to actualNeuron.visit(this);
                actualNeuron.index = index;
                if (actualNeuron == outputNeuron)
                    break;

            }
            return outputNeuron.getComputationView(neuronVisitor.stateVisitor.stateIndex).getResult(neuronVisitor.stateVisitor);
        }

        @Override
        public void visit(BaseNeuron<BaseNeuron, State.Neural> neuron) {
            neuronVisitor.visit(neuron);
        }

        @Override
        public void visit(WeightedNeuron<BaseNeuron, State.Neural> neuron) {
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
        public BaseNeuron<BaseNeuron, State.Neural> next() {
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

        public BUpIterator(BaseNeuron<BaseNeuron, State.Neural> outputNeuron, NeuronVisitor.Weighted pureNeuronVisitor) {
            super(Topologic.this.network, outputNeuron, pureNeuronVisitor);
        }

        @Override
        public BaseNeuron<BaseNeuron, State.Neural> next() {
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