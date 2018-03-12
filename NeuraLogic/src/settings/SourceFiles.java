package settings;

import neuralogic.ParseTree;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.XmlParseTree;
import org.apache.commons.cli.CommandLine;
import utils.Utilities;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatFlagsException;
import java.util.logging.Logger;

/**
 * Created by gusta on 26.3.17.
 */
public class SourceFiles {

    private static final Logger LOG = Logger.getLogger(SourceFiles.class.getName());

    /**
     * Source files may be further (even recursively) split into folds
     * TODO - test this behavior
     */
    public List<SourceFiles> folds;

    public ParseTree templateParseTree;
    public ParseTree examplesParseTree;

    public File templatePath;
    public File examplesPath;
    public File trainQueriesPath;
    public File testQueriesPath;

    public FileReader templateFileReader;
    public FileReader examplesFileReader;
    public FileReader trainQueriesFileReader;  // these should always be present for learning
    public FileReader testQueriesFileReader;


    public boolean validate(Settings settings) {
        boolean valid = true;

        settings.structureLearning = templateFileReader == null ? true : null;
        settings.testFile = testQueriesFileReader != null ? true : false;
        settings.training = trainQueriesFileReader == null ? false : true;
        settings.foldFiles = folds == null ? false : true;

        if (trainQueriesFileReader == null && testQueriesFileReader == null) {
            LOG.severe("Invalid learning setup - no training nor testing samples provided");
            valid = false;
        }
        if (templateFileReader == null && trainQueriesFileReader == null && testQueriesFileReader == null) {
            LOG.severe("Invalid learning setup - no template nor training or testing samples provided");
            valid = false;
        }
        //TODO is complete?
        return valid;
    }

    public SourceFiles(Settings settings, CommandLine cmd) throws FileNotFoundException {

        if (cmd.hasOption("folds")) {
            String sourcePath = cmd.getOptionValue("sourcePath", settings.defaults.sourcePath);
            String foldPrefix = cmd.getOptionValue("foldPrefix", settings.defaults.foldsPrefix);

            if (foldPrefix.contains(File.separator)) {
                LOG.severe("Invalid folds prefix name, it must not contain file separators: " + foldPrefix);
                throw new IllegalArgumentException(foldPrefix);
            }
            setupFromDir(settings, cmd, Paths.get(".").toAbsolutePath().toFile());

            crawlFolds(settings, cmd, sourcePath, foldPrefix);
        } else {
            setupFromDir(settings, cmd, Paths.get(".").toAbsolutePath().toFile());
        }
    }

    private void crawlFolds(Settings settings, CommandLine cmd, String path, String prefix) throws FileNotFoundException {
        File dir = new File(path);
        File[] foldDirs = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });
        folds = new ArrayList<>();
        for (File foldDir : foldDirs) {
            SourceFiles sfFold = setupFromDir(settings, cmd, foldDir);
            folds.add(sfFold);
            // recursive setup call on this fold sourceFiles object
            sfFold.crawlFolds(settings, cmd, Paths.get(path, foldDir.toString()).toString(), prefix);
        }
    }

    public SourceFiles setupFromDir(Settings settings, CommandLine cmd, File foldDir) {

        try {
            this.trainQueriesFileReader = new FileReader(this.trainQueriesPath = Paths.get(foldDir.toString(), cmd.getOptionValue("queries", settings.defaults.queriesFile)).toFile());
        } catch (FileNotFoundException e) {
            LOG.warning("There are no training queries");
        }

        try {
            this.templateFileReader = new FileReader(this.templatePath = Paths.get(foldDir.toString(), cmd.getOptionValue("template", settings.defaults.templateFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(templatePath.toString())) {
                    case "text/plain":
                        templateParseTree = new PlainTemplateParseTree(templateFileReader);
                        break;
                    case "application/xml":
                        templateParseTree = new XmlParseTree(templateFileReader);
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
            this.examplesFileReader = new FileReader(this.examplesPath = Paths.get(foldDir.toString(), cmd.getOptionValue("examples", settings.defaults.examplesFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(examplesPath.toString())) {
                    case "text/plain":
                        examplesParseTree = new PlainExamplesParseTree(examplesFileReader);
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input template/rules not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The examples file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.info("There are no training examples");
        }

        try {
            this.testQueriesFileReader = new FileReader(this.testQueriesPath = Paths.get(foldDir.toString(), cmd.getOptionValue("queries", settings.defaults.testFile)).toFile());
        } catch (FileNotFoundException e) {
            LOG.info("There are no testing queries");
        }

        return this;
    }
}