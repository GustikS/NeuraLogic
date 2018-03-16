package settings;

import ida.utils.tuples.Pair;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.examples.PlainQueriesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.XmlPlainParseTree;
import org.apache.commons.cli.CommandLine;
import utils.Utilities;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatFlagsException;
import java.util.logging.Logger;

public class SourceFiles extends Sources {
    private static final Logger LOG = Logger.getLogger(SourceFiles.class.getName());

    /**
     * Source files may be further (even recursively) split into folds
     * TODO - test this behavior
     */
    public List<SourceFiles> folds;
    private SourceFiles parent;

    public File template;
    public File examples;
    public File trainQueries;
    public File testQueries;

    public SourceFiles() {

    }

    public Pair<Boolean, String> validate(Settings settings) {
        Pair<Boolean, String> basePair = isValid(settings);
        String msg = "";
        for (SourceFiles fold : folds) {
            Pair<Boolean, String> foldpair = fold.validate(settings);
            basePair.r &= foldpair.r;
            basePair.s += "due to fold: " + foldpair.s;
        }
        return basePair;
    }

    public Pair<Boolean, String> isValid(Settings settings) {
        boolean valid = true;
        String msg = "";

        if (folds == null) {
            // settings for the regular case (without given xval folds)
            settings.structureLearning = templateFileReader == null ? true : null;
            settings.testFileProvided = testQueriesFileReader != null ? true : false;
            settings.training = trainQueriesFileReader == null ? false : true;
        } else {
            settings.crossvalidation = true;
            settings.foldFiles = true;
        }

        if (trainQueriesFileReader == null && testQueriesFileReader == null && folds == null) {
            LOG.severe(msg = "Invalid learning setup - no training nor testing samples provided");
            valid = false;
        }
        if (templateFileReader == null && trainQueriesFileReader == null && testQueriesFileReader == null) {
            LOG.severe(msg = "Invalid learning setup - no template nor training or testing samples provided");
            valid = false;
        }
        //TODO is complete?
        return new Pair(valid, msg);
    }


    public SourceFiles(Settings settings, CommandLine cmd) {
        if (cmd.hasOption("folds")) {
            String sourcePath = cmd.getOptionValue("sourcePath", settings.sourcePath);
            String foldPrefix = cmd.getOptionValue("foldPrefix", settings.foldsPrefix);

            if (foldPrefix.contains(File.separator)) {
                LOG.severe("Invalid folds prefix name, it must not contain file separators: " + foldPrefix);
                throw new IllegalArgumentException(foldPrefix);
            }
            setupFromDir(settings, cmd, Paths.get(".").toAbsolutePath().toFile());

            crawlFolds(settings, cmd, sourcePath, foldPrefix);
            settings.foldFiles = true;
        } else {
            setupFromDir(settings, cmd, Paths.get(".").toAbsolutePath().toFile());
        }
    }

    private void crawlFolds(Settings settings, CommandLine cmd, String path, String prefix) {
        File dir = new File(path);
        File[] foldDirs = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });
        this.folds = new ArrayList<>();
        for (File foldDir : foldDirs) {
            SourceFiles sFold = new SourceFiles();
            sFold.parent = this;
            sFold.setupFromDir(settings, cmd, foldDir);
            this.folds.add(sFold);
            // recursive setup call on this fold sources object
            sFold.crawlFolds(settings, cmd, Paths.get(path, foldDir.toString()).toString(), prefix);
        }
    }

    private SourceFiles setupFromDir(Settings settings, CommandLine cmd, File foldDir) {
        try {
            if ((this.template = Paths.get(foldDir.toString(), cmd.getOptionValue("template", settings.templateFile)).toFile()).exists()) {
                this.templateFileReader = new FileReader(this.template);
            } else {
                // if no template is provided in current folder, take the one from the parent folder
                this.templateFileReader = parent.templateFileReader;
            }
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(template.toString())) {
                    case "text/plain":
                        templateParseTree = new PlainTemplateParseTree(templateFileReader);
                        break;
                    case "application/xml":
                        templateParseTree = new XmlPlainParseTree(templateFileReader);
                        break;
                    case "application/json":
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input template/rules not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The template file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.info("There is no learning template");
        }

        try {
            this.examplesFileReader = new FileReader(this.examples = Paths.get(foldDir.toString(), cmd.getOptionValue("examples", settings.examplesFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(examples.toString())) {
                    case "text/plain":
                        examplesParseTree = new PlainExamplesParseTree(examplesFileReader);
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input examples not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The examples file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.info("There are no examples");
        }

        try {
            this.trainQueriesFileReader = new FileReader(this.trainQueries = Paths.get(foldDir.toString(), cmd.getOptionValue("queries", settings.queriesFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(trainQueries.toString())) {
                    case "text/plain":
                        trainQueriesParseTree = new PlainQueriesParseTree(trainQueriesFileReader);
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input train queries not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The train queriest file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.warning("There are no training queries");
        }

        try {
            this.testQueriesFileReader = new FileReader(this.testQueries = Paths.get(foldDir.toString(), cmd.getOptionValue("queries", settings.testFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(testQueries.toString())) {
                    case "text/plain":
                        testQueriesParseTree = new PlainQueriesParseTree(testQueriesFileReader);
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input train queries not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The examples file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.info("There are no testing queries");
        }

        return this;
    }
}
