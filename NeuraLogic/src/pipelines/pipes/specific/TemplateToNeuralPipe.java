package pipelines.pipes.specific;

import constructs.template.Template;
import networks.computation.training.NeuralModel;
import pipelines.Pipe;
import settings.Settings;

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
        //TODO
        NeuralModel neuralModel = new NeuralModel(template.getAllWeights(), this.settings);
        return neuralModel;
    }
}