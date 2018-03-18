package grounding;

import constructs.template.Template;
import learning.LearningSample;
import pipeline.SampleProcess;

/**
 * Created by Gusta on 06.10.2016.
 */
public abstract class Grounder implements SampleProcess<LearningSample> {
    public abstract LearningSample ground(LearningSample sample, Template template);
}

