package pipelines.building;

import constructs.building.TemplateBuilder;
import constructs.template.Template;
import constructs.template.transforming.MetadataProcessor;
import constructs.template.transforming.TemplateReducing;
import constructs.template.types.GraphTemplate;
import constructs.template.types.ParsedTemplate;
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
            Pipe<ParsedTemplate, Template> nextPipe;
            if (settings.processMetadata) {
                nextPipe = pipeline.registerEnd(processMetadata());
                sourcesTemplatePipe.connectAfter(nextPipe);
            } else {
                nextPipe = pipeline.registerEnd(new Pipe<ParsedTemplate, Template>("SkippingMetadataPipe") {
                    @Override
                    public Template apply(ParsedTemplate parsedTemplate) {
                        return parsedTemplate;
                    }
                });
                sourcesTemplatePipe.connectAfter(nextPipe);
            }
            Pipe<?, Template> nextPipe1 = null;
            if (settings.reduceTemplate) {
                Pipe<Template, GraphTemplate> graphTemplatePipe = pipeline.register(buildTemplateGraph());
                nextPipe.connectAfter(graphTemplatePipe);
                Pipe<GraphTemplate, Template> reduceTemplatePipe = reduceTemplate();
                graphTemplatePipe.connectAfter(pipeline.registerEnd(reduceTemplatePipe));
                nextPipe1 = reduceTemplatePipe;
            }
            if (settings.inferTemplateFacts) {
                Pipe<Template, Template> inferencePipe = pipeline.registerEnd(inferFacts());
                nextPipe1.connectAfter(inferencePipe);
                nextPipe1 = inferencePipe;
            }
            return pipeline;
        } else {
            LOG.warning("Template extraction from sources requested but no template provided.");
            return null;
        }
    }

    protected Pipe<Template, Template> inferFacts() {
        return new Pipe<Template, Template>("TemplateInferencePipe") {
            @Override
            public Template apply(Template template) {
                template.inferTemplateFacts();
                return template;
            }
        };
    }

    protected Pipe<GraphTemplate, Template> reduceTemplate() {
        templateReducer = TemplateReducing.getReducer(settings);
        return new Pipe<GraphTemplate, Template>("TemplateReducingPipe") {
            @Override
            public Template apply(GraphTemplate template) {
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

    protected Pipe<Template, GraphTemplate> buildTemplateGraph() {
        return new Pipe<Template, GraphTemplate>("BuildTemplateGraphPipe") {
            @Override
            public GraphTemplate apply(Template template) {
                GraphTemplate graphTemplate = new GraphTemplate(template);
                return graphTemplate;
            }
        };
    }
}