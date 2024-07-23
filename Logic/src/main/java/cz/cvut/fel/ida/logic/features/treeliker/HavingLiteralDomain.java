/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

/**
 * Interface implemented by objects which have literal-domain (see e.g. class Block)
 * 
 * @author Ondra
 */
public interface HavingLiteralDomain {
    
    /**
     * 
     * @return unique odentifier of the object
     */
    public int id();
    
    /**
     * 
     * @param example example w.r.t. which literal-domain should be computed
     * @return literal-domain
     */
    public Domain literalDomain(Example example);
    
}
