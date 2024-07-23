/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.*;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.collections.Counters;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation class for creating combinations (Joins) of already generated Blocks.
 * It works by extending already generated Joins (the empty Join is used in the first iteration to create the first collection of Joins)
 * 
 * @author Ondra
 */
public class CombinationsBuilder implements Runnable {

    private JoinAndInt originalJoin;

    private ConcurrentHashMap<List<Domain>,Integer> redundancyMap;

    private ConcurrentHashMap<Block,Integer> indices;

    private ConcurrentHashMap<Pair<Block,Block>,Boolean> hSubsumptions;

    private MultiList<Integer,JoinAndInt> hbl;

    private int combinationSize;

    private MultiMap<Pair<Integer,Integer>,Block> termsInDomainsOf;

    private DomainComputing domainComputing;

    private HSubsumption hsubsumptionEngine;

    private Pruning pruning;

    private boolean[] mask, negMask;
    
    private List<SyntaxChecker> syntaxCheckers;
    
    private boolean useIrrelevancyFiltering = false;
    
    private boolean useRepetitions = false;

    /**
     * Creates a new instance of class CombinationsBuilder
     * @param originalJoin the Join which should be extended
     * @param redundancyMap map used to filter redundant joins (i.e. joins with the same domains for all examples).
     * @param indices Map which contains pairs: Block -> its index in the list of blocks from which the combinations are built
     * @param hSubsumptions Map containing pre-computed H-subsumption relations (see Kuzelka, Zelezny, MLJ, 2011 for definition) between Blocks
     * @param hbl wMultiList here the new Joins should be stored, keys are numbers of Blocks in the Joins, valuea re sets of JoinAndInt objects 
     * (the Join is the generated Join, the int is the index, to the list of Blocks, of the last Block in this Join (Joins are constructed from Blocks with increasing IDs)
     * @param combinationSize size of the new combinations
     * @param termsInDomainsOf MultiMap computed using the class TermsInDomainsOfBuilder
     * @param domainComputing an instance of the implementation class DomainComputing for computing domains
     * @param hsubsumption an instance of the implementation class HSubsumption for computing H-subsumption relations
     * @param pruning an instance of the implementation class Pruning for filtering of redundant Blocks and Joins
     * @param syntaxCheckers syntaxCheckers syntax checkers which can be used to filter the generated features based on syntactical constraints
     */
    public CombinationsBuilder(JoinAndInt originalJoin, ConcurrentHashMap<List<Domain>,Integer> redundancyMap,
            ConcurrentHashMap<Block,Integer> indices, ConcurrentHashMap<Pair<Block,Block>,Boolean> hSubsumptions, MultiList<Integer,JoinAndInt> hbl,
            int combinationSize, MultiMap<Pair<Integer,Integer>,Block> termsInDomainsOf, DomainComputing domainComputing, 
            HSubsumption hsubsumption, Pruning pruning, List<SyntaxChecker> syntaxCheckers){
        this.originalJoin = originalJoin;
        this.redundancyMap = redundancyMap;
        this.indices = indices;
        this.hSubsumptions = hSubsumptions;
        this.hbl = hbl;
        this.combinationSize = combinationSize;
        this.termsInDomainsOf = termsInDomainsOf;
        this.domainComputing = domainComputing;
        this.hsubsumptionEngine = hsubsumption;
        this.pruning = pruning;
        this.mask = pruning.getMask();
        this.negMask = pruning.getNegMask();
        this.syntaxCheckers = syntaxCheckers;
    }

    public void run() {
        Join join = originalJoin.r;
        Counters<Block> frequencyOnCoveredClass = new Counters<Block>();
        Domain[] termDomainsOfCombination = domainComputing.computeTermDomains(join);
        //must be a set
        Set<Pair<Block,Integer>> candidates0 = new LinkedHashSet<Pair<Block,Integer>>();
        for (int j = 0; j < termDomainsOfCombination.length; j++){
            for (int k : termDomainsOfCombination[j].integerSet().values()){
                for (Block candidate : termsInDomainsOf.get(new Pair<Integer,Integer>(j, k))){
                    int index = indices.get(candidate);
                    if ((index > originalJoin.s || (index == originalJoin.s && useRepetitions)) && checkBranchings(join, candidate) && mask[j] && frequencyOnCoveredClass.incrementPre(candidate) >= pruning.minFrequencyOnCoveredClass()){
                        candidates0.add(new Pair<Block,Integer>(candidate,index));
                    }
                }
            }
        }
        List<JoinAndInt> combinations = new ArrayList<JoinAndInt>();
        Map<Integer,Domain[]> combinationsDomains = new HashMap<Integer,Domain[]>();
        for (Pair<Block,Integer> p : candidates0){
            Block block = p.r;
            int index = p.s;
            if (!TreeLikerSettings.USE_H_REDUCTION || !becomesHReducible(originalJoin.r, p.r, hSubsumptions)){
                Join newJoin = join.addBlock(block);
                if (checkSyntax(newJoin)){
                    Domain[] blockDomains = domainComputing.computeTermDomains(block);
                    if (!TreeLikerSettings.USE_REDUNDANCY_FILTERING || !useIrrelevancyFiltering ||
                        (!Domain.allAreSubsets(termDomainsOfCombination, blockDomains, negMask) &&
                        !Domain.allAreSubsets(blockDomains, termDomainsOfCombination, negMask))){
                        Domain[] intersections = Domain.cross(termDomainsOfCombination, blockDomains);
                        if (Domain.countNonEmpty(intersections, mask) >= pruning.minFrequencyOnCoveredClass()){
                            List<Domain> asList = Arrays.asList(intersections);
                            if (TreeLikerSettings.USE_REDUNDANCY_FILTERING && getRedundancyMap().containsKey(asList)){
                                if (getRedundancyMap().get(asList) > block.size()+originalJoin.r.numLiterals()){
                                    getRedundancyMap().put(asList, block.size()+originalJoin.r.numLiterals());
                                    combinations.add(new JoinAndInt(newJoin, index));
                                    combinationsDomains.put(newJoin.id(), intersections);
                                }
                            } else {
                                combinations.add(new JoinAndInt(newJoin, index));
                                getRedundancyMap().put(asList, block.size()+originalJoin.r.numLiterals());
                                combinationsDomains.put(newJoin.id(), intersections);
                            }
                        }
                    }
                }
            }
        }
        if (useIrrelevancyFiltering){
            for (JoinAndInt candidate : pruning.filterIrrelevantCombinationsOfBlocksInParallel(combinations, combinationsDomains)){
                hbl.put(combinationSize+1, candidate);
            }
        } else {
            hbl.putAll(combinationSize+1, combinations);
        }
    }

    private boolean checkBranchings(Join join, Block block){
        Counters<Integer> counters = new Counters<Integer>();
        if (block.branchingFactors()[block.input()] > 0 && block.branchingFactors()[block.input()] < join.numBlocks()+1){
            return false;
        }
        if (block.branchingModPredicate() > 0){
            counters.increment(block.predicate());
        }
        for (Block inJoin : join){
            if (inJoin.branchingFactors()[inJoin.input()] > 0 && inJoin.branchingFactors()[inJoin.input()] < join.numBlocks()+1){
                    return false;
                }
            if (inJoin.branchingModPredicate() > 0){
                if (counters.incrementPre(inJoin.predicate()) > inJoin.branchingModPredicate()){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean becomesHReducible(Join join, Block block, Map<Pair<Block,Block>,Boolean> subsumptions){
        if (TreeLikerSettings.USE_H_REDUCTION){
            for (Block inJoin : join){
                if ((block.numAggregators() == 0 && hsubsumptionEngine.hSubsumption(block, inJoin, subsumptions))
                        || (inJoin.numAggregators() == 0 && hsubsumptionEngine.hSubsumption(inJoin, block, subsumptions))){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @return the redundancyMap (map used for filtering of Blocks or Joins with equal domains).
     */
    public ConcurrentHashMap<List<Domain>,Integer> getRedundancyMap() {
        return redundancyMap;
    }
    
    private boolean checkSyntax(Join join){
        for (SyntaxChecker syntaxChecker : this.syntaxCheckers){
            if (!syntaxChecker.check(join)){
                return false;
            }
        }
        return true;
    }

    /**
     * Sets whether reundancy as defined in (Kuzelka, Zelezny, MLJ 2011) should be used
     * or if the weaker version of redundancy should be used instead of it (defined
     * in Kuzelka, Zelezny, ILP - late breaking papers, 2008). The stronger version of redundancy
     * is used by RelF - it requires presence of at least two classes in a dataset in order to work properly.
     * @param useIrrelevancyFiltering true if the stronger version of redundancy should be used
     */
    public void setUseIrrelevancyFiltering(boolean useIrrelevancyFiltering) {
        this.useIrrelevancyFiltering = useIrrelevancyFiltering;
    }

    /**
     * Sets whether repetitions of the same block in a combination are allowed.
     * Normally, this is not desirable but it is desirable for example when we have 
     * two equal blocks with aggregation variables - in such case, the resulting combination
     * is not reducible.
     * @param useRepetitions boolean indicating whether repetitions of the same block should be allowed
     */
    public void setUseRepetitions(boolean useRepetitions) {
        this.useRepetitions = useRepetitions;
    }
}
