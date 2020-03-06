package networks.computation.training;

import constructs.template.Template;
import evaluation.values.Value;
import evaluation.values.distributions.ValueInitializer;
import learning.Model;
import networks.structure.components.neurons.QueryNeuron;
import networks.structure.components.weights.Weight;
import settings.Settings;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralModel implements Model<QueryNeuron> {    //todo now add accuracy progress plotting
    private static final Logger LOG = Logger.getLogger(NeuralModel.class.getName());

    public List<Weight> weights;
    private transient Settings settings;

    public Value threshold;

    /**
     * Only used in debug mode for drawing of original template during training.
     */
    private Template template;

    public NeuralModel(List<Weight> weights, Settings settings) {
        this.settings = settings;
        this.weights = weights;
//        this.weights = filterLearnable(weights);  //todo now SPEEDUP - reindex weights here so that the first N are only learnable, and so no further checks are necessary, as we have a continuous array in weightupdates
        if (settings.optimizer == Settings.OptimizerSet.ADAM) {
            init4Adam(weights);
        }
    }

    public NeuralModel(Template template, Settings settings) {  //todo next add debug option for neuralModel only - ie.e. printing out weights (nicely) only
        this(template.getAllWeights(), settings);
        if (settings.debugTemplateTraining) {
            this.template = template;
        }
    }

    private NeuralModel(Settings settings){
        this.settings = settings;
    }

    protected void init4Adam(List<Weight> weights) {
        for (Weight weight : weights) {
            weight.velocity = weight.value.getForm();
            weight.momentum = weight.value.getForm();
        }
    }

    /**
     * Be careful with this, as the returned weights are not bind in the neural structures, they can be used just to store values
     * @return
     */
    public NeuralModel cloneValues() {
        List<Weight> clonedWeights = weights.stream().map(Weight::clone).collect(Collectors.toList());
        NeuralModel clone = new NeuralModel(this.settings);
        clone.weights = clonedWeights;
        clone.template = this.template;
        return clone;
    }

    public void resetWeights(ValueInitializer valueInitializer) {
        for (Weight weight : weights) {
            weight.init(valueInitializer);
        }
    }

    /**
     * Restore weight Values from another model
     * @param otherModel
     */
    public void loadWeightValues(NeuralModel otherModel) {
        Map<Integer, Weight> otherWeights = otherModel.mapWeightsToIds();
        for (Weight weight : weights) {
            weight.value = otherWeights.get(weight.index).value;
        }
    }

    public void dropoutWeights() {
        //go through weights and set them randomly off
    }

    public List<Weight> filterLearnable(List<Weight> allWeights){
        return allWeights.stream().filter(Weight::isLearnable).collect(Collectors.toList());
    }

    /**
     * For external training, map all weights to UNIQUE integers
     *
     * @return
     */
    public Map<Integer, Weight> mapWeightsToIds() {
        return weights.stream().collect(Collectors.toMap(w -> w.index, w -> w));
    }

    /**
     * For external training, map all weights to, mostly unique, strings
     *
     * @return
     */
    public Map<String, Weight> mapWeightsToNames() {
        return weights.stream().collect(Collectors.toMap(w -> w.name, w -> w));
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
        return weights;
    }

    public Template getTemplate() {
        if (template == null){
            LOG.severe("No template was stored in this NeuralModel");
        }
        return template;
    }
}