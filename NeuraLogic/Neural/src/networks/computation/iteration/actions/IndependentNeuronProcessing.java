package networks.computation.iteration.actions;

import networks.computation.iteration.IterationStrategy;
import networks.computation.iteration.modes.BFS;
import networks.computation.iteration.modes.DFSstack;
import networks.computation.iteration.modes.Topologic;
import networks.computation.iteration.visitors.neurons.StandardNeuronVisitors;
import networks.computation.iteration.visitors.states.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

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
        StandardNeuronVisitors.Independent inval = new StandardNeuronVisitors.Independent(network, visitor);
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
