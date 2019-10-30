package utils.drawing;

import settings.Settings;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Drawer<S> {

    private static final Logger LOG = Logger.getLogger(Drawer.class.getName());

    protected final NumberFormat numberFormat;
    protected Settings.Detail drawingDetail;
    protected boolean storeNotShow;

    protected final GraphViz graphviz;
    public final Settings settings;

    public Drawer(Settings settings) {
        this.settings = settings;
        this.graphviz = new GraphViz(settings);

        this.numberFormat = Settings.numberFormat;
        this.drawingDetail = settings.drawingDetail;
        this.storeNotShow = settings.storeNotShow;
    }

    public void display(byte[] imageBytes, String name) {
        //now turn off annoying java awt logging messages here
        Logger.getLogger("java.awt").setLevel(Level.WARNING);
        Logger.getLogger("sun.awt").setLevel(Level.WARNING);
        Logger.getLogger("javax.swing").setLevel(Level.WARNING);

        Object lock = new Object();
        DebugWindow debugWindow = new DebugWindow(imageBytes, name, lock);
        debugWindow.pauseUntilWindowCloses(lock);
    }

    public void draw(S obj) {
        this.graphviz.clearGraph();
        loadGraph(obj);
        try {
            if (storeNotShow) {
                graphviz.storeGraphSource(obj.toString());
            } else {
                display(graphviz.getGraphImage(obj.toString()), obj.toString());
            }
        } catch (IOException | InterruptedException e) {
            LOG.severe(e.getMessage());
        }
    }

    public abstract void loadGraph(S obj);

}
