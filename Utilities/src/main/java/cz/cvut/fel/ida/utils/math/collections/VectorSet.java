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

import java.util.Arrays;

public class VectorSet {
    private static final float LOAD_FACTOR = 0.5f;
    private int capacity;
    private int size;
    private int mask;
    private int[][] data;
    private int resizeThreshold;

    /**
     * Standard constructor
     */
    public VectorSet() {
        this(32);
    }

    public VectorSet(int initialCapacity) {
        int cap = 1;
        while (cap < initialCapacity) cap <<= 1;
        init(cap);
    }

    /**
     * Private dummy constructor used exclusively for super-fast cloning.
     * Prevents any array allocation during the cloning process.
     */
    private VectorSet(boolean dummy) {}

    private void init(int cap) {
        this.capacity = cap;
        this.mask = cap - 1;
        this.data = new int[cap][];
        this.resizeThreshold = (int) (cap * LOAD_FACTOR);
        this.size = 0;
    }

    public final void add(int[] vector) {
        if (size >= resizeThreshold) {
            rehash();
        }

        int index = mix(Arrays.hashCode(vector)) & mask;
        int[] curr;

        while ((curr = data[index]) != null) {
            // Primitive length check is the fastest way to skip deep equality
            if (curr.length == vector.length && Arrays.equals(curr, vector)) {
                return;
            }
            index = (index + 1) & mask;
        }

        data[index] = vector;
        size++;
    }

    public final boolean contains(int[] vector) {
        int index = mix(Arrays.hashCode(vector)) & mask;
        int[] curr;

        while ((curr = data[index]) != null) {
            if (curr.length == vector.length && Arrays.equals(curr, vector)) {
                return true;
            }
            index = (index + 1) & mask;
        }
        return false;
    }

    /**
     * SUPER FAST COPY
     * Uses dummy constructor to skip initialization and native clone for the array.
     */
    public VectorSet copy() {
        VectorSet copy = new VectorSet(true); // Allocate object only, no arrays
        copy.capacity = this.capacity;
        copy.mask = this.mask;
        copy.size = this.size;
        copy.resizeThreshold = this.resizeThreshold;
        copy.data = this.data.clone(); // Shallow clone of reference array (O(1) pointers)
        return copy;
    }

    private void rehash() {
        int[][] oldData = data;
        int oldCap = capacity;
        init(oldCap << 1); // Double the capacity

        for (int i = 0; i < oldCap; i++) {
            int[] vector = oldData[i];
            if (vector != null) {
                // Simplified insertion for rehash (we know elements are unique)
                int index = mix(Arrays.hashCode(vector)) & mask;
                while (data[index] != null) {
                    index = (index + 1) & mask;
                }
                data[index] = vector;
                size++;
            }
        }
    }

    // MurmurHash3-style mixer to prevent clustering in Linear Probing
    private static int mix(int h) {
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }

    public int size() { return size; }
}
