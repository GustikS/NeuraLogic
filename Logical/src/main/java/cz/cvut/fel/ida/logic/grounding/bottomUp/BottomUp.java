package cz.cvut.fel.ida.logic.grounding.bottomUp;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
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
import cz.cvut.fel.ida.logic.subsumption.SpecialBinaryPredicates;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Gusta on 06.10.2016.
 * <p>
 * TODO create a streaming version for single examples?
 * todo add the version with continual rule creation instead of final substitutions
 */
public class BottomUp extends Grounder {
    private static final Logger LOG = Logger.getLogger(BottomUp.class.getName());

    int totalRules = 0;
    long totalGroundRules = 0;

    public BottomUp(Settings settings) {
        super(settings);
    }

    /**
     * Searching for all valid groundings of all the rules in the current template & example
     *
     * @param example
     * @param template
     * @return
     */
    @NotNull
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        timing.tic();
        if (template.herbrandModel == null) {    // first run with skipped template inference (in non-standard pipelines)
            template.preprocessInference(settings.preprocessTemplateInference);
        }

        if (template.clause == null) {
            template.herbrandModel.addFactsWithoutAdditions(template.getAllAtoms(settings.preprocessTemplateInference));
            template.clause = template.herbrandModel.setupClause();
            template.clauseE = template.herbrandModel.getClauseE();
        } else {
            template.herbrandModel.syncWithCache();
        }

        HerbrandModel herbrandModel = template.herbrandModel;
        herbrandModel.setClause(template.clause.copy());
        herbrandModel.setClauseE(template.clauseE.copy());

        final List<HornClause> exampleRules = example.getRules();
        herbrandModel.addRules(exampleRules);
        herbrandModel.addFacts(getAllFacts(example));

        example.clause = herbrandModel.updateClause();  // storing the efficient ClauseE structure of the original example for potential reuse
        example.clauseE = herbrandModel.getClauseE();

        if (template.atomMapCache == null) {     //also the case for a template loaded from a stored model, where the caches are not kept
            template.atomMapCache = templateFacts(template);
            template.factNeuronCache = new Object2ObjectOpenHashMap<>(0);
            template.createdFactNeuronCache = new ObjectArrayList<>(0);
        }

        Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> rulesAndFacts = rulesAndFacts(example, template);
        Map<HornClause, List<WeightedRule>> ruleMap = rulesAndFacts.r;
        Map<Literal, ValuedFact> atomMap = rulesAndFacts.s;

        LinkedHashMap<Literal, LinkedHashMap<GroundHeadRule, Collection<GroundRule>>> groundRules = new LinkedHashMap<>();  //todo test optimize access by further aggregating literals with the same predicate for subsumption testing?

        LOG.fine("Infering Herbrand model...");
        herbrandModel.inferAtoms();

        Map<Literal, Literal> allLiterals = herbrandModel.toIdentityMap();
        LOG.fine("...HerbrandModel inferred with " + allLiterals.size() + " facts");

        LOG.fine("Grounding of " + ruleMap.size() + " rules...");
        totalRules += ruleMap.size();

        for (Map.Entry<HornClause, List<WeightedRule>> ruleEntry : ruleMap.entrySet()) {

            Map<Literal, ValuedFact> embeddings = checkForEmbeddings(ruleEntry, herbrandModel);  //if the rule is merely an embedding
            if (embeddings != null) {
                atomMap.putAll(embeddings); // add the ground embedding facts (from the head atom)
                template.facts.addAll(embeddings.values());     //add them directly to the template facts as they carry learnable weights!
                continue;
            }

            final boolean hasEvalPredicates = hasEvalPredicates(ruleEntry.getKey());
            Pair<Term[], List<Term[]>> groundingSubstitutions = herbrandModel.groundingSubstitutions(ruleEntry.getKey());
            for (WeightedRule weightedRule : ruleEntry.getValue()) {
                List<GroundRule> groundings = groundRules(weightedRule, groundingSubstitutions);
                final boolean splittable = weightedRule.getAggregationFcn() != null && weightedRule.getAggregationFcn().isSplittable();

                for (GroundRule grounding : groundings) {
                    grounding.internLiterals(allLiterals);

                    if (grounding.groundBody.length == 0) {
                        storeRuleAsFact(atomMap, weightedRule, grounding);
                        continue;   // if there are no literals in the body left, turn the rule into a mere fact
                    }

                    if (hasEvalPredicates) {
                        createEvalFacts(grounding, atomMap);
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
        GroundTemplate groundTemplate = new GroundTemplate(groundRules, atomMap, template.atomMapCache);
        groundTemplate.templateFactNeurons = template.factNeuronCache;
        groundTemplate.createdFactNeuronCache = template.createdFactNeuronCache;

        herbrandModel.removeRules(exampleRules);
        herbrandModel.removeAllAtoms();

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

        template.herbrandModel.addFacts(memory.templateFacts.keySet());
        template.herbrandModel.addFacts(memory.groundFacts.keySet());   //add what was known before
        template.herbrandModel.addFacts(memory.derivedGroundFacts);  //also add what has been previously derived!
        GroundTemplate bigger = groundRulesAndFacts(example, template);
        GroundTemplate diff = bigger.diffAgainst(memory);
        memory.groundRules = bigger.groundRules;
        memory.groundFacts = bigger.groundFacts;
        memory.derivedGroundFacts = bigger.derivedGroundFacts;
        memory.templateFactNeurons = bigger.templateFactNeurons;
        memory.createdFactNeuronCache = bigger.createdFactNeuronCache;
        return diff;
    }


    public List<GroundRule> groundRules(HerbrandModel herbrandModel, WeightedRule liftedRule) {
        return groundRules(herbrandModel, liftedRule, liftedRule.toHornClause());
    }

    private Map<Literal, ValuedFact> checkForEmbeddings(Map.Entry<HornClause, List<WeightedRule>> clauseListEntry, HerbrandModel herbrandModel) {
        WeightedRule weightedRule = clauseListEntry.getValue().get(0);
        HeadAtom head = weightedRule.getHead();
        Map<Literal, ValuedFact> embeddings = null;
        if ((head.getPredicate().flags & 0x01) != 0) {
            if (head.getPredicate().name.startsWith("embed")) {

                if (clauseListEntry.getValue().size() > 1) {
                    LOG.severe("There is more than one definition of the same embedding logic in the template...");
                }

                //1) get the groundings
                Clause query = new Clause(head.literal);    //query only the head atom (the embedding predicate)
                Pair<Term[], List<Term[]>> substitutions = herbrandModel.groundingSubstitutions(query);

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

    private boolean hasEvalPredicates(HornClause clause) {
        for (Literal l : clause.body().literals()) {
            if ((l.predicate().flags & 0x04) != 0) {
                return true;
            }
        }

        return false;
    }

    private void createEvalFacts(GroundRule grounding, Map<Literal, ValuedFact> atomMap) {
        final int len = grounding.groundBody.length;
        for (int i = 0; i < len; i++) {
            final Literal lit = grounding.groundBody[i];
            final Predicate pred = lit.predicate();

            if ((pred.flags & 0x04) == 0) {
                continue;
            }

            atomMap.computeIfAbsent(lit, (l) -> {
                final String name = l.toString();
                final Predicate predicate = l.predicate();

                final Term[] terms = l.arguments();
                final Constant a = (Constant) terms[0];
                final Constant b = (Constant) terms[1];
                final double result = SpecialBinaryPredicates.getEvalValue(predicate.name, a.doubleValue(), b.doubleValue());

                Weight weight = weightFactory.construct(name, new ScalarValue(result) ,true, true);
                ValuedFact vf = new ValuedFact(new WeightedPredicate(predicate, null), l.termList(), false, weight);
                vf.originalString = name;

                return vf;
            });
        }
    }


    /**
     * Storing rules with empy bodies as facts
     *
     * @param atomMap
     * @param weightedRule
     * @param grounding
     */
    private void storeRuleAsFact(Map<Literal, ValuedFact> atomMap, WeightedRule weightedRule, GroundRule grounding) {
        Weight weight = null;
        if (weightedRule.getWeight() != Weight.unitWeight) {
            weight = weightedRule.getWeight();
        } else if (weightedRule.getHead().getOffset() != null) {
            weight = weightedRule.getHead().getOffset();
        }
        ValuedFact valuedFact = new ValuedFact(weightedRule.getHead().offsettedPredicate, grounding.groundHead.termList(), false, weight);
        valuedFact.originalString = grounding.groundHead.toString();
        atomMap.put(grounding.groundHead, valuedFact);
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
        Pair<Term[], List<Term[]>> substitutions = herbrandModel.groundingSubstitutions(new Clause(hc.getLiterals()));
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