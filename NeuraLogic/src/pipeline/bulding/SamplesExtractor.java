package pipeline.bulding;

import learning.LearningSample;
import pipeline.Pipeline;
import settings.Settings;
import settings.Sources;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class SamplesExtractor extends AbstractPipelineBuilder<Sources, Stream<LearningSample>> {
    private static final Logger LOG = Logger.getLogger(SamplesExtractor.class.getName());

    public SamplesExtractor(Settings settings) {
        super(settings);
    }

    @Override
    public Pipeline<Sources, Stream<LearningSample>> buildPipeline(Sources sources) {
        if (sources.foldFiles) {
            for (Sources fold : sources.folds) {
                //load external folds sources.folds, perfrom crossvalidation
            }

        } else if (settings.crossvalidation) {
            //split the dataset into folds, perform crossvalidation
            //TODO check for inconsistent settings, e.g. no test-set should be provided here
        } else {
            //
        }


        //TODO next

    }



}
