package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.logic.constructs.building.factories.ConstantFactory;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Action {

    public static Matching matching = new Matching();

    public static ConstantFactory constantFactory = new ConstantFactory();

    public String name;

    public List<Literal> preconditions;
    public List<Literal> addEffects;
    public List<Literal> deleteEffects;

    public Term[] variables;
    /**
     * An auxiliary literal representing the action, to be added
     */
    public Literal applicable;

    private SubsumptionEngineJ2.ClauseC preconditionsC;

    public Action(String name, List<String> parameters, List<String> preconditions, List<String> addEffects, List<String> deleteEffects) {
        this.name = name;

        HashMap<Variable, Variable> variables = new HashMap<>();
        HashMap<Constant, Constant> constants = new HashMap<>();

        this.preconditions = preconditions.stream().map(lit -> Literal.parseLiteral(lit, variables, constants)).collect(Collectors.toList());
        this.addEffects = addEffects.stream().map(lit -> Literal.parseLiteral(lit, variables, constants)).collect(Collectors.toList());
        this.deleteEffects = deleteEffects.stream().map(lit -> Literal.parseLiteral(lit, variables, constants)).collect(Collectors.toList());

        Set<Variable> vars = getVariables(this.preconditions);
        this.variables = vars.toArray(new Variable[0]);
        this.applicable = new Literal(name, this.variables);
    }

    public Action(String name, List<Literal> preconditions, List<Literal> addEffects, List<Literal> deleteEffects) {
        this.name = name;
        this.preconditions = preconditions;
        this.addEffects = addEffects;
        this.deleteEffects = deleteEffects;

        Set<Variable> vars = getVariables(preconditions);
        this.variables = vars.toArray(new Variable[0]);
        this.applicable = new Literal(name, this.variables);
    }

    @NotNull
    private Set<Variable> getVariables(List<Literal> preconditions) {
        Set<Variable> vars = new HashSet<>();
        for (Literal precondition : preconditions) {
            for (Term term : precondition.termList()) {
                if (term instanceof Variable) {
                    vars.add((Variable) term);
                }
            }
        }
        return vars;
    }

    public SubsumptionEngineJ2.ClauseC getClauseC(Matching matching) {
        if (preconditionsC != null) {
            return preconditionsC;
        }
        final Clause clause = new Clause(preconditions);
        preconditionsC = matching.getEngine().createClauseC(clause);
        return preconditionsC;
    }

    public Substitutions substitutions(State state) {
        final SubsumptionEngineJ2.ClauseE clauseE = state.getClauseE(matching);
        final SubsumptionEngineJ2.ClauseC clauseC = this.getClauseC(matching);
        return new Substitutions(matching.allSubstitutions(clauseC, clauseE, Integer.MAX_VALUE));
    }

    public static class Substitutions {
        public Term[] variables;
        public List<Term[]> constants;

        public Substitutions(Pair<Term[], List<Term[]>> substitutions) {
            this.variables = substitutions.r;
            this.constants = substitutions.s;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < constants.size(); i++) {
                sb.append(i + ") ");
                Term[] consts = constants.get(i);
                for (int j = 0; j < consts.length; j++) {
                    sb.append(variables[j]).append("->").append(consts[j]).append("; ");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    public Set<GroundAction> groundings(Substitutions substitutions) {
        Set<GroundAction> groundActions = new HashSet<>();
        for (Term[] terms : substitutions.constants) {
            final GroundAction groundAction = new Action.GroundAction(this, substitutions.variables, terms);
            groundAction.computeEffects(terms);
            groundActions.add(groundAction);
        }
        return groundActions;
    }

    public GroundAction grounding(String constants) {
        final String[] split = constants.split(",");
        Constant[] consts = new Constant[this.variables.length];
        for (int i = 0; i < split.length; i++) {
            consts[i] = constantFactory.construct(split[i]);
        }
        GroundAction groundAction = new GroundAction(this, this.variables, consts);
        groundAction.computeEffects(consts);
        return groundAction;
    }

    public static class GroundAction {
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

        /**
         * Non-destructive creation of a new State
         *
         * @param state
         * @return
         */
        public State successor(State state) {
            Set<Literal> next = new HashSet<>();
            next.addAll(state.clause.literals());
            next.addAll(this.addEffects);
            this.deleteEffects.forEach(next::remove);
            return new State(new Clause(next));
        }

        @Override
        public String toString() {
            return applicable.toString();
        }
    }
}
