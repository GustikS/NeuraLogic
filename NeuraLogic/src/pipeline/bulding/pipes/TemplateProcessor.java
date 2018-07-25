package pipeline.bulding.pipes;

import constructs.building.TemplateBuilder;
import constructs.template.Template;
import constructs.template.transforming.MetadataProcessor;
import constructs.template.transforming.TemplateReducing;
import pipeline.Pipe;
import pipeline.Pipeline;
import pipeline.bulding.AbstractPipelineBuilder;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;

public class TemplateProcessor extends AbstractPipelineBuilder<Sources, Template> {
    private static final Logger LOG = Logger.getLogger(TemplateProcessor.class.getName());
    private final Settings settings;

    TemplateBuilder templateBuilder;

    MetadataProcessor metadataProcessor;
    TemplateReducing templateReducer;

    public TemplateProcessor(Settings settings) {
        super(settings);
        this.settings = settings;
        templateBuilder = new TemplateBuilder(settings);
    }

    @Override
    public Pipeline<Sources, Template> buildPipeline(Sources sources) {
        Pipeline<Sources, Template> templateProcessingPipeline = new Pipeline<>("TemplateProcessingPipeline");
        if (sources.templateProvided) {
            Pipe<Sources, Template> sourcesTemplatePipe = templateProcessingPipeline.register(extractTemplate(sources));
            Pipe<Template, Template> templateTemplatePipe = templateProcessingPipeline.register(postProcessTemplate());
            sourcesTemplatePipe.connectAfter(templateTemplatePipe);
            return templateProcessingPipeline;
        } else {
            LOG.warning("Template extraction from sources requested but no template provided.");
            return null;
        }
    }

    public Pipe<Sources, Template> extractTemplate(Sources sources) {
        if (!sources.templateProvided) {
            LOG.severe("No template provided yet required.");
            return null;
        }
        Pipe<Sources, Template> pipe = new Pipe<Sources, Template>("TemplateExtractionPipe") {
            @Override
            public Template apply(Sources sources) {
                return templateBuilder.buildFrom(sources.templateParseTree);

            }
        };
        return pipe;
    }

    public Pipe<Template, Template> postProcessTemplate() {
        Pipe<Template, Template> pipe = new Pipe<Template, Template>("TemplatePostprocessingPipe") {
            @Override
            public Template apply(Template template) {
                if (settings.processMetadata) {
                    template = metadataProcessor.processMetadata(template);
                }
                if (settings.reduceTemplate) {
                    templateReducer = TemplateReducing.getReducer(settings);
                    template = templateReducer.reduce(template);
                }
                //TODO rest of template transformations
                return template;
            }
        };
        return pipe;
    }
}