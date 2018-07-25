package pipeline.bulding.pipes;

import constructs.building.SamplesBuilder;
import constructs.example.LiftedExample;
import learning.LearningSample;
import learning.Query;
import pipeline.Pipe;
import pipeline.Pipeline;
import pipeline.bulding.AbstractPipelineBuilder;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SamplesProcessor {
    private static final Logger LOG = Logger.getLogger(SamplesProcessor.class.getName());
    private final Settings settings;

    SamplesBuilder samplesBuilder;

    public SamplesProcessor(Settings settings) {
        this.settings = settings;
        samplesBuilder = new constructs.building.SamplesBuilder(settings);
    }

    public class TrainingSamplesProcessor extends AbstractPipelineBuilder<Sources, Stream<LearningSample>> {


        public TrainingSamplesProcessor(Settings settings) {
            super(settings);
        }

        public Pipe<Sources, Stream<LiftedExample>> extractTrainingExamples(Sources sources) {
            return null;
        }

        public Pipe<Sources, Stream<Query>> extractTrainingQueries(Sources sources) {
            return null;
        }

        public Pipe<Sources, Stream<LearningSample>> extractTrainingSamplesPipe(Sources sources) {
            Pipe<Sources, Stream<LearningSample>> pipe = new Pipe<Sources, Stream<LearningSample>>("TrainingSamplesExtractionPipe") {
                @Override
                public Stream<LearningSample> apply(Sources sources) {
                    if (sources.trainQueriesSeparate) {
                        return samplesBuilder.buildFrom(sources.trainExamplesParseTree, sources.trainQueriesParseTree);
                    } else if (sources.trainQueriesProvided) {
                        return samplesBuilder.buildFrom(sources.trainExamplesParseTree);
                    } else {
                        LOG.warning("No Queries found to assemble train Samples");
                        return null;
                    }
                }
            };
            return pipe;
        }

        @Override
        public Pipeline<Sources, Stream<LearningSample>> buildPipeline(Sources sources) {
            Pipeline<Sources, Stream<LearningSample>> samplesProcessingPipeline = new Pipeline<>("TrainingSamplesProcessingPipeline");
            if (sources.trainQueriesProvided) {
                Pipe<Sources, Stream<LearningSample>> sourcesSamplesPipe = samplesProcessingPipeline.register(extractTrainingSamplesPipe(sources));
                Pipe<Stream<LearningSample>, Stream<LearningSample>> samplesPostprocessPipe = samplesProcessingPipeline.register(postprocessSamples());
                sourcesSamplesPipe.connectAfter(samplesPostprocessPipe);
                return samplesProcessingPipeline;
            } else {
                LOG.warning("Training samples extraction from sources requested but no training queries provided.");
                return null;
            }
        }
    }

    public class TestingSamplesProcessor extends AbstractPipelineBuilder<Sources, Stream<LearningSample>> {

        public TestingSamplesProcessor(Settings settings) {
            super(settings);
        }

        public Pipe<Sources, Stream<LiftedExample>> extractTestingExamples(Sources sources) {
            return null;
        }

        public Pipe<Sources, Stream<Query>> extractTestingQueries(Sources sources) {
            return null;
        }

        public Pipe<Sources, Stream<LearningSample>> extractTestingSamplesPipe(Sources sources) {
            Pipe<Sources, Stream<LearningSample>> pipe = new Pipe<Sources, Stream<LearningSample>>("TrainingSamplesExtractionPipe") {
                @Override
                public Stream<LearningSample> apply(Sources sources) {
                    if (sources.testQueriesSeparate) {
                        return samplesBuilder.buildFrom(sources.testExamplesParseTree, sources.testQueriesParseTree);
                    } else if (sources.trainQueriesProvided) {
                        return samplesBuilder.buildFrom(sources.testExamplesParseTree);
                    } else {
                        LOG.warning("No Queries found to assemble test Samples");
                        return null;
                    }
                }
            };
            return pipe;
        }

        @Override
        public Pipeline<Sources, Stream<LearningSample>> buildPipeline(Sources sources) {
            Pipeline<Sources, Stream<LearningSample>> samplesProcessingPipeline = new Pipeline<>("TestingSamplesProcessingPipeline");
            if (sources.testQueriesProvided) {
                Pipe<Sources, Stream<LearningSample>> sourcesSamplesPipe = samplesProcessingPipeline.register(extractTestingSamplesPipe(sources));
                Pipe<Stream<LearningSample>, Stream<LearningSample>> samplesPostprocessPipe = samplesProcessingPipeline.register(postprocessSamples());
                sourcesSamplesPipe.connectAfter(samplesPostprocessPipe);
                return samplesProcessingPipeline;
            } else {
                LOG.warning("Testing samples extraction from sources requested but no testing queries provided.");
                return null;
            }
        }
    }

    public Pipe<Stream<LearningSample>, Stream<LearningSample>> postprocessSamples() {
        //TODO
        return null;
    }
}