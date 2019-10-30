package constructs.building.debugging;

import constructs.template.Template;
import pipelines.Pipeline;
import settings.Sources;
import utils.PipelineDebugger;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TemplateDebugger extends PipelineDebugger<Template> {
    private static final Logger LOG = Logger.getLogger(TemplateDebugger.class.getName());


    public TemplateDebugger(String[] args) {
        super(args);
    }

    @Override
    public void debug(Template obj) {

    }

    @Override
    public Pipeline<Sources, Stream<Template>> buildPipeline() {
        return null;
    }
}
