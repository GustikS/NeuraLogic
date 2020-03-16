package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.functions.CrossProduct;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.AggregationState;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CrossDown extends NeuronVisitor.Weighted {

    NeuronVisitor.Weighted classicDown;

    public CrossDown(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor, WeightUpdater weightUpdater) {
        super(network, computationVisitor, weightUpdater);
        classicDown = new StandardNeuronVisitors.Down(network, computationVisitor, weightUpdater);
    }

    public CrossDown(Weighted down) {
        super(down.network, down.stateVisitor, down.weightUpdater);
        classicDown = down;
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        if (neuron.getAggregation() instanceof CrossProduct) {  //todo make somewhat nicer
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            int[][] mapping = ((AggregationState.CrossProducState) state.getAggregationState()).mapping;  //todo make this brutal exploitation somewhat nicer

            Iterator<T> inputs = network.getInputs(neuron);
            List<Value> inputGradients = new ArrayList<>();

            while (inputs.hasNext()) {
                T next = inputs.next();
                inputGradients.add(next.getComputationView(stateVisitor.stateIndex).getGradient());
            }

            for (int i = 0; i < mapping.length; i++) {
                double grad = gradient.get(i);
                int[] map = mapping[i];

                for (int j = 0; j < inputGradients.size(); j++) {
                    Value inGrad = inputGradients.get(j);
                    if (inGrad != null)
                        inGrad.increment(map[j], grad);
                }
            }
        } else {
            classicDown.visit(neuron);
        }
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        if (neuron.getAggregation() instanceof CrossProduct) {
            State.Neural.Computation state = neuron.getComputationView(stateVisitor.stateIndex);
            Value gradient = stateVisitor.visit(state);
            int[][] mapping = ((AggregationState.CrossProducState) state.getAggregationState()).mapping;  //todo make this brutal exploitation somewhat nicer

            Pair<Iterator<T>, Iterator<Weight>> inputs = network.getInputs(neuron);

            weightUpdater.visit(neuron.offset, gradient);

            Iterator<T> inputNeurons = inputs.r;
            Iterator<Weight> inWeights = inputs.s;

            List<Value> inputGradients = new ArrayList<>();
            List<Value> inputOutputValues = new ArrayList<>();
            List<Weight> inputWeights = new ArrayList<>();

            while (inputNeurons.hasNext()) {
                T next = inputNeurons.next();
                inputWeights.add(inWeights.next());
                inputGradients.add(next.getComputationView(stateVisitor.stateIndex).getGradient());
                inputOutputValues.add(next.getComputationView(stateVisitor.stateIndex).getValue());
            }

            for (int i = 0; i < mapping.length; i++) {
                double grad = gradient.get(i);
                int[] map = mapping[i];

                for (int j = 0; j < inputGradients.size(); j++) {
                    Weight weight = inputWeights.get(j);

                    if (weight.isLearnable)
                        weight.value.increment(map[j], grad * inputOutputValues.get(j).get(map[j]));

                    Value inGrad = inputGradients.get(j);
                    if (inGrad != null) {
//                        inGrad.increment(map[j], grad * weight.value.get(map[j]));    //todo next now Weights in crossproduct do not get actually multiplied here
                        inGrad.increment(map[j], grad * weight.value.get(0));   //assuming only Scalar weight here
                    }
                }
            }
        } else {
            classicDown.visit(neuron);
        }
    }
}