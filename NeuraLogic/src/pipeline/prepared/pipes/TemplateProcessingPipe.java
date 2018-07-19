package pipeline.prepared.pipes;

import constructs.template.Template;
import constructs.template.transforming.MetadataProcessor;
import pipeline.Pipe;
import settings.Settings;

import java.util.logging.Logger;

public class TemplateProcessingPipe extends Pipe<Template, Template> {
    private static final Logger LOG = Logger.getLogger(TemplateProcessingPipe.class.getName());


    MetadataProcessor metadataProcessor;
    Settings settings;

    protected TemplateProcessingPipe(String id) {
        super(id);
    }

    public TemplateProcessingPipe(Settings settings, String id) {
        super(id);
        this.settings = settings;
    }

    @Override
    public Template apply(Template template) {
        template = processMetadata(template);
        if (settings.reduceTemplate) template = settings.templateReducer.reduce(template);
        return template;
    }

    public Template processMetadata(Template template) {
        return metadataProcessor.processMetadata(template);
    }
}