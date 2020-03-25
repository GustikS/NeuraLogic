package cz.cvut.fel.ida.pipelines.building;

import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.export.NeuralSerializer;
import cz.cvut.fel.ida.pipelines.pipes.specific.NeuralSerializerPipe;
import cz.cvut.fel.ida.pipelines.pipes.specific.TemplateToNeuralPipe;
import cz.cvut.fel.ida.pipelines.pipes.specific.TrainingResultTemplateMerge;
import cz.cvut.fel.ida.pipelines.ConnectAfter;
import cz.cvut.fel.ida.pipelines.Merge;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.bulding.AbstractPipelineBuilder;
import cz.cvut.fel.ida.pipelines.pipes.generic.DuplicateBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.FirstFromPairExtractionBranch;
import cz.cvut.fel.ida.pipelines.pipes.generic.PairMerge;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

//import cz.cvut.fel.ida.ml.pipes.specific.NeuralSerializerPipe;

public class End2endTrainigBuilder extends AbstractPipelineBuilder<Sources, Pair<Pair<Template, NeuralModel>, Progress>> {
    private static final Logger LOG = Logger.getLogger(End2endTrainigBuilder.class.getName());

    private final Sources sources;

    public End2endTrainigBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    public Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> buildPipeline() {  //todo use this to make other pipelines simpler
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = new Pipeline<>("End2endTraining", this);

        //build neural nets first
        End2endNNBuilder end2endNNBuilder = new End2endNNBuilder();
        Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> buildNNsPipeline = pipeline.registerStart(end2endNNBuilder.buildPipeline());
        ConnectAfter<Template> template = end2endNNBuilder.getTemplate();

        //training
        Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> trainingPipeline = pipeline.register(buildTraining(settings));

        buildNNsPipeline.connectAfter(trainingPipeline);

        //helper blocks
        Merge<Template, Pair<NeuralModel, Progress>, Pair<Pair<Template, NeuralModel>, Progress>> trainingResultTemplateMerge = pipeline.registerEnd(trainingResultsTemplateMerge());

        trainingResultTemplateMerge.connectBeforeL(template);
        trainingResultTemplateMerge.connectBeforeR(trainingPipeline);

//        settings.root = pipeline;
        return pipeline;
    }

    public Pipeline<Sources, Pair<Template, Stream<LogicSample>>> buildFromSources(Sources sources, Settings settings) {
        TemplateSamplesBuilder templateSamplesBuilder = new TemplateSamplesBuilder(sources, settings);
        return templateSamplesBuilder.buildPipeline();
    }

    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> buildGrounding(Settings settings, WeightFactory weightFactory) {
        GroundingBuilder groundingBuilder = new GroundingBuilder(settings, weightFactory);
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = groundingBuilder.buildPipeline();
        return groundingPipeline;
    }

    public Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> buildNeuralNets(Settings settings, WeightFactory weightFactory) {
        NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, weightFactory);
        return neuralNetsBuilder.buildPipeline();
    }

    public Pipeline<Pair<NeuralModel, Stream<NeuralSample>>, Pair<NeuralModel, Progress>> buildTraining(Settings settings) {
        return new TrainingBuilder.NeuralLearningBuilder(settings).buildPipeline();
    }

    public Pipe<Template, NeuralModel> convertModel() {
        return new TemplateToNeuralPipe(settings);
    }

    public TrainingResultTemplateMerge trainingResultsTemplateMerge() {
        return new TrainingResultTemplateMerge();
    }


    /**
     * Helper class to delegate NN building
     */
    public class End2endNNBuilder extends AbstractPipelineBuilder<Sources, Pair<NeuralModel, Stream<NeuralSample>>> {

        private DuplicateBranch<Template> templateDuplicateBranch;

        public End2endNNBuilder() {
            super(End2endTrainigBuilder.this.settings);
        }


        @Override
        public Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> buildPipeline() {
            Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> pipeline = new Pipeline<>("End2EndNNbuilding", this);

            //simple pipes
            FirstFromPairExtractionBranch<Template, Stream<LogicSample>> templateSamplesBranch = pipeline.register(new FirstFromPairExtractionBranch<>());
            templateDuplicateBranch = pipeline.register(new DuplicateBranch<>());

            //to transfer parameters from groundings to neural nets
            WeightFactory weightFactory = new WeightFactory();

            //pipelines
            Pipeline<Sources, Pair<Template, Stream<LogicSample>>> sourcesPairPipeline = pipeline.registerStart(buildFromSources(sources, settings));
            Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>> groundingPipeline = pipeline.register(buildGrounding(settings, weightFactory));
            Pipeline<Stream<GroundingSample>, Stream<NeuralSample>> neuralizationPipeline = pipeline.register(buildNeuralNets(settings, weightFactory));

            //connecting the execution graph
            sourcesPairPipeline.connectAfter(templateSamplesBranch);
            templateSamplesBranch.connectAfterL(groundingPipeline).connectAfter(neuralizationPipeline);
            templateSamplesBranch.connectAfterR(templateDuplicateBranch);

            PairMerge<NeuralModel, Stream<NeuralSample>> neuralMerge = pipeline.registerEnd(new PairMerge<>());

            //prepare for training
            Pipe<Template, NeuralModel> template2NeuralModelPipe = pipeline.register(convertModel());
            templateDuplicateBranch.connectAfterL(template2NeuralModelPipe);

            neuralMerge.connectBeforeL(template2NeuralModelPipe);
            neuralMerge.connectBeforeR(neuralizationPipeline);

            return pipeline;
        }

        public Pair<List<NeuralSerializer.SerializedWeight>, Stream<NeuralSerializer.SerializedSample>> getSerializedNNs() throws Exception {
            Pipeline<Sources, Pair<NeuralModel, Stream<NeuralSample>>> buildNNsPipeline = buildPipeline();
            NeuralSerializerPipe neuralSerializerPipe = new NeuralSerializerPipe();
            Pipe<Pair<NeuralModel, Stream<NeuralSample>>, Pair<List<NeuralSerializer.SerializedWeight>, Stream<NeuralSerializer.SerializedSample>>> serializationPipe = buildNNsPipeline.connectAfter(neuralSerializerPipe);
            return serializationPipe.get();
        }


        public ConnectAfter<Template> getTemplate() {
            return templateDuplicateBranch.output2;
        }

    }
}
