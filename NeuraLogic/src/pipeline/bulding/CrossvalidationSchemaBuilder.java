package pipeline.bulding;

import learning.LearningSample;
import pipeline.MultiBranch;
import pipeline.Pipeline;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class CrossvalidationSchemaBuilder<T> {
    private static final Logger LOG = Logger.getLogger(CrossvalidationSchemaBuilder.class.getName());

    private final Settings settings;

    public CrossvalidationSchemaBuilder(Settings settings) {
        this.settings = settings;
    }

    public Pipeline<Sources, T> buildSchema(Sources sources) {
        SamplesExtractor samplesExtractor = new SamplesExtractor(settings);

        if (sources.foldFiles) {
            //load external folds sources.folds, perfrom crossvalidation

            MultiBranch<Stream<Sources>, Stream<LearningSample>> crossvalTrainBranch;
            crossvalTrainBranch = new MultiBranch<Stream<Sources>, Stream<LearningSample>>("ExternalTrainingCrossvalSplit") {
                @Override
                public Stream<Stream<LearningSample>> apply(Stream<Sources> sourcesStream) {
                    Stream<Stream<LearningSample>> crossStream = sourcesStream.parallel().map(samplesExtractor::getTrainingSamples);
                    return crossStream;
                }
            };

            Training training = new Training(settings);


            MultiBranch<Stream<Sources>, Stream<LearningSample>> crossvalTestBranch;
            crossvalTestBranch = new MultiBranch<Stream<Sources>, Stream<LearningSample>>("ExternalTestingCrossvalSplit") {
                @Override
                public Stream<Stream<LearningSample>> apply(Stream<Sources> sourcesStream) {
                    Stream<Stream<LearningSample>> crossStream = sourcesStream.parallel().map(samplesExtractor::getTestingSamples);
                    return crossStream;
                }

            };


        } else if (settings.crossvalidation) {
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