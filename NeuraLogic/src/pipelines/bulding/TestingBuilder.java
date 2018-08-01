package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import pipelines.Pipeline;
import settings.Settings;
import training.NeuralModel;
import training.NeuralSample;
import networks.evaluation.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TestingBuilder {
    private static final Logger LOG = Logger.getLogger(TestingBuilder.class.getName());
    Settings settings;

    public TestingBuilder(Settings settings) {
        this.settings = settings;
    }


    public class LogicTestingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Results> {

        public LogicTestingBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Template, Stream<LogicSample>>, Results> buildPipeline() {
            return null;
        }
    }

    public class NeuralTestingBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Stream<NeuralSample>>, Results> {
        public NeuralTestingBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> buildPipeline() {
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> pipeline = new Pipeline<>("NeuralTestingPipeline");

            return null;
        }
    }

}
