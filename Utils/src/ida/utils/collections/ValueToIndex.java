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

/**
 * Class for converting objects to unique identifiers (integers) and back.
 * 
 * @param <T> type of the objects
 * @author Ondra
 */
public class ValueToIndex<T> {
    
    private int lastIndex = 0;

    private int max = 0;
    
    private LinkedHashMap<T,Integer> valueToIndex = new LinkedHashMap<T,Integer>();
    
    private LinkedHashMap<Integer,T> indexToValue = new LinkedHashMap<Integer,T>();

    public ValueToIndex(){}

    public ValueToIndex(int startIndex){
        this.lastIndex = startIndex;
    }

    /**
     * Converts the given object <em>t</em> to a unique integer.
     * @param t the object
     * @return the unique integer representing the object
     */
    public int valueToIndex(T t){
        if (!valueToIndex.containsKey(t)){
            valueToIndex.put(t, lastIndex);
            indexToValue.put(lastIndex, t);
            max = lastIndex;
            lastIndex++;
        }
        return valueToIndex.get(t);
    }
    
    /**
     * Converts the given unique identifier back to the original object (the method
     * valueToIndex with this object must have had been called prior to calling this method,
     * otherwise the method would return null).
     * @param index the unique identifier of the object
     * @return the object corresponding to the given unique identifier or null if there is no such object
     */
    public T indexToValue(int index){
        if (!indexToValue.containsKey(index))
            return null;
        else
            return indexToValue.get(index);
    }

    public T getValue(int index){
        return this.indexToValue.get(index);
    }

    public int getIndex(T value){
        return this.valueToIndex.get(value);
    }

    public boolean containsValue(T value){
        return this.valueToIndex.containsKey(value);
    }

    public boolean containsIndex(int index){
        return this.indexToValue.containsKey(index);
    }

    /**
     * Adds the given pair unique identifier - object
     * @param key the unique identifier
     * @param value the object
     */
    public void put(int key, T value){
        this.valueToIndex.put(value, key);
        this.indexToValue.put(key, value);
        this.lastIndex = Math.max(this.lastIndex, key+1);
        max = Math.max(lastIndex, key);
    }
    
    /**
     * Creates a set of unique identifiers for the objects iterable the given collection.
     * @param coll the collection of objects
     * @return the set of unique identifiers for the objects iterable the collection <em>coll</em>
     */
    public Set<Integer> valuesToIndices(Collection<T> coll){
        Set<Integer> retVal = new HashSet<Integer>();
        for (T t : coll){
            retVal.add(valueToIndex(t));
        }
        return retVal;
    }
    
    /**
     * Creates a set of objects for the unique identifiers iterable the given collection.
     * @param coll the collection of unique identifiers
     * @return the set of objects for the unique identifiers iterable the collection <em>coll</em>
     */
    public Set<T> indicesToValues(Collection<Integer> coll){
        Set<T> retVal = new HashSet<T>();
        for (Integer i : coll){
            retVal.add(indexToValue(i));
        }
        return retVal;
    }

    public Map<T,Integer> valuesToIndicesMap(){
        return this.valueToIndex;
    }

    public Map<Integer,T> indicesToValuesMap(){
        return this.indexToValue;
    }
    
    /**
     * 
     * @return number of elements for which there are the unique IDs
     */
    public int size(){
        return this.valueToIndex.size();
    }
    
    /**
     * 
     * @return the objects for which there are the unique IDs
     */
    public Set<T> values(){
        return valueToIndex.keySet();
    }
    
    /**
     * 
     * @return the unique IDs
     */
    public Set<Integer> indices(){
        return indexToValue.keySet();
    }
    
    @Override
    public String toString(){
        return this.valueToIndex.toString();
    }

    public int max(){
        return this.max;
    }
}
