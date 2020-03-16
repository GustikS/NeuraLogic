package cz.cvut.fel.ida.logic.constructs;

//import cz.cvut.fel.ida.ml.constructs.example.ValuedFact;

import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;

import java.util.List;
import java.util.logging.Logger;

public class Conjunction {
    private static final Logger LOG = Logger.getLogger(Conjunction.class.getName());

    public List<ValuedFact> facts;

    public Conjunction(List<ValuedFact> conjunction) {
        facts = conjunction;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < facts.size(); i++) {
            sb.append(facts.get(i).toString()).append(",");
        }
        return sb.substring(0, sb.length()-1);
    }
}
