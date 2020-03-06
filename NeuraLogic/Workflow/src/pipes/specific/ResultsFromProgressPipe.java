package pipes.specific;

import learning.results.Progress;
import learning.results.Results;
import pipelines.Pipe;

import java.util.logging.Logger;

public class ResultsFromProgressPipe extends Pipe<Progress, Results> {
    private static final Logger LOG = Logger.getLogger(ResultsFromProgressPipe.class.getName());

    public ResultsFromProgressPipe() {
        super("ResultsFromProgressPipe");
    }

    @Override
    public Results apply(Progress progress) {
        return progress.getLastTrueResults().training;
    }
}