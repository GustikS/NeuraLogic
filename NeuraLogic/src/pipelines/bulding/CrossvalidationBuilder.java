package pipelines.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.crossvalidation.Crossvalidation;
import learning.crossvalidation.TrainTestResults;
import learning.crossvalidation.splitting.Splitter;
import pipelines.*;
import pipelines.prepared.pipes.generic.*;
import pipelines.prepared.pipes.specific.TemplateToNeuralPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import training.NeuralModel;
import training.NeuralSample;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * TODO - decompose into smaller blocks
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
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("CrossvalidationPipeline");

        MultiMerge<TrainTestResults, TrainTestResults> resultsMultiMerge = pipeline.registerEnd(new MultiMerge<TrainTestResults, TrainTestResults>("ResultsAggregateMerge", sources.folds.size()) {
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

            if (sources.folds.stream().allMatch(fold -> fold.trainTest)) {  //folds are completely independent (train+test provided)

                List<Pipeline<Sources, TrainTestResults>> trainTestPipelines = trainTestBuilder.buildPipelines(sources.folds.size());
                trainTestPipelines.forEach(pipeline::register);

                foldsBranch.connectAfter(trainTestPipelines);


                resultsMultiMerge.connectBefore(trainTestPipelines);

            } else if (sources.folds.stream().allMatch(fold -> fold.testOnly)) { //train-test folds need to be assembled first from simple folds
                List<Pipe<Sources, Source>> testFoldPipes = new Pipe<Sources, Source>("GetTestFold") {
                    @Override
                    public Source apply(Sources sources) {
                        return sources.test;
                    }
                }.parallel(sources.folds.size());


                SamplesProcessingBuilder samplesExtractor = new SamplesProcessingBuilder(settings, sources.folds.get(0).test);
                List<Pipeline<Source, Stream<LogicSample>>> samplesExtract = samplesExtractor.buildPipelines(sources.folds.size());
                samplesExtract.forEach(pipeline::register);
                Pipe.connect(testFoldPipes, samplesExtract);

                if (sources.templateProvided) {

                    DuplicateBranch<Sources> duplicateOrigin = pipeline.registerStart(new DuplicateBranch<>());
                    TemplateProcessingBuilder templateProcessor = new TemplateProcessingBuilder(settings, sources);

                    Pipeline<Sources, Template> sourcesTemplatePipeline = null;
                    List<Pipeline<Sources, Template>> sourcesTemplatePipelines = null;
                    if (settings.commonTemplate) {

                        sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());

                        duplicateOrigin.connectAfterL(sourcesTemplatePipeline);
                        duplicateOrigin.connectAfterR(foldsBranch);
                        foldsBranch.connectAfter(testFoldPipes);

                    } else {

                        sourcesTemplatePipelines = templateProcessor.buildPipelines(sources.folds.size());

                        List<Branch<Sources, Sources, Sources>> parallel = duplicateOrigin.parallel(sources.folds.size());
                        pipeline.registerStart(foldsBranch);
                        foldsBranch.connectAfter(parallel);
                        Branch.connectAfterL(parallel, testFoldPipes);
                        Branch.connectAfterR(parallel, sourcesTemplatePipelines);
                    }


                    if (!settings.isolatedFoldsGrounding) { //ground all, then CV - most efficient mode

                        List<Pipeline<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults>> neuralTrainTestPipeline = trainTestBuilder.new NeuralTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                        neuralTrainTestPipeline.forEach(pipeline::register);
                        resultsMultiMerge.connectBefore(neuralTrainTestPipeline);

                        MultiMerge<Stream<NeuralSample>, Crossvalidation<NeuralSample>> neuralCrossvalidation = new MultiMerge<Stream<NeuralSample>, Crossvalidation<NeuralSample>>("NeuralCrossvalidation", sources.folds.size()) {
                            @Override
                            protected Crossvalidation<NeuralSample> merge(List<Stream<NeuralSample>> inputs) {
                                Crossvalidation<NeuralSample> cv = new Crossvalidation<>(settings, inputs.size());
                                cv.loadFolds(inputs);
                                return cv;
                            }
                        };

                        List<Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>>> groundingPipelines = new GroundingBuilder(settings).buildPipelines(sources.folds.size());

                        List<Merge<Template, Stream<LogicSample>, Pair<Template, Stream<LogicSample>>>> templateSamplesMerges = new PairMerge<Template, Stream<LogicSample>>().parallel(sources.folds.size());

                        neuralCrossvalidation.connectBefore(groundingPipelines);

                        Merge.connectBeforeR(templateSamplesMerges, samplesExtract);
                        Pipe.connect(templateSamplesMerges, groundingPipelines);


                        if (settings.commonTemplate) {
                            DuplicateBranch<Template> duplicateTemplate = new DuplicateBranch<>();
                            TemplateToNeuralPipe templateToNeuralPipe = new TemplateToNeuralPipe();

                            DuplicateListBranch<Template> templateListBranch = new DuplicateListBranch<>(sources.folds.size());
                            sourcesTemplatePipeline.connectAfter(duplicateTemplate);
                            duplicateTemplate.connectAfterL(templateListBranch);
                            duplicateTemplate.connectAfterR(templateToNeuralPipe);
                            Merge.connectBeforeL(templateSamplesMerges, templateListBranch.outputs);

                            PairMerge<NeuralModel, Crossvalidation<NeuralSample>> pairMerge = pipeline.register(new PairMerge<>());
                            pairMerge.connectBeforeL(templateToNeuralPipe);
                            pairMerge.connectBeforeR(neuralCrossvalidation);

                            MultiBranch<Pair<NeuralModel, Crossvalidation<NeuralSample>>, Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> pairMultiBranch = pipeline.register(new MultiBranch<Pair<NeuralModel, Crossvalidation<NeuralSample>>, Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>>("MergeFoldWithNeuralModel", sources.folds.size()) {
                                @Override
                                protected List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> branch(Pair<NeuralModel, Crossvalidation<NeuralSample>> cv) {
                                    List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> pairList = new ArrayList<>(cv.s.foldCount);
                                    for (int i = 0; i < cv.s.foldCount; i++) {
                                        pairList.add(new Pair<>(cv.r, new Pair<>(cv.s.folds.get(i).train.stream(), cv.s.folds.get(i).test.stream())));  //TODO consider parallel streaming of samples
                                    }
                                    return pairList;
                                }
                            });
                            pairMerge.connectAfter(pairMultiBranch);
                            pairMultiBranch.connectAfter(neuralTrainTestPipeline);

                        } else {
                            List<Branch<Template, Template, Template>> parallelTemplateBranches = new DuplicateBranch<Template>().parallel(sources.folds.size());
                            Pipe.connect(sourcesTemplatePipelines, parallelTemplateBranches);
                            List<Pipe<Template, Template>> identityPipes = new IdentityGenPipe<Template>().parallel(sources.folds.size());
                            Branch.connectAfterL(parallelTemplateBranches, identityPipes);
                            List<Pipe<Template, NeuralModel>> parallelNeuralModels = new TemplateToNeuralPipe().parallel(sources.folds.size());
                            Branch.connectAfterR(parallelTemplateBranches, parallelNeuralModels);
                            Merge.connectBeforeL(templateSamplesMerges, identityPipes);


                            ListMerge<NeuralModel> listMerge = pipeline.register(new ListMerge<>(sources.folds.size()));
                            listMerge.connectBefore(parallelNeuralModels);
                            Merge<List<NeuralModel>, Crossvalidation<NeuralSample>, List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>>> mergeCVTemplates = pipeline.register(new Merge<List<NeuralModel>, Crossvalidation<NeuralSample>, List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>>>("MergeCVTemplates") {
                                @Override
                                protected List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> merge(List<NeuralModel> templates, Crossvalidation<NeuralSample> cv) {
                                    List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> folds = new ArrayList<>(templates.size());
                                    for (int i = 0; i < templates.size(); i++) {
                                        folds.add(new Pair<>(templates.get(i), new Pair<>(cv.folds.get(i).train.stream(), cv.folds.get(i).test.stream())));
                                    }
                                    return folds;
                                }
                            });
                            mergeCVTemplates.connectBeforeL(listMerge);
                            mergeCVTemplates.connectBeforeR(neuralCrossvalidation);

                            ListBranch<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> listBranch = pipeline.register(new ListBranch<>(sources.folds.size()));
                            mergeCVTemplates.connectAfter(listBranch);
                            listBranch.connectAfter(neuralTrainTestPipeline);
                        }

                    } else { //repetively ground all train-test folds from scratch

                        List<Pipeline<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults>> logicTrainTestPipelines = trainTestBuilder.new LogicTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                        logicTrainTestPipelines.forEach(pipeline::register);
                        resultsMultiMerge.connectBefore(logicTrainTestPipelines);

                        MultiMerge<Stream<LogicSample>, Crossvalidation<LogicSample>> logicCrossvalidation = new MultiMerge<Stream<LogicSample>, Crossvalidation<LogicSample>>("LogicCrossvalidation", sources.folds.size()) {
                            @Override
                            protected Crossvalidation<LogicSample> merge(List<Stream<LogicSample>> inputs) {
                                Crossvalidation<LogicSample> cv = new Crossvalidation<>(settings, inputs.size());
                                cv.loadFolds(inputs);
                                return cv;
                            }
                        };
                        logicCrossvalidation.connectBefore(samplesExtract);

                        if (settings.commonTemplate) { //common template for all folds

                            PairMerge<Template, Crossvalidation<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
                            pairMerge.connectBeforeL(sourcesTemplatePipeline);
                            pairMerge.connectBeforeR(logicCrossvalidation);

                            MultiBranch<Pair<Template, Crossvalidation<LogicSample>>, Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> pairMultiBranch = pipeline.register(new MultiBranch<Pair<Template, Crossvalidation<LogicSample>>, Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>>("MergeFoldWithTemplate", sources.folds.size()) {
                                @Override
                                protected List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> branch(Pair<Template, Crossvalidation<LogicSample>> cv) {
                                    List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> pairList = new ArrayList<>(cv.s.foldCount);
                                    for (int i = 0; i < cv.s.foldCount; i++) {
                                        pairList.add(new Pair<>(cv.r, new Pair<>(cv.s.folds.get(i).train.stream(), cv.s.folds.get(i).test.stream())));  //TODO consider parallel streaming of samples
                                    }
                                    return pairList;
                                }
                            });
                            pairMerge.connectAfter(pairMultiBranch);
                            pairMultiBranch.connectAfter(logicTrainTestPipelines);

                        } else { //each fold has a separate template

                            ListMerge<Template> listMerge = pipeline.register(new ListMerge<>(sources.folds.size()));
                            listMerge.connectBefore(sourcesTemplatePipelines);
                            Merge<List<Template>, Crossvalidation<LogicSample>, List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>>> mergeCVTemplates = pipeline.register(new Merge<List<Template>, Crossvalidation<LogicSample>, List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>>>("MergeCVTemplates") {
                                @Override
                                protected List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> merge(List<Template> templates, Crossvalidation<LogicSample> cv) {
                                    List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> folds = new ArrayList<>(templates.size());
                                    for (int i = 0; i < templates.size(); i++) {
                                        folds.add(new Pair<>(templates.get(i), new Pair<>(cv.folds.get(i).train.stream(), cv.folds.get(i).test.stream())));
                                    }
                                    return folds;
                                }
                            });
                            mergeCVTemplates.connectBeforeL(listMerge);
                            mergeCVTemplates.connectBeforeR(logicCrossvalidation);

                            ListBranch<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> listBranch = pipeline.register(new ListBranch<>(sources.folds.size()));
                            mergeCVTemplates.connectAfter(listBranch);
                            listBranch.connectAfter(logicTrainTestPipelines);
                        }
                    }

                } else {    //structure learning must go with isolated folds anyway

                    MultiMerge<Stream<LogicSample>, Crossvalidation<LogicSample>> createFoldsMMerge = new MultiMerge<Stream<LogicSample>, Crossvalidation<LogicSample>>("CreateCvFolds", sources.folds.size()) {
                        @Override
                        protected Crossvalidation<LogicSample> merge(List<Stream<LogicSample>> inputs) {
                            Crossvalidation<LogicSample> cv = new Crossvalidation<>(settings, inputs.size());
                            cv.loadFolds(inputs);
                            return cv;
                        }
                    };
                    createFoldsMMerge.connectBefore(samplesExtract);

                    MultiBranch<Crossvalidation<LogicSample>, Pair<Stream<LogicSample>, Stream<LogicSample>>> pairMultiBranch = pipeline.register(new MultiBranch<Crossvalidation<LogicSample>, Pair<Stream<LogicSample>, Stream<LogicSample>>>("ExtractCVFolds", sources.folds.size()) {

                        @Override
                        protected List<Pair<Stream<LogicSample>, Stream<LogicSample>>> branch(Crossvalidation<LogicSample> cv) {
                            List<Pair<Stream<LogicSample>, Stream<LogicSample>>> folds = new ArrayList<>(cv.foldCount);
                            for (int i = 0; i < cv.foldCount; i++) {
                                folds.add(new Pair<>(cv.folds.get(i).train.stream(), cv.folds.get(i).test.stream()));
                            }
                            return folds;
                        }
                    });
                    pairMultiBranch.connectBefore(createFoldsMMerge);

                    List<Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults>> structTrainTestPipeline = trainTestBuilder.new StructureTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                    structTrainTestPipeline.forEach(pipeline::register);
                    pairMultiBranch.connectAfter(structTrainTestPipeline);

                    resultsMultiMerge.connectBefore(structTrainTestPipeline);
                }
            }

        } else {    //internal splitting

            Pipe<Sources, Source> getTrain = new Pipe<Sources, Source>("GetTrainFold") {
                @Override
                public Source apply(Sources sources) {
                    return sources.train;
                }
            };

            Pipeline<Source, Stream<LogicSample>> samplesExtract = new SamplesProcessingBuilder(settings, sources.train).buildPipeline(sources.train);

            if (sources.templateProvided) {
                Pipeline<Sources, Template> templatePipeline = new TemplateProcessingBuilder(settings, sources).buildPipeline();

                DuplicateBranch<Sources> sourcesDuplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
                sourcesDuplicateBranch.connectAfterL(templatePipeline);
                sourcesDuplicateBranch.connectAfterR(getTrain);
                getTrain.connectAfter(samplesExtract);

                if (settings.isolatedFoldsGrounding) { //assemble folds -> perform full logic learning including repetitive grounding on the train parts

                    List<Pipeline<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>, TrainTestResults>> logicTrainTestPipelines = trainTestBuilder.new LogicTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                    logicTrainTestPipelines.forEach(pipeline::register);
                    resultsMultiMerge.connectBefore(logicTrainTestPipelines);

                    Pipe<Stream<LogicSample>, Crossvalidation<LogicSample>> logicCrossvalidation = new Pipe<Stream<LogicSample>, Crossvalidation<LogicSample>>("CrossvalidationPipe") {
                        @Override
                        public Crossvalidation<LogicSample> apply(Stream<LogicSample> logicSampleStream) {
                            Crossvalidation<LogicSample> cv = new Crossvalidation<>(settings, settings.foldsCount);
                            cv.loadFolds(logicSampleStream);
                            return cv;
                        }
                    };

                    samplesExtract.connectAfter(logicCrossvalidation);

                    PairMerge<Template, Crossvalidation<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
                    pairMerge.connectBeforeL(templatePipeline);
                    pairMerge.connectBeforeR(logicCrossvalidation);

                    MultiBranch<Pair<Template, Crossvalidation<LogicSample>>, Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> pairMultiBranch = pipeline.register(new MultiBranch<Pair<Template, Crossvalidation<LogicSample>>, Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>>("MergeFoldWithTemplate", sources.folds.size()) {
                        @Override
                        protected List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> branch(Pair<Template, Crossvalidation<LogicSample>> cv) {
                            List<Pair<Template, Pair<Stream<LogicSample>, Stream<LogicSample>>>> pairList = new ArrayList<>(cv.s.foldCount);
                            for (int i = 0; i < cv.s.foldCount; i++) {
                                pairList.add(new Pair<>(cv.r, new Pair<>(cv.s.folds.get(i).train.stream(), cv.s.folds.get(i).test.stream())));  //TODO consider parallel streaming of samples
                            }
                            return pairList;
                        }
                    });
                    pairMerge.connectAfter(pairMultiBranch);
                    pairMultiBranch.connectAfter(logicTrainTestPipelines);
                    
                } else { //partition -> ground -> assemble train-test folds

                    DuplicateBranch<Template> duplicateTemplate = new DuplicateBranch<>();
                    templatePipeline.connectAfter(duplicateTemplate);
                    DuplicateListBranch<Template> templateListBranch = new DuplicateListBranch<>(sources.folds.size());
                    duplicateTemplate.connectAfterL(templateListBranch);

                    Pipe<Stream<LogicSample>, List<Stream<LogicSample>>> splitterPipe = new Pipe<Stream<LogicSample>, List<Stream<LogicSample>>>("SplitterPipe") {
                        @Override
                        public List<Stream<LogicSample>> apply(Stream<LogicSample> logicSampleStream) {
                            Splitter<LogicSample> splitter = Splitter.getSplitter(settings);
                            return splitter.partition(logicSampleStream, settings.foldsCount);
                        }
                    };
                    ListBranch<Stream<LogicSample>> foldsBranch = new ListBranch<>(sources.folds.size());
                    samplesExtract.connectAfter(splitterPipe);
                    splitterPipe.connectAfter(foldsBranch);

                    List<Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>>> groundingPipelines = new GroundingBuilder(settings).buildPipelines(sources.folds.size());

                    List<Merge<Template, Stream<LogicSample>, Pair<Template, Stream<LogicSample>>>> templateSamplesMerges = new PairMerge<Template, Stream<LogicSample>>().parallel(sources.folds.size());
                    Merge.connectBeforeL(templateSamplesMerges, templateListBranch.outputs);
                    Merge.connectBeforeR(templateSamplesMerges, foldsBranch.outputs);
                    Pipe.connect(templateSamplesMerges,groundingPipelines);

                    List<Pipeline<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>, TrainTestResults>> neuralTrainTestPipeline = trainTestBuilder.new NeuralTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                    neuralTrainTestPipeline.forEach(pipeline::register);
                    resultsMultiMerge.connectBefore(neuralTrainTestPipeline);

                    MultiMerge<Stream<NeuralSample>, Crossvalidation<NeuralSample>> neuralCrossvalidation = new MultiMerge<Stream<NeuralSample>, Crossvalidation<NeuralSample>>("NeuralCrossvalidation", sources.folds.size()) {
                        @Override
                        protected Crossvalidation<NeuralSample> merge(List<Stream<NeuralSample>> inputs) {
                            Crossvalidation<NeuralSample> cv = new Crossvalidation<>(settings, inputs.size());
                            cv.loadFolds(inputs);
                            return cv;
                        }
                    };

                    neuralCrossvalidation.connectBefore(groundingPipelines);

                    TemplateToNeuralPipe templateToNeuralPipe = new TemplateToNeuralPipe();
                    duplicateTemplate.connectAfterR(templateToNeuralPipe);

                    PairMerge<NeuralModel, Crossvalidation<NeuralSample>> pairMerge = pipeline.register(new PairMerge<>());
                    pairMerge.connectBeforeL(templateToNeuralPipe);
                    pairMerge.connectBeforeR(neuralCrossvalidation);

                    MultiBranch<Pair<NeuralModel, Crossvalidation<NeuralSample>>, Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> pairMultiBranch = pipeline.register(new MultiBranch<Pair<NeuralModel, Crossvalidation<NeuralSample>>, Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>>("MergeFoldWithNeuralModel", sources.folds.size()) {
                        @Override
                        protected List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> branch(Pair<NeuralModel, Crossvalidation<NeuralSample>> cv) {
                            List<Pair<NeuralModel, Pair<Stream<NeuralSample>, Stream<NeuralSample>>>> pairList = new ArrayList<>(cv.s.foldCount);
                            for (int i = 0; i < cv.s.foldCount; i++) {
                                pairList.add(new Pair<>(cv.r, new Pair<>(cv.s.folds.get(i).train.stream(), cv.s.folds.get(i).test.stream())));  //TODO consider parallel streaming of samples
                            }
                            return pairList;
                        }
                    });
                    pairMerge.connectAfter(pairMultiBranch);
                    pairMultiBranch.connectAfter(neuralTrainTestPipeline);

                }

            } else {


                Pipe<Stream<LogicSample>, Crossvalidation<LogicSample>> createFoldsMMerge = new Pipe<Stream<LogicSample>, Crossvalidation<LogicSample>>("CreateCvFolds") {
                    @Override
                    public Crossvalidation<LogicSample> apply(Stream<LogicSample> inputs) {
                        Crossvalidation<LogicSample> cv = new Crossvalidation<LogicSample>(settings);
                        cv.loadFolds(inputs);
                        return cv;
                    }
                };

                createFoldsMMerge.connectBefore(samplesExtract);

                MultiBranch<Crossvalidation<LogicSample>, Pair<Stream<LogicSample>, Stream<LogicSample>>> pairMultiBranch = pipeline.register(new MultiBranch<Crossvalidation<LogicSample>, Pair<Stream<LogicSample>, Stream<LogicSample>>>("ExtractCVFolds", sources.folds.size()) {

                    @Override
                    protected List<Pair<Stream<LogicSample>, Stream<LogicSample>>> branch(Crossvalidation<LogicSample> cv) {
                        List<Pair<Stream<LogicSample>, Stream<LogicSample>>> folds = new ArrayList<>(cv.foldCount);
                        for (int i = 0; i < cv.foldCount; i++) {
                            folds.add(new Pair<>(cv.folds.get(i).train.stream(), cv.folds.get(i).test.stream()));
                        }
                        return folds;
                    }
                });
                pairMultiBranch.connectBefore(createFoldsMMerge);

                List<Pipeline<Pair<Stream<LogicSample>, Stream<LogicSample>>, TrainTestResults>> structTrainTestPipeline = trainTestBuilder.new StructureTrainTestBuilder(settings).buildPipelines(sources.folds.size());
                structTrainTestPipeline.forEach(pipeline::register);
                pairMultiBranch.connectAfter(structTrainTestPipeline);

                resultsMultiMerge.connectBefore(structTrainTestPipeline);

            }

            if (settings.exportFolds) {
                //TODO
            }
        }
        return pipeline;
    }
}