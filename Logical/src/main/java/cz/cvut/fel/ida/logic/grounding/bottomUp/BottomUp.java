package cz.cvut.fel.ida.logic.grounding.bottomUp;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.HornClause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.Template;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundHeadRule;
import cz.cvut.fel.ida.logic.constructs.template.components.GroundRule;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.Grounder;
import cz.cvut.fel.ida.logic.grounding.constructs.GroundRulesCollection;
import cz.cvut.fel.ida.logic.subsumption.HerbrandModel;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 06.10.2016.
 * <p>
 * TODO create a streaming version for single examples?
 * todo add the version with continual rule creation instead of final substitutions
 */
public class BottomUp extends Grounder {
    private static final Logger LOG = Logger.getLogger(BottomUp.class.getName());

    transient HerbrandModel herbrandModel;

    long herbrandCumSize = 0;
    int totalRules = 0;
    long totalGroundRules = 0;

    public BottomUp(Settings settings) {
        super(settings);
        herbrandModel = new HerbrandModel();
    }

    @NotNull
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        timing.tic();

        Map<HornClause, List<WeightedRule>> ruleMap = template.hornClauses;
        Map<Literal, ValuedFact> groundFacts = null;
        if (example.rules.isEmpty() && ruleMap != null) {  //no new rules here, only facts, reuse the rules mapping from template
            groundFacts = mapToLogic(rulesAndFacts(example, template).s);
        } else {
            Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(rulesAndFacts(example, template));
            ruleMap = rulesAndFacts.r;
            groundFacts = rulesAndFacts.s;
            template.hornClauses = ruleMap;
        }

        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules = new LinkedHashMap<>();  //todo test optimize access by further aggregating literals with the same predicate for subsumption testing?

        Set<Literal> facts = groundFacts.keySet();
        // add already inferred facts as a hack to speedup the Herbrand model calculation
        if (settings.inferTemplateFacts) {
            Set<Literal> templateAllFacts = template.getAllFacts();
            if (templateAllFacts != null) {
                facts = new HashSet<>(facts);
                facts.addAll(templateAllFacts);
            }
        }

        LOG.fine("Infering Herbrand model...");
        Collection<Literal> literals = herbrandModel.inferLiterals(ruleMap.keySet(), facts);
        Map<Literal, Literal> allLiterals = literals.stream().collect(Collectors.toMap(l -> l, l -> l));
        LOG.fine("...HerbrandModel inferred with " + allLiterals.size() + " facts");
        herbrandCumSize += allLiterals.size();

        LOG.fine("Grounding of " + ruleMap.size() + " rules...");
        totalRules += ruleMap.size();
        for (Map.Entry<HornClause, List<WeightedRule>> ruleEntry : ruleMap.entrySet()) {

            Map<Literal, ValuedFact> embeddings = checkIfEmbedding(ruleEntry, herbrandModel);  //if the rule is merely an embedding
            if (embeddings != null) {
                groundFacts.putAll(embeddings); // add the ground embedding facts (from the head atom)
                template.facts.addAll(embeddings.values());
                template.getAllFacts().addAll(embeddings.keySet());
                continue;
            }

            Pair<Term[], List<Term[]>> groundingSubstitutions = herbrandModel.groundingSubstitutions(ruleEntry.getKey());
            for (WeightedRule weightedRule : ruleEntry.getValue()) {
                List<GroundRule> groundings = groundRules(weightedRule, groundingSubstitutions);
                final boolean splittable = weightedRule.getAggregationFcn() != null && weightedRule.getAggregationFcn().isSplittable();

                for (GroundRule grounding : groundings) {
                    grounding.internLiterals(allLiterals);

                    if (grounding.groundBody.length == 0) {
                        Weight weight = null;
                        if (weightedRule.getWeight() != Weight.unitWeight) {
                            weight = weightedRule.getWeight();
                        } else if (weightedRule.getHead().getOffset() != null){
                            weight = weightedRule.getHead().getOffset();
                        }
                        ValuedFact valuedFact = new ValuedFact(weightedRule.getHead().offsettedPredicate, grounding.groundHead.termList(), false, weight);
                        valuedFact.originalString = grounding.groundHead.toString();
                        groundFacts.put(grounding.groundHead, valuedFact);
                        continue;   // if there are no literals in the body left, turn the rule into a mere fact
                    }

                    storeGrounding(groundRules, grounding, grounding.groundHead);

                    if (splittable) { // if this rule has a special "splittable" aggregation
                        Literal maskedHead = grounding.groundHead.maskTerms(weightedRule.getAggregationFcn().aggregableTerms()); // mask out the head w.r.t. aggregableTerms
                        storeGrounding(groundRules, grounding, maskedHead); // and store the masked version, too
                    }
                }
            }
        }
        LOG.fine(groundRules.size() + " ground rules created.");
        totalGroundRules += groundRules.size();
        GroundTemplate groundTemplate = new GroundTemplate(groundRules, groundFacts);
        herbrandModel.herbrand.clear();

        timing.toc();
        return groundTemplate;
    }

    /**
     * Performs incremental grounding over the memory GroundTemplate.
     * Returns GroundTemplate that carries diff w.r.t. ground rules and facts but union w.r.t. neurons.
     * Updates the memory with the union of ground rules and facts.
     *
     * @param example
     * @param template
     * @param memory
     * @return
     */
    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory) {
        if (memory == null) {
            memory = new GroundTemplate();
        }
        herbrandModel.storeFacts(memory.groundFacts.keySet());    //add what was known before
        herbrandModel.storeFacts(memory.derivedGroundFacts);  //also add what has been previously derived!
        GroundTemplate bigger = groundRulesAndFacts(example, template);
        GroundTemplate diff = bigger.diffAgainst(memory);
        memory.groundRules = bigger.groundRules;
        memory.groundFacts = bigger.groundFacts;
        memory.derivedGroundFacts = bigger.derivedGroundFacts;
        return diff;
    }


    public List<GroundRule> groundRules(HerbrandModel herbrandModel, WeightedRule liftedRule) {
        return groundRules(herbrandModel, liftedRule, liftedRule.toHornClause());
    }

    private Map<Literal, ValuedFact> checkIfEmbedding(Map.Entry<HornClause, List<WeightedRule>> clauseListEntry, HerbrandModel herbrandModel) {
        WeightedRule weightedRule = clauseListEntry.getValue().get(0);
        HeadAtom head = weightedRule.getHead();
        Map<Literal, ValuedFact> embeddings = null;
        if (head.getPredicate().special) {
            if (head.getPredicate().name.startsWith("embed")) {

                if (clauseListEntry.getValue().size() > 1) {
                    LOG.severe("There is more than one definition of the same embedding logic in the template...");
                }

                //1) get the groundings
                Clause query = new Clause(head.literal);    //query only the head atom (the embedding predicate)
                cz.cvut.fel.ida.utils.generic.tuples.Pair<Term[], List<Term[]>> listPair = herbrandModel.matching.allSubstitutions(query, 0, Integer.MAX_VALUE);

                Term[] variables = listPair.r;
                for (int i = 0; i < variables.length; i++) {
                    variables[i].setIndexWithinSubstitution(i);
                }
                Pair<Term[], List<Term[]>> substitutions = new Pair<>(variables, listPair.s);


                //2) create an embedding for each
                embeddings = new HashMap<>();

                for (int i = 0; i < substitutions.s.size(); i++) {   //for all ground embeddings
                    Term[] terms = substitutions.s.get(i);
                    Literal groundHead = head.literal.subsCopy(terms);
                    Weight weight = weightFactory.construct("embed_" + weightedRule.getWeight().name + "-" + i, weightedRule.getWeight().value.getForm(), false, false);
                    ValuedFact embedding = new ValuedFact(head.offsettedPredicate, groundHead.termList(), false, weight);
                    embeddings.put(groundHead, embedding);
                }
            }
        }
        return embeddings;
    }

    /**
     * We cannot simply merge logically identical body literals in a ground rule's body, nor can we merge two permuted bodies.
     * These merging transformations can only be done depending on the properties of the used activation/aggregation functions,
     * and so it is taken care of later in {NeuralNetsBuilder} with {networks.structure.transforming.ParallelEdgeMerger}.
     *
     * @param liftedRule
     * @param hc
     * @return
     */
    public List<GroundRule> groundRules(HerbrandModel herbrandModel, WeightedRule liftedRule, HornClause hc) {
        Pair<Term[], List<Term[]>> substitutions = herbrandModel.groundingSubstitutions(hc);
        return groundRules(liftedRule, substitutions);
    }

    public List<GroundRule> groundRules(WeightedRule liftedRule, Pair<Term[], List<Term[]>> substitutions) {
        List<GroundRule> groundRules = new ArrayList<>();
        for (int i = 0; i < substitutions.s.size(); i++) {
            GroundRule groundRule = liftedRule.groundRule(substitutions.s.get(i));
            groundRules.add(groundRule);
        }
        return groundRules;
    }

    /**
     * Get the path to this "grounding" through the "groundRules" structure and store it.
     * Initialize on the way, if necessary.
     *
     * @param groundRules
     * @param grounding
     * @param groundHead
     */
    private void storeGrounding(LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules, GroundRule grounding, Literal groundHead) {
        GroundHeadRule groundHeadRule = grounding.weightedRule.groundHeadRule(groundHead);  // get groundHeadRule for this grounding and weightedRule (i.e. remove the body from this grounding)
        Map<GroundHeadRule, Collection<GroundRule>> groundHeadRules2groundings = groundRules.computeIfAbsent(groundHead, k -> new LinkedHashMap<>());  // get all the GroundHeadRules with the same head as this one AND initialize new map if necessary (hence 2 steps)
        Collection<GroundRule> ruleGroundings = groundHeadRules2groundings.computeIfAbsent(groundHeadRule, k -> GroundRulesCollection.getGroundingCollection(grounding.weightedRule));  // now get the list of groundings for this groundHeadRule only. Here we also choose whether we want only unique ground bodies or not
        ruleGroundings.add(grounding);  // finally, add this particular grounding to the right place
    }
}