package cz.cvut.fel.ida.logic.constructs.template.components;

import cz.cvut.fel.ida.logic.Literal;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class GroundRule extends GroundHeadRule {
    private static final Logger LOG = Logger.getLogger(GroundRule.class.getName());

    /**
     * It's an array, so there can be duplicates!
     */
    public Literal[] groundBody;    //todo now add check for uniqueness in both lifted and ground rules (unique by set of body literals)

    public GroundRule(WeightedRule weightedRule, Literal groundHead, Literal[] groundBody) {
        super(weightedRule, groundHead);
        this.groundBody = groundBody;
    }

    @Override
    public String toFullString() {
        return "(" + groundHead.toString() + ":-" + Arrays.toString(groundBody) + ") -> " + weightedRule.getOriginalString();
    }

    @Override
    public String toString() {
        return groundHead.toString() + ":-" + Arrays.toString(groundBody);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroundRule that = (GroundRule) o;
        return Objects.equals(weightedRule, that.weightedRule) &&
                Objects.equals(groundHead, that.groundHead) &&
                Arrays.equals(groundBody, that.groundBody);
    }

    @Override
    public int hashCode() {
        if (hashCode != -1)
            return hashCode;
        hashCode = super.hashCode();
        hashCode = 17 * hashCode + Arrays.hashCode(groundBody);
        return hashCode;
    }

    /**
     * Replace literals that have been created via the rule grounding with their original versions from the Herbrand model
     * inferred before (saves some memory if the examples are retained, these literals can still be immediately GC'ed)
     * @param herbrand
     */
    public void internLiterals(Map<Literal, Literal> herbrand) {
        super.internLiterals(herbrand);
        for (int i = 0; i < groundBody.length; i++) {
            groundBody[i] = herbrand.get(groundBody[i]);
        }
    }
}
