package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.pipelines.debugging.TrainingDebugger;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class TemplateToNeuralPipe extends Pipe<Template, NeuralModel> {
    private static final Logger LOG = Logger.getLogger(TemplateToNeuralPipe.class.getName());

    public TemplateToNeuralPipe() {
        super("TemplateToNeuralPipe");
    }

    public TemplateToNeuralPipe(String id) {
        super(id);
    }

    public TemplateToNeuralPipe(Settings settings) {
        this();
        this.settings = settings;
    }

    @Override
    public NeuralModel apply(Template template) {
        TrainingDebugger trainingDebugger = new TrainingDebugger(this.settings, template);
        return new NeuralModel(template.getAllWeights(), trainingDebugger::debugWeights, this.settings);
    }
}