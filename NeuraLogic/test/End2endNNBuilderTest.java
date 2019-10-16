import constructs.template.Template;
import networks.computation.training.NeuralModel;
import networks.computation.training.NeuralSample;
import org.junit.Test;
import pipelines.Pipeline;
import pipelines.building.End2endNNBuilder;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;

import java.util.List;

public class End2endNNBuilderTest {

    @Test
    public void simple(){
        Main.initLogging();
        String[] args = new String("-e ./resources/datasets/family/examples -t ./resources/datasets/family/template -q ./resources/datasets/family/queries").split(" ");
        Settings settings = new Settings();
        Sources sources = Main.getSources(args, settings);
        End2endNNBuilder end2endNNBuilder = new End2endNNBuilder(settings, sources);
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> pipeline = end2endNNBuilder.buildPipeline();
        settings.root = pipeline;
        Pair<String, Pair<Pair<Template, NeuralModel>, List<NeuralSample>>> result = pipeline.execute(sources);
        System.out.println(result);
    }

}