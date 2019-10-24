package pipelines.building;

import constructs.building.ExamplesBuilder;
import constructs.building.QueriesBuilder;
import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import learning.Query;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;
import settings.Source;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SamplesProcessingBuilder extends AbstractPipelineBuilder<Source, Stream<LogicSample>> {
    private static final Logger LOG = Logger.getLogger(SamplesProcessingBuilder.class.getName());
    private Source source;

    public SamplesProcessingBuilder(Settings settings, Source source) {
        super(settings);
        this.source = source;
    }


    @Override
    public Pipeline<Source, Stream<LogicSample>> buildPipeline() {
        return buildPipeline(this.source);
    }

    public Pipeline<Source, Stream<LogicSample>> buildPipeline(Source source) {
        Pipeline<Source, Stream<LogicSample>> samplesProcessingPipeline = new Pipeline<>("SamplesProcessingPipeline", this);
        Pipe<Source, Stream<LogicSample>> sourcesSamplesPipe = samplesProcessingPipeline.registerStart(extractSamplesPipe(source));
        Pipe<Stream<LogicSample>, Stream<LogicSample>> samplesPostprocessPipe = samplesProcessingPipeline.registerEnd(postprocessSamplesPipe());
        sourcesSamplesPipe.connectAfter(samplesPostprocessPipe);
        return samplesProcessingPipeline;
    }

    @Deprecated
    public Pipe<Source, Stream<LiftedExample>> extractExamples(Source source) {
        return null;
    }

    @Deprecated
    public Pipe<Source, Stream<Query>> extractQueries(Source source) {
        return null;
    }


    public Pipe<Source, Stream<LogicSample>> extractSamplesPipe(Source source) {

        if (source.ExamplesReader == null && source.QueriesReader == null) {
            LOG.severe("No sources found to assemble Samples at construction");
        }

        Pipe<Source, Stream<LogicSample>> sampleExtractionPipe = null;

        if (source.QueriesSeparate && source.ExamplesSeparate) {
            sampleExtractionPipe = new Pipe<Source, Stream<LogicSample>>("QueriesWithExamplesPipe", settings) {
                @Override
                public Stream<LogicSample> apply(Source source) {
                    ExamplesBuilder examplesBuilder = new ExamplesBuilder(settings);
                    Stream<LogicSample> examples = examplesBuilder.buildSamplesFrom(examplesBuilder.parseTreeFrom(source.ExamplesReader));

                    QueriesBuilder queriesBuilder = new QueriesBuilder(settings);
                    queriesBuilder.setFactoriesFrom(examplesBuilder);   //todo check if this is indeed desirable
                    Stream<LogicSample> queries = queriesBuilder.buildSamplesFrom(queriesBuilder.parseTreeFrom(source.QueriesReader));

                    return queriesBuilder.merge2streams(queries, examples);
                }
            };
        } else if (!source.QueriesSeparate && source.QueriesProvided && source.ExamplesSeparate) {
            sampleExtractionPipe = new Pipe<Source, Stream<LogicSample>>("QueriesWithinExamplesPipe", settings) {
                @Override
                public Stream<LogicSample> apply(Source source) {
                    ExamplesBuilder examplesBuilder = new ExamplesBuilder(settings);
                    Stream<LogicSample> labeledExamples = examplesBuilder.buildSamplesFrom(examplesBuilder.parseTreeFrom(source.ExamplesReader));
                    return labeledExamples;
                }
            };
        } else if (!source.QueriesProvided && source.ExamplesSeparate) {
            sampleExtractionPipe = new Pipe<Source, Stream<LogicSample>>("UnsupervisedExamplesPipe", settings) {
                @Override
                public Stream<LogicSample> apply(Source source) {
                    ExamplesBuilder examplesBuilder = new ExamplesBuilder(settings);
                    Stream<LogicSample> unlabeledExamples = examplesBuilder.buildSamplesFrom(examplesBuilder.parseTreeFrom(source.ExamplesReader));
                    return unlabeledExamples;
                }
            };
        } else if (source.QueriesSeparate && !source.ExamplesProvided) {
            sampleExtractionPipe = new Pipe<Source, Stream<LogicSample>>("QueriesOnlyPipe", settings) {
                @Override
                public Stream<LogicSample> apply(Source source) {
                    QueriesBuilder queriesBuilder = new QueriesBuilder(settings);
                    return queriesBuilder.buildSamplesFrom(queriesBuilder.parseTreeFrom(source.QueriesReader));
                }
            };
        } else {
            LOG.severe("No sources found to assemble Samples at pipe construction");
            return null;
        }
        return sampleExtractionPipe;
    }


    public Pipe<Stream<LogicSample>, Stream<LogicSample>> postprocessSamplesPipe() {
        //todo for instance order by example id
        Pipe<Stream<LogicSample>, Stream<LogicSample>> postProcessPipe = new Pipe<Stream<LogicSample>, Stream<LogicSample>>("PostprocessSamplesPipe", settings) {
            @Override
            public Stream<LogicSample> apply(Stream<LogicSample> logicSampleStream) {
                if (settings.limitSamples > 0) {
                    LOG.warning("Limiting the learning samples to the first: " + settings.limitSamples);
                    logicSampleStream = logicSampleStream.limit(settings.limitSamples);
                }
                return logicSampleStream;
            }
        };
        return postProcessPipe;
    }
}