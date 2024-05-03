package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.Variable;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;

import java.util.*;

public class Action {

    public String name;

    public List<Literal> preconditions;
    public List<Literal> addEffects;
    public List<Literal> deleteEffects;

    public List<Term> terms;
    /**
     * An auxiliary literal representing the action, to be added
     */
    public Literal applicable;

    private SubsumptionEngineJ2.ClauseC preconditionsC;

    public Action(String name, List<Literal> preconditions, List<Literal> addEffects, List<Literal> deleteEffects) {
        this.name = name;
        this.preconditions = preconditions;
        this.addEffects = addEffects;
        this.deleteEffects = deleteEffects;

        Set<Variable> vars = new HashSet<>();
        for (Literal precondition : preconditions) {
            for (Term term : precondition.termList()) {
                if (term instanceof Variable) {
                    vars.add((Variable) term);
                }
            }
        }
        this.applicable = new Literal(name, new ArrayList<>(vars));
    }

    public SubsumptionEngineJ2.ClauseC getClauseC(Matching matching) {
        if (preconditionsC != null) {
            return preconditionsC;
        }
        final Clause clause = new Clause(preconditions);
        preconditionsC = matching.getEngine().createClauseC(clause);
        return preconditionsC;
    }

    public class GroundAction {
        public Action lifted;

        public Literal applicable;

        public List<Literal> preconditions;
        public List<Literal> addEffects;
        public List<Literal> deleteEffects;

        public GroundAction(Action lifted, Term[] variables, Term[] substitution) {
            this.lifted = lifted;
            for (int i = 0; i < variables.length; i++) {
                variables[i].setIndexWithinSubstitution(i);
            }
            this.applicable = lifted.applicable.subsCopy(substitution);
        }

        public void groundPreconditions(Term[] substitution) {
            preconditions = new LinkedList<>();
            for (Literal precondition : lifted.preconditions) {
                preconditions.add(precondition.subsCopy(substitution));
            }
        }

        public void computeEffects(Term[] substitution) {
            addEffects = new LinkedList<>();
            for (Literal addEffect : lifted.addEffects) {
                addEffects.add(addEffect.subsCopy(substitution));
            }
            deleteEffects = new LinkedList<>();
            for (Literal deleteEffect : lifted.deleteEffects) {
                deleteEffects.add(deleteEffect.subsCopy(substitution));
            }
        }
    }
}
