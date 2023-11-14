package cz.cvut.fel.ida.pipelines.debugging.drawing;

import cz.cvut.fel.ida.drawing.Drawer;
import cz.cvut.fel.ida.drawing.GraphViz;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GroundingDrawer extends Drawer<GroundingSample> {
    private static final Logger LOG = Logger.getLogger(GroundingDrawer.class.getName());

    boolean compact = true;

    public GroundingDrawer(Settings settings) {
        super(settings);
        this.compact = settings.compactGroundingDrawing;
    }

    @Override
    public void loadGraph(GroundingSample sample) {
        this.graphviz.start_graph();

        nodeGraph(sample);

        if (sample.query.headAtom != null) {
            graphviz.addln(GraphViz.sanitize(sample.query.headAtom.literal.toString()) + "[shape = tripleoctagon, xlabel=\"\n\n\ntarget = " + sample.target + "  \"]");
        }

        this.graphviz.end_graph();
    }

    public void nodeGraph(GroundingSample obj) {
        GroundTemplate groundTemplate = obj.groundingWrap.getGroundTemplate();
        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules = groundTemplate.groundRules;
        Map<Literal, ValuedFact> groundFacts = groundTemplate.groundFacts;

        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> mapEntry : groundRules.entrySet()) {
            Literal groundHead = mapEntry.getKey();
            graphviz.addln(draw(groundHead));
            for (Map.Entry<GroundHeadRule, Collection<GroundRule>> entry : mapEntry.getValue().entrySet()) {
                GroundHeadRule groundHeadRule = entry.getKey();
                if (!compact) {
                    graphviz.addln(draw(groundHeadRule));
                    graphviz.addln(drawEdge(groundHead, groundHeadRule));
                }
                Collection<GroundRule> bodyGroundings = entry.getValue();
                for (GroundRule bodyGrounding : bodyGroundings) {
                    graphviz.addln(draw(bodyGrounding));
                    if (compact) {
                        graphviz.addln(drawEdge(groundHead, bodyGrounding, groundHeadRule));
                    } else
                        graphviz.addln(drawEdge(groundHeadRule, bodyGrounding));
                    for (Literal literal : bodyGrounding.groundBody) {
                        graphviz.addln(draw(literal));
                        graphviz.addln(drawEdge(bodyGrounding, literal));
                    }
                }
            }
        }
    }

    private String drawEdge(Literal groundHead, GroundRule bodyGrounding, GroundHeadRule groundHeadRule) {
        return bodyGrounding.hashCode() + " -> " + GraphViz.sanitize(groundHead.toString()) + " [fontsize=10, color=blue, fontcolor=green, label = " + GraphViz.sanitize(groundHeadRule.toFullString()) + "]";
    }

    private String draw(GroundHeadRule groundHeadRule) {
        return groundHeadRule.hashCode() + " [shape=larrow, fontsize=10, color=green, label=" + GraphViz.sanitize(groundHeadRule.toFullString()) + "]";
    }

    private String draw(GroundRule groundRule) {
        return groundRule.hashCode() + " [shape=lpromoter, color=red, label=" + GraphViz.sanitize(groundRule.toString()) + "]";
    }

    public String draw(Literal groundHead) {
        String name = "null";
        if (groundHead != null) {
            name = groundHead.toString();
        }
        return GraphViz.sanitize(name) + "[color=blue, fontcolor=blue]";
    }

    private String drawEdge(GroundRule bodyGrounding, Literal literal) {
        return GraphViz.sanitize(literal == null ? "null" : literal.toString()) + " -> " + bodyGrounding.hashCode() + "[color=red, fontcolor=red]";
    }

    public String drawEdge(GroundHeadRule groundHeadRule, GroundRule bodyGrounding) {
        return bodyGrounding.hashCode() + " -> " + groundHeadRule.hashCode() + "[style=dashed, color=green]";
    }

    public String drawEdge(Literal groundHead, GroundHeadRule groundHeadRule) {
        return groundHeadRule.hashCode() + " -> " + GraphViz.sanitize(groundHead.toString()) + "[color=blue]";
    }

}
