package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import networks.evaluation.results.Results;
import pipelines.Branch;
import pipelines.Pipeline;
import pipelines.prepared.pipes.generic.PairMerge;
import pipelines.prepared.pipes.specific.NeuralEvaluationPipe;
import settings.Settings;
import training.NeuralModel;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TestingBuilder {
    private static final Logger LOG = Logger.getLogger(TestingBuilder.class.getName());
    Settings settings;

    public TestingBuilder(Settings settings) {
        this.settings = settings;
    }


    public class LogicTestingBuilder extends AbstractPipelineBuilder<Pair<Pair<Template,NeuralModel>, Stream<LogicSample>>, Results> {
        Pipeline<Pair<Template,Stream<LogicSample>>, Stream<NeuralSample>> groundingPipeline;
        Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> neuralTestingPipeline;

        public LogicTestingBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<Pair<Template,NeuralModel>, Stream<LogicSample>>, Results> buildPipeline() {
            Pipeline<Pair<Pair<Template,NeuralModel>, Stream<LogicSample>>, Results> pipeline = new Pipeline<>("LogicTestingPipeline");
            if (groundingPipeline == null){
                GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
                groundingPipeline = pipeline.register(groundingBuilder.buildPipeline());
            }

            if (neuralTestingPipeline == null){
                NeuralTestingBuilder neuralTestingBuilder = new NeuralTestingBuilder(settings);
                neuralTestingPipeline = pipeline.registerEnd(neuralTestingBuilder.buildPipeline());
            }

            Branch<Pair<Pair<Template,NeuralModel>, Stream<LogicSample>>,Pair<Template,Stream<LogicSample>>,NeuralModel> modelSplitBranch =
                    pipeline.registerStart(new Branch<Pair<Pair<Template, NeuralModel>, Stream<LogicSample>>, Pair<Template, Stream<LogicSample>>, NeuralModel>("ModelSplitBranch") {
                @Override
                protected Pair<Pair<Template, Stream<LogicSample>>, NeuralModel> branch(Pair<Pair<Template, NeuralModel>, Stream<LogicSample>> input) {
                    return new Pair<>(new Pair<>(input.r.r,input.s),input.r.s);
                }
            });

            modelSplitBranch.connectAfterL(groundingPipeline);

            PairMerge<NeuralModel, Stream<NeuralSample>> pairMerge = pipeline.register(new PairMerge<>());
            pairMerge.connectBeforeL(modelSplitBranch.output2);
            pairMerge.connectBeforeR(groundingPipeline);

            pairMerge.connectAfter(neuralTestingPipeline);
            return pipeline;
        }
    }

    public class NeuralTestingBuilder extends AbstractPipelineBuilder<Pair<NeuralModel, Stream<NeuralSample>>, Results> {
        public NeuralTestingBuilder(Settings settings) {
            super(settings);
        }

        @Override
        public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> buildPipeline() {
            Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Results> pipeline = new Pipeline<>("NeuralTestingPipeline");
            NeuralEvaluationPipe neuralEvaluationPipe = pipeline.registerEnd(pipeline.registerStart(new NeuralEvaluationPipe(settings)));
            return pipeline;
        }
    }

}