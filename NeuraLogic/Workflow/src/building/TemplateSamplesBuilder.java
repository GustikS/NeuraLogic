package building;

import constructs.example.LogicSample;
import constructs.template.Template;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.bulding.AbstractPipelineBuilder;
import pipelines.pipes.generic.DuplicateBranch;
import pipelines.pipes.generic.PairMerge;
import settings.Settings;
import settings.Source;
import settings.Sources;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TemplateSamplesBuilder extends AbstractPipelineBuilder<Sources, Pair<Template, Stream<LogicSample>>> {
    private static final Logger LOG = Logger.getLogger(TemplateSamplesBuilder.class.getName());
    private Sources sources;

    public TemplateSamplesBuilder(Sources sources, Settings settings) {
        super(settings);
        this.sources = sources;
    }

    public Pipeline<Sources, Pair<Template, Stream<LogicSample>>> buildPipeline() {
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> pipeline = new Pipeline<>("buildFromSources", settings);

        Pipe<Sources, Source> getSource = null;
        SamplesProcessingBuilder samplesProcessor = null;

        if (sources.trainOnly) {
            getSource = pipeline.register(new Pipe<Sources, Source>("getTrainSourcePipe") {
                @Override
                public Source apply(Sources sources) {
                    return sources.train;
                }
            });
            samplesProcessor = new SamplesProcessingBuilder(settings, sources.train);

        } else if (sources.testOnly) {
            getSource = pipeline.register(new Pipe<Sources, Source>("getTestSourcePipe") {
                @Override
                public Source apply(Sources sources) {
                    return sources.test;
                }
            });
            samplesProcessor = new SamplesProcessingBuilder(settings, sources.test);
        } else {
            LOG.severe("Can only ground a single source (train/test) at a time");
            throw new UnsupportedOperationException();
        }

        Pipeline<Source, Stream<LogicSample>> getLogicSampleStream = pipeline.register(samplesProcessor.buildPipeline());
        getSource.connectAfter(getLogicSampleStream);

        if (!sources.templateProvided) {
            LOG.severe("The template must be provided in the simplified buildFromSource mode.");
            throw new UnsupportedOperationException();
        }

        DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>("TemplateSamplesBranch"));
        duplicateBranch.connectAfterL(getSource);

        Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(getSourcesTemplatePipeline(sources, settings));

        PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.registerEnd(new PairMerge<>("TemplateSamplesMerge"));

        duplicateBranch.connectAfterR(sourcesTemplatePipeline);
        pairMerge.connectBeforeL(sourcesTemplatePipeline);
        pairMerge.connectBeforeR(getLogicSampleStream);
        return pipeline;
    }

    public Pipeline<Sources, Template> getSourcesTemplatePipeline(Sources sources, Settings settings) {
        TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
        return templateProcessor.buildPipeline();
    }

}
