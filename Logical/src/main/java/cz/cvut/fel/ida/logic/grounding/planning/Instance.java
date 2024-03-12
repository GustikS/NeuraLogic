package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class Instance {

    String name;
    Domain domain;

    List<Action> actions;
    Set<Literal> staticFacts;

    State initState;
    Clause goal;

    /**
     * A possible (optional) list of labeled states for training
     */
    @Nullable
    List<State> states;

    private SubsumptionEngineJ2.ClauseC goalC;

    public Instance(Set<Literal> staticFacts, State initState, Set<Literal> goalState) {
        this.staticFacts = staticFacts;
        this.initState = initState;
        this.goal = new Clause(goalState);
    }

    public SubsumptionEngineJ2.ClauseC getGoalC(Matching matching) {
        if (goalC != null) {
            return goalC;
        }
        goalC = matching.getEngine().createCluaseC(goal);
        return goalC;
    }

    public boolean isGoal(State state, Matching matching) {
        final Pair<Term[], List<Term[]>> listPair = matching.getEngine().allSolutions(goalC, state.getClauseE(matching), 1);
        if (listPair.s.isEmpty()) {
            return false;
        }
        return true;
    }
}