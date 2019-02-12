package grounding.bottomUp;

import constructs.template.components.WeightedRule;
import ida.ilp.logic.*;
import ida.ilp.logic.subsumption.CustomPredicate;
import ida.ilp.logic.subsumption.Matching;
import ida.ilp.logic.subsumption.SolutionConsumer;
import ida.utils.Sugar;
import ida.utils.VectorUtils;
import ida.utils.collections.MultiMap;
import ida.utils.tuples.Pair;

import java.util.*;
import java.util.logging.Logger;

/**
 * Least Herbrand model from purely logical world (taken from Ondra)
 */
public class HerbrandModel {
    private static final Logger LOG = Logger.getLogger(HerbrandModel.class.getName());

    /**
     * map predicates to ground literal Sets
     */
    MultiMap<Predicate, Literal> herbrand;
    Matching matching;

    public HerbrandModel() {
        herbrand = new MultiMap<>();
    }

    public HerbrandModel(MultiMap<Predicate, Literal> init) {
        herbrand = init;
    }


    public HerbrandModel(Collection<Literal> facts) {
        herbrand = new MultiMap<>();
        populateHerbrand(facts);
    }


    /**
     *
     * @param clauses - may contain facts and rules
     * @return
     */
    public MultiMap<Predicate, Literal> inferModel(Collection<? extends Clause> clauses) {
        Pair<List<HornClause>, List<Literal>> rulesAndFacts = rulesAndFacts(clauses);
        return inferModel(rulesAndFacts.r, rulesAndFacts.s);
    }


    /**
     * todo add version with constraints at input
     *
     * @param irules
     * @param facts
     * @return
     */
    public MultiMap<Predicate, Literal> inferModel(Collection<HornClause> irules, Collection<Literal> facts) {
        populateHerbrand(facts);

        LinkedHashSet<HornClause> rules = new LinkedHashSet<>(irules); //for removing
        //rule heads map to empty sets at the beginning
        Set<Predicate> headSignatures = new LinkedHashSet<>();  //for faster iteration
        for (HornClause rule : rules) {
            headSignatures.add(rule.head().predicate());
            herbrand.set(rule.head().predicate(), new HashSet<>());
        }

        boolean changed;
        do {
            int herbrandSize0 = VectorUtils.sum(herbrand.sizes());
            LOG.fine("herbrand size before round: " + herbrandSize0);
            //get all valid literals from current herbrand in to matching
            matching = new Matching(Sugar.<Clause>list(new Clause(Sugar.flatten(herbrand.values())))); //todo somehow change this to incrementally pass only NEW facts (ClauseE) to existing Matching object for speedup? Try version without example indexing
            LOG.finer("Matching created.");
            for (Predicate predicate : headSignatures) {
                //may overwrite the previous ones which is actually what we want (?)
                matching.getEngine().addCustomPredicate(new TupleNotIn(predicate, herbrand.get(predicate))); //predicate that evaluates to true if the head-mapping set does not containt such a literal yet
            }
            for (Iterator<? extends HornClause> iterator = rules.iterator(); iterator.hasNext(); ) {
                HornClause rule = iterator.next();
                Literal head = rule.head();
                // solution consumer = automatically add all found valid substitutions of the head literal into the herbrand map
                SolutionConsumer solutionConsumer = new PredicateSolutionConsumer(head, herbrand.get(head.predicate()));
                matching.getEngine().addSolutionConsumer(solutionConsumer);
                // if the rule head is already ground
                if (LogicUtils.isGround(head)) {
                    Clause query = new Clause(LogicUtils.flipSigns(rule.body().literals()));
                    // add the head to herbrand if the rule body is true
                    if (matching.subsumption(query, 0)) {
                        herbrand.put(head.predicate(), head);
                        iterator.remove(); // if so, do not ever try this ground rule again
                    }
                } else {
                    //if it is not ground, extend the rule with restriction that the head substitution solution must not be contained in the herbrand yet (for speedup instead of just adding them repetitively to the set)
                    Clause query = new Clause(LogicUtils.flipSigns(Sugar.union(rule.getLiterals(), new Literal(tupleNotInPredicateName(head.predicate()), true, head.arguments()))));
                    matching.allSubstitutions(query, 0, Integer.MAX_VALUE); //then find (and through consumer add to herbrand) all NEW substitutions for the head literal - todo add version where these substitutions will be iteratively saved into some hashmap instead of repeating final substitutions
                }
                matching.getEngine().removeSolutionConsumer(solutionConsumer); //the found substitutions should be applied only to the head of the currently solved rule
            }
            LOG.finer(rules.size() + " rules grounded.");
            int herbrandSize1 = VectorUtils.sum(herbrand.sizes());
            LOG.fine("herbrand size after round: " + herbrandSize1);
            changed = herbrandSize1 > herbrandSize0;
        } while (changed);
        return herbrand;
    }

    public Pair<Term[], List<Term[]>> groundingSubstitutions(HornClause hornClause) {
        return matching.allSubstitutions(hornClause.toClause(), 0, Integer.MAX_VALUE);  //todo check negations here
    }

    public List<WeightedRule> groundRules(WeightedRule liftedRule) {
        return groundRules(liftedRule, liftedRule.toHornClause());
    }

    /**
     * todo must kill permutations of body literals here - return only truly unique ground rules?
     *  - but careful for conjunction weights - aggregate them? must not be shared - same problem in Grounder merging different WeightedRules with same hornClauses
     * @param liftedRule
     * @param hc
     * @return
     */
    public List<WeightedRule> groundRules(WeightedRule liftedRule, HornClause hc) {
        List<WeightedRule> weightedRules = new ArrayList<>();
        Pair<Term[], List<Term[]>> substitutions = groundingSubstitutions(hc);
        for (int i = 0; i < substitutions.s.size(); i++) {
            weightedRules.add(liftedRule.ground(substitutions.r, substitutions.s.get(i)));
        }
        return weightedRules;
    }

    /**
     * add all existing unit ground literals (facts) to herbrand set
     *
     * @param facts
     */
    protected void populateHerbrand(Collection<Literal> facts) {
        for (Literal groundLiteral : facts) {
            herbrand.put(new Predicate(groundLiteral.predicateName(), groundLiteral.arity()), groundLiteral);
        }
    }

    /**
     * Split given clauses into ground facts and rules, filter out the rest
     *
     * @param clauses
     * @return
     */
    private Pair<List<HornClause>, List<Literal>> rulesAndFacts(Collection<? extends Clause> clauses) {
        List<Literal> groundFacts = new ArrayList<>();
        List<HornClause> rest = new ArrayList<>();
        for (Clause c : clauses) {
            if (c.countLiterals() == 1 && LogicUtils.isGround(c)) {
                groundFacts.add(Sugar.chooseOne(c.literals()));
            } else {
                HornClause hc = new HornClause(c);
                if (hc.body() != null)
                    rest.add(hc);
            }
        }
        return new Pair<>(rest, groundFacts);
    }

    /**
     * Name of the special predicate for binding within the substitution engine
     *
     * @param predicate
     * @return
     */
    private static String tupleNotInPredicateName(Predicate predicate) {
        return "@tuplenotin-" + predicate.name + "/" + predicate.arity;
    }

    public void clear() {
        herbrand.clear();
    }

    /**
     * Used for adding solutions (found by the engine) to the herbrand map on the fly, so that the engine can prune the rest
     */
    private static class PredicateSolutionConsumer implements SolutionConsumer {

        Literal ruleHead;
        private Set<Literal> headGroundings;

        private PredicateSolutionConsumer(Literal head, Set<Literal> groundHeads) {
            this.ruleHead = head;
            this.headGroundings = groundHeads;
        }

        @Override
        public void solution(Term[] template, Term[] solution) {
            headGroundings.add(LogicUtils.substitute(ruleHead, template, solution));
        }
    }

    /**
     * For a given predicate stores all found substitutions and is only satisfiable for NEW solutions.
     * To be added to the substitution engine for solution pruning.
     */
    private static class TupleNotIn implements CustomPredicate {

        private Set<Literal> literals; //mildly optimize this by storing set of Term[] instead? Probably not

        private String name;

        private String predicate;

        TupleNotIn(Predicate predicate, Set<Literal> literals) {
            this.predicate = predicate.name;
            this.name = tupleNotInPredicateName(predicate);
            this.literals = literals;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean isSatisfiable(Term... arguments) {
            for (Term arg : arguments) {
                if (arg == null) {
                    return true;
                }
            }
            return !literals.contains(new Literal(predicate, arguments));
        }
    }
}
