package settings;

import org.apache.commons.cli.CommandLine;
import utils.Utilities;
import utils.generic.Pair;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SourceFiles extends Sources {
    private static final Logger LOG = Logger.getLogger(SourceFiles.class.getName());

    public File template;
    public File trainExamples;
    public File testExamples;
    public File trainQueries;
    public File testQueries;

    public SourceFiles(Settings settings) {
        super(settings);
    }

    public Pair<Boolean, String> validate(Settings settings) {
        Pair<Boolean, String> basePair = isValid(settings);
        String msg = "";
        if (folds != null)
            for (Sources fold : folds) {
                Pair<Boolean, String> foldpair = fold.validate(settings);
                basePair.r &= foldpair.r;
                basePair.s += "due to fold: " + foldpair.s;
            }
        return basePair;
    }

    @Override
    public void infer(Settings settings) {
        if (checkForSubstring(trainExamples, settings.queryExampleSeparator, 2)) {
            LOG.info("Queries within train example file detected via separator " + settings.queryExampleSeparator);
            this.train.QueriesProvided = true;
        }
        if (checkForSubstring(testExamples, settings.queryExampleSeparator, 2)) {
            LOG.info("Queries within test example file detected via separator " + settings.queryExampleSeparator);
            this.test.QueriesProvided = true;
        }

        super.infer(settings);
    }

    public Pair<Boolean, String> isValid(Settings settings) {
        Pair<Boolean, String> validate = super.validate(settings);
        //TODO is complete?
        return validate;
    }

    public SourceFiles(Settings settings, CommandLine cmd) {
        super(settings);
        String sourcePath = cmd.getOptionValue("sourcePath", settings.sourcePath);

        if (cmd.hasOption("folds")) {

            String foldPrefix = cmd.getOptionValue("foldPrefix", settings.foldsPrefix);

            if (foldPrefix.contains(File.separator)) {
                LOG.severe("Invalid folds prefix name, it must not contain file separators: " + foldPrefix);
                throw new IllegalArgumentException(foldPrefix);
            }
            setupFromDir(settings, cmd, Paths.get(sourcePath).toAbsolutePath().toFile());

            crawlFolds(settings, cmd, settings.sourcePath, foldPrefix);
        } else {
            setupFromDir(settings, cmd, Paths.get(sourcePath).toAbsolutePath().toFile());
        }
    }

    private void crawlFolds(Settings settings, CommandLine cmd, String path, String prefix) {
        File dir = new File(path);
        File[] foldDirs = dir.listFiles((dir1, name) -> name.startsWith(prefix));
        this.folds = new ArrayList<>();
        for (File foldDir : foldDirs) {
            SourceFiles sFold = new SourceFiles(settings);
            sFold.parent = this;
            sFold.setupFromDir(settings, cmd, foldDir);
            this.folds.add(sFold);
            // recursive setup call on this fold sources object
            sFold.crawlFolds(settings, cmd, Paths.get(path, foldDir.toString()).toString(), prefix);
        }
    }

    /**
     * TODO do not create the parse trees here, process them separately later in the respective builders (load only Readers)
     *
     * @param settings
     * @param cmd
     * @param foldDir
     * @return
     */
    private SourceFiles setupFromDir(Settings settings, CommandLine cmd, File foldDir) {
        LOG.info("Setting up sources from directory: " + foldDir + " with settings : " + settings);
        try {
            String templatePath = cmd.getOptionValue("template", settings.templateFile);
            if (templatePath.startsWith("\\.") || settings.sourcePathProvided) {
                this.template = Paths.get(foldDir.toString(), templatePath).toFile();
            } else {
                this.template = Paths.get(templatePath).toFile();
            }
            if (this.template.exists()) {
                if (parent != null && parent.templateReader != null) {
                    LOG.warning("Inconsistent setting - there are templates both in parent folder and fold folder (don't know which one to use)");
                }
                this.templateReader = new FileReader(this.template);
            } else {
                // if no template is provided in current folder, take the one from the parent folder
                if (parent != null)
                    this.templateReader = parent.templateReader;
                else {
                    LOG.severe("There is no template found at the specified path! : " + templatePath);
                    throw new FileNotFoundException();
                }
            }
            switch (Utilities.identifyFileTypeUsingFilesProbeContentType(this.template.getAbsolutePath())) {
                case "text/plain":
                    settings.plaintextInput = true;
                    LOG.finer("Input template file type identified as plain text");
                    break;
                case "application/xml":
                    LOG.finer("Input template file type identified as xml");
                    break;
                case "application/json":
                    LOG.finer("Input template file type identified as json");
                    break;
                default:
                    LOG.warning("File type of input template/rules not recognized!");
            }
        } catch (FileNotFoundException e) {
            LOG.info("There is no learning template");
        }

        try {
            String trainExamplesPath = cmd.getOptionValue("trainExamples", settings.trainExamplesFile);
            if (trainExamplesPath.startsWith("\\.") || settings.sourcePathProvided) {
                this.trainExamples = Paths.get(foldDir.toString(), trainExamplesPath).toFile();
            } else {
                this.trainExamples = Paths.get(trainExamplesPath).toFile();
            }

            this.train.ExamplesReader = new FileReader(this.trainExamples);
            switch (Utilities.identifyFileTypeUsingFilesProbeContentType(this.trainExamples.toString())) {
                case "text/plain":
                    LOG.finer("Input train examples file type identified as plain text");
                    break;
                default:
                    LOG.warning("File type of input examples not recognized!");
            }

        } catch (FileNotFoundException e) {
            LOG.info("There are no train examples");
        }

        try {
            String testExamplesPath = cmd.getOptionValue("testExamples", settings.testExamplesFile);
            if (testExamplesPath.startsWith("\\.") || settings.sourcePathProvided) {
                this.testExamples = Paths.get(foldDir.toString(), testExamplesPath).toFile();
            } else {
                this.testExamples = Paths.get(testExamplesPath).toFile();
            }
            this.test.ExamplesReader = new FileReader(this.testExamples);
            switch (Utilities.identifyFileTypeUsingFilesProbeContentType(this.testExamples.toString())) {
                case "text/plain":
                    LOG.finer("Input test examples file type identified as plain text");
                    break;
                default:
                    LOG.warning("File type of input test examples not recognized!");
            }

        } catch (FileNotFoundException e) {
            LOG.info("There are no test examples found.");
        }

        try {
            String trainQueriesPath = cmd.getOptionValue("trainQueries", settings.trainQueriesFile);
            if (trainQueriesPath.startsWith("\\.") || settings.sourcePathProvided) {
                this.trainQueries = Paths.get(foldDir.toString(), trainQueriesPath).toFile();
            } else {
                this.trainQueries = Paths.get(trainQueriesPath).toFile();
            }
            this.train.QueriesReader = new FileReader(this.trainQueries);

            switch (Utilities.identifyFileTypeUsingFilesProbeContentType(this.trainQueries.toString())) {
                case "text/plain":
                    LOG.finer("Input train queries file type identified as plain text");
                    break;
                default:
                    LOG.warning("File type of input train queries not recognized!");
            }

        } catch (FileNotFoundException e) {
            LOG.info("There are no separate train queries found.");
        }

        try {
            String testQueriesPath = cmd.getOptionValue("testQueries", settings.testQueriesFile);
            if (testQueriesPath.startsWith("\\.") || settings.sourcePathProvided) {
                this.testQueries = Paths.get(foldDir.toString(), testQueriesPath).toFile();
            } else {
                this.testQueries = Paths.get(testQueriesPath).toFile();
            }
            this.test.QueriesReader = new FileReader(this.testQueries);
            switch (Utilities.identifyFileTypeUsingFilesProbeContentType(this.testQueries.toString())) {
                case "text/plain":
                    LOG.finer("Input tst queries file type identified as plain text");
                    break;
                default:
                    LOG.warning("File type of input test queries not recognized!");
            }

        } catch (FileNotFoundException e) {
            LOG.info("There are no separate test queries found.");
        }

        return this;
    }

    public boolean checkForSubstring(File file, String substring, int numberOfLines) {

        //are queries hidden in the example file? Quick check
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null && i++ < numberOfLines) {
                if (line.contains(substring))
                    return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}