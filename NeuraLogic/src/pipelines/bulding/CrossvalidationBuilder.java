package pipelines.bulding;

import learning.crossvalidation.Crossvalidation;
import learning.crossvalidation.TrainTestResults;
import networks.evaluation.results.Results;
import pipelines.MultiBranch;
import pipelines.MultiMerge;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CrossvalidationBuilder extends AbstractPipelineBuilder<Sources, TrainTestResults> {
    private static final Logger LOG = Logger.getLogger(CrossvalidationBuilder.class.getName());
    private Sources sources;
    public TrainTestBuilder trainTestBuilder;

    public CrossvalidationBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, TrainTestResults> buildPipeline() {
        return buildPipeline(sources);
    }

    public Pipeline<Sources, TrainTestResults> buildPipeline(Sources sources) {
        Pipeline<Sources, TrainTestResults> pipeline = new Pipeline<>("LearningSchemePipeline");

        if (sources.foldFiles) { //external pre-split folds by user
            if (sources.folds.stream().allMatch(fold -> fold.trainTest)) {  //folds are completely independent (train+test provided)

                MultiBranch<Sources, Sources> multiBranch = pipeline.registerStart(new MultiBranch<Sources, Sources>("FoldsBranch", sources.folds.size()) {
                    @Override
                    protected List<Sources> branch(Sources folds) {
                        return folds.folds;
                    }
                });

                TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings, sources);
                List<Pipeline<Sources, TrainTestResults>> trainTestPipelines = trainTestBuilder.buildPipelines(sources.folds.size());
                trainTestPipelines.forEach(pipeline::register);

                multiBranch.connectAfter(trainTestPipelines);

                MultiMerge<TrainTestResults, TrainTestResults> multiMerge = pipeline.registerEnd(new MultiMerge<TrainTestResults, TrainTestResults>("ResultsAggregateMerge", sources.folds.size()) {
                    @Override
                    protected TrainTestResults merge(List<TrainTestResults> inputs) {
                        Crossvalidation crossvalidation = new Crossvalidation();
                        return crossvalidation.aggregateResults(inputs);
                    }
                });
                multiMerge.connectBefore(trainTestPipelines);

            } else if (sources.folds.stream().allMatch(fold -> fold.testOnly)) { //train-test folds need to be created first
                if (settings.isolatedFoldsGrounding) {

                } else {

                }
                SamplesProcessingBuilder samplesExtractor = new SamplesProcessingBuilder(settings, sources.train);
            }


        } else {    //internal splitting
            if (settings.isolatedFoldsGrounding) {
                //first split the source and then ground
            } else {
                //first ground the examples and everything and then split
            }


            //TODO check for inconsistent settings, e.g. no test-set should be provided here

            if (settings.exportFolds) {
                //TODO
            }
        }
    }


}