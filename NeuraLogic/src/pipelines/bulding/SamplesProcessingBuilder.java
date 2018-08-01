package pipelines.bulding;

import constructs.building.SamplesBuilder;
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

    SamplesBuilder samplesBuilder;
    private Source source;

    public SamplesProcessingBuilder(Settings settings, Source sources) {
        super(settings);
        this.source = sources;
        samplesBuilder = new constructs.building.SamplesBuilder(settings);
    }

    public Pipe<Source, Stream<LiftedExample>> extractExamples(Source sources) {
        return null;
    }

    public Pipe<Source, Stream<Query>> extractQueries(Source sources) {
        return null;
    }

    public Pipe<Source, Stream<LogicSample>> extractSamplesPipe(Source sources) {
        Pipe<Source, Stream<LogicSample>> pipe = new Pipe<Source, Stream<LogicSample>>("SamplesExtractionPipe") {
            @Override
            public Stream<LogicSample> apply(Source sources) {
                if (sources.QueriesSeparate) {
                    return samplesBuilder.buildFrom(sources.ExamplesParseTree, sources.QueriesParseTree);
                } else if (sources.QueriesProvided) {
                    return samplesBuilder.buildFrom(sources.ExamplesParseTree);
                } else {
                    LOG.severe("No Queries found to assemble Samples");
                    return null;
                }
            }
        };
        return pipe;
    }

    @Override
    public Pipeline<Source, Stream<LogicSample>> buildPipeline() {
        return buildPipeline(this.source);
    }

    public Pipeline<Source, Stream<LogicSample>> buildPipeline(Source source) {
        Pipeline<Source, Stream<LogicSample>> samplesProcessingPipeline = new Pipeline<>("SamplesProcessingPipeline");
            Pipe<Source, Stream<LogicSample>> sourcesSamplesPipe = samplesProcessingPipeline.register(extractSamplesPipe(source));
            Pipe<Stream<LogicSample>, Stream<LogicSample>> samplesPostprocessPipe = samplesProcessingPipeline.register(postprocessSamplesPipe());
            sourcesSamplesPipe.connectAfter(samplesPostprocessPipe);
            return samplesProcessingPipeline;

    }


    public Pipe<Stream<LogicSample>, Stream<LogicSample>> postprocessSamplesPipe() {
        //TODO
        return null;
    }
}