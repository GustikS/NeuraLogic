package grounding.topDown;

import constructs.example.LiftedExample;
import constructs.template.Template;
import grounding.GroundTemplate;
import grounding.Grounder;
import settings.Settings;

/**
 * Created by Gusta on 06.10.2016.
 */
public class TopDown extends Grounder {
    public TopDown(Settings settings) {
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
