package constructs.example;


import constructs.template.BodyAtom;
import learning.Example;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Gusta on 06.10.2016.
 */
public class GroundExample implements Example {
    Set<WeightedFact> facts;

    public GroundExample(List<BodyAtom> body) {
        facts = new LinkedHashSet<>();
        for (BodyAtom bodyAtom : body) {
            WeightedFact weightedFact = new WeightedFact();
            weightedFact.weightedPredicate = bodyAtom.weightedPredicate;
            weightedFact.literal = bodyAtom.literal;
            weightedFact.value = bodyAtom.weight;
            weightedFact.isNegated = bodyAtom.isNegated;
            facts.add(weightedFact);
        }
    }

    @Override
    public String getId() {
        return null;
    }
}
