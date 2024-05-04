package cz.cvut.fel.ida.logic.constructs.template.types;

import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.BodyAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Predicate;
import cz.cvut.fel.ida.logic.subsumption.Matching;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Template representation with possible inference paths represented via a graph
 */
public class GraphTemplate extends Template {
    private static final Logger LOG = Logger.getLogger(GraphTemplate.class.getName());

    /**
     * Maps any literal from the template to a list of rules with a compatible (subsumable) head
     */
    transient public Map<Literal, Set<WeightedRule>> atom2rules;    //todo change to UnsignedLiterals everywhere?
    /**
     * Set of literals that are simple facts inferred directly from the template
     */
    transient Set<Literal> closedAtoms;
    /**
     * Set of literals that certainly need to be derived from an example (cannot be inferred from the template))
     */
    transient Set<Literal> openAtoms;
    /**
     * No cycles with negation
     */
    public boolean isStratified = true;

    public GraphTemplate() {
        atom2rules = new LinkedHashMap<>();
    }

    public GraphTemplate(GraphTemplate template) {
        super(template);
        atom2rules = template.atom2rules;
        closedAtoms = template.closedAtoms;
        openAtoms = template.openAtoms;
        isStratified = template.isStratified;
    }

    public GraphTemplate(Template template) {
        super(template);

        Map<Predicate, List<WeightedRule>> predicate2heads = new LinkedHashMap<>();
        atom2rules = new LinkedHashMap<>();
        closedAtoms = template.facts.stream().map(f -> f.literal).collect(Collectors.toSet());
        if (template.inferredAtoms != null) {
            closedAtoms.addAll(template.inferredAtoms);
        }
        openAtoms = new HashSet<>();

        Clause clause = new Clause(closedAtoms);
        Matching matching = new Matching(Collections.singletonList(clause));

        for (WeightedRule rule : template.rules) {
            List<WeightedRule> list = predicate2heads.computeIfAbsent(rule.getHead().literal.predicate(), k -> new ArrayList<>());
            list.add(rule);
            Set<WeightedRule> list2 = atom2rules.computeIfAbsent(rule.getHead().literal, k -> new HashSet<>());
            list2.add(rule);
        }

        for (Map.Entry<Predicate, List<WeightedRule>> entry : predicate2heads.entrySet()) {
            for (WeightedRule anyRule : entry.getValue()) {
                for (BodyAtom bodyAtom : anyRule.getBody()) {
                    Literal bodyLiteral = bodyAtom.literal;
                    if (bodyLiteral.isNegated()) {
                        this.containsNegation = true;
                        bodyLiteral = new Literal(bodyLiteral.predicate(), false, bodyLiteral.termList());  // we want to search for positive version of literals only
                    }
                    List<WeightedRule> possibleRules = predicate2heads.get(bodyLiteral.predicate());
                    boolean hasChildren = false;
                    if (possibleRules != null) {
                        for (WeightedRule possibleRule : possibleRules) {   //todo check and optimize this
                            if (matching.subsumption(new Clause(possibleRule.getHead().literal), new Clause(bodyLiteral)) // if the head of this possibleRule can be unified with the bodyatom (subtituted for it), add such a possible path
                                    || (matching.subsumption(new Clause(bodyLiteral), new Clause(possibleRule.getHead().literal))) // the other direction - bodyAtom might be a general (lifted) case of the possibleRule's head - also a possible inference path
                            ) {
                                hasChildren = true;
                                Set<WeightedRule> rules4atom = atom2rules.computeIfAbsent(bodyLiteral, k -> new HashSet<>());
                                rules4atom.add(possibleRule);
                            }
                        }
                    }
                    if (!hasChildren) {
                        if (!closedAtoms.contains(bodyLiteral)) {  //if it is not true directly in the template
                            openAtoms.add(bodyLiteral);
                        }
                    }
                }
            }
        }
//        if (this.containsNegation) {
//            new Stratification().check();
//        }
    }

    public class Stratification {

        private final GraphTemplate graphTemplate;

        Set<Literal> open = new HashSet<>();
        Set<Literal> closed = new HashSet<>();
        LinkedList<UnsignedLiteral> path = new LinkedList<>();

        public Stratification(GraphTemplate graphTemplate) {
            this.graphTemplate = graphTemplate;
        }

        public void check() {
            for (Literal root : graphTemplate.atom2rules.keySet()) {
                checkCyclesStartingFrom(root);
            }
        }

        private void checkCyclesStartingFrom(Literal literal) {
            UnsignedLiteral unsignedLiteral = new UnsignedLiteral(literal);
            path.addLast(unsignedLiteral);
            literal = unsignedLiteral.literal;

            if (closed.contains(literal)) {
                return;
            }

            if (open.contains(literal)) {
                boolean negatedCycle = !containsNegativeCycle(literal, path);
                if (negatedCycle) {
                    graphTemplate.isStratified = false;
                }
                return;
            }
            open.add(literal);
            Set<WeightedRule> rules = graphTemplate.atom2rules.get(literal);
            if (rules != null) {
                for (WeightedRule rule : rules) {
                    for (BodyAtom bodyAtom : rule.getBody()) {
                        checkCyclesStartingFrom(bodyAtom.literal);
                    }
                }
            }
            path.removeLast();
            open.remove(literal);
            closed.add(literal);
        }

        private boolean containsNegativeCycle(Literal literal, LinkedList<UnsignedLiteral> stack) {
            Iterator<UnsignedLiteral> iterator = stack.iterator();
            LinkedList<UnsignedLiteral> cycle = new LinkedList<>();
            boolean negatedCycle = false;
            UnsignedLiteral next = iterator.next();
            while (!next.literal.equals(literal)) {
                next = iterator.next();
            }
            cycle.add(next);
            do {
                next = iterator.next();
                cycle.add(next);
                if (next.wasNegated) {
                    negatedCycle = true;
                }
            } while (iterator.hasNext());

            if (negatedCycle) {
                LOG.severe("The template is not stratified - there is a cyclic dependency with negation: " + cycle.toString());
                LOG.severe("This may lead to weird and ambiguous behavior, please check your template carefully (or google stratified logic programming).");
                return true;
            }
            return false;
        }
    }

    /**
     * Just a Literal but without negation considered
     */
    private class UnsignedLiteral {

        private Literal literal;
        boolean wasNegated = false;

        public UnsignedLiteral(Literal literal) {
            if (literal.isNegated()) {
                this.literal = new Literal(literal.predicate(), false, literal.termList());
                wasNegated = true;
            } else {
                this.literal = literal;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnsignedLiteral that = (UnsignedLiteral) o;
            return Objects.equals(literal, that.literal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(literal);
        }

        @Override
        public String toString() {
            return wasNegated ? "!" + literal.toString() : literal.toString();
        }
    }

    /**
     * Prune the parent Template's rules so that rules irrelevant to this queryAtom (not in Herbrand support) are removed
     *
     * @param queryAtom
     * @return
     */
    public GraphTemplate prune(QueryAtom queryAtom) {
        GraphTemplate pruned = new GraphTemplate(this);
        pruned.rules = new LinkedHashSet<>();
        recursePrune(queryAtom.headAtom.literal, pruned.rules);
        return pruned;
    }

    private void recursePrune(Literal head, LinkedHashSet<WeightedRule> rules) {
        Set<WeightedRule> childrenRules = atom2rules.get(head);
        if (childrenRules == null) return;

        for (WeightedRule rule : childrenRules) {
            rules.add(rule);
            for (BodyAtom bodyAtom : rule.getBody()) {
                recursePrune(bodyAtom.literal, rules);
            }
        }
    }
}