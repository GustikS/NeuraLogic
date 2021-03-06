package cz.cvut.fel.ida.logic.constructs.example;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.learning.Query;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.Grounder;
import cz.cvut.fel.ida.setup.Settings;

/**
 * Created by Gusta on 04.10.2016.
 * <p>
 * HeadAtom desont have to be ground! - it will apply to all compatible neuralize neurons then
 */
public class QueryAtom extends Query<LiftedExample, Template> {

    public HeadAtom headAtom;

    private QueryAtom(){
        super();
    }

    public QueryAtom(String id, int queryCounter, double importance, HeadAtom query, LiftedExample evidence) {
        super(id, queryCounter, importance, evidence);
        this.headAtom = query;
    }

    public QueryAtom(String id, int queryCounter, double importance, HeadAtom query) {
        super(id, queryCounter, importance, null);
        this.headAtom = query;
    }

    @Override
    public ScalarValue evaluate(Settings settings, Template template) {
        LiftedExample example = this.evidence;
        Grounder grounder = Grounder.getGrounder(settings);
        GroundTemplate groundTemplate = grounder.groundRulesAndFacts(example, template);
        ScalarValue present;
        if (groundTemplate.groundFacts.containsKey(headAtom.literal) || groundTemplate.groundRules.keySet().contains(headAtom.literal)) {
            present = new ScalarValue(1);
        } else {
            present = new ScalarValue(0);
        }
        return present;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (headAtom != null)
            sb.append(headAtom.toString());
        if (evidence != null)
            sb.append(" <- " + evidence.toString());
        return sb.toString();
    }
}