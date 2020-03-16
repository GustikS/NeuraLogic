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


import cz.cvut.fel.ida.utils.math.VectorUtils;

import java.util.*;
/**
 * Class for representing sets of numbers of type double.
 * @author Ondra
 */
public class DoubleSet {

    private int hashCode = -1;

    private double[] values;

    /**
     * The unique empty set
     */
    public final static EmptySet emptySet = new EmptySet();

    private DoubleSet(){}

    private DoubleSet(double[] values){
        this(values, false);
    }

    private DoubleSet(double[] values, boolean sort){
        this.values = new double[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
        if (sort){
            Arrays.sort(this.values);
        }
    }

    /**
     * Creates a new instance of class DoubleSet from the given array of numbers.
     * 
     * @param values the array of numbers
     * @return new instance of class DoubleSet from the given array of numbers
     */
    public static DoubleSet createDoubleSet(double[] values){
        Arrays.sort(values);
        if (values.length == 0){
            return emptySet;
        } else {
            return createDoubleSetFromSortedArray(values);
        }
    }

    /**
     * Creates a new instance of class DoubleSet from the given sorted array of numbers.
     * 
     * @param values the array of numbers
     * @return new instance of class DoubleSet from the given sorted array of numbers
     */
    public static DoubleSet createDoubleSetFromSortedArray(double[] values){
        int duplicates = 0;
        for (int i = 0; i < values.length-1; i++){
            if (values[i] == values[i+1]){
                duplicates++;
            }
        }
        if (duplicates > 0){
            double[] newValues = new double[values.length-duplicates];
            int j = 0;
            for (int i = 0; i < newValues.length; i++){
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
            return new DoubleSet(values, false);
        }
    }

    /**
     * Creates a new instance of class DoubleSet from the given set of numbers.
     * 
     * @param set the set of numbers
     * @return new instance of class DoubleSet from the given set of numbers
     */
    public static DoubleSet createDoubleSet(Set<Double> set){
        double values[] = new double[set.size()];
        int index = 0;
        for (double integer : set){
            values[index] = integer;
            index++;
        }
        return createDoubleSet(values);
    }

    /**
     * Computes intersection of the given sets.
     * @param a the first set
     * @param b the second set
     * @return the intersection of the given sets
     */
    public static DoubleSet intersection(DoubleSet a, DoubleSet b){
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
        double[] newValues = new double[count];
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
            DoubleSet set = new DoubleSet();
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
    public static DoubleSet union(DoubleSet a, DoubleSet b){
        if (a instanceof EmptySet){
            return b;
        } else if (b instanceof EmptySet){
            return a;
        }
        int aLength = a.values.length;
        int bLength = b.values.length;
        double[] aValues = a.values;
        double[] bValues = b.values;
        if (a == b){
            return a;
        } else if (aValues[aLength-1] < bValues[0]){
            double[] values = VectorUtils.concat(aValues, bValues);
            return new DoubleSet(values);
        } else if (aValues[0] > bValues[bLength-1]){
            double[] values = VectorUtils.concat(bValues, aValues);
            return new DoubleSet(values);
        }
        int count = 0;
        int indexA = 0;
        int indexB = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                double aValue = aValues[indexA];
                double bValue = bValues[indexB];
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
        double[] newValues = new double[count];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                double aValue = aValues[indexA];
                double bValue = bValues[indexB];
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
        DoubleSet set = new DoubleSet();
        set.values = newValues;
        return set;
    }

    /**
     * Computes difference of the given sets (<em>a</em> - <em>b</em>).
     * @param a the first set
     * @param b the second set
     * @return the difference of the given sets
     */
    public static DoubleSet difference(DoubleSet a, DoubleSet b){
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
        double[] newValues = new double[count];
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
        DoubleSet set = new DoubleSet();
        set.values = newValues;
        return set;
    }

    /**
     * Checks if all sets stored iterable the first list are subsets of the respective sets
     * iterable the second list (i-th set from the first list is checked only w.r.t. the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if all sets stored iterable the first list are subsets of the respective sets
     * iterable the second list, fase otherwise
     */
    public static boolean allAreSubsets(List<DoubleSet> a, List<DoubleSet> b){
        for (int i = 0; i < a.size(); i++){
            if (!a.get(i).isSubsetOf(b.get(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets stored iterable the first array are subsets of the respective sets
     * iterable the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if all sets stored iterable the array list are subsets of the respective sets
     * iterable the second array, fase otherwise
     */
    public static boolean allAreSubsets(DoubleSet[] a, DoubleSet[] b){
        for (int i = 0; i < a.length; i++){
            if (!a[i].isSubsetOf(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets iterable the given list are empty.
     * @param a the list of sets
     * @return true if all sets iterable the given list are empty, false otherwise
     */
    public static boolean allAreEmpty(List<DoubleSet> a){
        for (DoubleSet is : a){
            if (!is.isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets iterable the given array are empty.
     * @param a the array of sets
     * @return true if all sets iterable the given array are empty, false otherwise
     */
    public static boolean allAreEmpty(DoubleSet[] a){
        for (DoubleSet is : a){
            if (!is.isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least one set iterable the given list is empty.
     * @param a the list of sets
     * @return true if at least one set iterable the given list is empty, false otherwise
     */
    public static boolean someAreEmpty(List<DoubleSet> a){
        for (DoubleSet is : a){
            if (is.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if at least one set iterable the given array is empty.
     * @param a the array of sets
     * @return true if at least one set iterable the given array is empty, false otherwise
     */
    public static boolean someAreEmpty(DoubleSet[] a){
        for (DoubleSet is : a){
            if (is.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets iterable the given array w.r.t. the given mask are empty.
     * @param a the array of sets
     * @param mask the mask denoting for which sets emptiness should be checked
     * @return true if all sets iterable the given array w.r.t. the mask are empty, false otherwise
     */
    public static boolean allAreEmpty(DoubleSet[] a, boolean[] mask){
        int i = 0;
        for (DoubleSet is : a){
            if (mask[i] && !is.isEmpty()){
                return false;
            }
            i++;
        }
        return true;
    }

    /**
     * Checks if at least one set iterable the given array w.r.t. the given mask is empty.
     * @param a the array of sets
     * @param mask the mask denoting for which sets emptiness should be checked
     * @return true if at least one set iterable the given array w.r.t. the mask is empty, false otherwise
     */
    public static boolean someAreEmpty(DoubleSet[] a, boolean[] mask){
        int i = 0;
        for (DoubleSet is : a){
            if (mask[i] && is.isEmpty()){
                return true;
            }
            i++;
        }
        return false;
    }

    /**
     * Checks if at least some sets stored iterable the first list are subsets of the respective sets
     * iterable the second list (i-th set from the first list is checked only w.r.t. the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if at least some sets stored iterable the first list are subsets of the respective sets
     * iterable the second list, fase otherwise
     */
    public static boolean someAreSubsets(List<DoubleSet> a, List<DoubleSet> b){
        for (int i = 0; i < a.size(); i++){
            if (a.get(i).isSubsetOf(b.get(i))){
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if at least some sets stored iterable the first array are subsets of the respective sets
     * iterable the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if at least some sets stored iterable the first array are subsets of the respective sets
     * iterable the second array, fase otherwise
     */
    public static boolean someAreSubsets(DoubleSet[] a, DoubleSet[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i].isSubsetOf(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets stored iterable the first array w.r.t. the given mask are subsets of the respective sets
     * iterable the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for "being subset"
     * @return true if all sets stored iterable the first array w.r.t. the given mask are subsets of the respective sets
     * iterable the second array, fase otherwise
     */
    public static boolean allAreSubsets(DoubleSet[] a, DoubleSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && !a[i].isSubsetOf(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least some sets stored iterable the first array w.r.t. the given mask are subsets of the respective sets
     * iterable the second list (i-th set from the first array is checked only w.r.t. the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for "being subset"
     * @return true if at least some sets stored iterable the first array w.r.t. the given mask are subsets of the respective sets
     * iterable the second array, fase otherwise
     */
    public static boolean someAreSubsets(DoubleSet[] a, DoubleSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && a[i].isSubsetOf(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets stored iterable the first list are equal to the respective sets from
     * the second list (always, the i-th set from the first list is compared to only the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if all sets stored iterable the first list are equal to the respective sets from
     * the second list, false otherwise
     */
    public static boolean allAreEqual(List<DoubleSet> a, List<DoubleSet> b){
        for (int i = 0; i < a.size(); i++){
            if (!a.get(i).equals(b.get(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all sets stored iterable the first array are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if all sets stored iterable the first array are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean allAreEqual(DoubleSet[] a, DoubleSet[] b){
        for (int i = 0; i < a.length; i++){
            if (!a[i].equals(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least some sets stored iterable the first list are equal to the respective sets from
     * the second list (always, the i-th set from the first list is compared to only the i-th set from the second list).
     * @param a the first list of sets
     * @param b the second list of sets
     * @return true if at least some sets stored iterable the first list are equal to the respective sets from
     * the second list, false otherwise
     */
    public static boolean someAreEqual(List<DoubleSet> a, List<DoubleSet> b){
        for (int i = 0; i < a.size(); i++){
            if (a.get(i).equals(b.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if at least some sets stored iterable the first array are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @return true if at least some sets stored iterable the first array are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean someAreEqual(DoubleSet[] a, DoubleSet[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i].equals(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all sets stored iterable the first array w.r.t. the given mask are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for equality
     * @return true if all sets stored iterable the first array w.r.t. the given mask are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean allAreEqual(DoubleSet[] a, DoubleSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && !a[i].equals(b[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if at least some sets stored iterable the first array w.r.t. the given mask are equal to the respective sets from
     * the second array (always, the i-th set from the first array is compared to only the i-th set from the second array).
     * @param a the first array of sets
     * @param b the second array of sets
     * @param mask the mask denoting which sets should be checked for equality
     * @return true if at least some sets stored iterable the first array w.r.t. the given mask are equal to the respective sets from
     * the second array, false otherwise
     */
    public static boolean someAreEqual(DoubleSet[] a, DoubleSet[] b, boolean[] mask){
        for (int i = 0; i < a.length; i++){
            if (mask[i] && a[i].equals(b[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Computes intersection of the sets iterable the given collection.
     * @param sets the collection of sets
     * @return the intersection of the sets iterable the given collection
     */
    public static DoubleSet intersection(Collection<DoubleSet> sets){
        DoubleSet result = null;
        for (DoubleSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = intersection(result, set);
            }
        }
        return result;
    }

    /**
     * Computes intersection of the sets iterable the given array.
     * @param sets the array of sets
     * @return the intersection of the sets iterable the given array
     */
    public static DoubleSet intersection(DoubleSet ...sets){
        DoubleSet result = null;
        for (DoubleSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = intersection(result, set);
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
    public static DoubleSet[] intersection(DoubleSet[] a, DoubleSet[] b){
        DoubleSet[] retVal = new DoubleSet[a.length];
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
    public static List<DoubleSet> intersection(List<DoubleSet> a, List<DoubleSet> b){
        List<DoubleSet> retVal = new ArrayList<DoubleSet>(a.size());
        Iterator<DoubleSet> iter1 = a.iterator();
        Iterator<DoubleSet> iter2 = b.iterator();
        while (iter1.hasNext() && iter2.hasNext()){
            retVal.add(intersection(iter1.next(), iter2.next()));
        }
        return retVal;
    }

    /**
     * Computes union of the sets iterable the given collection.
     * @param sets the collection of sets
     * @return the union of the sets iterable the given collection
     */
    public static DoubleSet union(Collection<DoubleSet> sets){
        DoubleSet result = null;
        for (DoubleSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = union(result, set);
            }
        }
        return result;
    }

    /**
     * Computes union of the sets iterable the given array.
     * @param sets the array of sets
     * @return the union of the sets iterable the given array
     */
    public static DoubleSet union(DoubleSet ...sets){
        DoubleSet result = null;
        for (DoubleSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = union(result, set);
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
    public static DoubleSet[] union(DoubleSet[] a, DoubleSet[] b){
        DoubleSet[] retVal = new DoubleSet[a.length];
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
    public static List<DoubleSet> union(List<DoubleSet> a, List<DoubleSet> b){
        List<DoubleSet> retVal = new ArrayList<DoubleSet>(a.size());
        Iterator<DoubleSet> iter1 = a.iterator();
        Iterator<DoubleSet> iter2 = b.iterator();
        while (iter1.hasNext() && iter2.hasNext()){
            retVal.add(union(iter1.next(), iter2.next()));
        }
        return retVal;
    }

    /**
     * Checks if this set contains the number <em>d</em>.
     * @param d the number for which we check if it is present iterable the set
     * @return true if this set contains the number <em>d</em>.
     */
    public boolean contains(double d){
        if (this.values.length < 16){
            for (int i = 0; i < this.values.length; i++){
                if (this.values[i] == d){
                    return true;
                } else if (this.values[i] > d){
                    return false;
                }
            }
        }
        return Arrays.binarySearch(this.values, d) > -1;
    }

    /**
     * Checks if this set is subset of the given set <em>b</em>.
     * @param b the set for which we want to check if it is a super-set of this set.
     * @return true if this set is subset of the given set <em>b</em>
     */
    public boolean isSubsetOf(DoubleSet b){
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
    public boolean isStrictSubsetOf(DoubleSet b){
        return !this.equals(b) && this.isSubsetOf(b);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof DoubleSet){
            DoubleSet cis = (DoubleSet)o;
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
            hash = ((hash+1)*(1+(int)(this.values[i]*i*i*i))) % (Integer.MAX_VALUE/128);
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
     * @return set with the elements of the DoubleSet
     */
    public Set<Double> toSet(){
        LinkedHashSet<Double> retVal = new LinkedHashSet<Double>();
        for (double i : this.values){
            retVal.add(i);
        }
        return retVal;
    }

    /**
     * 
     * @return true if the DoubleSet is empty, false otherwise
     */
    public boolean isEmpty(){
        return false;
    }

    @Override
    public String toString(){
        return "DoubleSet"+ VectorUtils.doubleArrayToString(values);
    }

    /**
     * @return the elements of the DoubleSet
     */
    public double[] values(){
        return this.values;
    }

    /**
     * 
     * @return number of elements iterable the DoubleSet
     */
    public int size(){
        return this.values.length;
    }


    private static class EmptySet extends DoubleSet {

        @Override
        public int size(){
            return 0;
        }

        @Override
        public boolean contains(double integer){
            return false;
        }

        @Override
        public boolean isSubsetOf(DoubleSet b){
            return true;
        }

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
        public double[] values(){
            return new double[0];
        }

        @Override
        public Set<Double> toSet(){
            return new HashSet<Double>(1);
        }
    }

//    public static void main(String args[]){
//        DoubleSet a = DoubleSet.createDoubleSet(new double[]{-3,-2,0,1,6,7,9});
//        DoubleSet b = DoubleSet.createDoubleSet(new double[]{-3,-1,0,1,5});
//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(difference(a,b));
//        System.out.println(difference(b,a));
//    }

}

