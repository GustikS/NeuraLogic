package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;
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
                final Pair<Term[], List<Term[]>> substitutions = getSubstitutions(state, action);
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

    public Pair<Term[], List<Term[]>> getSubstitutions(State state, Action action) {
        final SubsumptionEngineJ2.ClauseE clauseE = state.getClauseE(matching);
        final SubsumptionEngineJ2.ClauseC clauseC = action.getClauseC(matching);
        return matching.allSubstitutions(clauseC, clauseE, Integer.MAX_VALUE);
    }

    public Set<Action.GroundAction> groundActions(Action action, Pair<Term[], List<Term[]>> substitutions) {
        Set<Action.GroundAction> groundActions = new HashSet<>();
        for (Term[] terms : substitutions.s) {
            final Action.GroundAction groundAction = action.new GroundAction(action, substitutions.r, terms);
            groundAction.computeEffects(terms);
            groundActions.add(groundAction);
        }
        return groundActions;
    }

    /**
     * Non-destructive creation of a new State
     *
     * @param state
     * @param action
     * @return
     */
    public State nextState(State state, Action.GroundAction action) {
        Set<Literal> next = new HashSet<>();
        next.addAll(state.clause.literals());
        next.addAll(action.addEffects);
        action.deleteEffects.forEach(next::remove);
        return new State(new Clause(next));
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
