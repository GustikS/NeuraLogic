package grounding.topDown;

import constructs.example.LiftedExample;
import constructs.template.Template;
import grounding.GroundTemplate;
import grounding.Grounder;
import settings.Settings;

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
