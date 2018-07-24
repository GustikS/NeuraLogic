package pipeline.bulding;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import pipeline.Pipeline;
import settings.Settings;
import training.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LearningSample>>,Pair<Template,Results>>{
    private static final Logger LOG = Logger.getLogger(TrainingBuilder.class.getName());

    public TrainingBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Pair<Template, Stream<LearningSample>>, Pair<Template, Results>> buildPipeline(Pair<Template, Stream<LearningSample>> sourceType) {
        return null;
    }

}