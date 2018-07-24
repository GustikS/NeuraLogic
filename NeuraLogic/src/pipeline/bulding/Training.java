package pipeline.bulding;

import constructs.template.Template;
import learning.LearningSample;
import settings.Settings;

import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Training {
    private static final Logger LOG = Logger.getLogger(Training.class.getName());
    Settings settings;

    public Training(Settings settings) {
        this.settings = settings;
    }

    Consumer<Stream<LearningSample>> getTrainer(Template template){

    }

    Consumer<Stream<LearningSample>> getTrainer(){

    }
}