package cz.cvut.fel.ida.logic.subsumption;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;
import java.util.logging.Logger;

/**
 * Least Herbrand model from purely logical world (taken from Ondra)
 */
public class HerbrandModel {
    private static final Logger LOG = Logger.getLogger(HerbrandModel.class.getName());

    /**
     * Map predicates to ground literal Sets
     */
    public HerbrandMap herbrand;

    /**
     * Using the subsumption engine wrapper
     */
    public Matching matching;

    public HerbrandModel() {
        herbrand = new HerbrandMap();
    }

    public HerbrandModel(HerbrandMap init) {
        herbrand = init;
    }

    public HerbrandModel(Collection<Literal> facts) {
        herbrand = new HerbrandMap();
        populateHerbrand(facts);
    }

    /**
     * @param clauses - may contain facts and rules
     * @return
     */
    public HerbrandMap inferModel(Collection<? extends Clause> clauses) {
        Pair<List<HornClause>, List<Literal>> rulesAndFacts = rulesAndFacts(clauses);
        return inferModel(rulesAndFacts.r, rulesAndFacts.s);
    }

    public Collection<Literal> inferLiterals(Collection<HornClause> irules, Collection<Literal> facts) {
        HerbrandMap herbrandMap = inferModel(irules, facts);
        return Sugar.flatten(herbrandMap.getLiterals());
    }

    /**
     * todo add version with constraints at input
     *
     * @param irules
     * @param facts
     * @return
     */
    public HerbrandMap inferModel(Collection<HornClause> irules, Collection<Literal> facts) {
        populateHerbrand(facts);

        LinkedHashSet<HornClause> rules = new LinkedHashSet<>(irules); //for removing
        //rule heads map to empty sets at the beginning
        Set<Predicate> headSignatures = new LinkedHashSet<>();  //for faster iteration
        for (HornClause rule : rules) {
            headSignatures.add(rule.head().predicate());
            if (!herbrand.containsKey(rule.head().predicate())) {   //a rule head predicate might have been in the facts already!
                herbrand.set(rule.head().predicate(), new HashSet<>());
            }
        }

        boolean changed;
        int round = 0;

        int herbrandSize0 = VectorUtils.sum(herbrand.sizes());
        LOG.finer("herbrand size before round " + round + " = " + herbrandSize0);
        do {
            //get all valid literals from current herbrand in to matching
            matching = new Matching(Sugar.<Clause>list(new Clause(Sugar.flatten(herbrand.values())))); //todo somehow change this to incrementally pass only NEW facts (ClauseE) to existing Matching object for speedup? Try version without example indexing
            //LOG.finest("Matching created.");
            for (Predicate predicate : headSignatures) {
                //may overwrite the previous ones which is actually what we want (?)
                matching.getEngine().addCustomPredicate(new TupleNotIn(predicate, herbrand.get(predicate))); //predicate that evaluates to true if the head-mapping set does not containt such a literal yet
            }
            for (Iterator<? extends HornClause> iterator = rules.iterator(); iterator.hasNext(); ) {    //todo now follow the rule precedence/order from the GraphTemplate here, so that we can skip the upper layer rules in the first iterations...
                HornClause rule = iterator.next();
                Literal head = rule.head();
                // solution consumer = automatically add all found valid substitutions of the head literal into the herbrand map
                SolutionConsumer solutionConsumer = new PredicateSolutionConsumer(head, herbrand.get(head.predicate()));
                matching.getEngine().addSolutionConsumer(solutionConsumer);
                // if the rule head is already ground
                if (LogicUtils.isGround(head)) {
//                    Clause query = new Clause(LogicUtils.flipSigns(rule.body().literals()));
//                    Clause query = new Clause(rule.body().literals());  //todo next both versions work in non-ground bodies, but only this one in ground bodies, investigate why

                    // add the head to herbrand if the rule body is true
                    if (matching.subsumption(prepareClauseForGrounder(rule, true), 0)) {
                        herbrand.put(head.predicate(), head);
                        iterator.remove(); // if so, do not ever try this ground rule again
                    }
                } else {
                    //if it is not ground, extend the rule with restriction that the head substitution solution must not be contained in the herbrand yet (for speedup instead of just adding them repetitively to the set)
                    cz.cvut.fel.ida.utils.generic.tuples.Pair<Term[], List<Term[]>> listPair = matching.allSubstitutions(prepareClauseForGrounder(rule, false), 0, Integer.MAX_VALUE); //then find (and through consumer add to herbrand) all NEW substitutions for the head literal - todo add version where these substitutions will be iteratively saved into some hashmap instead of repeating final substitutions
                }
                matching.getEngine().removeSolutionConsumer(solutionConsumer); //the found substitutions should be applied only to the head of the currently solved rule
            }
            LOG.finest(() -> irules.size() + " rules grounded.");
            int herbrandSize1 = VectorUtils.sum(herbrand.sizes());
            LOG.finer("herbrand size after round " + round++ + " = " + herbrandSize1);
            changed = herbrandSize1 > herbrandSize0;
            herbrandSize0 = herbrandSize1;  //reset to next round
        } while (changed);
        return herbrand;
    }

    public Pair<Term[], List<Term[]>> groundingSubstitutions(HornClause hornClause) {
        Clause query = new Clause(hornClause.getLiterals()); //todo check negations here
        cz.cvut.fel.ida.utils.generic.tuples.Pair<Term[], List<Term[]>> listPair = matching.allSubstitutions(query, 0, Integer.MAX_VALUE);

        Term[] variables = listPair.r;
        for (int i = 0; i < variables.length; i++) {
            variables[i].setIndexWithinSubstitution(i);
        }

        return new Pair<>(variables, listPair.s);
    }


    /**
     * add all existing unit ground literals (facts) to herbrand set
     *
     * @param facts
     */
    public void populateHerbrand(Collection<Literal> facts) {
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
     * Transform the input HornClause (may contain negated literals in body) into a general Clause suitable for {@link Matching}
     *
     * @param hc
     * @return
     */
    public Clause prepareClauseForGrounder(HornClause hc, boolean groundHead) {
        Set<Literal> literalSet = new HashSet<>(hc.body().literals());

        for (Literal l : hc.body().literals()) {
            if (l.isNegated()) {
                literalSet.add(new Literal(tupleNotInPredicateName(l.predicate()), false, l.arguments()));  //negated body literals will only be satified if not found YET (see @link TupleNotIn)
            }
        }
        if (!groundHead) {
            literalSet.add(hc.head().negation());
            literalSet.add(new Literal(tupleNotInPredicateName(hc.head().predicate()), false, hc.head().arguments()));
        }
        return new Clause(literalSet);
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
            for (int i = 0; i < template.length; i++) {
                template[i].setIndexWithinSubstitution(i);
            }
            headGroundings.add(ruleHead.subsCopy(solution));
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

    public static class HerbrandMap extends MultiMap<Predicate, Literal> {
        public Collection<Set<Literal>> getLiterals() {
            return super.values();
        }
    }
}
