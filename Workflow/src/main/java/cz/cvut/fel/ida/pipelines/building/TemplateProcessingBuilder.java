package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.logic.constructs.building.TemplateBuilder;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.transforming.MetadataProcessor;
import cz.cvut.fel.ida.logic.constructs.template.transforming.TemplateReducing;
import cz.cvut.fel.ida.logic.constructs.template.types.GraphTemplate;
import cz.cvut.fel.ida.logic.constructs.template.types.ParsedTemplate;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.debugging.TemplateDebugger;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;

import java.io.IOException;
import java.io.ObjectInputStream;
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
        Pipeline<Sources, Template> pipeline = new Pipeline<>("TemplateProcessingPipeline", this);

        if (sources.binaryTemplateStream != null) {
            Pipe<Sources, Template> pipe = pipeline.registerStart(new Pipe<Sources, Template>("LoadingBinaryTemplatePipe") {
                @Override
                public Template apply(Sources sources) throws IOException, ClassNotFoundException {
                    ObjectInputStream in = new ObjectInputStream(sources.binaryTemplateStream);
                    Template tmp = (Template) in.readObject();
                    return tmp;
                }
            });
            pipeline.registerEnd(pipe);
        } else if (sources.templateProvided) {
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
            Pipe<?, Template> nextPipe1 = nextPipe;
            if (settings.checkStratification){
                Pipe<Template, Template> stratificationPipe = pipeline.registerEnd(checkStratificationPipe());
                nextPipe1.connectAfter(stratificationPipe);
                nextPipe1 = stratificationPipe;
            }
            if (settings.reduceTemplate) {
                Pipe<Template, GraphTemplate> graphTemplatePipe = pipeline.register(buildTemplateGraph());
                nextPipe.connectAfter(graphTemplatePipe);
                Pipe<GraphTemplate, Template> reduceTemplatePipe = reduceTemplate();
                graphTemplatePipe.connectAfter(pipeline.registerEnd(reduceTemplatePipe));
                nextPipe1 = reduceTemplatePipe;
            }
            if (settings.preprocessTemplateInference) {
                Pipe<Template, Template> inferencePipe = pipeline.registerEnd(preprocessInference());
                nextPipe1.connectAfter(inferencePipe);
                nextPipe1 = inferencePipe;
            }
            if (settings.debugTemplate) {
                new TemplateDebugger(settings).addDebugElement(pipeline);
            }
        } else {
            LOG.warning("Template extraction from sources requested but no template provided.");
            return null;
        }
        return pipeline;
    }

    private Pipe<Template, Template> checkStratificationPipe() {
        return new Pipe<Template, Template>("CheckStratificationPipe") {
            @Override
            public Template apply(Template template) {
                if (template.containsNegation) {
                    GraphTemplate graphTemplate = new GraphTemplate(template);
                    graphTemplate.new Stratification(graphTemplate).check();
                    return graphTemplate;
                }
                return template;
            }
        };
    }

    protected Pipe<Template, Template> preprocessInference() {
        return new Pipe<Template, Template>("TemplateInferencePipe") {
            @Override
            public Template apply(Template template) {
                template.preprocessInference(true);
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
                return templateBuilder.buildTemplateFrom(sources.getTemplateReader());
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