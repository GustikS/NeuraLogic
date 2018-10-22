package constructs.template.types;

import constructs.example.QueryAtom;
import constructs.template.components.BodyAtom;
import constructs.template.Template;
import constructs.template.components.WeightedRule;
import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Predicate;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.Sugar;

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
    Map<Literal, List<WeightedRule>> atom2rules;
    /**
     * Set of literals that are simple facts inferred directly from the template
     */
    Set<Literal> closedAtoms;
    /**
     * Set of literals that certainly need to be derived from an example (cannot be inferred from the template))
     */
    Set<Literal> openAtoms;

    public GraphTemplate(){
        atom2rules = new HashMap<>();
    }

    public GraphTemplate(Template template) {
        super(template);

        Map<Predicate, List<WeightedRule>> predicate2heads = new HashMap<>();
        atom2rules = new HashMap<>();
        closedAtoms = new HashSet<>();
        openAtoms = new HashSet<>();

        Matching matching = new Matching(Sugar.list(new Clause(template.facts.stream().map(f -> f.literal).collect(Collectors.toList()))));


        for (WeightedRule rule : template.rules) {
            List<WeightedRule> list = predicate2heads.computeIfAbsent(rule.head.literal.predicate(), k -> new ArrayList<>());
            list.add(rule);
            List<WeightedRule> list2 = atom2rules.computeIfAbsent(rule.head.literal, k -> new ArrayList<>());
            list2.add(rule);
        }

        for (Map.Entry<Predicate, List<WeightedRule>> entry : predicate2heads.entrySet()) {
            for (WeightedRule anyRule : entry.getValue()) {
                for (BodyAtom bodyAtom : anyRule.body) {
                    List<WeightedRule> possibleRules = predicate2heads.get(bodyAtom.literal.predicate());
                    boolean hasChildren = false;
                    if (possibleRules != null) {
                        for (WeightedRule possibleRule : possibleRules) {
                            // if the head of this possibleRule can be unified with the bodyatom, add such a possible path
                            if (matching.subsumption(new Clause(possibleRule.head.literal), new Clause(bodyAtom.literal))) { //todo check and optimize this
                                hasChildren = true;
                                List<WeightedRule> rules4atom = atom2rules.computeIfAbsent(bodyAtom.literal, k -> new ArrayList<>());
                                rules4atom.add(possibleRule);
                            }
                        }
                    }
                    if (!hasChildren) {
                        if (matching.subsumption(new Clause(bodyAtom.literal), 0)) {  //if it is true directly in the template
                            closedAtoms.add(bodyAtom.literal);
                        } else {    //if it needs to be derived from example
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
        List<WeightedRule> childrenRules = atom2rules.get(head);
        if (childrenRules == null) return;

        for (WeightedRule rule : childrenRules) {
            rules.add(rule);
            for (BodyAtom bodyAtom : rule.body) {
                recursePrune(bodyAtom.literal, rules);
            }
        }
    }
}