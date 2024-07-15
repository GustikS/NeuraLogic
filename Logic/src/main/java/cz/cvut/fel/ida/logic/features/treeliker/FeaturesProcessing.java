/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class containing a method for applying functions on Blocks and their sub-blocks.
 * The method is a little different from method applyRecursively from class Block.
 * They are intended for applying methods on collections of blocks which may share some
 * of their sub-blocks - the methods take this into account and process each sub-block
 * (whether or not it is shared) just once.
 * 
 * @author Ondra
 */
public class FeaturesProcessing {
    
    /**
     * Applies recursively the given function on all blocks from collection <em>features</em>.
     * @param features the blocks to be processed 
     * @param transformFun the function to be applied
     */
    public static void process(Collection<Block> features, Sugar.VoidFun<Block> transformFun){
        Set<Integer> alreadyProcessed = new HashSet<Integer>();
        for (Block b : features){ 
            process(b, transformFun, alreadyProcessed);
        }
    }
    
    private static void process(Block b, Sugar.VoidFun<Block> transformFun, Set<Integer> alreadyProcessed){
        for (int i = 0; i < b.arity(); i++){
            if (b.children(i) != null){
                for (Block child : b.children(i)){
                    if (!alreadyProcessed.contains(b.id())){
                        process(child, transformFun, alreadyProcessed);
                    }
                }
            }
        }
        transformFun.apply(b);
        alreadyProcessed.add(b.id());
    }
    
}
