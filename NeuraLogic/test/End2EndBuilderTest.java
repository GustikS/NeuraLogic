import constructs.template.Template;
import networks.computation.evaluation.results.Progress;
import networks.computation.training.NeuralModel;
import org.junit.Test;
import pipelines.Pipeline;
import pipelines.building.End2endTrainigBuilder;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;
import utils.logging.Logging;

public class End2EndBuilderTest {

    @Test
    public void simple(){
        Logging.initLogging();
        String[] args = new String("-path ./resources/datasets/neural/xor/vectorized/").split(" ");
        Settings settings = new Settings();
        Sources sources = Sources.getSources(args, settings);
        End2endTrainigBuilder end2EndTrainigBuilder = new End2endTrainigBuilder(settings, sources);
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> pipeline = end2EndTrainigBuilder.buildPipeline();
        settings.root = pipeline;
        Pair<String, Pair<Pair<Template, NeuralModel>, Progress>> result = pipeline.execute(sources);
        System.out.println(result);
    }

}