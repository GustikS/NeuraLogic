package cz.cvut.fel.ida.pipelines.pipes.generic;

import cz.cvut.fel.ida.utils.exporting.Exportable;
import cz.cvut.fel.ida.utils.exporting.Exporter;
import cz.cvut.fel.ida.utils.generic.Timing;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class ExportingPipe<S> extends Pipe<Stream<S>, Stream<S>> {
    private static final Logger LOG = Logger.getLogger(ExportingPipe.class.getName());
    private Exportable toExport;
    private Exporter exporter;
    private Timing timing;

    public ExportingPipe(Exportable exportable, Exporter exporter, Timing timing, Settings settings) {
        super("ExportingPipe", settings);
        this.toExport = exportable;
        this.exporter = exporter;
        this.timing = timing;
    }

    @Override
    public Stream<S> apply(Stream<S> stream) {
        if (this.exporter != null) {
            stream.onClose(() -> trueExport());
        }

        return stream;
    }

    private void trueExport() {
        timing.finish();
        toExport.export(exporter);
    }
}
