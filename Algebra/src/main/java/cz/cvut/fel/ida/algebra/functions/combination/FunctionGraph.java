package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FunctionGraph implements Combination {
    private static final Logger LOG = Logger.getLogger(FunctionGraph.class.getName());

    public FunctionGraphNode root = null;

    private final String name;

    public FunctionGraph(String name, FunctionGraphNode root) {
        this.root = root;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Combination replaceWithSingleton() {
        return null;
    }

    @Override
    public Value evaluate(List<Value> inputs) {
        return null;
    }

    @Override
    public boolean isPermutationInvariant() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public ActivationFcn.State getState(boolean singleInput) {
        return new State(this);
    }

    public static class State extends InputArrayState {
        private List<Value> processedGradients = null;

        private final FunctionGraphNode root;

        public State(FunctionGraph combination) {
            super(combination);

            root = initStates(combination.root);
        }

        private FunctionGraphNode initStates(FunctionGraphNode node) {
            FunctionGraphNode[] nodes = new FunctionGraphNode[node.nodes.length];

            for (int i = 0; i < node.nodes.length; i++) {
                if (node.nodes[i] != null) {
                    nodes[i] = initStates(node.nodes[i]);
                }
            }

            FunctionGraphNode root = new FunctionGraphNode(node.function, nodes, node.indices);
            root.state = root.function.getState(node.nodes.length == 1);

            return root;
        }

        @Override
        public Value evaluate() {
            return evaluate(root);
        }

        private Value evaluate(FunctionGraphNode node) {
            for (int i = 0; i < node.nodes.length; i++) {
                if (node.nodes[i] == null) {
                    node.state.cumulate(accumulatedInputs.get(node.indices[i]));
                } else {
                    node.state.cumulate(evaluate(node.nodes[i]));
                }
            }

            return node.state.evaluate();
        }

        @Override
        public void cumulate(Value value) {
            super.cumulate(value);
        }

        @Override
        public void ingestTopGradient(Value topGradient) {
            processedGradient = topGradient;
        }

        @Override
        public Value nextInputGradient() {
            if (i == 0) {
                nextInputGradient(root, processedGradient);
            }

            return processedGradients.get(i++);
        }

        private void nextInputGradient(FunctionGraphNode node, Value grad) {
            node.state.ingestTopGradient(grad);

            for (int i = 0; i < node.nodes.length; i++) {
                if (node.nodes[i] != null) {
                    nextInputGradient(node.nodes[i], node.state.nextInputGradient());
                } else {
                    final Value gradAtIndex = processedGradients.get(node.indices[i]);

                    if (gradAtIndex == null) {
                        processedGradients.set(node.indices[i], node.state.nextInputGradient());
                    } else {
                        processedGradients.set(node.indices[i], gradAtIndex.plus(node.state.nextInputGradient()));
                    }
                }
            }
        }

        @Override
        public void invalidate() {
            super.invalidate();

            for (int i = 0; i < processedGradients.size(); i++) {
                processedGradients.set(i, null);
            }

            invalidate(root);
        }

        private void invalidate(FunctionGraphNode node) {
            node.state.invalidate();

            for (int i = 0; i < node.nodes.length; i++) {
                if (node.nodes[i] != null) {
                    invalidate(node.nodes[i]);
                }
            }
        }

        @Override
        public Value initEval(List<Value> values) {
            processedGradients = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++) {
                processedGradients.add(null);
            }

            accumulatedInputs = (ArrayList<Value>) values;
            accumulatedInputs.trimToSize();
            i = 0;

            return initEval(root, values);
        }

        private Value initEval(FunctionGraphNode node, List<Value> values) {
            List<Value> nextValues = new ArrayList<>(node.indices.length);

            for (int i = 0; i < node.indices.length; i++) {
                if (node.nodes[i] == null) {
                    nextValues.add(values.get(node.indices[i]));
                } else {
                    nextValues.add(initEval(node.nodes[i], values));
                }
            }

            return node.state.initEval(nextValues);
        }
    }

    public static class FunctionGraphNode {
        public int[] indices;

        public FunctionGraphNode[] nodes;

        public ActivationFcn function = null;

        public ActivationFcn.State state = null;

        public FunctionGraphNode(ActivationFcn function, FunctionGraphNode[] nodes, int[] indices) {
            this.function = function;
            this.nodes = nodes;
            this.indices = indices;
        }
    }
}
