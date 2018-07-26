package grounding;

import constructs.template.Template;
import constructs.example.LogicSample;
import pipeline.SampleProcess;

/**
 * Created by Gusta on 06.10.2016.
 */
public abstract class Grounder implements SampleProcess<LogicSample> {
    public abstract LogicSample ground(LogicSample sample, Template template);
}

