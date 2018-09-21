package grounding.topDown;

import constructs.example.LiftedExample;
import constructs.example.QueryAtom;
import constructs.template.Template;
import constructs.template.templates.GraphTemplate;
import grounding.Grounder;
import networks.structure.networks.NeuralNetwork;
import networks.structure.neurons.QueryNeuron;
import settings.Settings;

/**
 * Created by Gusta on 06.10.2016.
 */
public class TopDown extends Grounder {
    public TopDown(Settings settings) {
        super(settings);
    }

    @Override
    public QueryNeuron ground(QueryAtom queryAtom, Template template) {
        return null;
    }

    @Override
    public QueryNeuron ground(QueryAtom queryAtom, GraphTemplate template) {
        return null;
    }

    @Override
    public NeuralNetwork ground(LiftedExample example, Template template) {
        return null;
    }

}
