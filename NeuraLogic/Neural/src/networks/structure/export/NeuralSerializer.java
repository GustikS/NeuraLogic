package networks.structure.export;

import evaluation.functions.Aggregation;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.types.AggregationNeuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.components.weights.Weight;
import networks.structure.components.neurons.states.State;
import utils.generic.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NeuralSerializer {
    private static final Logger LOG = Logger.getLogger(NeuralSerializer.class.getName());

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

    public class SerializedSample {
        public int neuron;
        public List<SerializedNeuron> network;
        public String id;
        public String target;

        public SerializedSample(NeuralSample sample) {
            neuron = sample.query.neuron.getIndex();
            network = serialize((TopologicNetwork<State.Structure>) sample.query.evidence);
            id = sample.getId();
            target = sample.target.toString();
        }
    }


    public static class SerializedNeuron {

        public boolean weighted;

        public String name;
        public int index;
        public String activation;

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
            Aggregation aggregation = neuron.getAggregation();
            if (aggregation != null)
                activation = aggregation.getName();
            if (neuron instanceof AggregationNeuron){
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
                this.value = neuron.getRawState().getComputationView(0).getValue().toString();
            }
            if (neuron.offset.index > 0)
                offset = neuron.offset.index;
        }
    }

    public class SerializedWeight {
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
            this.value = weight.value.toString();
        }
    }
}