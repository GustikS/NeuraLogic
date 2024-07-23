/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.aggregables;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Class for representing Aggregables filled with VoidAggregable objects - it is 
 * very efficient as it does not have to do almost anything.
 * 
 * @author Ondra
 */
public class VoidAggregables extends AggregablesFilledWithOneElement<VoidAggregable> {
    
    private final static Object lock = new Object();
    
    private static SoftReference<HashMap<Integer,SoftReference<VoidAggregables>>> cache = new SoftReference<HashMap<Integer,SoftReference<VoidAggregables>>>(new HashMap<Integer,SoftReference<VoidAggregables>>());
    
    private VoidAggregables(VoidAggregable va, int size){
        super(va, size);
    }
    
    /**
     * Creates new instance of class VoidAggregables which "contains"(virtually of course)
     * <em>size</em> elements.
     * @param size number of VoidAggregable elements contained in this class
     */
    protected VoidAggregables(int size){
        super(VoidAggregable.construct(), size);
    }
    
    
    /**
     * Parametrized singleton-method - creates one VoidAggregables object for every value of <em>size</em> 
     * (which is used when calling the method construct).
     * @param size number of VoidAggregable objects
     * @return VoidAggregables object for every value of <em>size</em>  which is either created or obtained from cache
     */
    public static VoidAggregables construct(int size){
        VoidAggregables retVal = null;
        HashMap<Integer,SoftReference<VoidAggregables>> map = null;
        SoftReference<VoidAggregables> softRef = null;
        synchronized (lock){
            if ((map = cache.get()) != null && (softRef = map.get(size)) != null && (retVal = softRef.get()) != null){
                return retVal;
            }
            retVal = new VoidAggregables(size);
            if (map == null){
                map = new HashMap<Integer,SoftReference<VoidAggregables>>();
                cache = new SoftReference<HashMap<Integer,SoftReference<VoidAggregables>>>(map);
            }
            map.put(size, new SoftReference<VoidAggregables>(retVal));
        }
        return retVal;
    }
    
    public String toString(){
        return "VoidAggregables["+this.size()+"]";
    }
}
