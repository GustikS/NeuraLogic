/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.utils.collections;

import java.util.*;
import java.util.Map.Entry;

/**
 * MultiMap of Integers - it stores sets of integers as instances of class IntegerSet.
 * This means that constructs.building an IntegerMultiMap by adding values one by one is not
 * a good idea as it could be very slow (IntegerSets are immutable so every addition
 * of an element to a MultiMap means creating a new IntegerSet which is slow).
 * @param <R> type of key-elements
 * @author Ondra
 */
public class IntegerMultiMap<R> {
    
    private Map<R,IntegerSet> map = new HashMap<R,IntegerSet>();

    /**
     * Creates a new instance of IntegerMulttiMap from an instance of class MultiMap<R,Integer>
     * @param <R> type of key-elements iterable the new constructed IntegerMultiMap
     * @param multiMap MultiMap from which the IntegerMultiMap should be constructed
     * @return a new instance of IntegerMulttiMap from the given instance of class MultiMap<R,Integer>
     */
    public static <R> IntegerMultiMap createIntegerMultiMap(MultiMap<R,Integer> multiMap){
        IntegerMultiMap<R> ib = new IntegerMultiMap<R>();
        for (Map.Entry<R,Set<Integer>> entry : multiMap.entrySet()){
            ib.add(entry.getKey(), IntegerSet.createIntegerSet(entry.getValue()));
        }
        return ib;
    }
    
    /**
     * 
     * @return number of different key-values iterable the IntegerMultiMap
     */
    public int size() {
        return map.size();
    }

    /**
     * 
     * @return true if the IntegerMultiMap is empty, false otherwise
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Checks if the IntegerMultiMap contains the given key-value (and an associated IntegerSet).
     * @param key the key
     * @return true if the IntegerMultiMap contains the given key-value
     */
    public boolean containsKey(R key) {
        return map.containsKey(key);
    }

    /**
     * 
     * @param key the key
     * @return IntegerKey associated to <em>key</em>. If there is no IntegerSet associated to <em>key</em> then
     * IntegerSet.emptySet is returned.
     */
    public IntegerSet get(R key) {
        if (map.containsKey(key))
            return map.get(key);
        else
            return IntegerSet.emptySet;
    }

    /**
     * Adds a new set of integers iterable the form of IntegerSet object.
     * @param key the key
     * @param value the value
     */
    public void add(R key, IntegerSet value) {
        if (!map.containsKey(key)){
            map.put(key, value);
        } else {
            map.put(key, IntegerSet.union(value, map.get(key)));
        }
    }
    
    /**
     * Removes the IntegerSet associated to the given key.
     * @param key the key
     */
    public void remove(R key){
        map.remove(key);
    }
    
    /**
     * Clears the IntegerMultiMap.
     */
    public void clear() {
        map.clear();
    }

    /**
     * 
     * @return all key-values contained iterable the IntegerMultiMap
     */
    public Set<R> keySet() {
        return map.keySet();
    }

    /**
     * 
     * @return all IntegerSets contained iterable the IntegerMultiMap
     */
    public Collection<IntegerSet> values() {
        return map.values();
    }

    /**
     * 
     * @return the entry-set of the IntegerMultiMap
     */
    public Set<Entry<R, IntegerSet>> entrySet() {
        return map.entrySet();
    }
    
    @Override
    public String toString(){
        return this.map.toString();
    }

    @Override
    public int hashCode(){
        return this.map.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof MultiMap) {
            return this.map.equals(((IntegerMultiMap)o).map);
        }
        return false;
    }
}