package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Instance {

    public String name;
    public Domain domain;

    public List<Action> actions;
    public List<Literal> staticFacts;

    public State initState;
    Clause goal;

    /**
     * A possible (optional) list of labeled states for training
     */
    @Nullable
    List<State> states;

    private SubsumptionEngineJ2.ClauseC goalC;

    public Instance(String name, List<Literal> staticFacts, List<Literal> initState, List<Literal> goalState, List<Action> actions) {
        this.name = name;
        this.staticFacts = staticFacts;
        initState.addAll(staticFacts);
        this.initState = new State(initState);
        goalState.addAll(staticFacts);
        this.goal = new Clause(goalState);
        this.actions = actions;
    }

    public SubsumptionEngineJ2.ClauseC getGoalC(Matching matching) {
        if (goalC != null) {
            return goalC;
        }
        this.goalC = matching.getEngine().createClauseC(goal);
        return goalC;
    }

    public boolean isGoal(State state, Matching matching) {
        final Pair<Term[], List<Term[]>> listPair = matching.getEngine().allSolutions(getGoalC(matching), state.getClauseE(matching), 1);
        if (listPair.s.isEmpty()) {
            return false;
        }
        return true;
    }
}