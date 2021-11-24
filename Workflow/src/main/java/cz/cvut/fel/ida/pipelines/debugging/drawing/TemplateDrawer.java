package cz.cvut.fel.ida.pipelines.debugging.drawing;

import cz.cvut.fel.ida.drawing.Drawer;
import cz.cvut.fel.ida.drawing.GraphViz;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.BodyAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.constructs.template.types.GraphTemplate;
import cz.cvut.fel.ida.setup.Settings;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by gusta on 8.3.17.
 */
public class TemplateDrawer extends Drawer<Template> {
    public TemplateDrawer(Settings settings) {
        super(settings);
    }

    @Override
    public void loadGraph(Template obj) {
        graphviz.start_graph();
        if (obj instanceof GraphTemplate) {
            loadGraph((GraphTemplate) obj);
        } else {
            loadGraph(new GraphTemplate(obj));
        }
        graphviz.end_graph();
    }

    public void loadGraph(GraphTemplate obj) {
        Map<Literal, Set<WeightedRule>> atom2rules = obj.atom2rules;
        LinkedHashSet<ValuedFact> facts = obj.facts;

        for (ValuedFact fact : facts) {
            graphviz.addln(draw(fact));
        }

        for (Map.Entry<Literal, Set<WeightedRule>> entry : atom2rules.entrySet()) {
            Literal literal = entry.getKey();
            Set<WeightedRule> rules = entry.getValue();
            graphviz.addln(draw(literal));
            for (WeightedRule rule : rules) {
                graphviz.addln(draw(rule)); //rule with subsumable head by this literal
                graphviz.addln(draw(literal, rule));
                for (BodyAtom bodyAtom : rule.getBody()) {
                    graphviz.addln(draw(bodyAtom));
                    graphviz.addln(draw(rule, bodyAtom)); //body literals from the  rule
                }
            }
        }
    }

    private String draw(WeightedRule rule, BodyAtom bodyAtom) {
        String edgeColor = bodyAtom.isNegated() ? "red" : "black";
        String weight = bodyAtom.getConjunctWeight() == null ? "" : bodyAtom.getConjunctWeight().toString(numberFormat);
        return rule.hashCode() + " -> " + bodyAtom.hashCode() + "[label=" + GraphViz.sanitize(weight) + ", color=" + edgeColor + "]";
    }

    private String draw(BodyAtom bodyAtom) {
        return draw(bodyAtom.literal);
    }

    private String draw(Literal literal, WeightedRule rule) {
        return literal.hashCode() + " -> " + rule.hashCode() + "[label=" + GraphViz.sanitize(rule.getWeight().toString(numberFormat)) + "]";
    }

    private String draw(Literal literal) {
        return literal.hashCode() + "[label=" + GraphViz.sanitize(literal.toString()) + "]";
    }

    private String draw(WeightedRule rule) {
        return rule.hashCode() + "[label=" + GraphViz.sanitize(rule.getOriginalString()) + ", shape=rarrow, color=green]";
    }

    private String draw(ValuedFact fact) {
        return fact.hashCode() + "[label=" + GraphViz.sanitize(fact.toString()) + "]";
    }

}
