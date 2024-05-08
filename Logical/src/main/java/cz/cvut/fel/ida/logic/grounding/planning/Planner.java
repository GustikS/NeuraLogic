package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.util.*;

public class Planner {

    /**
     * The grounding (theta-subsumption) engine
     */
    public Matching matching = new Matching();

    public List<Pair<Action.GroundAction, State>> solveGreedy(Instance instance) {
        List<Pair<Action.GroundAction, State>> plan = new LinkedList<>();
        Set<State> closed = new HashSet<>();

        State state = instance.initState;
        plan.add(new Pair<>(null, state));
        while (!instance.isGoal(state, matching)) {
            Set<Action.GroundAction> possibleActions = new HashSet<>();
            for (Action action : instance.actions) {
                final Action.Substitutions substitutions = getSubstitutions(state, action);
                possibleActions.addAll(groundActions(action, substitutions));
            }
            List<Pair<Action.GroundAction, Double>> actionScores = scoreActions(state, possibleActions);
            for (Pair<Action.GroundAction, Double> actionScore : actionScores) {
                State nextState = nextState(state, actionScore.r);
                if (!closed.contains(nextState)) {
                    state = nextState;
                    closed.add(nextState);  // just check for cycles...
                    plan.add(new Pair<>(actionScore.r, nextState));
                    break;
                }
            }
        }
        return plan;
    }

    private List<Pair<Action.GroundAction, Double>> scoreActions(State state, Set<Action.GroundAction> groundActions) {
        List<Pair<Action.GroundAction, Double>> scores = new ArrayList<>();
        for (Action.GroundAction groundAction : groundActions) {
            scores.add(new Pair<>(groundAction, 0.0));  // todo no scoring just yet
        }
        return scores;
    }

    public Action.Substitutions getSubstitutions(State state, Action action) {
        return action.substitutions(state);
    }

    public Set<Action.GroundAction> groundActions(Action action, Action.Substitutions substitutions) {
        return action.groundings(substitutions);
    }

    /**
     * Non-destructive creation of a new State
     *
     * @param state
     * @param action
     * @return
     */
    public State nextState(State state, Action.GroundAction action) {
        return action.successor(state);
    }

    /**
     * Destructive (in-place) application of a GroundAction
     *
     * @param state
     * @param action
     */
    public void applyAction(State state, Action.GroundAction action) {
        state.clause.addLiterals(action.addEffects);
        for (Literal deleteEffect : action.deleteEffects) {
            state.clause.removeLiteral(deleteEffect);
        }
    }
}
