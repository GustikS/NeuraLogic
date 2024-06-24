package cz.cvut.fel.ida.logic.grounding;

import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;
import cz.cvut.fel.ida.utils.exporting.Exportable;

public class GroundingSample extends LogicSample {

    transient public Template template;
    public Wrap groundingWrap;

    /**
     * Switch between reusing the Wrapped grounding without change, or just in incremental grounding mode
     */
    public boolean groundingComplete;

    public GroundingSample(LogicSample sample, Template template) {
        super(sample.target, sample.query);
        this.type = sample.type;
        this.position = sample.position;
        this.template = template;
        groundingWrap = new Wrap(sample.query.evidence);
    }

    /**
     * Helper subclass for keeping references to existing GroundTemplate that may be shared between different GroundingSamples based on common LiftedExample
     */
    public static class Wrap implements Exportable {

        public Wrap(LiftedExample example) {
            this.setExample(example);
        }

        /**
         * Duplicate reference (to GroundingSample.Query.Evidence) but with synchronized access
         */
        public LiftedExample example;
        /**
         * Memory for Herbrand models
         */
        private GroundTemplate groundTemplate;
        /**
         * Memory for created neurons
         */
        transient private Object neuronMaps;

        public synchronized LiftedExample getExample() {
            return example;
        }

        /**
         * Shared example in this wrap may be accessed by multiple Threads
         *
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

        public void copyFrom(Wrap other) {
            this.example = other.example;
            this.groundTemplate = other.groundTemplate;
            this.neuronMaps = other.neuronMaps;
        }
    }

}