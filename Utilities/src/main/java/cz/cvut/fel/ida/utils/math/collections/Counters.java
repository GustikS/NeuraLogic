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

import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.util.*;

/**
 * Class for tracking counts of objects.
 * 
 * @param <T> type of the objects for which counts should be tracked
 * @author Ondra
 */
public class Counters<T> {

    private final HashMap<T, Integer> map;
    private static final int INITIAL_CAPACITY = 16;

    /**
     * Creates a new empty instance of class Counters
     */
    public Counters() {
        this.map = new HashMap<>(INITIAL_CAPACITY);
    }

    public Counters(int expectedSize) {
        this.map = new HashMap<>(expectedSize);
    }

    public Counters(Collection<T> elements) {
        this(Math.max(INITIAL_CAPACITY, (int)(elements.size() / 0.75f)));
        for (T t : elements) {
            this.increment(t);
        }
    }

    /**
     * Creates new instance of class Counters<T> initialized with the counts given
     * as a set of pairs: [key, count]
     * @param <T> the type of the counted objects
     * @param pairs the initial counts specified as pairs: [key, count]
     * @return new instance of class Counters<T> initialized with the counts given
     * as a set of pairs: [key, count]
     */
    public static <T> Counters<T> createCounters(Set<Pair<T, Integer>> pairs) {
        Counters<T> counters = new Counters<>(Math.max(INITIAL_CAPACITY, (int)(pairs.size() / 0.75f)));
        for (Pair<T, Integer> pair : pairs) {
            counters.add(pair.r, pair.s);
        }
        return counters;
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     */
    public void add(T key, int value) {
        map.put(key, map.getOrDefault(key, 0) + value);
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     * @return the count associated to <em>key</em> after the modification
     */
    public int addPre(T key, int value) {
        int newValue = map.getOrDefault(key, 0) + value;
        map.put(key, newValue);
        return newValue;
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     * @return the count associated to <em>key</em> before the modification
     */
    public int addPost(T key, int value) {
        int oldValue = map.getOrDefault(key, 0);
        map.put(key, oldValue + value);
        return oldValue;
    }

    /**
     * Increments by 1 the count associated to the element <em>key</em>
     * @param key the element for which count should be incremented
     */
    public void increment(T key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }
    
    /**
     * Decrements by 1 the count associated to the element <em>key</em>
     * @param key the element for which count should be decremented
     */
    public void decrement(T key) {
        map.put(key, map.getOrDefault(key, 0) - 1);
    }
    
    /**
     * 
     * @param key the element for which we want to get the value
     * @return the value associated to element <em>key</em>
     */
    public int get(T key) {
        return map.getOrDefault(key, 0);
    }
    
    /**
     * Increments the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> after the modification
     */
    public int incrementPre(T key) {
        int newValue = map.getOrDefault(key, 0) + 1;
        map.put(key, newValue);
        return newValue;
    }
    
    /**
     * Increments the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> before the modification
     */
    public int incrementPost(T key) {
        int oldValue = map.getOrDefault(key, 0);
        map.put(key, oldValue + 1);
        return oldValue;
    }
    
    /**
     * Decrements the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> after the modification
     */
    public int decrementPre(T key) {
        int newValue = map.getOrDefault(key, 0) - 1;
        map.put(key, newValue);
        return newValue;
    }
    
    /**
     * Decrements the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> before the modification
     */
    public int decrementPost(T key) {
        int oldValue = map.getOrDefault(key, 0);
        map.put(key, oldValue - 1);
        return oldValue;
    }
    
    /**
     * 
     * @return all elements for which counts are tracked
     */
    public Set<T> keySet() {
        return this.map.keySet();
    }

    /**
     * 
     * @return the counts of all keys iterable one collection
     */
    public Collection<Integer> counts() {
        return this.map.values();
    }

    /**
     * Adds all keys and values of another instance of class BigIntegerCounters<T>.
     * @param counters the instance of class BigIntegerCounters<T> whose content should be added
     */
    public void addAll(Counters<T> counters) {
        for (Map.Entry<T, Integer> entry : counters.map.entrySet()) {
            map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    /**
     * Finds the minimum count value.
     * @return the minimum count, or Integer.MAX_VALUE if empty
     */
    public int findMinCount() {
        int min = Integer.MAX_VALUE;
        for (int value : map.values()) {
            if (value < min) {
                min = value;
            }
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    /**
     * Finds the maximum count value.
     * @return the maximum count, or Integer.MIN_VALUE if empty
     */
    public int findMaxCount() {
        int max = Integer.MIN_VALUE;
        for (int value : map.values()) {
            if (value > max) {
                max = value;
            }
        }
        return max == Integer.MIN_VALUE ? 0 : max;
    }

    /**
     * Finds the element with the minimum count.
     * @return the element with minimum count, or null if empty
     */
    public T findMin() {
        if (map.isEmpty()) {
            return null;
        }
        T minKey = null;
        int min = Integer.MAX_VALUE;
        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            int value = entry.getValue();
            if (value < min) {
                min = value;
                minKey = entry.getKey();
            }
        }
        return minKey;
    }

    /**
     * Finds the element with the maximum count.
     * @return the element with maximum count, or null if empty
     */
    public T findMax() {
        if (map.isEmpty()) {
            return null;
        }
        T maxKey = null;
        int max = Integer.MIN_VALUE;
        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            int value = entry.getValue();
            if (value > max) {
                max = value;
                maxKey = entry.getKey();
            }
        }
        return maxKey;
    }

    /**
     * Removes all tracking for the given key.
     * @param key the element to remove
     */
    public void remove(T key) {
        map.remove(key);
    }

    /**
     * Clears all counters.
     */
    public void clear() {
        map.clear();
    }

    /**
     * @return number of elements for which counts are tracked
     */
    public int size() {
        return this.map.size();
    }

    /**
     * @return true if no elements are being tracked
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * @return map in the form: element -> count
     */
    public Map<T, Integer> toMap() {
        return new HashMap<>(this.map);
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Counters) {
            return ((Counters<?>) o).map.equals(this.map);
        }
        return false;
    }
}