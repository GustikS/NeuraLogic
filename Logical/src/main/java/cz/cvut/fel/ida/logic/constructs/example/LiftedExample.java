package cz.cvut.fel.ida.logic.constructs.example;

import cz.cvut.fel.ida.logic.HornClause;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lifted trainExamples are structures that share common template part through learning (just like the regular trainExamples),
 * but also have extra parts that are unique to them. This decomposes the template into relevant subparts,
 * instead of having one huge template carrying data for all the trainExamples.
 * <p>
 * Created by gusta on 13.3.17.
 */
public class LiftedExample extends GroundExample {
    public LinkedHashSet<WeightedRule> rules;

    public LiftedExample() {
        super();
        rules = new LinkedHashSet<>();
    }

    public LiftedExample(List<Conjunction> conjunctions, List<WeightedRule> irules) {
        super(conjunctions);
        rules = new LinkedHashSet<>();
        rules.addAll(irules);
    }

    public List<HornClause> getRules() {
        return rules.stream().map(WeightedRule::toHornClause).collect(Collectors.toList());
    }

    public void addAllFrom(@Nullable LiftedExample evidence) {
        super.addAllFrom(evidence);
        rules.addAll(evidence.rules);
    }

    @Override
    public String toString() {
        StringBuilder sizes = new StringBuilder("facts:" + flatFacts.size());
        if (!conjunctions.isEmpty() || !rules.isEmpty()) {
            sizes.append(", (in conjunctions:" + conjunctions.size() + ", rules:" + rules.size() + ")");
        }
        return "ex:" + getId() + " [" + sizes + "]";
    }
}