package utils.drawing;

import networks.computation.evaluation.functions.Aggregation;
import networks.computation.evaluation.values.Value;
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

    private NeuronDrawer neuronDrawer;
    private Topologic.BUpIterator bUpIterator;
    public int stateIndex = -1; //which state (=values if minibatches) to draw? (-1 = default)

    public NeuralNetDrawer(Settings settings) {
        super(settings);
    }

    @Override
    public void loadGraph(NeuralSample sample) {
        this.neuronDrawer = new NeuronDrawer(sample.query.evidence, stateIndex, graphviz);
        this.bUpIterator = new Topologic((TopologicNetwork<State.Neural.Structure>) sample.query.evidence).new BUpIterator(sample.query.neuron, neuronDrawer);

        this.graphviz.start_graph();
        iterateNetwork();
        this.graphviz.addln(sample.query.neuron.getIndex() + " [shape = tripleoctagon]");
        this.graphviz.end_graph();
    }

    private void iterateNetwork() {
        while (this.bUpIterator.hasNext()) {
            BaseNeuron<Neurons, State.Neural> nextNeuron = this.bUpIterator.next();
            nextNeuron.visit((NeuronVisitor.Weighted.Detailed) neuronDrawer);
        }
    }

    public static class NeuronDrawer extends NeuronVisitor.Weighted.Detailed {
        private final GraphViz gv;
        private final NeuralNetwork<State.Structure> neuralNetwork;

        public NeuronDrawer(NeuralNetwork<State.Structure> network, int stateIndex, GraphViz gv) {
            super(network, new Evaluator(stateIndex), null);
            this.gv = gv;
            this.neuralNetwork = network;
        }

        private String getNeuronLabel(BaseNeuron neuron) {

            String name = neuron.getClass().getSimpleName() + ":" + neuron.index + ":" + neuron.id;
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            String value = state.getValue().toString();
            Value stateGradient = state.getGradient();
            String gradient = "";
            if (stateGradient != null)
                gradient = stateGradient.toString();
            String dimensions = Arrays.toString(state.getValue().size());
            Aggregation neuronAggregation = neuron.getAggregation();
            String aggregation = "";
            if (neuronAggregation != null)
                aggregation = neuron.getAggregation().getName();

            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(name).append("\n");
            sb.append("val: ").append(value).append("\n");
            sb.append("grad: ").append(gradient).append("\n");
            sb.append("dim: ").append(dimensions).append("\n");
            sb.append("fcn: ").append(aggregation).append("\n");
            sb.append("\"");
            return sb.toString();
        }

        private String getEdgeLabel(int from, Integer to, Weight weight) {
            StringBuilder sb = new StringBuilder();
            sb.append(" [label=");
            sb.append("\"");
            sb.append(weight.index).append(":");
            sb.append(weight.name).append(":");
            sb.append(Arrays.toString(weight.value.size())).append(":");
            sb.append(weight.value.toString());
            sb.append("\"");
            if (weight.isFixed)
                sb.append(", style=dashed ");
            sb.append("]");
            return sb.toString();
        }

        private <T extends Neurons, S extends State.Neural> String getEdges(BaseNeuron<T, S> neuron) {
            Iterator<T> inputs = neuralNetwork.getInputs(neuron);
            T input;
            StringBuilder sb = new StringBuilder();
            while (inputs.hasNext()) {
                input = inputs.next();
                sb.append(neuron.index + " -> " + input.getIndex() + " [style=dashed] ").append("\n");
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
            while (inputNeurons.hasNext()) {
                input = inputNeurons.next();
                weight = inputWeights.next();
                sb.append(neuron.index + " -> " + input.getIndex() + getEdgeLabel(neuron.index, input.getIndex(), weight)).append("\n");
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
        public void visit(UnweightedAtomNeuron neuron) {
            gv.addln(neuron.index + " [shape=ellipse, color=blue, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        public void visit(AtomNeuron neuron) {
            gv.addln(neuron.index + " [shape=ellipse, color=blue, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        public void visit(AggregationNeuron neuron) {
            gv.addln(neuron.index + " [shape=box, color=green, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        public void visit(RuleNeuron neuron) {
            gv.addln(neuron.index + " [shape=ellipse, color=red, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        public void visit(WeightedRuleNeuron neuron) {
            gv.addln(neuron.index + " [shape=ellipse, color=red, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }


        @Override
        public void visit(FactNeuron neuron) {
            gv.addln(neuron.index + " [shape=house, color=black, label=" + getNeuronLabel(neuron) + "]");
            gv.addln();
        }
    }
}
