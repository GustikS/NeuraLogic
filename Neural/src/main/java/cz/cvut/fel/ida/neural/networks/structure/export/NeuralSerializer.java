package cz.cvut.fel.ida.neural.networks.structure.export;

import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AggregationNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NeuralSerializer {
    private static final Logger LOG = Logger.getLogger(NeuralSerializer.class.getName());

    public static NumberFormat numberFormat = Settings.defaultNumberFormat;

    public SerializedSample serialize(NeuralSample neuralSample) {
        return new SerializedSample(neuralSample);
    }

    public List<SerializedWeight> serialize(NeuralModel neuralModel) {
        return neuralModel.getAllWeights().stream().map(SerializedWeight::new).collect(Collectors.toList());
    }

    public List<SerializedNeuron> serialize(TopologicNetwork<State.Structure> network) {
        network.restartIndices();
        return network.allNeuronsTopologic.stream().map(neuron -> SerializedNeuron.create(neuron, network)).collect(Collectors.toList());
    }

    public String toGraphviz(TopologicNetwork<State.Structure> network) {
        List<SerializedNeuron> serializedNeurons = serialize(network);
        return null;
    }

    public class SerializedSample implements Exportable {
        public int neuron;
        public List<SerializedNeuron> network;
        public String id;
        public String target;

        public SerializedSample(NeuralSample sample) {
            neuron = sample.query.neuron.getIndex();
            network = serialize((TopologicNetwork<State.Structure>) sample.query.evidence);
            id = sample.getId();
            target = sample.target.toString(numberFormat);
        }
    }


    public static class SerializedNeuron implements Exportable {

        public boolean weighted;

        public String name;
        public int index;

        public String combination;
        public String transformation;

        public int[] dimensions;

        public List<Integer> inputs;

        public List<Integer> weights;
        public int offset;

        public String value;

        public boolean pooling;

        public static SerializedNeuron create(BaseNeuron<Neurons, State.Neural> neuron, TopologicNetwork<State.Structure> network) {
            if (neuron instanceof WeightedNeuron) {
                return new SerializedNeuron((WeightedNeuron<Neurons, State.Neural>) neuron, network);
            } else {
                return new SerializedNeuron(neuron, network);
            }
        }

        SerializedNeuron(BaseNeuron<Neurons, State.Neural> neuron, TopologicNetwork<State.Structure> network) {
            name = neuron.toString();
            index = neuron.index;
            Combination combination = neuron.getCombination();
            if (combination != null)
                this.combination = combination.getName();
            Transformation transformation = neuron.getTransformation();
            if (transformation != null)
                this.transformation = transformation.getName();
            if (neuron instanceof AggregationNeuron) {
                pooling = true;
            }
            Iterator<Neurons> inputs = network.getInputs(neuron);
            if (inputs != null) {
                this.inputs = new ArrayList<>();
                while (inputs.hasNext()) {
                    Neurons next = inputs.next();
                    this.inputs.add(next.getIndex());
                }
            }
        }

        SerializedNeuron(WeightedNeuron<Neurons, State.Neural> neuron, TopologicNetwork<State.Structure> network) {
            this((BaseNeuron<Neurons, State.Neural>) neuron, network);
            weighted = true;
            Pair<Iterator<Neurons>, Iterator<Weight>> inputs = network.getInputs(neuron);
            if (inputs != null) {
                this.inputs = new ArrayList<>();
                this.weights = new ArrayList<>();
                while (inputs.r.hasNext()) {
                    Neurons n = inputs.r.next();
                    Weight w = inputs.s.next();
                    this.inputs.add(n.getIndex());
                    this.weights.add(w.index);
                }
            }
            if (this.inputs.isEmpty()) {    //fact neuron
                this.value = neuron.getRawState().getComputationView(0).getValue().toString(numberFormat);
            }
            if (neuron.offset.index >= 0)
                offset = neuron.offset.index;
        }
    }

    public class SerializedWeight implements Exportable {
        public String name;
        public int index;

        public boolean isFixed;

        public int[] dimensions;
        public String value;

        public SerializedWeight(Weight weight) {
            this.name = weight.name;
            this.index = weight.index;
            this.isFixed = weight.isFixed;
            this.dimensions = weight.value.size();
            this.value = weight.value.toString(numberFormat);
        }
    }
}