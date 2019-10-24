package pipelines.building;

import constructs.example.LogicSample;
import constructs.template.Template;
import grounding.GroundingSample;
import learning.LearningSample;
import learning.Model;
import learning.crossvalidation.Crossvalidation;
import learning.crossvalidation.TrainTestResults;
import learning.crossvalidation.splitting.Splitter;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import networks.structure.building.Neuralizer;
import pipelines.*;
import pipelines.pipes.generic.*;
import pipelines.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import utils.generic.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Build CV pipeline w.r.t. given Sources and Settings
 */
public class CrossvalidationBuilder extends AbstractPipelineBuilder<Sources, TrainTestResults> {
    private static final Logger LOG = Logger.getLogger(CrossvalidationBuilder.class.getName());
    private Sources sources;

    public CrossvalidationBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, TrainTestResults> buildPipeline() {
        return buildPipeline(sources);
    }

    public Pipeline<Sources, TrainTestResults> buildPipeline(Sources sources) {
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("CrossvalidationPipeline", this);

        MultiMerge<TrainTestResults, TrainTestResults> resultsMultiMerge = pipeline.registerEnd(new MultiMerge<TrainTestResults, TrainTestResults>("ResultsAggregateMerge", settings.foldsCount) {
            @Override
            protected TrainTestResults merge(List<TrainTestResults> inputs) {
                Crossvalidation crossvalidation = new Crossvalidation(settings);
                return crossvalidation.aggregateResults(inputs);
            }
        });

        TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings, sources);

        if (sources.foldFiles) { //external pre-split folds by user

            MultiBranch<Sources, Sources> foldsBranch = pipeline.registerStart(new MultiBranch<Sources, Sources>("FoldsBranch", sources.folds.size()) {
                @Override
                protected List<Sources> branch(Sources folds) {
                    return folds.folds;
                }
            });

            if (sources.folds.stream().allMatch(fold -> fold.trainTest)) {  //folds are completely independent (train+test already provided in each)

                List<Pipeline<Sources, TrainTestResults>> trainTestPipelines = trainTestBuilder.buildPipelines(sources.folds.size());
                trainTestPipelines.forEach(pipeline::register);

                foldsBranch.connectAfter(trainTestPipelines);
                resultsMultiMerge.connectBefore(trainTestPipelines);

            } else if (sources.folds.stream().allMatch(fold -> fold.testOnly)) { //train-test folds need to be assembled first from provided test-sets

                List<Pipe<Sources, Source>> testFoldPipes = new Pipe<Sources, Source>("GetTestFold") {
                    @Override
                    public Source apply(Sources sources) {
                        return sources.test;
                    }
                }.parallel(sources.folds.size());
                testFoldPipes.forEach(pipeline::register);

                List<Pipeline<Source, Stream<LogicSample>>> samplesExtract = new SamplesProcessingBuilder(settings, sources.folds.get(0).test).buildPipelines(sources.folds.size());
                samplesExtract.forEach(pipeline::register);
                Pipe.connect(testFoldPipes, samplesExtract);

                if (sources.templateProvided) { //standard learning with a provided template - prepare for template processing

                    DuplicateBranch<Sources> duplicateOrigin = pipeline.registerStart(new DuplicateBranch<>());
                    TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);

                    Pipeline<Sources, Template> sourcesTemplatePipeline = null;
                    List<Pipeline<Sources, Template>> sourcesTemplatePipelines = null;

                    if (settings.commonTemplate) { //there is a single template common to all the provided folds - process it

                        sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());

                        duplicateOrigin.connectAfterL(sourcesTemplatePipeline);
                        duplicateOrigin.connectAfterR(foldsBranch);
                        foldsBranch.connectAfter(testFoldPipes);

                    } else { //there are possibly different templates provided separately for each of the folds - process them in parallel together with the corresponding test-sets

                        sourcesTemplatePipelines = templateProcessor.buildPipelines(sources.folds.size());
                        sourcesTemplatePipelines.forEach(pipeline::register);

                        List<Branch<Sources, Sources, Sources>> parallelSourcesDuplicates = duplicateOrigin.parallel(sources.folds.size());
                        parallelSourcesDuplicates.forEach(pipeline::register);
                        pipeline.registerStart(foldsBranch);

                        foldsBranch.connectAfter(parallelSourcesDuplicates);
                        Branch.connectAfterL(parallelSourcesDuplicates, testFoldPipes);
                        Branch.connectAfterR(parallelSourcesDuplicates, sourcesTemplatePipelines);
                    }

                    if (settings.trainFoldsIsolation) { //first assemble full (overlapping with train) train-test CV folds, then do the full training (repetively ground all train samples folds from scratch). Grounding is hidden within each TrainTest pipeline (easier here).

                        List<Pipeline<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults>> logicTrainTestPipelines = trainTestBuilder.new LogicTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                        logicTrainTestPipelines.forEach(pipeline::register);
                        resultsMultiMerge.connectBefore(logicTrainTestPipelines);

                        MultiMerge<Stream<LogicSample>, Crossvalidation<LogicSample>> logicCrossvalidation = pipeline.register(assembleCV(LogicSample.class, sources.folds.size()));
                        logicCrossvalidation.connectBefore(samplesExtract);

                        if (settings.commonTemplate) { //common template for all folds - no need to copy here since there's no grounding or preprocessing into neural

                            PairMerge<Template, Crossvalidation<LogicSample>> templateCVmerge = pipeline.register(new PairMerge<>());
                            templateCVmerge.connectBeforeL(sourcesTemplatePipeline);
                            templateCVmerge.connectBeforeR(logicCrossvalidation);

                            MultiBranch<Pair<Template, Crossvalidation<LogicSample>>, Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> emitFolds = pipeline.register(emitModelFolds(Template.class, LogicSample.class, sources.folds.size()));
                            templateCVmerge.connectAfter(emitFolds);
                            emitFolds.connectAfter(logicTrainTestPipelines);

                        } else { //each fold has a separate template - again no preprocessing into neural

                            ListMerge<Template> templatesMergeList = pipeline.register(new ListMerge<>(sources.folds.size()));
                            templatesMergeList.connectBefore(sourcesTemplatePipelines);
                            Merge<List<Template>, Crossvalidation<LogicSample>, List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>>> mergeCVtemplates = pipeline.register(emitModelsFolds(Template.class, LogicSample.class, sources.folds.size()));
                            mergeCVtemplates.connectBeforeL(templatesMergeList);
                            mergeCVtemplates.connectBeforeR(logicCrossvalidation);

                            ListBranch<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> modelsFoldsBranch = pipeline.register(new ListBranch<>(sources.folds.size()));
                            mergeCVtemplates.connectAfter(modelsFoldsBranch);
                            modelsFoldsBranch.connectAfter(logicTrainTestPipelines);

                        }

                    } else { //ground all test-sets independently, then assemble CV folds <- most efficient mode (no repeated grounding of the same sample). Explicit grounding and transforming to Neural representation here

                        List<Pipeline<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults>> neuralTrainTestPipelines = trainTestBuilder.new NeuralTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                        neuralTrainTestPipelines.forEach(pipeline::register);
                        resultsMultiMerge.connectBefore(neuralTrainTestPipelines);

                        MultiMerge<Stream<NeuralSample>, Crossvalidation<NeuralSample>> neuralCrossvalidation = pipeline.register(assembleCV(NeuralSample.class, sources.folds.size()));
                        GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
                        List<Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>>> groundingPipelines = groundingBuilder.buildPipelines(sources.folds.size());

                        NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, new Neuralizer(settings, groundingBuilder.grounder.weightFactory));
                        List<Pipeline<Stream<GroundingSample>, Stream<NeuralSample>>> neuralizationPipelines = neuralNetsBuilder.buildPipelines(sources.folds.size());

                        groundingPipelines.forEach(pipeline::register);
                        neuralizationPipelines.forEach(pipeline::register);
                        List<Merge<Template, Stream<LogicSample>, Pair<Template, Stream<LogicSample>>>> templateSamplesMerges = new PairMerge<Template, Stream<LogicSample>>().parallel(sources.folds.size());
                        templateSamplesMerges.forEach(pipeline::register);

                        Pipe.connect(groundingPipelines, neuralizationPipelines);
                        neuralCrossvalidation.connectBefore(neuralizationPipelines);
                        Merge.connectBeforeR(templateSamplesMerges, samplesExtract);
                        Pipe.connect(templateSamplesMerges, groundingPipelines);


                        if (settings.commonTemplate) { //process the single template to neural model and duplicate it to each of the folds, merge into crossval

                            DuplicateBranch<Template> duplicateTemplate = pipeline.register(new DuplicateBranch<>());
                            TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe(settings));
                            DuplicateListBranch<Template> templateListBranch = pipeline.register(new DuplicateListBranch<>(sources.folds.size()));  //duplicating the same template - might be advantagoues for subsequent parallel grounding

                            sourcesTemplatePipeline.connectAfter(duplicateTemplate);
                            duplicateTemplate.connectAfterL(templateListBranch);
                            duplicateTemplate.connectAfterR(templateToNeuralPipe);
                            Merge.connectBeforeL(templateSamplesMerges, templateListBranch.outputs);

                            PairMerge<NeuralModel, Crossvalidation<NeuralSample>> pairMerge = pipeline.register(new PairMerge<>());
                            pairMerge.connectBeforeL(templateToNeuralPipe);
                            pairMerge.connectBeforeR(neuralCrossvalidation);

                            MultiBranch<Pair<NeuralModel, Crossvalidation<NeuralSample>>, Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> emitFolds = pipeline.register(emitModelFolds(NeuralModel.class, NeuralSample.class, sources.folds.size()));
                            pairMerge.connectAfter(emitFolds);
                            emitFolds.connectAfter(neuralTrainTestPipelines);

                        } else { //process the different templates in parallel to neural models and merge them into crossval

                            List<Branch<Template, Template, Template>> parallelTemplateBranches = new DuplicateBranch<Template>().parallel(sources.folds.size());
                            parallelTemplateBranches.forEach(pipeline::register);
                            List<Pipe<Template, Template>> templatesPipes = new IdentityGenPipe<Template>().parallel(sources.folds.size());
                            templatesPipes.forEach(pipeline::register);
                            List<Pipe<Template, NeuralModel>> template2NeuralModels = new TemplateToNeuralPipe(settings).parallel(sources.folds.size());
                            template2NeuralModels.forEach(pipeline::register);
                            ListMerge<NeuralModel> neuralModelsMerge = pipeline.register(new ListMerge<>(sources.folds.size()));
                            Merge<List<NeuralModel>, Crossvalidation<NeuralSample>, List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>>> emitModelsFolds = pipeline.register(emitModelsFolds(NeuralModel.class, NeuralSample.class, sources.folds.size()));
                            ListBranch<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> modelsFoldsBranch = pipeline.register(new ListBranch<>(sources.folds.size()));

                            Pipe.connect(sourcesTemplatePipelines, parallelTemplateBranches);
                            Branch.connectAfterL(parallelTemplateBranches, templatesPipes);
                            Branch.connectAfterR(parallelTemplateBranches, template2NeuralModels);
                            Merge.connectBeforeL(templateSamplesMerges, templatesPipes);

                            neuralModelsMerge.connectBefore(template2NeuralModels);
                            emitModelsFolds.connectBeforeL(neuralModelsMerge);
                            emitModelsFolds.connectBeforeR(neuralCrossvalidation);

                            emitModelsFolds.connectAfter(modelsFoldsBranch);
                            modelsFoldsBranch.connectAfter(neuralTrainTestPipelines);
                        }
                    }

                } else { //structure learning must go with isolated training folds anyway

                    MultiMerge<Stream<LogicSample>, Crossvalidation<LogicSample>> mergeFolds2CV = pipeline.register(assembleCV(LogicSample.class, sources.folds.size()));
                    MultiBranch<Crossvalidation<LogicSample>, Pair<Stream<LogicSample>, Stream<LogicSample>>> emitTrainTest = pipeline.register(emitTrainTest(LogicSample.class, sources.folds.size()));
                    List<Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults>> structTrainTestPipeline = trainTestBuilder.new StructureTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                    structTrainTestPipeline.forEach(pipeline::register);

                    mergeFolds2CV.connectBefore(samplesExtract);
                    emitTrainTest.connectBefore(mergeFolds2CV);
                    emitTrainTest.connectAfter(structTrainTestPipeline);
                    resultsMultiMerge.connectBefore(structTrainTestPipeline);
                }
            }

        } else { //internal folds splitting from provided 'train' data

            Pipe<Sources, Source> getTrainSource = pipeline.register(new Pipe<Sources, Source>("GetTrainFold") {
                @Override
                public Source apply(Sources sources) {
                    return sources.train;
                }
            });

            Pipeline<Source, Stream<LogicSample>> samplesExtract = pipeline.register(new SamplesProcessingBuilder(settings, sources.train).buildPipeline(sources.train));

            if (sources.templateProvided) { //prepare template processing

                Pipeline<Sources, Template> getTemplate = pipeline.register(new TemplateProcessingBuilder(settings, sources).buildPipeline());

                DuplicateBranch<Sources> sourcesDuplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
                sourcesDuplicateBranch.connectAfterL(getTemplate);
                sourcesDuplicateBranch.connectAfterR(getTrainSource);
                getTrainSource.connectAfter(samplesExtract);

                if (settings.trainFoldsIsolation) { //assemble full train folds -> perform full logic learning including repetitive grounding on the train parts

                    List<Pipeline<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults>> logicTrainTestPipelines = trainTestBuilder.new LogicTrainTestBuilder(settings).buildPipelines(settings.foldsCount);
                    Pipe<Stream<LogicSample>, Crossvalidation<LogicSample>> logicCrossvalidation = pipeline.register(cvFromStream(LogicSample.class));
                    logicTrainTestPipelines.forEach(pipeline::register);
                    PairMerge<Template, Crossvalidation<LogicSample>> templateCVmerge = pipeline.register(new PairMerge<>());
                    MultiBranch<Pair<Template, Crossvalidation<LogicSample>>, Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> emitFolds = pipeline.register(emitModelFolds(Template.class, LogicSample.class, settings.foldsCount));

                    samplesExtract.connectAfter(logicCrossvalidation);
                    templateCVmerge.connectBeforeL(getTemplate);
                    templateCVmerge.connectBeforeR(logicCrossvalidation);

                    templateCVmerge.connectAfter(emitFolds);
                    emitFolds.connectAfter(logicTrainTestPipelines);
                    resultsMultiMerge.connectBefore(logicTrainTestPipelines);

                } else { //partition -> ground -> assemble train-test folds

                    List<Pipeline<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults>> neuralTrainTestPipeline = trainTestBuilder.new NeuralTrainTestBuilder(settings).buildPipelines(settings.foldsCount);
                    neuralTrainTestPipeline.forEach(pipeline::register);

                    GroundingBuilder groundingBuilder = new GroundingBuilder(settings);
                    List<Pipeline<Pair<Template, Stream<LogicSample>>, Stream<GroundingSample>>> groundingPipelines = groundingBuilder.buildPipelines(settings.foldsCount);
                    groundingPipelines.forEach(pipeline::register);

                    NeuralNetsBuilder neuralNetsBuilder = new NeuralNetsBuilder(settings, new Neuralizer(settings, groundingBuilder.grounder.weightFactory));
                    List<Pipeline<Stream<GroundingSample>, Stream<NeuralSample>>> neuralizationPipelines = neuralNetsBuilder.buildPipelines(settings.foldsCount);
                    neuralizationPipelines.forEach(pipeline::register);

                    DuplicateBranch<Template> duplicateTemplate = pipeline.register(new DuplicateBranch<>());
                    DuplicateListBranch<Template> templateListBranch = pipeline.register(new DuplicateListBranch<>(settings.foldsCount));
                    ListBranch<Stream<LogicSample>> foldsBranch = pipeline.register(new ListBranch<>(settings.foldsCount));
                    Pipe<Stream<LogicSample>, List<Stream<LogicSample>>> samplesSplitterPipe = pipeline.register(new Pipe<Stream<LogicSample>, List<Stream<LogicSample>>>("SplitterPipe") {
                        @Override
                        public List<Stream<LogicSample>> apply(Stream<LogicSample> logicSampleStream) {
                            Splitter<LogicSample> splitter = Splitter.getSplitter(settings);
                            return splitter.partition(logicSampleStream, settings.foldsCount);
                        }
                    });

                    List<Merge<Template, Stream<LogicSample>, Pair<Template, Stream<LogicSample>>>> templateSamplesMerges = new PairMerge<Template, Stream<LogicSample>>().parallel(settings.foldsCount);
                    templateSamplesMerges.forEach(pipeline::register);
                    MultiMerge<Stream<NeuralSample>, Crossvalidation<NeuralSample>> neuralCrossvalidation = pipeline.register(assembleCV(NeuralSample.class, settings.foldsCount));
                    TemplateToNeuralPipe templateToNeuralPipe = pipeline.register(new TemplateToNeuralPipe(settings));
                    PairMerge<NeuralModel, Crossvalidation<NeuralSample>> modelCVmerge = pipeline.register(new PairMerge<>());
                    MultiBranch<Pair<NeuralModel, Crossvalidation<NeuralSample>>, Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> emitFolds = emitModelFolds(NeuralModel.class, NeuralSample.class, settings.foldsCount);


                    getTemplate.connectAfter(duplicateTemplate);
                    duplicateTemplate.connectAfterL(templateListBranch);
                    samplesExtract.connectAfter(samplesSplitterPipe);
                    samplesSplitterPipe.connectAfter(foldsBranch);

                    Merge.connectBeforeL(templateSamplesMerges, templateListBranch.outputs);
                    Merge.connectBeforeR(templateSamplesMerges, foldsBranch.outputs);
                    Pipe.connect(templateSamplesMerges, groundingPipelines);
                    Pipe.connect(groundingPipelines,neuralizationPipelines);

                    neuralCrossvalidation.connectBefore(neuralizationPipelines);
                    duplicateTemplate.connectAfterR(templateToNeuralPipe);

                    modelCVmerge.connectBeforeL(templateToNeuralPipe);
                    modelCVmerge.connectBeforeR(neuralCrossvalidation);

                    modelCVmerge.connectAfter(emitFolds);
                    emitFolds.connectAfter(neuralTrainTestPipeline);

                    resultsMultiMerge.connectBefore(neuralTrainTestPipeline);
                }

            } else { //structure learning

                Pipe<Stream<LogicSample>, Crossvalidation<LogicSample>> cvFromStream = pipeline.register(cvFromStream(LogicSample.class));
                MultiBranch<Crossvalidation<LogicSample>, Pair<Stream<LogicSample>, Stream<LogicSample>>> emitTrainTest = pipeline.register(emitTrainTest(LogicSample.class, sources.folds.size()));
                List<Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults>> structTrainTestPipelines = trainTestBuilder.new StructureTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                structTrainTestPipelines.forEach(pipeline::register);

                cvFromStream.connectBefore(samplesExtract);
                emitTrainTest.connectBefore(cvFromStream);
                emitTrainTest.connectAfter(structTrainTestPipelines);

                resultsMultiMerge.connectBefore(structTrainTestPipelines);
            }
        }

        return pipeline;
    }

    protected <S extends LearningSample> Pipe<Stream<S>, Crossvalidation<S>> cvFromStream(Class<S> s) {
        return new Pipe<Stream<S>, Crossvalidation<S>>("CVFromStream") {
            @Override
            public Crossvalidation<S> apply(Stream<S> logicSampleStream) {
                Crossvalidation<S> cv = new Crossvalidation<>(settings, settings.foldsCount);
                cv.loadFolds(logicSampleStream);
                return cv;
            }
        };
    }

    protected <S extends LearningSample> MultiMerge<Stream<S>, Crossvalidation<S>> assembleCV(Class<S> s, int foldsCount) {
        return new MultiMerge<Stream<S>, Crossvalidation<S>>("MergeFolds2CV", foldsCount) {
            @Override
            protected Crossvalidation<S> merge(List<Stream<S>> inputs) {
                Crossvalidation<S> cv = new Crossvalidation<S>(settings, inputs.size());
                cv.loadFolds(inputs);
                return cv;
            }
        };
    }

    protected <S extends LearningSample> MultiBranch<Crossvalidation<S>, Pair<Stream<S>, Stream<S>>> emitTrainTest(Class<S> s, int foldCount) {
        return new MultiBranch<Crossvalidation<S>, Pair<Stream<S>, Stream<S>>>("BranchCV2Folds", sources.folds.size()) {

            @Override
            protected List<Pair<Stream<S>, Stream<S>>> branch(Crossvalidation<S> cv) {
                List<Pair<Stream<S>, Stream<S>>> folds = new ArrayList<>(cv.foldCount);
                for (int i = 0; i < cv.foldCount; i++) {
                    folds.add(new Pair<>(cv.folds.get(i).train.stream(), cv.folds.get(i).test.stream()));
                }
                return folds;
            }
        };
    }

    protected <T extends Model, S extends LearningSample> MultiBranch<Pair<T, Crossvalidation<S>>, Pair<T, Pair<Stream<S>, Stream<S>>>> emitModelFolds(Class<T> t, Class<S> s, int foldCount) {
        return new MultiBranch<Pair<T, Crossvalidation<S>>, Pair<T, Pair<Stream<S>, Stream<S>>>>("EmitFoldsWithModel", foldCount) {
            @Override
            protected List<Pair<T, Pair<Stream<S>, Stream<S>>>> branch(Pair<T, Crossvalidation<S>> cv) {
                List<Pair<T, Pair<Stream<S>, Stream<S>>>> pairList = new ArrayList<>(cv.s.foldCount);
                for (int i = 0; i < cv.s.foldCount; i++) {
                    pairList.add(new Pair<>(cv.r, new Pair<>(cv.s.folds.get(i).train.stream(), cv.s.folds.get(i).test.stream())));  //TODO consider parallel streaming of samples in each fold? (probably turn to parallel later)
                }
                return pairList;
            }
        };
    }

    protected <T extends Model, S extends LearningSample> Merge<List<T>, Crossvalidation<S>, List<Pair<T, Pair<Stream<S>, Stream<S>>>>> emitModelsFolds(Class<T> t, Class<S> s, int foldsCount) {
        return new Merge<List<T>, Crossvalidation<S>, List<Pair<T, Pair<Stream<S>, Stream<S>>>>>("EmitFoldsWithModels") {
            @Override
            protected List<Pair<T, Pair<Stream<S>, Stream<S>>>> merge(List<T> templates, Crossvalidation<S> cv) {
                List<Pair<T, Pair<Stream<S>, Stream<S>>>> folds = new ArrayList<>(templates.size());
                for (int i = 0; i < templates.size(); i++) {
                    folds.add(new Pair<>(templates.get(i), new Pair<>(cv.folds.get(i).train.stream(), cv.folds.get(i).test.stream())));
                }
                return folds;
            }
        };
    }
}