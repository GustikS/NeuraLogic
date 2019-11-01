package constructs.template.components;

import ida.ilp.logic.Literal;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class GroundHeadRule {
    private static final Logger LOG = Logger.getLogger(GroundHeadRule.class.getName());

    public WeightedRule weightedRule;

    public Literal groundHead;

    int hashCode = -1;

    public GroundHeadRule(WeightedRule weightedRule, Literal groundHead) {
        this.weightedRule = weightedRule;
        this.groundHead = groundHead;
    }

    public void internLiterals(Map<Literal, Literal> herbrand) {
        this.groundHead = herbrand.get(groundHead);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroundHeadRule that = (GroundHeadRule) o;
        return Objects.equals(weightedRule, that.weightedRule) &&
                Objects.equals(groundHead, that.groundHead);
    }

    @Override
    public int hashCode() {
        if (hashCode != -1)
            return hashCode;
        hashCode = (weightedRule.hashCode() + 31 * groundHead.hashCode());
        return hashCode;
    }

    public String toFullString() {
        return groundHead.toString() + " := " + weightedRule.getOriginalString();
    }
}
