package cz.cvut.fel.ida.neural.networks.computation.iteration.actions;

import cz.cvut.fel.ida.neural.networks.computation.iteration.IterationStrategy;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.BFS;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.DFSstack;
import cz.cvut.fel.ida.neural.networks.computation.iteration.modes.Topologic;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.Independent;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.states.StateVisiting;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class IndependentNeuronProcessing {
    private static final Logger LOG = Logger.getLogger(IndependentNeuronProcessing.class.getName());

    Settings settings;
    StateVisiting.Computation visitor;

    public IndependentNeuronProcessing(Settings settings, StateVisiting.Computation visitor) {
        this.settings = settings;
        this.visitor = visitor;
    }

    private IterationStrategy getIterationStrategy(NeuralNetwork network, Neurons outputNeuron) {
        Independent inval = new Independent(network, visitor);
        if (network instanceof TopologicNetwork) {
            return new Topologic((TopologicNetwork<State.Neural.Structure>) network).new BUpIterator(outputNeuron, inval);
        } else if (settings.iterationMode == Settings.IterationMode.DFS_STACK) {
            return new DFSstack().new TDownIterator(network, outputNeuron, inval);
        } else {
            return new BFS().new TDownIterator(network, outputNeuron, inval);
        }
    }

    public void process(NeuralNetwork network, Neurons outputNeuron) {
        IterationStrategy iterationStrategy = getIterationStrategy(network, outputNeuron);
        iterationStrategy.iterate();
    }

}
