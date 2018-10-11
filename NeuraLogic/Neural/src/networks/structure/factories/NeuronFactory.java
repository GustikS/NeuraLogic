package networks.structure.factories;

import constructs.template.HeadAtom;
import networks.structure.networks.State;
import networks.structure.networks.States;
import networks.structure.neurons.creation.*;
import settings.Settings;

import java.util.logging.Logger;

public class NeuronFactory {
    private static final Logger LOG = Logger.getLogger(NeuronFactory.class.getName());

    Settings settings;

    private int index;

    public NeuronFactory(Settings settings) {
        this.settings = settings;
    }


    State.Computation getComputationState() {
        if (settings.minibatch){
            return new States.ComputationArray<>(new State.Computation.Pair[settings.minibatchSize]);
        }
    }

    public <S extends State.Computation> AtomNeuron<S> createAtomNeuron(HeadAtom head) {
        S state = getComputationState();
        AtomNeuron<S> atomNeuron = new AtomNeuron<S>(head, index++, state);
    }

    public <S extends State.Computation> AggregationNeuron<S> createAggNeuron() {

    }

    public <S extends State.Computation> RuleNeuron<S> createRuleNeuron() {

    }


    public FactNeuron createFactoNeuron() {

    }

    public <S extends State.Computation> NegationNeuron<S> createNegationNeuron() {

    }

    public <S extends State.Computation> WeightedRuleNeuron<S> createWeightedRuleNeuron() {

    }
}