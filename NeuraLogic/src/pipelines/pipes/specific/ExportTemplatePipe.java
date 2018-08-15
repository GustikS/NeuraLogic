package pipelines.pipes.specific;

import constructs.template.Template;
import pipelines.Pipe;

import java.util.logging.Logger;

public class ExportTemplatePipe extends Pipe<Template,String> {
    private static final Logger LOG = Logger.getLogger(ExportTemplatePipe.class.getName());

    protected ExportTemplatePipe(String id) {
        super(id);
    }

    @Override
    public String apply(Template template) {
        return null;
    }
}
