package pipelines.bulding;

import constructs.building.TemplateBuilder;
import constructs.template.Template;
import constructs.template.templates.ParsedTemplate;
import constructs.template.transforming.MetadataProcessor;
import constructs.template.transforming.TemplateReducing;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;

public class TemplateProcessingBuilder extends AbstractPipelineBuilder<Sources, Template> {
    private static final Logger LOG = Logger.getLogger(TemplateProcessingBuilder.class.getName());
    private final Sources sources;

    TemplateBuilder templateBuilder;

    MetadataProcessor metadataProcessor;
    TemplateReducing templateReducer;

    public TemplateProcessingBuilder(Settings settings, Sources sources) {
        super(settings);
        templateBuilder = new TemplateBuilder(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, Template> buildPipeline() {
        Pipeline<Sources, Template> pipeline = new Pipeline<>("TemplateProcessingPipeline");
        if (sources.templateProvided) {
            Pipe<Sources, ParsedTemplate> sourcesTemplatePipe = pipeline.registerStart(extractTemplate(sources));
            if (settings.processMetadata) {
                Pipe<ParsedTemplate, Template> metadataPipe = pipeline.registerEnd(processMetadata());
                sourcesTemplatePipe.connectAfter(metadataPipe);
            }
            if (settings.reduceTemplate) {
                Pipe<Template, Template> reduceTemplatePipe = reduceTemplate();
                pipeline.terminal.connectAfter(pipeline.registerEnd(reduceTemplatePipe)); //todo check if correct end of pipeline
            }
            //TODO rest of template transformations
            return pipeline;
        } else {
            LOG.warning("Template extraction from sources requested but no template provided.");
            return null;
        }
    }

    protected Pipe<Template, Template> reduceTemplate() {
        templateReducer = TemplateReducing.getReducer(settings);
        return new Pipe<Template, Template>("TemplateReducingPipe") {
            @Override
            public Template apply(Template template) {
                return templateReducer.reduce(template);
            }
        };
    }

    protected Pipe<ParsedTemplate, Template> processMetadata() {
        metadataProcessor = new MetadataProcessor(settings);
        return new Pipe<ParsedTemplate, Template>("MetadataProcessingPipe") {
            @Override
            public Template apply(ParsedTemplate template) {
                return metadataProcessor.processMetadata(template);
            }
        };
    }

    public Pipe<Sources, ParsedTemplate> extractTemplate(Sources sources) {
        if (!sources.templateProvided) {
            LOG.severe("No template provided yet required.");
            return null;
        }
        Pipe<Sources, ParsedTemplate> pipe = new Pipe<Sources, ParsedTemplate>("TemplateExtractionPipe") {
            @Override
            public ParsedTemplate apply(Sources sources) {
                return templateBuilder.buildTemplateFrom(sources.templateReader);
            }
        };
        return pipe;
    }
}