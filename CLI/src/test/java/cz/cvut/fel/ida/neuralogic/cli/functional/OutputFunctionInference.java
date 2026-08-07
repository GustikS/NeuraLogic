package cz.cvut.fel.ida.neuralogic.cli.functional;

import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeuron;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Output function inference rewrites the queried neuron's transformation so that a template need not care
 * whether the error function wants logits or probabilities. It has to stop short of a transformation the
 * template stated itself, or there is no way to say "this head is already the final quantity" - a model whose
 * output is a mean of probabilities could not be written at all.
 */
public class OutputFunctionInference {
    private static final Logger LOG = Logger.getLogger(OutputFunctionInference.class.getName());

    @TestAnnotations.Fast
    public void inferenceFillsInAnUnstatedTransformation() {
        Transformation result = infer(Transformation.Singletons.identity, false);
        assertTrue(result instanceof cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Sigmoid,
                "a template that says nothing still gets the activation the error function wants, got " + result);
    }

    @TestAnnotations.Fast
    public void inferenceLeavesAStatedTransformationAlone() {
        Transformation result = infer(Transformation.Singletons.identity, true);
        assertEquals(Transformation.Singletons.identity, result,
                "a transformation the template stated must survive");
    }

    private static Transformation infer(Transformation stated, boolean fromTemplate) {
        Settings settings = Settings.forFastTest();
        settings.inferOutputFcns = true;
        settings.errorFunction = Settings.ErrorFcn.CROSSENTROPY;
        settings.trainOnlineResultsType = Settings.ResultsType.CLASSIFICATION;

        State.Neural.Computation state = State.createBaseState(settings, null, stated);
        AtomNeuron<State.Neural.Computation> neuron = new AtomNeuron<>("queried", 0, state);
        neuron.transformationFromTemplate = fromTemplate;

        QueryNeuron query = new QueryNeuron("q", 0, 1.0, neuron, null);
        new NeuralProcessingSample(new ScalarValue(1.0), query, LearningSample.Split.TRAIN, settings);

        return neuron.getRawState().getComputationView(-1).getTransformation();
    }
}
