package pipelines.pipes.specific;

import constructs.template.Template;
import networks.computation.evaluation.results.Progress;
import networks.computation.training.NeuralModel;
import pipelines.Merge;
import utils.generic.Pair;

import java.util.logging.Logger;

/**
 * TODO - use this in the pipelines at the output of training
 */
public class TrainingResultTemplateMerge extends Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>> {
    private static final Logger LOG = Logger.getLogger(TrainingResultTemplateMerge.class.getName());

    public TrainingResultTemplateMerge() {
        super("TrainingResultTemplateMerge");
    }

    protected Pair<Pair<Template, NeuralModel>, Progress> merge(Template input1, Pair<NeuralModel, Progress> input2) {
        input1.updateWeightsFrom(input2.r);
        return new Pair<>(new Pair<>(input1, input2.r), input2.s);
    }

}
