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

import ida.utils.tuples.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * Class for tracking counts of objects. 
 *
 * @param <T> type of the objects for which counts should be tracked
 * @author Ondra
 */
public class DoubleCounters<T> {

    private HashMap<T,Double> map = new HashMap<T,Double>();

    /**
     * Creates a new empty instance of class Counters
     */
    public DoubleCounters(){}

    public DoubleCounters(Collection<T> elements){
        this();
        for (T t : elements){
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
    public static <T> DoubleCounters<T> createCounters(Set<Pair<T,Double>> pairs){
        DoubleCounters<T> counters = new DoubleCounters<T>();
        for (Pair<T,Double> pair : pairs){
            counters.add(pair.r, pair.s);
        }
        return counters;
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     */
    public void add(T key, double value){
        addPre(key, value);
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     * @return the count associated to <em>key</em> after the modification
     */
    public double addPre(T key, double value){
        if (!map.containsKey(key)){
            map.put(key, value);
        } else {
            map.put(key, map.get(key)+value);
        }
        return map.get(key);
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     * @return the count associated to <em>key</em> before the modification
     */
    public double addPost(T key, double value){
        double oldValue = 0;
        if (!map.containsKey(key)){
            map.put(key, value);
        } else {
            oldValue = map.get(key);
            map.put(key, map.get(key)+value);
        }
        return oldValue;
    }

    /**
     * Increments by 1 the count associated to the element <em>key</em>
     * @param key the element for which count should be incremented
     */
    public void increment(T key){
        this.incrementPre(key);
    }

    /**
     * Decrements by 1 the count associated to the element <em>key</em>
     * @param key the element for which count should be decremented
     */
    public void decrement(T key){
        this.decrementPre(key);
    }

    /**
     *
     * @param key the element for which we want to get the value
     * @return the value associated to element <em>key</em>
     */
    public double get(T key){
        if (!map.containsKey(key)){
            return 0;
        } else {
            return map.get(key);
        }
    }

    /**
     * Increments the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> after the modification
     */
    public double incrementPre(T key){
        if (!map.containsKey(key)){
            map.put(key, 1.0);
        } else {
            map.put(key, map.get(key)+1);
        }
        return map.get(key);
    }

    /**
     * Increments the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> before the modification
     */
    public double incrementPost(T key){
        if (!map.containsKey(key)){
            map.put(key, 1.0);
        } else {
            map.put(key, map.get(key)+1);
        }
        return map.get(key)-1;
    }

    /**
     * Decrements the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> after the modification
     */
    public double decrementPre(T key){
        if (!map.containsKey(key)){
            map.put(key, -1.0);
        } else {
            map.put(key, map.get(key)-1);
        }
        return map.get(key);
    }

    /**
     * Decrements the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> before the modification
     */
    public double decrementPost(T key){
        if (!map.containsKey(key)){
            map.put(key, -1.0);
        } else {
            map.put(key, map.get(key)-1);
        }
        return map.get(key)+1;
    }

    /**
     *
     * @return all elements for which counts are tracked
     */
    public Set<T> keySet(){
        return this.map.keySet();
    }

    /**
     *
     * @return the counts of all keys iterable one collection
     */
    public Collection<Double> counts(){
        return this.map.values();
    }

    /**
     * Adds all keys and values of another instance of class BigIntegerCounters<T>.
     * @param counters the instance of class BigIntegerCounters<T> whose content should be added
     */
    public void addAll(DoubleCounters<T> counters){
        for (Map.Entry<T,Double> entry : counters.map.entrySet()){
            if (this.map.containsKey(entry.getKey())){
                this.map.put(entry.getKey(), this.map.get(entry.getKey())+entry.getValue());
            } else {
                this.map.put(entry.getKey(), entry.getValue());
            }
        }
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
        if (o instanceof DoubleCounters){
            return ((DoubleCounters)o).map.equals(this.map);
        }
        return false;
    }

    /**
     *
     * @return map iterable the following form: element -> count
     */
    public Map<T,Double> toMap(){
        HashMap<T,Double> copy = new HashMap<T,Double>();
        copy.putAll(this.map);
        return copy;
    }

    /**
     *
     * @return number of alements for which counts are tracked
     */
    public int size(){
        return this.map.size();
    }

    public double findMinCount(){
        double min = Double.POSITIVE_INFINITY;
        for (Map.Entry<T,Double> entry : this.map.entrySet()){
            min = Math.min(min, entry.getValue());
        }
        return min;
    }

    public double findMaxCount(){
        double max = Double.NEGATIVE_INFINITY;
        for (Map.Entry<T,Double> entry : this.map.entrySet()){
            max = Math.max(max, entry.getValue());
        }
        return max;
    }

    public T findMin(){
        T retVal = null;
        double min = Double.POSITIVE_INFINITY;
        for (Map.Entry<T,Double> entry : this.map.entrySet()){
            min = Math.min(min, entry.getValue());
            retVal = entry.getKey();
        }
        return retVal;
    }

    public T findMax(){
        T retVal = null;
        double max = Double.NEGATIVE_INFINITY;
        for (Map.Entry<T,Double> entry : this.map.entrySet()){
            max = Math.max(max, entry.getValue());
            retVal = entry.getKey();
        }
        return retVal;
    }
}
