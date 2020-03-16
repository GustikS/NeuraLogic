package cz.cvut.fel.ida.logic.grounding.topDown;

import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.grounding.Grounder;
import cz.cvut.fel.ida.setup.Settings;

public class Gringo extends Grounder {
    public Gringo(Settings settings) {
        super(settings);
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        return null;
    }

    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory) {
        return null;
    }
}
