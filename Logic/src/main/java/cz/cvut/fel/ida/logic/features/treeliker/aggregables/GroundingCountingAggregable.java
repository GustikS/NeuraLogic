/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.aggregables;

import cz.cvut.fel.ida.logic.features.treeliker.Aggregable;

import java.math.BigInteger;

/**
 * Aggregable used to compute number of true groundings of features.
 * 
 * @author Ondra
 */
public class GroundingCountingAggregable implements Aggregable {

    private BigInteger bigInteger;
    
    private static GroundingCountingAggregable one = new GroundingCountingAggregable(BigInteger.ONE);
    
    /**
     * Creates a new instance of class GroundingCountingAggregable
     * @param bigInteger the number of groundings
     */
    protected GroundingCountingAggregable(BigInteger bigInteger){
        this.bigInteger = bigInteger;
    }
    
    /**
     * 
     * @return GroundingCountingAggregable with just one true grounding
     */
    public static GroundingCountingAggregable one(){
        return one;
    }
    
    public Aggregable cross(Aggregable b) {
        GroundingCountingAggregable gca = (GroundingCountingAggregable)b;
        if (this.bigInteger.equals(BigInteger.ONE)){
            return b;
        } else if (gca.bigInteger.equals(BigInteger.ONE)){
            return this;
        } else {
            return new GroundingCountingAggregable(this.bigInteger.multiply(gca.bigInteger));
        }
    }

    public Aggregable plus(Aggregable b) {
        GroundingCountingAggregable gca = (GroundingCountingAggregable)b;
        return new GroundingCountingAggregable(this.bigInteger.add(gca.bigInteger));
    }
    
    @Override
    public String toString(){
        return bigInteger.toString();
    }
    
}
