package cz.cvut.fel.ida.pipelines.debugging.drawing;

import cz.cvut.fel.ida.drawing.Drawer;
import cz.cvut.fel.ida.drawing.GraphViz;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.HornClause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Predicate;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.BodyAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.constructs.template.types.GraphTemplate;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.setup.Settings;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gusta on 8.3.17.
 */
public class TemplateDrawer extends Drawer<Template> {

    Set<String> graph;

    public TemplateDrawer(Settings settings) {
        super(settings);
    }

    @Override
    public void loadGraph(Template obj) {
        graph = new LinkedHashSet<>();
        graphviz.start_graph();
        if (obj instanceof GraphTemplate) {
            loadGraph((GraphTemplate) obj);
        } else {
            loadGraph(new GraphTemplate(obj));
        }
        graph.forEach(graphviz::addln);
        graphviz.end_graph();
    }

    public void loadGraph(GraphTemplate obj) {

        Map<Literal, Set<WeightedRule>> atom2rules = obj.atom2rules;
        LinkedHashSet<ValuedFact> facts = obj.facts;
        Matching matching = new Matching(Collections.singletonList(new Clause(facts.stream().map(f -> f.literal).collect(Collectors.toSet()))));
        Map<Predicate, Set<ValuedFact>> pred2facts = new HashMap<>();

        for (ValuedFact fact : facts) {
            graph.add(draw(fact));
            Set<ValuedFact> factSet = pred2facts.computeIfAbsent(fact.literal.predicate(), k -> new HashSet<>());
            factSet.add(fact);
        }

        for (Map.Entry<Literal, Set<WeightedRule>> entry : atom2rules.entrySet()) {
            Literal literal = entry.getKey();
            Set<WeightedRule> rules = entry.getValue();
            graph.add(draw(literal));
            for (WeightedRule rule : rules) {
                graph.add(draw(rule)); //rule with subsumable head by this literal
                graph.add(draw(literal, rule));
                for (BodyAtom bodyAtom : rule.getBody()) {
                    graph.add(draw(bodyAtom));
                    graph.add(draw(rule, bodyAtom)); //body literals from the rule

                    Set<ValuedFact> matchedFacts;
                    if ((matchedFacts = pred2facts.get(bodyAtom.literal.predicate())) != null)
                        for (ValuedFact matchedFact : matchedFacts) {
                            if (matching.subsumption(new Clause(matchedFact.literal), new Clause(bodyAtom.literal))
                                    || matching.subsumption(new Clause(bodyAtom.literal), new Clause(matchedFact.literal))) {
                                graph.add(draw(bodyAtom, matchedFact));
                            }
                        }
                }
            }
        }
    }

    private String draw(WeightedRule rule, BodyAtom bodyAtom) {
        String edgeColor = bodyAtom.isNegated() ? "red" : "black";
        String weight = bodyAtom.getConjunctWeight() == null ? "" : bodyAtom.getConjunctWeight().toString(numberFormat);
        return bodyAtom.literal.liftedHashCode() + " -> " + rule.hashCode() + "[label=" + GraphViz.sanitize(weight) + ", color=" + edgeColor + "]";
    }

    private String draw(BodyAtom bodyAtom) {
        return draw(bodyAtom.literal);
    }

    private String draw(Literal literal, WeightedRule rule) {
        String weight = rule.getWeight() != null ? GraphViz.sanitize(rule.getWeight().toString(numberFormat)) : "null : 1";
        return rule.hashCode() + " -> " + literal.liftedHashCode() + "[label=" + weight + ", color=green, style=dashed]";
    }

    private String draw(BodyAtom bodyAtom, ValuedFact matchedFact) {
        return matchedFact.literal.liftedHashCode() + " -> " + bodyAtom.literal.liftedHashCode() + "[dir=both, style=dotted, color=blue]";
    }

    private String draw(Literal literal) {
        String name = literal.toString();
        if (literal.isNegated()) {
            name = name.substring(1);
        }
        return literal.liftedHashCode() + "[label=" + GraphViz.sanitize(name) + "]";
    }

    private String draw(WeightedRule rule) {
        return rule.hashCode() + "[label=" + GraphViz.sanitize(rule.getOriginalString()) + ", shape=larrow, color=green]";
    }

    private String draw(ValuedFact fact) {
        return fact.literal.liftedHashCode() + "[shape=house, label=" + GraphViz.sanitize(fact.toString()) + "]";
    }

}
