import constructs.template.Template;
import networks.structure.export.NeuralSerializer;
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
        String[] args = new String("-path ./resources/datasets/neural/xor/vectorized/").split(" ");
        Settings settings = new Settings();
        Sources sources = Main.getSources(args, settings);
        End2endNNBuilder end2endNNBuilder = new End2endNNBuilder(settings, sources);
        Pipeline<Sources, Pair<Pair<Template, List<NeuralSerializer.SerializedWeight>>, List<NeuralSerializer.SerializedSample>>> pipeline = end2endNNBuilder.buildPipeline();
        settings.root = pipeline;
        Pair<String, Pair<Pair<Template, List<NeuralSerializer.SerializedWeight>>, List<NeuralSerializer.SerializedSample>>> result = pipeline.execute(sources);
        System.out.println(result);
    }

}