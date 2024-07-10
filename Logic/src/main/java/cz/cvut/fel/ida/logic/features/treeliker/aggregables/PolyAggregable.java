/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.aggregables;


import cz.cvut.fel.ida.logic.features.treeliker.Aggregable;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Tuple;
import cz.cvut.fel.ida.utils.math.Cache;
import cz.cvut.fel.ida.utils.math.VectorUtils;

import java.util.*;

/**
 * Class for polynomial aggregation implementing interface Aggregable.
 * @author Ondra
 */
public class PolyAggregable implements Aggregable {
    
    private int numVariables;
    
    private int maxDegree;
    
    private double samples;

    private double[] monomials;

    private int hashCode = -1;
    
    private static Cache<Pair<Integer,Integer>, List<Tuple<Integer>>> monomialCache = new Cache<Pair<Integer,Integer>,List<Tuple<Integer>>>();
    
    /**
     * Creates new instance of class PolyAggregable
     * @param numVariables number of numerical variables in the monomials
     * @param maxDegree maximum degree of the monomials
     * @param monomials values of the monomials (note that monomials are represented implicitly - we have the number of variables
     * and maximum degree, so we may create an ordering of the monomials an represented them implicitly by their position in the ordering)
     * @param samples number of samples (size of the sample set - see Kuzelka, et al, ECMLPKDD 2011) used to compute the monomials
     */
    public PolyAggregable(int numVariables, int maxDegree, double[] monomials, double samples){
        this.numVariables = numVariables;
        this.maxDegree = maxDegree;
        this.monomials = monomials;
        this.samples = samples;
    }
    
    public Aggregable cross(Aggregable agg) {
        PolyAggregable pa = (PolyAggregable)agg;
        if (pa.numVariables == 0){
            return new PolyAggregable(this.numVariables, this.maxDegree, this.monomials, this.samples*pa.samples);
        } else if (this.numVariables == 0){
            return new PolyAggregable(pa.numVariables, pa.maxDegree, pa.monomials, this.samples*pa.samples);
        } else {
            Map<Tuple<Integer>,Double> values = new HashMap<Tuple<Integer>,Double>();
            List<Tuple<Integer>> myExponents = allMonomialExponents(this.numVariables, this.maxDegree);
            List<Tuple<Integer>> paExponents = allMonomialExponents(pa.numVariables, pa.maxDegree);
            for (int i = 0; i < this.monomials.length; i++){
                Tuple<Integer> myExponent = myExponents.get(i);
                for (int j = 0; j < pa.monomials.length; j++){
                    Tuple<Integer> paExponent = paExponents.get(j);
                    if (myExponent.getExtraInt()+paExponent.getExtraInt() <= this.maxDegree){
                        values.put(Tuple.merge(myExponent, paExponent), this.monomials[i]*pa.monomials[j]);
                    }
                }
            }
            List<Tuple<Integer>> newExponents = allMonomialExponents(this.numVariables+pa.numVariables, pa.maxDegree);
            double[] newValues = new double[newExponents.size()];
            int index = 0;
            for (Tuple<Integer> e : newExponents){
                newValues[index] = values.get(e);
                index++;
            }
            return new PolyAggregable(pa.numVariables+this.numVariables, this.maxDegree, newValues, this.samples*pa.samples);
        }
    }

    public Aggregable plus(Aggregable agg) {
        PolyAggregable pa = (PolyAggregable)agg;
        if (this.numVariables != pa.numVariables || pa.maxDegree != this.maxDegree){
            throw new IllegalArgumentException("The operations plus can be applied only to aggregables with equal numVariables and maxDegree");
        }
        double[] newMonomials = new double[monomials.length];
        for (int i = 0; i < newMonomials.length; i++){
            newMonomials[i] = (this.samples*this.monomials[i]+pa.samples*pa.monomials[i])/(this.samples+pa.samples);
        }
        return new PolyAggregable(this.numVariables, this.maxDegree, newMonomials, this.samples+pa.samples);
    }

    /**
     * 
     * @return number of samples used to compute the monomials
     */
    public double samples() {
        return samples;
    }

    /**
     * @return the values of monomials
     */
    public double[] monomials() {
        return monomials;
    }


    @Override
    public int hashCode() {
        if (this.hashCode == -1){
            int hash = 7;
            hash = 53 * hash + (int) (Double.doubleToLongBits(this.samples) ^ (Double.doubleToLongBits(this.samples) >>> 32));
            hash = 53 * hash + Arrays.hashCode(this.monomials);
            this.hashCode = hash;
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PolyAggregable other = (PolyAggregable) obj;
        if (this.hashCode() != other.hashCode()){
            return false;
        }
        if (Double.doubleToLongBits(this.samples) != Double.doubleToLongBits(other.samples)) {
            return false;
        }
        if (!Arrays.equals(this.monomials, other.monomials)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        return "PolyAggregable[monomials="+ VectorUtils.doubleArrayToString(monomials)+", n = "+samples+"]";
    }
    
    /**
     * Monomials are represented implicitly - we have the number of variables
     * and maximum degree, so we may create an ordering of the monomials an represented them implicitly by their position in the ordering.
     * This method gives us explicit representation of these monomials in terms of exponents of the individual variables
     * @param numVariables number of variables in the monomials
     * @param maxDegree maximum degree of the monomials
     * @return
     */
    public static List<Tuple<Integer>> allMonomialExponents(int numVariables, int maxDegree){
        List<Tuple<Integer>> retVal = null;
        if ((retVal = monomialCache.get(new Pair<Integer,Integer>(numVariables, maxDegree))) == null){
            List<Tuple<Integer>> monomials = new ArrayList<Tuple<Integer>>();
            monomials.add(new Tuple<Integer>());
            for (int i = 0; i < numVariables; i++){
                List<Tuple<Integer>> newMonomials = new ArrayList<Tuple<Integer>>();
                for (Tuple<Integer> m : monomials){
                    for (int j = 1; j <= maxDegree-m.getExtraInt(); j++){
                        Tuple<Integer> newTuple = Tuple.append(m,j);
                        newTuple.setExtraInt(m.getExtraInt()+j);
                        newMonomials.add(newTuple);
                    }
                }
                monomials = newMonomials;
            }
            monomialCache.put(new Pair<Integer,Integer>(numVariables, maxDegree), monomials);
            retVal = monomials;
        }
        return retVal;
    }
    
    /**
     * 
     * @return maximum degree of monomials
     */
    public int maxDegree(){
        return maxDegree;
    }
}
