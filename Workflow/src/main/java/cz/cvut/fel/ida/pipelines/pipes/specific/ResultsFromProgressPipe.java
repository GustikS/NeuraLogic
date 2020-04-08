package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.learning.results.Results;
import cz.cvut.fel.ida.pipelines.Pipe;

import java.util.logging.Logger;

public class ResultsFromProgressPipe extends Pipe<Progress, Results> {
    private static final Logger LOG = Logger.getLogger(ResultsFromProgressPipe.class.getName());

    public ResultsFromProgressPipe() {
        super("ResultsFromProgressPipe");
    }

    @Override
    public Results apply(Progress progress) {
        if (progress.bestResults.validation != null && !progress.bestResults.validation.isEmpty())
            return progress.bestResults.validation;
        else
            return progress.bestResults.training;
    }
}