package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.learning.Example;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class State implements Example {

    public Clause clause;

    private SubsumptionEngineJ2.ClauseE clauseE;

    public State(String literals) {
        this.clause = Clause.parse(literals);
    }

    public State(List<Literal> literals) {
        this.clause = new Clause(literals);
    }

    public State(Clause clause) {
        this.clause = clause;
    }

    public State(Clause clause, SubsumptionEngineJ2.ClauseE clauseE) {
        this.clause = clause;
        this.clauseE = clauseE;
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

    @Override
    public String toString() {
        return clause.toString();
    }

    public static interface Label {

    }
}
