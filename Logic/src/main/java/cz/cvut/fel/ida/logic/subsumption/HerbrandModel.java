package cz.cvut.fel.ida.logic.subsumption;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;
import java.util.logging.Logger;

/**
 * Least Herbrand model from purely logical world (building on Ondrej's subsumption solver)
 */
public class HerbrandModel {
    private static final Logger LOG = Logger.getLogger(HerbrandModel.class.getName());

    /**
     * Map predicates to SETs of ground atoms
     */
    private MultiMap<Predicate, Literal> herbrand;

    /**
     * Use the subsumption engine wrapper
     */
    private Matching matching;

    /**
     * The current Herbrand model processed as an indexed structure
     */
    private SubsumptionEngineJ2.ClauseE clauseE;

    /**
     * The rules used to create the current Herbrand model pre-processed as an indexed structure
     */
    public LinkedHashMap<HornClause, PreparedRule> preparedRules;

    public HerbrandModel(Collection<Literal> facts, Collection<HornClause> rules) {
        herbrand = new MultiMap<>();
        preparedRules = new LinkedHashMap<>();
        matching = new Matching();
        addFacts(facts);
        addRules(rules);
    }

    /**
     * The main Herbrand model/interpretation computation (Datalog inference) happens here...
     * <p>
     * todo somehow change this to incrementally pass only NEW facts to existing ClauseE without reindexing from scratch?
     * <p>
     * todo follow the rule precedence/order from the GraphTemplate here, so that we can skip the upper layer rules in the first iterations...
     *      - not a big speedup, as there is a fast initial check whether the ClauseE contains all the predicates from ClauseC at the very beginning
     *
     * @return
     */
    public Collection<Literal> inferAtoms() {
        boolean changed;
        int round = 0;

        int herbrandSize0 = VectorUtils.sum(herbrand.sizes());
        LOG.finer("herbrand size before round " + round + " = " + herbrandSize0);
        do {
            //get all valid literals from the CURRENT herbrand model/map into a newly indexed ClauseE structure
            final Clause exampleClause = new Clause(Sugar.flatten(herbrand.values()));
            clauseE = matching.createClauseE(exampleClause);

            LinkedList<Map.Entry<HornClause, PreparedRule>> entries = new LinkedList<>(preparedRules.entrySet()); // a copy for removing
            for (Iterator<Map.Entry<HornClause, PreparedRule>> iterator = entries.iterator(); iterator.hasNext(); ) {
                final Map.Entry<HornClause, PreparedRule> next = iterator.next();
                final HornClause rule = next.getKey();
                final PreparedRule preparedRule = next.getValue();

                matching.getEngine().addSolutionConsumer(preparedRule.solutionConsumer);
                // if the rule head is already ground
                if (preparedRule.isGroundHead) {
                    // add the head to herbrand iff the rule body is true
                    if (matching.subsumption(preparedRule.clauseC, clauseE)) {
                        herbrand.put(rule.head().predicate(), rule.head());
                        iterator.remove(); // if so, do not ever try this ground rule again
                    }
                } else {  //if it is not ground, find (and in the background through the solutionConsumer add to herbrand) all NEW substitutions for the head literal
                    matching.allSubstitutions(preparedRule.clauseC, clauseE, Integer.MAX_VALUE);
                }
                //remove the consumer as the found substitutions should be applied only to the head of the currently solved rule
                matching.getEngine().removeSolutionConsumer(preparedRule.solutionConsumer);
            }
            LOG.finest(() -> preparedRules.size() + " rules grounded.");
            int herbrandSize1 = VectorUtils.sum(herbrand.sizes());
            LOG.finer("herbrand size after round " + round++ + " = " + herbrandSize1);
            changed = herbrandSize1 > herbrandSize0;
            herbrandSize0 = herbrandSize1;  //reset to next round
        } while (changed);
        return Sugar.flatten(herbrand.values());
    }

    /**
     * Here we really want to ask the current Herbrand base for all substitutions
     * - i.e. we do not prune with the tuple-not-in special predicates anymore!
     *
     * @return
     */
    public Pair<Term[], List<Term[]>> groundingSubstitutions(Clause clause) {
        if (clauseE == null) {
            clauseE = matching.createClauseE(new Clause(Sugar.flatten(herbrand.values())));
        }
        final SubsumptionEngineJ2.ClauseC clauseC = matching.createClauseC(clause);
        cz.cvut.fel.ida.utils.generic.tuples.Pair<Term[], List<Term[]>> listPair = matching.allSubstitutions(clauseC, clauseE, Integer.MAX_VALUE);

        Term[] variables = listPair.r;
        for (int i = 0; i < variables.length; i++) {
            variables[i].setIndexWithinSubstitution(i);
        }

        return new Pair<>(variables, listPair.s);
    }

    /**
     * Add all existing unit ground literals (facts) to the herbrand map
     *
     * @param facts
     */
    public void addFacts(Collection<Literal> facts) {
        for (Literal groundLiteral : facts) {
            herbrand.put(groundLiteral.predicate(), groundLiteral);
        }
    }

    /**
     * Preprocess all the horn clauses to later work with them through Matching
     *
     * @param rules
     * @return
     */
    public void addRules(Collection<HornClause> rules) {
        //rule heads map to empty sets at the beginning
        for (HornClause rule : rules) {
            final Predicate headPredicate = rule.head().predicate();
            if (!herbrand.containsKey(headPredicate)) {   //a rule head predicate might have been in the facts already!
                herbrand.set(headPredicate, Collections.synchronizedSet(new HashSet<>()));
            }
            final boolean isGroundHead = LogicUtils.isGround(rule.head());
            final SubsumptionEngineJ2.ClauseC clauseC = prepareClauseForGrounder(rule, isGroundHead);
            // solution consumer = automatically add all found valid substitutions of the head literal into the herbrand map
            PredicateSolutionConsumer solutionConsumer = new PredicateSolutionConsumer(rule.head(), herbrand.get(headPredicate));

            preparedRules.put(rule, new PreparedRule(clauseC, isGroundHead, solutionConsumer));
        }
    }

    public void removeRules(Collection<HornClause> rules) {
        for (HornClause rule : rules) {
            preparedRules.remove(rule);
        }
    }

    /**
     * Transform the input HornClause (may contain negated literals in body) into a preprocessed ClauseC structure
     * suitable for {@link Matching}, which is also updated with special predicates from the rule in the process
     * - adds special predicate that evaluates to true if the head-mapping set does not contain such a literal YET
     *
     * @param hc
     * @return
     */
    public SubsumptionEngineJ2.ClauseC prepareClauseForGrounder(HornClause hc, boolean groundHead) {
        final Predicate headPredicate = hc.head().predicate();
        Set<Literal> literalSet = new HashSet<>(hc.body().literals());

        //extend the rule with restriction that the head substitution solution must not be contained in the herbrand YET (for speedup instead of just adding them repetitively to the set)
        matching.getEngine().addCustomPredicate(new TupleNotIn(headPredicate, herbrand.get(headPredicate)));

        for (Literal l : hc.body().literals()) {
            if (l.isNegated()) {
                literalSet.add(new Literal(tupleNotInPredicateName(l.predicate()), false, l.arguments()));  //negated body literals will only be satified if not found YET (see @link TupleNotIn)
                matching.getEngine().addCustomPredicate(new TupleNotIn(l.predicate(), herbrand.get(l.predicate())));
            }
        }
        if (!groundHead) {
            literalSet.add(hc.head().negation());
            literalSet.add(new Literal(tupleNotInPredicateName(headPredicate), false, hc.head().arguments()));
        }
        final Clause clause = new Clause(literalSet);
        return matching.createClauseC(clause);
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
                if (hc.body() != null) rest.add(hc);
            }
        }
        return new Pair<>(rest, groundFacts);
    }

    public class PreparedRule {
        final SubsumptionEngineJ2.ClauseC clauseC;
        final boolean isGroundHead;
        final PredicateSolutionConsumer solutionConsumer;

        public PreparedRule(SubsumptionEngineJ2.ClauseC clauseC, boolean isGroundHead, PredicateSolutionConsumer solutionConsumer) {
            this.clauseC = clauseC;
            this.isGroundHead = isGroundHead;
            this.solutionConsumer = solutionConsumer;
        }
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
            for (int i = 0; i < template.length; i++) {
                template[i].setIndexWithinSubstitution(i);
            }
            headGroundings.add(ruleHead.subsCopy(solution));
        }

        public void clear(){
            headGroundings.clear();
        }
    }

    /**
     * Name of the special predicate for binding within the substitution engine
     *
     * @param predicate
     * @return
     */
    public static String tupleNotInPredicateName(Predicate predicate) {
        return "@tuplenotin-" + predicate.name + "/" + predicate.arity;
    }

    /**
     * For a given predicate stores all found substitutions and is only satisfiable for NEW solutions.
     * To be added to the substitution engine for solution pruning and stratified negation.
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

    /**
     * Removes everything from the herbrand map as well as all the (linked) solution consumers
     */
    public void removeAllAtoms() {
        for (Map.Entry<Predicate, Set<Literal>> predicateSetEntry : herbrand.entrySet()) {
            predicateSetEntry.getValue().clear();
        }
    }
}
