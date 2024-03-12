package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.learning.Example;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;

import java.util.Set;

public class State implements Example {

    int distance = -1;
    Clause clause;

    private SubsumptionEngineJ2.ClauseE clauseE;

    public State(Clause clause) {
        this.clause = clause;
    }

    public SubsumptionEngineJ2.ClauseE getClauseE(Matching matching) {
        if (this.clauseE != null) {
            return this.clauseE;
        }
        clauseE = matching.getEngine().new ClauseE(clause);
        return clauseE;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Integer getNeuronCount() {
        return null;
    }
}
