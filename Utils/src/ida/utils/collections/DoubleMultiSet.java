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

import ida.utils.Sugar;
import ida.utils.VectorUtils;
import ida.utils.tuples.Pair;

import java.util.*;

/**
 * Class implementing multi-set of numbers of type double.
 * 
 * @author admin
 */
public class DoubleMultiSet {

    private int hashCode = -1;

    private double[] counts;

    private double[] values;

    /**
     * The unique empty multi-set.
     */
    public final static EmptySet emptyBag = new EmptySet();

    /**
     * 
     */
    protected DoubleMultiSet(){}

    private DoubleMultiSet(double[] values, double[] counts){
        int zeros = VectorUtils.occurrences(counts, 0);
        this.values = new double[values.length-zeros];
        this.counts = new double[values.length-zeros];
        int j = 0;
        for (int i = 0; i < values.length; i++){
            if (counts[i] > 0){
                this.values[j] = values[i];
                this.counts[j] = counts[i];
                j++;
            }
        }
    }

    private DoubleMultiSet(double[] values){
        this(values, false);
    }

    private DoubleMultiSet(double[] values, boolean sort){
        if (sort){
            Arrays.sort(values);
        }
        int duplicates = 0;
        for (int i = 0; i < values.length-1; i++){
            if (values[i] == values[i+1]){
                duplicates++;
            }
        }
        this.counts = new double[values.length-duplicates];
        this.values = new double[values.length-duplicates];
        int j = 0;
        for (int i = 0; i < values.length; i++){
            this.values[j] = values[i];
            this.counts[j]++;
            if (i == values.length-1 || values[i] != values[i+1]){
                j++;
            }
        }
    }

    /**
     * Creates a new instance of class DoubleMultiSet from the given array of numbers.
     * When there are multiplicities iterable the given array, they will be taken into account
     * (multi-sets keep track of the numbers of copies of each element iterable them).
     * 
     * @param values the array of numbers
     * @return new instance of class DoubleMultiSet from the given array of numbers
     */
    public static DoubleMultiSet createDoubleMultiSet(double[] values){
        Arrays.sort(values);
        if (values.length == 0){
            return emptyBag;
        } else {
            return new DoubleMultiSet(values);
        }
    }

    /**
     * Creates a new instance of class DoubleMultiSet from the given sorted array of numbers.
     * When there are multiplicities iterable the given array, they will be taken into account
     * (multi-sets keep track of the numbers of copies of each element iterable them).
     * 
     * @param values the array of numbers
     * @return new instance of class DoubleMultiSet from the given array of numbers
     */
    public static DoubleMultiSet createDoubleMultiSetFromSortedArray(double[] values){
        if (values.length == 0){
            return emptyBag;
        } else {
            return new DoubleMultiSet(values, false);
        }
    }

    /**
     * Creates a new instance of class DoubleMultiSet from the given Counters<Double> object.
     * Multiplicities of the numbers iterable the multi-set are taken from the given Counters<Double> object.
     * 
     * @param counter the counter which contaions the information about multiplicities of the 
     * elements iterable the multi-set
     * @return new instance of class DoubleMultiSet from the given Counters<Double> object
     */
    public static DoubleMultiSet createDoubleMultiSet(Counters<Double> counter){
        List<Pair<Double,Integer>> list = new ArrayList<Pair<Double,Integer>>();
        for (Double d : counter.keySet()){
            list.add(new Pair<Double,Integer>(d, counter.get(d)));
        }
        return createDoubleMultiSet(list);
    }

    /**
     * Creates a new instance of class DoubleMultiSet from the given collection of pairs: [number, multiplicity].
     * When there are multiplicities iterable the given array, they will be taken into account
     * (multi-sets keep track of the numbers of copies of each element iterable them).
     * 
     * @param pairs the collection of numbers
     * @return new instance of class DoubleMultiSet
     */
    public static DoubleMultiSet createDoubleMultiSet(Collection<Pair<Double,Integer>> pairs){
        double[] values = new double[pairs.size()];
        double[] counts = new double[pairs.size()];
        List<Pair<Double,Integer>> list = Sugar.listFromCollections(pairs);
        Collections.sort(list, new Comparator<Pair<Double,Integer>>(){
            public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2){
                return (int)Math.signum(o1.r-o2.r);
            }
        });
        for (int i = 0; i < list.size(); i++){
            values[i] = list.get(i).r;
            counts[i] = list.get(i).s;
        }
        return new DoubleMultiSet(values, counts);
    }

    /**
     * Creates a new instance of class DoubleMultiSet by multiplying all multiplicities
     * of elements iterable this DoubleMultiSet by the given number.
     * @param multiplier the multiplicator
     * @return the new instance of class DoubleMultiSet by multiplying all multiplicities
     * of elements iterable this DoubleMultiSet by the given number
     */
    public DoubleMultiSet multiplyCounts(double multiplier){
        return new DoubleMultiSet(VectorUtils.copyArray(this.values), VectorUtils.times(VectorUtils.copyArray(this.counts), multiplier));
    }

    /**
     * Computes intersection of the given DoubleMultiSets.
     * @param a the first DoubleMultiSet
     * @param b the second DoubleMultiSet
     * @return the intersection of the given DoubleMultiSets
     */
    public static DoubleMultiSet intersection(DoubleMultiSet a, DoubleMultiSet b){
        if (a.isEmpty() || b.isEmpty() || a.values[0] > b.values[b.values.length-1] || b.values[0] > a.values[a.values.length-1]){
            return emptyBag;
        }
        int distinctCount = 0;
        int indexA = 0;
        int indexB = 0;
        int aLength = a.values.length;
        int bLength = b.values.length;
        while (indexA < aLength && indexB < bLength){
            if (a.values[indexA] == b.values[indexB]){
                distinctCount++;
                indexA++;
                indexB++;
            } else if (a.values[indexA] < b.values[indexB]){
                indexA++;
            } else if (a.values[indexA] > b.values[indexB]){
                indexB++;
            }
        }
        if (distinctCount == aLength){
            return a;
        }
        if (distinctCount == bLength){
            return b;
        }
        double[] newValues = new double[distinctCount];
        double[] newCounts = new double[distinctCount];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength && indexB < bLength){
            if (a.values[indexA] == b.values[indexB]){
                newValues[index] = a.values[indexA];
                newCounts[index] = Math.min(a.counts[indexA], b.counts[indexB]);
                indexA++;
                indexB++;
                index++;
            } else if (a.values[indexA] < b.values[indexB]){
                indexA++;
            } else if (a.values[indexA] > b.values[indexB]){
                indexB++;
            }
        }
        if (distinctCount == 0){
            return emptyBag;
        } else {
            return new DoubleMultiSet(newValues, newCounts);
        }
    }

    /**
     * Computes union of the given DoubleMultiSets.
     * @param a the first DoubleMultiSet
     * @param b the second DoubleMultiSet
     * @return the union of the given DoubleMultiSets
     */
    public static DoubleMultiSet union(DoubleMultiSet a, DoubleMultiSet b){
        //System.out.print("Union of "+a+" and "+b+" is ");
        if (a instanceof EmptySet){
            return b;
        } else if (b instanceof EmptySet){
            return a;
        }
        int aLength = a.values.length;
        int bLength = b.values.length;
        if (a.values[aLength-1] < b.values[0]){
            double[] values = VectorUtils.concat(a.values, b.values);
            double[] counts = VectorUtils.concat(a.counts, b.counts);
            return new DoubleMultiSet(values, counts);
        } else if (a.values[0] > b.values[bLength-1]){
            double[] values = VectorUtils.concat(b.values, a.values);
            double[] counts = VectorUtils.concat(b.counts, a.counts);
            return new DoubleMultiSet(values, counts);
        }
        int count = 0;
        int indexA = 0;
        int indexB = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                if (a.values[indexA] == b.values[indexB]){
                    indexA++;
                    indexB++;
                    count++;
                } else if (a.values[indexA] < b.values[indexB]){
                    indexA++;
                    count++;
                } else if (a.values[indexA] > b.values[indexB]){
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
        double[] newValues = new double[count];
        double[] newCounts = new double[count];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                if (a.values[indexA] == b.values[indexB]){
                    newValues[index] = a.values[indexA];
                    newCounts[index] = a.counts[indexA] + b.counts[indexB];
                    indexA++;
                    indexB++;
                    index++;
                } else if (a.values[indexA] < b.values[indexB]){
                    newValues[index] = a.values[indexA];
                    newCounts[index] = a.counts[indexA];
                    indexA++;
                    index++;
                } else if (a.values[indexA] > b.values[indexB]){
                    newValues[index] = b.values[indexB];
                    newCounts[index] = b.counts[indexB];
                    indexB++;
                    index++;
                }
            } else if (indexA < aLength){
                newValues[index] = a.values[indexA];
                newCounts[index] = a.counts[indexA];
                indexA++;
                index++;
            } else if (indexB < bLength){
                newValues[index] = b.values[indexB];
                newCounts[index] = b.counts[indexB];
                indexB++;
                index++;
            }
        }
        //System.out.println(new DoubleBag(newValues, newCounts));
        return new DoubleMultiSet(newValues, newCounts);
    }

    /**
     * Computes intersection of the DoubleNultiSets iterable the given collection.
     * @param sets the collection of DoubleMultiSets
     * @return the  intersection of the DoubleNultiSets iterable the given collection
     */
    public static DoubleMultiSet intersection(Collection<DoubleMultiSet> sets){
        DoubleMultiSet result = null;
        for (DoubleMultiSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = intersection(result, set);
            }
        }
        return result;
    }

    /**
     * Computes intersection of the DoubleNultiSets iterable the given array.
     * @param sets the array of DoubleMultiSets
     * @return the intersection of the DoubleNultiSets iterable the given array
     */
    public static DoubleMultiSet intersection(DoubleMultiSet ...sets){
        DoubleMultiSet result = null;
        for (DoubleMultiSet set : sets){
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
     * of DoubleMultiSets
     * @param a the first array of DoubleMultiSets
     * @param b the second array of DoubleMultiSets
     * @return the intersections of the respective pairs from the given two arrays
     * of DoubleMultiSets
     */
    public static DoubleMultiSet[] intersection(DoubleMultiSet[] a, DoubleMultiSet[] b){
        DoubleMultiSet[] retVal = new DoubleMultiSet[a.length];
        for (int i = 0; i < a.length; i++){
            retVal[i] = intersection(a[i], b[i]);
        }
        return retVal;
    }

    /**
     * Computes union of the DoubleNultiSets iterable the given collection.
     * @param sets the collection of DoubleMultiSets
     * @return the union of the DoubleNultiSets iterable the given collection
     */
    public static DoubleMultiSet union(Collection<DoubleMultiSet> sets){
        DoubleMultiSet result = null;
        for (DoubleMultiSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = union(result, set);
            }
        }
        return result;
    }

    /**
     * Computes union of the DoubleNultiSets iterable the given array.
     * @param sets the array of DoubleMultiSets
     * @return the union of the DoubleNultiSets iterable the given array
     */
    public static DoubleMultiSet union(DoubleMultiSet[] sets){
        DoubleMultiSet result = null;
        for (DoubleMultiSet set : sets){
            if (result == null){
                result = set;
            } else {
                result = union(result, set);
            }
        }
        return result;
    }
    
    /**
     * Counts the number of non-empty DoubleMultiSets iterable the given array.
     * @param sets the array of DoubleMultiSets
     * @return the number of non-empty DoubleMultiSets iterable the given array.
     */
    public static int countNonEmpty(DoubleMultiSet[] sets){
        int count = 0;
        for (DoubleMultiSet is : sets){
            if (!is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of empty DoubleMultiSets iterable the given array.
     * @param sets the array of DoubleMultiSets
     * @return the number of empty DoubleMultiSets iterable the given array.
     */
    public static int countEmpty(DoubleMultiSet[] sets){
        int count = 0;
        for (DoubleMultiSet is : sets){
            if (is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if this DoubleMultiSet contains the number <em>d</em>.
     * @param d the number for which we check if it is present iterable the DoubleMultiSet
     * @return true if this DoubleMultiSet contains the number <em>d</em>.
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
     * Checks if this DoubleMultiSet is subset of the given DoubleMultiSet.
     * @param b the multi-set for which we want to check if it is a super-set of this set.
     * @return true if this DoubleMultiSet is subset of the given DoubleMultiSet
     */
    public boolean isSubsetOf(DoubleMultiSet b){
        if (b.isEmpty() || b.size() < this.size() || this.values[0] < b.values[0] || this.values[this.values.length-1] > b.values[b.values.length-1]){
            return false;
        }
        int i2 = 0;
        for (int i = 0; i < this.values.length; i++){
            while (i2 < b.values.length && b.values[i2] < this.values[i]){
                i2++;
            }
            if (i2 == b.values.length || b.values[i2] > this.values[i] || b.counts[i2] > this.counts[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this DoubleMultiSet is strict subset of the given DoubleMultiSet. "Strict subset"
     * means that it is a subset but it is not equal.
     * @param b the multi-set for which we want to check if it is a super-set of this set.
     * @return true if this DoubleMultiSet is strict subset of the given DoubleMultiSet
     */
    public boolean isStrictSubsetOf(DoubleMultiSet b){
        return !this.equals(b) && this.isSubsetOf(b);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof DoubleMultiSet){
            DoubleMultiSet cis = (DoubleMultiSet)o;
            if (this.isEmpty() != cis.isEmpty()){
                return false;
            }
            if ((cis.hashCode != -1 && this.hashCode != -1 && cis.hashCode != this.hashCode) || cis.values.length != this.values.length)
                return false;
            if (cis.values[cis.values.length-1] != this.values[this.values.length-1]){
                return false;
            }
            for (int i = 0; i < this.values.length-1; i++){
                if (this.values[i] != cis.values[i] || this.counts[i] != cis.counts[i]){
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
            //hash += (i+1)*this.values[i];
            hash = (int)((hash+1)*(1+this.values[i]*i*i+this.counts[i]*i)) % (Integer.MAX_VALUE/128);
            hash += Arrays.hashCode(this.values);
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
     * @return true if this DoubleMultiSet is empty, false otherwise
     */
    public boolean isEmpty(){
        return false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("IntegerMultiset[");
        for (int i = 0; i < this.values.length; i++){
            sb.append(this.counts[i]+"*"+this.values[i]);
            if (i < this.values.length-1){
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * @return the array of values iterable this DoubleMultiSet
     */
    public double[] values(){
        return this.values;
    }

    /**
     * 
     * @return the multiplicities of elements as returned by the method <em>values()</em>
     */
    public double[] counts(){
        return this.counts;
    }

    /**
     * 
     * @return number of elements iterable this DoubleMultiSet
     */
    public int size(){
        return this.values.length;
    }


    private static class EmptySet extends DoubleMultiSet {

        @Override
        public int size(){
            return 0;
        }

        @Override
        public boolean contains(double d){
            return false;
        }

        @Override
        public boolean isSubsetOf(DoubleMultiSet b){
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
    }

//    public static void main(String args[]){
//        DoubleBag a = DoubleBag.createDoubleBag(new double[]{0,1,6,7,9});
//        DoubleBag b = DoubleBag.createDoubleBag(new double[]{0,1,5});
//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(intersection(a,b));
//        System.out.println(union(b,a));
//    }

}
