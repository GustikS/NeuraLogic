package cz.cvut.fel.ida.logic.grounding;

import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;

public class GroundingSample extends LogicSample {

    public Template template;
    public Wrap groundingWrap;

    /**
     * Switch between reusing the Wrapped grounding without change, or just in incremental grounding mode
     */
    public boolean groundingComplete;

    public GroundingSample(LogicSample sample, Template template) {
        super(sample.target, sample.query);
        this.template = template;
        groundingWrap = new Wrap(sample.query.evidence);
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
        /**
         * Memory for Herbrand models
         */
        private GroundTemplate groundTemplate;
        /**
         * Memory for created neurons
         */
        private  Object neuronMaps;

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

        public synchronized GroundTemplate getGroundTemplate() {
            return groundTemplate;
        }

        public synchronized void setGroundTemplate(GroundTemplate groundTemplate) {
            this.groundTemplate = groundTemplate;
        }

        public synchronized Object getNeuronMaps() {
            return neuronMaps;
        }

        public synchronized void setNeuronMaps(Object neuronMaps) {
            this.neuronMaps = neuronMaps;
        }
    }

}