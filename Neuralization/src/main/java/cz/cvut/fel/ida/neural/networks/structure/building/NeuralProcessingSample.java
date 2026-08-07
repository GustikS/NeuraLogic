package cz.cvut.fel.ida.neural.networks.structure.building;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.ElementWise;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.functions.combination.Softmax;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.ReLu;
import cz.cvut.fel.ida.algebra.functions.transformation.elementwise.Sigmoid;
import cz.cvut.fel.ida.algebra.functions.transformation.joint.Identity;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.algebra.values.VectorValue;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralSample;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.QueryNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
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

        // Inferring the output function exists so that a template need not care whether the error function
        // wants logits or probabilities. A template that does state what its queried head computes is left
        // alone - otherwise there is no way to say "this head is already the final quantity", and an output
        // that is, say, a mean of probabilities cannot be expressed at all.
        if (settings.inferOutputFcns && q.neuron != null && !statedByTheTemplate(q.neuron)) {
            State.Neural.Computation computationState = q.neuron.getRawState().getComputationView(-1);
            ActivationFcn.State newFcnState = computationState.getFcnState();
            if (settings.trainOnlineResultsType == Settings.ResultsType.REGRESSION && !(q.neuron.getTransformation() == null ||
                    q.neuron.getTransformation() instanceof Identity || q.neuron.getTransformation() instanceof ReLu)) {
                newFcnState = computationState.getFcnState().changeTransformationState(Transformation.Singletons.identity);
            } else if (settings.errorFunction == Settings.ErrorFcn.SOFTENTROPY && !(q.neuron.getTransformation() == null || q.neuron.getTransformation() instanceof Identity)) {
                newFcnState = computationState.getFcnState().changeTransformationState(Transformation.Singletons.identity);
            } else if (settings.errorFunction == Settings.ErrorFcn.CROSSENTROPY && !(q.neuron.getTransformation() instanceof Softmax || q.neuron.getTransformation() instanceof Sigmoid)) {
                if (v instanceof VectorValue) {
                    newFcnState = computationState.getFcnState().changeTransformationState(Transformation.Singletons.softmax);
                } else if (v instanceof ScalarValue) {
                    newFcnState = computationState.getFcnState().changeTransformationState(ElementWise.Singletons.sigmoid);
                }
            }
            computationState.setFcnState(newFcnState);
        }

        try {
            detailedNetwork = (DetailedNetwork) q.evidence;     //todo remove this class cast by refactoring inside grounder
        } catch (
                ClassCastException e) {
            LOG.severe("Inappropriate use of NeuralProcessingSample. Use Grounder to create a DetailedNetwork based sample.");
        }
    }

    /**
     * Whether the queried neuron's transformation came from the template rather than from a default -
     * {@link cz.cvut.fel.ida.neural.networks.structure.building.NeuronFactory} records that as it creates the
     * atom neurons, since only there is it still known which of the two it was.
     */
    private static boolean statedByTheTemplate(AtomNeurons<?> queried) {
        return queried instanceof BaseNeuron && ((BaseNeuron<?, ?>) queried).transformationFromTemplate;
    }
}
