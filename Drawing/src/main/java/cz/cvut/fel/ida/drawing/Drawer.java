package cz.cvut.fel.ida.drawing;


import cz.cvut.fel.ida.setup.Settings;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Drawer<S> {   //todo next replace hashcodes (which collide sometimes) with truly unique IDs

    private static final Logger LOG = Logger.getLogger(Drawer.class.getName());

    public NumberFormat numberFormat;
    protected Settings.Detail drawingDetail;
    protected boolean storeNotShow;

    protected GraphViz graphviz;
    public Settings settings;

    public Drawer(Settings settings) {
        this.settings = settings;
        if (settings.drawing) {
            this.graphviz = new GraphViz(settings);

            Logger.getLogger("java.awt").setLevel(Level.WARNING);
            Logger.getLogger("sun.awt").setLevel(Level.WARNING);
            Logger.getLogger("javax.swing").setLevel(Level.WARNING);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }
        }


        this.drawingDetail = settings.drawingDetail;
        if (drawingDetail == Settings.Detail.LOW){
            this.numberFormat = null;
        } else {
            this.numberFormat = settings.defaultNumberFormat;
        }
        this.storeNotShow = settings.storeNotShow;
    }

    public void display(byte[] imageBytes, String name) {
        //now turn off annoying java awt logging messages here
        Logger.getLogger("java.awt").setLevel(Level.WARNING);
        Logger.getLogger("sun.awt").setLevel(Level.WARNING);
        Logger.getLogger("javax.swing").setLevel(Level.WARNING);

        Object lock = new Object();
        DrawWindow debugWindow = new DrawWindow(imageBytes, name, lock);
        debugWindow.pauseUntilWindowCloses(lock);
    }

    public void draw(S obj) {
        if (graphviz == null) {
            LOG.warning("Drawing is required but no graphviz exec found, could not draw!");
            return;
        }
        this.graphviz.clearGraph();
        loadGraph(obj);
        try {
            LOG.info("Paused for drawing with Graphviz...(this may take a while for bigger graphs)");
            if (storeNotShow) {
                graphviz.storeGraphSource(obj.toString());
                byte[] image = graphviz.getGraphUsingTemporaryFile(graphviz.getDotSource(), graphviz.imgtype, graphviz.algorithm);
                graphviz.writeImageToFile(image, obj.toString());
                LOG.info("Graph stored into file named: " + obj.toString());
            } else {
                display(graphviz.getGraphImage(obj.toString()), obj.toString());
            }
        } catch (IOException | InterruptedException e) {
            LOG.severe(e.getMessage());
        }
    }

    public byte[] drawIntoFile(S obj, String path) {
        byte[] image = this.drawIntoBytes(obj);

        File file = new File(path);
        file.getParentFile().mkdirs();
        graphviz.writeImageToFile(image, file);

        return null;
    }

    public byte[] drawIntoBytes(S obj) {
        if (this.graphviz == null) {
            this.graphviz = new GraphViz(this.settings);
        }

        this.graphviz.clearGraph();
        loadGraph(obj);

        return graphviz.getGraphUsingTemporaryFile(graphviz.getDotSource(), graphviz.imgtype, graphviz.algorithm);
    }

    public String getGraphSource(S obj) {
        if (this.graphviz == null) {
            this.graphviz = new GraphViz(this.settings);
        }

        this.graphviz.clearGraph();
        loadGraph(obj);

        return this.graphviz.getDotSource();
    }

    /**
     * Returns the graph of the given object as GraphML, an XML format that can be
     * loaded directly into networkx, e.g.:
     * <pre>import networkx as nx; nx.read_graphml(...)</pre>
     *
     * @return GraphML string, or null if the graphviz-based conversion failed.
     */
    public String getGraphMLSource(S obj) {
        if (this.graphviz == null) {
            this.graphviz = new GraphViz(this.settings);
        }

        this.graphviz.clearGraph();
        loadGraph(obj);

        return this.graphviz.toGraphML();
    }

    /**
     * Stores the graph of the given object as a GraphML file consumable by networkx.
     *
     * @param obj  the object whose graph should be exported
     * @param path destination file path (parent directories are created if needed)
     */
    public void saveGraphML(S obj, String path) {
        String graphML = getGraphMLSource(obj);
        if (graphML == null) {
            LOG.warning("Could not create GraphML source for " + obj);
            return;
        }

        File file = new File(path);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(graphML);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
    }

    public abstract void loadGraph(S obj);  //todo add indentation into the dot file

}
