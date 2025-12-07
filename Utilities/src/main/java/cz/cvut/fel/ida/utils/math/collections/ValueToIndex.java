/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cz.cvut.fel.ida.utils.math.collections;

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

    private final HashMap<T, Integer> valueToIndex;
    private final HashMap<Integer, T> indexToValue;

    private static final int INITIAL_CAPACITY = 16;

    public ValueToIndex() {
        this(0);
    }

    public ValueToIndex(int startIndex) {
        this.lastIndex = startIndex;
        this.max = startIndex - 1;
        this.valueToIndex = new HashMap<>(INITIAL_CAPACITY);
        this.indexToValue = new HashMap<>(INITIAL_CAPACITY);
    }

    /**
     * Converts the given object <em>t</em> to a unique integer.
     * @param t the object
     * @return the unique integer representing the object
     */
    public int valueToIndex(T t) {
        Integer index = valueToIndex.get(t);
        if (index != null) {
            return index;
        }

        int newIndex = lastIndex++;
        valueToIndex.put(t, newIndex);
        indexToValue.put(newIndex, t);
        max = newIndex;
        return newIndex;
    }

    /**
     * Converts the given unique identifier back to the original object (the method
     * valueToIndex with this object must have had been called prior to calling this method,
     * otherwise the method would return null).
     * @param index the unique identifier of the object
     * @return the object corresponding to the given unique identifier or null if there is no such object
     */
    public T indexToValue(int index) {
        return indexToValue.get(index);
    }

    public T getValue(int index) {
        return indexToValue.get(index);
    }

    public int getIndex(T value) {
        Integer index = valueToIndex.get(value);
        return index != null ? index : -1;
    }

    public boolean containsValue(T value) {
        return valueToIndex.containsKey(value);
    }

    public boolean containsIndex(int index) {
        return indexToValue.containsKey(index);
    }

    /**
     * Adds the given pair unique identifier - object
     * @param key the unique identifier
     * @param value the object
     */
    public void put(int key, T value) {
        valueToIndex.put(value, key);
        indexToValue.put(key, value);

        if (key >= lastIndex) {
            lastIndex = key + 1;
            max = key;
        } else if (key > max) {
            max = key;
        }
    }

    /**
     * Creates a set of unique identifiers for the objects iterable the given collection.
     * @param coll the collection of objects
     * @return the set of unique identifiers for the objects iterable the collection <em>coll</em>
     */
    public Set<Integer> valuesToIndices(Collection<T> coll) {
        Set<Integer> retVal = new HashSet<>(coll.size());
        for (T t : coll) {
            retVal.add(valueToIndex(t));
        }
        return retVal;
    }

    /**
     * Creates a set of objects for the unique identifiers iterable the given collection.
     * @param coll the collection of unique identifiers
     * @return the set of objects for the unique identifiers iterable the collection <em>coll</em>
     */
    public Set<T> indicesToValues(Collection<Integer> coll) {
        Set<T> retVal = new HashSet<>(coll.size());
        for (Integer i : coll) {
            T value = indexToValue.get(i);
            if (value != null) {
                retVal.add(value);
            }
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
    public int size() {
        return valueToIndex.size();
    }

    /**
     * 
     * @return the objects for which there are the unique IDs
     */
    public Set<T> values() {
        return valueToIndex.keySet();
    }

    /**
     * 
     * @return the unique IDs
     */
    public Set<Integer> indices() {
        return indexToValue.keySet();
    }

    /**
     * Returns the maximum index assigned.
     * @return the max index
     */
    public int max() {
        return max;
    }

    @Override
    public String toString() {
        return valueToIndex.toString();
    }
}