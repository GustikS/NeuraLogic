package grounding.bottomUp;

import constructs.example.LiftedExample;
import constructs.example.QueryAtom;
import constructs.template.Template;
import grounding.Grounder;
import networks.structure.NeuralNetwork;
import networks.structure.lrnnTypes.QueryNeuron;
import settings.Settings;

/**
 * Created by Gusta on 06.10.2016.
 */
public class BottomUp extends Grounder {

    public BottomUp(Settings settings) {
        super(settings);
    }

    @Override
    public QueryNeuron ground(QueryAtom queryAtom, Template template) {
        return null;
    }

    @Override
    public NeuralNetwork ground(LiftedExample example, Template template) {
        return null;
    }
}