package grounding;

import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.example.QueryAtom;
import constructs.template.Template;
import grounding.bottomUp.BottomUp;
import grounding.topDown.TopDown;
import networks.structure.NeuralNetwork;
import networks.structure.lrnnTypes.QueryNeuron;
import settings.Settings;
import training.NeuralSample;

import java.util.stream.Stream;

/**
 * Created by Gusta on 06.10.2016.
 */
public abstract class Grounder {
    Settings settings;

    public Grounder(Settings settings) {
        this.settings = settings;
    }

    public Stream<NeuralSample> ground(Stream<LogicSample> logicSampleStream, Template template) {
        Stream<NeuralSample> neuralSampleStream = null;
        if (settings.sequentialGrounding) {

        } else {
            neuralSampleStream = logicSampleStream.map(logicSample -> new NeuralSample(logicSample.target, ground(logicSample.query, template)));
        }
        return neuralSampleStream;
    }

    public NeuralSample ground(LogicSample logicSample, Template template) {
        NeuralSample neuralSample = new NeuralSample(logicSample.target, ground(logicSample.query, template));
        return neuralSample;
    }

    /**
     * Supervised grounding
     *
     * @param queryAtom
     * @param template
     * @return
     */
    public abstract QueryNeuron ground(QueryAtom queryAtom, Template template);

    /**
     * Unsupervised grounding
     *
     * @param example
     * @param template
     * @return
     */
    public abstract NeuralNetwork ground(LiftedExample example, Template template);

    public static Grounder getGrounder(Settings settings) {
        switch (settings.grounding) {
            case BUP:
                return new BottomUp();
            case TDOWN:
                return new TopDown();
            default:
                return new BottomUp();
        }
    }
}