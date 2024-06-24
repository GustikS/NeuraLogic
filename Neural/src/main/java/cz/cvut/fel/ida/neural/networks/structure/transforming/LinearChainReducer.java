package cz.cvut.fel.ida.neural.networks.structure.transforming;

import cz.cvut.fel.ida.algebra.functions.transformation.joint.Identity;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AggregationNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Timing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LinearChainReducer implements NetworkReducing {
    private static final Logger LOG = Logger.getLogger(LinearChainReducer.class.getName());
    private transient final Settings settings;

    int allNeurons = 0;
    int prunedNeurons = 0;

    Timing timing;


    public LinearChainReducer(Settings settings) {
        this.settings = settings;
        this.timing = new Timing();
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Structure> inet, List<QueryNeuron> outputs) {
        timing.tic();
        int prunings = 0;
        int sizeBefore = inet.allNeuronsTopologic.size();
        for (int i = sizeBefore - 1; i >= 0; i--) {
            BaseNeuron<Neurons, State.Neural> neuron = inet.allNeuronsTopologic.get(i);
            if (!settings.pruneEvenWeightedNeurons && neuron instanceof WeightedNeuron)
                continue;
            if (settings.pruneOnlyIdentities && !(neuron instanceof AggregationNeuron) && !(neuron.getTransformation() instanceof Identity)) {
                continue;
            }
            if (neuron.getTransformation() != null && neuron.getTransformation().changesShape()) {
                continue;
            }

            boolean pruned = prune(inet, neuron);
            if (pruned) {
                prunings++;
            }
        }
        List<Neurons> collect = outputs.stream().map(s -> s.neuron).collect(Collectors.toList());
        NetworkReducing.supervisedNetReconstruction(inet, collect);    //lastly remove all the dead (pruned) neurons by building a new topologic sort starting from output neuron
        int sizeAfter = inet.allNeuronsTopologic.size();
//        LOG.info(inet.toString());

        allNeurons += sizeBefore;
        prunedNeurons += sizeAfter;
        LOG.info("LinearChainPruning reduced neurons from " + sizeBefore + " down to " + sizeAfter + " with prunings: " + prunings);

        timing.toc();
        return inet;
    }

    @Override
    public NeuralNetwork reduce(DetailedNetwork<State.Neural.Structure> inet, QueryNeuron outputNeuron) {
        return reduce(inet, Arrays.asList(outputNeuron));
    }

    @Override
    public void finish() {
        timing.finish();
    }

    private boolean prune(DetailedNetwork<State.Neural.Structure> inet, BaseNeuron<Neurons, State.Neural> middle) {
        List<Neurons> middleInputNeurons = new ArrayList<>();
        Iterator<Neurons> inputs = inet.getInputs(middle);
        inputs.forEachRemaining(middleInputNeurons::add);
        if (middleInputNeurons.size() == 1) {  //detected linear chain parent -> middle -> child
            BaseNeuron<Neurons, State.Neural> child = (BaseNeuron<Neurons, State.Neural>) middleInputNeurons.get(0);
            Iterator<Neurons> parents = inet.getOutputs(middle);
            if (parents == null) {
                LOG.fine("Neuron has only 1 input but has no output, thus not pruning it (i.e., an output neuron).");
                return false;
            }

            List<Neurons> middleOutputNeurons = new ArrayList<>();
            parents.forEachRemaining(middleOutputNeurons::add);

            for (Neurons parent : middleOutputNeurons) {

                inet.replaceInput((BaseNeuron<Neurons, State.Neural>) parent, middle, child);

                if (middleOutputNeurons.size() > 1) {   // if there are more outputs of the middle neuron, we cannot simply remove middle, but must add the current child -> parent
                    inet.outputMapping.get(middle).removeLink(parent);
                    inet.outputMapping.get(child).addLink(parent);
                } else {    // if middle has but a single output, we can safely skip it completely and replace also child -> parent
                    inet.replaceOutput(child, middle, parent);
                }
            }
//            System.out.println("pruning: " + middle);
            return true;
        }
        return false;
    }

    private void prune(DetailedNetwork<State.Neural.Structure> inet, WeightedNeuron<Neurons, State.Neural> middle) {
        //void - weighted input edges should not be pruned as they might change functionality still
        LOG.warning("Trying to prune weighted neuron input");
    }

}
