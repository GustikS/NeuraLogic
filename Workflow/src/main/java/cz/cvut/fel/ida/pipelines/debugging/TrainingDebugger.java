package cz.cvut.fel.ida.pipelines.debugging;

import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.learning.results.Progress;
import cz.cvut.fel.ida.neural.networks.computation.training.NeuralModel;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.debugging.drawing.TemplateDrawer;
import cz.cvut.fel.ida.pipelines.pipes.generic.FirstFromPairPipe;
import cz.cvut.fel.ida.pipelines.pipes.generic.StreamifyPipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.setup.Sources;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingDebugger extends TemplateDebugger {
    private static final Logger LOG = Logger.getLogger(TrainingDebugger.class.getName());

    private Consumer<Map<Integer, Weight>> templateRedrawCallback;

    public TrainingDebugger(Sources sources, Settings settings) {
        super(sources, settings);
        if (intermediateDebug) {
            settings.debugPipeline = true;
            settings.debugTemplate = true;
            settings.debugSampleTraining = true;
            settings.debugGrounding = true;
        }
    }

    public TrainingDebugger(Settings settings, Template template) {
        super(settings);
        drawer = new TemplateDrawer(settings);
        this.templateRedrawCallback = (weightMap -> {
            template.updateWeightsFrom(weightMap);
            drawer.draw(template);
        });
        if (intermediateDebug) {
            settings.debugPipeline = true;
            settings.debugTemplate = true;
            settings.debugSampleTraining = true;
            settings.debugGrounding = true;
        }
    }

    /**
     * Build whole training and return learned template
     *
     * @return
     */
    @Override
    public Pipeline<Sources, Stream<Template>> buildPipeline() {
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> sourcesPairPipeline = pipeline.registerStart(end2endTrainigBuilder.buildPipeline());
        //just extract the learned template
        Pipe<Pair<Pair<Template, NeuralModel>, Progress>, Pair<Template, NeuralModel>> pairTemplatePipe1 = pipeline.register(new FirstFromPairPipe<>("FirstFromPairPipe1"));
        sourcesPairPipeline.connectAfter(pairTemplatePipe1);
        Pipe<Pair<Template, NeuralModel>, Template> pairTemplatePipe = pipeline.register(pairTemplatePipe1.connectAfter(new FirstFromPairPipe<>("FirstFromPairPipe2")));
        pipeline.registerEnd(pairTemplatePipe.connectAfter(new StreamifyPipe<>()));
        return pipeline;
    }

    public void debugWeights(Map<Integer, Weight> weightMap) {
        LOG.info("drawing a trained template with updated weights...");
        templateRedrawCallback.accept(weightMap);
    }
}