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

import java.math.BigInteger;
import java.util.*;
/**
 * Class for tracking counts of objects. It uses BigIntegers so it is possible
 * to have arbitrarily high numbers.
 * 
 * @param <T> type of the objects for which counts should be tracked
 * @author Ondra
 */
public class BigIntegerCounters<T> {

    private HashMap<T,BigInteger> map = new HashMap<T,BigInteger>();

    /**
     * Creates a new empty instance of class BigIntegerCounts
     */
    public BigIntegerCounters(){}

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     */
    public void add(T key, BigInteger value){
        addPre(key, value);
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     * @return the count associated to <em>key</em> after the modification
     */
    public BigInteger addPre(T key, BigInteger value){
        if (!map.containsKey(key)){
            map.put(key, value);
        } else {
            map.put(key, map.get(key).add(value));
        }
        return map.get(key);
    }

    /**
     * Adds <em>value</em> to the count associated to the element <em>key</em>
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     * @return the count associated to <em>key</em> before the modification
     */
    public BigInteger addPost(T key, BigInteger value){
        BigInteger oldValue = BigInteger.ZERO;
        if (!map.containsKey(key)){
            map.put(key, value);
        } else {
            oldValue = map.get(key);
            map.put(key, map.get(key).add(value));
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
    public BigInteger get(T key){
        if (!map.containsKey(key)){
            return BigInteger.ZERO;
        } else {
            return map.get(key);
        }
    }

    /**
     * Increments the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @param value the increment by which the value should be increased
     * @return the count associated to <em>key</em> after the modification
     */
    public BigInteger incrementPre(T key){
        if (!map.containsKey(key)){
            map.put(key, BigInteger.ONE);
        } else {
            map.put(key, map.get(key).add(BigInteger.ONE));
        }
        return map.get(key);
    }

    /**
     * Increments the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> before the modification
     */
    public BigInteger incrementPost(T key){
        if (!map.containsKey(key)){
            map.put(key, BigInteger.ONE);
        } else {
            map.put(key, map.get(key).add(BigInteger.ONE));
        }
        return map.get(key).subtract(BigInteger.ONE);
    }

    /**
     * Decrements the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> after the modification
     */
    public BigInteger decrementPre(T key){
        if (!map.containsKey(key)){
            map.put(key, BigInteger.valueOf(-1));
        } else {
            map.put(key, map.get(key).subtract(BigInteger.ONE));
        }
        return map.get(key);
    }

    /**
     * Decrements the count associated to the element <em>key</em> by 1.
     * @param key the element for which count should be modified
     * @return the count associated to <em>key</em> before the modification
     */
    public BigInteger decrementPost(T key){
        if (!map.containsKey(key)){
            map.put(key, BigInteger.valueOf(-1));
        } else {
            map.put(key, map.get(key).subtract(BigInteger.ONE));
        }
        return map.get(key).add(BigInteger.ONE);
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
    public Collection<BigInteger> counts(){
        return this.map.values();
    }

    /**
     * Adds all keys and values of another instance of class BigIntegerCounters<T>.
     * @param counters the instance of class BigIntegerCounters<T> whose content should be added
     */
    public void addAll(BigIntegerCounters<T> counters){
        for (Map.Entry<T,BigInteger> entry : counters.map.entrySet()){
            if (this.map.containsKey(entry.getKey())){
                this.map.put(entry.getKey(), this.map.get(entry.getKey()).add(entry.getValue()));
            } else {
                this.map.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String toString(){
        return this.map.toString();
    }

    /**
     * 
     * @return map iterable the following form: element -> count
     */
    public Map<T,BigInteger> toMap(){
        HashMap<T,BigInteger> copy = new HashMap<T,BigInteger>();
        copy.putAll(this.map);
        return copy;
    }
}
