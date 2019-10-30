package utils.drawing;

import settings.Settings;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

// GraphViz.java - a simple API to call dot from Java programs
/*$Id$*/
/*
 ******************************************************************************
 *                                                                            *
 *                    (c) Copyright Laszlo Szathmary                          *
 *                                                                            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published by   *
 * the Free Software Foundation; either version 2.1 of the License, or        *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public    *
 * License for more details.                                                  *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program; if not, write to the Free Software Foundation,    *
 * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.                              *
 *                                                                            *
 ******************************************************************************
 */

/**
 * <dl>
 * <dt>Purpose: GraphViz Java API
 * <dd>
 *
 * <dt>Description:
 * <dd> With this Java class you can simply call dot
 * from your Java programs.
 * <dt>Example usage:
 * <dd>
 * <pre>
 *    GraphViz gv = new GraphViz();
 *    gv.addln(gv.start_graph());
 *    gv.addln("A -> B;");
 *    gv.addln("A -> C;");
 *    gv.addln(gv.end_graph());
 *    System.out.println(gv.getDotSource());
 *
 *    String type = "gif";
 *    String representationType="dot";
 *    File out = new File("out." + type);   // out.gif in this example
 *    gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, representationType), out );
 * </pre>
 * </dd>
 *
 * </dl>
 *
 * @author Laszlo Szathmary (<a href="jabba.laci@gmail.com">jabba.laci@gmail.com</a>)
 * @version v0.1, 2003/12/04 (December) -- first release
 */
// todo turn this into a separate library with my modifications (running Graphviz without temporary files, instant drawing etc.)
public class GraphViz {

    private static final Logger LOG = Logger.getLogger(GraphViz.class.getName());

    private static final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private static final int width = gd.getDisplayMode().getWidth();
    private static final int height = gd.getDisplayMode().getHeight();

    /**
     * The image size in dpi. 96 dpi is normal size. Higher values are 10% higher each.
     * Lower values 10% lower each.
     * <p>
     * dpi patch by Peter Mueller
     */
    private static final int[] dpiSizes = {46, 51, 57, 63, 70, 78, 86, 96, 106, 116, 128, 141, 155, 170, 187, 206, 226, 249};


    /**
     * Detects the client's operating system.
     */
    private static final String osName = System.getProperty("os.name").replaceAll("\\s", "");


    /**
     * Define the index in the image size array.
     */
    private int currentDpiPos = 7;

    private String fileName;
    private String algorithm;
    private String imgtype;
    private boolean fixScreenSize;
    private boolean storeNotShow;

    /**
     * The source of the graph written in dot language.
     */
    private StringBuilder graph = new StringBuilder();

    public String tempDir;

    private String executable;

    /**
     * For storing multiple files within a single run.
     */
    private static int counter;

    /**
     * Configurable Constructor with path to executable dot and a temp dir
     *
     * @param executable absolute path to dot executable
     * @param tempDir    absolute path to temp directory
     */
    private GraphViz(String executable, String tempDir) {
        this.executable = executable;
        this.tempDir = tempDir;
    }

    public GraphViz(Settings settings) {
        this(getGraphvizExecutable(settings), settings.outDir);
        this.fileName = settings.drawingFile;
        this.algorithm = settings.graphVizAlgorithm;
        this.imgtype = settings.imgtype;
        this.fixScreenSize = settings.fix2ScreenSize;
        this.storeNotShow = settings.storeNotShow;
    }

    private static String getGraphvizExecutable(Settings settings) {
        if (Settings.os == Settings.OS.WINDOWS) {
            return settings.graphvizPathWindows + "/" + settings.graphVizAlgorithm + ".exe";
        } else if (GraphViz.osName.contains("MacOSX")) {
            return settings.graphvizPathMac + "/" + settings.graphVizAlgorithm;
        } else {
            return settings.graphvizPathLinux + "/" + settings.graphVizAlgorithm;
        }
    }

    /**
     * Increase the image size (dpi).
     */
    public void increaseDpi() {
        if (this.currentDpiPos < (this.dpiSizes.length - 1)) {
            ++this.currentDpiPos;
        }
    }

    /**
     * Decrease the image size (dpi).
     */
    public void decreaseDpi() {
        if (this.currentDpiPos > 0) {
            --this.currentDpiPos;
        }
    }

    public int getImageDpi() {
        return this.dpiSizes[this.currentDpiPos];
    }


    /**
     * Returns the graph's source description in dot language.
     *
     * @return Source of the graph in dot language.
     */
    public String getDotSource() {
        return this.graph.toString();
    }

    /**
     * Adds a string to the graph's source (without newline).
     */
    public void add(String line) {
        this.graph.append(line);
    }

    /**
     * Adds a string to the graph's source (with newline).
     */
    public void addln(String line) {
        this.graph.append(line + "\n");
    }

    /**
     * Adds a newline to the graph's source.
     */
    public void addln() {
        this.graph.append('\n');
    }

    public void clearGraph() {
        this.graph = new StringBuilder();
    }

    public String getImageName(String name) {
        return fileName + counter++ + "_" + name + "." + imgtype;
    }

    public String getGraphName(String name) {
        return fileName + counter++ + "_" + name + "." + algorithm;
    }

    public void storeGraphSource(String name) throws IOException {
        try {
            File file = new File(getGraphName(name));
            FileWriter fout = new FileWriter(file);
            fout.write(getDotSource());
            fout.close();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
    }

    /**
     * Call Graphviz using IO streams, i.e. without creating any temporary files
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public byte[] getGraphImage(String name) throws IOException, InterruptedException {
        String[] args = getArgs(name);

        ProcessBuilder builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true); // This is important part
        Process process = builder.start();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        bw.write(graph.toString()); //or better yet : toString().getBytes(Charset.forName("UTF-8"))
        bw.flush();
        bw.close();

        //wait here?    - probably not, the outputstream does the waiting

        byte[] bytes = readInputImageStream(process.getInputStream());
        return bytes;
    }

    public String[] getArgs(String name) {
        ArrayList<String> args = new ArrayList<>();
        args.add(executable);
        args.add("-T" + imgtype);
        args.add("-Gdpi=" + dpiSizes[this.currentDpiPos]);
        if (fixScreenSize)
            args.add("-Gsize=" + width / dpiSizes[this.currentDpiPos] + "," + height / dpiSizes[this.currentDpiPos] + "\\!");
        if (storeNotShow)
            args.add("-o " + getImageName(name));
        return args.toArray(new String[args.size()]);
    }

    public byte[] readInputImageStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        buffer.close();
        return buffer.toByteArray();
    }

    public static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }


    /**
     * Returns the graph as an image in binary format.
     *
     * @param dot_source         Source of the graph to be drawn.
     * @param type               Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
     * @param representationType Type of how you want to represent the graph:
     *                           <ul>
     *                           <li>dot</li>
     *                           <li>neato</li>
     *                           <li>fdp</li>
     *                           <li>sfdp</li>
     *                           <li>twopi</li>
     *                           <li>circo</li>
     *                           </ul>
     *                           see http://www.graphviz.org under the Roadmap title
     * @return A byte array containing the image of the graph.
     */
    public byte[] getGraph(String dot_source, String type, String representationType) {
        File dot;
        byte[] img_stream = null;

        try {
            dot = writeDotSourceToFile(dot_source);
            if (dot != null) {
                img_stream = get_img_stream(dot, type, representationType);
                if (dot.delete() == false) {
                    System.err.println("Warning: " + dot.getAbsolutePath() + " could not be deleted!");
                }
                return img_stream;
            }
            return null;
        } catch (java.io.IOException ioe) {
            return null;
        }
    }

    /**
     * Writes the graph's image in a file.
     *
     * @param img  A byte array containing the image of the graph.
     * @param file Name of the file to where we want to write.
     * @return Success: 1, Failure: -1
     */
    public int writeGraphToFile(byte[] img, String file) {
        File to = new File(file);
        return writeGraphToFile(img, to);
    }

    /**
     * Writes the graph's image in a file.
     *
     * @param img A byte array containing the image of the graph.
     * @param to  A File object to where we want to write.
     * @return Success: 1, Failure: -1
     */
    public int writeGraphToFile(byte[] img, File to) {
        try {
            FileOutputStream fos = new FileOutputStream(to);
            fos.write(img);
            fos.close();
        } catch (java.io.IOException ioe) {
            return -1;
        }
        return 1;
    }

    /**
     * It will call the external dot program, and return the image in
     * binary format.
     *
     * @param dot                Source of the graph (in dot language).
     * @param type               Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
     * @param representationType Type of how you want to represent the graph:
     *                           <ul>
     *                           <li>dot</li>
     *                           <li>neato</li>
     *                           <li>fdp</li>
     *                           <li>sfdp</li>
     *                           <li>twopi</li>
     *                           <li>circo</li>
     *                           </ul>
     *                           see http://www.graphviz.org under the Roadmap title
     * @return The image of the graph in .gif format.
     */
    private byte[] get_img_stream(File dot, String type, String representationType) {
        File img;
        byte[] img_stream = null;

        try {
            img = File.createTempFile("graph_", "." + type, new File(this.tempDir));
            Runtime rt = Runtime.getRuntime();

            // patch by Mike Chenault
            // representation type with -K argument by Olivier Duplouy
            String[] args = {executable, "-T" + type, "-K" + representationType, "-Gdpi=" + dpiSizes[this.currentDpiPos], dot.getAbsolutePath(), "-o", img.getAbsolutePath()};

            Process p = rt.exec(args);  //this is plain dangerous in multithread programs and repeated runs...
            int i = p.waitFor();
            if (i > 0) {
                System.err.println(i);
            }

            FileInputStream in = new FileInputStream(img.getAbsolutePath());
            img_stream = new byte[in.available()];
            in.read(img_stream);
            // Close it if we need to
            if (in != null) {
                in.close();
            }

            if (img.delete() == false) {
                System.err.println("Warning: " + img.getAbsolutePath() + " could not be deleted!");
            }
        } catch (java.io.IOException ioe) {
            System.err.println("Error:    in I/O processing of tempfile in dir " + tempDir + "\n");
            System.err.println("       or in calling external command");
            ioe.printStackTrace();
        } catch (java.lang.InterruptedException ie) {
            System.err.println("Error: the execution of the external program was interrupted");
            ie.printStackTrace();
        }

        return img_stream;
    }

    /**
     * Writes the source of the graph in a file, and returns the written file
     * as a File object.
     *
     * @param str Source of the graph (in dot language).
     * @return The file (as a File object) that contains the source of the graph.
     */
    private File writeDotSourceToFile(String str) throws java.io.IOException {
        File temp;
        try {
            temp = File.createTempFile("graph_", ".dot.tmp", new File(tempDir));
            FileWriter fout = new FileWriter(temp);
            fout.write(str);
            fout.close();
        } catch (Exception e) {
            System.err.println("Error: I/O error while writing the dot source to temp file!");
            return null;
        }
        return temp;
    }

    /**
     * Returns a string that is used to start a graph.
     *
     * @return A string to open a graph.
     */
    public void start_graph() {
        graph.append("digraph G {").append("\n");
    }

    /**
     * Returns a string that is used to end a graph.
     *
     * @return A string to close a graph.
     */
    public void end_graph() {
        graph.append("}").append("\n");
    }

    /**
     * Takes the cluster or subgraph id as input parameter and returns a string
     * that is used to start a subgraph.
     *
     * @return A string to open a subgraph.
     */
    public void start_subgraph(int clusterid) {
        graph.append("subgraph cluster_" + clusterid + " {");
    }

    /**
     * Returns a string that is used to end a graph.
     *
     * @return A string to close a graph.
     */
    public void end_subgraph() {
        graph.append("}");
    }

    /**
     * Read a DOT graph from a text file.
     *
     * @param input Input text file containing the DOT graph
     *              source.
     */
    public void readSource(String input) {
        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(input);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            dis.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        this.graph = sb;
    }

}