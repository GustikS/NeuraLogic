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
import java.math.*;
/**
 * A simple but fast class for storing sets of int[] arrays.
 * 
 * @author ondra
 */
public class VectorSet {
    
    private static Random random = new Random();
    
    private int startTwoPow = 5;
    
    private int capacity = BigInteger.probablePrime(startTwoPow, random).intValue();
    
    private int size;
    
    private int[][] data;
    
    private List<int[]>[] collisions;
    
    /**
     * Creates a new instance of class VectorSet
     */
    public VectorSet(){
        this.data = new int[capacity][];
        this.collisions = new List[capacity];
    }
    
    /**
     * Adds the given int[] array to the set.
     * @param vector the array
     */
    public final void add(int[] vector){
        int hash = hash(vector);
        if (data[hash] == null){
            data[hash] = vector;
            this.size++;
        } else {
            if (collisions[hash] == null){
                collisions[hash] = new ArrayList<int[]>(1);
                //collisions[hash] = new LinkedList<int[]>();
            }
            for (int[] array : collisions[hash]){
                if (Arrays.equals(array, vector)){
                    return;
                }
            }
            collisions[hash].add(vector);
            this.size++;
        }
        if (this.size >= this.capacity/1.5+1){
            resize();
        }
    }
    
    private void resize(){
        this.startTwoPow++;
        this.capacity = BigInteger.probablePrime(this.startTwoPow, random).intValue();
        int[][] oldData = this.data;
        List<int[]>[] oldCollisions = this.collisions;
        this.data = new int[capacity][];
        this.collisions = new List[capacity];//new ArrayList[capacity];
        for (int i = 0; i < oldData.length; i++){
            if (oldData[i] != null){
                this.add(oldData[i]);
            }
        }
        for (List<int[]> list : oldCollisions){
            if (list != null){
                for (int[] array : list){
                    this.add(array);
                }
            }
        }
    }

    /**
     * Checks if the VectorSet contains the given array of integers.
     * @param vector the array
     * @return true if the VectorSet contains the given array of integers
     */
    public final boolean contains(int[] vector){
        int hash = hash(vector);
        int[] array0 = this.data[hash];
//        if (array0 != null){
//            if (array0.length == vector.length){
//                boolean equal = true;
//                for (int i = 0; i < vector.length; i++){
//                    if (array0[i] != vector[i]){
//                        equal = false;
//                        break;
//                    }
//                }
//                if (equal){
//                    return true;
//                }
//            }
            if (Arrays.equals(array0, vector)){
                return true;
            }
            if (this.collisions[hash] != null){
                outerLoop: for (int[] array : this.collisions[hash]){
//                    if (array.length == vector.length){
//                        for (int i = 0; i < array.length; i++){
//                            if (array[i] != vector[i]){
//                                continue outerLoop;
//                            }
//                        }
//                        return true;
//                    }
                    if (Arrays.equals(array, vector)){
                        return true;
                    }
                }
            }
//        }
        return false;
    }

    public void printStats(){
        double num = 0;
        double max = 0;
        for (List<int[]> c : collisions){
            if (c != null) {
                num += c.size();
                max = Math.max(max, c.size());
            }
        }
        System.out.println("size: "+this.size+", capacity: "+this.capacity+", num collisions: "+num+", max: "+max);
    }
    
    private int hash(int[] vector){
        int ret = 0;
        int cap = this.capacity;
        for (int i = 0; i < vector.length; i++){
            ret = ((ret+1) * (vector[i] + 1+i*i*i)) % cap;
        }
        if (ret < 0){
            ret = (cap-ret) % cap;
        }
        return ret;
//        return Arrays.hashCode(vector) % this.capacity;
    }
    
}
