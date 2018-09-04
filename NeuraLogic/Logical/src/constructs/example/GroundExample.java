package constructs.example;


import constructs.Conjunction;
import grounding.GroundTemplate;
import learning.Example;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ground example is simply a collection of facts, potentially separated into conjunctions
 */
public class GroundExample implements Example {
    public LinkedHashSet<Conjunction> conjunctions;

    public LinkedHashSet<ValuedFact> flatFacts;

    public GroundExample(){}

    public GroundExample(List<Conjunction> body) {
        conjunctions = new LinkedHashSet<>();
        conjunctions.addAll(body);
        flatFacts = new LinkedHashSet<>();
        flatFacts.addAll(body.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toList()));
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Integer getSize() {
        return null;
    }
}
