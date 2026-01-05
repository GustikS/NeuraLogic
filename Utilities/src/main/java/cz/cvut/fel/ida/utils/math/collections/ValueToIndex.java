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

    private final HashMap<T, Integer> valueToIndex;
    private Object[] indexToValue;

    private int nextIndex;
    private int maxIndex;
    private int minIndex;
    private int size = 0;

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MIN_ALLOWED_INDEX = -16;
    private static final int ARRAY_OFFSET = -MIN_ALLOWED_INDEX;  // 16

    public ValueToIndex() {
        this(0);
    }

    public ValueToIndex(int startIndex) {
        if (startIndex < MIN_ALLOWED_INDEX) {
            throw new IllegalArgumentException(
                    "startIndex must be >= " + MIN_ALLOWED_INDEX + ", got " + startIndex);
        }

        this.nextIndex = startIndex;
        this.minIndex = startIndex;
        this.maxIndex = startIndex - 1;
        this.valueToIndex = new HashMap<>(INITIAL_CAPACITY);

        int initialArraySize = Math.max(startIndex + ARRAY_OFFSET + 16, INITIAL_CAPACITY + ARRAY_OFFSET);
        this.indexToValue = new Object[initialArraySize];
    }

    /**
     * Converts the given object <em>t</em> to a unique integer.
     * @param t the object
     * @return the unique integer representing the object
     */
    public int valueToIndex(T value) {
        Integer cachedIndex = valueToIndex.putIfAbsent(value, nextIndex);
        if (cachedIndex != null) {
            return cachedIndex;
        }

        int arrayPos = nextIndex + ARRAY_OFFSET;
        if (arrayPos >= indexToValue.length) {
            Object[] newArray = new Object[(int)(indexToValue.length * 1.5f) + 1];
            System.arraycopy(indexToValue, 0, newArray, 0, indexToValue.length);
            indexToValue = newArray;
        }

        indexToValue[arrayPos] = value;
        maxIndex = Math.max(maxIndex, nextIndex);
        minIndex = Math.min(minIndex, nextIndex);
        size++;

        return nextIndex++;
    }

    /**
     * Converts the given unique identifier back to the original object (the method
     * valueToIndex with this object must have had been called prior to calling this method,
     * otherwise the method would return null).
     * @param index the unique identifier of the object
     * @return the object corresponding to the given unique identifier or null if there is no such object
     */
    @SuppressWarnings("unchecked")
    public T indexToValue(int index) {
        int arrayPos = index + ARRAY_OFFSET;
        if (arrayPos < 0 || arrayPos >= indexToValue.length || indexToValue[arrayPos] == null) {
            return null;
        }
        return (T) indexToValue[arrayPos];
    }

    @SuppressWarnings("unchecked")
    public T getValue(int index) {
        int arrayPos = index + ARRAY_OFFSET;
        return arrayPos >= 0 && arrayPos < indexToValue.length ? (T) indexToValue[arrayPos] : null;
    }

    public int getIndex(T value) {
        Integer index = valueToIndex.get(value);
        return index != null ? index : -1;
    }

    public boolean containsValue(T value) {
        return valueToIndex.containsKey(value);
    }

    public boolean containsIndex(int index) {
        if (index < MIN_ALLOWED_INDEX || index >= nextIndex) {
            return false;
        }
        int arrayPos = index + ARRAY_OFFSET;
        return arrayPos >= 0 && arrayPos < indexToValue.length && indexToValue[arrayPos] != null;
    }

    /**
     * Adds the given pair unique identifier - object
     * @param index the unique identifier
     * @param value the object
     */
    public void put(int index, T value) {
        if (index < MIN_ALLOWED_INDEX) {
            throw new IllegalArgumentException(
                    "Index must be >= " + MIN_ALLOWED_INDEX + ", got " + index);
        }

        Integer oldIndex = valueToIndex.get(value);
        if (oldIndex != null && oldIndex == index) {
            return;  // No change
        }

        int arrayPos = index + ARRAY_OFFSET;

        // Grow array if necessary
        while (arrayPos >= indexToValue.length) {
            Object[] newArray = new Object[(int)(indexToValue.length * 1.5f) + 1];
            System.arraycopy(indexToValue, 0, newArray, 0, indexToValue.length);
            indexToValue = newArray;
        }

        // Remove old mapping if exists
        if (oldIndex != null) {
            int oldArrayPos = oldIndex + ARRAY_OFFSET;
            if (oldArrayPos >= 0 && oldArrayPos < indexToValue.length) {
                indexToValue[oldArrayPos] = null;
            }
        } else {
            size++;
        }

        // Insert new mapping
        valueToIndex.put(value, index);
        indexToValue[arrayPos] = value;

        nextIndex = Math.max(nextIndex, index + 1);
        maxIndex = Math.max(maxIndex, index);
        minIndex = Math.min(minIndex, index);
    }

    /**
     * Creates a set of unique identifiers for the objects iterable the given collection.
     * @param coll the collection of objects
     * @return the set of unique identifiers for the objects iterable the collection <em>coll</em>
     */
    public Set<Integer> valuesToIndices(Collection<T> coll) {
        Set<Integer> result = new HashSet<>((int)(coll.size() / LOAD_FACTOR) + 1);
        for (T value : coll) {
            result.add(valueToIndex(value));
        }
        return result;
    }

    /**
     * Creates a set of objects for the unique identifiers iterable the given collection.
     * @param coll the collection of unique identifiers
     * @return the set of objects for the unique identifiers iterable the collection <em>coll</em>
     */
    public Set<T> indicesToValues(Collection<Integer> coll) {
        Set<T> result = new HashSet<>((int)(coll.size() / LOAD_FACTOR) + 1);
        for (Integer index : coll) {
            int arrayPos = index + ARRAY_OFFSET;
            if (arrayPos >= 0 && arrayPos < indexToValue.length) {
                T value = (T) indexToValue[arrayPos];
                if (value != null) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    public Map<T, Integer> valuesToIndicesMap() {
        return Collections.unmodifiableMap(valueToIndex);
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, T> indicesToValuesMap() {
        Map<Integer, T> result = new HashMap<>();
        for (int arrayPos = 0; arrayPos < indexToValue.length; arrayPos++) {
            if (indexToValue[arrayPos] != null) {
                result.put(arrayPos - ARRAY_OFFSET, (T) indexToValue[arrayPos]);
            }
        }
        return result;
    }

    /**
     * 
     * @return number of elements for which there are the unique IDs
     */
    public int size() {
        return size;
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
        Set<Integer> result = new HashSet<>();
        for (int arrayPos = 0; arrayPos < indexToValue.length; arrayPos++) {
            if (indexToValue[arrayPos] != null) {
                result.add(arrayPos - ARRAY_OFFSET);
            }
        }
        return result;
    }

    /**
     * Returns the maximum index assigned.
     * @return the max index
     */
    public int max() {
        return maxIndex;
    }

    public int min() {
        return minIndex;
    }

    public int minAllowedIndex() {
        return MIN_ALLOWED_INDEX;
    }

    @Override
    public String toString() {
        return "ValueToIndex[size=" + size + ", range=[" + minIndex + ", " + maxIndex + "]]";
    }
}
