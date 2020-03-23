package cz.cvut.fel.ida.old_tests;

import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.logging.Logging;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.neuralogic.cli.utils.Runner;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.building.End2endTrainigBuilder;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;
import org.junit.jupiter.api.Test;

public class End2EndBuilderTest {

    @Test
    public void simple() throws Exception {
        Logging.initLogging();
        String[] args = new String("-path ./resources/datasets/neural/xor/vectorized/").split(" ");
        Settings settings = new Settings();
        Sources sources = Runner.getSources(args, settings);
        End2endTrainigBuilder end2EndTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = end2EndTrainigBuilder.buildPipeline();
//        settings.root = pipeline;
        Pair<String, Pair<Pair<Template, NeuralModel>, Progress>> result = pipeline.execute(sources);
        System.out.println(result);
    }

}