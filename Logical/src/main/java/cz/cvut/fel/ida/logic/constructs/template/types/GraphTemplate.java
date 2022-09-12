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
    transient public Map<Literal, Set<WeightedRule>> atom2rules;
    /**
     * Set of literals that are simple facts inferred directly from the template
     */
    transient Set<Literal> closedAtoms;
    /**
     * Set of literals that certainly need to be derived from an example (cannot be inferred from the template))
     */
    transient Set<Literal> openAtoms;

    public GraphTemplate() {
        atom2rules = new LinkedHashMap<>();
    }

    public GraphTemplate(Template template) {
        super(template);

        Map<Predicate, List<WeightedRule>> predicate2heads = new LinkedHashMap<>();
        atom2rules = new LinkedHashMap<>();
        closedAtoms = template.facts.stream().map(f -> f.literal).collect(Collectors.toSet());
        if (template.inferredLiterals != null) {
            closedAtoms.addAll(template.inferredLiterals);
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
                    List<WeightedRule> possibleRules = predicate2heads.get(bodyAtom.literal.predicate());
                    boolean hasChildren = false;
                    if (possibleRules != null) {
                        for (WeightedRule possibleRule : possibleRules) {   //todo check and optimize this
                            if (matching.subsumption(new Clause(possibleRule.getHead().literal), new Clause(bodyAtom.literal)) // if the head of this possibleRule can be unified with the bodyatom (subtituted for it), add such a possible path
                                    || (matching.subsumption(new Clause(bodyAtom.literal), new Clause(possibleRule.getHead().literal))) // the other direction - bodyAtom might be a general (lifted) case of the possibleRule's head - also a possible inference path
                            ) {
                                hasChildren = true;
                                Set<WeightedRule> rules4atom = atom2rules.computeIfAbsent(bodyAtom.literal, k -> new HashSet<>());
                                rules4atom.add(possibleRule);
                            }
                        }
                    }
                    if (!hasChildren) {
                        if (!closedAtoms.contains(bodyAtom.literal)) {  //if it is not true directly in the template
                            openAtoms.add(bodyAtom.literal);
                        }
                    }
                }
            }
        }
    }

    public GraphTemplate(GraphTemplate template) {
        super(template);
        atom2rules = template.atom2rules;
        closedAtoms = template.closedAtoms;
        openAtoms = template.openAtoms;
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