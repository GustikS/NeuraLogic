package utils.drawing;

import networks.computation.iteration.modes.Topologic;
import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.computation.iteration.visitors.states.neurons.Evaluator;
import networks.computation.training.NeuralSample;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.types.*;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;
import settings.Settings;
import utils.generic.Pair;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralNetDrawer extends Drawer<NeuralSample> {
    private static final Logger LOG = Logger.getLogger(NeuralNetDrawer.class.getName());

    private NeuronVisitor.Weighted.Detailed neuronDrawer;
    private Topologic.BUpIterator bUpIterator;
    public int stateIndex = -1; //which state (=values if minibatches) to draw? (-1 = default)

    public NeuralNetDrawer(Settings settings) {
        super(settings);
    }

    public NeuralNetDrawer(NeuralSample sample, Settings settings) {
        super(settings);
        this.neuronDrawer = new NeuronDrawer(sample.query.evidence);
        this.bUpIterator = new Topologic((TopologicNetwork<State.Neural.Structure>) sample.query.evidence).new BUpIterator(sample.query.neuron, neuronDrawer);
    }

    public void draw(NeuralSample sample) {
        this.neuronDrawer = new NeuronDrawer(sample.query.evidence);
        this.bUpIterator = new Topologic((TopologicNetwork<State.Neural.Structure>) sample.query.evidence).new BUpIterator(sample.query.neuron, neuronDrawer);
        byte[] graphvizGraph = getRawImage();
        graphviz.writeGraphToFile(graphvizGraph, defaultName);
    }

    public void draw(String filename) {
        byte[] graphvizGraph = getRawImage();
        graphviz.writeGraphToFile(graphvizGraph, filename);
    }

    protected byte[] getRawImage() {
        this.graphviz.start_graph();
        bUpIterator.bottomUp();
        this.graphviz.end_graph();
        return graphviz.getGraph(graphviz.getDotSource(), imgtype, algorithm);
    }


    public class NeuronDrawer extends NeuronVisitor.Weighted.Detailed {
        private final GraphViz gv;
        private final NeuralNetwork<State.Structure> neuralNetwork;

        public NeuronDrawer(NeuralNetwork<State.Structure> network) {
            super(network, new Evaluator(stateIndex), null);
            this.gv = NeuralNetDrawer.this.graphviz;
            this.neuralNetwork = NeuralNetDrawer.this.bUpIterator.network;
        }

        private String getNeuronLabel(BaseNeuron neuron) {

            String name = neuron.getClass().getSimpleName() + ":" + neuron.index + ":" + neuron.id;
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            String value = state.getValue().toString();
            String gradient = state.getGradient().toString();
            String dimensions = Arrays.toString(state.getValue().size());
            String aggregation = neuron.getAggregation().getName();

            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("val: ").append(value);
            sb.append("grad: ").append(gradient);
            sb.append("dim: ").append(dimensions);
            sb.append("fcn: ").append(aggregation);
            return sb.toString();
        }

        private String getEdgeLabel(int from, Integer to, Weight weight) {
            StringBuilder sb = new StringBuilder();
            sb.append(weight.index).append(":");
            sb.append(weight.name).append(":");
            sb.append(Arrays.toString(weight.value.size())).append(":");
            sb.append(weight.value.toString());
            if (weight.isFixed)
                sb.append(" [style=dashed] ");
            return sb.toString();
        }

        private <T extends Neurons, S extends State.Neural> String getEdges(BaseNeuron<T, S> neuron) {
            Iterator<T> inputs = neuralNetwork.getInputs(neuron);
            T input;
            StringBuilder sb = new StringBuilder();
            while (inputs.hasNext()) {
                input = inputs.next();
                sb.append(neuron.index + " -> " + input.getIndex() + " [style=dashed] ");
            }
            return sb.toString();
        }

        private <T extends Neurons, S extends State.Neural> String getEdges(WeightedNeuron<T, S> neuron) {
            Pair<Iterator<T>, Iterator<Weight>> inputs = neuralNetwork.getInputs(neuron);
            Iterator<T> inputNeurons = inputs.r;
            Iterator<Weight> inputWeights = inputs.s;
            T input;
            Weight weight;
            StringBuilder sb = new StringBuilder();
            while (inputNeurons.hasNext()) { //todo test version with fori
                input = inputNeurons.next();
                weight = inputWeights.next();
                sb.append(getEdgeLabel(neuron.index, input.getIndex(), weight));
            }
            return sb.toString();
        }


        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            LOG.severe("DDD while drawing");
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            LOG.severe("DDD while drawing");
        }

        @Override
        protected void visit(UnweightedAtomNeuron neuron) {
            gv.addln(neuron.index + "[ shape=ellipse, color=gray30, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        protected void visit(AtomNeuron neuron) {
            gv.addln(neuron.index + "[ shape=ellipse, color=gray30, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        public void visit(AggregationNeuron neuron) {
            gv.addln(neuron.index + "[ shape=box, color=gray20, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        protected void visit(RuleNeuron neuron) {
            gv.addln(neuron.index + "[ shape=ellipse, color=gray10, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        protected void visit(WeightedRuleNeuron neuron) {
            gv.addln(neuron.index + "[ shape=ellipse, color=gray10, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }


        @Override
        protected void visit(FactNeuron neuron) {
            gv.addln(neuron.index + "[ shape=house, color=gray0, label=" + getNeuronLabel(neuron) + "]");
        }
    }
}
