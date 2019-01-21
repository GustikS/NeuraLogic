package networks.computation.iteration;

import networks.computation.iteration.actions.StateVisiting;
import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.Neuron;
import networks.structure.components.types.TopologicNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.logging.Logger;

public class IndependentNeuronProcessing {
    private static final Logger LOG = Logger.getLogger(IndependentNeuronProcessing.class.getName());

    Settings settings;
    StateVisiting.ComputationVisitor visitor;

    public IndependentNeuronProcessing(Settings settings, StateVisiting.ComputationVisitor visitor) {
         this.settings = settings;
         this.visitor = visitor;
    }


    private IterationStrategy getIterationStrategy(NeuralNetwork network, Neuron outputNeuron) {
        StandardNeuronVisitors.Independent inval = new StandardNeuronVisitors.Independent(network, visitor);
        if (network instanceof TopologicNetwork) {
            return new Topologic((TopologicNetwork<State.Neural.Structure>) network).new BUpIterator(outputNeuron, inval);
        } else {
            return new DFSstack(network, inval).new TDownIterator(outputNeuron);
        }
    }

    public void process(NeuralNetwork network, Neuron outputNeuron) {
        IterationStrategy iterationStrategy = getIterationStrategy(network, outputNeuron);
        iterationStrategy.iterate();
    }

}
