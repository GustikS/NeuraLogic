package grounding;

import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.template.Template;

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
     * Helper subclass for keeping references to existing GroundTemplate that may be shared between different GroundingSamples based on common LiftedExample
     */
    public static class Wrap {

        public Wrap(LiftedExample example) {
            this.setExample(example);
        }

        /**
         * Duplicate reference (to GroundingSample.Query.Evidence) but with synchronized access
         */
        private LiftedExample example;
        private GroundTemplate grounding;

        public synchronized LiftedExample getExample() {
            return example;
        }

        /**
         * Shared example in this wrap may be accessed by multiple Threads
         * @param example
         */
        public synchronized void setExample(LiftedExample example) {
            this.example = example;
        }

        public synchronized GroundTemplate getGrounding() {
            return grounding;
        }

        public synchronized void setGrounding(GroundTemplate grounding) {
            this.grounding = grounding;
        }
    }

}