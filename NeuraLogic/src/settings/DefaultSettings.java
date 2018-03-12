package settings;

import java.util.logging.Logger;

public class DefaultSettings {

    private static final Logger LOG = Logger.getLogger(DefaultSettings.class.getName());

    public String grounding = "bup";
    public String seed = "1";
    public String sourcePath = "./";
    public String foldsPrefix = "fold";
    public String testFile = "./test.txt";
    public String folds = "5";
    public String queriesFile = "./queries.txt";
    public String templateFile = "./template.txt";
    public String examplesFile = "./examples.txt";
    public String stratification = "true";
}