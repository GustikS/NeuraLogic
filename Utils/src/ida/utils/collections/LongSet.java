package ida.utils.collections;

import ida.utils.VectorUtils;

import java.util.*;

/**
 * Created by ondrejkuzelka on 13/10/16.
 */
public class LongSet {

    private int hashCode = -1;

    private long[] values;

    /**
     * The unique empty set
     */
    public final static EmptySet emptySet = new EmptySet();

    private LongSet(){}

    private LongSet(long[] values){
        this(values, false);
    }

    private LongSet(long[] values, boolean sort){
        this.values = new long[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
        if (sort){
            Arrays.sort(this.values);
        }
    }

    /**
     *
     * @return minimum value iterable the set
     */
    public long min(){
        return this.values[0];
    }

    /**
     *
     * @return maximum value iterable the set
     */
    public long max(){
        return this.values[values.length-1];
    }

    /**
     * Creates a new instance of class IntegerSet from the given array of numbers.
     *
     * @param values the array of numbers
     * @return new instance of class IntegerSet from the given array of numbers
     */
    public static LongSet createLongSet(long ...values){
        Arrays.sort(values);
        if (values.length == 0){
            return emptySet;
        } else {
            return createLongSetFromSortedArray(values);
        }
    }

    /**
     * Creates a new instance of class IntegerSet from the given sorted array of numbers.
     *
     * @param values the array of numbers
     * @return new instance of class IntegerSet from the given sorted array of numbers
     */
    public static LongSet createLongSetFromSortedArray(long[] values){
        int duplicates = 0;
        for (int i = 0; i < values.length-1; i++){
            if (values[i] == values[i+1]){
                duplicates++;
            }
        }
        if (duplicates > 0){
            long[] newValues = new long[values.length-duplicates];
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
            return new LongSet(values, false);
        }
    }

    /**
     * Creates a new instance of class IntegerSet from the given set of numbers.
     *
     * @param set the set of numbers
     * @return new instance of class IntegerSet from the given set of numbers
     */
    public static LongSet createLongSet(Set<Long> set){
        long values[] = new long[set.size()];
        int index = 0;
        for (long integer : set){
            values[index] = integer;
            index++;
        }
        return createLongSet(values);
    }

    /**
     * Creates a new instance of class IntegerSet which contains
     * numbers start, start+1, ..., end-1.
     * @param start
     * @param end
     * @return new instance of class IntegerSet which contains
     * numbers start, start+1, ..., end-1.
     */
    public static LongSet createLongSetFromRange(int start, int end){
        long[] v = new long[end-start];
        for (int i = start, index = 0; i < end; i++){
            v[index] = i;
            index++;
        }
        return createLongSet(v);
    }


    /**
     * Computes intersection of the given sets.
     * @param a the first set
     * @param b the second set
     * @return the intersection of the given sets
     */
    public static LongSet intersection(LongSet a, LongSet b){
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
        long[] newValues = new long[count];
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
            LongSet set = new LongSet();
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
    public static LongSet union(LongSet a, LongSet b){
        if (a instanceof EmptySet){
            return b;
        } else if (b instanceof EmptySet){
            return a;
        }
        int aLength = a.values.length;
        int bLength = b.values.length;
        long[] aValues = a.values;
        long[] bValues = b.values;
        if (a == b){
            return a;
        } else if (aValues[aLength-1] < bValues[0]){
            long[] values = VectorUtils.concat(aValues, bValues);
            return new LongSet(values);
        } else if (aValues[0] > bValues[bLength-1]){
            long[] values = VectorUtils.concat(bValues, aValues);
            return new LongSet(values);
        }
        int count = 0;
        int indexA = 0;
        int indexB = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                long aValue = aValues[indexA];
                long bValue = bValues[indexB];
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
        long[] newValues = new long[count];
        indexA = 0;
        indexB = 0;
        int index = 0;
        while (indexA < aLength || indexB < bLength){
            if (indexA < aLength && indexB < bLength){
                long aValue = aValues[indexA];
                long bValue = bValues[indexB];
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
        LongSet set = new LongSet();
        set.values = newValues;
        return set;
    }

    /**
     * Computes difference of the given sets (<em>a</em> - <em>b</em>).
     * @param a the first set
     * @param b the second set
     * @return the difference of the given sets
     */
    public static LongSet difference(LongSet a, LongSet b){
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
        long[] newValues = new long[count];
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
        LongSet set = new LongSet();
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
    public static boolean allAreSubsets(List<LongSet> a, List<LongSet> b){
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
    public static boolean allAreSubsets(LongSet[] a, LongSet[] b){
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
    public static boolean allAreEmpty(List<LongSet> a){
        for (LongSet is : a){
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
    public static boolean allAreEmpty(LongSet[] a){
        for (LongSet is : a){
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
    public static boolean someAreEmpty(List<LongSet> a){
        for (LongSet is : a){
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
    public static boolean someAreEmpty(LongSet[] a){
        for (LongSet is : a){
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
    public static boolean allAreEmpty(LongSet[] a, boolean[] mask){
        int i = 0;
        for (LongSet is : a){
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
    public static boolean someAreEmpty(LongSet[] a, boolean[] mask){
        int i = 0;
        for (LongSet is : a){
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
    public static boolean someAreSubsets(List<LongSet> a, List<LongSet> b){
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
    public static boolean someAreSubsets(LongSet[] a, LongSet[] b){
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
    public static boolean allAreSubsets(LongSet[] a, LongSet[] b, boolean[] mask){
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
    public static boolean someAreSubsets(LongSet[] a, LongSet[] b, boolean[] mask){
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
    public static boolean allAreEqual(List<LongSet> a, List<LongSet> b){
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
    public static boolean allAreEqual(LongSet[] a, LongSet[] b){
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
    public static boolean someAreEqual(List<LongSet> a, List<LongSet> b){
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
    public static boolean someAreEqual(LongSet[] a, LongSet[] b){
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
    public static boolean allAreEqual(LongSet[] a, LongSet[] b, boolean[] mask){
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
    public static boolean someAreEqual(LongSet[] a, LongSet[] b, boolean[] mask){
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
    public static LongSet intersection(Collection<LongSet> sets){
        LongSet result = null;
        for (LongSet set : sets){
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
     * Computes intersection of the sets iterable the given array.
     * @param sets the array of sets
     * @return the intersection of the sets iterable the given array
     */
    public static LongSet intersection(LongSet ...sets){
        LongSet result = null;
        for (LongSet set : sets){
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
    public static LongSet[] intersection(LongSet[] a, LongSet[] b){
        LongSet[] retVal = new LongSet[a.length];
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
    public static List<LongSet> intersection(List<LongSet> a, List<LongSet> b){
        List<LongSet> retVal = new ArrayList<LongSet>(a.size());
        Iterator<LongSet> iter1 = a.iterator();
        Iterator<LongSet> iter2 = b.iterator();
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
    public static LongSet union(Collection<LongSet> sets){
        LongSet result = null;
        for (LongSet set : sets){
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
     * Computes union of the sets iterable the given array.
     * @param sets the array of sets
     * @return the union of the sets iterable the given array
     */
    public static LongSet union(LongSet ...sets){
        LongSet result = null;
        for (LongSet set : sets){
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
    public static LongSet[] union(LongSet[] a, LongSet[] b){
        LongSet[] retVal = new LongSet[a.length];
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
    public static List<LongSet> union(List<LongSet> a, List<LongSet> b){
        List<LongSet> retVal = new ArrayList<LongSet>(a.size());
        Iterator<LongSet> iter1 = a.iterator();
        Iterator<LongSet> iter2 = b.iterator();
        while (iter1.hasNext() && iter2.hasNext()){
            retVal.add(union(iter1.next(), iter2.next()));
        }
        return retVal;
    }

    /**
     * Counts the number of non-empty sets iterable the given list.
     * @param sets the list of sets
     * @return the number of non-empty sets iterable the given list
     */
    public static int countNonEmpty(List<LongSet> sets){
        int count = 0;
        for (LongSet is : sets){
            if (!is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of empty sets iterable the given list.
     * @param sets the list of sets
     * @return the number of empty sets iterable the given list
     */
    public static int countEmpty(List<LongSet> sets){
        int count = 0;
        for (LongSet is : sets){
            if (is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of non-empty sets iterable the given array.
     * @param sets the list of sets
     * @return the number of non-empty sets iterable the given array
     */
    public static int countNonEmpty(LongSet[] sets){
        int count = 0;
        for (LongSet is : sets){
            if (!is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of empty sets iterable the given array.
     * @param sets the list of sets
     * @return the number of empty sets iterable the given array
     */
    public static int countEmpty(LongSet[] sets){
        int count = 0;
        for (LongSet is : sets){
            if (is.isEmpty()){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of non-empty sets iterable the given array w.r.t. the given mask.
     * @param sets the array of sets
     * @param mask the mask denoting which sets from the array should be considered
     * @return the number of non-empty sets iterable the given array.
     */
    public static int countNonEmpty(LongSet[] sets, boolean[] mask){
        int count = 0;
        int i = 0;
        for (LongSet is : sets){
            if (mask[i] && !is.isEmpty()){
                count++;
            }
            i++;
        }
        return count;
    }

    /**
     * Counts the number of empty sets iterable the given array w.r.t. the given mask.
     * @param sets the array of sets
     * @param mask the mask denoting which sets from the array should be considered
     * @return the number of empty sets iterable the given array.
     */
    public static int countEmpty(LongSet[] sets, boolean[] mask){
        int count = 0;
        int i = 0;
        for (LongSet is : sets){
            if (mask[i] && is.isEmpty()){
                count++;
            }
            i++;
        }
        return count;
    }

    /**
     * Computes the sum of sizes of the sets iterable the given array of sets.
     * @param sets the array of sets
     * @return sum of sizes of the sets iterable the given array
     */
    public static int sumSizes(LongSet[] sets){
        int sum = 0;
        for (LongSet is : sets){
            sum += is.size();
        }
        return sum;
    }

    /**
     * Computes the sum of sizes of the sets iterable the given array of sets w.r.t. the given mask.
     * @param sets the array of sets
     * @param mask the mask which denotes which sets should be considered when computing the sum
     * @return sum of sizes of the sets iterable the given array
     */
    public static int sumSizes(LongSet[] sets, boolean[] mask){
        int sum = 0;
        int i = 0;
        for (LongSet is : sets){
            if (mask[i]){
                sum += is.size();
            }
            i++;
        }
        return sum;
    }

    /**
     * Checks if this set contains the number <em>integer</em>.
     * @param integer the number for which we check if it is present iterable the set
     * @return true if this set contains the number <em>integer</em>.
     */
    public boolean contains(long integer){
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
     * Checks if this set is subset of the given set <em>b</em>.
     * @param b the set for which we want to check if it is a super-set of this set.
     * @return true if this set is subset of the given set <em>b</em>
     */
    public boolean isSubsetOf(LongSet b){
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
    public boolean isStrictSubsetOf(LongSet b){
        return !this.equals(b) && this.isSubsetOf(b);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof LongSet){
            LongSet cis = (LongSet)o;
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
        long hash = 1;
        for (int i = 0; i < this.values.length; i++){
            //hash += (i+1)*this.values[i];
            hash = ((hash+1)*(1+this.values[i]*i*i)) % (Integer.MAX_VALUE/128);
        }
        this.hashCode = (int)(hash % 1 << 30);
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
    public Set<Long> toSet(){
        LinkedHashSet<Long> retVal = new LinkedHashSet<Long>();
        for (long i : this.values){
            retVal.add(i);
        }
        return retVal;
    }

    /**
     *
     * @return list with the elements of the IntegerSet
     */
    public List<Long> toList(){
        List<Long> retVal = new ArrayList<Long>();
        for (long i : this.values){
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
        return "LongSet"+ VectorUtils.longArrayToString(values);
    }

    /**
     * @return the elements of the DoubleSet
     */
    public long[] values(){
        return this.values;
    }

    /**
     *
     * @return number of elements iterable the DoubleSet
     */
    public int size(){
        return this.values.length;
    }

    private static class EmptySet extends LongSet {

        private long[] emptyArray = new long[0];
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
        public boolean contains(long integer){
            return false;
        }

        /**
         *
         * @param b
         * @return
         */
        @Override
        public boolean isSubsetOf(LongSet b){
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
        public long[] values(){
            return emptyArray;
        }

        @Override
        public Set<Long> toSet(){
            return new HashSet<Long>(1);
        }

        public List<Long> toList(){
            return new ArrayList<Long>(1);
        }
    }

}
