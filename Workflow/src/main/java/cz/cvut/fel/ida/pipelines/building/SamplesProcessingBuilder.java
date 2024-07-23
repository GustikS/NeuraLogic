package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.learning.Query;
import cz.cvut.fel.ida.learning.crossvalidation.splitting.StratifiedSplitter;
import cz.cvut.fel.ida.logic.constructs.building.ExamplesBuilder;
import cz.cvut.fel.ida.logic.constructs.building.QueriesBuilder;
import cz.cvut.fel.ida.logic.constructs.example.GroundExample;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Source;
import cz.cvut.fel.ida.setup.Sources;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static cz.cvut.fel.ida.utils.generic.Utilities.terminateSampleStream;

public class SamplesProcessingBuilder extends AbstractPipelineBuilder<Source, Stream<LogicSample>> {
    private static final Logger LOG = Logger.getLogger(SamplesProcessingBuilder.class.getName());

    private Source source;

    private Sources sources;

    int counter = 0;

    public SamplesProcessingBuilder(Settings settings, Source source) {
        super(settings);
        this.source = source;
    }

    public SamplesProcessingBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Source, Stream<LogicSample>> buildPipeline() {
        if (sources != null) {
            return buildPipeline(sources.folds.get(counter++).getTrainOrTest());
        } else
            return buildPipeline(this.source);
    }

    public Pipeline<Source, Stream<LogicSample>> buildPipeline(Source source) {
        Pipeline<Source, Stream<LogicSample>> samplesProcessingPipeline = new Pipeline<>("SamplesProcessingPipeline", this);
        Pipe<Source, Stream<LogicSample>> sourcesSamplesPipe = samplesProcessingPipeline.registerStart(extractSamplesPipe(source, samplesProcessingPipeline));
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


    public Pipe<Source, Stream<LogicSample>> extractSamplesPipe(Source source, Pipeline<Source, Stream<LogicSample>> samplesProcessingPipeline) {

        GroundExample.exampleCounter = 0;

        if (source.ExamplesReader == null && source.QueriesReader == null) {
            String err = "No sources found to assemble Samples at construction";
            LOG.severe(err);
            throw new RuntimeException(err);
        }

        Pipe<Source, Stream<LogicSample>> sampleExtractionPipe = null;
        ExamplesBuilder examplesBuilder = new ExamplesBuilder(settings);

        if (source.QueriesSeparate && source.ExamplesSeparate) {
            sampleExtractionPipe = new Pipe<Source, Stream<LogicSample>>("QueriesWithExamplesPipe", settings) {
                @Override
                public Stream<LogicSample> apply(Source source) {

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
                    Stream<LogicSample> labeledExamples = examplesBuilder.buildSamplesFrom(examplesBuilder.parseTreeFrom(source.ExamplesReader));
                    return labeledExamples;
                }
            };
        } else if (!source.QueriesProvided && source.ExamplesSeparate) {
            sampleExtractionPipe = new Pipe<Source, Stream<LogicSample>>("UnsupervisedExamplesPipe", settings) {
                @Override
                public Stream<LogicSample> apply(Source source) {
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

        examplesBuilder.setRebuildCallback(samplesProcessingPipeline::rebuildPipeline);
        return sampleExtractionPipe;
    }


    public Pipe<Stream<LogicSample>, Stream<LogicSample>> postprocessSamplesPipe() {
        //todo for instance order by example id
        Pipe<Stream<LogicSample>, Stream<LogicSample>> postProcessPipe = new Pipe<Stream<LogicSample>, Stream<LogicSample>>("PostprocessSamplesPipe", settings) {
            @Override
            public Stream<LogicSample> apply(Stream<LogicSample> logicSampleStream) {

                if (settings.appLimitSamples > 0) {
                    LOG.warning("Limiting the learning samples to the first: " + settings.appLimitSamples);
                    if (settings.stratification) {
                        LOG.warning("Stratified subset requested, will need to consume the stream of LogicSamples first...");
                        List<LogicSample> collect = terminateSampleStream(logicSampleStream);
                        Collections.shuffle(collect, settings.random);
                        StratifiedSplitter<LogicSample> stratifiedSplitter = new StratifiedSplitter<>(settings);
                        List<LogicSample> stratifiedSubset = stratifiedSplitter.getStratifiedSubset(collect, settings.appLimitSamples);
                        LOG.warning("Limited to exactly " + stratifiedSubset.size() + " samples (may be slightly different from the requested " + settings.appLimitSamples + " due to class balancing)");
                        logicSampleStream = stratifiedSubset.stream();
                    } else {
                        logicSampleStream = logicSampleStream.sorted(LogicSample::compare).limit(settings.appLimitSamples);
                    }
                }

                return logicSampleStream;
            }

        };
        return postProcessPipe;
    }
}