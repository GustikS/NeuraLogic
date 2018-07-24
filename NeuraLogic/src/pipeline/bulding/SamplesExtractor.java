package pipeline.bulding;

import building.SamplesBuilder;
import constructs.example.LiftedExample;
import learning.LearningSample;
import learning.Query;
import pipeline.Pipeline;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SamplesExtractor extends AbstractPipelineBuilder<Sources, Stream<LearningSample>> {
    private static final Logger LOG = Logger.getLogger(SamplesExtractor.class.getName());

    SamplesBuilder samplesBuilder;

    public SamplesExtractor(Settings settings) {
        super(settings);
        samplesBuilder = new SamplesBuilder(settings);
    }

    @Override
    public Pipeline<Sources, Stream<LearningSample>> buildPipeline(Sources sourceType) {
        return null;
    }


    public Stream<LiftedExample> getTrainingExamples(Sources sources) {
        return null;
    }

    public Stream<Query> getTrainingQueries(Sources sources) {
        return null;
    }

    public Stream<LearningSample> getTrainingSamples(Sources sources) {

        if (sources.trainQueriesSeparate) {
            return samplesBuilder.buildFrom(sources.trainExamplesParseTree, sources.trainQueriesParseTree);
        } else if (sources.trainQueriesProvided) {
            return samplesBuilder.buildFrom(sources.trainExamplesParseTree);
        } else {
            LOG.warning("No Queries found to assemble train Samples");
            return null;
        }
    }

    public Stream<LiftedExample> getTestingExamples(Sources sources) {
        return null;
    }

    public Stream<Query> getTestingQueries(Sources sources) {
        return null;
    }

    public Stream<LearningSample> getTestingSamples(Sources sources) {

        if (sources.testQueriesSeparate) {
            return samplesBuilder.buildFrom(sources.testExamplesParseTree, sources.testQueriesParseTree);
        } else if (sources.trainQueriesProvided) {
            return samplesBuilder.buildFrom(sources.testExamplesParseTree);
        } else {
            LOG.warning("No Queries found to assemble test Samples");
            return null;
        }
    }

}