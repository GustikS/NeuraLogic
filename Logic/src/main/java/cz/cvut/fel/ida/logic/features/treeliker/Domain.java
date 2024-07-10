/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.features.treeliker.aggregables.VoidAggregables;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;

import java.util.Arrays;

/**
 * Class representing compound-domain consisting of domain (i.e. set of literals or terms
 * represented through integers) and Aggregables
 * @author Ondra
 */
public class Domain {
    
    private int hashCode = -1;
    
    private IntegerSet integerSet;
    
    private Aggregables aggregables;
    
    /**
     * Empy domain
     */
    public final static Domain emptyDomain = new Domain(IntegerSet.emptySet);
    
    /**
     * Creates a new instance of class Domain containing only the domain-part and
     * not the aggregables-part (the aggregables-part is set to VoidAggregables
     * which is a class for such purposes).
     * @param integerSet the domain represented through integers
     */
    public Domain(IntegerSet integerSet){
        this.integerSet = integerSet;
        this.aggregables = VoidAggregables.construct(integerSet.size());
    }
    
    /**
     * Create a new instance of class Domain containing the given domain (integerSet)
     * and aggregables (aggregables)
     * @param integerSet the domain represented using integers
     * @param aggregables aggregables associated to the domain
     */
    public Domain(IntegerSet integerSet, Aggregables aggregables){
        this.integerSet = integerSet;
        this.aggregables = aggregables;
    }
    
    /**
     * 
     * @return the integer-set part of domain
     */
    public IntegerSet integerSet(){
        return this.integerSet;
    }
    
    /**
     * 
     * @return the aggregables-part of domain
     */
    public Aggregables aggregables(){
        return this.aggregables;
    }
    
    /**
     * Computes cross-operation of domains in array <em>a</em> with the respctive 
     * domains in array <em>b</em>. The cross opearation first computed intersection
     * of the integer-domains, then selectes the aggregables associated to the values
     * in the intersection of the integer-domains and computes the cross-operation defined
     * in interface Aggregable.
     * 
     * @param a array of domains
     * @param b array of domains
     * @return result of operation cross
     */
    public static Domain[] cross(Domain[] a, Domain[] b){
        Domain[] retVal = new Domain[a.length];
        for (int i = 0; i < a.length; i++){
            retVal[i] = cross(a[i], b[i]);
        }
        return retVal;
    }
    
    /**
     * 
     * Computes cross-operation of domains <em>a</em> and  <em>b</em>. The cross opearation first computed intersection
     * of the integer-domains, then selectes the aggregables associated to the values
     * in the intersection of the integer-domains and computes the cross-operation defined
     * in interface Aggregable.
     * 
     * @param a domain
     * @param b domain
     * @return result of operation cross
     */
    public static Domain cross(Domain a, Domain b){
        IntegerSet newIntegerSet = IntegerSet.intersection(a.integerSet(), b.integerSet());
        if (newIntegerSet.isEmpty()){
            return emptyDomain;
        } else {
            Aggregables newAggregables = null;
            if (a.aggregables instanceof VoidAggregables || b.aggregables instanceof VoidAggregables){
                newAggregables = VoidAggregables.construct(newIntegerSet.size());
            } else {
                int indexA = 0;
                int indexB = 0;
                int[] newValues = newIntegerSet.values();
                int[] aValues = a.integerSet.values();
                int[] bValues = b.integerSet.values();
                newAggregables = new Aggregables(newValues.length);
                for (int i = 0; i < newValues.length; i++){
                    int val = newValues[i];
                    while (aValues[indexA] < val){
                        indexA++;
                    }
                    while (bValues[indexB] < val){
                        indexB++;
                    }
                    newAggregables.add(a.aggregables.get(indexA).cross(b.aggregables.get(indexB)));
                }
            }
            return new Domain(newIntegerSet, newAggregables);
        }
    }
    
    /**
     * Selects a subset of domain according to values in IntegerSet selectedValues.
     * The resulting domain contains integer-set which is intersection of the original
     * integer-set with the IntegerSet selectedValues and aggregables which were associated to
     * the values contained in the new integer-set.
     * 
     * @param selectedValues
     * @return
     */
    public Domain select(IntegerSet selectedValues){
        IntegerSet newIntegerSet = IntegerSet.intersection(this.integerSet, selectedValues);
        if (newIntegerSet.isEmpty()){
            return emptyDomain;
        } else {
            Aggregables newAggregables = new Aggregables(newIntegerSet.size());
            int[] values = this.integerSet.values();
            int[] selected = newIntegerSet.values();
            int index = 0;
            for (int i = 0; i < values.length; i++){
                if (values[i] == selected[index]){
                    newAggregables.add(this.aggregables.get(i));
                    index++;
                }
            }
            return new Domain(newIntegerSet, newAggregables);
        }
    }
    
    /**
     * 
     * @return true if the integer-set is empty, false otherwise
     */
    public boolean isEmpty(){
        return this.integerSet.isEmpty();
    }
    
    /**
     * 
     * @param key
     * @return the aggregable which is associated to integer-set domain-element <em>key</em>
     */
    public Aggregable getAggregableByDomainElement(int key){
        int index = Arrays.binarySearch(this.integerSet.values(), key);
        if (index >= 0){
            return this.aggregables.get(index);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Domain){
            Domain d = (Domain)o;
            if (this.hashCode() != d.hashCode()){
                return false;
            } else {
                return this.integerSet.equals(d.integerSet) && this.aggregables.equals(d.aggregables);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this.hashCode == -1){
            int hash = 7;
            hash = 97 * hash + (this.integerSet != null ? this.integerSet.hashCode() : 0);
            hash = 97 * hash + (this.aggregables != null ? this.aggregables.hashCode() : 0);
            this.hashCode = hash;
        }
        return this.hashCode;
    }
    
    @Override
    public String toString(){
        return "Domain["+ VectorUtils.intArrayToString(this.integerSet.values()) +", "+this.aggregables+"]";
    }
    
    /**
     * Checks if mask[i] && a[i].integerSet.isSubsetOf(b[i].integerSet) for all <em>i</em>.
     * @param a
     * @param b
     * @param mask - in which domains we are interested
     * @return true if mask[i] && a[i].integerSet.isSubsetOf(b[i].integerSet) for all <em>i</em>, false otherwise
     */
    public static boolean allAreSubsets(Domain[] a, Domain[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && !a[i].integerSet.isSubsetOf(b[i].integerSet)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Counts the number of non-empty domains in the given array - only the domains 
     * at indices defined by the mask are counted.
     * @param domains array of domains
     * @param mask mask
     * @return number of non-empty domains in the given array - only the domains 
     * at indices defined by the mask are counted.
     */
    public static int countNonEmpty(Domain[] domains, boolean[] mask){
        int count = 0;
        for (int i = 0; i < domains.length; i++){
            if (mask[i] && !domains[i].integerSet.isEmpty()){
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts the number of non-empty domains in given array.
     * @param domains array of domains
     * @return number of non-empty domains in the given array
     */
    public static int countNonEmpty(Domain[] domains){
        int count = 0;
        for (int i = 0; i < domains.length; i++){
            if (!domains[i].integerSet.isEmpty()){
                count++;
            }
        }
        return count;
    }
}
