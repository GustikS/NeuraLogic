/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

/**
 * Interface for defining so-called aggregables which are objects which can be efficiently 
 * aggregated using operations cross and plus.
 * 
 * @author Ondra
 */
public interface Aggregable {
    
    /**
     * Computes cross-operation for this Aggregable and Aggregable b
     * @param b aggregable with which the cross-operation should be computed
     * @return result of the cross-operation of this and Aggregable b
     */
    public Aggregable cross(Aggregable b);
    
    /**
     * Computes plus-operation for this Aggregable and Aggregable b
     * @param b aggregable with which the plus-operation should be computed
     * @return result of the plus-operation of this and Aggregable b
     */
    public Aggregable plus(Aggregable b);
    
}
