package networks.computation.evaluation.results;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class RegressionResults extends Results{
    private Double dispersion;
    private Double mse;

    public RegressionResults(List<Result> outputs) {
        super(outputs);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean recalculate() {
        return false;
    }
}