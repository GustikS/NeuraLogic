package cz.cvut.fel.ida.algebra.functions.combination;

import cz.cvut.fel.ida.algebra.functions.ActivationFcn;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MixedCombination implements Combination {
    private static final Logger LOG = Logger.getLogger(MixedCombination.class.getName());

    public MixedCombinationNode root = null;

    private final String name;

    public MixedCombination(String name, MixedCombinationNode root) {
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
        return evaluate(root, inputs);
    }

    private Value evaluate(MixedCombinationNode node, List<Value> values) {
        List<Value> nextValues = new ArrayList<>(2);

        if (node.leftNode == null) {
            nextValues.add(values.get(node.leftIndex));
        } else {
            nextValues.add(evaluate(node.leftNode, values));
        }

        if (node.rightNode == null) {
            nextValues.add(values.get(node.rightIndex));
        } else {
            nextValues.add(evaluate(node.rightNode, values));
        }

        return node.combination.evaluate(nextValues);
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

        private final MixedCombinationNode root;

        public State(MixedCombination combination) {
            super(combination);

            root = combination.root;
            initStates(root);
        }

        private void initStates(MixedCombinationNode node) {
            node.state = node.combination.getState(false);
            if (node.leftNode != null) {
                initStates(node.leftNode);
            }
            if (node.rightNode != null) {
                initStates(node.rightNode);
            }
        }

        @Override
        public Value evaluate() {
            return evaluate(root);
        }

        private Value evaluate(MixedCombinationNode node) {
            if (node.leftNode == null) {
                node.state.cumulate(accumulatedInputs.get(node.leftIndex));
            } else {
                node.state.cumulate(evaluate(node.leftNode));
            }

            if (node.rightNode == null) {
                node.state.cumulate(accumulatedInputs.get(node.rightIndex));
            } else {
                node.state.cumulate(evaluate(node.rightNode));
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

        private void nextInputGradient(MixedCombinationNode node, Value grad) {
            node.state.ingestTopGradient(grad);

            if (node.leftNode != null) {
                nextInputGradient(node.leftNode, node.state.nextInputGradient());
            } else {
                processedGradients.set(node.leftIndex, node.state.nextInputGradient());
            }

            if (node.rightNode != null) {
                nextInputGradient(node.rightNode, node.state.nextInputGradient());
            } else {
                processedGradients.set(node.rightIndex, node.state.nextInputGradient());
            }
        }

        @Override
        public void invalidate() {
            super.invalidate();
            invalidate(root);
        }

        private void invalidate(MixedCombinationNode node) {
            node.state.invalidate();
            if (node.leftNode != null) {
                invalidate(node.leftNode);
            }
            if (node.rightNode != null) {
                invalidate(node.rightNode);
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

        private Value initEval(MixedCombinationNode node, List<Value> values) {
            List<Value> nextValues = new ArrayList<>(2);

            if (node.leftNode == null) {
                nextValues.add(values.get(node.leftIndex));
            } else {
                nextValues.add(initEval(node.leftNode, values));
            }

            if (node.rightNode == null) {
                nextValues.add(values.get(node.rightIndex));
            } else {
                nextValues.add(initEval(node.rightNode, values));
            }

            return node.state.initEval(nextValues);
        }
    }

    public static class MixedCombinationNode {
        public int leftIndex = -1;
        public int rightIndex = -1;

        public MixedCombinationNode leftNode = null;

        public MixedCombinationNode rightNode = null;

        public Combination combination = null;

        public ActivationFcn.State state = null;

        public MixedCombinationNode(Combination combination, MixedCombinationNode leftNode, MixedCombinationNode rightNode, int leftIndex, int rightIndex) {
            this.combination = combination;
            this.leftNode = leftNode;
            this.rightNode = rightNode;
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
        }
    }
}
