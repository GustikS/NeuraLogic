package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import learning.crossvalidation.TrainTestResults;
import pipeline.Pipeline;
import pipeline.bulding.pipes.SamplesProcessor;
import pipeline.bulding.pipes.TemplateProcessor;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class LearningSchemeBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(LearningSchemeBuilder.class.getName());

    public LearningSchemeBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        Pipeline<Sources, Results> pipeline = new Pipeline<>("LearningSchemePipeline");

        //based on provided sources (samples) decide the learning mode
        if (sources.crossvalidation) {
            CrossvalidationBuilder crossvalidationSchemeBuilder = new CrossvalidationBuilder(settings);
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            crossvalidationSchemeBuilder.trainTestBuilder = trainTestBuilder;
            Pipeline<Sources, Results> sourcesResultsPipeline = crossvalidationSchemeBuilder.buildPipeline(sources);
            return sourcesResultsPipeline;
        } else if (sources.trainTest) {
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            Pipeline<Sources, TrainTestResults> pipeline = trainTestBuilder.buildPipeline(sources);
        } else if (sources.trainOnly) {
            SamplesProcessor.TrainingSamplesProcessor trainingSamplesProcessor = (new SamplesProcessor(settings)).new TrainingSamplesProcessor(settings);
            Pipeline<Sources, Stream<LearningSample>> sourcesStreamPipeline = trainingSamplesProcessor.buildPipeline(sources);
            TemplateProcessor templateProcessor = new TemplateProcessor(settings);
            Pipeline<Sources, Template> sourcesTemplatePipeline = templateProcessor.buildPipeline(sources);
            LearningBuilder learningBuilder = new LearningBuilder(settings);
            Pipeline<Pair<Template, Stream<LearningSample>>, Pair<Template, Results>> trainingPipeline = learningBuilder.buildPipeline(sources);

        } else if (sources.testOnly) {
            TestingBuilder testingBuilder = new TestingBuilder(settings);
            testingBuilder.buildPipeline(sources);
        } else {
            LOG.warning("Invalid learning mode setting.");
        }
    }
}