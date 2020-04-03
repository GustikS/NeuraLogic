package cz.cvut.fel.ida.neural.networks.computation.training;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.inits.ValueInitializer;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.learning.Model;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.setup.Settings;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralModel implements Model<QueryNeuron> {
    private static final Logger LOG = Logger.getLogger(NeuralModel.class.getName());

    private transient Settings settings;

    public final List<Weight> allWeights;
    public final int maxWeightIndex;
    public transient final List<Weight> learnableWeights;

    public Value threshold;

    /**
     * Only used in debug mode for drawing of original template during training.
     */
    public Consumer<Map<Integer, Weight>> templateDebugCallback;

    public NeuralModel(List<Weight> weights, Settings settings) {
        this.settings = settings;
        this.allWeights = weights;
        this.learnableWeights = filterLearnable(weights);
        if (settings.getOptimizer() == Settings.OptimizerSet.ADAM) {
            init4Adam(weights);
        }
        maxWeightIndex = allWeights.size()-1;
    }

    public NeuralModel(List<Weight> weights, Consumer<Map<Integer, Weight>> templateUpdateCallback, Settings settings) {  //todo next add debug option for neuralModel only - ie.e. printing out weights (nicely) only
        this(weights, settings);
        if (settings.debugTemplateTraining) {
            this.templateDebugCallback = templateUpdateCallback;
        }
    }

    protected void init4Adam(List<Weight> weights) {
        for (Weight weight : weights) {
            weight.velocity = weight.value.getForm();
            weight.momentum = weight.value.getForm();
        }
    }

    /**
     * Be careful with this, as the returned weights are not bind in the neural structures, they can be used just to store values
     *
     * @return
     */
    public NeuralModel cloneValues() {
        List<Weight> clonedWeights = allWeights.stream().map(Weight::clone).collect(Collectors.toList());
        NeuralModel clone = new NeuralModel(clonedWeights, this.settings);
//        clone.template = this.template;
        return clone;
    }

    public void resetWeights(ValueInitializer valueInitializer) {
        for (Weight weight : allWeights) {
            weight.init(valueInitializer);
        }
    }

    /**
     * Restore weight Values from another model
     *
     * @param otherModel
     */
    public void loadWeightValues(NeuralModel otherModel) {
        Map<Integer, Weight> otherWeights = otherModel.mapWeightsToIds();
        for (Weight weight : allWeights) {
            weight.value = otherWeights.get(weight.index).value;
        }
    }

    public void dropoutWeights() {
        //go through weights and set them randomly off
    }

    public static List<Weight> filterLearnable(List<Weight> allWeights) {
        return allWeights.stream().filter(Weight::isLearnable).collect(Collectors.toList());
    }

    /**
     * For external training, map all weights to UNIQUE integers
     *
     * @return
     */
    public Map<Integer, Weight> mapWeightsToIds() {
        return allWeights.stream().collect(Collectors.toMap(w -> w.index, w -> w));
    }

    /**
     * For external training, map all weights to, mostly unique, strings
     *
     * @return
     */
    public Map<String, Weight> mapWeightsToNames() {
        return allWeights.stream().collect(Collectors.toMap(w -> w.name, w -> w));
    }

    /**
     * Load weight values from unique strings after external training
     *
     * @param tensorflow
     */
    public void importWeights(Reader tensorflow, Map<String, Weight> mapping) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate(QueryNeuron query) {
        return null;
    }


    @Override
    public List<Weight> getAllWeights() {
        return allWeights;
    }
//
//
//    private Template getTemplate() {
//        if (template == null) {
//            LOG.severe("No template was stored in this NeuralModel");
//        }
//        return template;
//    }
}