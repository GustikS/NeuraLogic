package networks.computation.evaluation.results;

import settings.Settings;
import utils.exporting.Exporter;

import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class RegressionResults extends Results {

    public RegressionResults(List<Result> outputs, Settings aggregationFcn) {
        super(outputs, aggregationFcn);
    }

    @Override
    public String toString() {
//        return Arrays.toString(evaluations.toArray());
        return super.toString();
    }

    @Override
    public boolean recalculate() {
        return false;
    }

    @Override
    public boolean betterThan(Results other) {
        return other.error.greaterThan(error);      //todo now fail sooner than here if no entailed examples! :)
    }

    @Override
    public String toString(Settings settings) {
        return null;
    }

    @Override
    public RegressionResults export(Exporter exporter) {
        exporter.export(this);
        return this;
    }
}