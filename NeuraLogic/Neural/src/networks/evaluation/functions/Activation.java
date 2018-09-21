package networks.evaluation.functions;

import networks.evaluation.values.Value;
import networks.structure.weights.Weight;
import networks.structure.neurons.Neurons;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public interface Activation {
    <T extends Neurons> Value evaluate(ArrayList<T> inputs);
    <T extends Neurons> Value evaluate(ArrayList<T> inputs, ArrayList<Weight> weights);

    Value differentiateAt(Value x);
    Activation differentiateGlobally();


}