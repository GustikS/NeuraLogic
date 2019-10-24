package utils.drawing;

import settings.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gusta on 8.3.17.
 */
public abstract class Drawer<S> {

    private static final Logger LOG = Logger.getLogger(Drawer.class.getName());

    public String algorithm = "dot";
    public String imgtype = "png";
    public String defaultName;

    protected final GraphViz graphviz;
    Settings.Detail drawingDetail;

    protected static List<String> dot = new ArrayList<>();

    protected final Settings settings;
    protected final NumberFormat numberFormat;

    public Drawer(Settings settings) {
        this.settings = settings;
        this.numberFormat = Settings.numberFormat;
        this.defaultName = settings.drawingFile + "." + imgtype;
        this.graphviz = new GraphViz(settings.graphvizPathLinux, settings.outDir);
        this.drawingDetail = settings.drawingDetail;
    }

    private String getGraphvizExecutable() {
        if (Settings.os == Settings.OS.WINDOWS) {
            return settings.graphvizPathWindows;
        } else {
            return settings.graphvizPathLinux;
        }
    }

    public void display(byte[] imageBytes) {
        JFrame frame = new JFrame();
        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            return;
        }
        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);
        frame.add(label);
        frame.setDefaultCloseOperation
                (JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public abstract void draw(S obj);
}
