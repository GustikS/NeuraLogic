package cz.cvut.fel.ida.neural.networks.structure.building;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

public class NeuralProcessingSample extends NeuralSample {
    private static final Logger LOG = Logger.getLogger(NeuralProcessingSample.class.getName());

    public DetailedNetwork detailedNetwork;

    public NeuralProcessingSample(Value v, QueryNeuron q, Split type, Settings settings) {
        super(v, q, type);

        // if a non-scalar target value is detected, switch the whole evaluation to basic (non-binary) classification results
        if (!(v instanceof ScalarValue)) {
            if (settings.trainOnlineResultsType == Settings.ResultsType.CLASSIFICATION) {
                settings.trainRecalculationResultsType = Settings.ResultsType.CLASSIFICATION;
                settings.validationResultsType = Settings.ResultsType.CLASSIFICATION;
                settings.testResultsType = Settings.ResultsType.CLASSIFICATION;
            }
        }

        try {
            detailedNetwork = (DetailedNetwork) q.evidence;     //todo remove this class cast by refactoring inside grounder
        } catch (
                ClassCastException e) {
            LOG.severe("Inappropriate use of NeuralProcessingSample. Use Grounder to create a DetailedNetwork based sample.");
        }
    }
}
