package settings;

import ida.utils.tuples.Pair;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.queries.PlainQueriesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import org.apache.commons.cli.CommandLine;
import utils.Utilities;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UnknownFormatFlagsException;
import java.util.logging.Logger;

public class SourceFiles extends Sources {
    private static final Logger LOG = Logger.getLogger(SourceFiles.class.getName());

    public File template;
    public File trainExamples;
    public File testExamples;
    public File trainQueries;
    public File testQueries;

    public SourceFiles() {

    }

    public Pair<Boolean, String> validate(Settings settings) {
        Pair<Boolean, String> basePair = isValid(settings);
        String msg = "";
        for (Sources fold : folds) {
            Pair<Boolean, String> foldpair = fold.validate(settings);
            basePair.r &= foldpair.r;
            basePair.s += "due to fold: " + foldpair.s;
        }
        return basePair;
    }

    public Pair<Boolean, String> isValid(Settings settings) {
        boolean valid = true;
        String msg = "";

        if (trainQueriesReader == null && testQueriesReader == null && folds == null) {
            LOG.severe(msg = "Invalid learning setup - no trainQueriesSeparate nor testing samples provided");
            valid = false;
        }
        if (templateReader == null && trainQueriesReader == null && testQueriesReader == null) {
            LOG.severe(msg = "Invalid learning setup - no template nor trainQueriesSeparate or testing samples provided");
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
                if (parent.templateReader != null){
                    LOG.warning("Inconsistent setting - there are templates both in parent folder and fold folder (don't know which one to use)");
                }
                this.templateReader = new FileReader(this.template);
            } else {
                // if no template is provided in current folder, take the one from the parent folder
                this.templateReader = parent.templateReader;
            }
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(template.toString())) {
                    case "text/plain":
                        templateParseTree = new PlainTemplateParseTree(templateReader);
                        break;
                    case "application/xml":
                        //TODO
                        //templateParseTree = new XmlPlainParseTree(templateReader);
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
            this.trainExamplesReader = new FileReader(this.trainExamples = Paths.get(foldDir.toString(), cmd.getOptionValue("trainExamples", settings.trainExamplesFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(trainExamples.toString())) {
                    case "text/plain":
                        trainExamplesParseTree = new PlainExamplesParseTree(trainExamplesReader);
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
            this.testExamplesReader = new FileReader(this.testExamples = Paths.get(foldDir.toString(), cmd.getOptionValue("testExamples", settings.testExamplesFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(testExamples.toString())) {
                    case "text/plain":
                        testExamplesParseTree = new PlainExamplesParseTree(testExamplesReader);
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input test examples not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The test examples file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.info("There are no test examples");
        }

        try {
            this.trainQueriesReader = new FileReader(this.trainQueries = Paths.get(foldDir.toString(), cmd.getOptionValue("trainQueries", settings.trainQueriesFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(trainQueries.toString())) {
                    case "text/plain":
                        trainQueriesParseTree = new PlainQueriesParseTree(trainQueriesReader);
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input train queries not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The train queries file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.warning("There are no trainQueriesSeparate queries");
        }

        try {
            this.testQueriesReader = new FileReader(this.testQueries = Paths.get(foldDir.toString(), cmd.getOptionValue("trainQueries", settings.testQueriesFile)).toFile());
            try {
                switch (Utilities.identifyFileTypeUsingFilesProbeContentType(testQueries.toString())) {
                    case "text/plain":
                        testQueriesParseTree = new PlainQueriesParseTree(testQueriesReader);
                        break;
                    default:
                        throw new UnknownFormatFlagsException("File type of input test queries not recognized!");
                }
            } catch (IOException ex) {
                LOG.severe("The test queries file is not readable");
            }
        } catch (FileNotFoundException e) {
            LOG.info("There are no test queries");
        }

        return this;
    }
}
