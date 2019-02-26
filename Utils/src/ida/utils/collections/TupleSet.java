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

import ida.utils.MatrixUtils;
import ida.utils.VectorUtils;
import ida.utils.tuples.Pair;
import ida.utils.tuples.Quadruple;
import ida.utils.tuples.Triple;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class for representing sets of tuples (vectors) of numbers of fixed length
 * @author Ondra
 */
public class TupleSet {
    
    private int hashCode = -1;
    
    private int[][] values;

    /**
     * The mepty set
     */
    public final static EmptySet emptySet = new EmptySet();
    
    private TupleSet(){}
    
    private TupleSet(int[][] values){
        this(values, false);
    }
    
    private TupleSet(int[][] values, boolean sort){
        this.values = new int[values.length][];
        for (int i = 0; i < values.length; i++){
            this.values[i] = VectorUtils.copyArray(values[i]);
        }
        if (sort){
            Arrays.sort(values, new Comparator<int[]>(){
                @Override
                public int compare(int[] o1, int[] o2) {
                    for (int i = 0; i < o1.length; i++){
                        if (o1[i] != o2[i]){
                            return o1[i]-o2[i];
                        }
                    }
                    return 0;
                }
            });
        }
    }
    
    /**
     * Creates a new instance of class TupleSet from a two-dimensional rectangular array
     * iterable which every row represents one tuple.
     * @param values the two-dimensional array
     * @return new instance of class TupleSet
     */
    public static TupleSet createTupleSet(int[][] values){
        Arrays.sort(values);
        if (values.length == 0){
            return emptySet;
        } else {
            return createTupleSetFromSortedArray(values);
        }
    }
    
    /**
     * Creates a new instance of class TupleSet with tuples of length 2 from the given collection of pairs of numbers.
     * 
     * @param pairs the pairs of numbers (= tuples of length 2)
     * @return new instance of class TupleSet with tuples of length 2 from the given collection of pairs of numbers
     */
    public static TupleSet createTupleSetFromPairs(Collection<Pair<Integer,Integer>> pairs){
        int[][] values = new int[pairs.size()][2];
        int index = 0;
        for (Pair<Integer,Integer> pair : pairs){
            values[index][0] = pair.r;
            values[index][1] = pair.s;
            index++;
        }
        return createTupleSetFromSortedArray(values);
    }
    
    /**
     * Creates a new instance of class TupleSet with tuples of length 3 from the given collection of triples of numbers.
     * 
     * @param triples the triples of numbers (= tuples of length 3)
     * @return new instance of class TupleSet with tuples of length 3 from the given collection of triples of numbers
     */
    public static TupleSet createTupleSetFromTriples(Collection<Triple<Integer,Integer,Integer>> triples){
        int[][] values = new int[triples.size()][2];
        int index = 0;
        for (Triple<Integer,Integer,Integer> pair : triples){
            values[index][0] = pair.r;
            values[index][1] = pair.s;
            values[index][2] = pair.t;
            index++;
        }
        return createTupleSetFromSortedArray(values);
    }
    
    /**
     * Creates a new instance of class TupleSet with tuples of length 4 from the given collection of quadruples of numbers.
     * 
     * @param quadruples the quadruples of numbers (= tuples of length 4)
     * @return new instance of class TupleSet with tuples of length 4 from the given collection of quadruples of numbers
     */
    public static TupleSet createTupleSetFromQuadruples(Collection<Quadruple<Integer,Integer,Integer,Integer>> pairs){
        int[][] values = new int[pairs.size()][2];
        int index = 0;
        for (Quadruple<Integer,Integer,Integer,Integer> pair : pairs){
            values[index][0] = pair.r;
            values[index][1] = pair.s;
            values[index][2] = pair.t;
            values[index][3] = pair.u;
            index++;
        }
        return createTupleSetFromSortedArray(values);
    }
    
    /**
     * Creates a new instance of class TupleSet from a two-dimensional rectangular
     * lexicographically sorted array (the rows are lexicographically sorted)
     * iterable which every row represents one tuple.
     * @param values the two-dimensional array
     * @return new instance of class TupleSet
     */
    public static TupleSet createTupleSetFromSortedArray(int[][] values){
        int duplicates = 0;
        for (int i = 0; i < values.length-1; i++){
            if (Arrays.equals(values[i], values[i+1])){
                duplicates++;
            }
        }
        if (duplicates > 0){
            int[][] newValues = new int[values.length-duplicates][values[0].length];
            int j = 0;
            for (int i = 0; i < newValues.length; i++){
                if (i == values.length-1 || !Arrays.equals(values[i], values[i+1])){
                    newValues[j] = values[i];
                    j++;
                }
            }
            values = newValues;
        }
        if (values.length == 0){
            return emptySet;
        } else {
            return new TupleSet(values, false);
        }
    }
    
    /**
     * Creates a new instance of class TupleSet from a set of tuples represented
     * as one-dimensional arrays of type int[].
     * @param set the set of tuples (arrays of type int[])
     * @return new instance of class TupleSet
     */
    public static TupleSet createTupleSet(Set<int[]> set){
        int values[][] = new int[set.size()][];
        int index = 0;
        for (int[] tuple : set){
            values[index] = VectorUtils.copyArray(tuple);
            index++;
        }
        return createTupleSet(values);
    }
    
    private static boolean gt(int[] a, int[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i] != b[i]){
                return a[i]-b[i] > 0;
            }
        }
        return false;
    }
    
    private static boolean lt(int[] a, int[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i] != b[i]){
                return a[i]-b[i] < 0;
            }
        }
        return false;
    }
    
    private static boolean eq(int[] a, int[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i] != b[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Computes intersection of the given sets.
     * @param a the first set
     * @param b the second set
     * @return the intersection of the given sets
     */
    public static TupleSet intersection(TupleSet a, TupleSet b){
        if (a.isEmpty() || b.isEmpty() || gt(a.values[0], b.values[b.values.length-1]) || gt(b.values[0], a.values[a.values.length-1])){
            return emptySet;
        }
        int count = 0;
        int indexA = 0;
        int indexB = 0;
        int aLength = a.values.length;
        int bLength = b.values.length;
        while (indexA < aLength && indexB < bLength){
            if (eq(a.values[indexA], b.values[indexB])){
                count++;
                indexA++;
                indexB++;
            } else if (lt(a.values[indexA], b.values[indexB])){
                indexA++;
            } else if (gt(a.values[indexA], b.values[indexB])){
                indexB++;
            }
        }
        if (count == aLength){
            return a;
        }
        if (count == bLength){
            return b;
        }
        int[][] newValues = new int[count][];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength && indexB < bLength){
            if (eq(a.values[indexA], b.values[indexB])){
                newValues[index] = a.values[indexA];
                indexA++;
                indexB++;
                index++;
            } else if (lt(a.values[indexA], b.values[indexB])){
                indexA++;
            } else if (gt(a.values[indexA], b.values[indexB])){
                indexB++;
            }
        }
        if (count == 0){
            return emptySet;
        } else {
            TupleSet set = new TupleSet();
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
    public static TupleSet union(TupleSet a, TupleSet b){
        if (a instanceof EmptySet){
            return b;
        } else if (b instanceof EmptySet){
            return a;
        }
        int aLength = a.values.length;
        int bLength = b.values.length;
        int[][] aValues = a.values;
        int[][] bValues = b.values;
        if (a == b){
            return a;
        } else if (lt(aValues[aLength-1], bValues[0])){
            int[][] values = new int[aLength+bLength][];
            System.arraycopy(aValues, 0, values, 0, aLength);
            System.arraycopy(bValues, 0, values, aLength, bLength);
            return new TupleSet(values);
        } else if (gt(aValues[0], bValues[bLength-1])){
            int[][] values = new int[aLength+bLength][];
            System.arraycopy(bValues, 0, values, 0, bLength);
            System.arraycopy(aValues, 0, values, bLength, aLength);
            return new TupleSet(values);
        }
        int count = 0;
        int indexA = 0;
        int indexB = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                int[] aValue = aValues[indexA];
                int[] bValue = bValues[indexB];
                if (eq(aValue, bValue)){
                    indexA++;
                    indexB++;
                    count++;
                } else if (lt(aValue, bValue)){
                    indexA++;
                    count++;
                } else if (gt(aValue, bValue)){
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
        int[][] newValues = new int[count][];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                int[] aValue = aValues[indexA];
                int[] bValue = bValues[indexB];
                if (eq(aValue, bValue)){
                    newValues[index] = aValue;
                    indexA++;
                    indexB++;
                    index++;
                } else if (lt(aValue, bValue)){
                    newValues[index] = aValue;
                    indexA++;
                    index++;
                } else if (gt(aValue, bValue)){
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
        TupleSet set = new TupleSet();
        set.values = newValues;
        return set;
    }

    /**
     * Computes difference of the given sets (<em>a</em> - <em>b</em>).
     * @param a the first set
     * @param b the second set
     * @return the difference of the given sets
     */
    public static TupleSet difference(TupleSet a, TupleSet b){
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
            while (indexB < b.values.length && lt(b.values[indexB], a.values[indexA])){
                indexB++;
            }
            if (indexB < b.values.length && gt(b.values[indexB], a.values[indexA])){
                count++;
            } else if (indexB >= b.values.length){
                count++;
            }
            indexA++;
        }
        int[][] newValues = new int[count][];
        indexA = 0;
        indexB = 0;
        count = 0;
        while (indexA < a.values.length){
            while (indexB < b.values.length && lt(b.values[indexB], a.values[indexA])){
                indexB++;
            }
            if (indexB < b.values.length && gt(b.values[indexB], a.values[indexA])){
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
        TupleSet set = new TupleSet();
        set.values = newValues;
        return set;
    }

    /**
     * Selects a subset of the TupleSey according top values from the given column.
     * @param column the column whose values are used to select tuples
     * @param keys values iterable the column which correspond to tuples whichshould be selected
     * @return subset of the TupleSet containing all tuples which contain a value
     * iterable the given column which is contained iterable the given set <em>keys</em>
     */
    public TupleSet select(int column, IntegerSet keys){
        int count = 0;
        for (int i = 0; i < this.values.length; i++){
            if (keys.contains(this.values[i][column])){
                count++;
            }
        }
        int[][] selectedValues = new int[count][];
        count = 0;
        for (int i = 0; i < this.values.length; i++){
            if (keys.contains(this.values[i][column])){
                selectedValues[count] = this.values[i];
                count++;
            }
        }
        return TupleSet.createTupleSetFromSortedArray(selectedValues);
    }
    
    /**
     * Selects all values of the given column from the TupleSet and returns
     * them as a set of values.
     * @param column the index of the column
     * @return the values iterable the column iterable the form of an IntegerSet
     */
    public IntegerSet column(int column){
        int[] valuesWithDuplicates = new int[values.length];
        for (int i = 0; i < this.values.length; i++){
            valuesWithDuplicates[i] = this.values[i][column];
        }
        return IntegerSet.createIntegerSet(valuesWithDuplicates);
    }
    
    /**
     * Computes intersection of the sets iterable the given collection.
     * @param sets the collection of sets
     * @return the intersection of the sets iterable the given collection
     */
    public static TupleSet intersection(Collection<TupleSet> sets){
        TupleSet result = null;
        for (TupleSet set : sets){
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
    public static TupleSet intersection(TupleSet ...sets){
        TupleSet result = null;
        for (TupleSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = intersection(result, set);
            }
        }
        return result;
    }

    /**
     * Computes union of the sets iterable the given collection.
     * @param sets the collection of sets
     * @return the union of the sets iterable the given collection
     */
    public static TupleSet union(Collection<TupleSet> sets){
        TupleSet result = null;
        for (TupleSet set : sets){
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
    public static TupleSet union(TupleSet ...sets){
        TupleSet result = null;
        for (TupleSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = union(result, set);
            }
        }
        return result;
    }

    /**
     * Checks if this set contains the tuple <em>tuple</em>.
     * @param tuple the tuple for which we check if it is present iterable the set
     * @return true if this set contains the tuple <em>tuple</em>.
     */
    public boolean contains(int[] tuple){
        if (this.values.length < 16){
            for (int i = 0; i < this.values.length; i++){
                if (eq(this.values[i], tuple)){
                    return true;
                } else if (gt(this.values[i], tuple)){
                    return false;
                }
            }
        }
        return Arrays.binarySearch(this.values, tuple, new Comparator<int[]>(){
            @Override
            public int compare(int[] o1, int[] o2) {
                for (int i = 0; i < o1.length; i++){
                    if (o1[i] != o2[i]){
                        return o1[i]-o2[i];
                    }
                }
                return 0;
            }
        }) > -1;
    }
    
    /**
     * Checks if this set is subset of the given set <em>b</em>.
     * @param b the set for which we want to check if it is a super-set of this set.
     * @return true if this set is subset of the given set <em>b</em>
     */
    public boolean isSubsetOf(TupleSet b){
        if (b.isEmpty() || b.size() < this.size() || lt(this.values[0], b.values[0]) || gt(this.values[this.values.length-1], b.values[b.values.length-1])){
            return false;
        }
        int i2 = 0;
        for (int i = 0; i < this.values.length; i++){
            while (i2 < b.values.length && lt(b.values[i2], this.values[i])){
                i2++;
            }
            if (i2 == b.values.length || gt(b.values[i2], this.values[i])){
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
    public boolean isStrictSubsetOf(TupleSet b){
        return !this.equals(b) && this.isSubsetOf(b);
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof TupleSet){
            TupleSet cis = (TupleSet)o;
            if (this.isEmpty() != cis.isEmpty()){
                return false;
            }
            if ((cis.hashCode != -1 && this.hashCode != -1 && cis.hashCode != this.hashCode) || cis.values.length != this.values.length)
                return false;
            if (!eq(cis.values[cis.values.length-1], this.values[this.values.length-1])){
                return false;
            }
            for (int i = 0; i < this.values.length-1; i++){
                if (!eq(this.values[i], cis.values[i])){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private void computeHashCode(){
        int hash = 1;
        for (int i = 0; i < this.values.length; i++){
            hash = ((hash+1)*(1+Arrays.hashCode(this.values[i])*i*i)) % (Integer.MAX_VALUE/128);
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
     * @return set with the elements of the TupleSet
     */
    public Set<int[]> toSet(){
        LinkedHashSet<int[]> retVal = new LinkedHashSet<int[]>();
        for (int[] i : this.values){
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
        return "TupleSet:\n"+MatrixUtils.intMatrixToString(values);
    }
    
    /**
     * Returned array must not be modified
     * @return
     */
    public int[][] values(){
        return this.values;
    }
    
    /**
     * 
     * @return
     */
    public int size(){
        return this.values.length;
    }
    
    /*
     * The empty set 
     */
    private static class EmptySet extends TupleSet {
        
        @Override
        public int size(){
            return 0;
        }
        
        @Override
        public boolean contains(int[] tuple){
            return false;
        }
        
        @Override
        public boolean isSubsetOf(TupleSet b){
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
        public Set<int[]> toSet(){
            return new HashSet<int[]>(1);
        }
        
        @Override
        public int[][] values(){
            return new int[0][0];
        }

        @Override
        public IntegerSet column(int column) {
            return IntegerSet.emptySet;
        }

        @Override
        public TupleSet select(int column, IntegerSet keys) {
            return TupleSet.emptySet;
        }
    }
}
