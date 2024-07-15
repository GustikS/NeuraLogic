/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.aggregables;

import cz.cvut.fel.ida.logic.features.treeliker.Aggregable;

/**
 * Aggregable which does not do anything - it is used when we do not want any aggregation.
 * There is just one immutable instance of this class - singleton.
 * @author Ondra
 */
public class VoidAggregable implements Aggregable {

    private final static VoidAggregable theOne = new VoidAggregable();
    
    private VoidAggregable(){}
    
    /**
     * @return the one VoidAggregable object
     */
    public static VoidAggregable construct(){
        return theOne;
    }
    
    public Aggregable cross(Aggregable b) {
        return theOne;
    }

    public Aggregable plus(Aggregable b) {
        return theOne;
    }
    
    @Override
    public boolean equals(Object o){
        return o == this;
    }
    
    @Override
    public int hashCode(){
        return 1;
    }
}
