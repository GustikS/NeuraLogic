/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

/**
 * Interface implemented by objects which have so-called term-domain (see e.g. classes Block or Join)
 * 
 * @author Ondra
 */
public interface HavingTermDomain {
    
    /**
     * @return unique identifier of the object
     */
    public int id();
    
    /**
     * 
     * @param example example w.r.t. which the term-domain should be computed
     * @return term-domain
     */
    public Domain termDomain(Example example);
    
}
