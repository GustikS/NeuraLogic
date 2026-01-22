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

package cz.cvut.fel.ida.utils.math.collections;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * Class for datastructure which roughly coprresponds to java.util.Map<R,java.util.Set<S>>.
 *
 * @param <R> type of key-elements
 * @param <S> type of value-elements
 * @author Ondra
 */
public class MultiMap<R,S> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private final Set<?> emptySet = new LinkedHashSet<>();

    private final Map<R, Set<S>> map;

    public MultiMap() {
        this(DEFAULT_CAPACITY);
    }

    public MultiMap(int initialCapacity) {
        this.map = new HashMap<>(initialCapacity, LOAD_FACTOR);
    }

    public int size() {
        return map.size();
    }

    /**
     * Checks if the MultiMap is empty.
     * @return true if the MultiMap is empty, false otherwise.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Checks if the MultiMap contains the given key.
     * @param key the key
     * @return true if the MultiMap contains the given key
     */
    public boolean containsKey(R key) {
        return map.containsKey(key);
    }

    /**
     * Returns a set of elements associated to <em>key</em>. If there is no set associated to <em>key</em> then
     * an empty set is returned.
     * @param key the key
     * @return list of elements associated to <em>key</em>.
     */
    public Set<S> get(R key) {
        Set<S> result = map.get(key);
        return result != null ? result : (Set<S>) emptySet;
    }

    /**
     * Returns a set of elements associated to keys from the given set <em>keys</em>. If there is no set associated to any of the keys then
     * an empty set is returned.
     * @param keys the keys
     * @return list of elements associated to <em>key</em>.
     */
    public Set<S> getAll(Set<R> keys) {
        if (keys == null || keys.isEmpty()) {
            return (Set<S>) emptySet;
        }

        // Pre-size based on expected result size
        Set<S> result = new HashSet<>();
        for (R key : keys) {
            Set<S> values = map.get(key);
            if (values != null) {
                result.addAll(values);
            }
        }
        return result;
    }

    /**
     * Adds the key-value pair to the MultiMap. It does not matter whether the MultiMap already contains
     * this key-value pair, the value will be simply added to the list associated with the given key.
     * @param key the key
     * @param value the value
     */
    public void put(R key, S value) {
        map.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(value);
    }

    /**
     * Adds all the key-value pair to the MultiMap. It does not matter whether the MultiMap already contains
     * any of the key-value pairs, the values will be simply added to the list associated with the given key.
     * @param key the key
     * @param values the collection of values
     */
    public void putAll(R key, Collection<S> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        map.computeIfAbsent(key, k -> new LinkedHashSet<>()).addAll(values);
    }

    /**
     * Adds all key-value pairs contained iterable the given MultiMap to this MultiMap.
     * @param multiMap the MultiMap whose key-value pairs should be added
     */
    public void putAll(MultiMap<R, S> multiMap) {
        if (multiMap == null || multiMap.isEmpty()) {
            return;
        }
        for (Entry<R, Set<S>> entry : multiMap.map.entrySet()) {
            Set<S> targetSet = map.computeIfAbsent(entry.getKey(), k -> new LinkedHashSet<>());
            targetSet.addAll(entry.getValue());
        }
    }

    /**
     * Sets the values associated with the given key.
     * @param key the key
     * @param value the new values
     */
    public void set(R key, Set<S> value) {
        map.put(key, value);
    }

    /**
     * Sets the values associated with the given key.
     * @param key the key
     * @param value the new values
     */
    public void set(R key, Collection<S> value) {
        Set<S> targetSet = map.computeIfAbsent(key, k -> new LinkedHashSet<>(value.size()));
        targetSet.clear();
        targetSet.addAll(value);
    }

    /**
     * Removes the first occurrence of the given value from the set associated with the given key.
     * @param key the key
     * @param value the value to be removed
     */
    public void remove(R key, S value) {
        Set<S> set = map.get(key);
        if (set != null && set.remove(value) && set.isEmpty()) {
            map.remove(key);
        }
    }

    /**
     * Removes all values associated with the given key.
     * @param key the key
     */
    public Set<S> remove(R key) {
        Set<S> removed = map.remove(key);
        return removed != null ? removed : (Set<S>) emptySet;
    }

    /**
     * Removes all values associated to keys from the given collection.
     * @param keys the keys for which the associated values should be removed
     * from the MultiMap
     */
    public void removeAll(Collection<R> keys) {
        if (keys != null) {
            keys.forEach(map::remove);
        }
    }

    /**
     * Removes everything from the MultiList.
     */
    public void clear() {
        map.clear();
    }

    /**
     * 
     * @return the set of all key-elements
     */
    public Set<R> keySet() {
        return map.keySet();
    }

    /**
     * 
     * @return a collection containing all the values
     */
    public Collection<Set<S>> values() {
        return map.values();
    }

    /**
     * 
     * @return the backing entry-set 
     */
    public Set<Entry<R, Set<S>>> entrySet() {
        return map.entrySet();
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
        if (o instanceof MultiMap) {
            return this.map.equals(((MultiMap) o).map);
        }
        return false;
    }

    /**
     * 
     * @return string with the numbers of elements associated to particular keys
     */
    public String sizesToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultiMap[");
        int index = 0;
        for (Entry<R, Set<S>> entry : this.map.entrySet()) {
            sb.append(entry.getKey()).append(" ~ ").append(entry.getValue().size());
            if (index++ < this.map.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void copyFrom(MultiMap<R, S> map) {
        for (Entry<R, Set<S>> entry : map.entrySet()) {
            this.map.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    public MultiMap<R, S> copy() {
        MultiMap<R, S> res = new MultiMap<>((int) (this.map.size() / LOAD_FACTOR + 1));

        for (Entry<R, Set<S>> entry : this.map.entrySet()) {
            res.map.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
        }

        return res;
    }

    /**
     * 
     * @return int[] array with numbers of elements associated to particular keys
     */
    public int[] sizes() {
        int[] sizes = new int[this.map.size()];
        int i = 0;
        for (Set<S> set : map.values()) {
            sizes[i++] = set.size();
        }
        return sizes;
    }
}
