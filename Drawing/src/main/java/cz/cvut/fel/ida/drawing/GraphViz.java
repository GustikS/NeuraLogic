package cz.cvut.fel.ida.drawing;

import cz.cvut.fel.ida.setup.Settings;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class is a (significant) modification of a simple java-graphviz wrapper
 * from https://github.com/jabbalaci/graphviz-java-api.
 * <p>
 * It can call graphviz without creating any temporary files with the use of process IO streams and ProcessBuilder.
 */
public class GraphViz {

    private static final Logger LOG = Logger.getLogger(GraphViz.class.getName());

    GraphicsDevice gd;
    int width;
    int height;

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
    public String algorithm;
    String imgtype;
    private boolean fix2ScreenSize;
    private boolean storeImage;

    public String tempDir;

    private String executable;

    /**
     * For storing multiple files within a single run.
     */
    private static int counter;

    /**
     * The source of the graph written in dot language.
     */
    private StringBuilder graph = new StringBuilder();

    /**
     * Attributes parsed from the DOT source so that GraphML export can keep
     * metadata (edge labels, weights, gradients, colors, styles, ...) that the
     * graphviz plain format does not preserve.
     */
    private final Map<String, Map<String, String>> dotNodeAttributes = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> dotEdgeAttributes = new LinkedHashMap<>();

    public static String sanitize(String name) {
        return "\"" + name + "\"";
    }

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
        this.fileName = settings.imageFile;
        this.algorithm = settings.graphVizAlgorithm;
        this.imgtype = settings.imgType;
        this.fix2ScreenSize = settings.fix2ScreenSize;
        this.storeImage = settings.storeNotShow;

        try {
            gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            width = gd.getDisplayMode().getWidth();
            height = gd.getDisplayMode().getHeight();
        } catch (Exception ex){
            width = 800;
            height = 600;
        }
    }

    private static String getGraphvizExecutable(Settings settings) {
        if (settings.graphvizPath != null) {
            return settings.graphvizPath;
        }

        if (Settings.os == Settings.OS.WINDOWS) {
            return settings.graphVizAlgorithm + ".exe";
        } else if (GraphViz.osName.contains("MacOSX")) {
            return settings.graphVizAlgorithm;
        } else {
            return settings.graphVizAlgorithm;
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
     * Returns the graph as GraphML, an XML format directly readable by networkx:
     * <pre>nx.read_graphml(...)</pre>
     *
     * The current DOT source is converted with the Graphviz 'plain' output,
     * so the graphviz executable must be available (the same one used for drawing).
     *
     * @return GraphML representation of the current graph, or null if conversion failed.
     */
    public String toGraphML() {
        try {
            String plain = getPlainSource();
            return convertPlainToGraphML(plain);
        } catch (IOException | InterruptedException e) {
            LOG.severe("Could not convert graph to GraphML: " + e.getMessage());
            return null;
        }
    }

    /**
     * Runs graphviz with -Tplain to obtain an easily parseable representation of the graph.
     */
    private String getPlainSource() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(executable, "-Tplain", "-K" + algorithm);
        builder.redirectErrorStream(true); // This is important part
        Process process = builder.start();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        bw.write(graph.toString());
        bw.flush();
        bw.close();

        return readInputTextStream(process.getInputStream());
    }

    private String readInputTextStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    private String convertPlainToGraphML(String plain) {
        String graphDeclaration = graph.toString().trim();
        boolean directed = graphDeclaration.contains("digraph");

        ArrayList<String[]> nodes = new ArrayList<>();
        ArrayList<String> edges = new ArrayList<>();
        HashSet<String> seenNodes = new HashSet<>();
        HashSet<String> seenEdges = new HashSet<>();

        for (String line : plain.split("\n")) {
            ArrayList<String> tokens = tokenize(line);
            if (tokens.isEmpty()) {
                continue;
            }
            switch (tokens.get(0)) {
                case "node":
                    if (tokens.size() > 1 && seenNodes.add(tokens.get(1))) {
                        String label = tokens.size() > 6 ? tokens.get(6) : tokens.get(1);
                        nodes.add(new String[]{tokens.get(1), label});
                    }
                    break;
                case "edge":
                    if (tokens.size() > 2) {
                        String edge = tokens.get(1) + "\u0000" + tokens.get(2);
                        if (seenEdges.add(edge)) {
                            edges.add(edge);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n");
        sb.append("  <key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n");

        LinkedHashSet<String> edgeKeys = new LinkedHashSet<>();
        for (Map<String, String> attrs : dotEdgeAttributes.values()) {
            edgeKeys.addAll(attrs.keySet());
        }
        for (String edgeKey : edgeKeys) {
            sb.append("  <key id=\"edge_").append(escapeXml(edgeKey))
                    .append("\" for=\"edge\" attr.name=\"").append(escapeXml(edgeKey))
                    .append("\" attr.type=\"string\"/>\n");
        }

        sb.append("  <graph edgedefault=\"").append(directed ? "directed" : "undirected").append("\">\n");
        for (String[] node : nodes) {
            sb.append("    <node id=\"").append(escapeXml(node[0])).append("\">\n");
            sb.append("      <data key=\"label\">").append(escapeXml(node[1])).append("</data>\n");
            sb.append("    </node>\n");
        }
        for (String edge : edges) {
            String[] parts = edge.split("\u0000");
            Map<String, String> attrs = dotEdgeAttributes.get(edge);
            if (attrs == null || attrs.isEmpty()) {
                sb.append("    <edge source=\"").append(escapeXml(parts[0]))
                        .append("\" target=\"").append(escapeXml(parts[1])).append("\"/>\n");
            } else {
                sb.append("    <edge source=\"").append(escapeXml(parts[0]))
                        .append("\" target=\"").append(escapeXml(parts[1])).append("\">\n");
                for (Map.Entry<String, String> attr : attrs.entrySet()) {
                    sb.append("      <data key=\"edge_").append(escapeXml(attr.getKey())).append("\">")
                            .append(escapeXml(attr.getValue())).append("</data>\n");
                }
                sb.append("    </edge>\n");
            }
        }
        sb.append("  </graph>\n");
        sb.append("</graphml>\n");
        return sb.toString();
    }

    /**
     * Splits a graphviz plain-format line into whitespace-separated tokens,
     * treating double-quoted sections (which may contain spaces) as single tokens.
     */
    private ArrayList<String> tokenize(String line) {
        ArrayList<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < line.length()) {
            while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (i >= line.length()) {
                break;
            }
            if (line.charAt(i) == '"') {
                StringBuilder quoted = new StringBuilder();
                i++;
                while (i < line.length()) {
                    char c = line.charAt(i);
                    if (c == '\\' && i + 1 < line.length()) {
                        char next = line.charAt(i + 1);
                        if (next == '"') {
                            quoted.append(next);
                        } else {
                            quoted.append(c).append(next);
                        }
                        i += 2;
                    } else if (c == '"') {
                        i++;
                        break;
                    } else {
                        quoted.append(c);
                        i++;
                    }
                }
                tokens.add(quoted.toString());
            } else {
                int start = i;
                while (i < line.length() && !Character.isWhitespace(line.charAt(i))) {
                    i++;
                }
                tokens.add(line.substring(start, i));
            }
        }
        return tokens;
    }

    private String escapeXml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Records node/edge attributes from one DOT statement so that GraphML
     * export can attach them as edge/node data. The generated DOT is simple
     * enough to parse statement by statement; statements that open/close the
     * graph or subgraphs carry no attributes and are skipped.
     */
    private void parseDotStatement(String statement) {
        String line = statement.trim();
        if (line.isEmpty() || line.startsWith("{") || line.startsWith("}") || line.startsWith("subgraph")
                || line.startsWith("strict") || line.startsWith("digraph") || line.startsWith("graph")
                || line.startsWith("//") || line.startsWith("compound") || line.startsWith("node")
                || line.startsWith("edge")) {
            return;
        }

        int bracketStart = findUnquoted(line, '[');
        String left;
        String attributes = null;
        if (bracketStart >= 0) {
            left = line.substring(0, bracketStart).trim();
            int bracketEnd = findMatchingBracket(line, bracketStart);
            if (bracketEnd > bracketStart) {
                attributes = line.substring(bracketStart + 1, bracketEnd);
            }
        } else {
            left = line;
            int semicolon = findUnquoted(left, ';');
            if (semicolon >= 0) {
                left = left.substring(0, semicolon);
            }
            left = left.trim();
        }

        int arrow = findArrow(left);
        if (arrow >= 0) {
            String source = unquote(left.substring(0, arrow).trim());
            String target = unquote(left.substring(arrow + 2).trim());
            if (source.isEmpty() || target.isEmpty()) {
                return;
            }
            Map<String, String> attrs = dotEdgeAttributes.computeIfAbsent(
                    source + "\u0000" + target, k -> new LinkedHashMap<>());
            if (attributes != null) {
                parseDotAttributes(attributes, attrs);
            }
        } else if (!left.isEmpty()) {
            String node = unquote(left);
            if (node.isEmpty()) {
                return;
            }
            Map<String, String> attrs = dotNodeAttributes.computeIfAbsent(
                    node, k -> new LinkedHashMap<>());
            if (attributes != null) {
                parseDotAttributes(attributes, attrs);
            }
        }
    }

    private void parseDotAttributes(String content, Map<String, String> target) {
        for (String part : splitTopLevel(content, ',')) {
            String attribute = part.trim();
            if (attribute.isEmpty()) {
                continue;
            }
            int equals = findUnquoted(attribute, '=');
            if (equals <= 0) {
                continue;
            }
            String key = attribute.substring(0, equals).trim();
            String value = unquote(attribute.substring(equals + 1).trim());
            target.put(key, value);
        }
    }

    private ArrayList<String> splitTopLevel(String s, char delimiter) {
        ArrayList<String> parts = new ArrayList<>();
        int start = 0;
        int bracketDepth = 0;
        boolean inQuotes = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (inQuotes) {
                if (c == '\\' && i + 1 < s.length()) {
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                } else if (c == delimiter && bracketDepth == 0) {
                    parts.add(s.substring(start, i));
                    start = i + 1;
                }
            }
        }
        parts.add(s.substring(start));
        return parts;
    }

    private int findUnquoted(String s, char target) {
        boolean inQuotes = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (inQuotes) {
                if (c == '\\' && i + 1 < s.length()) {
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == target) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int findMatchingBracket(String s, int openBracket) {
        int depth = 0;
        boolean inQuotes = false;
        for (int i = openBracket; i < s.length(); i++) {
            char c = s.charAt(i);
            if (inQuotes) {
                if (c == '\\' && i + 1 < s.length()) {
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == '[') {
                    depth++;
                } else if (c == ']') {
                    depth--;
                    if (depth == 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private int findArrow(String s) {
        boolean inQuotes = false;
        for (int i = 0; i + 1 < s.length(); i++) {
            char c = s.charAt(i);
            if (inQuotes) {
                if (c == '\\') {
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == '-' && s.charAt(i + 1) == '>') {
                    return i;
                }
            }
        }
        return -1;
    }

    private String unquote(String s) {
        if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            return s.substring(1, s.length() - 1).replace("\\\"", "\"");
        }
        return s;
    }

    /**
     * Adds a string to the graph's source (without newline).
     */
    public void add(String line) {
        this.graph.append(line);
        parseDotStatement(line);
    }

    /**
     * Adds a string to the graph's source (with newline).
     */
    public void addln(String line) {
        this.graph.append(line + "\n");
        parseDotStatement(line);
    }

    /**
     * Adds a newline to the graph's source.
     */
    public void addln() {
        this.graph.append('\n');
    }

    public void uniqueLines() {
        String[] split = graph.toString().split("\n");
        HashSet<String> strings = new HashSet<>();
        for (String s : split) {
            strings.add(s);
        }
        String collect = strings.stream().collect(Collectors.joining("\n"));
        graph = new StringBuilder(collect);
        dotNodeAttributes.clear();
        dotEdgeAttributes.clear();
        for (String s : split) {
            parseDotStatement(s);
        }
    }

    public void clearGraph() {
        this.graph = new StringBuilder();
        dotNodeAttributes.clear();
        dotEdgeAttributes.clear();
    }

    private String getImageName(String name) {
        return fileName + counter++ + "_" + name + "." + imgtype;
    }

    private String getGraphName(String name) {
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
    public byte[] getGraphImage(String nameOrEmpty) throws IOException, InterruptedException {
        String[] args = getArgs(nameOrEmpty);

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

    private String[] getArgs(String name) {
        ArrayList<String> args = new ArrayList<>();
        args.add(executable);
        args.add("-T" + imgtype);
        args.add("-K" + algorithm);
        args.add("-Gdpi=" + dpiSizes[this.currentDpiPos]);
        if (fix2ScreenSize)
            args.add("-Gsize=" + width / dpiSizes[this.currentDpiPos] + "," + height / dpiSizes[this.currentDpiPos] + "\\!");
        if (storeImage)
            args.add("-o " + getImageName(name));
        return args.toArray(new String[args.size()]);
    }

    private byte[] readInputImageStream(InputStream inputStream) throws IOException {
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
    public byte[] getGraphUsingTemporaryFile(String dot_source, String type, String representationType) {
        File dot;
        byte[] img_stream = null;

        try {
            dot = writeDotSourceToFile(dot_source);
            if (dot != null) {
                img_stream = getImgStream(dot, type, representationType);
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
    public int writeImageToFile(byte[] img, String file) {
        File to = new File(tempDir + "/" + sanitize(file) + "." + imgtype);
        return writeImageToFile(img, to);
    }

    /**
     * Writes the graph's image in a file.
     *
     * @param img A byte array containing the image of the graph.
     * @param to  A File object to where we want to write.
     * @return Success: 1, Failure: -1
     */
    public int writeImageToFile(byte[] img, File to) {
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
    private byte[] getImgStream(File dot, String type, String representationType) {
        File img;
        byte[] img_stream = null;

        try {
            img = File.createTempFile("graph_", "." + type, new File(this.tempDir));
            Runtime rt = Runtime.getRuntime();

            // patch by Mike Chenault
            // representation type with -K argument by Olivier Duplouy
            String[] args = {executable, "-T" + type, "-K" + representationType, "-Gdpi=" + dpiSizes[this.currentDpiPos], dot.getAbsolutePath(), "-o", img.getAbsolutePath()};

            Process p = rt.exec(args);  //this is plain dangerous in multithreaded programs and repeated runs...
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
        dotNodeAttributes.clear();
        dotEdgeAttributes.clear();
        for (String line : sb.toString().split("\n")) {
            parseDotStatement(line);
        }
    }

}