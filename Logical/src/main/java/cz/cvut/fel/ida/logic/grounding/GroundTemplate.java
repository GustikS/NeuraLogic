package cz.cvut.fel.ida.logic.grounding;

import cz.cvut.fel.ida.learning.Example;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.constructs.template.types.GraphTemplate;
import cz.cvut.fel.ida.logic.grounding.constructs.GroundRulesCollection;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Temporary structure created during grounding for transfer between ground Herbrand model (rules+facts) and a neural network.
 */
public class GroundTemplate extends GraphTemplate implements Example {

    public static int counter = 0;

    /**
     * for outside callback
     */
    public static java.util.function.IntConsumer progressBar;

    /**
     * Temp (for current pair of Template+Example) structure (head -> rules -> ground bodies) for traversing the graph of groundings
     */
    @NotNull
    public LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules;    //todo test optimize access by further aggregating literals with the same predicate for subsumption testing?

    /**
     * Temp (for current pair of Template+Example) set of true ground facts
     */
    @NotNull
    public Map<Literal, ValuedFact> groundFacts;

    /**
     * Inferred ground facts from heads of the inferred ground rules
     */
    public Set<Literal> derivedGroundFacts; //todo next where are these useful?

    /**
     * Linking between logic literals and rules and neurons - can be possibly reused between different GroundTemplates, so it is saved here.
     */
//    public NeuronMaps neuronMaps;   //now move to grounding.GroundingSample for clarity   -> moved
    public GroundTemplate() {
        this.name = "g" + counter++;
        if (progressBar != null) {
            progressBar.accept(counter);
        }
    }

    public GroundTemplate(LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules, Map<Literal, ValuedFact> groundFacts) {
        this();
        this.groundRules = groundRules;
        this.groundFacts = groundFacts;
        this.derivedGroundFacts = getFactsFromGroundRules(groundRules);
//        this.neuronMaps = new NeuronMaps(groundRules, groundFacts);

    }

    public GroundTemplate(GroundTemplate other) {
        this();
        this.groundRules = other.groundRules;
        this.groundFacts = other.groundFacts;
        this.derivedGroundFacts = other.derivedGroundFacts;
//        this.neuronMaps = other.neuronMaps;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public Integer getNeuronCount() {
        return groundRules.size();
    }


    private Set<Literal> getFactsFromGroundRules(LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules) {
        Set<Literal> derivedFacts = new HashSet<>();
        derivedFacts.addAll(groundRules.keySet());
        return derivedFacts;
    }

    /**
     * Returns set difference of this GroundTemplate w.r.t. memory in terms of Rules and Facts,
     * but takes all the previous neurons from memory.
     *
     * @param memory
     * @return
     */
    public GroundTemplate diffAgainst(GroundTemplate memory) {
        GroundTemplate diff = new GroundTemplate();

        //1) copy all ground rules into new diff
        diff.groundRules = new LinkedHashMap<>();
        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> entry : this.groundRules.entrySet()) {
            LinkedHashMap<GroundHeadRule, Collection<GroundRule>> put = diff.groundRules.put(entry.getKey(), new LinkedHashMap<>());
            for (Map.Entry<GroundHeadRule, Collection<GroundRule>> entry2 : entry.getValue().entrySet()) {
                Collection<GroundRule> put1 = put.put(entry2.getKey(), GroundRulesCollection.getGroundingCollection(entry2.getKey().weightedRule));
                put1.addAll(entry2.getValue());
            }
        }
        //2) copy all ground facts into new diff
        diff.groundFacts = new HashMap<>();
        diff.groundFacts.putAll(this.groundFacts);

        //forget repetitive ground rules
        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> entry : memory.groundRules.entrySet()) {
            for (Map.Entry<GroundHeadRule, Collection<GroundRule>> entry2 : entry.getValue().entrySet()) {
                for (GroundRule rule : entry2.getValue()) {
                    //delete pointers to the newly proved rules which are equivalent the the previously proved rules
                    Collection<GroundRule> rules = diff.groundRules.get(entry.getKey()).get(entry2.getKey());
                    rules.remove(rule); //todo test change to factory method which tells if new instead and go back to arraylist instead of LinkedHashSet for the groundings??(will be faster?)
                }
            }
        }
        //also forget newly proved equivalent facts
        diff.groundFacts.keySet().removeAll(memory.groundFacts.keySet());

        //but take all the previously created neurons
//        diff.neuronMaps.addAllFrom(memory.neuronMaps);

        return diff;
    }

    public GroundTemplate prune(QueryAtom queryAtom) {
        GroundTemplate groundTemplate = new GroundTemplate(this);
        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> support = new LinkedHashMap<>();
        Matching matching = new Matching();
        for (Map.Entry<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> entry : groundRules.entrySet()) {

            if (queryAtom.headAtom.literal.predicate().equals(entry.getKey().predicate()) && matching.subsumption(new Clause(queryAtom.headAtom.literal), new Clause(entry.getKey()))) { //todo check this method
                LinkedHashMap<GroundHeadRule, Collection<GroundRule>> ruleMap = support.computeIfAbsent(entry.getKey(), f -> new LinkedHashMap<>());

                for (Map.Entry<GroundHeadRule, Collection<GroundRule>> groundings : entry.getValue().entrySet()) {
                    Collection<GroundRule> weightedRules = ruleMap.computeIfAbsent(groundings.getKey(), f -> new LinkedHashSet<>());

                    for (GroundRule grounding : groundings.getValue()) {
                        weightedRules.add(grounding);
                        recursePrune(grounding, support, new HashSet<>());
                    }
                }
            }
        }
        groundTemplate.groundRules = support;
        return this;
    }

    private void recursePrune(GroundRule grounding, LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> support, Set<Literal> closedList) {
        for (Literal bodyAtom : grounding.groundBody) {
            if (closedList.contains(bodyAtom)) {
                continue;
            }
            closedList.add(bodyAtom);

            LinkedHashMap<GroundHeadRule, Collection<GroundRule>> validRules = groundRules.get(bodyAtom);
            LinkedHashMap<GroundHeadRule, Collection<GroundRule>> nextRules = support.computeIfAbsent(bodyAtom, f -> new LinkedHashMap<>());

            for (Map.Entry<GroundHeadRule, Collection<GroundRule>> validGroundings : validRules.entrySet()) {
                Collection<GroundRule> weightedRules = nextRules.computeIfAbsent(validGroundings.getKey(), f -> new LinkedHashSet<>());

                for (GroundRule nextGrounding : validGroundings.getValue()) {
                    weightedRules.add(nextGrounding);
                    recursePrune(nextGrounding, support, closedList);
                }
            }
        }
    }

}
