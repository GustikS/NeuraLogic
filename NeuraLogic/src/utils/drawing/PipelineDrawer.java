package utils.drawing;

import pipelines.*;
import pipelines.pipes.generic.IdentityGenPipe;
import settings.Settings;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import static utils.drawing.GraphViz.sanitize;

public class PipelineDrawer<S, T> extends Drawer<Pipeline<S, T>> {
    private static final Logger LOG = Logger.getLogger(PipelineDrawer.class.getName());

    public PipelineDrawer(Settings settings) {
        super(settings);
    }

    @Override
    public void loadGraph(Pipeline<S, T> pipeline) {
        this.graphviz.addln("strict digraph {");
        graphviz.addln("compound=true");
        loadPipeline(pipeline);

        this.graphviz.end_graph();
    }


    private void loadPipeline(Pipeline<S, T> pipeline) {

        for (Map.Entry<String, Pipe> entry : pipeline.pipes.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, Branch> entry : pipeline.branches.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, Merge> entry : pipeline.merges.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, ParallelPipe> entry : pipeline.multiPipes.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, MultiBranch> entry : pipeline.multiBranches.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, MultiMerge> entry : pipeline.multiMerges.entrySet()) {
            draw(entry.getValue());
        }

        for (Map.Entry<String, Pipeline> entry : pipeline.pipelines.entrySet()) {
            drawInnerPipeline(entry.getValue());
        }

        drawStart(getStartElement(pipeline));
        drawTerminal(getTerminalElement(pipeline));

        //manual add the root start/terminal
//        graphviz.addln(pipeline.start.hashCode() + "[label=" + sanitize(pipeline.start.toString()) + ", shape=tripleoctagon]");
//        if (pipeline.start instanceof Pipeline)
//            graphviz.addln(pipeline.start.hashCode() + " -> " + getStartElement(pipeline.start).hashCode() + "[lhead=cluster_" + pipeline.start.hashCode() + "]");
//        graphviz.addln(pipeline.terminal.hashCode() + "[label=" + sanitize(pipeline.terminal.toString()) + ", shape=tripleoctagon]");
//        if (pipeline.terminal instanceof Pipeline)
//            graphviz.addln(pipeline.terminal.hashCode() + " -> " + getTerminalElement(pipeline.terminal).hashCode() + "[lhead=cluster_" + pipeline.terminal.hashCode() + "]");
    }

    private void drawStart(Object start) {
//        if (start != null)
        graphviz.addln(getNodeId(start) + "[label=" + sanitize(start.toString()) + ", color=green]");
    }

    private void drawTerminal(Object terminal) {
        if (terminal != null)
            graphviz.addln(getNodeId(terminal) + "[label=" + sanitize(terminal.toString()) + ", color=red]");
    }

    private void draw(Pipe pipe) {
        ArrayList<String> types = GenericTypeGetter.getTypes(pipe); //todo next replace reflection with explicit Class info storing within the pipes - e.g. to show "Sources" type as the input

        if (pipe instanceof IdentityGenPipe) {
            graphviz.addln(getNodeId(pipe) + "[shape=point, width=0.1, height=0.1]");
        } else {
            graphviz.addln(getNodeId(pipe) + "[label=" + sanitize(pipe.toString()) + ", shape=cds]");
        }
        if (pipe.input != null)
            drawEdge(pipe.input, pipe, "label=" + sanitize(types.get(0)));
        if (pipe.output != null)
            drawEdge(pipe, pipe.output, "label=" + sanitize(types.get(1)));
    }

    private void draw(Branch branch) {
        graphviz.addln(getNodeId(branch) + "[label=" + sanitize(branch.toString()) + ", shape=trapezium]");
        if (branch.input != null)
            drawEdge(branch.input, branch);
        if (branch.output1 != null)
            drawEdge(branch, branch.output1);
        if (branch.output2 != null)
            drawEdge(branch, branch.output2);
    }

    private void draw(Merge merge) {
        graphviz.addln(getNodeId(merge) + "[label=" + sanitize(merge.toString()) + ", shape=invtrapezium]");
        if (merge.input1 != null)
            graphviz.addln(getNodeId(merge.input1) + "[shape=point, width=0.1, height=0.1]");
        drawEdge(merge.input1, merge);
        if (merge.input2 != null)
            graphviz.addln(getNodeId(merge.input2) + "[shape=point, width=0.1, height=0.1]");
        drawEdge(merge.input2, merge);
        if (merge.output != null)
            drawEdge(merge, merge.output);
    }

    private void draw(ParallelPipe pipe) {
        graphviz.addln(pipe.hashCode() + "[label=" + getNodeId(pipe.toString()) + "]");
        for (Object input : pipe.inputs) {
            graphviz.addln(input.hashCode() + " -> " + pipe.hashCode());
        }
        for (Object output : pipe.outputs) {
            graphviz.addln(pipe.hashCode() + " -> " + output.hashCode());
        }
    }

    private void draw(MultiBranch branch) { //todo now test some multi-pipeline (xval)
        graphviz.addln(getNodeId(branch) + "[label=" + sanitize(branch.toString()) + ", shape=trapezium]");
        if (branch.input != null)
            graphviz.addln(getNodeId(branch.input) + " -> " + branch.hashCode());
        for (Object output : branch.outputs) {
            graphviz.addln(branch.hashCode() + " -> " + output.hashCode());
        }
    }

    private void draw(MultiMerge merge) {
        graphviz.addln(getNodeId(merge) + "[label=" + sanitize(merge.toString()) + ", shape=invtrapezium]");
        for (Object input : merge.inputs) {
            graphviz.addln(input.hashCode() + " -> " + merge.hashCode());
        }
        if (merge.output != null)
            graphviz.addln(merge.hashCode() + " -> " + getNodeId(merge.output));
    }

    private void drawInnerPipeline(Pipeline pipeline) {
        graphviz.addln("subgraph cluster_" + pipeline.hashCode() + " {");
//        graphviz.addln(getNodeId(pipeline) + " [shape=point, style=invis]");
        this.loadPipeline(pipeline);
        graphviz.addln("label=" + sanitize(pipeline.toString()) + "}");

        if (pipeline.input != null)
            drawEdge(pipeline.input, pipeline);
        if (pipeline.output != null)
            drawEdge(pipeline, pipeline.output);
    }

    private void drawEdge(Object obj1, Object obj2) {
        drawEdge(obj1, obj2, "");
    }

    private void drawEdge(Object obj1, Object obj2, String attributes) {
        if (obj1 instanceof Pipeline) {
            attributes += " ltail=" + getNodeId(obj1);
        }
        if (obj2 instanceof Pipeline) {
            attributes += " lhead=" + getNodeId(obj2);
        }
        String node1 = getEnd(obj1);
        String node2 = getStart(obj2);
        graphviz.addln(node1 + " -> " + node2 + "[" + attributes + "]");
    }

    private String getStart(Object obj) {
        if (obj instanceof Pipeline) {
            Pipeline pipeline = (Pipeline) obj;
            return getNodeId(pipeline.start);
        } else {
            return getNodeId(obj);
        }
    }

    private String getEnd(Object obj) {
        if (obj instanceof Pipeline) {
            Pipeline pipeline = (Pipeline) obj;
            if (pipeline.terminal != null)
                return getNodeId(pipeline.terminal);
            else {
                LOG.severe("Pipeline without terminal");
                return getNodeId(pipeline.start);   // todo next
            }
        } else {
            return getNodeId(obj);
        }
    }

    private String getNodeId(Object object) {
        int hashCode = object.hashCode();
        if (object instanceof Pipeline) {
            return "cluster_" + hashCode;
        } else {
            return String.valueOf(hashCode);
        }
    }

    private Object getStartElement(Object object) {
        if (object instanceof Pipeline) {
            Pipeline pipeline = (Pipeline) object;
            return getStartElement(pipeline.start);
        } else {
            return object;
        }
    }

    private Object getTerminalElement(Object object) {
        if (object instanceof Pipeline) {
            Pipeline pipeline = (Pipeline) object;
            return getTerminalElement(pipeline.terminal);
        } else {
            return object;
        }
    }

    private static class GenericTypeGetter {

        public static ArrayList<String> getTypes(Object object) {
            try {
                Type genericSuperclass = object.getClass().getGenericSuperclass();
                Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                return recurseTypes(actualTypeArguments);
            } catch (Exception e) {
                LOG.warning("Java error while hacking around with reflection for recovering generics at runtime.");
            }
            return null;
        }

        private static ArrayList<String> recurseTypes(Type[] actualTypeArguments) {
            ArrayList<String> types = new ArrayList<>();
            for (Type actualTypeArgument : actualTypeArguments) {
                types.add(getCompoundType(actualTypeArgument) + ",");
            }
            return types;
        }

        private static String getCompoundType(Type actualTypeArgument) {
            String simpleName = "";
            try {
                if (actualTypeArgument instanceof Class) {
                    Class aClass = (Class) actualTypeArgument;
                    simpleName = aClass.getSimpleName();
                } else if (actualTypeArgument instanceof ParameterizedType) {
                    Type[] actualTypeArguments = ((ParameterizedType) actualTypeArgument).getActualTypeArguments();
                    simpleName = Arrays.toString(recurseTypes(actualTypeArguments).toArray());
                } else if (actualTypeArgument instanceof TypeVariableImpl) {
                    TypeVariableImpl typeVariable = (TypeVariableImpl) actualTypeArgument;
                    simpleName = typeVariable.getName();
                }


            } catch (ClassCastException e) {
                System.out.println();
            }
            return simpleName;
        }
    }
}
