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
        this(values, false);
    }
    
    private IntegerSet(int[] values, boolean sort){
        this.values = new int[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
        if (sort){
            Arrays.sort(this.values);
        }
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
        Arrays.sort(values);
        if (values.length == 0){
            return emptySet;
        } else {
            return createIntegerSetFromSortedArray(values);
        }
    }
    
    /**
     * Creates a new instance of class IntegerSet from the given sorted array of numbers.
     * 
     * @param values the array of numbers
     * @return new instance of class IntegerSet from the given sorted array of numbers
     */
    public static IntegerSet createIntegerSetFromSortedArray(int[] values){
        int duplicates = 0;
        for (int i = 0; i < values.length-1; i++){
            if (values[i] == values[i+1]){
                duplicates++;
            }
        }
        if (duplicates > 0){
            int[] newValues = new int[values.length-duplicates];
            int j = 0;
            for (int i = 0; i < values.length; i++){
                if (i == values.length-1 || values[i] != values[i+1]){
                    newValues[j] = values[i];
                    j++;
                }
            }
            values = newValues;
        }
        if (values.length == 0){
            return emptySet;
        } else {
            //return new IntegerSet(values, false);
            IntegerSet retVal = new IntegerSet();
            retVal.values = values;
            return retVal;
        }
    }
    
    /**
     * Creates a new instance of class IntegerSet from the given set of numbers.
     * 
     * @param set the set of numbers
     * @return new instance of class IntegerSet from the given set of numbers
     */
    public static IntegerSet createIntegerSet(Set<Integer> set){
        int values[] = new int[set.size()];
        int index = 0;
        for (int integer : set){
            values[index] = integer;
            index++;
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
        int[] v = new int[end-start];
        for (int i = start, index = 0; i < end; i++){
            v[index] = i;
            index++;
        }
        return createIntegerSet(v);
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
        if (a.isEmpty() || b.isEmpty() || a.values[0] > b.values[b.values.length-1] || b.values[0] > a.values[a.values.length-1]){
            return emptySet;
        }
        int count = 0;
        int indexA = 0;
        int indexB = 0;
        int aLength = a.values.length;
        int bLength = b.values.length;
        while (indexA < aLength && indexB < bLength){
            if (a.values[indexA] == b.values[indexB]){
                count++;
                indexA++;
                indexB++;
            } else if (a.values[indexA] < b.values[indexB]){
                indexA++;
            } else if (a.values[indexA] > b.values[indexB]){
                indexB++;
            }
        }
        if (count == aLength){
            return a;
        }
        if (count == bLength){
            return b;
        }
        int[] newValues = new int[count];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength && indexB < bLength){
            if (a.values[indexA] == b.values[indexB]){
                newValues[index] = a.values[indexA];
                indexA++;
                indexB++;
                index++;
            } else if (a.values[indexA] < b.values[indexB]){
                indexA++;
            } else if (a.values[indexA] > b.values[indexB]){
                indexB++;
            }
        }
        if (count == 0){
            return emptySet;
        } else {
            IntegerSet set = new IntegerSet();
            set.values = newValues;
            return set;
        }
    }

    /**
     * Computes union of the given sets.
     * @param a the first set
     * @param b the second set
     * @return the union of the given sets
     */
    public static IntegerSet union(IntegerSet a, IntegerSet b){
        if (a instanceof EmptySet){
            return b;
        } else if (b instanceof EmptySet){
            return a;
        }
        int aLength = a.values.length;
        int bLength = b.values.length;
        int[] aValues = a.values;
        int[] bValues = b.values;
        if (a == b){
            return a;
        } else if (aValues[aLength-1] < bValues[0]){
            int[] values = VectorUtils.concat(aValues, bValues);
            return new IntegerSet(values);
        } else if (aValues[0] > bValues[bLength-1]){
            int[] values = VectorUtils.concat(bValues, aValues);
            return new IntegerSet(values);
        }
        int count = 0;
        int indexA = 0;
        int indexB = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                int aValue = aValues[indexA];
                int bValue = bValues[indexB];
                if (aValue == bValue){
                    indexA++;
                    indexB++;
                    count++;
                } else if (aValue < bValue){
                    indexA++;
                    count++;
                } else if (aValue > bValue){
                    indexB++;
                    count++;
                }
            } else if (indexA < aLength){
                indexA++;
                count++;
            } else if (indexB < bLength){
                indexB++;
                count++;
            }
        }
        if (count == aLength){
            return a;
        }
        if (count == bLength){
            return b;
        }
        int[] newValues = new int[count];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                int aValue = aValues[indexA];
                int bValue = bValues[indexB];
                if (aValue == bValue){
                    newValues[index] = aValue;
                    indexA++;
                    indexB++;
                    index++;
                } else if (aValue < bValue){
                    newValues[index] = aValue;
                    indexA++;
                    index++;
                } else if (aValue > bValue){
                    newValues[index] = bValue;
                    indexB++;
                    index++;
                }
            } else if (indexA < aLength){
                newValues[index] = aValues[indexA];
                indexA++;
                index++;
            } else if (indexB < bLength){
                newValues[index] = bValues[indexB];
                indexB++;
                index++;
            }
        }
        IntegerSet set = new IntegerSet();
        set.values = newValues;
        return set;
    }

    /**
     * Computes difference of the given sets (<em>a</em> - <em>b</em>).
     * @param a the first set
     * @param b the second set
     * @return the difference of the given sets
     */
    public static IntegerSet difference(IntegerSet a, IntegerSet b){
        if (a == b){
            return emptySet;
        } else if (a instanceof EmptySet){
            return emptySet;
        } else if (b instanceof EmptySet){
            return a;
        }
        int indexA = 0;
        int indexB = 0;
        int count = 0;
        while (indexA < a.values.length){
            while (indexB < b.values.length && b.values[indexB] < a.values[indexA]){
                indexB++;
            }
            if (indexB < b.values.length && b.values[indexB] > a.values[indexA]){
                count++;
            } else if (indexB >= b.values.length){
                count++;
            }
            indexA++;
        }
        int[] newValues = new int[count];
        indexA = 0;
        indexB = 0;
        count = 0;
        while (indexA < a.values.length){
            while (indexB < b.values.length && b.values[indexB] < a.values[indexA]){
                indexB++;
            }
            if (indexB < b.values.length && b.values[indexB] > a.values[indexA]){
                newValues[count] = a.values[indexA];
                count++;
            } else if (indexB >= b.values.length){
                newValues[count] = a.values[indexA];
                count++;
            }
            indexA++;
        }
        if (newValues.length == 0){
            return emptySet;
        }
        IntegerSet set = new IntegerSet();
        set.values = newValues;
        return set;
    }
    
    /**
     * Checks if all sets stored in the first list are subsets of the respective sets
     * in the second list (i-th set from the first list is checked only w.r.t. the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if all sets stored in the first list are subsets of the respective sets
     * in the second list, fase otherwise
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
     * in the second array, fase otherwise
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
        int i = 0;
        for (IntegerSet is : a){
            if (mask[i] && !is.isEmpty()){
                return false;
            }
            i++;
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
        int i = 0;
        for (IntegerSet is : a){
            if (mask[i] && is.isEmpty()){
                return true;
            }
            i++;
        }
        return false;
    }

    /**
     * Checks if at least some sets stored in the first list are subsets of the respective sets
     * in the second list (i-th set from the first list is checked only w.r.t. the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if at least some sets stored in the first list are subsets of the respective sets
     * in the second list, fase otherwise
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
     * in the second array, fase otherwise
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
     * in the second array, fase otherwise
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
     * in the second array, fase otherwise
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
                }
            }
        }
        return result;
    }
    
    /**
     * Computes intersection of the sets in the given array.
     * @param sets the array of sets
     * @return the intersection of the sets in the given array
     */
    public static IntegerSet intersection(IntegerSet ...sets){
        Arrays.sort(sets, new Comparator<IntegerSet>() {
            @Override
            public int compare(IntegerSet o1, IntegerSet o2) {
                return o1.size()-o2.size();
            }
        });
        IntegerSet result = null;
        for (IntegerSet set : sets){
            if (set != null){
                if (result == null){
                    result = set;
                } else {
                    result = intersection(result, set);
                }
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
        List<IntegerSet> retVal = new ArrayList<IntegerSet>(a.size());
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
        if (sets.size() > 2){
            int sumOfSizes = 0;
            for (IntegerSet is : sets){
                sumOfSizes += is.size();
            }
            int[] aux = new int[sumOfSizes];
            int index = 0;
            for (IntegerSet is : sets){
                System.arraycopy(is.values, 0, aux, index, is.values.length);
                index += is.size();
            }
            return IntegerSet.createIntegerSet(aux);
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
        return result;
    }
    
    /**
     * Computes union of the sets in the given array.
     * @param sets the array of sets
     * @return the union of the sets in the given array
     */
    public static IntegerSet union(IntegerSet ...sets){
        if (sets.length > 2){
            int sumOfSizes = 0;
            for (IntegerSet is : sets){
                sumOfSizes += is.size();
            }
            int[] aux = new int[sumOfSizes];
            int index = 0;
            for (IntegerSet is : sets){
                System.arraycopy(is.values, 0, aux, index, is.values.length);
                index += is.size();
            }
            return IntegerSet.createIntegerSet(aux);
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
        List<IntegerSet> retVal = new ArrayList<IntegerSet>(a.size());
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
        int i = 0;
        for (IntegerSet is : sets){
            if (mask[i] && !is.isEmpty()){
                count++;
            }
            i++;
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
        int i = 0;
        for (IntegerSet is : sets){
            if (mask[i] && is.isEmpty()){
                count++;
            }
            i++;
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
        int i = 0;
        for (IntegerSet is : sets){
            if (mask[i]){
                sum += is.size();
            }
            i++;
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
        }
        return Arrays.binarySearch(this.values, integer) > -1;
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
        for (int i = 0; i < this.values.length; i++){
            while (i2 < b.values.length && b.values[i2] < this.values[i]){
                i2++;
            }
            if (i2 != b.values.length && this.values[i] == b.values[i2]){
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
        if (b.isEmpty() || b.size() < this.size() || this.values[0] < b.values[0] || this.values[this.values.length-1] > b.values[b.values.length-1]){
            return false;
        }
        int i2 = 0;
        for (int i = 0; i < this.values.length; i++){
            while (i2 < b.values.length && b.values[i2] < this.values[i]){
                i2++;
            }
            if (i2 == b.values.length || b.values[i2] > this.values[i]){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if this set is strict subset of the given set <em>b</em>. "Strict subset"
     * means that it is a subset but it is not equal.
     * @param b the set for which we want to check if it is a strict super-set of this set.
     * @return true if this set is strict subset of the given set <em>b</em>t
     */
    public boolean isStrictSubsetOf(IntegerSet b){
        return !this.equals(b) && this.isSubsetOf(b);
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof IntegerSet){
            IntegerSet cis = (IntegerSet)o;
            if (this.isEmpty() != cis.isEmpty()){
                return false;
            }
            if ((cis.hashCode != -1 && this.hashCode != -1 && cis.hashCode != this.hashCode) || cis.values.length != this.values.length)
                return false;
            if (cis.values[cis.values.length-1] != this.values[this.values.length-1]){
                return false;
            }
            for (int i = 0; i < this.values.length-1; i++){
                if (this.values[i] != cis.values[i])
                    return false;
            }
            return true;
        }
        return false;
    }
    
    private void computeHashCode(){
        int hash = 1;
        for (int i = 0; i < this.values.length; i++){
            //hash += (i+1)*this.values[i];
            hash = ((hash+1)*(1+this.values[i]*i*i)) % (Integer.MAX_VALUE/128);
        }
        this.hashCode = hash;
    }
    
    @Override
    public int hashCode(){
        if (hashCode == -1)
            computeHashCode();
        return hashCode;
    }

    /**
     * 
     * @return set with the elements of the IntegerSet
     */
    public Set<Integer> toSet(){
        LinkedHashSet<Integer> retVal = new LinkedHashSet<Integer>();
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
        List<Integer> retVal = new ArrayList<Integer>();
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
        return "IntegerSet"+ VectorUtils.intArrayToString(values);
    }
    
    /**
     * @return the elements of the DoubleSet
     */
    public int[] values(){
        return this.values;
    }
    
    /**
     * 
     * @return number of elements in the DoubleSet
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
            return new HashSet<Integer>(1);
        }
    }
}