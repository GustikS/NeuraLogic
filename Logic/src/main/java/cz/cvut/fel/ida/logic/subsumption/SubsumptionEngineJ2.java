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
import cz.cvut.fel.ida.utils.math.collections.*;
import cz.cvut.fel.ida.utils.math.random.CustomRandomGenerator;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Triple;

import java.util.*;

/**
 * This class contains implementations of RelF algorithms (Kuzelka, Zelezny, Fundamenta Informaticae 2008).
 * It is not advisable to use this class directly. It is more comfortable to use the class Matching which performs
 * preprocessing of clauses etc.
 *
 * @author ondra
 */
public class SubsumptionEngineJ2 {
    private static final Term[] cacheTerm0 = new Term[0];
    private static final Term[] cacheTerm1 = new Term[1];
    private static final Term[] cacheTerm2 = new Term[2];
    private static final Term[] cacheTerm3 = new Term[3];

    private int cacheIndex = Integer.MAX_VALUE;
    private CustomPredicate cachePredicate = null;

    public static int countFC = 0;

    private int lowArity = 3;

    private ValueToIndex<String> predicatesToIntegers = new ValueToIndex<String>();

    private Set<Integer> specialPredicateIds = new HashSet<Integer>();

    private Map<String, CustomPredicate> customPredicates = new HashMap<String, CustomPredicate>();

    private Random random = new Random(Settings.seed);

    private boolean learnVariableOrder = true;

    private int exploredNodesInCurrentRestart = 0;

    private int currentCutoff = Integer.MAX_VALUE;

    private int maxRestarts = Integer.MAX_VALUE;

    private int forcedVariable = -1;

    public final static int THETA = 1, OBJECT_IDENTITY = 2;

    private int subsumptionMode = THETA;

    private int forwardCheckingFrom = 1;

    private int arcConsistencyFrom = 6;

    private int[] firstVariableOrder;

    private int[] lastVariableOrder;

    private IntegerFunction restartSequence = new IntegerFunction.ConstantFunction(Integer.MAX_VALUE);

    private boolean solvedWithoutSearch = false;

    private int numberOfLastRestart = -1;

    private long timeout = Long.MAX_VALUE;

    protected FixedValueToIndex<Term> termsToIntegers = new FixedValueToIndex<Term>();

    protected ValueToIndex<String> typesToIntegers = new ValueToIndex<String>();

    private final static int NORMAL_PREDICATE = 1, COMPLETELY_SYMMETRIC_PREDICATE = 2, SPECIAL_PREDICATE = 4;

    private LinkedHashSet<SolutionConsumer> solutionConsumers = new LinkedHashSet<SolutionConsumer>();

    private Map<Integer, Number> numbers = new HashMap<Integer, Number>();

    //this is for speed - so that we could just be checking integer identifiers
    private final static int alldiff = -1, neq = -2, eq = -3, leq = -4, lt = -5, geq = -6, gt = -7, maxcard = -8, in = -9,
            anypred = -10, truepred = -11, falsepred = -12, next = -13, add = -14, sub = -15, mod = -16;

    public SubsumptionEngineJ2() {
        this.predicatesToIntegers.put(alldiff, SpecialVarargPredicates.ALLDIFF);
        this.predicatesToIntegers.put(neq, SpecialBinaryPredicates.NEQ);
        this.predicatesToIntegers.put(eq, SpecialBinaryPredicates.EQ);
        this.predicatesToIntegers.put(leq, SpecialBinaryPredicates.LEQ);
        this.predicatesToIntegers.put(lt, SpecialBinaryPredicates.LT);
        this.predicatesToIntegers.put(geq, SpecialBinaryPredicates.GEQ);
        this.predicatesToIntegers.put(gt, SpecialBinaryPredicates.GT);
        this.predicatesToIntegers.put(next, SpecialBinaryPredicates.NEXT);
        this.predicatesToIntegers.put(maxcard, SpecialVarargPredicates.MAX_CARD);
        this.predicatesToIntegers.put(in, SpecialVarargPredicates.IN);
        this.predicatesToIntegers.put(anypred, SpecialVarargPredicates.ANYPRED);
        this.predicatesToIntegers.put(truepred, SpecialVarargPredicates.TRUE);
        this.predicatesToIntegers.put(falsepred, SpecialVarargPredicates.FALSE);
        this.predicatesToIntegers.put(add, SpecialVarargPredicates.ADD);
        this.predicatesToIntegers.put(sub, SpecialVarargPredicates.SUB);
        this.predicatesToIntegers.put(mod, SpecialVarargPredicates.MOD);
        for (String specialBinaryPredicate : SpecialBinaryPredicates.SPECIAL_PREDICATES) {
            this.specialPredicateIds.add(this.predicatesToIntegers.valueToIndex(specialBinaryPredicate));
        }
        for (String specialVarargPredicate : SpecialVarargPredicates.SPECIAL_PREDICATES) {
            this.specialPredicateIds.add(this.predicatesToIntegers.valueToIndex(specialVarargPredicate));
        }
    }

    /**
     * Computes all solutions to the subsumption problem "c theta-subsumes e"
     *
     * @param c hypothesis
     * @param e example
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSolutions(Clause c, Clause e) {
        return allSolutions(c, e, Integer.MAX_VALUE);
    }

    /**
     * Computes all solutions to the subsumption problem "c theta-subsumes e"
     *
     * @param c        hypothesis
     * @param e        example
     * @param maxCount maximum number of solutions that we want to get
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSolutions(Clause c, Clause e, int maxCount) {
        return allSolutions(new ClauseC(c), new ClauseE(e), maxCount);
    }

    /**
     * Computes all solutions to the subsumption problem "c theta-subsumes e"
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
    public Pair<Term[], List<Term[]>> allSolutions(Clause c, Clause e, int maxCount, int depth) {
        return allSolutions(new ClauseC(c), new ClauseE(e), maxCount, depth);
    }

    /**
     * Computes all solutions to the subsumption problem "c theta-subsumes e"
     *
     * @param c hypothsis
     * @param e example
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSolutions(ClauseC c, ClauseE e) {
        return allSolutions(c, e, Integer.MAX_VALUE);
    }

    /**
     * Computes all solutions to the subsumption problem "c theta-subsumes e"
     *
     * @param c        hypothesis
     * @param e        example
     * @param maxCount maximum number of solutions that we want to get
     * @return pair: the first element is an array of variables, the second element is a list
     * of arrays of terms - each such array represents one solution of the subsumption problem.
     * The terms iterable the arrays are substitutions to the respective variables listed iterable the array which
     * is the first element iterable the pair.
     */
    public Pair<Term[], List<Term[]>> allSolutions(ClauseC c, ClauseE e, int maxCount) {
        return allSolutions(c, e, maxCount, Integer.MAX_VALUE);
    }

    public Pair<Term[], List<Term[]>> allSolutions(ClauseC c, ClauseE e, int maxCount, int maxDepth) {
        return allSolutions_impl(c, e, maxCount, maxDepth, null, -1);
    }

    public Pair<Term[], List<Term[]>> allSolutions(ClauseC c, ClauseE e, int maxCount, int maxDepth, Term firstVariable) {
        return allSolutions_impl(c, e, maxCount, maxDepth, null, c.variablesToIntegers.valueToIndex(firstVariable));
    }

    public Pair<Term[], List<Term[]>> allSolutions(ClauseC c, ClauseE e, int maxCount, int maxDepth, Term[] variableOrder) {
        int[] intVariableOrder = new int[c.containedIn.length];
        int i = 0;
        Set<Term> used = new HashSet<Term>();
        for (; i < variableOrder.length; i++) {
            intVariableOrder[i] = c.variablesToIntegers.valueToIndex(variableOrder[i]);
            used.add(variableOrder[i]);

        }
        for (Term t : c.variablesToIntegers.values()) {
            if (!used.contains(t)) {
                intVariableOrder[i] = c.variablesToIntegers.valueToIndex(t);
                i++;
            }
        }

        return allSolutions_impl(c, e, maxCount, maxDepth, intVariableOrder, -1);
    }

    private Pair<Term[], List<Term[]>> allSolutions_impl(ClauseC c, ClauseE e, int maxCount, int maxDepth, int[] variableOrder, int firstVariable) {
        if (variableOrder != null && firstVariable != -1) {
            throw new IllegalArgumentException();
        }
        if (!initialUnsatCheck(c, e) || !c.initialize(e) || (this.arcConsistencyFrom <= 0 && !arcConsistencyOnProjection(c, e))) {
            this.solvedWithoutSearch = true;
            Term[] template = new Term[c.containedIn.length];
            for (int i = 0; i < template.length; i++) {
                template[i] = c.variablesToIntegers.indexToValue(i);
            }
            return new Pair<Term[], List<Term[]>>(template, new ArrayList<Term[]>(1));
        }
        List<Term[]> solutions = new ArrayList<Term[]>();
        if (variableOrder == null) {
            variableOrder = variableOrder(c, e, firstVariable, false);
        }
        Term[] template = new Term[c.numActualVariables()];
        int j = 0;
        for (int i = 0; i < variableOrder.length; i++) {
            if (!c.isConstant(variableOrder[i])) {
                template[j] = c.variablesToIntegers.indexToValue(variableOrder[i]);
                j++;
            }
        }
        this.solvedWithoutSearch = false;
        solveAll(c, e, 0, 0, variableOrder, new HashSet<Integer>(), template, solutions, maxCount, maxDepth);
        return new Pair<Term[], List<Term[]>>(template, solutions);
    }

    private Boolean solveAll(ClauseC c, ClauseE e, int varIndex, int numActualVarsProcessed, int[] variableOrder, Set<Integer> oiSet, Term[] template, List<Term[]> solutions, int maxCount, int maxDepth) {
        while (varIndex < variableOrder.length && c.isConstant(variableOrder[varIndex])) {
            varIndex++;
        }
        if (varIndex == variableOrder.length) {
            Term[] solution = new Term[c.numActualVariables()];
            int j = 0;
            for (int i = 0; i < variableOrder.length; i++) {
                if (!c.isConstant(variableOrder[i])) {
                    solution[j] = termsToIntegers.indexToValue(c.groundedValues[variableOrder[i]]);
                    j++;
                }
            }
//            System.out.print(".");
            solutions.add(solution);
            for (SolutionConsumer consumer : solutionConsumers) {
                consumer.solution(template, solution);
            }
            return Boolean.TRUE;
        }
        int[] valueOrder;
        if (numActualVarsProcessed >= maxDepth || maxCount < Integer.MAX_VALUE) {
            valueOrder = valueOrder(c, e, variableOrder[varIndex], 1);
        } else {
            valueOrder = c.variableDomains[variableOrder[varIndex]].values();
        }
        outerLoop:
        for (int i = 0; i < valueOrder.length; i++) {
            if (solutions.size() >= maxCount) {
                return Boolean.TRUE;
            }
            if (this.subsumptionMode == OBJECT_IDENTITY && !c.isConstant(variableOrder[varIndex]) && oiSet.contains(valueOrder[i])) {
                continue;
            }
            Boolean result = null;
            IntegerSet[] oldDomains = c.oldDomains();
            if (c.groundFC(variableOrder[varIndex], valueOrder[i], e)) {
                if (this.subsumptionMode == OBJECT_IDENTITY && !c.isConstant(variableOrder[varIndex])) {
                    oiSet.add(valueOrder[i]);
                }
                int j = 0;
                for (GlobalConstraint gc : c.globalConstraints) {
                    IntegerSet[] newDomains = gc.propagate(c.variableDomains);
                    if (newDomains == null) {
                        for (int k = 0; k <= j; k++) {
                            c.globalConstraints.get(k).undoPropagation();
                        }
                        oiSet.remove(valueOrder[i]);
                        c.unground(variableOrder[varIndex]);
                        c.restoreDomains(oldDomains);
                        //System.out.println("Backtrack: "+varIndex);
                        continue outerLoop;
                    }
                    c.variableDomains = newDomains;
                    j++;

                }
                result = solveAll(c, e, varIndex + 1, numActualVarsProcessed + (c.isConstant(variableOrder[varIndex]) ? 0 : 1), variableOrder, oiSet, template, solutions, maxCount, maxDepth);
                if (this.subsumptionMode == OBJECT_IDENTITY && !c.isConstant(variableOrder[varIndex])) {
                    oiSet.remove(valueOrder[i]);
                }
                for (GlobalConstraint gc : c.globalConstraints) {
                    gc.undoPropagation();
                }
            }

            c.unground(variableOrder[varIndex]);
            c.restoreDomains(oldDomains);
            if (result != null && result.booleanValue() && numActualVarsProcessed >= maxDepth) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Solves subsumption problem "c theta-subsumes e" using Resumer1
     *
     * @param c hypothesis
     * @param e example
     * @return Boolean.TRUE if subsumption has been proved iterable the given limit (time and number of backtracks),
     * false if subsumption has been disproved iterable the given limit and null otherwise
     */
    public Boolean solveWithResumer1(Clause c, Clause e) {
        return solveWithResumer(new ClauseC(c), new ClauseE(e), 1);
    }

    /**
     * Solves subsumption problem "c theta-subsumes e" using Resumer2
     *
     * @param c hypothesis
     * @param e example
     * @return Boolean.TRUE if subsumption has been proved iterable the given limit (time and number of backtracks),
     * false if subsumption has been disproved iterable the given limit and null otherwise
     */
    public Boolean solveWithResumer2(Clause c, Clause e) {
        return solveWithResumer(new ClauseC(c), new ClauseE(e), 2);
    }

    /**
     * Solves subsumption problem "c theta-subsumes e" using Resumer1
     *
     * @param c hypothesis
     * @param e example
     * @return Boolean.TRUE if subsumption has been proved iterable the given limit (time and number of backtracks),
     * false if subsumption has been disproved iterable the given limit and null otherwise
     */
    public Boolean solveWithResumer1(ClauseC c, ClauseE e) {
        return solveWithResumer(c, e, 1);
    }

    /**
     * Solves subsumption problem "c theta-subsumes e" using Resumer2
     *
     * @param c hypothesis
     * @param e example
     * @return Boolean.TRUE if subsumption has been proved iterable the given limit (time and number of backtracks),
     * Boolean.FALSE if subsumption has been disproved iterable the given limit and null otherwise
     */
    public Boolean solveWithResumer2(ClauseC c, ClauseE e) {
        return solveWithResumer(c, e, 2);
    }

    /**
     * Solves subsumption problem "c theta-subsumes e" using specified version of Resumer
     *
     * @param c           hypothesis
     * @param e           example
     * @param resumerType resumer type: 1, 2 or 3
     * @return Boolean.TRUE if subsumption has been proved iterable the given limit (time and number of backtracks),
     * Boolean.FALSE if subsumption has been disproved iterable the given limit and null otherwise
     */
    public Boolean solveWithResumer(Clause c, Clause e, int resumerType) {
        return solveWithResumer(new ClauseC(c), new ClauseE(e), resumerType);
    }

    /**
     * @param cs             hypothesis iterable the form of an instance ClauseStructure
     * @param e              example
     * @param resumerVersion version of Resumer: 1, 2 or 3
     * @return Boolean.TRUE if subsumption has been proved iterable the given limit (time and number of backtracks),
     * Boolean.FALSE if subsumption has been disproved iterable the given limit and null otherwise
     */
    public Boolean solveWithResumer(ClauseStructure cs, ClauseE e, int resumerVersion) {
        if (resumerVersion >= 3) {
            throw new UnsupportedOperationException();
        }
        this.numberOfLastRestart = 0;
        if (!initialUnsatCheck(cs, e)) {
            this.solvedWithoutSearch = true;
            return Boolean.FALSE;
        }
        ClauseC c = null;
        if (resumerVersion <= 2) {
            c = (ClauseC) cs;
        }

        Boolean success = null;
        IntegerSet[] oldDomains = null;
        int restart = 1;
        boolean ac = false;
        long deadline = Long.MAX_VALUE;
        if (this.timeout != Long.MAX_VALUE) {
            deadline = System.currentTimeMillis() + this.timeout;
        }
        if (!cs.initialize(e)) {
            this.solvedWithoutSearch = true;
            this.numberOfLastRestart = restart;
            return Boolean.FALSE;
        } else if (c.literals.length == 0) {
            this.solvedWithoutSearch = true;
            return Boolean.TRUE;
        }
        do {
            exploredNodesInCurrentRestart = 0;
            currentCutoff = restartSequence.f(restart) + 2 * c.variableDomains.length;
            int[] variableOrder;
            if (resumerVersion > 1 && restart % 2 == 0 && forcedVariable != -1) {
                variableOrder = variableOrder(c, e, forcedVariable, subsumptionMode == THETA);
            } else {
                variableOrder = variableOrder(c, e, subsumptionMode == THETA);
            }
            c.unground();
            if (oldDomains != null) {
                c.restoreDomains(oldDomains);
            }
            if (!ac && restart >= getArcConsistencyFrom()) {
                if (!arcConsistencyOnProjection(c, e)) {
                    this.numberOfLastRestart = restart;
                    return false;
                }
                ac = true;
                oldDomains = c.oldDomains();
            }
            Term[] template = new Term[variableOrder.length];
            if (cs instanceof ClauseC) {
                success = solveR(c, e, 0, variableOrder, restart, new HashSet<Integer>(), template, deadline);
            }
            //System.out.println("explored nodes: "+this.exploredNodesInCurrentRestart);
        } while (success == null && restart++ < maxRestarts && (System.currentTimeMillis() < deadline));
        this.solvedWithoutSearch = false;
        if (success == null) {
            this.firstVariableOrder = null;
        }
        this.numberOfLastRestart = restart;
        return success;
    }

    /**
     * Sets the sequence of "tries" iterable restarts of the subsumption algorithms.
     * For example if we want to have a sequence of restarts increasing exponentially as 10 exp(index) + 100
     * then we can use new new IntegerFunction.Exponential(10, 1, 100);
     *
     * @param f restart sequence
     */
    public void setRestartSequence(IntegerFunction f) {
        this.restartSequence = f;
    }

    public IntegerFunction getRestartSequence() {
        return this.restartSequence;
    }

    /**
     * Sets the timeout after which subsumption is considered undecided (iterable milliseconds)
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return this.timeout;
    }

    private boolean initialUnsatCheck(ClauseStructure c, ClauseE e) {
        if (c.contradiction()) {
            return false;
        }
        if (!c.predicates().isSubsetOf(e.predicates)) {
            return false;
        }
        return true;
    }

    private Boolean solveR(ClauseC c, ClauseE e, int varIndex, int[] variableOrder, int restart, Set<Integer> oiSet, Term[] template, long deadline) {
        while (varIndex < variableOrder.length && c.isConstant(variableOrder[varIndex])) {
            varIndex++;
        }
        if (varIndex == variableOrder.length) {
            return Boolean.TRUE;
        }
        if (exploredNodesInCurrentRestart++ >= currentCutoff || (exploredNodesInCurrentRestart % 100 == 0 && System.currentTimeMillis() >= deadline)) {
            return null;
        }
        int[] valueOrder = valueOrder(c, e, variableOrder[varIndex], restart);
        for (int i = 0; i < valueOrder.length; i++) {
            IntegerSet[] oldDomains = c.oldDomains();
            if (this.subsumptionMode == OBJECT_IDENTITY && !c.isConstant(variableOrder[varIndex]) && oiSet.contains(valueOrder[i])) {
                continue;
            }
            if ((restart < this.getForwardCheckingFrom() && c.ground(variableOrder[varIndex], valueOrder[i], e)) ||
                    (restart >= this.getForwardCheckingFrom() && c.groundFC(variableOrder[varIndex], valueOrder[i], e))) {
                if (this.subsumptionMode == OBJECT_IDENTITY && !c.isConstant(variableOrder[varIndex])) {
                    oiSet.add(valueOrder[i]);
                }
                Boolean success = solveR(c, e, varIndex + 1, variableOrder, restart, oiSet, template, deadline);
                if (success == null) {
                    return null;
                } else if (success.booleanValue()) {
                    return true;
                }
                if (this.subsumptionMode == OBJECT_IDENTITY && !c.isConstant(variableOrder[varIndex])) {
                    oiSet.remove(valueOrder[i]);
                }
            } else {
                forcedVariable = variableOrder[varIndex];
            }
            c.unground(variableOrder[varIndex]);
            c.restoreDomains(oldDomains);
        }
        return Boolean.FALSE;
    }

    private int[] valueOrder(ClauseC c, ClauseE e, int variable, int restart) {
        if (c.groundedValues[variable] == -1) {
            int[] array = null;
            if (restart == 1) {
                array = c.variableDomains[variable].values();
            } else {
                array = VectorUtils.copyArray(c.variableDomains[variable].values());
                VectorUtils.shuffle(array, random);
            }
            return array;
        } else {
            return new int[]{c.groundedValues[variable]};
        }
    }

    private int[] variableOrder(ClauseC c, ClauseE e, boolean ignoreSingletons) {
        return variableOrder(c, e, -1, ignoreSingletons);
    }

    private int[] variableOrder(ClauseC c, ClauseE e, int fv, boolean ignoreSingletons) {
        if (c.containedIn.length == 0) {
            return new int[]{};
        }
        if (this.learnVariableOrder && this.firstVariableOrder != null) {
            int[] ret = this.firstVariableOrder;
            this.firstVariableOrder = null;
            this.lastVariableOrder = ret;
            return ret;
        }

        List<Integer> variableOrder = new ArrayList<Integer>();
        double[] weights = new double[c.containedIn.length];
        int index = 0;
        for (IntegerSet containedIn : c.containedIn) {
            weights[index] = containedIn.size();
            weights[index] /= (double) c.variableDomains[index].size();
            index++;
        }
        double[] heuristic1 = new double[c.containedIn.length];
        if (fv == -1) {
            CustomRandomGenerator crg = new CustomRandomGenerator(weights, random);
            variableOrder.add(crg.nextInt());
        } else {
            variableOrder.add(fv);
        }
        heuristic1[variableOrder.get(0)] = -1;
        for (int ci : c.containedIn[variableOrder.get(0)].values()) {
            for (int i = 0; i < c.literals[ci + 1]; i++) {
                if (heuristic1[c.literals[ci + 3 + i]] != -1) {
                    heuristic1[c.literals[ci + 3 + i]] += weights[c.literals[ci + 3 + i]];
                }
            }
        }
        for (int i = 1; i < heuristic1.length; i++) {
            //System.out.println(VectorUtils.doubleArrayToString(heuristic1));
            int selected = maxIndexWithTieBreaking(heuristic1);
            heuristic1[selected] = -1;
            if (!ignoreSingletons || c.occurrences[selected] > 1) {
                variableOrder.add(selected);
            }
            for (int ci : c.containedIn[selected].values()) {
                double count = e.getPredicateCount(c.literals[ci]);
                if (count == 0) {
                    //todo - handle separately special predicateNames and negations
                    count = 1e8;
                }

                for (int j = 0; j < c.literals[ci + 1]; j++) {
                    if (heuristic1[c.literals[ci + 3 + j]] != -1) {
                        heuristic1[c.literals[ci + 3 + j]] += weights[c.literals[ci + 3 + j]] / count;
                    }
                }
            }
        }

        this.lastVariableOrder = VectorUtils.toIntegerArray(variableOrder);
        return this.lastVariableOrder;
    }

    /**
     * @return the last ordering of variables used by the algorithm
     */
    public int[] getLastVariableOrder() {
        return this.lastVariableOrder;
    }

    /**
     * Sets the initial ordering of variables (normally, this ordering is gotten from a heuristic function),
     *
     * @param order order of variables represented by their indices iterable the data structure ClauseC
     */
    public void setFirstVariableOrder(int[] order) {
        this.firstVariableOrder = order;
    }

    private int maxIndexWithTieBreaking(double values[]) {
        double max = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;
        int index = 0;
        int countOfEqualValues = 0;
        int[] equal = new int[values.length];
        for (double value : values) {
            if (value > max) {
                max = value;
                maxIndex = index;
                countOfEqualValues = 0;
            } else if (value == max) {
                if (countOfEqualValues == 0) {
                    equal[countOfEqualValues] = maxIndex;
                    countOfEqualValues++;
                }
                equal[countOfEqualValues] = index;
                countOfEqualValues++;
            }
            index++;
        }
        if (countOfEqualValues > 0) {
            return equal[random.nextInt(countOfEqualValues)];
        }
        return maxIndex;
    }

    /**
     * Sets the subsumption mode. Aside from normal theta-subsumption, the class can work with OI-subsumption and a special version of OI subsumption.
     *
     * @param subsumptionMode can be one of the following THETA = 1, OBJECT_IDENTITY = 2, SELECTIVE_OBJECT_IDENTITY = 3;
     */
    public void setSubsumptionMode(int subsumptionMode) {
        this.subsumptionMode = subsumptionMode;
    }

    public int subsumptionMode() {
        return this.subsumptionMode;
    }

    /**
     * @return true if the last solved problem has been solved without the backtracking search
     */
    public boolean solvedWithoutSearch() {
        return this.solvedWithoutSearch;
    }

    /**
     * Sets the maximum number of restarts after which the algorithm gives up and returns null instead of TRUE or FALSE.
     *
     * @param maxRestarts the maximum number of restarts
     */
    public void setMaxRestarts(int maxRestarts) {
        this.maxRestarts = maxRestarts;
    }

    /**
     * Sets the number of first restart iterable which forward-checking is used.
     *
     * @param forwardCheckingFrom the index of the first restart iterable which forward-checking is used
     */
    public void setForwardCheckingFrom(int forwardCheckingFrom) {
        this.forwardCheckingFrom = forwardCheckingFrom;
    }

    private boolean arcConsistencyOnProjection(ClauseC clauseC, ClauseE clauseE) {
        Stack<Triple<Integer, Integer, Integer>> stack = new Stack<Triple<Integer, Integer, Integer>>();
        Map<Integer, Set<Integer>> domains = new HashMap<Integer, Set<Integer>>();
        Set<Triple<Integer, Integer, Integer>> pairs = new HashSet<Triple<Integer, Integer, Integer>>();
        for (int i = 0; i < clauseC.literals.length; i += clauseC.literals[i + 1] + 3) {
            if (clauseC.literals[i + 1] > 1) {
                for (int j = 0; j < clauseC.literals[i + 1]; j++) {
                    for (int k = 0; k < clauseC.literals[i + 1]; k++) {
                        if (clauseC.literals[i + 3 + j] != clauseC.literals[i + 3 + k]) {
                            Triple<Integer, Integer, Integer> p1 = new Triple<Integer, Integer, Integer>(clauseC.literals[i + 3 + j], clauseC.literals[i + 3 + k], i);
                            if (!pairs.contains(p1)) {
                                stack.push(p1);
                                pairs.add(p1);
                            }
                            if (!domains.containsKey(clauseC.literals[i + 3 + j])) {
                                domains.put(clauseC.literals[i + 3 + j], clauseC.variableDomains[clauseC.literals[i + 3 + j]].toSet());
                            }
                        }
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            Triple<Integer, Integer, Integer> triple = stack.pop();
            pairs.remove(triple);
            int oldSize = domains.get(triple.r).size();
            Set<Integer> filteredDomain = clauseC.revise(domains.get(triple.r), triple.r, domains.get(triple.s), triple.s, clauseE, triple.t);
            if (filteredDomain.size() < oldSize) {
                if (filteredDomain.isEmpty()) {
                    return false;
                }
                for (int neighbour : clauseC.neighbours[triple.r].values()) {
                    if (neighbour != triple.r) {
                        for (int neighbLit : clauseC.containedIn[neighbour].values()) {
                            Triple<Integer, Integer, Integer> newTriple = new Triple<Integer, Integer, Integer>(neighbour, triple.r, neighbLit);
                            if (!pairs.contains(newTriple)) {
                                stack.push(newTriple);
                                pairs.add(newTriple);
                            }
                        }
                    }
                }
                domains.put(triple.r, filteredDomain);
            }
        }
        for (Map.Entry<Integer, Set<Integer>> entry : domains.entrySet()) {
            clauseC.variableDomains[entry.getKey()] = IntegerSet.createIntegerSet(entry.getValue());
        }
        return true;
    }

    /**
     * @return the arcConsistencyFrom the first restart iterable which arc-consistency is used
     */
    public int getArcConsistencyFrom() {
        return arcConsistencyFrom;
    }

    /**
     * Sets the number of first restart iterable which forward-checking is used.
     *
     * @param arcConsistencyFrom the index of the first restart iterable which arc-consistency is used
     */
    public void setArcConsistencyFrom(int arcConsistencyFrom) {
        this.arcConsistencyFrom = arcConsistencyFrom;
    }

    /**
     * @return the arcConsistencyFrom the first restart iterable which forward-checking is used
     */
    public int getForwardCheckingFrom() {
        return forwardCheckingFrom;
    }

    /**
     * @return number of restarts used iterable the last run of the algorithm.
     */
    public int getNoOfRestarts() {
        return this.numberOfLastRestart - 1;
    }

    /**
     * An interface implemented by data-structures for hypotheses (i.e. the clauses on
     * the left-hand-side of theta-subsumption).
     */
    public interface ClauseStructure {

        /**
         * initializes the data-structure for subsequent computation of theta-subsumption with example <em>clauseE</em>
         *
         * @param clauseE the example with which subsumoption will be computed
         * @return true if it was not proved without search that there cannot be subsumption
         * between the hypothsis represented by this ClauseStructure object and the example <em>clauseE</em>
         */
        public boolean initialize(ClauseE clauseE);

        /**
         * @return set of integers representing predicate symbols contained iterable this
         * ClauseStructure. It is used for quickly refuting subsumption - when
         * predicateNames() is not subset of predicateNames contained iterable <em>clauseE</em>
         */
        public IntegerSet predicates();

        public boolean contradiction();
    }

    /**
     *
     */
    public class ClauseC implements ClauseStructure {

        private boolean contradiction = false;
        //predicate, arity, terms
        protected int[] literals;

        private IntegerSet predicates;

        protected int[] variableTypes;

        protected IntegerSet[] variableDomains;

        protected int[] groundedValues;

        private int[] occurrences;

        private IntegerSet negations;

        //[term] -> literals' indices
        private IntegerSet[] containedIn;

        //[term] -> neighbours' indices iterable 'domains'
        private IntegerSet[] neighbours;

        private boolean[] constantsMask;

        private int numActualVariables;

        private int numActualConstants;

        private ValueToIndex<Term> variablesToIntegers = new ValueToIndex<Term>();

        private int[] auxBuffer1;

        private int[][] lowArityAuxBuffers;

        private boolean useFirstSuccessFC = true;

        private ArrayList<GlobalConstraint> globalConstraints = new ArrayList<GlobalConstraint>();

        /**
         * Creates a new empty ClauseC
         */
        protected ClauseC() {
        }

        /**
         * Creates a new instance of class ClauseC by compiling the given Clause c to
         * an efficient representation.
         *
         * @param c the clause on the left-hand side of theta-subsumption (i.e. hypothesis)
         */
        public ClauseC(Clause c) {
            Set<Integer> predicateSet = new HashSet<Integer>();
            int literalsArrayLength = 0;
            for (Literal l : c.literals()) {
                literalsArrayLength += 3 + l.arity();
                int predicate = predicatesToIntegers.valueToIndex(l.predicateName());
                if (!l.isNegated() && !specialPredicateIds.contains(predicate)) {
                    predicateSet.add(predicate);
                }
            }
            Set<Integer> negations = new HashSet<Integer>();
            this.predicates = IntegerSet.createIntegerSet(predicateSet);
            this.literals = new int[literalsArrayLength];
            Map<Literal, Integer> intLitMap = new HashMap<Literal, Integer>();
            Map<Integer, Literal> litIntMap = new HashMap<Integer, Literal>();
            int index = 0;
            for (Literal l : c.literals()) {
                if (l.isNegated()) {
                    negations.add(index);
                }
                literals[index] = predicatesToIntegers.valueToIndex(l.predicateName());
                literals[index + 1] = l.arity();
                if (l.predicateName().startsWith(SymmetricPredicates.PREFIX)) {
                    literals[index + 2] |= COMPLETELY_SYMMETRIC_PREDICATE;
                }
                if (specialPredicateIds.contains(literals[index])) {
                    literals[index + 2] |= SPECIAL_PREDICATE;
                    if (literals[index] == falsepred && !l.isNegated()) {
                        this.contradiction = true;
                    } else if (literals[index] == truepred && l.isNegated()) {
                        this.contradiction = true;
                    }
                }
                intLitMap.put(l, index);
                litIntMap.put(index, l);
                index += 3;
                for (int j = 0; j < l.arity(); j++) {
                    literals[index + j] = variablesToIntegers.valueToIndex(l.get(j));
                }
                index += l.arity();
            }
            this.negations = IntegerSet.createIntegerSet(negations);
            containedIn = new IntegerSet[variablesToIntegers.size()];
            occurrences = new int[variablesToIntegers.size()];
            MultiMap<Integer, Integer> containedInBag = new MultiMap<Integer, Integer>();
            for (Literal l : c.literals()) {
                for (int i = 0; i < l.arity(); i++) {
//                    if (l.get(i) instanceof Variable) {
                    containedInBag.put(variablesToIntegers.valueToIndex(l.get(i)), intLitMap.get(l));
                    occurrences[variablesToIntegers.valueToIndex(l.get(i))]++;
//                    } else if (l.get(i) instanceof Constant){
//                        occurrences[variablesToIntegers.valueToIndex(l.get(i))] = 1;
//                        //if (!containedInBag.containsKey(variablesToIntegers.valueToIndex(l.get(i)))){
//                            containedInBag.put(variablesToIntegers.valueToIndex(l.get(i)), intLitMap.get(l));
//                        //}
//                    }
                }
            }
            for (Map.Entry<Integer, Set<Integer>> entry : containedInBag.entrySet()) {
                int term = entry.getKey();
                containedIn[term] = IntegerSet.createIntegerSet(entry.getValue());
            }
            neighbours = new IntegerSet[containedIn.length];
            for (int i = 0; i < containedIn.length; i++) {
                Set<Integer> set = new HashSet<Integer>();
                for (int literalsIndex : containedIn[i].values()) {
                    for (int j = literalsIndex + 3; j < literalsIndex + 3 + literals[literalsIndex + 1]; j++) {
                        if (literals[j] != i) {
                            set.add(literals[j]);
                        }
                    }
                }
                neighbours[i] = IntegerSet.createIntegerSet(set);
            }
            variableDomains = new IntegerSet[c.terms().size()];
            groundedValues = new int[c.terms().size()];
            constantsMask = new boolean[c.terms().size()];
            Arrays.fill(groundedValues, -1);
            for (int i = 0; i < groundedValues.length; i++) {
                if (variablesToIntegers.indexToValue(i) instanceof Constant) {
                    groundedValues[i] = termsToIntegers.valueToIndex(variablesToIntegers.indexToValue(i));
                    constantsMask[i] = true;
                    numActualConstants++;
                } else {
                    numActualVariables++;
                }
            }
            variableTypes = new int[variablesToIntegers.size()];
            for (Term t : variablesToIntegers.values()) {
                if (t.type() != null && t instanceof Variable) {
                    variableTypes[variablesToIntegers.valueToIndex(t)] = typesToIntegers.valueToIndex(t.type());
                } else {
                    variableTypes[variablesToIntegers.valueToIndex(t)] = -1;
                }
            }
            auxBuffer1 = new int[this.variableDomains.length];
            lowArityAuxBuffers = new int[lowArity + 1][];
            for (int i = 0; i <= lowArity; i++) {
                lowArityAuxBuffers[i] = new int[i + 2];
            }
        }

        public boolean initialize(ClauseE e) {
            Arrays.fill(this.variableDomains, null);
            //simple propagation of "@in" literals
            int k = 0;
            while (k < this.literals.length) {
                if (predicatesToIntegers.indexToValue(this.literals[k]).equals(SpecialVarargPredicates.IN)) {
                    boolean groundRightPart = true;
                    int arity = this.literals[k + 1];
                    for (int j = 1; j < arity; j++) {
                        if (!this.isConstant(this.literals[k + j + 3])) {
                            groundRightPart = false;
                            break;
                        }
                    }
                    if (groundRightPart) {
                        int varIndex = this.literals[k + 3];
                        int[] groundValues = new int[arity - 1];
                        for (int j = 0; j < groundValues.length; j++) {
                            groundValues[j] = termsToIntegers.valueToIndex(variablesToIntegers.indexToValue(this.literals[k + j + 3 + 1]));
                        }
                        IntegerSet dom = IntegerSet.createIntegerSet(groundValues);
                        if (this.negations.contains(k)) {
                            dom = IntegerSet.difference(e.allTerms, dom);
                        }
                        if (this.variableDomains[varIndex] == null) {
                            this.variableDomains[varIndex] = dom;
                        } else {
                            this.variableDomains[varIndex] = IntegerSet.intersection(this.variableDomains[varIndex], dom);
                        }
                    }
                }
                k += this.literals[k + 1] + 3;
            }
            //
            for (int i = 0; i < this.variableDomains.length; i++) {
                if (variablesToIntegers.indexToValue(i) instanceof Variable) {
                    for (int ciLit : this.containedIn[i].values()) {
                        for (int j = 0; j < literals[ciLit + 1]; j++) {
                            if (this.literals[ciLit + j + 3] == i) {
                                if (this.variableDomains[i] == null) {
                                    if (this.negations.contains(ciLit) || specialPredicateIds.contains(this.literals[ciLit])) {
                                        if (this.variableTypes[i] == -1) {
                                            this.variableDomains[i] = e.allTerms;
                                        } else {
                                            this.variableDomains[i] = e.typedTerms(this.variableTypes[i]);
                                        }
                                    } else {
                                        if (this.variableTypes[i] == -1) {
                                            this.variableDomains[i] = e.variableDomains.get(((long) this.literals[ciLit] << 32) | (j & 0xFFFFFFFFL));
                                        } else {
                                            this.variableDomains[i] = IntegerSet.intersection(
                                                    e.variableDomains.get(((long) this.literals[ciLit] << 32) | (j & 0xFFFFFFFFL)),
                                                    e.typedTerms(this.variableTypes[i]));
                                        }
                                    }
                                } else {
                                    if (!this.negations.contains(ciLit) && !specialPredicateIds.contains(this.literals[ciLit])) {
                                        IntegerSet varDomain = e.variableDomains.get(((long) this.literals[ciLit] << 32) | (j & 0xFFFFFFFFL));
                                        if (varDomain != null) {
                                            this.variableDomains[i] = IntegerSet.intersection(varDomain, this.variableDomains[i]);
                                        } else {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    int termId = termsToIntegers.valueToIndex(variablesToIntegers.indexToValue(i));
                    this.variableDomains[i] = IntegerSet.createIntegerSet(termId);
                    if (variableTypes[i] != -1 && !e.typedTerms(variableTypes[i]).contains(termId)) {
                        this.variableDomains[i] = IntegerSet.emptySet;
                    }
                }
                if (this.variableDomains[i] == null || this.variableDomains[i].isEmpty()) {
                    return false;
                }
            }
            Arrays.fill(groundedValues, -1);
            for (int i = 0; i < groundedValues.length; i++) {
                if (variablesToIntegers.indexToValue(i) instanceof Constant) {
                    if (!ground(i, termsToIntegers.valueToIndex(variablesToIntegers.indexToValue(i)), e)) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void addGlobalConstraint(GlobalConstraint gc) {
            if (gc.needsStrongPropagation()) {
                this.useFirstSuccessFC = false;
            }
            this.globalConstraints.add(gc);
        }

        public void removeGlobalConstraints() {
            this.globalConstraints.clear();
        }


        /**
         * Substitutes the value <em>value</em> for the variable at index <em>variable</em>
         * and checks if it cannot be yet proved that there is no extension of the partial solution.
         *
         * @param variable the variable to be grounded
         * @param value    the value to be set for the variable
         * @param e        the example for which subsumption is computed
         * @return true if it could not be proved that the partial solution cannot be extended
         * (whch does not mean that it can), false otherwise i.e. when it has been proved that the
         * partial solution really cannot be extended to full solution.
         */
        protected boolean ground(int variable, int value, ClauseE e) {
            this.groundedValues[variable] = value;
            if (!useFirstSuccessFC) {
                this.variableDomains[variable] = IntegerSet.createIntegerSet(value);
            }

            final int[] values = containedIn[variable].values();
            for (int i = 0; i < values.length; i++) {
                final int ciLit = values[i];
                if (!e.checkLiteral(this, ciLit, this.negations.contains(ciLit))) {
                    return false;
                }
            }
            return true;
        }


        /**
         * Substitutes the value <em>value</em> for the variable at index <em>variable</em>
         * and checks if it cannot be yet proved using forward checking that there is no extension of the partial solution.
         *
         * @param variable the variable to be grounded
         * @param value    the value to be set for the variable
         * @param e        the example for which subsumption is computed
         * @return true if it could not be proved using forward checking that the partial solution cannot be extended
         * (whch does not mean that it can), false otherwise i.e. when it has been proved that the
         * partial solution really cannot be extended to full solution.
         */
        private boolean groundFC(int variable, int value, ClauseE e) {
            countFC++;
            if (!ground(variable, value, e)) {
                return false;
            }
            if (this.useFirstSuccessFC) {
                outerLoop:
                for (int neighb : this.neighbours[variable].values()) {
                    this.auxBuffer1[neighb] = 0;
                    if (this.groundedValues[neighb] == -1 && this.containedIn[neighb].size() > 1) {
                        for (int val : this.variableDomains[neighb].values()) {
                            boolean succ = ground(neighb, val, e);
                            unground(neighb);
                            if (succ) {
                                if (this.auxBuffer1[neighb] > 0) {
                                    this.variableDomains[neighb] = IntegerSet.createIntegerSetFromSortedArray(Arrays.copyOfRange(this.variableDomains[neighb].values(), this.auxBuffer1[neighb], this.variableDomains[neighb].size()));
                                }

                                continue outerLoop;
                            }

                            this.auxBuffer1[neighb]++;
                        }
                        return false;
                    }
                }

                return true;
            } else {
                //todo
                for (int neighb : this.neighbours[variable].values()) {
                    if (this.groundedValues[neighb] == -1 && this.containedIn[neighb].size() > 1) {
                        int[] domain = new int[this.variableDomains[neighb].size()];
                        int index = 0;
                        for (int val : this.variableDomains[neighb].values()) {
                            boolean succ = ground(neighb, val, e);
                            unground(neighb);
                            if (succ) {
                                domain[index] = val;
                                index++;
                            }
                        }
                        this.variableDomains[neighb] = IntegerSet.createIntegerSetFromSortedArray(Arrays.copyOfRange(domain, 0, index));
                        if (this.variableDomains[neighb].isEmpty()) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        //revise function of AC-3 algorithm (arc consistency)
        private Set<Integer> revise(Set<Integer> domain1, int var1, Set<Integer> domain2, int var2, ClauseE e, int literal) {
            Set<Integer> filtered = new LinkedHashSet<Integer>();
            if (groundedValues[var1] == -1 && groundedValues[var2] == -1) {
                for (Integer d1 : domain1) {
                    this.groundedValues[var1] = d1;
                    for (Integer d2 : domain2) {
                        this.groundedValues[var2] = d2;
                        if (e.checkLiteral(this, literal, this.negations.contains(literal))) {
                            filtered.add(d1);
                            unground(var2);
                            break;
                        }
                        unground(var2);
                    }
                    unground(var1);
                }
            } else if (groundedValues[var1] > -1) {
                filtered.add(groundedValues[var1]);
            } else if (groundedValues[var1] == -1 && groundedValues[var2] > -1) {
                for (Integer d1 : domain1) {
                    this.groundedValues[var1] = d1;
                    if (e.checkLiteral(this, literal, this.negations.contains(literal))) {
                        filtered.add(d1);
                    }
                    unground(var1);
                }
            } else {
                return domain1;
            }
            return filtered;
        }

        /**
         * Restores domains of variables to values contained iterable <em>oldDomains</em>
         *
         * @param oldDomains the values that should be restored
         */
        protected void restoreDomains(IntegerSet[] oldDomains) {
            this.variableDomains = oldDomains;
        }

        /**
         * @return creates a copy of domains of this ClauseC, these can later be used
         * iterable restoreDomains(...)
         */
        protected IntegerSet[] oldDomains() {
            IntegerSet[] oldDoms = new IntegerSet[this.variableDomains.length];
            System.arraycopy(this.variableDomains, 0, oldDoms, 0, oldDoms.length);
            return oldDoms;
        }

        protected void unground() {
            for (int i = 0; i < this.groundedValues.length; i++) {
                unground(i);
            }
        }

        /**
         * Ungrounds variable <em>variable</em>.
         *
         * @param variable the variable to be unground
         */
        protected void unground(int variable) {
            if (this.variablesToIntegers.indexToValue(variable) instanceof Variable) {
                this.groundedValues[variable] = -1;
            }
        }

        @Override
        public String toString() {
            return toClause().toString();
        }

        /**
         * @return original clause represented by this ClauseC object
         */
        public Clause toOriginalClause() {
            List<Literal> lits = new ArrayList<Literal>();
            for (int i = 0; i < literals.length; i += literals[i + 1] + 3) {
                Literal l = new Literal(predicatesToIntegers.indexToValue(literals[i]), this.negations.contains(i), literals[i + 1]);
                for (int j = 0; j < literals[i + 1]; j++) {
                    l.set(variablesToIntegers.indexToValue(literals[i + 3 + j]), j);
                }
                lits.add(l);
            }
            return new Clause(lits);
        }

        /**
         * @return representation of this ClauseC object as an instance of class Clause
         */
        public Clause toClause() {
            List<Literal> lits = new ArrayList<Literal>();
            for (int i = 0; i < literals.length; i += literals[i + 1] + 3) {
                Literal l = new Literal(predicatesToIntegers.indexToValue(literals[i]), this.negations.contains(i), literals[i + 1]);
                for (int j = 0; j < literals[i + 1]; j++) {
                    if (groundedValues[literals[i + 3 + j]] != -1) {
                        l.set(Constant.construct(String.valueOf(groundedValues[literals[i + 3 + j]])), j);
                    } else {
                        l.set(variablesToIntegers.indexToValue(literals[i + 3 + j]), j);
                    }
                }
                lits.add(l);
            }
            return new Clause(lits);
        }

        /**
         * @return the set of predicateNames (represented as integers) contained iterable this ClauseC
         */
        public IntegerSet predicates() {
            return this.predicates;
        }

        /**
         * [template,assignment]
         *
         * @param example the example for which subsumption is computed
         * @return Pair:  the first element is an array of variables, the second element is a an
         * array of terms - represents the groundings of the variables.
         * The terms iterable the second array are substitutions to the respective variables listed iterable the array which
         * is the first element in the pair.
         */
        public Pair<Term[], Term[]> getVariableAssignment(ClauseE example) {
            Term[] template = new Term[this.groundedValues.length];
            Term[] assignment = new Term[this.groundedValues.length];
            for (int i = 0; i < template.length; i++) {
                template[i] = this.variablesToIntegers.indexToValue(i);
                assignment[i] = termsToIntegers.indexToValue(this.groundedValues[i]);
            }
            return new Pair<Term[], Term[]>(template, assignment);
        }

        public boolean isConstant(int index) {
            return constantsMask[index];
        }

        public int numActualVariables() {
            return numActualVariables;
        }

        public int numActualConstants() {
            return numActualConstants;
        }

        public boolean contradiction() {
            return contradiction;
        }
    }

    /**
     *
     */
    public class ClauseE {

        protected Map<Long, IntegerSet> variableDomains;

        private IntegerMultiMap<Integer> typedTerms;

        protected int[] literals;

        private int[] predicateCounts;

        private IntegerSet allTerms;

        private IntegerSet predicates;

        private LowArityLiterals lal;

        private HighArityLiterals hal;

        private CompletelySymmetricLiterals csl;

        private int minPredicateCounter = Integer.MAX_VALUE;

        private int maxPredicateCounter = Integer.MIN_VALUE;

        /**
         * Creates a new instance of class ClauseE which serves as an efficient data-structure
         * for storing the clauses that are on the right-hand side of theta-subsumption relation (i.e. the examples).
         *
         * @param clause the clause which should be compiled into the efficient representation
         */
        public ClauseE(Clause clause) {
            // Pre-calculate clause structure to avoid redundant iterations
            this.expand(clause.literals());
        }

        public void expand(Set<Literal> clauseLiterals) {
            // First pass: collect predicates and calculate array sizes
            Set<Integer> predicateSet = new HashSet<>(clauseLiterals.size());
            int literalArraySize = 0;

            for (Literal l : clauseLiterals) {
                if (!l.isNegated()) {
                    predicateSet.add(predicatesToIntegers.valueToIndex(l.predicateName()));
                    literalArraySize += 2 + l.arity();
                }
            }

            IntegerSet newPredicates = IntegerSet.createIntegerSet(predicateSet);
            this.predicates = this.predicates == null ? newPredicates : IntegerSet.union(this.predicates, newPredicates);

            // Second pass: build literals array and collect domains
            int index = 0;
            int startIndex = 0;
            int[] newLiterals = null;

            if (literals == null) {
                newLiterals = new int[literalArraySize];
            } else {
                index = literals.length;
                startIndex = index;
                newLiterals = new int[index + literalArraySize];
                System.arraycopy(literals, 0, newLiterals, 0, index);
            }

            Map<Long, Set<Integer>> varDomainsMap = new HashMap<>(literalArraySize);

            // Single pass: collect all terms and typed terms
            Set<Integer> allTermsSet = new HashSet<>(clauseLiterals.size() * 2);
            Map<Integer, Set<Integer>> typedTermsMap = new HashMap<>(clauseLiterals.size() * 2);

            for (Literal l : clauseLiterals) {
                final Term[] terms = l.arguments();
                final int arity = terms.length;

                if (l.isNegated()) {
                    for (int i = 0; i < arity; i++) {
                        int termId = termsToIntegers.valueToIndex(terms[i]);
                        allTermsSet.add(termId);

                        final String typeStr = terms[i].type();
                        if (typeStr != null) {
                            int typeId = typesToIntegers.valueToIndex(typeStr);
                            typedTermsMap.computeIfAbsent(typeId, k -> new HashSet<>(8))
                                    .add(termId);
                        }
                    }

                    continue;
                }

                final int predicateId = predicatesToIntegers.valueToIndex(l.predicateName());
                newLiterals[index] = predicateId;
                newLiterals[index + 1] = arity;
                index += 2;

                maxPredicateCounter = Integer.max(predicateId, maxPredicateCounter);
                minPredicateCounter = Integer.min(predicateId, minPredicateCounter);

                // Check once if symmetric
                boolean isSymmetric = l.predicateName().startsWith(SymmetricPredicates.PREFIX);

                if (isSymmetric) {
                    // Symmetric predicate: collect all position/term pairs
                    for (int i = 0; i < arity; i++) {
                        final long key = ((long) predicateId << 32) | (i & 0xFFFFFFFFL);
                        Set<Integer> set = varDomainsMap.computeIfAbsent(key, k -> new HashSet<>(8));

                        for (int j = 0; j < arity; j++) {
                            int termId = termsToIntegers.valueToIndex(terms[j]);
                            newLiterals[index + j] = termId;
                            set.add(termId);
                        }

                        int termId = termsToIntegers.valueToIndex(terms[i]);
//                        allTermsSet.add(termId);

                        final String typeStr = terms[i].type();
                        if (typeStr != null) {
                            int typeId = typesToIntegers.valueToIndex(typeStr);
                            typedTermsMap.computeIfAbsent(typeId, k -> new HashSet<>(8))
                                    .add(termId);
                        } else {
                            allTermsSet.add(termId);
                        }
                    }
                } else {
                    // Normal predicate: position j maps to argument j
                    for (int j = 0; j < arity; j++) {
                        int termId = termsToIntegers.valueToIndex(terms[j]);
                        newLiterals[index + j] = termId;

                        // Collect domain for position j
                        final long key = ((long) predicateId << 32) | (j & 0xFFFFFFFFL);
                        varDomainsMap.computeIfAbsent(key, k -> new HashSet<>())
                                .add(termId);

//                        allTermsSet.add(termId);

                        final String typeStr = terms[j].type();
                        if (typeStr != null) {
                            int typeId = typesToIntegers.valueToIndex(typeStr);
                            typedTermsMap.computeIfAbsent(typeId, k -> new HashSet<>())
                                    .add(termId);
                        } else {
                            allTermsSet.add(termId);
                        }
                    }
                }
                index += arity;
            }

            // Convert varDomainsMap to variableDomains in single pass
            Map<Long, IntegerSet> variableDomains = this.variableDomains == null ? new HashMap<>(varDomainsMap.size()) : this.variableDomains;
            for (Map.Entry<Long, Set<Integer>> entry : varDomainsMap.entrySet()) {
                variableDomains.merge(entry.getKey(), IntegerSet.createIntegerSet(entry.getValue()), IntegerSet::union);
            }

            this.variableDomains = variableDomains;

            // Initialize literal matchers
            if (lal == null) {
                lal = new LowArityLiterals(newLiterals, lowArity);
            } else {
                for (int i = startIndex; i < newLiterals.length; i += newLiterals[i + 1] + 2) {
                    lal.add(newLiterals, i);
                }
            }

            hal = new HighArityLiterals(newLiterals, lowArity);
            csl = new CompletelySymmetricLiterals(newLiterals);

            IntegerSet newAllTerms = IntegerSet.createIntegerSet(allTermsSet);
            this.allTerms = this.allTerms == null ? newAllTerms : IntegerSet.union(this.allTerms, newAllTerms);

            IntegerMultiMap<Integer> typedTermMM = this.typedTerms == null ? new IntegerMultiMap<>() : this.typedTerms;
            for (Map.Entry<Integer, Set<Integer>> entry : typedTermsMap.entrySet()) {
                typedTermMM.add(entry.getKey(), IntegerSet.createIntegerSet(entry.getValue()));
            }

            this.typedTerms = typedTermMM;
            this.predicateCounts = null;
            this.literals = newLiterals;
        }

        public int getPredicateCount(int predicateId) {
            if (this.predicateCounts == null) {
                this.predicateCounts = new int[maxPredicateCounter - minPredicateCounter + 1];

                for (int i = 0; i < this.literals.length; i += this.literals[i + 1] + 2) {
                    this.predicateCounts[this.literals[i] - minPredicateCounter]++;
                }
            }

            int index = predicateId - minPredicateCounter;
            if (index < 0 || index >= this.predicateCounts.length) {
                return 0;
            }

            return this.predicateCounts[index];
        }

        public IntegerSet typedTerms(int type) {
            return typedTerms.get(type);
        }

        /**
         * Checks if the literal at position <em>index</em> (which is taken
         * from ClauseC) can be extended to a literal which is contained in this ClauseE.
         *
         * @param c
         * @param index index at which the checked literal is located in the array cliterals
         * @return true of the literal can be extended so that it would be equal to some literal from this ClauseE, false otherwise
         */
        public boolean checkLiteral(ClauseC c, int index, boolean negated) {
            final int[] cliterals = c.literals;

            final boolean ground = isGround(cliterals, c.groundedValues, index);
            if (negated && !ground) {
                return true;
            }

            final int flags = cliterals[index + 2];
            final int arity = cliterals[index + 1];

            final boolean isSymmetric = (flags & COMPLETELY_SYMMETRIC_PREDICATE) > 0;
            final boolean isLowArity = arity <= lowArity;

            boolean result;

            if (isSymmetric) {
                result = this.csl.match(c, index, this, ground);
            } else if (isLowArity) {
                result = this.lal.match(c, index, this, c.lowArityAuxBuffers[arity], ground);
            } else {
                result = this.hal.match(c, index, this, ground);
            }

            return negated ? !result : result;
        }

        /**
         * @param literal the integer representation of the literal for which we want the string representation
         * @return string representation of literal represented by integer <em>literal</em>
         */
        public String literalToString(int literal) {
            StringBuilder sb = new StringBuilder();
            sb.append(predicatesToIntegers.indexToValue(literals[literal]));
            sb.append("(");
            for (int i = 0; i < literals[literal + 1]; i++) {
                sb.append(termsToIntegers.indexToValue(literals[literal + 2 + i]));
                if (i < literals[literal + 1] - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }

        public Set<Term> terms() {
            Set<Term> retVal = new HashSet<Term>();
            for (int termID : allTerms.values()) {
                retVal.add(termsToIntegers.indexToValue(termID));
            }
            return retVal;
        }

        public IntegerSet allTerms() {
            return this.allTerms;
        }
    }

    private boolean isGround(int[] cliterals, int[] grounding, int index) {
        final int arity = cliterals[index + 1];
        final int base  = index + 3;

        if (arity == 0) return true;
        for (int j = base; j < arity + base; j++) {
            if (grounding[cliterals[j]] == -1) return false;
        }
        return true;
    }

    private boolean matchCustomLiteral(int[] cliterals, int[] grounding, int index) {
        if (cliterals[index] != cacheIndex) {
            String predicate = predicatesToIntegers.indexToValue(cliterals[index]);
            this.cachePredicate = this.customPredicates.get(predicate);
            this.cacheIndex = cliterals[index];
        }

        if (this.cachePredicate == null) return true;

        final int arity = cliterals[index + 1];
        final int offset = index + 3;

        if (arity == 0) {
            return this.cachePredicate.isSatisfiable(cacheTerm0);
        }

        if (arity == 1) {
            final int termId = grounding[cliterals[offset]];
            final Term arg = termsToIntegers.indexToValue(termId);

            cacheTerm1[0] = arg;
            return this.cachePredicate.isSatisfiable(cacheTerm1);
        }

        if (arity == 2) {
            final int termId0 = grounding[cliterals[offset]];
            final Term arg0 = termsToIntegers.indexToValue(termId0);
            cacheTerm2[0] = arg0;

            final int termId1 = grounding[cliterals[offset + 1]];
            final Term arg1 = termsToIntegers.indexToValue(termId1);
            cacheTerm2[1] = arg1;
            return this.cachePredicate.isSatisfiable(cacheTerm2);
        }

        final int termId0 = grounding[cliterals[offset]];
        final Term arg0 = termsToIntegers.indexToValue(termId0);
        cacheTerm3[0] = arg0;

        final int termId1 = grounding[cliterals[offset + 1]];
        final Term arg1 = termsToIntegers.indexToValue(termId1);
        cacheTerm3[1] = arg1;

        final int termId2 = grounding[cliterals[offset + 2]];
        final Term arg2 = termsToIntegers.indexToValue(termId2);
        cacheTerm3[2] = arg2;
        return this.cachePredicate.isSatisfiable(cacheTerm3);
    }

    private boolean matchSpecialLiteral(ClauseC c, int index, ClauseE e, boolean isGround) {
        final int[] cliterals = c.literals;
        final int[] grounding = c.groundedValues;
        final int predicate = cliterals[index];

        if (predicate >= 0) {
            if (!isGround) {
                return true;
            }
            return matchCustomLiteral(cliterals, grounding, index);
        }

        final int arity = cliterals[index + 1];
        final int offset = index + 3;

        if (predicate <= leq && predicate >= gt) {
            return checkComparison(predicate, cliterals, grounding, offset);
        }

        if (predicate == truepred) return true;
        if (predicate == falsepred) return false;

        if (predicate == eq) return checkComparison(predicate, cliterals, grounding, offset);
        if (predicate == neq) return checkComparison(predicate, cliterals, grounding, offset);
        if (predicate == add) return checkAdd(cliterals, grounding, offset);
        if (predicate == alldiff) return checkAlldiff(cliterals, grounding, offset, arity, index);
        if (predicate == in) return checkIn(cliterals, grounding, offset, arity);
        if (predicate == sub) return checkSub(cliterals, grounding, offset);
        if (predicate == mod) return checkMod(cliterals, grounding, offset);
        if (predicate == next) return checkNext(cliterals, grounding, offset, isGround);
        if (predicate == maxcard) return checkMaxCard(cliterals, grounding, offset, arity);
        if (predicate == anypred) return checkAnyPred(c, e, cliterals, index, arity);

        return false;
    }

    private boolean checkComparison(int predicate, int[] cliterals, int[] grounding, int offset) {
        final int gid1 = grounding[cliterals[offset]];
        if (gid1 == -1) return true;
        final int gid2 = grounding[cliterals[offset + 1]];
        if (gid2 == -1) return true;

        final Term arg1 = termsToIntegers.indexToValue(gid1);
        final Term arg2 = termsToIntegers.indexToValue(gid2);

        if (arg1 instanceof Constant && arg2 instanceof Constant) {
            final Constant c1 = (Constant) arg1;
            final Constant c2 = (Constant) arg2;
            if (c1.isNumeric() && c2.isNumeric()) {
                final double d1 = c1.doubleValue();
                final double d2 = c2.doubleValue();
                switch (predicate) {
                    case neq: return d1 != d2;
                    case eq: return d1 == d2;
                    case gt: return d1 > d2;
                    case geq: return d1 >= d2;
                    case lt: return d1 < d2;
                    case leq: return d1 <= d2;
                }
            }
        }

        if (predicate == eq) return gid1 == gid2;
        if (predicate == neq) return gid1 != gid2;

        final String str1 = arg1.toString();
        final String str2 = arg2.toString();
        final int cmp = str1.compareTo(str2);
        switch (predicate) {
            case gt: return cmp > 0;
            case geq: return cmp >= 0;
            case lt: return cmp < 0;
            case leq: return cmp <= 0;
        }
        return false;
    }

    private boolean checkAdd(int[] cliterals, int[] grounding, int offset) {
        final int gid1 = grounding[cliterals[offset]];
        if (gid1 == -1) return true;
        final int gid2 = grounding[cliterals[offset + 1]];
        if (gid2 == -1) return true;
        final int gid3 = grounding[cliterals[offset + 2]];
        if (gid3 == -1) return true;

        final Term arg1 = termsToIntegers.indexToValue(gid1);
        final Term arg2 = termsToIntegers.indexToValue(gid2);
        final Term arg3 = termsToIntegers.indexToValue(gid3);

        if (arg1 instanceof Constant && arg2 instanceof Constant && arg3 instanceof Constant) {
            final Constant c1 = (Constant) arg1;
            final Constant c2 = (Constant) arg2;
            final Constant c3 = (Constant) arg3;
            if (c1.isNumeric() && c2.isNumeric() && c3.isNumeric()) {
                return c1.doubleValue() + c2.doubleValue() == c3.doubleValue();
            }
        }
        return false;
    }

    private boolean checkSub(int[] cliterals, int[] grounding, int offset) {
        final int gid1 = grounding[cliterals[offset]];
        if (gid1 == -1) return true;
        final int gid2 = grounding[cliterals[offset + 1]];
        if (gid2 == -1) return true;
        final int gid3 = grounding[cliterals[offset + 2]];
        if (gid3 == -1) return true;

        final Term arg1 = termsToIntegers.indexToValue(gid1);
        final Term arg2 = termsToIntegers.indexToValue(gid2);
        final Term arg3 = termsToIntegers.indexToValue(gid3);

        if (arg1 instanceof Constant && arg2 instanceof Constant && arg3 instanceof Constant) {
            final Constant c1 = (Constant) arg1;
            final Constant c2 = (Constant) arg2;
            final Constant c3 = (Constant) arg3;
            if (c1.isNumeric() && c2.isNumeric() && c3.isNumeric()) {
                return c1.doubleValue() - c2.doubleValue() == c3.doubleValue();
            }
        }
        return false;
    }

    private boolean checkMod(int[] cliterals, int[] grounding, int offset) {
        final int gid1 = grounding[cliterals[offset]];
        if (gid1 == -1) return true;
        final int gid2 = grounding[cliterals[offset + 1]];
        if (gid2 == -1) return true;
        final int gid3 = grounding[cliterals[offset + 2]];
        if (gid3 == -1) return true;

        final Term arg1 = termsToIntegers.indexToValue(gid1);
        final Term arg2 = termsToIntegers.indexToValue(gid2);
        final Term arg3 = termsToIntegers.indexToValue(gid3);

        if (arg1 instanceof Constant && arg2 instanceof Constant && arg3 instanceof Constant) {
            final Constant c1 = (Constant) arg1;
            final Constant c2 = (Constant) arg2;
            final Constant c3 = (Constant) arg3;
            if (c1.isNumeric() && c2.isNumeric() && c3.isNumeric()) {
                return c1.doubleValue() % c2.doubleValue() == c3.doubleValue();
            }
        }
        return false;
    }

    private boolean checkNext(int[] cliterals, int[] grounding, int offset, boolean isGround) {
        if (!isGround) {
            return true;
        }

        final int gid1 = grounding[cliterals[offset]];
        final int gid2 = grounding[cliterals[offset + 1]];

        Constant c1;
        Constant c2;

        final Term arg1 = termsToIntegers.indexToValue(gid1);
        if (!(arg1 instanceof Constant) || !(c1 = (Constant) arg1).isNumeric()) {
            return false;
        }

        final Term arg2 = termsToIntegers.indexToValue(gid2);
        if (!(arg2 instanceof Constant) || !(c2 = (Constant) arg2).isNumeric()) {
            return false;
        }

        return c1.doubleValue() == c2.doubleValue() - 1.0;
    }

    private boolean checkAlldiff(int[] cliterals, int[] grounding, int offset, int arity, int index) {
        long pc = 0;
        boolean hasDuplicate = false;

        for (int i = offset; i < offset + arity; i++) {
            final int gid = grounding[cliterals[i]];
            if (gid != -1) {
                final int x = (gid + 1) * (gid + 1) * (gid + 1);
                if ((pc & x) == x) {
                    hasDuplicate = true;
                    break;
                }
                pc |= x;
            }
        }

        if (hasDuplicate) {
            if (arity <= 3) {
                return checkAlldiffSmall(cliterals, grounding, offset, arity);
            } else {
                return checkAlldiffSet(cliterals, grounding, offset, arity);
            }
        }
        return true;
    }

    private boolean checkAlldiffSmall(int[] cliterals, int[] grounding, int offset, int arity) {
        switch (arity) {
            case 1: return true;
            case 2: {
                final int gid0 = grounding[cliterals[offset]];
                final int gid1 = grounding[cliterals[offset + 1]];
                return gid0 == -1 || gid1 == -1 || gid0 != gid1;
            }
            case 3: {
                final int gid0 = grounding[cliterals[offset]];
                final int gid1 = grounding[cliterals[offset + 1]];
                final int gid2 = grounding[cliterals[offset + 2]];
                return (gid0 == -1 || gid1 == -1 || gid0 != gid1) &&
                        (gid0 == -1 || gid2 == -1 || gid0 != gid2) &&
                        (gid1 == -1 || gid2 == -1 || gid1 != gid2);
            }
        }
        return true;
    }

    private boolean checkAlldiffSet(int[] cliterals, int[] grounding, int offset, int arity) {
        final Set<Integer> values = new HashSet<>(arity);
        for (int i = offset; i < offset + arity; i++) {
            final int gid = grounding[cliterals[i]];
            if (gid != -1) {
                if (!values.add(gid)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkIn(int[] cliterals, int[] grounding, int offset, int arity) {
        final int gid = grounding[cliterals[offset]];
        if (gid == -1) return true;

        for (int i = offset + 1; i < offset + arity; i++) {
            final int gidCheck = grounding[cliterals[i]];
            if (gidCheck == -1) return true;
            if (gid == gidCheck) return true;
        }
        return false;
    }

    private boolean checkMaxCard(int[] cliterals, int[] grounding, int offset, int arity) {
        final int gidCard = grounding[cliterals[offset]];
        if (gidCard == -1) return true;

        final Term cardTerm = termsToIntegers.indexToValue(gidCard);
        if (!(cardTerm instanceof Constant)) return true;

        final Constant constant = (Constant) cardTerm;
        if (!constant.isNumeric()) return false;

        final int cardinality = constant.intValue();
        if (cardinality >= arity - 1) return true;

        final Set<Integer> values = new HashSet<>(arity);
        for (int i = offset + 1; i < offset + arity; i++) {
            final int gid = grounding[cliterals[i]];
            if (gid != -1) {
                values.add(gid);
                if (values.size() > cardinality) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkAnyPred(ClauseC c, ClauseE e, int[] cliterals, int index, int arity) {
        final int[] cliteral = new int[arity + 3];
        System.arraycopy(cliterals, index, cliteral, 0, arity + 3);
        for (final int predE : e.predicates.values()) {
            cliteral[0] = predE;
            if (e.checkLiteral(c, 0, false)) {
                return true;
            }
        }
        return false;
    }

    private class HighArityLiterals {

        private Map<Triple<Integer, Integer, Integer>, Integer> lower;

        private Map<Triple<Integer, Integer, Integer>, Integer> upper;

        private int[] literals;

        private int maxArity;

        /**
         * @param lits
         * @param maxArity
         */
        public HighArityLiterals(int[] lits, int maxArity) {
            this.maxArity = maxArity;
            List<Integer> tempLiterals = new ArrayList<Integer>();
            //[predicate,argument,term] -> position iterable literals array
            MultiMap<Triple<Integer, Integer, Integer>, Integer> bag = new MultiMap<Triple<Integer, Integer, Integer>, Integer>();
            for (int i = 0; i < lits.length; i += lits[i + 1] + 2) {
                if (lits[i + 1] > this.maxArity) {
                    for (int j = 0; j < lits[i + 1] + 2; j++) {
                        tempLiterals.add(lits[i + j]);
                    }
                }
            }
            this.literals = VectorUtils.toIntegerArray(tempLiterals);
            for (int i = 0; i < this.literals.length; i += this.literals[i + 1] + 2) {
                for (int j = 0; j < this.literals[i + 1]; j++) {
                    bag.put(new Triple<Integer, Integer, Integer>(this.literals[i], j, this.literals[i + 2 + j]), i);
                }
            }
            this.lower = new HashMap<Triple<Integer, Integer, Integer>, Integer>();
            this.upper = new HashMap<Triple<Integer, Integer, Integer>, Integer>();
            for (Map.Entry<Triple<Integer, Integer, Integer>, Set<Integer>> entry : bag.entrySet()) {
                this.lower.put(entry.getKey(), Sugar.findBest(entry.getValue(), new Sugar.MyComparator<Integer>() {
                    @Override
                    public boolean isABetterThanB(Integer a, Integer b) {
                        return a < b;
                    }
                }));
                this.upper.put(entry.getKey(), Sugar.findBest(entry.getValue(), new Sugar.MyComparator<Integer>() {
                    @Override
                    public boolean isABetterThanB(Integer a, Integer b) {
                        return a >= b;
                    }
                }));
            }
        }

        /**
         * @param c
         * @param index
         * @param e
         * @param isGround
         * @return
         */
        public boolean match(ClauseC c, int index, ClauseE e, boolean isGround) {
            int[] cliterals = c.literals;
            if ((cliterals[index + 2] & SPECIAL_PREDICATE) != 0/*specialPredicateIds.contains(cliterals[index])*/) {
                return matchSpecialLiteral(c, index, e, isGround);
            }
            int lowerBound = 0;
            int upperBound = this.literals.length;
            int predicate = cliterals[index];
            int[] cliteral = new int[cliterals[index + 1] + 2];
            cliteral[0] = cliterals[index];
            cliteral[1] = cliterals[index + 1];
            Triple<Integer, Integer, Integer> t = new Triple<Integer, Integer, Integer>(predicate, 0, 0);
            for (int i = index + 3, j = 0; i < index + cliterals[index + 1] + 3; i++, j++) {
                if (c.groundedValues[cliterals[i]] == -1) {
                    cliteral[j + 2] = -maxArity - 2;
                } else {
                    cliteral[j + 2] = c.groundedValues[cliterals[i]];
                    t.s = j;
                    t.t = c.groundedValues[cliterals[i]];
                    Integer fromLower, fromUpper;
                    if ((fromLower = this.lower.get(t)) == null || (fromUpper = this.upper.get(t)) == null) {
                        return false;
                    }
                    lowerBound = Math.max(lowerBound, fromLower);
                    upperBound = Math.min(upperBound, fromUpper);
                }
            }
            int iters = 0;
            outerLoop:
            for (int i = lowerBound; i <= upperBound; i += this.literals[i + 1] + 2) {
                iters++;
                for (int j = 0; j < cliteral.length; j++) {
                    if (cliteral[j] > -1 && this.literals[i + j] != cliteral[j]) {
                        continue outerLoop;
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     *
     */
    private class LowArityLiterals {

        private final VectorSet set = new VectorSet();

        private final int maxArity;

        /**
         * @param literals
         * @param maxArity
         */
        public LowArityLiterals(int[] literals, int maxArity) {
            this.maxArity = maxArity;
            for (int i = 0; i < literals.length; i += literals[i + 1] + 2) {
                add(literals, i);
            }
            //set.printStats();
        }

        /**
         * @param literals
         * @param index
         */
        public void add(int[] literals, int index) {
            final int arity = literals[index + 1];

            if (arity > maxArity) {
                return;
            }

            final int predicateId = literals[index];
            final int maskIterations = 1 << arity;
            final int wildcardValue = -maxArity - 2;

            for (int i = 0; i < maskIterations; i++){
                int[] literal = new int[arity + 2];
                literal[0] = predicateId;
                literal[1] = arity;

                for (int j = 0; j < arity; j++) {
                    literal[j + 2] = ((i >> j) & 1) == 0 ? literals[index + 2 + j] : wildcardValue;
                }

                set.add(literal);
            }
        }

        /**
         * @param c
         * @param index
         * @param e
         * @param isGround
         * @return
         */
        public boolean match(ClauseC c, int index, ClauseE e, int[] auxBuffer, boolean isGround) {
            final int[] cliterals = c.literals;
            if ((cliterals[index + 2] & SPECIAL_PREDICATE) != 0) {
                return matchSpecialLiteral(c, index, e, isGround);
            }

            final int arity = cliterals[index + 1];
            final int wildCard = -maxArity - 2;

            auxBuffer[0] = cliterals[index];
            auxBuffer[1] = arity;

            switch (arity) {
                case 0:
                    break;
                case 1:
                    auxBuffer[2] = c.groundedValues[cliterals[index + 3]] == -1 ? wildCard : c.groundedValues[cliterals[index + 3]];
                    break;
                case 2:
                    auxBuffer[2] = c.groundedValues[cliterals[index + 3]] == -1 ? wildCard : c.groundedValues[cliterals[index + 3]];
                    auxBuffer[3] = c.groundedValues[cliterals[index + 4]] == -1 ? wildCard : c.groundedValues[cliterals[index + 4]];
                    break;
                case 3:
                    auxBuffer[2] = c.groundedValues[cliterals[index + 3]] == -1 ? wildCard : c.groundedValues[cliterals[index + 3]];
                    auxBuffer[3] = c.groundedValues[cliterals[index + 4]] == -1 ? wildCard : c.groundedValues[cliterals[index + 4]];
                    auxBuffer[4] = c.groundedValues[cliterals[index + 5]] == -1 ? wildCard : c.groundedValues[cliterals[index + 5]];
                    break;
            }

            return set.contains(auxBuffer);
        }
    }

    /**
     *
     */
    private class CompletelySymmetricLiterals {


        private Map<Integer, IntegerMultiMap<Integer>> termsToLiterals = new HashMap<Integer, IntegerMultiMap<Integer>>();

        /**
         * @param literals
         */
        public CompletelySymmetricLiterals(int[] literals) {
            Map<Integer, MultiMap<Integer, Integer>> ttl = new HashMap<Integer, MultiMap<Integer, Integer>>();
            for (int index = 0; index < literals.length; index += literals[index + 1] + 2) {
                if (predicatesToIntegers.indexToValue(literals[index]).startsWith(SymmetricPredicates.PREFIX)) {
                    final int arity = literals[index + 1];
                    MultiMap<Integer, Integer> mm = ttl.computeIfAbsent(literals[index], k -> new MultiMap<>());
                    for (int i = 0; i < arity; i++) {
                        mm.put(literals[index + i + 2], index);
                    }
                }
            }
            for (Map.Entry<Integer, MultiMap<Integer, Integer>> entry : ttl.entrySet()) {
                termsToLiterals.put(entry.getKey(), IntegerMultiMap.createIntegerMultiMap(entry.getValue()));
            }
        }

        /**
         * @param c
         * @param index
         * @param e
         * @param isGround
         * @return
         */
        public boolean match(ClauseC c, int index, ClauseE e, boolean isGround) {
            int[] cliterals = c.literals;
            if ((cliterals[index + 2] & SPECIAL_PREDICATE) != 0/*specialPredicateIds.contains(cliterals[index])*/) {
                return matchSpecialLiteral(c, index, e, isGround);
            }
            int[] cliteral = new int[cliterals[index + 1] + 2];
            cliteral[0] = cliterals[index];
            cliteral[1] = cliterals[index + 1];
            IntegerSet domain = null;
            for (int i = index + 3, j = 0; i < index + cliterals[index + 1] + 3; i++, j++) {
                int predicate = cliteral[0];
                if (c.groundedValues[cliterals[i]] != -1) {
                    int term = c.groundedValues[cliterals[i]];
                    if (domain == null) {
                        domain = termsToLiterals.get(predicate).get(term);
                    } else {
                        domain = IntegerSet.intersection(domain, termsToLiterals.get(predicate).get(term));
                    }
                    if (domain.isEmpty()) {
                        return false;
                    }
                }
            }
            return domain != null;
        }
    }

    /**
     * @param c
     * @return
     */
    public ClauseC createClauseC(Clause c) {
        return new ClauseC(c);
    }

    /**
     * @param e
     * @return
     */
    public ClauseE createClauseE(Clause e) {
        return new ClauseE(e);
    }

    /**
     * @param seed
     */
    public void setRandomSeed(long seed) {
        this.random = new Random(seed);
    }

    /**
     * @param lowArity
     */
    public void setWhatIsLowArity(int lowArity) {
        this.lowArity = lowArity;
    }

    public void addCustomPredicate(CustomPredicate customPredicate) {
        this.customPredicates.put(customPredicate.name(), customPredicate);
        this.specialPredicateIds.add(this.predicatesToIntegers.valueToIndex(customPredicate.name()));
    }

    public void removeCustomPredicate(String name) {
        this.customPredicates.remove(name);
        this.specialPredicateIds.remove(this.predicatesToIntegers.valueToIndex(name));
    }

    public void addSolutionConsumer(SolutionConsumer solutionConsumer) {
        this.solutionConsumers.add(solutionConsumer);
    }

    public void removeSolutionConsumer(SolutionConsumer solutionConsumer) {
        this.solutionConsumers.remove(solutionConsumer);
    }

    protected Term[] lastVariableOrder(ClauseC c) {
        if (this.lastVariableOrder == null) {
            return null;
        } else {
            List<Term> varsList = new ArrayList<Term>();
            for (int i = 0; i < this.lastVariableOrder.length; i++) {
                Term t = c.variablesToIntegers.indexToValue(this.lastVariableOrder[i]);
                if (t instanceof Variable) {
                    varsList.add(t);
                }
            }
            Term[] retVal = new Term[varsList.size()];
            varsList.toArray(retVal);
            return retVal;
        }
    }

//    public static void main(String args[]){
//        Clause c = Clause.parsePrologLikeClause("a(A,B), b(B,C), c(C,D), d(D,E), e(E,F), f(F,D)");
//        Clause e = Clause.parsePrologLikeClause("a(a,b), a(b,a), b(b,c), c(c,d), d(d,e), e(e,f), f(f,d), f(d,e)");
//        SubsumptionEngineJ2 sej2 = new SubsumptionEngineJ2();
//        DecomposedClauseC dcc = sej2.new DecomposedClauseC(c);
//        ClauseE ce = sej2.new ClauseE(e);
//        dcc.initialize(ce);
//
//
//    }
}
