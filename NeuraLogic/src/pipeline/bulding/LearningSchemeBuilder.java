package pipeline.bulding;

import constructs.template.Template;
import learning.crossvalidation.TrainTestResults;
import pipeline.Pipe;
import pipeline.Pipeline;
import pipeline.bulding.pipes.TemplateProcessor;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.logging.Logger;

public class LearningSchemeBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(LearningSchemeBuilder.class.getName());

    public LearningSchemeBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        Pipeline<Sources, Results> pipeline = new Pipeline<>("LearningSchemePipeline");
        //1st process the template if provided
        Pipeline<Sources, Template> templateProcessingPipeline = new Pipeline<>("TemplateProcessingPipeline");
        if (sources.templateProvided){
            TemplateProcessor templateProcessor = new TemplateProcessor(settings);
            Pipe<Sources, Template> sourcesTemplatePipe = templateProcessor.extractTemplate(sources);
            templateProcessingPipeline.register(sourcesTemplatePipe);
            Pipe<Template, Template> templateTemplatePipe = templateProcessor.postProcessTemplate();
            templateProcessingPipeline.register(templateTemplatePipe);
            sourcesTemplatePipe.output = templateTemplatePipe;
            templateTemplatePipe.input = sourcesTemplatePipe;
        }

        //2nd based on provided samples decide the learning mode
        if (sources.crossvalidation) {
            CrossvalidationBuilder crossvalidationSchemaBuilder = new CrossvalidationBuilder(settings);
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            crossvalidationSchemaBuilder.trainTestBuilder = trainTestBuilder;
            Pipeline<Sources, Results> sourcesResultsPipeline = crossvalidationSchemaBuilder.buildPipeline(sources);
            return sourcesResultsPipeline;
        } else if (sources.trainTest) {
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            Pipeline<Sources, TrainTestResults> pipeline = trainTestBuilder.buildPipeline(sources);
        } else if (sources.trainOnly) {
            TrainingBuilder trainingBuilder = new TrainingBuilder(settings);
            trainingBuilder.buildPipeline(sources);
        } else if (sources.testOnly) {
            TestingBuilder testingBuilder = new TestingBuilder(settings);
            testingBuilder.buildPipeline(sources);
        } else {
            LOG.warning("Invalid learning mode setting.");
        }
    }
}