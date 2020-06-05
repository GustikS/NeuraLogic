package cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.CrossSum;
import cz.cvut.fel.ida.algebra.functions.ElementProduct;
import cz.cvut.fel.ida.algebra.functions.Product;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.weights.WeightUpdater;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;

import java.util.logging.Logger;

public class ComplexDown extends NeuronVisitor.Weighted {
    private static final Logger LOG = Logger.getLogger(ComplexDown.class.getName());

    private Weighted classicDown;
    private Weighted crossDown;
    private Weighted elementDown;
    private Weighted productDown;


    public ComplexDown(NeuralNetwork<State.Structure> network, StateVisiting.Computation computationVisitor, WeightUpdater weightUpdater) {
        super(network, computationVisitor, weightUpdater);
    }

    public ComplexDown(Weighted down) {
        super(down.network, down.stateVisitor, down.weightUpdater);
        classicDown = down;
        crossDown = new CrossSumDown(down.network, down.stateVisitor, down.weightUpdater);
        elementDown = new ElementProductDown(down.network, down.stateVisitor, down.weightUpdater);
        productDown = new ProductDown(down.network, down.stateVisitor, down.weightUpdater);
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
        Aggregation aggregation = neuron.getAggregation();
        if (aggregation instanceof CrossSum) {
            crossDown.visit(neuron);
        } else if (aggregation instanceof ElementProduct) {
            elementDown.visit(neuron);
        } else if (aggregation instanceof Product) {
            productDown.visit(neuron);
        } else {
            classicDown.visit(neuron);
        }
    }

    @Override
    public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
        Aggregation aggregation = neuron.getAggregation();
        if (aggregation instanceof CrossSum) {
            crossDown.visit(neuron);
        } else if (aggregation instanceof ElementProduct) {
            elementDown.visit(neuron);
        } else if (aggregation instanceof Product) {
            productDown.visit(neuron);
        } else {
            classicDown.visit(neuron);
        }
    }
}
