/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;


import cz.cvut.fel.ida.utils.math.collections.IntegerSet;

/**
 * Interface for a factory for building instances of class Aggregables (i.e. lists of instances implementing interface Aggregable).
 * @author Ondra
 */
public interface AggregablesBuilder {
    
    /**
     * Constructs a new Aggregables object - given a predicate definition from so-called template, which bears the information about which arguments
     * of the predicate represented by the instance of class PredicateDefinition def contain the information which
     * should be extracted into the aggregables.
     * 
     * @param def redicate definition, which bears the information about which arguments
     * of the repsective predicate contain the information which
     * @param literalIDs unique identifiers of literals from which the information should be extracted (note: these IDs are not IDs gotten
     * from method id() of class Literal, they are different)
     * @param example the example which contains the literals with the IDs from literalIDs set
     * @return new instance of class Aggregables
     */
    public Aggregables construct(PredicateDefinition def, IntegerSet literalIDs, Example example);
    
}
