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

import cz.cvut.fel.ida.utils.generic.tuples.Tuple;
import cz.cvut.fel.ida.utils.math.Combinatorics;
import cz.cvut.fel.ida.utils.math.VectorUtils;

import java.util.*;

/**
 * Class for representing sets of numbers of type int
 * .
 * @author Ondra
 */
public class IntegerSet {
    
    private int hashCode = -1;
    
    private int[] values;

    /**
     * The unique empty set
     */
    public final static EmptySet emptySet = new EmptySet();

    private IntegerSet(){}

    private IntegerSet(int[] values){
        this.values = values;
    }

    /**
     * 
     * @return minimum value in the set
     */
    public int min(){
        return this.values[0];
    }

    /**
     * 
     * @return maximum value in the set
     */
    public int max(){
        return this.values[values.length-1];
    }

    /**
     * Creates a new instance of class IntegerSet from the given array of numbers.
     * 
     * @param values the array of numbers
     * @return new instance of class IntegerSet from the given array of numbers
     */
    public static IntegerSet createIntegerSet(int ...values){
        if (values.length == 0){
            return emptySet;
        }
        Arrays.sort(values);
        return createIntegerSetFromSortedArray(values);
    }

    /**
     * Creates a new instance of class IntegerSet from the given sorted array of numbers.
     * 
     * @param values the array of numbers
     * @return new instance of class IntegerSet from the given sorted array of numbers
     */
    public static IntegerSet createIntegerSetFromSortedArray(int[] values){
        if (values.length == 0){
            return emptySet;
        }

        // Count duplicates in single pass
        int duplicates = 0;
        for (int i = 0; i < values.length - 1; i++){
            if (values[i] == values[i+1]){
                duplicates++;
            }
        }

        if (duplicates == 0){
            IntegerSet retVal = new IntegerSet();
            retVal.values = values;
            return retVal;
        }

        // Remove duplicates
        int[] newValues = new int[values.length - duplicates];
        int j = 0;
        for (int i = 0; i < values.length; i++){
            if (i == values.length - 1 || values[i] != values[i+1]){
                newValues[j++] = values[i];
            }
        }

        IntegerSet retVal = new IntegerSet();
        retVal.values = newValues;
        return retVal;
    }

    /**
     * Creates a new instance of class IntegerSet from the given set of numbers.
     * @param set the set of numbers
     * @return new instance of class IntegerSet from the given set of numbers
     */
    public static IntegerSet createIntegerSet(Set<Integer> set){
        if (set.isEmpty()){
            return emptySet;
        }
        int[] values = new int[set.size()];
        int index = 0;
        for (int integer : set){
            values[index++] = integer;
        }
        return createIntegerSet(values);
    }

    /**
     * Creates a new instance of class IntegerSet which contains
     * numbers start, start+1, ..., end-1.
     * @param start
     * @param end
     * @return new instance of class IntegerSet which contains
     * numbers start, start+1, ..., end-1.
     */
    public static IntegerSet createIntegerSetFromRange(int start, int end){
        if (start >= end){
            return emptySet;
        }
        int[] v = new int[end - start];
        for (int i = 0; i < v.length; i++){
            v[i] = start + i;
        }
        IntegerSet retVal = new IntegerSet();
        retVal.values = v;
        return retVal;
    }

    /**
     * Creates a new instance of class IntegerSet which contains
     * a random subset of size k drawn from the numbers start, start+1, ..., end-1.
     * @param start first element of the set from which the values should be drawn
     * @param end (exclusive) upper-bound on the set of numbers from which the values should be drawn
     * @param k size of the set
     * @return new instance of class IntegerSet which contains
     * a random subset of size k drawn from the numbers start, start+1, ..., end-1
     */
    public static IntegerSet createRandomIntegerSet(int start, int end, int k){
        return createRandomIntegerSet(start, end, k, new Random());
    }

    /**
     * Creates a new instance of class IntegerSet which contains
     * a random subset of size k drawn from the numbers start, start+1, ..., end-1.
     * @param start first element of the set from which the values should be drawn
     * @param end (exclusive) upper-bound on the set of numbers from which the values should be drawn
     * @param k size of the set
     * @param random random number generator to be used
     * @return new instance of class IntegerSet which contains
     * a random subset of size k drawn from the numbers start, start+1, ..., end-1
     */
    public static IntegerSet createRandomIntegerSet(int start, int end, int k, Random random){
        Tuple<Integer> randomTuple = Combinatorics.randomCombination(new NaturalNumbersList(start, end), k, random);
        int[] randomArray = new int[randomTuple.size()];
        for (int i = 0; i < randomArray.length; i++){
            randomArray[i] = randomTuple.get(i);
        }
        return createIntegerSet(randomArray);
    }

    /**
     * Computes intersection of the given sets.
     * @param a the first set
     * @param b the second set
     * @return the intersection of the given sets
     */
    public static IntegerSet intersection(IntegerSet a, IntegerSet b){
        if (a.isEmpty() || b.isEmpty()){
            return emptySet;
        }

        int[] aValues = a.values;
        int[] bValues = b.values;

        // Quick bounds check
        if (aValues[0] > bValues[bValues.length - 1] || bValues[0] > aValues[aValues.length - 1]){
            return emptySet;
        }

        // First pass: count intersecting elements
        int count = 0;
        int indexA = 0, indexB = 0;
        int aLength = aValues.length, bLength = bValues.length;

        while (indexA < aLength && indexB < bLength){
            int aVal = aValues[indexA];
            int bVal = bValues[indexB];
            if (aVal == bVal){
                count++;
                indexA++;
                indexB++;
            } else if (aVal < bVal){
                indexA++;
            } else {
                indexB++;
            }
        }

        if (count == 0){
            return emptySet;
        }
        if (count == aLength){
            return a;
        }
        if (count == bLength){
            return b;
        }

        // Second pass: build result
        int[] newValues = new int[count];
        indexA = 0;
        indexB = 0;
        int index = 0;

        while (indexA < aLength && indexB < bLength){
            int aVal = aValues[indexA];
            int bVal = bValues[indexB];
            if (aVal == bVal){
                newValues[index++] = aVal;
                indexA++;
                indexB++;
            } else if (aVal < bVal){
                indexA++;
            } else {
                indexB++;
            }
        }

        IntegerSet result = new IntegerSet();
        result.values = newValues;
        return result;
    }

    /**
     * Computes union of the given sets with optimized memory usage.
     * @param a the first set
     * @param b the second set
     * @return the union of the given sets
     */
    public static IntegerSet union(IntegerSet a, IntegerSet b){
        if (a instanceof EmptySet){
            return b;
        }
        if (b instanceof EmptySet){
            return a;
        }
        if (a == b){
            return a;
        }

        int[] aValues = a.values;
        int[] bValues = b.values;
        int aLength = aValues.length;
        int bLength = bValues.length;

        // Handle non-overlapping ranges
        if (aValues[aLength - 1] < bValues[0]){
            int[] values = new int[aLength + bLength];
            System.arraycopy(aValues, 0, values, 0, aLength);
            System.arraycopy(bValues, 0, values, aLength, bLength);
            IntegerSet result = new IntegerSet();
            result.values = values;
            return result;
        }
        if (bValues[bLength - 1] < aValues[0]){
            int[] values = new int[aLength + bLength];
            System.arraycopy(bValues, 0, values, 0, bLength);
            System.arraycopy(aValues, 0, values, bLength, aLength);
            IntegerSet result = new IntegerSet();
            result.values = values;
            return result;
        }

        // Count union size (first pass)
        int count = 0;
        int indexA = 0, indexB = 0;

        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                int aVal = aValues[indexA];
                int bVal = bValues[indexB];
                if (aVal == bVal){
                    indexA++;
                    indexB++;
                } else if (aVal < bVal){
                    indexA++;
                } else {
                    indexB++;
                }
            } else if (indexA < aLength){
                indexA++;
            } else {
                indexB++;
            }
            count++;
        }

        // Return original sets if union equals one of them
        if (count == aLength){
            return a;
        }
        if (count == bLength){
            return b;
        }

        // Build union (second pass)
        int[] newValues = new int[count];
        indexA = 0;
        indexB = 0;
        int index = 0;

        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                int aVal = aValues[indexA];
                int bVal = bValues[indexB];
                if (aVal == bVal){
                    newValues[index++] = aVal;
                    indexA++;
                    indexB++;
                } else if (aVal < bVal){
                    newValues[index++] = aVal;
                    indexA++;
                } else {
                    newValues[index++] = bVal;
                    indexB++;
                }
            } else if (indexA < aLength){
                newValues[index++] = aValues[indexA++];
            } else {
                newValues[index++] = bValues[indexB++];
            }
        }

        IntegerSet result = new IntegerSet();
        result.values = newValues;
        return result;
    }

    /**
     * Computes difference of the given sets (<em>a</em> - <em>b</em>).
     * @param a the first set
     * @param b the second set
     * @return the difference of the given sets
     */
    public static IntegerSet difference(IntegerSet a, IntegerSet b){
        if (a == b || a.isEmpty()){
            return emptySet;
        }
        if (b.isEmpty()){
            return a;
        }

        int[] aValues = a.values;
        int[] bValues = b.values;

        // Quick bounds check
        if (aValues[aValues.length - 1] < bValues[0] || aValues[0] > bValues[bValues.length - 1]){
            return a;
        }

        // Count elements in difference (first pass)
        int count = 0;
        int indexB = 0;
        int bLength = bValues.length;

        for (int indexA = 0; indexA < aValues.length; indexA++){
            int aVal = aValues[indexA];
            while (indexB < bLength && bValues[indexB] < aVal){
                indexB++;
            }
            if (indexB >= bLength || bValues[indexB] != aVal){
                count++;
            }
        }

        if (count == 0){
            return emptySet;
        }
        if (count == aValues.length){
            return a;
        }

        // Build result (second pass)
        int[] newValues = new int[count];
        indexB = 0;
        int index = 0;

        for (int indexA = 0; indexA < aValues.length; indexA++){
            int aVal = aValues[indexA];
            while (indexB < bLength && bValues[indexB] < aVal){
                indexB++;
            }
            if (indexB >= bLength || bValues[indexB] != aVal){
                newValues[index++] = aVal;
            }
        }

        IntegerSet result = new IntegerSet();
        result.values = newValues;
        return result;
    }

    /**
     * Checks if all sets stored in the first list are subsets of the respective sets
     * in the second list (i-th set from the first list is checked only w.r.t. the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if all sets stored in the first list are subsets of the respective sets
     * in the second list, false otherwise
     */
    public static boolean allAreSubsets(List<IntegerSet> a, List<IntegerSet> b){
        for (int i = 0; i < a.size(); i++){
            if (!a.get(i).isSubsetOf(b.get(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets stored in the first array are subsets of the respective sets
     * in the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if all sets stored in the array list are subsets of the respective sets
     * in the second array, false otherwise
     */
    public static boolean allAreSubsets(IntegerSet[] a, IntegerSet[] b){
        for (int i = 0; i < a.length; i++){
            if (!a[i].isSubsetOf(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets in the given list are empty.
     * @param a the list of sets
     * @return true if all sets in the given list are empty, false otherwise
     */
    public static boolean allAreEmpty(List<IntegerSet> a){
        for (IntegerSet is : a){
            if (!is.isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets in the given array are empty.
     * @param a the array of sets
     * @return true if all sets in the given array are empty, false otherwise
     */
    public static boolean allAreEmpty(IntegerSet[] a){
        for (IntegerSet is : a){
            if (!is.isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least one set in the given list is empty.
     * @param a the list of sets
     * @return true if at least one set in the given list is empty, false otherwise
     */
    public static boolean someAreEmpty(List<IntegerSet> a){
        for (IntegerSet is : a){
            if (is.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if at least one set in the given array is empty.
     * @param a the array of sets
     * @return true if at least one set in the given array is empty, false otherwise
     */
    public static boolean someAreEmpty(IntegerSet[] a){
        for (IntegerSet is : a){
            if (is.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets in the given array w.r.t. the given mask are empty.
     * @param a the array of sets
     * @param mask the mask denoting for which sets emptiness should be checked
     * @return true if all sets in the given array w.r.t. the mask are empty, false otherwise
     */
    public static boolean allAreEmpty(IntegerSet[] a, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && !a[i].isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least one set in the given array w.r.t. the given mask is empty.
     * @param a the array of sets
     * @param mask the mask denoting for which sets emptiness should be checked
     * @return true if at least one set in the given array w.r.t. the mask is empty, false otherwise
     */
    public static boolean someAreEmpty(IntegerSet[] a, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && a[i].isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if at least some sets stored in the first list are subsets of the respective sets
     * in the second list (i-th set from the first list is checked only w.r.t. the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if at least some sets stored in the first list are subsets of the respective sets
     * in the second list, false otherwise
     */
    public static boolean someAreSubsets(List<IntegerSet> a, List<IntegerSet> b){
        for (int i = 0; i < a.size(); i++){
            if (a.get(i).isSubsetOf(b.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if at least some sets stored in the first array are subsets of the respective sets
     * in the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if at least some sets stored in the first array are subsets of the respective sets
     * in the second array, false otherwise
     */
    public static boolean someAreSubsets(IntegerSet[] a, IntegerSet[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i].isSubsetOf(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets stored in the first array w.r.t. the given mask are subsets of the respective sets
     * in the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for "being subset"
     * @return true if all sets stored in the first array w.r.t. the given mask are subsets of the respective sets
     * in the second array, false otherwise
     */
    public static boolean allAreSubsets(IntegerSet[] a, IntegerSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && !a[i].isSubsetOf(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least some sets stored in the first array w.r.t. the given mask are subsets of the respective sets
     * in the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for "being subset"
     * @return true if at least some sets stored in the first array w.r.t. the given mask are subsets of the respective sets
     * in the second array, false otherwise
     */
    public static boolean someAreSubsets(IntegerSet[] a, IntegerSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && a[i].isSubsetOf(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets stored in the first list are equal to the respective sets from
     * the second list (always, the i-th set from the first list is compared to only the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if all sets stored in the first list are equal to the respective sets from
     * the second list, false otherwise
     */
    public static boolean allAreEqual(List<IntegerSet> a, List<IntegerSet> b){
        for (int i = 0; i < a.size(); i++){
            if (!a.get(i).equals(b.get(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets stored in the first array are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if all sets stored in the first array are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean allAreEqual(IntegerSet[] a, IntegerSet[] b){
        for (int i = 0; i < a.length; i++){
            if (!a[i].equals(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least some sets stored in the first list are equal to the respective sets from
     * the second list (always, the i-th set from the first list is compared to only the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if at least some sets stored in the first list are equal to the respective sets from
     * the second list, false otherwise
     */
    public static boolean someAreEqual(List<IntegerSet> a, List<IntegerSet> b){
        for (int i = 0; i < a.size(); i++){
            if (a.get(i).equals(b.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if at least some sets stored in the first array are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if at least some sets stored in the first array are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean someAreEqual(IntegerSet[] a, IntegerSet[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i].equals(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets stored in the first array w.r.t. the given mask are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for equality
     * @return true if all sets stored in the first array w.r.t. the given mask are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean allAreEqual(IntegerSet[] a, IntegerSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && !a[i].equals(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least some sets stored in the first array w.r.t. the given mask are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for equality
     * @return true if at least some sets stored in the first array w.r.t. the given mask are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean someAreEqual(IntegerSet[] a, IntegerSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && a[i].equals(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Computes intersection of the sets in the given collection.
     * @param sets the collection of sets
     * @return the intersection of the sets in the given collection
     */
    public static IntegerSet intersection(Collection<IntegerSet> sets){
        IntegerSet result = null;
        for (IntegerSet set : sets){
            if (set != null){
                if (result == null){
                    result = set;
                } else {
                    result = intersection(result, set);
                    if (result.isEmpty()){
                        return emptySet;
                    }
                }
            }
        }
        return result == null ? emptySet : result;
    }

    /**
     * Computes intersection of the sets in the given array.
     * Optimized by sorting sets by size (smallest first) to reduce computation.
     * @param sets the array of sets
     * @return the intersection of the sets in the given array
     */
    public static IntegerSet intersection(IntegerSet ...sets){
        if (sets.length == 0){
            return emptySet;
        }

        // Sort by size (smallest first) for performance
        Arrays.sort(sets, (o1, o2) -> Integer.compare(o1.size(), o2.size()));

        IntegerSet result = sets[0];
        for (int i = 1; i < sets.length; i++){
            result = intersection(result, sets[i]);
            if (result.isEmpty()){
                return emptySet;
            }
        }
        return result;
    }

    /**
     * Computes intersections of the respective pairs from the given two arrays
     * of sets.
     * @param a the first array of sets
     * @param b the second array of sets
     * @return the intersections of the respective pairs from the given two arrays
     * of sets
     */
    public static IntegerSet[] intersection(IntegerSet[] a, IntegerSet[] b){
        IntegerSet[] retVal = new IntegerSet[a.length];
        for (int i = 0; i < a.length; i++){
            retVal[i] = intersection(a[i], b[i]);
        }
        return retVal;
    }

    /**
     * Computes intersections of the respective pairs from the given two lists
     * of sets.
     * @param a the first list of sets
     * @param b the second list of sets
     * @return the intersections of the respective pairs from the given two lists
     * of sets
     */
    public static List<IntegerSet> intersection(List<IntegerSet> a, List<IntegerSet> b){
        List<IntegerSet> retVal = new ArrayList<>(a.size());
        Iterator<IntegerSet> iter1 = a.iterator();
        Iterator<IntegerSet> iter2 = b.iterator();
        while (iter1.hasNext() && iter2.hasNext()){
            retVal.add(intersection(iter1.next(), iter2.next()));
        }
        return retVal;
    }

    /**
     * Computes union of the sets in the given collection.
     * @param sets the collection of sets
     * @return the union of the sets in the given collection
     */
    public static IntegerSet union(Collection<IntegerSet> sets){
        if (sets.isEmpty()){
            return emptySet;
        }

        if (sets.size() > 2){
            // Fast path: concatenate all arrays and sort once
            int totalSize = 0;
            for (IntegerSet is : sets){
                totalSize += is.size();
            }

            if (totalSize == 0){
                return emptySet;
            }

            int[] combined = new int[totalSize];
            int offset = 0;
            for (IntegerSet is : sets){
                System.arraycopy(is.values, 0, combined, offset, is.size());
                offset += is.size();
            }
            return createIntegerSet(combined);
        }

        IntegerSet result = null;
        for (IntegerSet set : sets){
            if (set != null){
                if (result == null){
                    result = set;
                } else {
                    result = union(result, set);
                }
            }
        }
        return result == null ? emptySet : result;
    }

    /**
     * Computes union of the sets in the given array.
     * @param sets the array of sets
     * @return the union of the sets in the given array
     */
    public static IntegerSet union(IntegerSet ...sets){
        if (sets.length == 0){
            return emptySet;
        }

        if (sets.length > 2){
            // Fast path: concatenate all arrays and sort once
            int totalSize = 0;
            for (IntegerSet is : sets){
                totalSize += is.size();
            }

            if (totalSize == 0){
                return emptySet;
            }

            int[] combined = new int[totalSize];
            int offset = 0;
            for (IntegerSet is : sets){
                System.arraycopy(is.values, 0, combined, offset, is.size());
                offset += is.size();
            }
            return createIntegerSet(combined);
        }

        IntegerSet result = sets[0];
        for (int i = 1; i < sets.length; i++){
            result = union(result, sets[i]);
        }
        return result;
    }

    /**
     * Computes unions of the respective pairs from the given two arrays
     * of sets.
     * @param a the first array of sets
     * @param b the second array of sets
     * @return the unions of the respective pairs from the given two arrays
     * of sets
     */
    public static IntegerSet[] union(IntegerSet[] a, IntegerSet[] b){
        IntegerSet[] retVal = new IntegerSet[a.length];
        for (int i = 0; i < a.length; i++){
            retVal[i] = union(a[i], b[i]);
        }
        return retVal;
    }

    /**
     * Computes unions of the respective pairs from the given two lists
     * of sets.
     * @param a the first list of sets
     * @param b the second list of sets
     * @return the unions of the respective pairs from the given two lists
     * of sets
     */
    public static List<IntegerSet> union(List<IntegerSet> a, List<IntegerSet> b){
        List<IntegerSet> retVal = new ArrayList<>(a.size());
        Iterator<IntegerSet> iter1 = a.iterator();
        Iterator<IntegerSet> iter2 = b.iterator();
        while (iter1.hasNext() && iter2.hasNext()){
            retVal.add(union(iter1.next(), iter2.next()));
        }
        return retVal;
    }

    /**
     * Counts the number of non-empty sets in the given list.
     * @param sets the list of sets
     * @return the number of non-empty sets in the given list
     */
    public static int countNonEmpty(List<IntegerSet> sets){
        int count = 0;
        for (IntegerSet is : sets){
            if (!is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of empty sets in the given list.
     * @param sets the list of sets
     * @return the number of empty sets in the given list
     */
    public static int countEmpty(List<IntegerSet> sets){
        int count = 0;
        for (IntegerSet is : sets){
            if (is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of non-empty sets in the given array.
     * @param sets the list of sets
     * @return the number of non-empty sets in the given array
     */
    public static int countNonEmpty(IntegerSet[] sets){
        int count = 0;
        for (IntegerSet is : sets){
            if (!is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of empty sets in the given array.
     * @param sets the list of sets
     * @return the number of empty sets in the given array
     */
    public static int countEmpty(IntegerSet[] sets){
        int count = 0;
        for (IntegerSet is : sets){
            if (is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of non-empty sets in the given array w.r.t. the given mask.
     * @param sets the array of sets
     * @param mask the mask denoting which sets from the array should be considered
     * @return the number of non-empty sets in the given array.
     */
    public static int countNonEmpty(IntegerSet[] sets, boolean[] mask){
        int count = 0;
        for (int i = 0; i < sets.length; i++){
            if (mask[i] && !sets[i].isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of empty sets in the given array w.r.t. the given mask.
     * @param sets the array of sets
     * @param mask the mask denoting which sets from the array should be considered
     * @return the number of empty sets in the given array.
     */
    public static int countEmpty(IntegerSet[] sets, boolean[] mask){
        int count = 0;
        for (int i = 0; i < sets.length; i++){
            if (mask[i] && sets[i].isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Computes the sum of sizes of the sets in the given array of sets.
     * @param sets the array of sets
     * @return sum of sizes of the sets in the given array
     */
    public static int sumSizes(IntegerSet[] sets){
        int sum = 0;
        for (IntegerSet is : sets){
            sum += is.size();
        }
        return sum;
    }

    /**
     * Computes the sum of sizes of the sets in the given array of sets w.r.t. the given mask.
     * @param sets the array of sets
     * @param mask the mask which denotes which sets should be considered when computing the sum
     * @return sum of sizes of the sets in the given array
     */
    public static int sumSizes(IntegerSet[] sets, boolean[] mask){
        int sum = 0;
        for (int i = 0; i < sets.length; i++){
            if (mask[i]){
                sum += sets[i].size();
            }
        }
        return sum;
    }

    /**
     * Checks if this set contains the number <em>integer</em>.
     * @param integer the number for which we check if it is present in the set
     * @return true if this set contains the number <em>integer</em>.
     */
    public boolean contains(int integer){
        if (this.values.length < 16){
            for (int i = 0; i < this.values.length; i++){
                if (this.values[i] == integer){
                    return true;
                } else if (this.values[i] > integer){
                    return false;
                }
            }
            return false;
        }
        return Arrays.binarySearch(this.values, integer) >= 0;
    }

    /**
     * Checks if this IntegerSet contains at least one of the integers contained in the
     * given IntegerSet <em>b</em>.
     * @param b the integer set
     * @return true if this IntegerSet contains at least one of the integers contained in the
     * given IntegerSet, false otherwise
     */
    public boolean containsAny(IntegerSet b){
        if (b.isEmpty() || this.max() < b.min() || this.min() > b.max()){
            return false;
        }
        int i2 = 0;
        int bLength = b.values.length;
        for (int i = 0; i < this.values.length; i++){
            while (i2 < bLength && b.values[i2] < this.values[i]){
                i2++;
            }
            if (i2 != bLength && this.values[i] == b.values[i2]){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this set is subset of the given set <em>b</em>.
     * @param b the set for which we want to check if it is a super-set of this set.
     * @return true if this set is subset of the given set <em>b</em>
     */
    public boolean isSubsetOf(IntegerSet b){
        if (this.isEmpty()){
            return true;
        }
        if (b.isEmpty() || b.size() < this.size()){
            return false;
        }

        int[] aValues = this.values;
        int[] bValues = b.values;

        if (aValues[0] < bValues[0] || aValues[aValues.length - 1] > bValues[bValues.length - 1]){
            return false;
        }

        int i2 = 0;
        int bLength = bValues.length;
        for (int i = 0; i < aValues.length; i++){
            while (i2 < bLength && bValues[i2] < aValues[i]){
                i2++;
            }
            if (i2 == bLength || bValues[i2] > aValues[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this set is strict subset of the given set <em>b</em>. "Strict subset"
     * means that it is a subset but it is not equal.
     * @param b the set for which we want to check if it is a strict super-set of this set.
     * @return true if this set is strict subset of the given set <em>b</em>
     */
    public boolean isStrictSubsetOf(IntegerSet b){
        return !this.equals(b) && this.isSubsetOf(b);
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof IntegerSet)){
            return false;
        }

        IntegerSet other = (IntegerSet) o;
        if (this.isEmpty() && other.isEmpty()){
            return true;
        }
        if (this.isEmpty() || other.isEmpty()){
            return false;
        }
        if (this.values.length != other.values.length){
            return false;
        }

        // Quick check: compare last element and hash
        if (this.values[this.values.length - 1] != other.values[other.values.length - 1]){
            return false;
        }

        // Compare hash codes if computed
        if (this.hashCode != -1 && other.hashCode != -1 && this.hashCode != other.hashCode){
            return false;
        }

        // Full comparison
        for (int i = 0; i < this.values.length; i++){
            if (this.values[i] != other.values[i]){
                return false;
            }
        }
        return true;
    }

    private void computeHashCode(){
        int hash = 1;
        for (int i = 0; i < this.values.length; i++){
            hash = ((hash + 1) * (1 + this.values[i] * i * i)) % (Integer.MAX_VALUE / 128);
        }
        this.hashCode = hash;
    }

    @Override
    public int hashCode(){
        if (hashCode == -1){
            computeHashCode();
        }
        return hashCode;
    }

    /**
     * 
     * @return set with the elements of the IntegerSet
     */
    public Set<Integer> toSet(){
        LinkedHashSet<Integer> retVal = new LinkedHashSet<>(this.values.length);
        for (int i : this.values){
            retVal.add(i);
        }
        return retVal;
    }

    /**
     * 
     * @return list with the elements of the IntegerSet
     */
    public List<Integer> toList(){
        List<Integer> retVal = new ArrayList<>(this.values.length);
        for (int i : this.values){
            retVal.add(i);
        }
        return retVal;
    }

    /**
     * 
     * @return true if the IntegerSet is empty, false otherwise
     */
    public boolean isEmpty(){
        return false;
    }

    @Override
    public String toString(){
        return "IntegerSet" + VectorUtils.intArrayToString(values);
    }

    /**
     * @return the elements of the IntegerSet
     */
    public int[] values(){
        return this.values;
    }

    /**
     * @return number of elements in the IntegerSet
     */
    public int size(){
        return this.values.length;
    }

    private static class EmptySet extends IntegerSet {
        
        /**
         * 
         * @return
         */
        @Override
        public int size(){
            return 0;
        }
        
        /**
         * 
         * @param integer
         * @return
         */
        @Override
        public boolean contains(int integer){
            return false;
        }
        
        /**
         * 
         * @param b
         * @return
         */
        @Override
        public boolean isSubsetOf(IntegerSet b){
            return true;
        }
        
        /**
         * 
         * @return
         */
        @Override
        public boolean isEmpty(){
            return true;
        }

        @Override
        public int hashCode(){
            return 0;
        }

        @Override
        public boolean equals(Object o){
            return o instanceof EmptySet;
        }

        @Override
        public String toString(){
            return "EmptySet[]";
        }

        @Override
        public int[] values(){
            return new int[0];
        }

        @Override
        public Set<Integer> toSet(){
            return new HashSet<>(0);
        }
    }
}