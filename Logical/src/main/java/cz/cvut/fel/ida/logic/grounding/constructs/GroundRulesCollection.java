package cz.cvut.fel.ida.logic.grounding.constructs;

import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.setup.Settings;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Just a switch between unique and non-unique rule body groundings
 */
public abstract class GroundRulesCollection {

    public static GroundRulesCollection get(Settings settings) {
        if (settings.uniqueGroundingsOnly) {
            return new GroundRulesCollectionUnique();
        } else {
            return new GroundRulesCollectionDuplicit();    //sometimes we might wish duplicit ground bodies to be aggregated (e.g. to get SUM of repeated elements)
        }
    }

    public static Collection<GroundRule> getGroundingCollection(WeightedRule weightedRule) {
        if (weightedRule.allowDuplicitGroundings) {
            return new LinkedList<>();
        } else {
            return new LinkedHashSet<>();
        }
    }

    public abstract Collection<GroundRule> getGroundingCollection();

    private static class GroundRulesCollectionDuplicit extends GroundRulesCollection {

        @Override
        public Collection<GroundRule> getGroundingCollection() {
            return new LinkedList<>();
        }

    }


    private static class GroundRulesCollectionUnique extends GroundRulesCollection {

        @Override
        public Collection<GroundRule> getGroundingCollection() {
            return new LinkedHashSet<>();
        }
    }

}