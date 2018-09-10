package grounding;

import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.example.QueryAtom;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.Example;
import learning.LearningSample;
import learning.Query;
import networks.evaluation.values.Value;

public class GroundingSample extends LogicSample {

    public Template template;
    public Wrap grounding;

    /**
     * Switch between reusing the Wrapped grounding without change, or just in incremental grounding mode
     */
    public boolean groundingComplete;

    public GroundingSample(LogicSample sample, Template template) {
        super(sample.target, sample.query);
        this.template = template;
        grounding = new Wrap(sample.query.evidence);
    }

    /**
     * todo add synchronized access to grounding and example (or remove it)
     */
    public static class Wrap {

        public Wrap(LiftedExample example) {
            this.example = example;
        }

        public LiftedExample example;
        public GroundTemplate grounding;
    }

}