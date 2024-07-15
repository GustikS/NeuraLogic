/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.Block;
import cz.cvut.fel.ida.logic.features.treeliker.PredicateDefinition;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.util.Map;

/**
 * Implementation class for computing H-subsumption relations between Blocks (see Kuzelka, Zelezny, MLJ, 2011)
 * @author Ondra
 */
public class HSubsumption {
    
    /**
     * Checks if Block b1 H-subsumed Block b2. It reuses previous results of H-subsumption stored in
     * Map subsumptions. 
     * @param b1 a Block
     * @param b2 a Block
     * @param subsumptions pre-computed H-subsumptions - it can be even empty and the method will still work correctly (but it will take a little longer time)
     * @return true if b1 H-subsumes b2, false otherwise
     */
    public boolean hSubsumption(Block b1, Block b2, Map<Pair<Block,Block>,Boolean> subsumptions){
        synchronized (subsumptions){
            if (subsumptions.containsKey(new Pair<Block,Block>(b1,b2))){
                return subsumptions.get(new Pair<Block,Block>(b1,b2));
            }
        }
        if (b1.predicate() != b2.predicate() || b1.arity() != b2.arity() || b1.input() != b2.input()
                || !b1.definition().equals(b2.definition())){
            synchronized (subsumptions){
                subsumptions.put(new Pair<Block,Block>(b1, b2), false);
            }
            return false;
        }
        for (int i = 0; i < b1.arity(); i++){
            if (b1.definition().modes()[i] == PredicateDefinition.OUTPUT){
                c1Loop: for (Block c1 : b1.children(i)){
                    for (Block c2 : b2.children(i)){
                        boolean containsKey = false;
                        synchronized (subsumptions){
                            containsKey = subsumptions.containsKey(new Pair<Block,Block>(c1,c2));
                        }
                        if (containsKey){
                            synchronized (subsumptions){
                                if (subsumptions.get(new Pair<Block,Block>(c1,c2))){
                                    continue c1Loop;
                                }
                            }
                        } else {
                            if (hSubsumption(c1,c2,subsumptions)){
                                continue c1Loop;
                            }
                        }
                    }
                    //i.e. there was no c2 such that c1 \hsbs c2
                    synchronized (subsumptions){
                        subsumptions.put(new Pair<Block,Block>(b1,b2), false);
                    }
                    return false;
                }
            }
        }
        synchronized (subsumptions){
            subsumptions.put(new Pair<Block,Block>(b1,b2), true);
        }
        return true;
    }
    
}
