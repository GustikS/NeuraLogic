/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.Block;
import cz.cvut.fel.ida.logic.features.treeliker.Domain;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation class for pre-computing MultiMaps containing
 * pairs: [EXAMPLE_ID, TERM] as keys and sets of Blocks which have TERM
 * in their term-domain computed w.r.t. the example with id EXAMPLE_ID.
 * 
 * @author Ondra
 */
public class TermsInDomainsOfBuilder implements Runnable {

    private final MultiMap<Pair<Integer,Integer>, Block> termsInDomainsOf;

    private Map<Integer, Domain[]> computedDomains;

    private ConcurrentHashMap<Integer,Block> posFeaturesLookup;

    private boolean[] mask;

    /**
     * Creates new instance of class TermsInDomainsOfBuilder.
     * @param termsInDomainsOf MultiMap to which the results should bge stored
     * @param computedDomains pre-computed term-domains of the blocks
     * @param blocksLookup Map which maps ids to Blocks
     * @param mask
     */
    public TermsInDomainsOfBuilder(MultiMap<Pair<Integer,Integer>,Block> termsInDomainsOf, Map<Integer,Domain[]> computedDomains,
            ConcurrentHashMap<Integer,Block> blocksLookup, boolean[] mask){
        this.termsInDomainsOf = termsInDomainsOf;
        this.computedDomains = computedDomains;
        this.posFeaturesLookup = blocksLookup;
        this.mask = mask;
    }

    public void run(){
        for (Map.Entry<Integer,Domain[]> entry : computedDomains.entrySet()){
            int exampleIndex = 0;
            for (Domain domain : entry.getValue()){
                if (mask[exampleIndex]){
                    synchronized (this.termsInDomainsOf){
                        for (int term : domain.integerSet().values()){
                            termsInDomainsOf.put(new Pair<Integer,Integer>(exampleIndex,term), posFeaturesLookup.get(entry.getKey()));
                        }
                    }
                }
                exampleIndex++;
            }
        }
    }
}