/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;


import cz.cvut.fel.ida.utils.generic.tuples.Pair;

/**
 * Class representing a pair - Joain and int - used fr some internal purposes 
 * in classes HiFi, RelF and Poly
 * 
 * @author Ondra
 */
public class JoinAndInt extends Pair<Join,Integer> implements HavingTermDomain {

    /**
     * Creates new empty instance of class JoinAndInt.
     */
    public JoinAndInt(){}
    
    /**
     * Creates new instance of class JoinAndInt
     * @param join the Join-part
     * @param integer the int-part
     */
    public JoinAndInt(Join join, Integer integer){
        this.r = join;
        this.s = integer;
    }
    
    /**
     * 
     * @return unique identifier of this JoinAndInt
     */
    public int id() {
        return this.r.id();
    }

    /**
     * Returns term-domain of the Join-part of this JoinAndInt object
     * @param example example w.r.t. which term-domain should be computed
     * @return term-domain
     */
    public Domain termDomain(Example example) {
        return this.r.termDomain(example);
    }
    
}
