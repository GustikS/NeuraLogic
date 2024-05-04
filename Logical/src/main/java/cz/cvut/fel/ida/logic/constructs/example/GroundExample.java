package cz.cvut.fel.ida.logic.constructs.example;


import cz.cvut.fel.ida.learning.Example;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.grounding.Grounder;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ground example is simply a collection of facts, potentially separated into conjunctions
 */
public class GroundExample implements Example {
    @Nullable
    public LinkedHashSet<Conjunction> conjunctions;

    public LinkedHashSet<ValuedFact> flatFacts;

    String id;

    static int exampleCounter = 0;

    public GroundExample() {
        id = "#" + exampleCounter++;
        conjunctions = new LinkedHashSet<>();
        flatFacts = new LinkedHashSet<>();
    }

    public GroundExample(List<Conjunction> conjunctions) {
        this();
        if (conjunctions.size() > 1) {  // only add conjunctions if there is more than one!
            this.conjunctions.addAll(conjunctions);
        }
        flatFacts.addAll(conjunctions.stream().flatMap(conj -> conj.facts.stream()).collect(Collectors.toList()));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getNeuronCount() {
        return null;
    }

    public void addAllFrom(@Nullable GroundExample evidence) {
        conjunctions.addAll(evidence.conjunctions);
        flatFacts.addAll(evidence.flatFacts);
    }
}
