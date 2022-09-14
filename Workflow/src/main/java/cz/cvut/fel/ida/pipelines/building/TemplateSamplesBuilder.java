package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.pipelines.Merge;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.pipes.generic.DuplicateBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.LambdaPipe;
import cz.cvut.fel.ida.pipelines.pipes.generic.PairMerge;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Source;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

//import cz.cvut.fel.ida.pipelines.Pipe;

public class TemplateSamplesBuilder extends AbstractPipelineBuilder<Sources, Pair<Template, Stream<LogicSample>>> {
    private static final Logger LOG = Logger.getLogger(TemplateSamplesBuilder.class.getName());
    private Sources sources;
    private Template template;

    public TemplateSamplesBuilder(Sources sources, Settings settings) {
        super(settings);
        this.sources = sources;
    }

    public TemplateSamplesBuilder(Sources sources, Settings settings, Template template) {
        this(sources, settings);
        this.template = template;
    }

    public Pipeline<Sources, Pair<Template, Stream<LogicSample>>> buildPipeline() {
        Pipeline<Sources, Pair<Template, Stream<LogicSample>>> pipeline = new Pipeline<>("buildFromSourcesPipeline", settings);

        Pipe<Sources, Source> getSource = null;
        SamplesProcessingBuilder samplesProcessor = null;

        if (sources.trainOnly || sources.trainTest || sources.drawing) {
            getSource = pipeline.register(new LambdaPipe<Sources, Source>("getTrainSourcePipe", sources -> sources.train, settings));
            samplesProcessor = new SamplesProcessingBuilder(settings, sources.train);
        } else if (sources.testOnly) {
            getSource = pipeline.register(new LambdaPipe<Sources, Source>("getTestSourcePipe", sources -> sources.test, settings));
            samplesProcessor = new SamplesProcessingBuilder(settings, sources.test);
        } else {
            LOG.severe("Unrecognized train/test mode");
            throw new UnsupportedOperationException();
        }

        Pipeline<Source, Stream<LogicSample>> getLogicSampleStream = pipeline.register(samplesProcessor.buildPipeline());
        getSource.connectAfter(getLogicSampleStream);

//        if (!sources.templateProvided) {
//            LOG.severe("The template must be provided in the simplified buildFromSource mode.");
//            throw new UnsupportedOperationException();
//        }

        DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>("DuplicateSourcesBranch"));

        PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.registerEnd(new PairMerge<>("TemplateSamplesMerge"));

        if (this.template == null) {
            Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(getSourcesTemplatePipeline(sources, settings));
            duplicateBranch.connectAfterL(sourcesTemplatePipeline);
            pairMerge.connectBeforeL(sourcesTemplatePipeline);
        } else {
            LambdaPipe<Sources, Template> sourcesTemplatePipeline = pipeline.register(
                    new LambdaPipe<>("TemplateIdentityPipe", s -> this.template, settings)
            );
            duplicateBranch.connectAfterL(sourcesTemplatePipeline);
            pairMerge.connectBeforeL(sourcesTemplatePipeline);
        }

        if (sources.val != null && (sources.val.QueriesProvided || sources.val.ExamplesProvided)) {  //merge the training samples with validation samples while marking the validation samples as ValidationOnly
            DuplicateBranch<Sources> trainValSamplesBranch = pipeline.register(new DuplicateBranch<>("TrainValSamplesBranch"));
            duplicateBranch.connectAfterR(trainValSamplesBranch);
            trainValSamplesBranch.connectAfterL(getSource);

            Pipe<Sources, Source> getVal = pipeline.register(new LambdaPipe<Sources, Source>("getValSourcePipe", sources -> sources.val, settings));
            SamplesProcessingBuilder valSamplesProcessor = new SamplesProcessingBuilder(settings, sources.val);
            Pipeline<Source, Stream<LogicSample>> getValSampleStream = pipeline.register(valSamplesProcessor.buildPipeline());
            getVal.connectAfter(getValSampleStream);

            trainValSamplesBranch.connectAfterR(getVal);

            Merge<Stream<LogicSample>, Stream<LogicSample>, Stream<LogicSample>> trainValMerge = pipeline.register(new Merge<Stream<LogicSample>, Stream<LogicSample>, Stream<LogicSample>>("trainValMerge", settings) {
                @Override
                protected Stream<LogicSample> merge(Stream<LogicSample> train, Stream<LogicSample> val) {
                    Stream<LogicSample> valStream = val.map(sample -> {
                        sample.type = LearningSample.Split.VALIDATION;
                        return sample;
                    });
                    return Stream.concat(train, valStream);
                }
            });
            trainValMerge.connectBeforeL(getLogicSampleStream);
            trainValMerge.connectBeforeR(getValSampleStream);

            pairMerge.connectBeforeR(trainValMerge);

        } else {
            duplicateBranch.connectAfterR(getSource);
            pairMerge.connectBeforeR(getLogicSampleStream);
        }

        return pipeline;
    }

    public Pipeline<Sources, Template> getSourcesTemplatePipeline(Sources sources, Settings settings) {
        TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);
        return templateProcessor.buildPipeline();
    }

}
