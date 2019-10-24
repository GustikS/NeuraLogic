package constructs.building.debugging;

import constructs.template.Template;
import utils.PipelineDebugger;

import java.util.logging.Logger;

public class TemplateDebugger extends PipelineDebugger<Template> {
    private static final Logger LOG = Logger.getLogger(TemplateDebugger.class.getName());


    public TemplateDebugger(String[] args) {
        super(args);
    }

    @Override
    public void debug(Template obj) {
        
    }

}
