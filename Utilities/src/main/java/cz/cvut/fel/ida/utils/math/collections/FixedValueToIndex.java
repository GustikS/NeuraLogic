package cz.cvut.fel.ida.utils.math.collections;

import java.util.*;

public class FixedValueToIndex<T> {

    private int lastIndex = 0;
    private int max = -1;

    private final HashMap<T, Integer> valueToIndex;
    private final ArrayList<T> indexToValue;

    public FixedValueToIndex() {
        this.valueToIndex = new HashMap<>();
        this.indexToValue = new ArrayList<>();
    }

    public int valueToIndex(T t) {
        Integer index = valueToIndex.get(t);
        if (index != null) {
            return index;
        }

        int newIndex = lastIndex++;
        valueToIndex.put(t, newIndex);
        
        // Safely expand list if needed
        if (newIndex >= indexToValue.size()) {
            indexToValue.add(t);
        } else {
            indexToValue.set(newIndex, t);
        }
        
        if (newIndex > max) {
            max = newIndex;
        }
        return newIndex;
    }

    public T indexToValue(int index) {
        if (index < 0 || index >= indexToValue.size()) {
            return null;
        }
        return indexToValue.get(index);
    }

    public T getValue(int index) {
        if (index < 0 || index >= indexToValue.size()) {
            return null;
        }
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
        return index >= 0 && index < indexToValue.size() && indexToValue.get(index) != null;
    }

    public void put(int key, T value) {
        if (key < 0) {
            throw new IllegalArgumentException("Index must be non-negative");
        }

        valueToIndex.put(value, key);

        // Efficiently expand list to accommodate key
        while (key >= indexToValue.size()) {
            indexToValue.add(null);
        }
        indexToValue.set(key, value);

        // Update tracking variables
        if (key >= lastIndex) {
            lastIndex = key + 1;
        }
        if (key > max) {
            max = key;
        }
    }

    public Set<Integer> valuesToIndices(Collection<T> coll) {
        Set<Integer> retVal = new HashSet<>(coll.size());
        for (T t : coll) {
            retVal.add(valueToIndex(t));
        }
        return retVal;
    }

    public Set<T> indicesToValues(Collection<Integer> coll) {
        Set<T> retVal = new HashSet<>(coll.size());
        for (Integer i : coll) {
            if (i >= 0 && i < indexToValue.size()) {
                T value = indexToValue.get(i);
                if (value != null) {
                    retVal.add(value);
                }
            }
        }
        return retVal;
    }

    /**
     * @return number of elements for which there are unique IDs
     */
    public int size() {
        return valueToIndex.size();
    }

    /**
     * @return the objects for which there are unique IDs
     */
    public Set<T> values() {
        return valueToIndex.keySet();
    }

    /**
     * Returns the maximum index assigned.
     */
    public int max() {
        return max;
    }

    @Override
    public String toString() {
        return valueToIndex.toString();
    }
}
