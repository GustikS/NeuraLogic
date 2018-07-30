package pipeline.prepared.pipes;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import pipeline.Branch;
import training.NeuralModel;

import java.util.logging.Logger;

public class TemplateToNeuralBranch extends Branch<Template, Template, NeuralModel> {
    private static final Logger LOG = Logger.getLogger(TemplateToNeuralBranch.class.getName());

    public TemplateToNeuralBranch(String id) {
        super(id);
    }

    @Override
    protected Pair<Template, NeuralModel> branch(Template template) {
        //TODO
        NeuralModel neuralModel = new NeuralModel(template.getAllWeights());
        return new Pair<>(template, neuralModel);
    }
}