package pipelines;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import settings.Settings;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by gusta on 26.3.17.
 */
public class StandardLearningPipeline extends Pipeline {
    @Override
    public void execute(Settings settings) {
        {Pair<Optional<Template>, Stream<LearningSample>> resources = initLearningPipeline(settings);
    }
}
