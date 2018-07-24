package pipeline.bulding.pipes;

import building.SamplesBuilder;
import constructs.example.LiftedExample;
import learning.LearningSample;
import learning.Query;
import pipeline.Pipe;
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
        samplesBuilder = new building.SamplesBuilder(settings);
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

    public Pipe<Stream<LearningSample>,Stream<LearningSample>> postprocessSamples(){
        //TODO
        return null;
    }
}