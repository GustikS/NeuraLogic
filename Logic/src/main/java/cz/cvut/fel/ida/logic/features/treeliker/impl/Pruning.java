/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.*;
import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class providing pruning capabilities used by RelF (described in detail in Kuzelka, Zelezny, MLJ, 2011)
 * 
 * @author Ondra
 */
public class Pruning {
    
    private boolean[] mask;
    
    private boolean[] negMask;
    
    private int minFrequencyOnCoveredClass;

    /**
     * Creates a new instance of class Pruning.
     * 
     * @param mask mask denoting the positive examples (in the arrays with domains)
     * @param negMask mask denoting the negative examples (in the arrays with domains)
     * @param minFrequencyOnCoveredClass minimum frequency 
     */
    public Pruning(boolean[] mask, boolean[] negMask, int minFrequencyOnCoveredClass){
        this.mask = mask;
        this.negMask = negMask;
        this.minFrequencyOnCoveredClass = minFrequencyOnCoveredClass;
    }
    
    /**
     * Filters redundant blocks (i.e. blocks with the same domains) from the given collection of blocks. It uses multiple threads so that
     * it could exploit modern multi-core processors.
     * @param candidates collection of blocks to be filtered
     * @param domains domains of these blocks in a Map, in which keys are IDs of the Blocks (obtainable using id() method defined in class Block).
     * @return filtered collection of blocks
     */
    public Collection<Block> filterRedundantBlocksInParallel(Collection<Block> candidates, Map<Integer, Domain[]> domains){
        if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
            int processorCount = Runtime.getRuntime().availableProcessors();
            List<Collection<Block>> lists = Sugar.splitCollection(candidates, processorCount);
            List<Block> flattened = Collections.synchronizedList(new ArrayList<Block>());
            List<Runnable> tasks = new ArrayList<Runnable>();
            for (Collection<Block> list : lists){
                tasks.add(new BlockRedundancyFiltering(list, domains, flattened));
            }
            Sugar.runInParallel(tasks);
            Collection<Block> retVal = filterRedundantBlocks(flattened, domains);
            return retVal;
        } else {
            return Sugar.listFromCollections(candidates);
        }
    }

    /**
     * Filters redundant blocks (i.e. blocks with the same domains) from the given collection of blocks. 
     * @param candidates collection of blocks to be filtered
     * @param domains domains of these blocks in a Map, in which keys are IDs of the Blocks (obtainable using id() method defined in class Block).
     * @return filtered collection of blocks
     */
    public Collection<Block> filterRedundantBlocks(Collection<Block> candidates, Map<Integer,Domain[]> domains){
        if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
            ConcurrentHashMap<List<Domain>,Block> retVal = new ConcurrentHashMap<List<Domain>,Block>();
            int index = 0;
            for (Block block : candidates){
                Domain[] dom = domains.get(block.id());
                List<Domain> domainsInList = Arrays.asList(dom);
                Block equivalent = retVal.get(domainsInList);
                if (Domain.countNonEmpty(dom, getMask()) >= minFrequencyOnCoveredClass() && (equivalent == null || block.size() < equivalent.size())){
                    retVal.put(domainsInList, block);
                }
                index++;
            }
            return retVal.values();
        } else {
            return Sugar.listFromCollections(candidates);
        }
    }

   /**
     * Filters redundant joins (i.e. jopins with the same domains) represnted as instances of class JoinAndInt. It uses multiple threads so that
     * it could exploit modern multi-core processors.
     * @param candidates collection of JoinAndInt objects to be filtered
     * @param domains domains of the joins in a Map, in which keys are IDs of the Joins (obtainable using id() method defined in class JoinAndIne).
     * @return filtered collection of JoinAndInt objects
     */
    public Collection<JoinAndInt> filterRedundantCombinationsOfBlocksInParallel(Collection<JoinAndInt> candidates, Map<Integer, Domain[]> domains){
        if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
            int processorCount = TreeLikerSettings.PROCESSORS;
            List<Collection<JoinAndInt>> lists = Sugar.splitCollection(candidates, processorCount);
            Vector<JoinAndInt> flattened = new Vector<JoinAndInt>();
            List<Runnable> tasks = new ArrayList<Runnable>();
            for (Collection<JoinAndInt> list : lists){
                tasks.add(new JoinRedundancyFiltering(list, domains, flattened));
            }
            Sugar.runInParallel(tasks);
            //this should be already much smaller than the original set, however, it could be also paralelized (TODO)
            Collection<JoinAndInt> retVal = filterRedundantCombinationsOfBlocks(flattened, domains);
            return retVal;
        } else {
            return Sugar.listFromCollections(candidates);
        }
    }

    /**
     * Filters redundant joins (i.e. joins with the same domains) represnted as instances of class JoinAndInt. I
     * @param candidates collection of JoinAndInt objects to be filtered
     * @param domains domains of the joins in a Map, in which keys are IDs of the Joins (obtainable using id() method defined in class JoinAndIne).
     * @return filtered collection of JoinAndInt objects
     */
    public Collection<JoinAndInt> filterRedundantCombinationsOfBlocks(Collection<JoinAndInt> candidates, Map<Integer, Domain[]> domains){
        if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
            ConcurrentHashMap<List<Domain>,JoinAndInt> retVal = new ConcurrentHashMap<List<Domain>,JoinAndInt>();
            for (JoinAndInt pair : candidates){
                Join candidate = pair.r;
                Domain[] dom = domains.get(candidate.id());
                List<Domain> domainsInList = Arrays.asList(dom);
                JoinAndInt equivalent = retVal.get(domainsInList);
                if (Domain.countNonEmpty(dom, getMask()) >= minFrequencyOnCoveredClass()){
                    if ((equivalent == null || candidate.numLiterals() < equivalent.r.numLiterals())){
                        retVal.put(domainsInList, new JoinAndInt(candidate, pair.s));
                    }
                }
            }
            return retVal.values();
        } else {
            return Sugar.listFromCollections(candidates);
        }
    }

    /**
     * Filters irrelevant blocks from the given collection of blocks. It uses multiple threads so that
     * it could exploit modern multi-core processors.
     * @param candidates collection of blocks to be filtered
     * @param domains domains of these blocks in a Map, in which keys are IDs of the Blocks (obtainable using id() method defined in class Block).
     * @return filtered collection of blocks
     */
    public Collection<Block> filterIrrelevantBlocksInParallel(Collection<Block> candidates, Map<Integer,Domain[]> computedDomains){
        if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
            candidates = Sugar.listFromCollections(candidates);
            ConcurrentHashMap<Integer, Domain[]> domainsOfCombinations = new ConcurrentHashMap<Integer,Domain[]>();
            List<Integer> candidateIDs = new ArrayList<Integer>();
            for (Block block : candidates){
                domainsOfCombinations.put(block.id(), computedDomains.get(block.id()));
                candidateIDs.add(block.id());
            }
            
            Set<Integer> filteredIDs = Sugar.setFromCollections(new IrrelevancyFiltering(candidateIDs, domainsOfCombinations, getMask(), getNegMask()).filter());
            List<Block> retVal = new ArrayList<Block>();
            for (Block block : candidates){
                if (filteredIDs.contains(block.id())){
                    retVal.add(block);
                }
            }
            return retVal;
        } else {
            return Sugar.listFromCollections(candidates);
        }
    }

    /**
     * Filters irrelevant joins represnted as instances of class JoinAndInt. It uses multiple threads so that
     * it could exploit modern multi-core processors.
     * @param candidates collection of JoinAndInt objects to be filtered
     * @param domains domains of the joins in a Map, in which keys are IDs of the Joins (obtainable using id() method defined in class JoinAndIne).
     * @return filtered collection of JoinAndInt objects
     */
    public Collection<JoinAndInt> filterIrrelevantCombinationsOfBlocksInParallel(Collection<JoinAndInt> candidates, Map<Integer,Domain[]> domains){
        if (TreeLikerSettings.USE_REDUNDANCY_FILTERING){
            candidates = Sugar.listFromCollections(candidates);
            ConcurrentHashMap<Integer, Domain[]> domainsOfCombinations = new ConcurrentHashMap<Integer,Domain[]>();
            List<Integer> candidateIDs = new ArrayList<Integer>();
            for (JoinAndInt pair : candidates){
                domainsOfCombinations.put(pair.r.id(), domains.get(pair.r.id()));
                candidateIDs.add(pair.r.id());
            }
            
            Set<Integer> filteredIDs = Sugar.setFromCollections(new IrrelevancyFiltering(candidateIDs, domainsOfCombinations, getMask(), getNegMask()).filter());
            List<JoinAndInt> retVal = new ArrayList<JoinAndInt>();
            for (JoinAndInt pair : candidates){
                if (filteredIDs.contains(pair.r.id())){
                    retVal.add(pair);
                }
            }
            return retVal;
        } else {
            return Sugar.listFromCollections(candidates);
        }
    }

    /**
     * 
     * @return the minimum frequency on the covered class
     */
    public int minFrequencyOnCoveredClass() {
        return minFrequencyOnCoveredClass;
    }

    /**
     * Sets the minimum frequency on the covered-class
     * @param minFrequencyOnCoveredClass the minimum frequency on the covered class
     */
    public void setMinFrequencyOnCoveredClass(int minFrequencyOnCoveredClass) {
        this.minFrequencyOnCoveredClass = minFrequencyOnCoveredClass;
    }

    /**
     * @return the mask mask for positive examples
     */
    public boolean[] getMask() {
        return mask;
    }

    /**
     * @return the negMask mask for negative examples
     */
    public boolean[] getNegMask() {
        return negMask;
    }
    
    private class JoinRedundancyFiltering implements Runnable {

        private Collection<JoinAndInt> combinations;

        private Vector<JoinAndInt> output;

        private Map<Integer, Domain[]> domains;

        public JoinRedundancyFiltering(Collection<JoinAndInt> combinations, Map<Integer, Domain[]> domains, Vector<JoinAndInt> output){
            this.combinations = combinations;
            this.domains = domains;
            this.output = output;
        }

        public void run() {
            Collection<JoinAndInt> result = filterRedundantCombinationsOfBlocks(this.combinations, this.domains);
            for (JoinAndInt pair : result){
                this.output.add(pair);
            }
        }
    }
    
    private class BlockRedundancyFiltering implements Runnable {

        private Collection<Block> posFeatures;

        private List<Block> output;

        private Map<Integer, Domain[]> domains;

        public BlockRedundancyFiltering(Collection<Block> combinations, Map<Integer, Domain[]> domains, List<Block> output){
            this.posFeatures = combinations;
            this.domains = domains;
            this.output = output;
        }

        public void run() {
            Collection<Block> result = filterRedundantBlocks(this.posFeatures, this.domains);
            for (Block posFeat : result){
                this.output.add(posFeat);
            }
        }
    }
}
