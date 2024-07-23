/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.Domain;
import cz.cvut.fel.ida.logic.features.treeliker.TreeLikerSettings;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Quadruple;
import cz.cvut.fel.ida.utils.math.Parallel;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.Counters;
import cz.cvut.fel.ida.utils.math.collections.IntegerMultiMap;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;

/**
 * Implementation class for filtering of redundant Blocks (see Kuzelka, Zelezny, MLJ, 2011 for definition). 
 * Some of the methods are parallelized, however, not fully.
 * 
 * @author admin
 */
public class IrrelevancyFiltering {

    private boolean[] mask;

    private boolean[] negMask;

    private Collection<Integer> candidates;

    private Map<Integer, Domain[]> computedDomains;

    private List<Pair<Integer,Integer>> exampleTermPairs = Collections.synchronizedList(new ArrayList<Pair<Integer,Integer>>());

    private static Parallel parallel = new Parallel(Runtime.getRuntime().availableProcessors());

    /**
     * Creates a new instance of class IrrelevancyFiltering
     * @param cands IDs of blocks or joins which should be filtered
     * @param computedDomains domains of the blocks or joins
     * @param mask mask denoting positive examples
     * @param negMask mask denoting negative examples - the indices refer to positions of the domains in the arrays
     */
    public IrrelevancyFiltering(Collection<Integer> cands, Map<Integer,Domain[]> computedDomains, boolean[] mask, boolean[] negMask){
        this.mask = mask;
        this.negMask = negMask;
        this.candidates = cands;
        this.computedDomains = Collections.synchronizedMap(computedDomains);
        Set<Pair<Integer,Integer>> set = new LinkedHashSet<Pair<Integer,Integer>>();
        Counters<Pair<Integer,Integer>> exampleTermPairCounter = new Counters<Pair<Integer,Integer>>();
        for (Integer id : candidates){
            Domain[] domains = computedDomains.get(id);
            for (int i = 0; i < domains.length; i++){
                for (int j : domains[i].integerSet().values()){
                    set.add(new Pair<Integer,Integer>(i, j));
                    exampleTermPairCounter.increment(new Pair<Integer,Integer>(i, j));
                }
            }
        }
        exampleTermPairs.addAll(set);
    }

    /**
     * 
     * @return filtered set of IDs (of blocks or joins)
     */
    public List<Integer> filter(){
        if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
            List<Integer> retVal = Collections.synchronizedList(findRelevant(Sugar.listFromCollections(this.candidates)));
            return retVal;
        } else {
            return Sugar.listFromCollections(this.candidates);
        }
    }

    private List<Integer> findRelevant(List<Integer> candidates){
        if (candidates.size() <= 100){
            return filterIrrelevant(candidates);
        } else {
            Pair<List<Integer>,List<Integer>> splitted = split(candidates);
            List<Integer> a = findRelevant(splitted.r);
            List<Integer> b = findRelevant(splitted.s);
            List<Integer> c = findRelevant(a, b);
            return Sugar.listFromCollections(a, c);
        }
    }

    private List<Integer> findRelevant(List<Integer> better, List<Integer> worse){
        if (worse.isEmpty()){
            return better;
        } else if (better.isEmpty()){
            return worse;
        } else if (better.size() < 200 || worse.size() < 200){
            return filterIrrelevant(better, worse);
        } else {
            Quadruple<List<Integer>,List<Integer>,List<Integer>,List<Integer>> quad = this.split(better, worse);
            List<Integer> betterBetter = quad.r;
            List<Integer> betterWorse = quad.s;
            List<Integer> worseBetter = quad.t;
            List<Integer> worseWorse = quad.u;
            if (betterBetter.isEmpty() || betterWorse.isEmpty() || worseBetter.isEmpty() || worseWorse.isEmpty()){
                return filterIrrelevant(better, worse);
            }
            List<Integer> a = findRelevant(betterBetter, worseBetter);
            List<Integer> b = findRelevant(betterWorse, worseWorse);
            List<Integer> c = findRelevant(betterBetter, b);
            return Sugar.listFromCollections(a, c);
        }
    }

    private class SplitBagBuilder implements Runnable {

        private int startIndex;

        private List<Integer> input;

        private final MultiMap<Pair<Integer,Integer>,Integer> outputBag;

        public SplitBagBuilder(int startIndex, List<Integer> input, MultiMap<Pair<Integer,Integer>,Integer> outputBag){
            this.startIndex = startIndex;
            this.input = input;
            this.outputBag = outputBag;
        }
        
        public void run() {
            MultiMap<Pair<Integer,Integer>,Integer> tempBag = new MultiMap<Pair<Integer,Integer>,Integer>();
            int index = startIndex;
            for (Integer id : input){
                Domain[] domains = computedDomains.get(id);
                for (int i = 0; i < domains.length; i++){
                    for (int d : domains[i].integerSet().values()){
                        tempBag.put(new Pair<Integer,Integer>(i,d), index);
                    }
                }
                index++;
            }
            synchronized (outputBag){
                outputBag.putAll(tempBag);
            }
        }
    }

    private List<Integer> buildSplitBag(List<Integer> candidates, MultiMap<Pair<Integer,Integer>,Integer> outputBag){
        List<List<Integer>> splitted = Sugar.splitList(candidates, Runtime.getRuntime().availableProcessors());
        List<Runnable> tasks = new ArrayList<Runnable>();
        int index = 0;
        for (List<Integer> list : splitted){
            tasks.add(new SplitBagBuilder(index, list, outputBag));
            index += list.size();
        }
        parallel.runTasks(tasks);
        return Sugar.flatten(splitted);
    }

    private Pair<List<Integer>,List<Integer>> split(List<Integer> candidates){
        MultiMap<Pair<Integer,Integer>,Integer> bag = new MultiMap<Pair<Integer,Integer>,Integer>();
        candidates = buildSplitBag(candidates, bag);
        Set<Pair<Integer,Integer>> splits = bag.keySet();
        IntegerMultiMap<Pair<Integer,Integer>> ib = IntegerMultiMap.createIntegerMultiMap(bag);
        IntegerSet a = IntegerSet.emptySet;
        IntegerSet b = IntegerSet.emptySet;
        IntegerSet candidateIndices = IntegerSet.createIntegerSetFromRange(0, candidates.size());
        for (Pair<Integer,Integer> split : splits){
            if (candidates.size() > 4 && 2*candidates.size()-4*a.size() < candidates.size()){
                break;
            }
            if (candidates.size() > 4 && 2*candidates.size()-4*b.size() < candidates.size()){
                break;
            }
            IntegerSet newA = null;
            IntegerSet newB = null;
            if (this.mask[split.r]){
                newA = IntegerSet.union(ib.get(split), a);
                if (newA.size() <= candidates.size()/2+candidates.size()%2){
                    a = newA;
                }
                newB = IntegerSet.union(b, IntegerSet.difference(candidateIndices, ib.get(split)));
                if (newB.size() <= candidates.size()/2+candidates.size()%2){
                    b = newB;
                }
            } else {
                newA = IntegerSet.union(a, IntegerSet.difference(candidateIndices, ib.get(split)));
                if (newA.size() <= candidates.size()/2+candidates.size()%2){
                    a = newA;
                }
                newB = IntegerSet.union(b, ib.get(split));
                if (newB.size() <= candidates.size()/2+candidates.size()%2){
                    b = newB;
                }
            }
        }
        if (a.isEmpty()){
            Set<Integer> bSet = new HashSet<Integer>();
            for (int i : b.values()){
                bSet.add(candidates.get(i));
            }
            return new Pair(Sugar.listFromCollections(Sugar.collectionDifference(candidates, bSet)), Sugar.listFromCollections(bSet));
        } else {
            Set<Integer> aSet = new HashSet<Integer>();
            for (int i : a.values()){
                aSet.add(candidates.get(i));
            }
            return new Pair(Sugar.listFromCollections(aSet),Sugar.listFromCollections(Sugar.collectionDifference(candidates, aSet)));
        }
    }

    //[better_better, better_worse, worse_better, worse_worse]
    private Quadruple<List<Integer>,List<Integer>,
            List<Integer>,List<Integer>> split(List<Integer> better, List<Integer> worse){
        Set<Pair<Integer,Integer>> splits = new HashSet<Pair<Integer,Integer>>();
        MultiMap<Pair<Integer,Integer>,Integer> betterBag = new MultiMap<Pair<Integer,Integer>,Integer>();
        MultiMap<Pair<Integer,Integer>,Integer> worseBag = new MultiMap<Pair<Integer,Integer>,Integer>();

        better = buildSplitBag(better, betterBag);
        splits.addAll(betterBag.keySet());
        IntegerSet betterCI = IntegerSet.createIntegerSetFromRange(0, better.size());
        IntegerMultiMap<Pair<Integer,Integer>> betterIB = IntegerMultiMap.createIntegerMultiMap(betterBag);

        worse = buildSplitBag(worse, worseBag);
        splits.addAll(worseBag.keySet());
        IntegerSet worseCI = IntegerSet.createIntegerSetFromRange(0, worse.size());
        IntegerMultiMap<Pair<Integer,Integer>> worseIB = IntegerMultiMap.createIntegerMultiMap(worseBag);

        Set<Pair<Integer,Integer>> splitsOfBoth_Set = new LinkedHashSet<Pair<Integer,Integer>>();
        Map<Pair<Integer,Integer>,Integer> evals = new HashMap<Pair<Integer,Integer>,Integer>();
        for (Pair<Integer,Integer> pair : splits){
            if (betterIB.get(pair).size() > 0 && worseIB.get(pair).size() > 0){
                splitsOfBoth_Set.add(pair);
                evals.put(pair, Math.min(Math.abs(better.size()/2+better.size()%2-betterIB.get(pair).size()), worse.size()/2+worse.size()%2-worseIB.get(pair).size()));
            }
        }
        List<Pair<Integer,Integer>> splitsOfBoth = Sugar.listFromCollections(splitsOfBoth_Set);
        Sugar.sortDesc(splitsOfBoth, evals);
        IntegerSet betterBetter = IntegerSet.emptySet;
        IntegerSet betterWorse = IntegerSet.emptySet;
        IntegerSet worseBetter = IntegerSet.emptySet;
        IntegerSet worseWorse = IntegerSet.emptySet;
        for (Pair<Integer,Integer> split : splitsOfBoth){
            IntegerSet newBetterBetter = null;
            IntegerSet newBetterWorse = null;
            IntegerSet newWorseBetter = null;
            IntegerSet newWorseWorse = null;
            if (better.size() > 4 && 2*better.size()-4*betterBetter.size() < better.size() && worse.size() > 4 && 2*worse.size()-4*worseBetter.size() < better.size()){
                break;
            }
            if (better.size() > 4 && 2*better.size()-4*betterWorse.size() < better.size() && worse.size() > 4 && 2*worse.size()-4*worseWorse.size() < better.size()){
                break;
            }
            if (this.mask[split.r]){
                newBetterBetter = IntegerSet.union(betterIB.get(split), betterBetter);
                newWorseBetter = IntegerSet.union(worseIB.get(split), worseBetter);
                if (newBetterBetter.size() <= better.size()/2+better.size()%2 && newWorseBetter.size() <= worse.size()/2+worse.size()%2){
                    betterBetter = newBetterBetter;
                    worseBetter = newWorseBetter;
                }
                newBetterWorse = IntegerSet.union(betterWorse, IntegerSet.difference(betterCI, betterIB.get(split)));
                newWorseWorse = IntegerSet.union(worseWorse, IntegerSet.difference(worseCI, worseIB.get(split)));
                if (newBetterWorse.size() <= better.size()/2+better.size()%2 && newWorseWorse.size() <= worse.size()/2+worse.size()%2){
                    betterWorse = newBetterWorse;
                    worseWorse = newWorseWorse;
                }
            } else {
                newBetterBetter = IntegerSet.union(betterBetter, IntegerSet.difference(betterCI, betterIB.get(split)));
                newWorseBetter = IntegerSet.union(worseBetter, IntegerSet.difference(worseCI, worseIB.get(split)));
                if (newBetterBetter.size() <= better.size()/2+better.size()%2 && newWorseBetter.size() <= worse.size()/2+worse.size()%2){
                    betterBetter = newBetterBetter;
                    worseBetter = newWorseBetter;
                }
                newBetterWorse = IntegerSet.union(betterWorse, betterIB.get(split));
                newWorseWorse = IntegerSet.union(worseWorse, worseIB.get(split));
                if (newBetterWorse.size() <= better.size()/2+better.size()%2 && newWorseWorse.size() <= worse.size()/2+worse.size()%2){
                    betterWorse = newBetterWorse;
                    worseWorse = newWorseWorse;
                }
            }
        }
        if (betterBetter.isEmpty() || worseBetter.isEmpty()){
            Set<Integer> betterWorseSet = new HashSet<Integer>();
            Set<Integer> worseWorseSet = new HashSet<Integer>();
            for (int i : betterWorse.values()){
                betterWorseSet.add(better.get(i));
            }
            for (int i : worseWorse.values()){
                worseWorseSet.add(worse.get(i));
            }
            return new Quadruple(Sugar.listFromCollections(Sugar.collectionDifference(better, betterWorseSet)), Sugar.listFromCollections(betterWorseSet),
                    Sugar.listFromCollections(Sugar.collectionDifference(worse, worseWorseSet)), Sugar.listFromCollections(worseWorseSet));
        } else {
            Set<Integer> betterBetterSet = new HashSet<Integer>();
            Set<Integer> worseBetterSet = new HashSet<Integer>();
            for (int i : betterBetter.values()){
                betterBetterSet.add(better.get(i));
            }
            for (int i : worseBetter.values()){
                worseBetterSet.add(worse.get(i));
            }
            return new Quadruple(Sugar.listFromCollections(betterBetterSet), Sugar.listFromCollections(Sugar.collectionDifference(better, betterBetterSet)),
                    Sugar.listFromCollections(worseBetterSet), Sugar.listFromCollections(Sugar.collectionDifference(worse, worseBetterSet)));
        }
    }

    private List<Integer> filterIrrelevant(Collection<Integer> candidates){
        candidates = Sugar.listFromCollections(candidates);
        List<Integer> candidatesList = new LinkedList<Integer>();
        candidatesList.addAll(candidates);
        Map<Integer,Integer> evaluationMap = new HashMap<Integer,Integer>();
        for (Integer id : candidatesList){
            evaluationMap.put(id, sumSizes(computedDomains.get(id), mask)-sumSizes(computedDomains.get(id), negMask));
        }
        Sugar.sortDesc(candidatesList, evaluationMap);
        Iterator<Integer> iter1 = candidatesList.iterator();
        while (iter1.hasNext()){
            Integer id1 = iter1.next();
            Iterator<Integer> iter2 = candidatesList.iterator();
            middleLoop: while (iter2.hasNext()){
                Integer id2 = iter2.next();
                if (!id1.equals(id2)){
                    boolean couldADominateB = couldADominateB(computedDomains.get(id2), computedDomains.get(id1));
                    if (!couldADominateB){
                        continue middleLoop;
                    }
                    if (allAreSubsets(computedDomains.get(id1), computedDomains.get(id2), mask) && allAreSubsets(computedDomains.get(id2), computedDomains.get(id1), negMask)){
                        iter1.remove();
                        break;
                    }
                }
            }
        }
        return candidatesList;
    }

    private List<Integer> filterIrrelevant(List<Integer> better, List<Integer> worse){
        List<Integer> filtered = Collections.synchronizedList(new ArrayList<Integer>());
        List<FilterIrrelevant2> tasks = new ArrayList<FilterIrrelevant2>();
        better = Collections.synchronizedList(better);
        for (Integer pair : worse){
            tasks.add(new FilterIrrelevant2(better, Sugar.list(pair), filtered));
        }
        parallel.runTasks(tasks);
        return filtered;
    }

    private class FilterIrrelevant2 implements Runnable {

        private List<Integer> better;

        private List<Integer> worse;

        private List<Integer> output;

        public FilterIrrelevant2(List<Integer> better, List<Integer> worse, List<Integer> output){
            this.better = better;
            this.worse = worse;
            this.output = output;
        }

        public void run() {
            this.output.addAll(filterIrrelevantImpl(better, worse));
        }

    }

    private List<Integer> filterIrrelevantImpl(List<Integer> better, List<Integer> worse){
        Iterator<Integer> iter1 = worse.iterator();
        while (iter1.hasNext()){
            Integer id1 = iter1.next();
            Iterator<Integer> iter2 = better.iterator();
            middleLoop: while (iter2.hasNext()){
                Integer id2 = iter2.next();
                boolean couldADominateB = couldADominateB(computedDomains.get(id2), computedDomains.get(id1));
                if (!couldADominateB){
                    continue middleLoop;
                }
                if (allAreSubsets(computedDomains.get(id1), computedDomains.get(id2), mask) && allAreSubsets(computedDomains.get(id2), computedDomains.get(id1), negMask)){
                    iter1.remove();
                    break;
                }
            }
        }
        return worse;
    }

    private int sumSizes(Domain[] domains, boolean[] mask){
        int sum = 0;
        int index = 0;
        for (Domain is : domains){
            if (mask[index]){
                sum += is.integerSet().size();
            }
            index++;
        }
        return sum;
    }
    
    private boolean allAreSubsets(Domain[] a, Domain[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && !a[i].integerSet().isSubsetOf(b[i].integerSet())){
                return false;
            }
        }
        return true;
    }
    
    private boolean couldADominateB(Domain[] a, Domain[] b){
        for (int i = 0; i < a.length; i++){
            if ((a[i].integerSet().size() < b[i].integerSet().size() && mask[i]) || (b[i].integerSet().size() < a[i].integerSet().size() && negMask[i])){
                return false;
            }
        }
        return true;
    }
}
