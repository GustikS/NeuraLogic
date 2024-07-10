/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;


import cz.cvut.fel.ida.logic.features.treeliker.Dataset;
import cz.cvut.fel.ida.logic.features.treeliker.Domain;
import cz.cvut.fel.ida.logic.features.treeliker.Example;
import cz.cvut.fel.ida.logic.features.treeliker.Join;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

/**
 * An implementation class for pre-computing MultiMaps containing
 * pairs: [EXAMPLE_ID, LITERAL] as keys and sets of Joins as values so that
 * .
 * 
 * @author Ondra
 */
public class BlocksWithLiteralsInDomainBuilder implements Runnable {

    private MultiMap<Pair<Integer,Integer>, Join> bagPosFeaturesWithLiteralsInDomain;

    private int argument;

    private int predicate;

    private Join join;

    private Dataset examples;

    /**
     * Creates a new instance of class BlocksWithLiteralsInDomainBuilder
     * @param join combination of blocks stored in a Join object which should be stored at appropriate sets in the MultiMap
     * @param predicate predicate of the literals for which we want to fill the blocksWithLiteralsInDomain
     * @param argument the argument of the literals for which we want to fill the blocksWithLiteralsInDomain
     * @param blocksWithLiteralsInDomain where the results should be stored
     * @param examples
     */
    public BlocksWithLiteralsInDomainBuilder(Join join, int predicate, int argument,
            MultiMap<Pair<Integer,Integer>,Join> blocksWithLiteralsInDomain, Dataset examples){
        this.join = join;
        this.predicate = predicate;
        this.argument = argument;
        this.bagPosFeaturesWithLiteralsInDomain = blocksWithLiteralsInDomain;
        this.examples = examples;
    }

    public void run(){
        Domain[] termDomains = new Domain[examples.countExamples()];
        Dataset localExamples = examples.shallowCopy();
        localExamples.reset();
        int index = 0;
        while (localExamples.hasNextExample()){
            termDomains[index] = join.termDomain(localExamples.nextExample());
            index++;
        }
        IntegerSet[] literalDomains = computeLiteralDomainsFromTermDomains(termDomains, predicate, argument);
        for (IntegerSet literalDomain : literalDomains){
            for (int literalID : literalDomain.values()){
                bagPosFeaturesWithLiteralsInDomain.put(new Pair<Integer,Integer>(argument, literalID), join);
            }
        }
    }

    private IntegerSet[] computeLiteralDomainsFromTermDomains(Domain[] termDomains, int predicate, int argument){
        IntegerSet[] literalDomains = new IntegerSet[examples.countExamples()];
        Dataset localExamples = examples.shallowCopy();
        localExamples.reset();
        int index = 0;
        while (localExamples.hasNextExample()){
            Example e = localExamples.nextExample();

            literalDomains[index] = e.getLiteralDomain(predicate, termDomains[index].integerSet(), argument);
            index++;
        }
        return literalDomains;
    }
}
