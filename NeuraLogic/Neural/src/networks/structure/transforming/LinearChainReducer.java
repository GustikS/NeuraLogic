package networks.structure.transforming;

import networks.structure.components.NeuralNetwork;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.neurons.Neurons;
import networks.structure.components.neurons.WeightedNeuron;
import networks.structure.components.neurons.types.AggregationNeuron;
import networks.structure.components.neurons.types.AtomNeurons;
import networks.structure.components.types.DetailedNetwork;
import networks.structure.metadata.states.State;
import settings.Settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class LinearChainReducer implements NetworkReducing {
    private static final Logger LOG = Logger.getLogger(LinearChainReducer.class.getName());
    private final Settings settings;

    public LinearChainReducer(Settings settings) {
        this.settings = settings;
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Neural.Structure> inet, AtomNeurons<State.Neural> outputNeuron) {
        List<Neurons> middleInputNeurons = new ArrayList<>();
        Iterator<AggregationNeuron> inputs = inet.getInputs(outputNeuron);
        inputs.forEachRemaining(middleInputNeurons::add);
        if (middleInputNeurons.size() == 1) {
            LOG.warning("Output neuron has only 1 input! (should be pruned? Skipping pruning for this neuron however)");
        }
        int sizeBefore = inet.allNeuronsTopologic.size();
        for (int i = sizeBefore - 1; i >= 0; i--) {
            BaseNeuron<Neurons, State.Neural> neuron = inet.allNeuronsTopologic.get(i);
            if (!settings.pruneEvenWeightedNeurons && neuron instanceof WeightedNeuron)
                continue;
            prune(inet, neuron);
        }
        NetworkReducing.supervisedNetPruning(inet, (BaseNeuron) outputNeuron);    //lastly remove all the dead (pruned) neurons by building a new topologic sort starting from output neuron
        int sizeAfter = inet.allNeuronsTopologic.size();
//        LOG.info(inet.toString());
        LOG.info("LinearChainPruning reduced neurons from " + sizeBefore + " down to " + sizeAfter);
        return inet;
    }

    private void prune(DetailedNetwork<State.Neural.Structure> inet, BaseNeuron<Neurons, State.Neural> middle) {
        List<Neurons> middleInputNeurons = new ArrayList<>();
        Iterator<Neurons> inputs = inet.getInputs(middle);
        inputs.forEachRemaining(middleInputNeurons::add);
        if (middleInputNeurons.size() == 1) {  //detected linear chain parent -> middle -> child
            BaseNeuron<Neurons, State.Neural> child = (BaseNeuron<Neurons, State.Neural>) middleInputNeurons.get(0);
            Iterator<Neurons> parents = inet.getOutputs(middle);
            if (parents == null) {
                LOG.warning("Neuron has only 1 input but has no output, thus not pruning it.");
                return;
            }
            parents.forEachRemaining(parent -> {
                inet.replaceInput((BaseNeuron<Neurons, State.Neural>) parent, middle, child);
                inet.replaceOutput(child, middle, parent);
            });
        }
    }

    private void prune(DetailedNetwork<State.Neural.Structure> inet, WeightedNeuron<Neurons, State.Neural> middle) {
        //void - weighted input edges should not be pruned as they might change functionality still
        LOG.warning("Trying to prune weighted neuron input");
    }

}
