/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.aggregables;


import cz.cvut.fel.ida.logic.features.treeliker.AggregablesBuilder;
import cz.cvut.fel.ida.logic.features.treeliker.Aggregables;
import cz.cvut.fel.ida.logic.features.treeliker.Example;
import cz.cvut.fel.ida.logic.features.treeliker.PredicateDefinition;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;

/**
 * Class for construction of VoidAggregables objects, implementing interface AggregablesBuilder.
 * @author Ondra
 */
public class VoidAggregablesBuilder implements AggregablesBuilder {

    private static VoidAggregablesBuilder theOne = new VoidAggregablesBuilder();
    
    private VoidAggregablesBuilder(){}
    
    /**
     * Singleton method for creating instances of class VoidAggregablesBuilder
     * @return
     */
    public static VoidAggregablesBuilder construct(){
        return theOne;
    }
    
    public Aggregables construct(PredicateDefinition def, IntegerSet literalIDs, Example example) {
        return VoidAggregables.construct(literalIDs.size());
    }
    
}
