package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;

import java.util.LinkedList;
import java.util.List;

public class Action extends WeightedRule {

    String name;

    List<Literal> preconditions;
    List<Literal> addEffects;
    List<Literal> deleteEffects;

    List<Term> terms;
    /**
     * An auxiliary literal representing the action, to be added
     */
    Literal applicable = new Literal(name, terms);

    private SubsumptionEngineJ2.ClauseC preconditionsC;

    public Action(List<Literal> preconditions, List<Literal> addEffects, List<Literal> deleteEffects){
        this.preconditions = preconditions;
        this.addEffects = addEffects;
        this.deleteEffects = deleteEffects;
    }

    public SubsumptionEngineJ2.ClauseC getClauseC(Matching matching) {
        if (preconditionsC != null){
            return preconditionsC;
        }
        final Clause clause = new Clause(preconditions);
        preconditionsC = matching.getEngine().createCluaseC(clause);
        return preconditionsC;
    }

    public class GroundAction {
        Action lifted;

        Literal applicable;

        List<Literal> preconditions;
        List<Literal> addEffects;
        List<Literal> deleteEffects;

        public GroundAction(Term[] variables, Term[] substitution){
            for (int i = 0; i < variables.length; i++) {
                variables[i].setIndexWithinSubstitution(i);
            }
            this.applicable = lifted.applicable.subsCopy(substitution);
        }

        public void computeEffects(Term[] substitution){
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
