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
 * Heap.java
 *
 * Created on 6. květen 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils.collections;

import java.util.*;
import ida.utils.Sugar;
import ida.utils.VectorUtils;

import ida.utils.tuples.*;
/**
 * Priority front implemnted as Heap.
 * 
 * @param <T> type of elements iterable the Heap
 * @author Ondra
 */
public class Heap<T> {
    
    private double keys[];
    
    private T values[];
    
    private Map<T,Integer> indexes = new HashMap<T,Integer>();
    
    private int last = 0;
    
    private double growFactor = 2;
    
    //statistics
    private int maxSize = 0;
    
    /** Creates a new instance of Heap */
    public Heap() {
        this(32);
    }
    
    /**
     * Creates a new instance of class Heap with the given initial capacity
     * @param initialCapacity the initial capacity
     */
    public Heap(int initialCapacity){
        this.keys = new double[initialCapacity];
        this.keys[0] = Integer.MIN_VALUE;
        this.values = (T[])new Object[initialCapacity];
    }

    /**
     * Removes the minimal element and returns it (if there is more than one such elements, it
     * selects one of them).
     * @return the minimal element
     */
    public T removeMin(){
        if (this.last > 1){
            T ret = this.values[1];
            swap(1, this.last);
            this.values[this.last] = null;
            this.last--;
            repairTopDown(1);
            this.indexes.remove(ret);
            return ret;
        }
        else if (this.last == 1){
            this.last = 0;
            this.indexes.remove(this.values[1]);
            return this.values[1];
        }
        else
            throw new java.lang.IllegalStateException();
    }
    
    /**
     * Returns the minimal element (if there is more than one such elements, it
     * selects one of them). It does not remove the returned element from the Heap.
     * @return the minimal element
     */
    public T lookAtMinValue(){
        return this.values[1];
    }
    
    public void updateKey(double newKey, T elementToBeUpdated){
        if (!this.indexes.containsKey(elementToBeUpdated)){
            this.add(newKey, elementToBeUpdated);
        } else {
            if (this.last > 1){
                int originalIndex = this.indexes.get(elementToBeUpdated);
                swap(originalIndex, this.last);
                this.last--;
                repairTopDown(originalIndex);
                add(newKey, elementToBeUpdated);
            } else {
                this.keys[1] = newKey;
            }
        }
    }
    
    public boolean containsElement(T element){
        return this.indexes.containsKey(element);
    }
    
    public void removeElement(T elementToBeRemoved){
        if (!this.indexes.containsKey(elementToBeRemoved)){
            throw new IllegalArgumentException("The removed element is not contained iterable the heap.");
        }
        if (this.last > 1){
            int originalIndex = this.indexes.get(elementToBeRemoved);
            swap(originalIndex, this.last);
            this.last--;
            repairTopDown(originalIndex);
            this.indexes.remove(elementToBeRemoved);
            this.values[this.last+1] = null;
        } else {
            this.last = 0;
            this.indexes.remove(this.values[1]);
            this.values[1] = null;
        }
    }
    
    /**
     * 
     * @return the "value" of the minimal element
     */
    public double lookAtMinKey(){
        return keys[1];
    }
    
    /**
     * 
     * @return true if the heap is nonEmpty
     */
    public boolean hasNext(){
        return last != 0;
    }
    
    /**
     * 
     * @return number of elements iterable the Heap
     */
    public int size(){
        return last;
    }
    
    /**
     * Adds new element to the heap. If there is already an element with the same key,
     * then this new element is still added - there may be more than one element with the same key 
     * iterable a Heap.
     * @param key the key (=priority)
     * @param value the value to be stored
     */
    public void add(double key, T value){
        this.last++;
        if (this.last == keys.length)
            realloc();
        this.values[last] = value;
        this.indexes.put(value, this.last);
        this.keys[this.last] = key;
        repairBottomUp(this.last);
        //
        this.maxSize = Math.max(this.maxSize, this.last-1);
    }
    
    private void repairBottomUp(int leaf){
        while (leaf > 1 && keys[leaf] < keys[leaf/2]){
            swap(leaf, leaf/2);
            leaf /= 2;
        }
    }
    
    private void repairTopDown(int root){
        while (true){
            if (2*root+1 <= last && keys[2*root+1] < keys[2*root] && keys[root] > keys[2*root+1]){
                swap(root, 2*root+1);
                root = 2*root+1;
            }
            else if (2*root <= last && keys[root] > keys[2*root]){
                swap(root, 2*root);
                root *= 2;
            }
            else
                break;
        }
    }
    
    private void swap(int a, int b){
        double temp = keys[a];
        keys[a] = keys[b];
        keys[b] = temp;
        T ttemp = values[a];
        values[a] = values[b];
        values[b] = ttemp;
        this.indexes.put(this.values[a], a);
        this.indexes.put(this.values[b], b);
    }
    
    private void realloc(){
        double[] newKeys = new double[(int)(keys.length*growFactor)];
        System.arraycopy(keys, 0, newKeys, 0, keys.length);
        this.keys = newKeys;
        T[] newValues = (T[])new Object[(int)(values.length*growFactor)];
        System.arraycopy(values, 0, newValues, 0, values.length);
        this.values = newValues;
    }
    
    /**
     * 
     * @return the list of pairs: [key, value] which are contained iterable the Heap
     */
    public List<Pair<T,Double>> values(){
        List<Pair<T,Double>> list = new ArrayList<Pair<T,Double>>();
        for (int i = 1; i <= this.last; i++){
            list.add(new Pair<T,Double>(this.values[i], this.keys[i]));
        }
        return list;
    }

    @Override
    public String toString(){
        return "Heap.values: "+Sugar.objectArrayToString(this.values)+", Heap.keys: "+ VectorUtils.doubleArrayToString(keys);
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(this.values)+Arrays.hashCode(this.keys);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Heap){
            Heap h = (Heap)o;
            return Arrays.equals(this.values, h.values) && Arrays.equals(this.keys, h.keys);
        }
        return false;
    }

    public static void main(String args[]){
        Heap<Integer> h = new Heap<Integer>();
        for (int i = 0; i < 20; i++){
            h.add((int)(2*Math.random()), i);
        }
        while (h.hasNext()){
            System.out.print(h.lookAtMinKey()+" -> "+h.removeMin()+", ");
        }
        System.out.println();
        for (int i = 0; i < 20; i++){
            h.add((2*Math.random()), i);
        }
        while (h.hasNext()){
            System.out.print(h.lookAtMinKey()+" -> "+h.removeMin()+", ");
        }
        System.out.println();
        
        h.add(2, 1);
        h.add(1, 2);
        h.add(3, 3);
        h.add(4, 4);
        h.add(6, 5);
        h.updateKey(1, 1);
        h.updateKey(2, 2);
        h.updateKey(5, 5);
        while (h.hasNext()){
            System.out.print(h.lookAtMinKey()+" -> "+h.removeMin()+", ");
        }
        System.out.println();
        
        h.add(2, 1);
        h.add(1, 2);
        h.add(3, 3);
        h.add(4, 4);
        h.add(6, 5);
        h.removeElement(1);
        h.removeElement(2);
        h.removeElement(5);
        while (h.hasNext()){
            System.out.print(h.lookAtMinKey()+" -> "+h.removeMin()+", ");
        }
        System.out.println();
    }
}
