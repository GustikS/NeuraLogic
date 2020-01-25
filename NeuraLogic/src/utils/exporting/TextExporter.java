package utils.exporting;

import learning.crossvalidation.TrainTestResults;
import networks.computation.evaluation.results.Progress;
import networks.computation.evaluation.results.Results;
import settings.Settings;

import java.util.logging.Logger;

public class TextExporter extends Exporter {
    private static final Logger LOG = Logger.getLogger(TextExporter.class.getName());

    public TextExporter(Settings settings, String id) {
        super(settings, id);
    }


    public void export(TrainTestResults trainTestResults) {
        resultsLine("TrainTestResults:");
        trainTestResults.training.bestResults.export(this);
        resultsLine("Testing:");
        trainTestResults.testing.export(this);
    }

    public void export(Results results) {
//        resultsLine("Results:");
        resultsLine(results.toString(settings));
    }

    public void export(Progress.TrainVal trainVal) {
        resultsLine("TrainVal:");
        resultsLine("Training:");
        trainVal.training.export(this);
        resultsLine("Validation:");
        trainVal.validation.export(this);
    }

    public void export(Progress progress) {
        resultsLine("Progress:");
        resultsLine("BestResults:");
        progress.bestResults.export(this);
        resultsLine("Training");
        for (Progress.Restart restart : progress.restarts) {
            resultsLine("onlineTrainingResults-----------------");
            for (Results onlineTrainingResult : restart.onlineTrainingResults) {
                export(onlineTrainingResult);
            }
            resultsLine("trueTrainingResults----------------");
            for (Results trueTrainingResult : restart.trueTrainingResults) {
                export(trueTrainingResult);
            }
            resultsLine("validationResults------------------");
            for (Results validationResult : restart.validationResults) {
                export(validationResult);
            }
        }
    }
}
