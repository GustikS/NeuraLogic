package cz.cvut.fel.ida.neural.networks.computation.iteration.modes;

import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.NeuralNetwork;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.Neurons;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.State;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.AtomNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.types.FactNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.types.TopologicNetwork;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The top-down iteration has to reach the very first neuron in the topologic order, too. It has no inputs to
 * propagate into, but it still carries its own offset - and for a {@link FactNeuron} that offset is its
 * learnable value, so skipping it silently freezes that fact. Ordinary templates never noticed because the
 * first neuron is typically a constant fact, whose visit is a no-op anyway.
 */
public class TopologicIterationTest {
    private static final Logger LOG = Logger.getLogger(TopologicIterationTest.class.getName());

    @TestAnnotations.Fast
    public void topDownVisitsTheFirstNeuronInTheOrder() {
        Settings settings = Settings.forFastTest();

        FactNeuron first = new FactNeuron("learnableFact",
                new Weight(0, "factValue", new ScalarValue(1.0), false, true),
                0, new States.SimpleValue(new ScalarValue(1.0)));
        first.hasLearnableValue = true;

        AtomNeuron<State.Neural.Computation> output = new AtomNeuron<>("output", 1,
                State.createBaseState(settings, null, Transformation.Singletons.identity));

        List<BaseNeuron<Neurons, State.Neural>> topologicOrder = new ArrayList<>();
        topologicOrder.add((BaseNeuron) first);
        topologicOrder.add((BaseNeuron) output);
        TopologicNetwork<State.Neural.Structure> network = new TopologicNetwork<>("test", topologicOrder, true);

        List<Neurons> visited = new ArrayList<>();
        new Topologic(network).new TDownVisitor(output, new Recorder(network, visited)).topdown();

        assertTrue(visited.contains(first),
                "the neuron at topologic index 0 was never visited, so its own offset gets no update");
    }

    private static class Recorder extends NeuronVisitor.Weighted {
        private final List<Neurons> visited;

        Recorder(NeuralNetwork<State.Neural.Structure> network, List<Neurons> visited) {
            super(network, null, null);
            this.visited = visited;
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(BaseNeuron<T, S> neuron) {
            visited.add(neuron);
        }

        @Override
        public <T extends Neurons, S extends State.Neural> void visit(WeightedNeuron<T, S> neuron) {
            visited.add(neuron);
        }
    }
}
