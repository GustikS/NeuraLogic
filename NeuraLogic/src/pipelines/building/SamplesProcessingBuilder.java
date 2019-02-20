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
        Pipeline<Source, Stream<LogicSample>> samplesProcessingPipeline = new Pipeline<>("SamplesProcessingPipeline");
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

        if (source.ExamplesReader != null || source.QueriesReader != null) {
            return new Pipe<Source, Stream<LogicSample>>("SamplesExtractionPipe") {
                @Override
                public Stream<LogicSample> apply(Source source) {
                    if (source.QueriesSeparate && source.ExamplesProvided) {    //todo next move the if's outside to pipeline creation
                        ExamplesBuilder examplesBuilder = new ExamplesBuilder(settings);
                        Stream<LogicSample> examples = examplesBuilder.buildSamplesFrom(examplesBuilder.parseTreeFrom(source.ExamplesReader));

                        QueriesBuilder queriesBuilder = new QueriesBuilder(settings);
                        queriesBuilder.setFactoriesFrom(examplesBuilder);   //TODO check if this is indeed desirable
                        Stream<LogicSample> queries = queriesBuilder.buildSamplesFrom(queriesBuilder.parseTreeFrom(source.QueriesReader));

                        return queriesBuilder.merge2streams(queries, examples);
                    } else if (!source.QueriesSeparate && source.ExamplesProvided) {
                        ExamplesBuilder examplesBuilder = new ExamplesBuilder(settings);
                        return examplesBuilder.buildSamplesFrom(examplesBuilder.parseTreeFrom(source.ExamplesReader));
                    } else if (source.QueriesSeparate && !source.ExamplesProvided) {
                        QueriesBuilder queriesBuilder = new QueriesBuilder(settings);
                        return queriesBuilder.buildSamplesFrom(queriesBuilder.parseTreeFrom(source.QueriesReader));
                    } else {
                        LOG.severe("No sources found to assemble Samples at pipe run");
                        return null;
                    }
                }
            };
        } else {
            LOG.severe("No sources found to assemble Samples at construction");
            return null;
        }
    }


    public Pipe<Stream<LogicSample>, Stream<LogicSample>> postprocessSamplesPipe() {
        //TODO for instance order by example id
        return null;
    }
}