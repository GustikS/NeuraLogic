package constructs.example;

import com.sun.istack.internal.Nullable;
import constructs.Conjunction;
import constructs.template.components.WeightedRule;

import java.util.LinkedHashSet;
import java.util.List;

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
    }

    public LiftedExample(List<Conjunction> conjunctions, List<WeightedRule> irules) {
        super(conjunctions);
        rules = new LinkedHashSet<>();
        rules.addAll(irules);

    }

    public void addAllFrom(@Nullable LiftedExample evidence) {
        super.addAllFrom(evidence);
        rules.addAll(evidence.rules);
    }

}