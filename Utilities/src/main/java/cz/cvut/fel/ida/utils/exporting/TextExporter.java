package cz.cvut.fel.ida.utils.exporting;

import java.util.logging.Logger;

/**
 * This is an antipattern, exporting shouldn't be dependent on the particular classes, move it to toString method of each class
 */
@Deprecated
public class TextExporter extends Exporter {
    private static final Logger LOG = Logger.getLogger(TextExporter.class.getName());

    public TextExporter(String exportDir, String id) {
        super(exportDir, id, "TEXT");
    }

    @Override
    public <I> void export(Exportable iExportable) {
        exportLine(iExportable.toString());
    }

    /*
    public void export(TrainTestResults trainTestResults) {
        exportLine("TrainTestResults:");
        trainTestResults.training.bestResults.export(this);
        exportLine("Testing:");
        trainTestResults.testing.export(this);
    }

    public void export(Results results) {
//        resultsLine("Results:");
        exportLine(results.toString());
    }

    public void export(Progress.TrainVal trainVal) {
        exportLine("TrainVal:");
        exportLine("Training:");
        trainVal.training.export(this);
        exportLine("Validation:");
        trainVal.validation.export(this);
    }

    public void export(Progress progress) {
        exportLine("Progress:");
        exportLine("BestResults:");
        progress.bestResults.export(this);
        exportLine("Training");
        for (Progress.Restart restart : progress.restarts) {
            exportLine("onlineTrainingResults-----------------");
            for (Results onlineTrainingResult : restart.onlineTrainingResults) {
                export(onlineTrainingResult);
            }
            exportLine("trueTrainingResults----------------");
            for (Results trueTrainingResult : restart.trueTrainingResults) {
                export(trueTrainingResult);
            }
            exportLine("validationResults------------------");
            for (Results validationResult : restart.validationResults) {
                export(validationResult);
            }
        }
    }
 */
}
