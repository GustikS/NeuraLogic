package cz.cvut.fel.ida.neural.networks.computation.iteration.actions;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.iteration.IterationStrategy;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.BFS;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSstack;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.Accumulator;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Accumulating {
    private static final Logger LOG = Logger.getLogger(Accumulating.class.getName());
    private Settings settings;
    private StateVisiting.Computation stateVisitor;

    public Accumulating(Settings settings, StateVisiting.Computation stateVisitor) {
        this.settings = settings;
        this.stateVisitor = stateVisitor;
    }

    private IterationStrategy getIterationStrategy(NeuralNetwork<State.Structure> network, Neurons outputNeuron, Accumulator accumulator) {
        if (network instanceof TopologicNetwork) {
            return new Topologic((TopologicNetwork<State.Neural.Structure>) network).new BUpIterator(outputNeuron, accumulator);
        } else if (settings.iterationMode == Settings.IterationMode.DFS_STACK) {
            return new DFSstack().new TDownIterator(network, outputNeuron, accumulator);
        } else {
            return new BFS().new TDownIterator(network, outputNeuron, accumulator);
        }
    }

    public Value accumulate(NeuralNetwork<State.Structure> neuralNetwork, Neurons outputNeuron) {
        Accumulator accumulator = new Accumulator(neuralNetwork, stateVisitor);
        IterationStrategy iterationStrategy = getIterationStrategy(neuralNetwork, outputNeuron, accumulator);
        iterationStrategy.iterate();
        return accumulator.accumulated;
    }

    public List<Pair<Value, Value>> accumulateStats(List<NeuralSample> samples) {
        List<Pair<Value, Value>> values = new ArrayList<>();
        for (NeuralSample sample : samples) {
            Value acc = accumulate(sample.query.evidence, sample.query.neuron);
            values.add(new Pair<>(acc, new ScalarValue(sample.query.evidence.getNeuronCount())));
        }
        return values;
    }
}
