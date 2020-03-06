package grounding.debugging.drawing;

import constructs.example.ValuedFact;
import constructs.template.components.GroundHeadRule;
import constructs.template.components.GroundRule;
import grounding.GroundTemplate;
import grounding.GroundingSample;
import ida.ilp.logic.Literal;
import settings.Settings;
import utils.drawing.Drawer;
import utils.drawing.GraphViz;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    public void loadGraph(GroundingSample obj) {
        this.graphviz.start_graph();

        nodeGraph(obj);

        graphviz.addln(GraphViz.sanitize(obj.query.headAtom.literal.toString()) + "[shape = tripleoctagon]");

        this.graphviz.end_graph();
    }

    public void nodeGraph(GroundingSample obj) {
        GroundTemplate groundTemplate = obj.groundingWrap.getGroundTemplate();
        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> groundRules = groundTemplate.groundRules;
        Map<Literal, ValuedFact> groundFacts = groundTemplate.groundFacts;

        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, LinkedHashSet<GroundRule>>> mapEntry : groundRules.entrySet()) {
            Literal groundHead = mapEntry.getKey();
            graphviz.addln(draw(groundHead));
            for (Map.Entry<GroundHeadRule, LinkedHashSet<GroundRule>> entry : mapEntry.getValue().entrySet()) {
                GroundHeadRule groundHeadRule = entry.getKey();
                if (!compact) {
                    graphviz.addln(draw(groundHeadRule));
                    graphviz.addln(drawEdge(groundHead, groundHeadRule));
                }
                LinkedHashSet<GroundRule> bodyGroundings = entry.getValue();
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
        return GraphViz.sanitize(groundHead.toString()) + " -> " + bodyGrounding.hashCode() + " [fontsize=10, color=blue, fontcolor=green, label = " + GraphViz.sanitize(groundHeadRule.toFullString()) + "]";
    }

    private String draw(GroundHeadRule groundHeadRule) {
        return groundHeadRule.hashCode() + " [shape=rarrow, fontsize=10, color=green, label=" + GraphViz.sanitize(groundHeadRule.toFullString()) + "]";
    }

    private String draw(GroundRule groundRule) {
        return groundRule.hashCode() + " [shape=cds, color=red, label=" + GraphViz.sanitize(groundRule.toString()) + "]";
    }

    public String draw(Literal groundHead) {
        return GraphViz.sanitize(groundHead.toString()) + "[color=blue, fontcolor=blue]";
    }

    private String drawEdge(GroundRule bodyGrounding, Literal literal) {
        return bodyGrounding.hashCode() + " -> " + GraphViz.sanitize(literal.toString())+ "[color=red, fontcolor=red]";
    }

    public String drawEdge(GroundHeadRule groundHeadRule, GroundRule bodyGrounding) {
        return groundHeadRule.hashCode() + " -> " + bodyGrounding.hashCode() + "[style=dashed, color=green]";
    }

    public String drawEdge(Literal groundHead, GroundHeadRule groundHeadRule) {
        return GraphViz.sanitize(groundHead.toString()) + " -> " + groundHeadRule.hashCode() + "[color=blue]";
    }


}
