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

import cz.cvut.fel.ida.setup.Settings;

import java.util.*;

/**
 * A simple but fast class for storing sets of int[] arrays.
 *
 * @author ondra
 */
public class VectorSet {

    private static final float LOAD_FACTOR = 0.667f;

    private int capacity;
    private int size;
    private int[][] data;
    private List<int[]>[] collisions;
    private int resizeThreshold;

    /**
     * Creates a new instance of class VectorSet
     */
    public VectorSet() {
        this(32);
    }

    public VectorSet(int initialCapacity) {
        this.capacity = findNextPrime(initialCapacity);
        this.resizeThreshold = (int) (this.capacity * LOAD_FACTOR);
        this.data = new int[capacity][];
        this.collisions = new List[capacity];
        this.size = 0;
    }

    /**
     * Adds the given int[] array to the set.
     * @param vector the array
     */
    public final void add(int[] vector) {
        int hash = hash(vector);
        int[] existing = data[hash];

        if (existing == null) {
            data[hash] = vector;
            size++;
        } else if (Arrays.equals(existing, vector)) {
            return; // Already exists
        } else {
            List<int[]> collisionList = collisions[hash];
            if (collisionList == null) {
                collisionList = new ArrayList<>(2);
                collisions[hash] = collisionList;
            } else {
                // Check if vector already exists in collision list
                for (int[] array : collisionList) {
                    if (Arrays.equals(array, vector)) {
                        return;
                    }
                }
            }
            collisionList.add(vector);
            size++;
        }

        if (size >= resizeThreshold) {
            resize();
        }
    }

    private void resize() {
        int oldCapacity = capacity;
        int[][] oldData = data;
        List<int[]>[] oldCollisions = collisions;

        capacity = findNextPrime(oldCapacity * 2);
        resizeThreshold = (int) (capacity * LOAD_FACTOR);
        data = new int[capacity][];
        collisions = new List[capacity];
        size = 0;

        // Rehash primary entries
        for (int[] vector : oldData) {
            if (vector != null) {
                addToNewTable(vector);
            }
        }

        // Rehash collision entries
        for (List<int[]> list : oldCollisions) {
            if (list != null) {
                for (int[] vector : list) {
                    addToNewTable(vector);
                }
            }
        }
    }

    private void addToNewTable(int[] vector) {
        int hash = hash(vector);
        if (data[hash] == null) {
            data[hash] = vector;
            size++;
        } else {
            List<int[]> collisionList = collisions[hash];
            if (collisionList == null) {
                collisionList = new ArrayList<>(2);
                collisions[hash] = collisionList;
            }
            collisionList.add(vector);
            size++;
        }
    }

    /**
     * Checks if the VectorSet contains the given array of integers.
     * @param vector the array
     * @return true if the VectorSet contains the given array of integers
     */
    public final boolean contains(int[] vector) {
        int hash = hash(vector);
        int[] existing = data[hash];

        if (existing != null && Arrays.equals(existing, vector)) {
            return true;
        }

        List<int[]> collisionList = collisions[hash];
        if (collisionList != null) {
            for (int[] array : collisionList) {
                if (Arrays.equals(array, vector)) {
                    return true;
                }
            }
        }

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

    private int hash(int[] vector) {
        int hash = 0;
        for (int i = 0; i < vector.length; i++) {
            hash = 31 * hash + vector[i];
        }
        hash = hash ^ (hash >>> 16);
        return (hash & Integer.MAX_VALUE) % capacity;
    }

    private static int findNextPrime(int n) {
        if (n < 2) return 2;
        if (n == 2) return 2;
        if ((n & 1) == 0) n++;

        while (!isPrime(n)) {
            n += 2;
        }
        return n;
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if ((n & 1) == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}