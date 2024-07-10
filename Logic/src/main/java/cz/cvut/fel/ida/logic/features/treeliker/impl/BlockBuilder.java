/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.*;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation class used by the feature construction algorithms which builds 
 * new Blocks by combining Blocks with Joins.
 * 
 * @author Ondra
 */
public class BlockBuilder implements Runnable {

    private Block oldPos;

    private int arg;

    private List<Block> current;

    private ConcurrentHashMap<Integer, Domain[]> literalDomains;

    private MultiMap<Pair<Integer,Integer>, Join> bagPosFeaturesWithLiteralsInDomain;

    private DomainComputing domainComputing;

    private Pruning pruning;
    
    private List<SyntaxChecker> syntaxCheckers;

    private boolean useIrrelevancyFiltering = false;
    
    /**
     * Creates a new instance of class BlockBuilder.
     * 
     * @param oldBlock the block which should be extended by adding various new combination of blocks (in the form of Joins) to one of its arguments.
     * @param arg the argument to which the Joins should be added
     * @param bagLiteralsWithLiteralsInDomain MultiMap computed using BlocksWithLiteralsInDomainsBuilder
     * @param literalDomains pre-computed domains of the combined blocks (this is actually being filled by methods in this class during their work)
     * @param current where the new generated blocks should be stored
     * @param domainComputing instance of the implementation class for computing domains
     * @param pruning instance of the implementation class for pruning of blocks
     * @param syntaxCheckers syntax checkers which can be used to filter the generated features based on syntactical constraints
     */
    public BlockBuilder(Block oldBlock, int arg, MultiMap<Pair<Integer,Integer>,Join> bagLiteralsWithLiteralsInDomain,
            ConcurrentHashMap<Integer,Domain[]> literalDomains, List<Block> current, DomainComputing domainComputing, Pruning pruning,
            List<SyntaxChecker> syntaxCheckers){
        this.oldPos = oldBlock;
        this.arg = arg;
        this.bagPosFeaturesWithLiteralsInDomain = bagLiteralsWithLiteralsInDomain;
        this.literalDomains = literalDomains;
        this.current = current;
        this.domainComputing = domainComputing;
        this.pruning = pruning;
        this.syntaxCheckers = syntaxCheckers;
    }

    public void run(){
        Domain[] oldPosDomains = literalDomains.get(oldPos.id());
        Set<Join> candidates = new HashSet<Join>();
        for (Domain oldPosDomain : oldPosDomains){
            for (int literalID : oldPosDomain.integerSet().values()){
                candidates.addAll(bagPosFeaturesWithLiteralsInDomain.get(new Pair<Integer,Integer>(arg,literalID)));
            }
        }
        List<Block> generated = new ArrayList<Block>();
        outerLoop: for (Join join : candidates){
            Block newBlock = oldPos.shallowCopy();
            newBlock.setChildren(join, arg);
            if (checkSyntax(newBlock)){
                generated.add(newBlock);
                literalDomains.put(newBlock.id(), domainComputing.computeLiteralDomains(newBlock));
            }
        }
        int remainingOutputs = 0;
        for (int i = arg+1; i < oldPos.arity(); i++){
            if (oldPos.modes()[i] == PredicateDefinition.OUTPUT){
                remainingOutputs++;
            }
        }
        if (remainingOutputs > 0 && useIrrelevancyFiltering){
            current.addAll(pruning.filterIrrelevantBlocksInParallel(generated, literalDomains));
        } else {
            current.addAll(generated);
        }
    }
    
    private boolean checkSyntax(Block block){
        for (SyntaxChecker syntaxChecker : this.syntaxCheckers){
            if (!syntaxChecker.check(block)){
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
}