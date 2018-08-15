package pipelines.pipes.specific;

import constructs.template.Template;
import pipelines.Merge;
import training.NeuralModel;

import java.util.logging.Logger;

/**
 * TODO - use this in the pipelines at the output of training
 */
public class NeuralToTemplateMerge extends Merge<Template, NeuralModel, Template> {
    private static final Logger LOG = Logger.getLogger(NeuralToTemplateMerge.class.getName());

    public NeuralToTemplateMerge(String id) {
        super(id);
    }

    @Override
    protected Template merge(Template template, NeuralModel neural) {
        template.updateWeightsFrom(neural);
        return template;
    }

}
