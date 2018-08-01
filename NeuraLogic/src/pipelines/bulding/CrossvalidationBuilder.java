package pipelines.bulding;

import constructs.example.LogicSample;
import pipelines.MultiBranch;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import networks.evaluation.results.Results;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CrossvalidationBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(CrossvalidationBuilder.class.getName());
    public TrainTestBuilder trainTestBuilder;

    public CrossvalidationBuilder(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        SamplesProcessingBuilder samplesExtractor = new SamplesProcessingBuilder(settings);

        if (sources.foldFiles) {
            //load external folds sources.folds, perfrom crossvalidation

            MultiBranch<List<Sources>, Stream<LogicSample>> crossvalTrainBranch;
            crossvalTrainBranch = new MultiBranch<List<Sources>, Stream<LogicSample>>(ExternalTrainingCrossvalSplit) {
                @Override
                public Stream<Stream<LogicSample>> apply(List<Sources> sources) {


                }
            };


        } else {
            //split the dataset into folds - create sources, perform crossvalidation
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