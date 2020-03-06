import building.End2endTrainigBuilder;
import constructs.template.Template;
import learning.results.Progress;
import networks.computation.training.NeuralModel;
import org.junit.Test;
import pipelines.Pipeline;
import settings.Settings;
import settings.Sources;
import utils.Runner;
import utils.generic.Pair;
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