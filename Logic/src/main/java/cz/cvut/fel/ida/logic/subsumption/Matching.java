/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.subsumption;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.math.IntegerFunction;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Triple;

import java.util.*;

/**
 * Class Matching encapsulates engines for computing theta-subsumption, namely ReSumEr1 and ReSumEr2 (Kuzelka, Zelezny, Fundamenta Informaticae, 2008)
 *
 * @author ondra
 */
public class Matching {

    public final static int THETA_SUBSUMPTION = SubsumptionEngineJ2.THETA, OI_SUBSUMPTION = SubsumptionEngineJ2.OBJECT_IDENTITY;

    private List<SubsumptionEngineJ2.ClauseE> examples = new ArrayList<SubsumptionEngineJ2.ClauseE>();

    public final static int YES = 1, NO = 2, UNDECIDED = 3;

    private int resumerVersion = 2;

    private SubsumptionEngineJ2 engine;

    private boolean adaptPropagationStrength = false;

    private boolean learnVariableOrdering = true;

    private Random random = new Random(Settings.seed);

    /**
     * Creates a new instance of class Matching.
     */
    public Matching() {
        this.engine = new SubsumptionEngineJ2();
        this.engine.setRestartSequence(new IntegerFunction.Exponential(50, 2, 500));
    }

    /**
     * Creates a new instance of class Matching. It also preprocesses positive and negative examples
     * given as arguments (it computes efficient index data-structures etc).
     *
     * @param examples list of examples (Clauses)
     */
    public Matching(List<Clause> examples) {
        this(new SubsumptionEngineJ2(), examples);
    }

    /**
     * Creates a new instance of class Matching. It also preprocesses positive and negative examples
     * given as arguments (it computes efficient index data-structures etc).
     *
     * @param engine   SubsumptionEngineJ2 which should be used by the Matching object.
     * @param examples list of examples (Clauses)
     */
    public Matching(SubsumptionEngineJ2 engine, List<Clause> examples) {
        this.engine = engine;
        this.engine.setRestartSequence(new IntegerFunction.Exponential(50, 2, 500));
        for (Clause e : examples) {
            this.examples.add(this.engine.new ClauseE(e));
        }
    }

    /**
     * Sets the sequence of "tries" iterable restarts of the subsumption algorithms.
     * For example if we want to have a sequence of restarts increasing exponentially as 10 exp(index) + 100
     * then we can use new new IntegerFunction.Exponential(10, 1, 100);
     *
     * @param f restart sequence
     */
    public void setRestartSequence(IntegerFunction f) {
        this.engine.setRestartSequence(f);
    }

    /**
     * Computes coverage of Clause hypothesis on the examples (stored iterable
     * the Matching object and set e.g. through the parameters of constructor)
     *
     * @param hypothesis the clause for which coverage should be computed
     * @param undecided  boolean array value true at position i means that theta-subsumption with
     *                   the corresponding example (at position i) should be checked. This can be useful when
     *                   we have a systematic search procedure based on specialization or generalization. When it is
     *                   based on specialization, then once we show that some examples cannot be covered by the given
     *                   hypothesis then they cannot be covered by any hypothesis obtained by specializing this hypothesis
     *                   therefore subsumption (which is quite costly) does not have to be computed for this particular pair hypothesis-example.
     *                   When searching for hypotheses iterable a method based on generalization, an analogical property holds (but for covered examples
     *                   )
     * @return array with results iterable the form of integers: Matching.YES means that the theta-subsumption holds, Matching.NO means that theta-subsumption
     * does not hold and Matching.UNDECIDED means that either we did not want to compute subsumption for the particular example or the subsumption
     * algorithm did not finish within given limit of backtracks or within given time-limit etc,
     */
    public int[] evaluateOnExamples(Clause hypothesis, boolean[] undecided) {
        return evaluateExamples(hypothesis, examples, undecided);
    }

    private int[] evaluateExamples(Clause hypothesis, List<SubsumptionEngineJ2.ClauseE> examples, boolean[] undecided) {
        int[] retVal = new int[undecided.length];
        undecided = VectorUtils.copyArray(undecided);
        Arrays.fill(retVal, UNDECIDED);
        for (Clause component : hypothesis.connectedComponents()) {
            int[] result = evaluateExamplesAgainstOneComponent(component, examples, undecided);
            for (int i = 0; i < result.length; i++) {
                if (undecided[i] && result[i] == NO) {
                    retVal[i] = NO;
                    undecided[i] = false;
                } else if (undecided[i] && result[i] == YES) {
                    retVal[i] = YES;
                }
            }
        }
        return retVal;
    }

    private int[] evaluateExamplesAgainstOneComponent(Clause hypothesis, List<SubsumptionEngineJ2.ClauseE> examples, boolean[] undecided) {
        SubsumptionEngineJ2.ClauseStructure c = null;
        if (resumerVersion < 3) {
            c = this.engine.new ClauseC(hypothesis);
        }
        int[] result = new int[undecided.length];
        int j = 0;
        engine.setFirstVariableOrder(null);
        final int UP = 0, DOWN = 1;
        //int shift = Sugar.random(2);
        int shift = random.nextInt(2);
        boolean[] shiftOrNot = VectorUtils.randomBooleanVector(undecided.length, random);
        long time1 = 0;
        int maxRestart = 0;
        if (adaptPropagationStrength) {
            if (shift == UP) {
                engine.setArcConsistencyFrom(engine.getArcConsistencyFrom() + 1);
            } else if (shift == DOWN) {
                engine.setArcConsistencyFrom(Math.max(1, engine.getArcConsistencyFrom() - 1));
            }
        }
        long m1 = System.currentTimeMillis();
        for (int i = 0; i < undecided.length; i++) {
            if (undecided[i] && shiftOrNot[i]) {
                if (j > 0 && learnVariableOrdering) {
                    engine.setFirstVariableOrder(engine.getLastVariableOrder());
                }
                Boolean succ = engine.solveWithResumer(c, examples.get(i), resumerVersion);
                maxRestart = Math.max(engine.getNoOfRestarts(), maxRestart);
                if (!engine.solvedWithoutSearch()) {
                    j++;
                }
                if (succ == null) {
                    result[i] = UNDECIDED;
                } else if (succ.booleanValue()) {
                    result[i] = YES;
                } else {
                    result[i] = NO;
                }
            }
        }
        long m2 = System.currentTimeMillis();
        time1 = m2 - m1;
        if (adaptPropagationStrength) {
            if (shift == UP) {
                engine.setArcConsistencyFrom(engine.getArcConsistencyFrom() - 1);
            } else {
                engine.setArcConsistencyFrom(Math.max(0, engine.getArcConsistencyFrom() + 1));
            }
        }
        m1 = System.currentTimeMillis();
        for (int i = 0; i < undecided.length; i++) {
            if (undecided[i] && !shiftOrNot[i]) {
                if (j > 0 && learnVariableOrdering) {
                    engine.setFirstVariableOrder(engine.getLastVariableOrder());
                }
                Boolean succ = engine.solveWithResumer(c, examples.get(i), resumerVersion);
                maxRestart = Math.max(engine.getNoOfRestarts(), maxRestart);
                if (!engine.solvedWithoutSearch()) {
                    j++;
                }
                if (succ == null) {
                    result[i] = UNDECIDED;
                } else if (succ.booleanValue()) {
                    result[i] = YES;
                } else {
                    result[i] = NO;
                }
            }
        }
        m2 = System.currentTimeMillis();
        long time2 = m2 - m1;
        double t1 = (double) time1 / (double) VectorUtils.occurrences(VectorUtils.and(undecided, shiftOrNot), true);
        double t2 = (double) time2 / (double) VectorUtils.occurrences(VectorUtils.and(undecided, VectorUtils.not(shiftOrNot)), true);
        if (t1 < t2) {
            if (adaptPropagationStrength) {
                if (shift == UP) {
                    if (maxRestart > engine.getArcConsistencyFrom() && engine.getArcConsistencyFrom() > 1) {
                        engine.setArcConsistencyFrom(engine.getArcConsistencyFrom() + 1);
                    }
                } else if (shift == DOWN) {
                    engine.setArcConsistencyFrom(Math.max(1, engine.getArcConsistencyFrom() - 1));
                }
                //System.out.println("AC adapted: "+engine.getArcConsistencyFrom()+", "+t1+" < "+t2);
            }
        }
        return result;
    }


    /**
     * Sets the subsumption mode.
     *
     * @param mode can be one of the following:
     *             THETA_SUBSUMPTION, OI_SUBSUMPTION, SELECTIVE_OI_SUBSUMPTION
     */
    public void setSubsumptionMode(int mode) {
        this.engine.setSubsumptionMode(mode);
    }

    /**
     * Sets the first restart iterable which forward-checking starts to be used by the subsumption algorithm.
     *
     * @param restart the first restart iterable which forward-checking should be started
     */
    public void setForwardCheckingStartsIn(int restart) {
        this.engine.setForwardCheckingFrom(restart);
    }

    /**
     * Sets the first restart iterable which arc-consistency starts to be used by the subsumption algorithm.
     *
     * @param restart the first restart iterable which arc-consistency should be started
     */
    public void setArcConsistencyStartsIn(int restart) {
        this.engine.setArcConsistencyFrom(restart);
    }

    /**
     * Sets the timeout after which subsumption is considered undecided (iterable milliseconds)
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.engine.setTimeout(timeout);
    }

    /**
     * The algorithm is able to automatically adjust the propagation strength
     * which means that it is able to automatically adjust the first restarts
     * iterable which forward-checking and arc-consistency start.
     *
     * @param adaptPropagationStrength the adaptPropagationStrength to set
     */
    public void setAdaptPropagationStrength(boolean adaptPropagationStrength) {
        this.adaptPropagationStrength = adaptPropagationStrength;
    }

    /**
     * @return the subsumption engine used by the Matching object
     */
    public SubsumptionEngineJ2 getEngine() {
        return this.engine;
    }

    /**
     * The subsumption engines do not use representation of hypotheses iterable the form
     * of objects Clause, they represent them using objects of class SubsumptionEngineJ2.ClauseC.
     * This method allows the user to create such representations of clauses.
     *
     * @param c the clause for which we want to create the efficient representation
     * @return the representation of Clause c as SubsumptionEngineJ2.ClauseC
     */
    public SubsumptionEngineJ2.ClauseC createClauseC(Clause c) {
        return engine.createClauseC(c);
        //return engine.createCluaseC(preprocessHypothesis(c));
    }

    /**
     * The subsumption engines do not use representation of examples iterable the form
     * of objects Clause, they represent them using objects of class SubsumptionEngineJ2.ClauseE.
     * This method allows the user to create such representations of examples.
     *
     * @param e the clause for which we want to create the efficient representation
     * @return the representation of Clause e as SubsumptionEngineJ2.ClauseE
     */
    public SubsumptionEngineJ2.ClauseE createClauseE(Clause e) {
        return engine.createClauseE(e);
        //return engine.createClauseE(preprocessExample(e));
    }

    /**
     * Computes subsumption for clauses c (hypothesis), e (example).
     *
     * @param c hypothesis
     * @param e examples
     * @return true if subsumption has been proved iterable the allocated time or number of backtracks,
     * false if it has been disproved iterable the allocated time or number of backtracks and null otherwise
     * (i.e. if the algorithm had not been able to decide subsumption iterable the allocated time or number of backtracks)
     */
    public Boolean subsumption(SubsumptionEngineJ2.ClauseC c, SubsumptionEngineJ2.ClauseE e) {
        return engine.solveWithResumer(c, e, resumerVersion);
    }

    /**
     * Computes subsumption for clauses c (hypothesis), e (example).
     *
     * @param c hypothesis
     * @param e examples
     * @return true if subsumption has been proved iterable the allocated time or number of backtracks,
     * false if it has been disproved iterable the allocated time or number of backtracks and null otherwise
     * (i.e. if the algorithm had not been able to decide subsumption iterable the allocated time or number of backtracks)
     */
    public Boolean subsumption(Clause c, Clause e) {
        if (this.engine.subsumptionMode() == Matching.THETA_SUBSUMPTION) {
            for (Clause component : c.connectedComponents()) {
                if (!engine.solveWithResumer(component, e, resumerVersion)) {
                    return false;
                }
            }
        } else {
            return engine.solveWithResumer(c, e, resumerVersion);
        }
        return true;
    }

    public Boolean subsumption(Clause c, int index) {
        return this.subsumption(this.createClauseC(c), this.examples.get(index));
    }

    /**
     * Computes all (or at most maxCount) solutions (substitutions) of the problem "c theta-subsumes e"
     *
     * @param c        hypothesis
     * @param e        example
     * @param maxCount maximum number of solutions that we want to get
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSubstitutions(Clause c, Clause e, int maxCount) {
        return engine.allSolutions(c, e, maxCount);
    }

    /**
     * Computes all (or at most maxCount) solutions (substitutions) of the problem "c theta-subsumes e"
     *
     * @param c        hypothesis
     * @param e        example
     * @param maxCount maximum number of solutions that we want to get
     * @param depth    depth (in the search tree - counted by variables) beyond which only one solution is given insetad of all solutions
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSubstitutions(Clause c, Clause e, int maxCount, int depth) {
        return engine.allSolutions(c, e, maxCount, depth);
    }

    /**
     * Computes all solutions (substitutions) of the problem "c theta-subsumes e"
     *
     * @param c query
     * @param e example
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSubstitutions(Clause c, Clause e) {
        return allSubstitutions(c, e, Integer.MAX_VALUE);
    }

    /**
     * Computes all (or at most maxCount) solutions (substitutions) of the problem "c theta-subsumes e"
     *
     * @param c            query
     * @param exampleIndex index of the example
     * @param maxCount     maximum number of solutions that we want to get
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSubstitutions(Clause c, int exampleIndex, int maxCount) {
        SubsumptionEngineJ2.ClauseC clauseC = this.engine.createClauseC(c);
        return engine.allSolutions(clauseC, this.examples.get(exampleIndex), maxCount);
    }

    /**
     * Computes all (or at most maxCount) solutions (substitutions) of the problem "c theta-subsumes e"
     *
     * @param c            query
     * @param exampleIndex index of the example
     * @param maxCount     maximum number of solutions that we want to get
     * @param depth        depth (in the search tree - counted by variables) beyond which only one solution is given insetad of all solutions
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSubstitutions(Clause c, int exampleIndex, int maxCount, int depth) {
        SubsumptionEngineJ2.ClauseC clauseC = this.engine.createClauseC(c);
        return engine.allSolutions(clauseC, this.examples.get(exampleIndex), maxCount, depth);
    }

    public Pair<Term[], List<Term[]>> allSubstitutions(SubsumptionEngineJ2.ClauseC c, SubsumptionEngineJ2.ClauseE e, int maxCount) {
        return engine.allSolutions(c, e, maxCount, Integer.MAX_VALUE);
    }

    public Pair<Term[], List<Term[]>> allSubstitutions(SubsumptionEngineJ2.ClauseC c, SubsumptionEngineJ2.ClauseE e, int maxCount, int depth) {
        return engine.allSolutions(c, e, maxCount, depth);
    }

    @Deprecated
    public Pair<Term[], List<Term[]>> allTrueGroundings(Clause c, int exampleIndex) {
        //The first part needs to be way more efficient
        List<Literal> auxLiterals = new ArrayList<Literal>();
        for (Variable v : c.variables()) {
            auxLiterals.add(new Literal("", true, v));
        }
        Clause aux = new Clause(auxLiterals);
        Pair<Term[], List<Term[]>> allGroundings = this.allSubstitutions(aux, exampleIndex, Integer.MAX_VALUE);
        Set<Map<Term, Term>> grSet = new LinkedHashSet<Map<Term, Term>>();
        for (Term[] grounding : allGroundings.s) {
            Map<Term, Term> gr = new HashMap<Term, Term>();
            for (int i = 0; i < grounding.length; i++) {
                gr.put(allGroundings.r[i], grounding[i]);
            }
            grSet.add(gr);
        }
        List<Literal> flippedSignLiterals = new ArrayList<Literal>();
        for (Literal literal : c.literals()) {
            flippedSignLiterals.add(literal.negation());
        }
        Clause negation = new Clause(flippedSignLiterals);
        Pair<Term[], List<Term[]>> negGroundings = this.allSubstitutions(negation, exampleIndex, Integer.MAX_VALUE);
        Set<Map<Term, Term>> negSet = new HashSet<Map<Term, Term>>();
        for (Term[] grounding : negGroundings.s) {
            Map<Term, Term> gr = new LinkedHashMap<Term, Term>();
            for (int i = 0; i < grounding.length; i++) {
                gr.put(negGroundings.r[i], grounding[i]);
            }
            negSet.add(gr);
        }
        grSet = Sugar.setDifference(grSet, negSet);
        List<Term[]> retList = new ArrayList<Term[]>();
        for (Map<Term, Term> map : grSet) {
            Term[] grounding = new Term[allGroundings.r.length];
            for (int i = 0; i < grounding.length; i++) {
                grounding[i] = map.get(allGroundings.r[i]);
            }
            retList.add(grounding);
        }
        return new Pair<Term[], List<Term[]>>(allGroundings.r, retList);
    }

    public Clause thetaReduction(Clause clause) {
        Set<Literal> closed = new HashSet<Literal>();
        while (true) {
            boolean reduced = false;
            for (Literal l : clause.literals()) {
                if (!closed.contains(l)) {
                    Pair<Term[], List<Term[]>> substitutions = this.allSubstitutions(clause, new Clause(Sugar.setDifference(clause.literals(), Sugar.<Literal>set(l))), 1);
                    if (substitutions.s.size() > 0) {
                        clause = LogicUtils.substitute(clause, substitutions.r, substitutions.s.get(0));
                        reduced = true;
                    }
                }
            }
            if (!reduced) {
                break;
            }
        }
        return clause;
    }

    /**
     * Sets the version of ReSumEr
     *
     * @param version 1,2
     */
    public void setResumerVersion(int version) {
        this.resumerVersion = version;
    }

    /**
     * The subsumption algorithms are capable to adjust the variable ordering heuristic automatically (to some extent)
     *
     * @param learn boolean which specifies whether variable ordering heuristic should or should not be adjusted
     */
    public void setLearnVariableOrdering(boolean learn) {
        this.learnVariableOrdering = learn;
    }

    /**
     * Sets the random seed for the subsumption algorithms (they are randomized).
     *
     * @param seed seed for the random number generator
     */
    public void setRandomSeed(long seed) {
        this.random = new Random(seed);
        this.engine.setRandomSeed(seed);
    }

    private Clause preprocessForIsomorphismCheck(Clause c) {
        List<Literal> literals = new ArrayList<Literal>();
        for (Literal l : c.literals()) {
            Literal newLiteral = new Literal((l.isNegated()) ? "~!" : "~" + l.predicateName(), l.arity());
            for (int i = 0; i < newLiteral.arity(); i++) {
                newLiteral.set(l.get(i), i);
            }
            literals.add(newLiteral);
        }
        return new Clause(literals);
    }

    public boolean isomorphism(Clause a, Clause b) {
        if (!(a.variables().size() == b.variables().size() &&
                a.countLiterals() == b.countLiterals() &&
                a.predicates().equals(b.predicates())
        )) {
            return false;
        }
        Matching m = new Matching();
        m.setSubsumptionMode(OI_SUBSUMPTION);
        m.setResumerVersion(this.resumerVersion);
        m.setAdaptPropagationStrength(this.adaptPropagationStrength);
        m.setForwardCheckingStartsIn(this.engine.getForwardCheckingFrom());
        m.setArcConsistencyStartsIn(this.engine.getArcConsistencyFrom());
        m.setLearnVariableOrdering(this.learnVariableOrdering);
        m.setRestartSequence(this.engine.getRestartSequence());
        m.setTimeout(this.engine.getTimeout());

        a = preprocessForIsomorphismCheck(a);
        b = preprocessForIsomorphismCheck(b);

        return m.subsumption(a, b);
    }

    private boolean isomorphism(SubsumptionEngineJ2.ClauseC a, SubsumptionEngineJ2.ClauseE b) {
        Matching m = new Matching();
        m.setSubsumptionMode(OI_SUBSUMPTION);
        m.setResumerVersion(this.resumerVersion);
        m.setAdaptPropagationStrength(this.adaptPropagationStrength);
        m.setForwardCheckingStartsIn(this.engine.getForwardCheckingFrom());
        m.setArcConsistencyStartsIn(this.engine.getArcConsistencyFrom());
        m.setLearnVariableOrdering(this.learnVariableOrdering);
        m.setRestartSequence(this.engine.getRestartSequence());
        m.setTimeout(this.engine.getTimeout());

        return m.subsumption(a, b);
    }

    public List<Clause> nonisomorphic(Iterable<Clause> clauses) {
        MultiList<Pair<Integer, Integer>, Clause> mm = new MultiList<Pair<Integer, Integer>, Clause>();
        for (Clause c : clauses) {
            mm.put(new Pair<Integer, Integer>(c.countLiterals(), c.variables().size()), c);
        }
        List<Clause> retVal = new ArrayList<Clause>();
        for (List<Clause> list : mm.values()) {
            retVal.addAll(nonisomorphic_impl(list));
        }
        return retVal;
    }

    private List<Clause> nonisomorphic_impl(Iterable<Clause> clauses) {
        Set<Integer> filteredOut = new HashSet<Integer>();
        List<SubsumptionEngineJ2.ClauseE> es = Sugar.funcall(clauses, new Sugar.Fun<Clause, SubsumptionEngineJ2.ClauseE>() {
            @Override
            public SubsumptionEngineJ2.ClauseE apply(Clause clause) {
                return createClauseE(clause);
            }
        });
        List<SubsumptionEngineJ2.ClauseC> cs = Sugar.funcall(clauses, new Sugar.Fun<Clause, SubsumptionEngineJ2.ClauseC>() {
            @Override
            public SubsumptionEngineJ2.ClauseC apply(Clause clause) {
                return createClauseC(clause);
            }
        });
        for (int i = 0; i < cs.size(); i++) {
            if (!filteredOut.contains(i)) {
                for (int j = i + 1; j < es.size(); j++) {
                    if (this.isomorphism(cs.get(i), es.get(j))) {
                        filteredOut.add(j);
                    }
                }
            }
        }
        List<Clause> retVal = new ArrayList<Clause>();
        int i = 0;
        for (Clause c : clauses) {
            if (!filteredOut.contains(i)) {
                retVal.add(c);
            }
            i++;
        }
        return retVal;
    }

    /**
     * Roughly based on Ermon, Gomes, Selman: Uniform Solution Sampling Using a Constraint Solver As an Oracle
     *
     * @param query         conjunctive query
     * @param exampleIndex  index of the database (possible world - example) stored in the Matching object
     * @param subsampleSize maximal number of sampled solutions (in each iteration)
     * @param l             size of the step
     */
    public Triple<Term[], List<Term[]>, Double> searchTreeSampler(Clause query, int exampleIndex, int subsampleSize, int l) {
        return this.searchTreeSampler(query, this.examples.get(exampleIndex), subsampleSize, l);
    }

    /**
     * Roughly based on Ermon, Gomes, Selman: Uniform Solution Sampling Using a Constraint Solver As an Oracle
     *
     * @param query         conjunctive query
     * @param db            the database (possible world)
     * @param subsampleSize maximal number of sampled solutions (in each iteration)
     * @param l             size of the step
     */
    public Triple<Term[], List<Term[]>, Double> searchTreeSampler(Clause query, Clause db, int subsampleSize, int l) {
        return this.searchTreeSampler(query, this.createClauseE(db), subsampleSize, l);
    }

    /**
     * Roughly based on Ermon, Gomes, Selman: Uniform Solution Sampling Using a Constraint Solver As an Oracle
     *
     * @param query         conjunctive query
     * @param example       the database (possible world)
     * @param subsampleSize maximal number of sampled solutions (in each iteration)
     * @param l             size of the step
     */
    private Triple<Term[], List<Term[]>, Double> searchTreeSampler(Clause query, SubsumptionEngineJ2.ClauseE example, int subsampleSize, int l) {
        if (!subsumption(createClauseC(query), example)) {
            Term[] template = new Term[query.variables().size()];
            int i = 0;
            for (Variable v : query.variables()) {
                template[i] = v;
                i++;
            }
            return new Triple<Term[], List<Term[]>, Double>(template, new ArrayList<Term[]>(1), Double.NEGATIVE_INFINITY);
        }
        Triple<Term[], List<Term[]>, Double> retVal = new Triple<Term[], List<Term[]>, Double>(new Term[0], Sugar.<Term[]>list(new Term[0]), null);
        int i = 0;
        int numVars = query.variables().size();
        double logEstModelCount = 0;
        while (i < numVars) {
            retVal = blackBoxSampler(query, example, retVal.r, retVal.s, subsampleSize, l);
            logEstModelCount += Math.log(retVal.t);
            i += l;
        }
        Collections.shuffle(retVal.s, this.random);
        if (retVal.s.size() > subsampleSize) {
            retVal.s = retVal.s.subList(0, subsampleSize);
        }
        return new Triple<Term[], List<Term[]>, Double>(retVal.r, retVal.s, logEstModelCount);
    }

    /**
     * Roughly based on Ermon, Gomes, Selman: "Uniform Solution Sampling Using a Constraint Solver As an Oracle"
     */
    private Triple<Term[], List<Term[]>, Double> blackBoxSampler(Clause query, SubsumptionEngineJ2.ClauseE example, Term[] substitutionTemplate, List<Term[]> sampledSolutions, int k, int l) {
        Term[] variableOrder = null;
        Collections.shuffle(sampledSolutions, this.random);
        List<Term[]> retValList = new ArrayList<Term[]>();
        Term[] retValSubsTemplate = new Term[Math.min(query.variables().size(), substitutionTemplate.length + l)];
        for (int j = 0; j < Math.min(k, sampledSolutions.size()); j++) {
            Term[] substitution = sampledSolutions.get(j);
            Clause restrictedQuery = LogicUtils.substitute(query, substitutionTemplate, substitution);
            //System.out.println(restrictedQuery+" -- "+Sugar.objectArrayToString(substitutionTemplate)+":"+Sugar.objectArrayToString(substitution));
            SubsumptionEngineJ2.ClauseC queryC = this.createClauseC(restrictedQuery);
            Pair<Term[], List<Term[]>> sols;
            if (j == 0) {
                sols = engine.allSolutions(queryC, example, Integer.MAX_VALUE, l);
                variableOrder = engine.lastVariableOrder(queryC);
                System.arraycopy(substitutionTemplate, 0, retValSubsTemplate, 0, substitutionTemplate.length);
                System.arraycopy(variableOrder, 0, retValSubsTemplate, substitutionTemplate.length, Math.min(l, variableOrder.length));
            } else {
                sols = engine.allSolutions(queryC, example, Integer.MAX_VALUE, l, variableOrder);
            }

            for (Term[] s : sols.s) {
                Term[] pseudoSol = new Term[substitution.length + Math.min(l, variableOrder.length)];
                System.arraycopy(substitution, 0, pseudoSol, 0, substitution.length);
                System.arraycopy(s, 0, pseudoSol, substitution.length, Math.min(l, variableOrder.length));
                retValList.add(pseudoSol);
            }
        }
        double avgSols = retValList.size() / (double) Math.min(k, sampledSolutions.size());
        return new Triple<Term[], List<Term[]>, Double>(retValSubsTemplate, retValList, avgSols);
    }

    public Set<Set<Term>> allImages(Clause query, int exampleIndex, int maxCard) {
        return this.allImages(query, this.examples.get(exampleIndex), maxCard);
    }

    public Set<Set<Term>> allImages(Clause query, Clause db, int maxCard) {
        return this.allImages(query, createClauseE(db), maxCard);
    }

    private Set<Set<Term>> allImages(Clause query, SubsumptionEngineJ2.ClauseE clauseE, int maxCard) {
        List<Variable> varsInQuery = Sugar.listFromCollections(query.variables());
        if (!subsumption(createClauseC(query), clauseE)) {
            return new HashSet<Set<Term>>();
        }
        Literal cardLiteral = new Literal(SpecialVarargPredicates.MAX_CARD, varsInQuery.size() + 1);
        cardLiteral.set(Constant.construct(String.valueOf(maxCard)), 0);
        for (int i = 1; i < cardLiteral.arity(); i++) {
            cardLiteral.set(varsInQuery.get(i - 1), i);
        }
        query = new Clause(Sugar.union(query.literals(), cardLiteral));

        SubsumptionEngineJ2.ClauseC queryC = createClauseC(query);
        if (!this.engine.solveWithResumer(queryC, clauseE, 2)) {
            return new HashSet<Set<Term>>();
        }
        Term[] termOrder = this.engine.lastVariableOrder(queryC);
        List<Variable> variableList = new ArrayList<Variable>();
        for (int i = 0; i < termOrder.length; i++) {
            if (termOrder[i] instanceof Variable) {
                variableList.add((Variable) termOrder[i]);
            }
        }
        System.out.println(variableList);

        Set<Set<Term>> partial = new HashSet<Set<Term>>();
        partial.add(new HashSet<Term>());

        for (int i = 0; i < variableList.size(); i++) {
            //System.out.println("size: "+partial.size());
            Set<Set<Term>> newPartial = new HashSet<Set<Term>>();
            for (Set<Term> set : partial) {

                List<Literal> extendedLits = new ArrayList<Literal>();
                extendedLits.addAll(query.literals());
                for (int j = 0; j < i; j++) {
                    Literal newLit = new Literal(SpecialVarargPredicates.IN, set.size() + 1);
                    newLit.set(variableList.get(j), 0);
                    int k = 1;
                    for (Term t : set) {
                        newLit.set(t, k);
                        k++;
                    }
                    extendedLits.add(newLit);
                }
                Clause extendedQuery = new Clause(extendedLits);
                long m1 = System.nanoTime();
                SubsumptionEngineJ2.ClauseC clauseC = createClauseC(extendedQuery);
                Pair<Term[], List<Term[]>> sol = engine.allSolutions(clauseC, clauseE, Integer.MAX_VALUE, 1, variableList.get(i));
                long m2 = System.nanoTime();
                //System.out.println("t: "+(m2-m1)/1e6+", "+sol.s.size());

                for (Term[] ts : sol.s) {
                    Term term = ts[0];
                    if (this.engine.subsumptionMode() == THETA_SUBSUMPTION || !set.contains(term)) {
                        Set<Term> extendedSet = Sugar.setFromCollections(set);
                        extendedSet.add(term);
                        if (extendedSet.size() <= maxCard) {
                            newPartial.add(extendedSet);
                        }
                    }
                }
            }
            partial = newPartial;
        }
        return partial;
    }

    public Set<Set<Term>> allImages(Clause query, Clause db, int maxCard, double epsilon, double delta) {
        return this.allImages(query, createClauseE(db), maxCard, epsilon, delta);
    }

    private Set<Set<Term>> allImages(Clause query, SubsumptionEngineJ2.ClauseE clauseE, int maxCard, double epsilon, double delta) {
        return null;
    }

    private Set<Set<Term>> allImages(Clause query, SubsumptionEngineJ2.ClauseE clauseE, int maxCard, boolean[][] xors) {
        List<Variable> varsInQuery = Sugar.listFromCollections(query.variables());
        if (!subsumption(createClauseC(query), clauseE)) {
            return new HashSet<Set<Term>>();
        }
        Literal cardLiteral = new Literal(SpecialVarargPredicates.MAX_CARD, varsInQuery.size() + 1);
        cardLiteral.set(Constant.construct(String.valueOf(maxCard)), 0);
        for (int i = 1; i < cardLiteral.arity(); i++) {
            cardLiteral.set(varsInQuery.get(i - 1), i);
        }
        query = new Clause(Sugar.union(query.literals(), cardLiteral));

        SubsumptionEngineJ2.ClauseC queryC = createClauseC(query);
        if (!this.engine.solveWithResumer(queryC, clauseE, 2)) {
            return new HashSet<Set<Term>>();
        }
        Term[] termOrder = this.engine.lastVariableOrder(queryC);
        List<Variable> variableList = new ArrayList<Variable>();
        for (int i = 0; i < termOrder.length; i++) {
            if (termOrder[i] instanceof Variable) {
                variableList.add((Variable) termOrder[i]);
            }
        }
        System.out.println(variableList);

        Set<Set<Term>> partial = new HashSet<Set<Term>>();
        partial.add(new HashSet<Term>());

        for (int i = 0; i < variableList.size(); i++) {
            System.out.println("size: " + partial.size());
            Set<Set<Term>> newPartial = new HashSet<Set<Term>>();
            for (Set<Term> set : partial) {

                List<Literal> extendedLits = new ArrayList<Literal>();
                extendedLits.addAll(query.literals());
                for (int j = 0; j < i; j++) {
                    Literal newLit = new Literal(SpecialVarargPredicates.IN, set.size() + 1);
                    newLit.set(variableList.get(j), 0);
                    int k = 1;
                    for (Term t : set) {
                        newLit.set(t, k);
                        k++;
                    }
                    extendedLits.add(newLit);
                }
                Clause extendedQuery = new Clause(extendedLits);
                long m1 = System.nanoTime();
                SubsumptionEngineJ2.ClauseC clauseC = createClauseC(extendedQuery);
                Pair<Term[], List<Term[]>> sol = engine.allSolutions(clauseC, clauseE, Integer.MAX_VALUE, 1, variableList.get(i));
                long m2 = System.nanoTime();
                //System.out.println("t: "+(m2-m1)/1e6+", "+sol.s.size());

                for (Term[] ts : sol.s) {
                    Term term = ts[0];
                    if (this.engine.subsumptionMode() == THETA_SUBSUMPTION || !set.contains(term)) {
                        Set<Term> extendedSet = Sugar.setFromCollections(set);
                        extendedSet.add(term);
                        if (extendedSet.size() <= maxCard) {
                            newPartial.add(extendedSet);
                        }
                    }
                }
            }
            partial = newPartial;
        }
        return partial;
    }

    protected List<SubsumptionEngineJ2.ClauseE> examples() {
        return this.examples;
    }

    public static void main(String[] args) {
        Clause c1 = Clause.parse("Year_1(c)");
        Clause c2 = Clause.parse("Year_1(b)");
        System.out.println(new Matching().subsumption(c1, c2));

        HornClause rule = new HornClause(Clause.parse("~head,b1(c),b2(c)"));
        Clause example = Clause.parse("b1(c),b2(c)");
        Clause query1 = new Clause(LogicUtils.flipSigns(rule.body().literals()));
        Clause query2 = new Clause(rule.body().literals());  //todo next both versions work in non-ground bodies, but only this one in ground bodies, investigate why
        new Matching().subsumption(query1, example);
    }
}
