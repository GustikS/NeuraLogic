package networks.structure.lrnnTypes;

import constructs.example.LiftedExample;
import learning.Model;
import learning.Query;
import networks.evaluation.values.Value;
import networks.structure.NeuralNetwork;
import networks.structure.Neuron;

import java.util.Optional;

/**
 * Created by gusta on 11.3.17.
 */
public class QueryNeuron extends Neuron implements Query{

    NeuralNetwork context;

    @Override
    public Optional<LiftedExample> getEvidence() {
        return context;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Value evaluate(Model template) {
        return null;
    }

    public Value evaluate() {
        return null;
    }
}