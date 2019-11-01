package utils.drawing;

import pipelines.Branch;
import pipelines.Merge;
import pipelines.Pipe;
import pipelines.Pipeline;
import settings.Settings;

import java.util.Map;
import java.util.logging.Logger;

import static utils.drawing.GraphViz.sanitize;

public class PipelineDrawer<S, T> extends Drawer<Pipeline<S, T>> {
    private static final Logger LOG = Logger.getLogger(PipelineDrawer.class.getName());

    public PipelineDrawer(Settings settings) {
        super(settings);
    }

    @Override
    public void loadGraph(Pipeline<S, T> obj) { //todo next
        for (Map.Entry<String, Pipe> entry : obj.pipes.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, Branch> entry : obj.branches.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, Merge> entry : obj.merges.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, Pipeline> entry : obj.pipelines.entrySet()) {
            drawInnerPipeline(entry.getValue());
        }
    }

    private void drawInnerPipeline(Pipeline pipeline) {
        loadGraph(pipeline);
    }

    private void draw(Merge branch) {
        graphviz.addln(branch.ID + "[label=" + sanitize(branch.toString()) + "]");
        graphviz.addln(branch.input1 + " -> " + branch.ID);
        graphviz.addln(branch.input2 + " -> " + branch.ID);
        graphviz.addln(branch.ID + " -> " + branch.output);
    }

    private void draw(Branch branch) {
        graphviz.addln(branch.ID + "[label=" + sanitize(branch.toString()) + "]");
        graphviz.addln(branch.input + " -> " + branch.ID);
        graphviz.addln(branch.ID + " -> " + branch.output1);
        graphviz.addln(branch.ID + " -> " + branch.output2);
    }

    private void draw(Pipe pipe) {
        graphviz.addln(pipe.ID + "[label=" + sanitize(pipe.toString()) + "]");
        graphviz.addln(pipe.input + " -> " + pipe.ID);
        graphviz.addln(pipe.ID + " -> " + pipe.output);
    }
}
