package networks.computation.iteration.actions;

import networks.computation.evaluation.values.ScalarValue;
import networks.computation.evaluation.values.Value;
import networks.computation.iteration.IterationStrategy;
import networks.computation.iteration.modes.BFS;
import networks.computation.iteration.modes.DFSstack;
import networks.computation.iteration.modes.Topologic;
import networks.computation.iteration.visitors.neurons.Accumulator;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.computation.training.NeuralSample;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;
import utils.generic.Pair;

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
