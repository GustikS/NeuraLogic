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

    protected ValueToIndex<Term> termsToIntegers = new ValueToIndex<Term>();

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
        //todo - move to ClausE, this is unnecessarily slow
        Counters<Integer> predicateCounts = new Counters<Integer>();
        for (int i = 0; i < e.literals.length; i += e.literals[i + 1] + 2) {
            predicateCounts.increment(e.literals[i]);
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
                    heuristic1[c.literals[ci + 3 + i]] += 1.0 * weights[c.literals[ci + 3 + i]];
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
                for (int j = 0; j < c.literals[ci + 1]; j++) {
                    if (heuristic1[c.literals[ci + 3 + j]] != -1) {
                        double count = predicateCounts.get(c.literals[ci]);
                        if (count == 0) {
                            //todo - handle separately special predicateNames and negations
                            count = 1e8;
                        }
                        heuristic1[c.literals[ci + 3 + j]] += 1.0 * weights[c.literals[ci + 3 + j]] / count;
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
                                            this.variableDomains[i] = e.variableDomains.get(new Pair<Integer, Integer>(this.literals[ciLit], j));
                                        } else {
                                            this.variableDomains[i] = IntegerSet.intersection(
                                                    e.variableDomains.get(new Pair<Integer, Integer>(this.literals[ciLit], j)),
                                                    e.typedTerms(this.variableTypes[i]));
                                        }
                                    }
                                } else {
                                    if (!this.negations.contains(ciLit) && !specialPredicateIds.contains(this.literals[ciLit])) {
                                        IntegerSet varDomain = e.variableDomains.get(new Pair<Integer, Integer>(this.literals[ciLit], j));
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
            for (int ciLit : containedIn[variable].values()) {
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
                Arrays.fill(this.auxBuffer1, 0);
                outerLoop:
                for (int neighb : this.neighbours[variable].values()) {
                    if (this.groundedValues[neighb] == -1 && this.containedIn[neighb].size() > 1) {
                        for (int val : this.variableDomains[neighb].values()) {
                            boolean succ = ground(neighb, val, e);
                            unground(neighb);
                            if (succ) {
                                continue outerLoop;
                            } else {
                                this.auxBuffer1[neighb]++;
                            }
                        }
                        return false;
                    }
                }
                for (int neighb : this.neighbours[variable].values()) {
                    if (this.auxBuffer1[neighb] > 0) {
                        this.variableDomains[neighb] = IntegerSet.createIntegerSetFromSortedArray(Arrays.copyOfRange(this.variableDomains[neighb].values(), this.auxBuffer1[neighb], this.variableDomains[neighb].size()));
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

        protected Map<Pair<Integer, Integer>, IntegerSet> variableDomains = new HashMap<Pair<Integer, Integer>, IntegerSet>();

        private IntegerMultiMap<Integer> typedTerms = new IntegerMultiMap<Integer>();

        protected int[] literals;

        private IntegerSet allTerms;

        private IntegerSet predicates;

        protected IntegerSet[] domainsByPredicates;

        private LowArityLiterals lal;

        private HighArityLiterals hal;

        private CompletelySymmetricLiterals csl;

        private Map<Term, Integer> numbers = new HashMap<Term, Integer>();

        /**
         * Creates a new instance of class ClauseE which serves as an efficient data-structure
         * for storing the clauses that are on the right-hand side of theta-subsumption relation (i.e. the examples).
         *
         * @param clause the clause which should be compiled into the efficient representation
         */
        public ClauseE(Clause clause) {
            MultiMap<Integer, Integer> integerMultiMap = new MultiMap<Integer, Integer>();
            Set<Integer> predicates = new HashSet<Integer>();
            int literalIndex = 0;
            for (Literal l : clause.literals()) {
                if (!l.isNegated()) {
                    integerMultiMap.put(predicatesToIntegers.valueToIndex(l.predicateName()), literalIndex);
                    literalIndex += 2 + l.arity();
                    predicates.add(predicatesToIntegers.valueToIndex(l.predicateName()));
                }
            }
            this.predicates = IntegerSet.createIntegerSet(predicates);
            domainsByPredicates = new IntegerSet[predicatesToIntegers.max() + 1];
            for (Map.Entry<Integer, Set<Integer>> entry : integerMultiMap.entrySet()) {
                //negative values are for special predicateNames
                if (entry.getKey() >= 0) {
                    domainsByPredicates[entry.getKey()] = IntegerSet.createIntegerSet(entry.getValue());
                }
            }
            literals = new int[literalIndex];
            Map<Literal, Integer> intLitMap = new HashMap<Literal, Integer>();
            Map<Integer, Literal> litIntMap = new HashMap<Integer, Literal>();
            int index = 0;
            MultiMap<Pair<Integer, Integer>, Integer> varDomains = new MultiMap<Pair<Integer, Integer>, Integer>();
            for (Literal l : clause.literals()) {
                if (!l.isNegated()) {
                    literals[index] = predicatesToIntegers.valueToIndex(l.predicateName());
                    literals[index + 1] = l.arity();
                    intLitMap.put(l, index);
                    litIntMap.put(index, l);
                    index += 2;
                    if (l.predicateName().startsWith(SymmetricPredicates.PREFIX)) {
                        for (int i = 0; i < l.arity(); i++) {
                            for (int j = 0; j < l.arity(); j++) {
                                literals[index + j] = termsToIntegers.valueToIndex(l.get(j));
                                varDomains.put(new Pair<Integer, Integer>(literals[index - 2], i), literals[index + j]);
                            }
                        }
                    } else {
                        for (int j = 0; j < l.arity(); j++) {
                            literals[index + j] = termsToIntegers.valueToIndex(l.get(j));
                            varDomains.put(new Pair<Integer, Integer>(literals[index - 2], j), literals[index + j]);
                        }
                    }
                    index += l.arity();
                }
            }
            for (Map.Entry<Pair<Integer, Integer>, Set<Integer>> entry : varDomains.entrySet()) {
                this.variableDomains.put(entry.getKey(), IntegerSet.createIntegerSet(entry.getValue()));
            }
            lal = new LowArityLiterals(literals, lowArity);
            hal = new HighArityLiterals(literals, lowArity);
            csl = new CompletelySymmetricLiterals(literals);
            Set<Integer> allTermsHere = new HashSet<Integer>();
            MultiMap<Integer, Integer> typedTermsMM = new MultiMap<Integer, Integer>();
            for (Term t : clause.terms()) {
                allTermsHere.add(termsToIntegers.valueToIndex(t));
                if (t.type() != null) {
                    typedTermsMM.put(typesToIntegers.valueToIndex(t.type()), termsToIntegers.valueToIndex(t));
                }
            }
            allTerms = IntegerSet.createIntegerSet(allTermsHere);
            typedTerms = IntegerMultiMap.createIntegerMultiMap(typedTermsMM);
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
            int[] cliterals = c.literals;
            if (negated) {
                if (isGround(cliterals, c.groundedValues, index)) {
                    if ((cliterals[index + 2] & COMPLETELY_SYMMETRIC_PREDICATE) > 0) {
                        return !this.csl.match(c, index, this);
                    } else if (cliterals[index + 1] <= lowArity) {
                        return !this.lal.match(c, index, this, c.lowArityAuxBuffers[cliterals[index + 1]]);
                    } else {
                        return !this.hal.match(c, index, this);
                    }
                }
                return true;
            } else {
                if ((cliterals[index + 2] & COMPLETELY_SYMMETRIC_PREDICATE) > 0) {
                    return this.csl.match(c, index, this);
                } else if (cliterals[index + 1] <= lowArity) {
                    return this.lal.match(c, index, this, c.lowArityAuxBuffers[cliterals[index + 1]]);
                } else {
                    return this.hal.match(c, index, this);
                }
            }
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
        for (int i = index + 3, j = 0; i < index + cliterals[index + 1] + 3; i++, j++) {
            if (grounding[cliterals[i]] == -1) {
                return false;
            }
        }
        return true;
    }

    private boolean matchCustomLiteral(int[] cliterals, int[] grounding, int index) {
        String predicate = predicatesToIntegers.indexToValue(cliterals[index]);
        CustomPredicate cs;
        if ((cs = this.customPredicates.get(predicate)) != null) {
            Term[] args = new Term[cliterals[index + 1]];
            int j = 0;
            for (int i = index + 3; i < index + cliterals[index + 1] + 3; i++) {
                if (grounding[cliterals[i]] != -1) {
                    args[j] = termsToIntegers.indexToValue(grounding[cliterals[i]]);
                }
                j++;
            }
            return cs.isSatisfiable(args);
        }
        return true;
    }

    private boolean matchSpecialLiteral(ClauseC c, int index, ClauseE e) {
        int[] cliterals = c.literals;
        int[] grounding = c.groundedValues;
        int predicate = cliterals[index];
        int arity = cliterals[index + 1];
        switch (predicate) {
            case alldiff:
                //fast pre-check for alldiff
                long pc = 0;
                boolean precheckSucceeded = false;
                for (int i = index + 3; i < index + arity + 3; i++) {
                    if (grounding[cliterals[i]] != -1) {
                        //todo something smarter that evenly distributes bits
                        int x = grounding[cliterals[i]] + 1;
                        x = x * x * x;
                        if ((pc & x) == x) {
                            precheckSucceeded = true;
                            break;
                        } else {
                            pc = pc | x;
                        }
                    }
                }
                //proper check
                if (precheckSucceeded) {
                    //for small arities, this should be faster than using Set
                    if (arity < 16) {
                        for (int i = index + 3; i < index + arity + 3; i++) {
                            if (grounding[cliterals[i]] != -1) {
                                for (int j = i + 1; j < index + arity + 3; j++) {
                                    if (grounding[cliterals[j]] != -1) {
                                        if (grounding[cliterals[i]] == grounding[cliterals[j]]) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Set<Integer> values = new HashSet<Integer>();
                        for (int i = index + 3; i < index + arity + 3; i++) {
                            if (grounding[cliterals[i]] != -1) {
                                if (values.contains(grounding[cliterals[i]])) {
                                    return false;
                                } else {
                                    values.add(grounding[cliterals[i]]);
                                }
                            }
                        }
                    }
                }
                return true;
            case anypred:
                int[] cliteral = new int[cliterals[index + 1] + 3];
                System.arraycopy(cliterals, index, cliteral, 0, cliterals[index + 1] + 3);
                for (int predE : e.predicates.values()) {
                    cliteral[0] = predE;
                    if (e.checkLiteral(c, 0, false)) {
                        return true;
                    }
                }
                return false;
            case truepred:
                return true;
            case falsepred:
                return false;
            case in:
                if (grounding[cliterals[index + 3]] == -1) {
                    return true;
                }
                for (int i = index + 4; i < index + cliterals[index + 1] + 3; i++) {
                    if (grounding[cliterals[i]] != -1) {
                        if (grounding[cliterals[index + 3]] == grounding[cliterals[i]]) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                return false;
            case maxcard:
                if (grounding[cliterals[index + 3]] == -1) {
                    return true;
                }
                Term firstArg = termsToIntegers.indexToValue(grounding[cliterals[index + 3]]);
                if (firstArg instanceof Constant) {
                    Constant constant = (Constant) firstArg;
                    int cardinality;
                    if (constant.isNumeric()) {
                        cardinality = constant.intValue();
                    } else {
                        return false;
                    }
                    if (cardinality >= arity - 1) {
                        return true;
                    }
                    int numGround = 0;
                    for (int i = index + 4; i < index + arity + 3; i++) {
                        if (grounding[cliterals[i]] != -1) {
                            numGround++;
                        }
                    }
                    if (numGround > cardinality) {
                        Set<Integer> values = new HashSet<Integer>();
                        for (int i = index + 4; i < index + arity + 3; i++) {
                            if (grounding[cliterals[i]] != -1) {
                                values.add(grounding[cliterals[i]]);
                                if (values.size() > cardinality) {
                                    return false;
                                }
                            }
                        }
                    }
                } else {
                    return true;
                }
                return true;
            case neq:
            case eq:
            case gt:
            case geq:
            case lt:
            case leq:
                if (isGround(cliterals, grounding, index)) {
                    Term arg1 = termsToIntegers.indexToValue(grounding[cliterals[index + 3]]);
                    Term arg2 = termsToIntegers.indexToValue(grounding[cliterals[index + 4]]);
                    Constant c1, c2;
                    Comparable comparable1;
                    Comparable comparable2;
                    String str1 = arg1.toString();
                    String str2 = arg2.toString();
                    if (arg1 instanceof Constant && arg2 instanceof Constant && (c1 = (Constant) arg1).isNumeric() && (c2 = (Constant) arg2).isNumeric()) {
                        switch (predicate) {
                            case neq:
                                return c1.doubleValue() != c2.doubleValue();
                            case eq:
                                return c1.doubleValue() == c2.doubleValue();
                            case gt:
                                return c1.doubleValue() > c2.doubleValue();
                            case geq:
                                return c1.doubleValue() >= c2.doubleValue();
                            case lt:
                                return c1.doubleValue() < c2.doubleValue();
                            case leq:
                                return c1.doubleValue() <= c2.doubleValue();
                        }
                    } else {
                        comparable1 = str1;
                        comparable2 = str2;
                        switch (predicate) {
                            case neq:
                                return grounding[cliterals[index + 3]] != grounding[cliterals[index + 4]];
                            case eq:
                                return grounding[cliterals[index + 3]] == grounding[cliterals[index + 4]];
                            case gt:
                                return comparable1.compareTo(comparable2) > 0;
                            case geq:
                                return comparable1.compareTo(comparable2) >= 0;
                            case lt:
                                return comparable1.compareTo(comparable2) < 0;
                            case leq:
                                return comparable1.compareTo(comparable2) <= 0;
                        }
                    }
                } else {
                    return true;
                }
            case next:
                if (isGround(cliterals, grounding, index)) {
                    Term arg1 = termsToIntegers.indexToValue(grounding[cliterals[index + 3]]);
                    Term arg2 = termsToIntegers.indexToValue(grounding[cliterals[index + 4]]);
                    Constant c1, c2;
                    if (arg1 instanceof Constant && arg2 instanceof Constant && (c1 = (Constant) arg1).isNumeric() && (c2 = (Constant) arg2).isNumeric()) {
                        return c1.doubleValue() == c2.doubleValue() - 1;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            case add:
                if (isGround(cliterals, grounding, index)) {
                    Term arg1 = termsToIntegers.indexToValue(grounding[cliterals[index + 3]]);
                    Term arg2 = termsToIntegers.indexToValue(grounding[cliterals[index + 4]]);
                    Term arg3 = termsToIntegers.indexToValue(grounding[cliterals[index + 5]]);
                    Constant c1, c2,c3;
                    if (arg1 instanceof Constant && arg2 instanceof Constant && arg3 instanceof Constant &&
                            (c1 = (Constant) arg1).isNumeric() && (c2 = (Constant) arg2).isNumeric() && (c3 = (Constant) arg3).isNumeric()) {
                        return c1.doubleValue() + c2.doubleValue() == c3.doubleValue();
                    }

                    return false;
                }

                return true;
            case sub:
                if (isGround(cliterals, grounding, index)) {
                    Term arg1 = termsToIntegers.indexToValue(grounding[cliterals[index + 3]]);
                    Term arg2 = termsToIntegers.indexToValue(grounding[cliterals[index + 4]]);
                    Term arg3 = termsToIntegers.indexToValue(grounding[cliterals[index + 5]]);
                    Constant c1, c2,c3;
                    if (arg1 instanceof Constant && arg2 instanceof Constant && arg3 instanceof Constant &&
                            (c1 = (Constant) arg1).isNumeric() && (c2 = (Constant) arg2).isNumeric() && (c3 = (Constant) arg3).isNumeric()) {
                        return c1.doubleValue() - c2.doubleValue() == c3.doubleValue();
                    }

                    return false;
                }

                return true;
            case mod:
                if (isGround(cliterals, grounding, index)) {
                    Term arg1 = termsToIntegers.indexToValue(grounding[cliterals[index + 3]]);
                    Term arg2 = termsToIntegers.indexToValue(grounding[cliterals[index + 4]]);
                    Term arg3 = termsToIntegers.indexToValue(grounding[cliterals[index + 5]]);
                    Constant c1, c2,c3;
                    if (arg1 instanceof Constant && arg2 instanceof Constant && arg3 instanceof Constant &&
                            (c1 = (Constant) arg1).isNumeric() && (c2 = (Constant) arg2).isNumeric() && (c3 = (Constant) arg3).isNumeric()) {
                        return c1.doubleValue() % c2.doubleValue() == c3.doubleValue();
                    }

                    return false;
                }

                return true;
        }
        return matchCustomLiteral(cliterals, grounding, index);
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
         * @return
         */
        public boolean match(ClauseC c, int index, ClauseE e) {
            int[] cliterals = c.literals;
            if ((cliterals[index + 2] & SPECIAL_PREDICATE) != 0/*specialPredicateIds.contains(cliterals[index])*/) {
                return matchSpecialLiteral(c, index, e);
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

        private VectorSet set = new VectorSet();

        private int maxArity;

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

            for (int i = 0; i < 1 << arity; i++){
                ArrayList<Integer> temp = new ArrayList<>();

                for (int j = 0; j < arity; j++){
                    if ((i / (1 << j)) % 2 == 0)
                        temp.add(j);
                }

                int[] literal = new int[arity + 2];
                Arrays.fill(literal, -maxArity - 2);

                literal[0] = literals[index];
                literal[1] = literals[index + 1];

                for (int k : temp) {
                    literal[k + 2] = literals[index + 2 + k];
                }

                set.add(literal);
            }
        }

        /**
         * @param c
         * @param index
         * @param e
         * @return
         */
        public boolean match(ClauseC c, int index, ClauseE e, int[] auxBuffer) {
            int[] cliterals = c.literals;
            if ((cliterals[index + 2] & SPECIAL_PREDICATE) != 0/*specialPredicateIds.contains(cliterals[index])*/) {
                return matchSpecialLiteral(c, index, e);
            }
            //auxBuffer = new int[cliterals[index + 1] + 2];
            auxBuffer[0] = cliterals[index];
            auxBuffer[1] = cliterals[index + 1];
            for (int i = index + 3, j = 0; i < index + cliterals[index + 1] + 3; i++, j++) {
                if (c.groundedValues[cliterals[i]] == -1) {
                    auxBuffer[j + 2] = -maxArity - 2;
                } else {
                    auxBuffer[j + 2] = c.groundedValues[cliterals[i]];
                }
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
                    int arity = literals[index + 1];
                    MultiMap<Integer, Integer> mm = null;
                    if ((mm = ttl.get(literals[index])) == null) {
                        mm = new MultiMap<Integer, Integer>();
                        ttl.put(literals[index], mm);
                    }
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
         * @return
         */
        public boolean match(ClauseC c, int index, ClauseE e) {
            int[] cliterals = c.literals;
            if ((cliterals[index + 2] & SPECIAL_PREDICATE) != 0/*specialPredicateIds.contains(cliterals[index])*/) {
                return matchSpecialLiteral(c, index, e);
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
