package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.pipelines.Merge;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;

/**
 * TODO - use this in the pipelines at the output of training
 */
public class TrainingResultTemplateMerge extends Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>> {
    private static final Logger LOG = Logger.getLogger(TrainingResultTemplateMerge.class.getName());

    public TrainingResultTemplateMerge() {
        super("TrainingResultTemplateMerge", null);
    }

    protected Pair<Pair<Template, NeuralModel>, Progress> merge(Template input1, Pair<NeuralModel, Progress> input2) {
        input1.updateWeightsFrom(input2.r.mapWeightsToIds());
        return new Pair<>(new Pair<>(input1, input2.r), input2.s);
    }

}
