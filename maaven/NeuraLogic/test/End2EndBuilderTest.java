import building.End2endTrainigBuilder;
import cz.cvut.fel.ida.pipelines.constructs.template.Template;
import cz.cvut.fel.ida.pipelines.learning.results.Progress;
import cz.cvut.fel.ida.pipelines.networks.computation.training.NeuralModel;
import org.junit.Test;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import utils.Runner;
import cz.cvut.fel.ida.pipelines.generic.Pair;
import utils.logging.Logging;

public class End2EndBuilderTest {

    @Test
    public void simple(){
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