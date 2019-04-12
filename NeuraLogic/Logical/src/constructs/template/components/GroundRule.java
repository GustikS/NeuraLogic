package constructs.template.components;

import ida.ilp.logic.Literal;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class GroundRule extends GroundHeadRule {
    private static final Logger LOG = Logger.getLogger(GroundRule.class.getName());

    public Literal[] groundBody;

    public GroundRule(WeightedRule weightedRule, Literal groundHead, Literal[] groundBody) {
        super(weightedRule, groundHead);
        this.groundBody = groundBody;
    }

    @Override
    public String toFullString() {
        return "(" + groundHead.toString() + ":-" + groundBody.toString() + ") -> " + weightedRule.getOriginalString();
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

    public void internLiterals(Map<Literal, Literal> herbrand) {
        super.internLiterals(herbrand);
        for (int i = 0; i < groundBody.length; i++) {
            groundBody[i] = herbrand.get(groundBody[i]);
        }
    }
}
