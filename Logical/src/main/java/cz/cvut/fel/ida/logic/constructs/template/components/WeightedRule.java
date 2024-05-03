package cz.cvut.fel.ida.logic.constructs.template.components;

import cz.cvut.fel.ida.algebra.functions.Aggregation;
import cz.cvut.fel.ida.algebra.functions.Combination;
import cz.cvut.fel.ida.algebra.functions.Transformation;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.HornClause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.constructs.template.metadata.RuleMetadata;
import cz.cvut.fel.ida.utils.exporting.Exportable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 04.10.2016.
 * <p>
 */
public class WeightedRule implements Exportable {

    /**
     * changable by structure learning?
     */
    boolean isEditable = false;

    private Weight weight;
    private Weight offset;

    private HeadAtom head;
    private List<BodyAtom> body;

    private Combination combinationFcn;
    private Transformation transformationFcn;
    private Aggregation aggregationFcn;

    public boolean allowDuplicitGroundings = false;

    private RuleMetadata metadata;
    private String originalString;

    private int hashCode = -1;

    public WeightedRule() {

    }

    /**
     * This does not really clone the rule, only references
     *
     * @param other
     */
    public WeightedRule(WeightedRule other) {
        this.setWeight(other.getWeight());
        this.setHead(other.getHead());
        this.setBody(new ArrayList<>(other.getBody().size()));
        this.getBody().addAll(other.getBody());
        this.setOffset(other.getOffset());
        this.setAggregationFcn(other.getAggregationFcn());
        this.setTransformation(other.getTransformation());
        this.setCombination(other.getCombination());
        this.setMetadata(other.getMetadata());
        this.setOriginalString(other.getOriginalString());
        this.isEditable = other.isEditable;
    }

    public HornClause toHornClause() {
        List<Literal> collected = getBody().stream().map(bodyLit -> bodyLit.getLiteral()).collect(Collectors.toList());
        return new HornClause(getHead().getLiteral(), new Clause(collected));
    }

    /**
     * Grounding of individual atoms will create new copies of them.
     *
     * @param terms
     * @return
     */
    public GroundRule groundRule(Term[] terms) {

        Literal groundHead = head.literal.subsCopy(terms);

        int size = getBody().size();
        List<Literal> groundBody = new ArrayList<>(size);

        for (BodyAtom atom : getBody()) {
            Literal literal = atom.literal;
            if (!literal.predicate().hidden && !literal.isNegated()) {     //remove special and purely logical (hidden) predicates from the grounded bodies!
                groundBody.add(literal.subsCopy(terms));
            } else {
//                System.out.println("removing special body literal");
            }
        }

        GroundRule groundRule = new GroundRule(this, groundHead, groundBody.toArray(new Literal[groundBody.size()]));

        return groundRule;
    }

    public GroundHeadRule groundHeadRule(Literal groundHead) {
        GroundHeadRule groundRule = new GroundHeadRule(this, groundHead);
        return groundRule;
    }

    public String signatureString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHead().getPredicate()).append(":-");
        for (BodyAtom bodyAtom : getBody()) {
            sb.append(bodyAtom.getPredicate()).append(",");
        }
        return sb.toString();
    }

    public boolean detectWeights() {
        boolean hasWeights = hasOffset();
        for (BodyAtom bodyAtom : getBody()) {
            if (bodyAtom.weight != null) {
                hasWeights = true;
            }
        }
        if (hasWeights) {
            for (BodyAtom bodyAtom : getBody()) {
                if (bodyAtom.weight == null) {
                    bodyAtom.weight = Weight.unitWeight;
                }
            }
        }
        return hasWeights;
    }

    @Override
    public int hashCode() {
        if (hashCode != -1)
            return hashCode;
        hashCode = head.hashCode() + body.hashCode();
        return hashCode;
    }

    public void setHashCode(int hashCode){
        this.hashCode = hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {   //this should catch absolute majority of all calls (due to factory creation and unique hash)
            return true;
        }
        if (!(obj instanceof WeightedRule)) {
            return false;
        }
        WeightedRule other = (WeightedRule) obj;
        if (getWeight() == null && other.getWeight() != null || getWeight() != null && other.getWeight() == null) {
            return false;
        }
        if (getOffset() == null && other.getOffset() != null || getOffset() != null && other.getOffset() == null) {
            return false;
        }
        if (getWeight() != null && !getWeight().equals(other.getWeight()) || getOffset() != null && !getOffset().equals(other.getOffset())) {
            return false;
        }
        if (getAggregationFcn() != null && !getAggregationFcn().equals(other.getAggregationFcn()) || getTransformation() != null && !getTransformation().equals(other.getTransformation()) || getCombination() != null && !getCombination().equals(other.getCombination())) {
            return false;
        }
        if (!getHead().equals(other.getHead()) || !getBody().equals(other.getBody())) {
            return false;
        }
        return true;
    }

    public String toRuleNeuronString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHead().toString()).append(":-");
        for (BodyAtom bodyAtom : getBody()) {
            sb.append(bodyAtom.toString()).append(",");
        }
        sb.setCharAt(sb.length() - 1, '.');
        return sb.toString();
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public boolean hasOffset() {
        return offset != null;
    }

    public Weight getOffset() {
        return offset;
    }

    public void setOffset(Weight offset) {
        this.offset = offset;
    }

    public HeadAtom getHead() {
        return head;
    }

    public void setHead(HeadAtom head) {
        this.head = head;
    }

    public List<BodyAtom> getBody() {
        return body;
    }

    public void setBody(List<BodyAtom> body) {
        this.body = body;
    }

    public Aggregation getAggregationFcn() {
        return aggregationFcn;
    }

    public void setAggregationFcn(Aggregation aggregationFcn) {
        this.aggregationFcn = aggregationFcn;
    }

    public Transformation getTransformation() {
        return transformationFcn;
    }

    public void setTransformation(Transformation transformation) {
        this.transformationFcn = transformation;
    }

    public Combination getCombination() {
        return combinationFcn;
    }

    public void setCombination(Combination combinationFcn) {
        this.combinationFcn = combinationFcn;
    }

    public RuleMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(RuleMetadata metadata) {
        this.metadata = metadata;
    }

    public String getOriginalString() {
        return originalString;
    }

    public void setOriginalString(String originalString) {
        this.originalString = originalString;
    }

}