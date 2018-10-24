package networks.computation.results;

/**
 * Created by gusta on 8.3.17.
 */
public class RegressionResults extends Results{
    private Double dispersion;
    private Double mse;

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean calculate() {
        return false;
    }
}