/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cz.cvut.fel.ida.logic.subsumption;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.logic.special.IsoClauseWrapper;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.math.Cache;
import cz.cvut.fel.ida.utils.math.Combinatorics;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Tuple;

import java.util.*;

/**
 * Assumes that maxCard = max arity of alldiff
 *
 * Created by ondrejkuzelka on 02/01/17.
 */
public class ApproximateSubsetCounter {

    public static boolean verbose = true;

    private final static double log2 = Math.log(2);

    private double epsilon = 0.1, delta = 0.1;

    private double simpleSamplingRelativeError = 0.01;

    private double simpleSamplingDelta = 0.1;

    //may overload delta
    private int numTries = -1;

    private Matching matching;

    private SubsumptionEngineJ2.ClauseE clauseE;

    private Clause db;

    private Random random = new Random(Settings.seed);

    private final static Object cacheLock = new Object();

    private static Cache<Pair<Set<IsoClauseWrapper>,Integer>,Double> cache = new Cache<Pair<Set<IsoClauseWrapper>, Integer>, Double>();

    public ApproximateSubsetCounter(Clause db){
        this.db = db;
        this.matching = new Matching(Sugar.list(db));
        this.clauseE = matching.examples().get(0);
    }

    public ApproximateSubsetCounter(Clause db, double epsilon, double delta){
        this(db);
        this.epsilon = epsilon;
        this.delta = delta;
    }

    public ApproximateSubsetCounter(Clause db, double epsilon, int numTries){
        this(db);
        this.epsilon = epsilon;
        this.numTries = numTries;
    }


    public double logApproxCount(Clause query, int maxCard){
        return this.logApproxCount(Sugar.list(query), maxCard);
    }

    public double logApproxCount(List<Clause> orOfQueries, int maxCard){
        Pair<Set<IsoClauseWrapper>,Integer> representative = new Pair<Set<IsoClauseWrapper>, Integer>(new HashSet<IsoClauseWrapper>(),maxCard);
        for (Clause c : orOfQueries){
            representative.r.add(new IsoClauseWrapper(c));
        }
        synchronized (cacheLock){
            Double d;
            if ((d = cache.get(representative)) != null){
                return d;
            }
        }
        double retVal;
        Double estimate = logApproxBySimpleSampling(orOfQueries, maxCard, 10000);
        if (estimate != null){
            //System.out.println("Simple sampling estimate: "+estimate);
            retVal = estimate.doubleValue();
        } else {
            int maxExact = 100000;
            int count1 = count(orOfQueries, maxCard, maxExact);
            if (count1 >= maxExact) {
                if (verbose) {
                    System.out.println("warning: using xor-based sampling (slow).");
                }
                retVal = approxMC2(orOfQueries, this.clauseE, maxCard);
            } else {
                retVal = Math.log(count1) / log2;
            }
        }
        synchronized (cacheLock){
            cache.put(representative, retVal);
        }
        return retVal;
    }

    private Double logApproxBySimpleSampling(List<Clause> orOfQueries, int maxCard, int numSamples){
        double hits = 0;
        List<Constant> constants = Sugar.listFromCollections(LogicUtils.constants(this.db));
        double tries = 0;
        outerLoop: for (int i = 0; i < numSamples; i++){
            Tuple<Constant> sample = Combinatorics.randomCombination(constants, maxCard, this.random);
            tries++;
            Clause e = new Clause(Sugar.union(LogicUtils.induced(this.db, sample.toSet()).literals(), new Literal("", sample.toList())));
            for (Clause q : orOfQueries){
                if (this.matching.subsumption(q, e)){
                    hits++;
                    break;
                }
            }
            if (hits > 0){
                double pHat = hits/tries;
                double lower = pHat*(1-this.simpleSamplingRelativeError);
                double higher = pHat*(1+this.simpleSamplingRelativeError);
                boolean enough = true;
                if (lower > 0){
                    if (Combinatorics.logBinomialProbability((int)hits, lower, (int)tries) > Math.log(this.simpleSamplingDelta)){
                        continue outerLoop;
                    }
                }
                if (higher < 1){
                    if (Combinatorics.logBinomialProbability((int)hits, higher, (int)tries) > Math.log(this.simpleSamplingDelta)){
                        continue outerLoop;
                    }
                }
                return (Math.log(hits)-Math.log(tries)+Combinatorics.logBinomial(constants.size(), maxCard))/Math.log(2);
            }
        }
        return null;
    }

    public int count(List<Clause> orOfQueries, int maxCard, int maxCount){
        return allImages(orOfQueries, maxCard, maxCount).size();
    }

    public int count(Clause query, int maxCard, int maxCount){
        return allImages(query, this.clauseE, maxCard, maxCount, null, null).size();
    }

    public Set<Set<Term>> allImages(Clause query, int maxCard){
        return allImages(query, maxCard, Integer.MAX_VALUE);
    }

    public Set<Set<Term>> allImages(Clause query, int maxCard, int maxCount){
        return allImages(query, this.clauseE, maxCard, maxCount, null, null);
    }

    public Set<Set<Term>> allImages(List<Clause> orOfQueries, int maxCard, int maxCount){
        return allImages(orOfQueries, this.clauseE, maxCard, maxCount, null, null);
    }

    private double approxMC2(Clause query, SubsumptionEngineJ2.ClauseE clauseE, int maxCard){
        return Sugar.println(approxMC2(Sugar.<Clause>list(query), clauseE, maxCard));
    }

    private double approxMC2(List<Clause> orOfQueries, SubsumptionEngineJ2.ClauseE clauseE, int maxCard){
        double threshold = 1+9.84*(1+epsilon/(1+epsilon))*Sugar.square(1 + 1 / epsilon);
        Set<Set<Term>> imgs = allImages(orOfQueries, clauseE, maxCard, (int)threshold, null, null);
        if (imgs.size() < (int)threshold){
            return Math.log(imgs.size())/log2;
        }
        double t;
        if (this.numTries == -1) {
            t = Math.ceil(17 * Math.log(2 / delta) / log2);
        } else {
            t = numTries;
        }
        int logNCells = 1;
        List<Double> logCounts = new ArrayList<Double>();
        for (int i = 0; i < t; i++){
            Pair<Double,Double> p = approxMC2Core(orOfQueries, clauseE, maxCard, (int)threshold, logNCells);
            if (p != null) {
                logCounts.add(p.r + Math.log(p.s) / log2);
                logNCells = p.r.intValue();
            }
        }
        if (logCounts.isEmpty()){
            return Double.NaN;
        }
        double[] logCountsArray = VectorUtils.toDoubleArray(logCounts);
        Arrays.sort(logCountsArray);
        return logCountsArray[logCountsArray.length/2];
    }

    private Pair<Double,Double> approxMC2Core(List<Clause> orOfQueries, SubsumptionEngineJ2.ClauseE clauseE, int maxCard, int threshold, int logPrevNCells){
        int numTerms = clauseE.allTerms().size();
        int numRows = (int)Math.ceil(Combinatorics.logBinomial(numTerms, maxCard)/log2);
        boolean[][] left = new boolean[numRows][];
        boolean[] right = new boolean[numRows];
        for (int i = 0; i < left.length; i++){
            left[i] = VectorUtils.randomBooleanVector(numTerms, this.random);
            right[i] = this.random.nextBoolean();
        }
        Set<Set<Term>> images = allImages(orOfQueries, clauseE, maxCard, threshold, left,  right);
        if (images.size() >= threshold){
            return null;
        }
        int m = logSatSearch(orOfQueries, clauseE, maxCard, threshold, left, right, logPrevNCells);
        boolean[][] leftM = new boolean[m][];
        System.arraycopy(left, 0, leftM, 0, m);
        boolean[] rightM = new boolean[m];
        System.arraycopy(right, 0, rightM, 0, m);
        images = allImages(orOfQueries, clauseE, maxCard, threshold, leftM, rightM);
        return new Pair<Double,Double>((double)m, (double)images.size());
    }

    private int logSatSearch(List<Clause> orOfQueries, SubsumptionEngineJ2.ClauseE clauseE, int maxCard, int threshold, boolean[][] left, boolean[] right, int mPrev){
        int loIndex = 0;
        int hiIndex = right.length-1;
        int m = mPrev;
        Boolean[] bigCell = new Boolean[right.length];
        bigCell[0] = Boolean.TRUE;
        bigCell[1] = Boolean.FALSE;
        while (true){
            boolean[][] leftM = new boolean[m][];
            System.arraycopy(left, 0, leftM, 0, m);
            boolean[] rightM = new boolean[m];
            System.arraycopy(right, 0, rightM, 0, m);
            Set<Set<Term>> images = allImages(orOfQueries, clauseE, maxCard, threshold, leftM,  rightM);
            if (images.size() >= threshold){
                if (bigCell[m+1] == Boolean.FALSE){
                    return m+1;
                }
                for (int i = 1; i <= m; i++){
                    bigCell[i] = Boolean.TRUE;
                }
                loIndex = m;
                if (Math.abs(m-mPrev) < 3){
                    m = m+1;
                } else if (2*m < bigCell.length){
                    m = 2*m;
                } else {
                    m = (m+hiIndex)/2;
                }
            } else {
                if (bigCell[m-1] == Boolean.TRUE){
                    return m;
                }
                for (int i = m; i < bigCell.length; i++){
                    bigCell[i] = Boolean.FALSE;
                }
                hiIndex = m;
                if (Math.abs(m-mPrev) < 3){
                    m = m-1;
                } else {
                    m = (m+loIndex)/2;
                }
            }
        }
    }

    private Set<Set<Term>> allImages(List<Clause> orOfQueries, SubsumptionEngineJ2.ClauseE clauseE, int maxCard, int maxCount, boolean[][] xorLeft, boolean[] xorRight){
        Set<Set<Term>> retVal = new HashSet<Set<Term>>();
        for (Clause query : orOfQueries){
            retVal.addAll(allImages(query, clauseE, maxCard, maxCount, xorLeft, xorRight));
            if (retVal.size() >= maxCount){
                break;
            }
        }
        return retVal;
    }

    private Set<Set<Term>> allImages(Clause query, SubsumptionEngineJ2.ClauseE clauseE, int maxCard, int maxCount, boolean[][] xorLeft, boolean[] xorRight){
        if (!matching.subsumption(matching.createClauseC(query), clauseE)){
            return new HashSet<Set<Term>>();
        }

        SubsumptionEngineJ2.ClauseC queryC = matching.createClauseC(query);
        if (!this.matching.getEngine().solveWithResumer(queryC, clauseE, 2)){
            return new HashSet<Set<Term>>();
        }
        Term[] termOrder = this.matching.getEngine().lastVariableOrder(queryC);
        List<Variable> variableList = new ArrayList<Variable>();
        for (int i = 0; i < termOrder.length; i++){
            if (termOrder[i] instanceof Variable){
                variableList.add((Variable)termOrder[i]);
            }
        }
        //System.out.println(variableList);

        Set<Set<Term>> partial = new HashSet<Set<Term>>();
        partial.add(new HashSet<Term>());

        outerLoop: for (int i = 0; i < variableList.size(); i++){
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
                SubsumptionEngineJ2.ClauseC clauseC = matching.createClauseC(extendedQuery);
                if (xorLeft != null && xorRight != null){
                    addXorConstraints(clauseC, maxCard, xorLeft, xorRight);
                }

                Pair<Term[],List<Term[]>> sol = matching.getEngine().allSolutions(clauseC, clauseE, maxCount, 1, variableList.get(i));

                for (Term[] ts : sol.s) {
                    Term term = ts[0];
                    if (this.matching.getEngine().subsumptionMode() == Matching.THETA_SUBSUMPTION || !set.contains(term)){
                        Set<Term> extendedSet = Sugar.setFromCollections(set);
                        extendedSet.add(term);
                        if (extendedSet.size() <= maxCard) {
                            newPartial.add(extendedSet);
                        }
                    }
                }
                double thresh = Math.pow(2, maxCard)*maxCount+1;
                if (newPartial.size() > thresh){
                    Set<Set<Term>> temp = new HashSet<Set<Term>>();
                    for (Set<Term> s : newPartial){
                        temp.add(s);
                        if (temp.size() >= thresh){
                            break;
                        }
                    }
                    partial = temp;
                    continue outerLoop;
                } else {
                    partial = newPartial;
                }
            }
        }
        return partial;
    }

    private void addXorConstraints(SubsumptionEngineJ2.ClauseC clauseC, int maxCard, boolean[][] leftHand, boolean[] rightHand){
        MaxCardXorConstraint constr = new MaxCardXorConstraint(maxCard, clauseC.numActualConstants(), clauseE.allTerms(), leftHand, rightHand, clauseC);
        clauseC.addGlobalConstraint(constr);
    }

    private static Clause randomGraph(int n, int m){
        Set<Literal> l = new HashSet<Literal>();
        List<Term> vs = new ArrayList<Term>();
        for (int i = 0; i < n; i++){
            vs.add(Constant.construct(String.valueOf(i)));
        }
        Random random = new Random(1);
        for (int i = 0; i < m; i++){
            l.add(new Literal("a", vs.get(random.nextInt(n)), vs.get(random.nextInt(n))));
        }
        return new Clause(l);
    }

    public void setSubsumptionMode(int subsumptionMode){
        this.matching.setSubsumptionMode(subsumptionMode);
    }

    public static void main(String[] args){
        Clause a = Clause.parse("@alldiff(A,B,C,D,E),a(A,B),a(A,C),a(A,D),a(B,C),a(C,D),a(D,C),a(D,E),a(E,A)");
        Clause c = Clause.parse("@alldiff(A,B,C,D,E), a(A,B), a(B,A)");
        Clause b = randomGraph(100,4000);

        ApproximateSubsetCounter asm  = new ApproximateSubsetCounter(b, 0.5, 3);
//        SubsumptionEngineJ2.ClauseE clauseE = asm.matching.createClauseE(b);
//
//        boolean[][] leftXor = new boolean[][]{
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size()),
//                VectorUtils.randomBooleanVector(clauseE.allTerms().size())
//        };
//        boolean[] rightXor = new boolean[]{true,true,true,true,true,true,true,false,false,true,true};

        long mm1 = System.currentTimeMillis();
        double logApproxCount = asm.logApproxCount(a, 5);
        long mm2 = System.currentTimeMillis();

        System.out.println("T: "+(mm2-mm1)+"ms");

        System.out.println("Approximate size: "+Math.pow(2,logApproxCount));


        //System.out.println(b);
        Matching m = new Matching();
        m.setArcConsistencyStartsIn(1);
        m.setSubsumptionMode(Matching.THETA_SUBSUMPTION);

        long m1 = System.currentTimeMillis();
        SubsumptionEngineJ2.countFC = 0;
        Pair<Term[],List<Term[]>> subs = m.allSubstitutions(a,b);
        //Triple<Term[],List<Term[]>,Double> subs = m.searchTreeSampler(a, b, 100, 1);
        long m2 = System.currentTimeMillis();
        System.out.println("T: "+(m2-m1)+"ms, est: "/*+Math.exp(subs.t)*/+", fc: "+(m2-m1)/(double)SubsumptionEngineJ2.countFC+", all groundings: "+subs.s.size());

        Set<Set<Term>> subsets0 = new HashSet<Set<Term>>();

        System.out.println(Sugar.objectArrayToString(subs.r));
        for (Term[] s : subs.s){
            //System.out.println(Sugar.objectArrayToString(s));
            subsets0.add(Sugar.<Term>set(s));
        }
        System.out.println("SUBSETS0.size(): "+subsets0.size());

        System.out.println("\nSUBSETS:");
        m1 = System.currentTimeMillis();




//        Pair<Term[],List<Term[]>> subs2 = m.allSubstitutions(a,b, Integer.MAX_VALUE, 4);
//        System.out.println(Sugar.objectArrayToString(subs.r));
//        for (Term[] s : subs2.s){
//            System.out.println(Sugar.objectArrayToString(s));
//        }
        System.out.println("subsumes? "+m.subsumption(a,b));
    }

}
