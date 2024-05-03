package cz.cvut.fel.ida.pipelines.debugging.drawing;

import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.drawing.Drawer;
import cz.cvut.fel.ida.drawing.GraphViz;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.neurons.Evaluator;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.*;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

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
        if (sample.query.neuron != null) {
            this.graphviz.addln(sample.query.neuron.getIndex() + "[shape = tripleoctagon, xlabel=\"\n\n\ntarget = " + sample.target + "  \"]");
        }
        this.graphviz.end_graph();
    }

    private void iterateNetwork() {
        while (this.bUpIterator.hasNext()) {
            BaseNeuron<Neurons, State.Neural> nextNeuron = this.bUpIterator.next();
            nextNeuron.visit((NeuronVisitor.Weighted.Detailed) neuronDrawer);
        }
    }

    public class NeuronDrawer extends NeuronVisitor.Weighted.Detailed {
        private final GraphViz gv;
        private final NeuralNetwork<State.Structure> neuralNetwork;

        public NeuronDrawer(NeuralNetwork<State.Structure> network, int stateIndex, GraphViz gv) {
            super(network, new Evaluator(stateIndex), null);
            this.gv = gv;
            this.neuralNetwork = network;
        }

        private String getNeuronLabel(BaseNeuron neuron) {
            String typeName = numberFormat != null ? neuron.getClass().getSimpleName() : "n";
            String name = typeName + ":" + neuron.index + ":" + neuron.name;
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            String value = state.getValue().toString(NeuralNetDrawer.this.numberFormat);
            Value stateGradient = state.getGradient();
            String gradient = "";
            if (stateGradient != null)
                gradient = stateGradient.toString(NeuralNetDrawer.this.numberFormat);
            String dimensions = Arrays.toString(state.getValue().size());
            Combination neuronCombination;
            Transformation neuronTransformation;
            if (neuron instanceof FactNeuron) {
                neuronCombination = null;
                neuronTransformation = null;
            } else {
                neuronCombination = neuron.getCombination();
                neuronTransformation = neuron.getTransformation();
            }
            String fcns = "";
            if (neuronCombination != null)
                fcns += neuronCombination.getName();
            if (neuronTransformation != null)
                fcns += "+" + neuronTransformation.getName();

            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(name).append("\n");
            sb.append("val: ").append(value).append("\n");
            sb.append("grad: ").append(gradient).append("\n");
            sb.append("dim: ").append(dimensions).append("\n");
            sb.append("fcn: ").append(fcns).append("\n");
            sb.append("\"");
            return sb.toString();
        }

        private String getEdgeLabel(int from, Integer to, Weight weight) {
            StringBuilder sb = new StringBuilder();
            sb.append(" [label=");
            sb.append("\"");
//            sb.append(weight.index).append(":");
            sb.append(weight.name).append(":");
//            sb.append(Arrays.toString(weight.value.size())).append(":");
            sb.append(weight.value.toString(NeuralNetDrawer.this.numberFormat));
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
                sb.append(input.getIndex() + " -> " + neuron.index + " [style=dashed] ").append("\n");
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
                sb.append(input.getIndex() + " -> " + neuron.index + getEdgeLabel(neuron.index, input.getIndex(), weight)).append("\n");
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
        public void visit(AtomNeuron neuron) {
            gv.addln(neuron.index + " [shape=ellipse, color=blue, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        public void visit(NegationNeuron neuron) {
            gv.addln(neuron.index + " [shape=ellipse, color=red, label=" + getNeuronLabel(neuron) + "]");
            gv.addln(getEdges(neuron));
        }

        @Override
        public void visit(WeightedAtomNeuron neuron) {
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
