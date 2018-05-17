package constructs;

import constructs.example.ValuedFact;

import java.util.List;
import java.util.logging.Logger;

public class Conjunction {
    private static final Logger LOG = Logger.getLogger(Conjunction.class.getName());

    public List<ValuedFact> facts;

    public Conjunction(List<ValuedFact> conjunction) {
        facts = conjunction;
    }
}
