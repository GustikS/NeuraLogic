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
 * Sugar.java
 *
 * Created on 30. ?ervenec 2006, 19:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.utils.math;

import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Tuple;

import java.math.BigInteger;
import java.util.*;

/**
 * Class with many collection-manipulation and other useful methods.
 * 
 * @author Ondrej Kuzelka
 */
public class Sugar {
    
    private static Random random = new Random(Settings.seed);

    public final static Object NIL = new Object(){
        public String toString(){
            return "NIL";
        }
    };

    /**
     * Creates an empty list
     * @param <T> 
     * @return an empty list
     */
    public static <T> List<T> list(){
        return new ArrayList<T>();
    }
    
    /**
     * Creates a new list containing the given elements, shorthand for:
     * List<T> list = new ArrayList<T>();<br />
     * list.add("a");<br />
     * list.add("b");<br />
     * It is considerably simpler to use: Sugar.<T>list("a", "b");
     * @param <T> the type of the elements of the list
     * @param args the elements which should be iterable the list
     * @return a new list containing the specified elements
     */
    public static <T> List<T> list(T... args){
        ArrayList<T> list = new ArrayList<T>();
        for (T o : args){
            list.add(o);
        }
        return list;
    }
    
    
    /**
     * Creates a sub-list from the given list and indices.
     * @param <T> type of the elements of the list
     * @param list the list from which the elements should be taken
     * @param indices the indices denoting which elements should be included iterable the new list.
     * @return a new list with the specified elements
     */
    public static <T> List<T> list(List<T> list, int[] indices){
        ArrayList<T> retVal = new ArrayList<T>();
        for (int index : indices){
            retVal.add(list.get(index));
        }
        return retVal;
    }

    /**
     * Creates a new list which contains <em>count</em> copies of the given element.
     * @param <T> type of the elements iterable the list
     * @param t the element with which the list should be filled
     * @param count the number of times the element <em>t</em> should be added to the list
     * @return a new list which contains <em>count</em> copies of the given element.
     */
    public static <T> List<T> filledList(T t, int count){
        return filledArrayList(t, count);
    }

    /**
     * Creates a new ArrayList which contains <em>count</em> copies of the given element.
     * @param <T> type of the elements iterable the list
     * @param t the element with which the list should be filled
     * @param count the number of times the element <em>t</em> should be added to the list
     * @return a new ArrayList which contains <em>count</em> copies of the given element.
     */
    public static <T> List<T> filledArrayList(T t, int count){
        List<T> retVal = new ArrayList<T>();
        for (int i = 0; i < count; i++){
            retVal.add(t);
        }
        return retVal;
    }

    /**
     * Creates a new LinkedList which contains <em>count</em> copies of the given element.
     * @param <T> type of the elements iterable the list
     * @param t the element with which the list should be filled
     * @param count the number of times the element <em>t</em> should be added to the list
     * @return a new LinkedList which contains <em>count</em> copies of the given element.
     */
    public static <T> List<T> filledLinkedList(T t, int count){
        List<T> retVal = new LinkedList<T>();
        for (int i = 0; i < count; i++){
            retVal.add(t);
        }
        return retVal;
    }

    /**
     * Creates a new tuple containing the given elements
     * @param <T> type of the elements iterable the tuple
     * @param args the elements which should be iterable the tuple
     * @return a new tuple containing the specified elements
     */
    public static <T> Tuple<T> tuple(T... args){
        Tuple<T> tuple = new Tuple<T>(args.length);
        int i = 0;
        for (T o : args){
            tuple.set(o, i);
            i++;
        }
        return tuple;
    }
    
    
    /**
     * Creates a tuple from the given list and indices.
     * @param <T> type of the elements of the list
     * @param list the list from which the elements should be taken
     * @param indices the indices denoting which elements should be included iterable the new list.
     * @return a new tuple with the specified elements
     */
    public static <T> Tuple<T> tuple(List<T> list, int[] indices){
        Tuple<T> retVal = new Tuple<T>(indices.length);
        int i = 0;
        for (int index : indices){
            retVal.set(list.get(index), i++);
        }
        return retVal;
    }
    
    /**
     * Shuffles a list and returns it - it is the same list, this method makes
     * changes to its argument and returns it (this is useful for some one-line expressions iterable the functional-programming style :-))
     * @param <T> types of the elements on the list
     * @param list the list to be shuffled
     * @return the shuffled list which was given as the argument to this function
     */
    public static <T> List<T> shuffle(List<T> list){
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Shuffles a list and returns it - it is the same list, this method makes
     * changes to its argument and returns it (this is useful for some one-line expressions iterable the functional-programming style :-))
     * @param <T> types of the elements on the list
     * @param list the list to be shuffled
     * @param random random number generator used for shuffling
     * @return the shuffled list which was given as the argument to this function
     */
    public static <T> List<T> shuffle(List<T> list, Random random){
        Collections.shuffle(list, random);
        return list;
    }
    
    /**
     * Shuffles a list and returns it - it is the same list, this method makes
     * changes to its argument and returns it (this is useful for some one-line expressions iterable the functional-programming style :-))
     * @param <T> types of the elements on the list
     * @param list the list to be shuffled
     * @param p "probability of shuffling two elements" - when this value is set to 1 then it is equivelent to shiffle(list)
     * @return the shuffled list which was given as the argument to this function
     */
    public static <T> List<T> shuffle(List<T> list, double p){
        for (int i = 0; i < list.size()/2; i++){
            if (Math.random() < p){
                Collections.swap(list, i, random.nextInt(list.size()-i)+i);
            }
        }
        return list;
    }

    /**
     * 
     * @return random integer from 0...Integer.MAX_VALUE
     */
    public static int random(){
        return random.nextInt();
    }

    /**
     * 
     * @return random double from [0,1]
     */
    public static double randomDouble(){
        return random.nextDouble();
    }

    /**
     * 
     * @param n
     * @return random integer from 0 to n-1
     */
    public static int random(int n){
        return random.nextInt(n);
    }

    /**
     * Creates a new list containing elements from the given collections.
     * @param <T> type of the elements iterable the list.
     * @param args collections containing elements which should be included into the new list
     * @return a list containing all elements from the given collections 
     */
    public static <T> List<T> listFromCollections(Collection<T>... args){
        return arrayListFromCollections(args);
    }

    /**
     * Creates a new ArrayList containing elements from the given collections.
     * @param <T> type of the elements iterable the list.
     * @param args collections containing elements which should be included into the new list
     * @return an ArrayList containing all elements from the given collections 
     */
    public static <T> ArrayList<T> arrayListFromCollections(Collection<T>... args){
        int size = 0;
        for (Collection<T> c : args){
            size += c.size();
        }
        ArrayList<T> list = new ArrayList<T>(size);
        for (Collection<T> o : args)
            list.addAll(o);
        return list;
    }

    /**
     * Creates a new LinkedList containing elements from the given collections.
     * @param <T> type of the elements iterable the list.
     * @param args collections containing elements which should be included into the new list
     * @return an LinkedList containing all elements from the given collections 
     */
    public static <T> LinkedList<T> linkedListFromCollections(Collection<T>... args){
        LinkedList<T> list = new LinkedList<T>();
        for (Collection<T> o : args)
            list.addAll(o);
        return list;
    }
    
    /**
     * Computes diffrence of the given two lists (the new list does not contain any value that has been
     * contained iterable the second list).
     * @param <R> type of the elements iterable the list
     * @param a the list from which we want to subtract
     * @param b the list we want subtract from the first list
     * @return the difference of the lists
     */
    public static <R> List<R> listDifference(Collection<R> a, Collection<R> b){
        LinkedHashSet<R> set = new LinkedHashSet<R>();
        set.addAll(b);
        List<R> diff = new ArrayList<R>();
        for (R r : a){
            if (!b.contains(r))
                diff.add(r);
        }
        return diff;
    }
    
    /**
     * Computes diffrence of the given two sets (the new set does not contain any value that has been
     * contained iterable the second set).
     * @param <R> type of the elements iterable the set
     * @param a the set from which we want to subtract
     * @param b the set we want subtract from the first set
     * @return the difference of the sets
     */
    public static <R> Set<R> setDifference(Set<R> a, Set<R> b){
        Set<R> diff = new LinkedHashSet<R>();
        for (R r : a){
            if (!b.contains(r)){
                diff.add(r);
            }
        }
        return diff;
    }

    public static <R> Set<R> setDifference(Set<R> a, R b){
        Set<R> diff = new LinkedHashSet<R>();
        for (R r : a){
            if (!b.equals(r)){
                diff.add(r);
            }
        }
        return diff;
    }
    
    /**
     * Computes diffrence of the given two collections (the new set does not contain any value that has been
     * contained iterable the second collection).
     * @param <R> type of the elements iterable the collection
     * @param a the collection from which we want to subtract
     * @param b the collection we want subtract from the first collection
     * @return the difference of the collections
     */
    public static <R> Set<R> collectionDifference(Collection<? extends R> a, Collection<? extends R> b){
        LinkedHashSet<R> set = new LinkedHashSet<R>();
        set.addAll(b);
        Set<R> diff = new LinkedHashSet<R>();
        for (R r : a){
            if (!set.contains(r)) {
                diff.add(r);
            }
        }
        return diff;
    }

    public static <R> Set<R> collectionDifference(Collection<? extends R> a, R b){
        Set<R> diff = new LinkedHashSet<R>();
        for (R r : a){
            if (!r.equals(b)) {
                diff.add(r);
            }
        }
        return diff;
    }
    
    /**
     * Creates a Map from a collection of pairs.
     * @param <R> type of the key-elements
     * @param <S> type of the value-elements
     * @param coll the collection with pairs
     * @return Map constructed from the pairs iterable which keys correspond to the first elements
     * of the pairs, and values correspond to the respective second elements
     */
    public static <R, S> Map<R, S> mapFromPairs(Collection<Pair<R, S>> coll){
        HashMap<R, S> map = new HashMap<R, S>();
        for (Pair<R, S> pair : coll){
            map.put(pair.r, pair.s);
        }
        return map;
    }

    /**
     * Creates a MultiMap from a collection of pairs.
     * @param <R> type of the key-elements
     * @param <S> type of the value-elements
     * @param coll the collection with pairs
     * @return MultiMap constructed from the pairs iterable which keys correspond to the first elements
     * of the pairs, and values correspond to the respective second elements
     */
    public static <R, S> MultiMap<R, S> multiMapFromPairs(Collection<Pair<R, S>> coll){
        MultiMap<R, S> map = new MultiMap<R, S>();
        for (Pair<R, S> pair : coll){
            map.put(pair.r, pair.s);
        }
        return map;
    }
    
    
    /**
     * Creates an empty set
     * @param <T> type of the elements iterable the set
     * @return a new empty set
     */
    public static <T> Set<T> set(){
        return new LinkedHashSet<T>();
    }
    
    /**
     * Creates a new set containing the given elements, shorthand for:
     * Set<T> set = new HeshSet<T>();<br />
     * set.add("a");<br />
     * set.add("b");<br />
     * It is considerably simpler to use: Sugar.<T>set("a", "b");
     * @param <T> the type of the elements of the set
     * @param args the elements which should be iterable the set
     * @return a new set containing the specified elements
     */
    public static <T> Set<T> set(T... args){
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        for (T o : args)
            set.add(o);
        return set;
    }
    
    /**
     * Creates a new set containing elements from the given collections.
     * @param <T> type of the elements iterable the set.
     * @param args collections containing elements which should be included into the new set
     * @return a set containing all elements from the given collections 
     */
    public static <T> Set<T> setFromCollections(Iterable<T>... args){
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        for (Iterable<T> o : args){
            for (T t : o) {
                set.add(t);
            }
        }
        return set;
    }

    /**
     * Creates a new LinkedHashSet containing elements from the given collections.
     * @param <T> type of the elements iterable the set.
     * @param args collections containing elements which should be included into the new set
     * @return a LinkedHashSet containing all elements from the given collections 
     */
    public static <T> LinkedHashSet<T> linkedHashSetFromCollections(Collection<T>... args){
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        for (Collection<T> o : args){
            set.addAll(o);
        }
        return set;
    }

    /**
     * Checks if the first set is a subset of the second set.
     * @param first the first set
     * @param second the second set
     * @return true if the first set is a subset of the second set
     */
    public static boolean isSubsetOf(Set first, Set second){
        if (first.size() > second.size()){
            return false;
        }
        for (Object o : first){
            if (!second.contains(o)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Computes union of two lists. The result is a set.
     * @param <R> type of the elements iterable the lists and iterable the resulting set
     * @param a the first list
     * @param b the second list
     * @return a set containing all elements from the two lists
     */
    public static <R> Set<R> union(Collection<R> a, Collection<R> b){
        return setFromCollections(a, b);
    }

    public static <R> Set<R> union(Collection<R> ...a){
        return setFromCollections(a);
    }

    /**
     * Computes union of two lists. The result is a set.
     * @param <R> type of the elements iterable the lists and iterable the resulting set
     * @param a the first list
     * @param b the second list
     * @return a set containing all elements from the two lists
     */
    public static <R> Set<R> union(Collection<R> a, R ...b){
        Set<R> retVal = setFromCollections(a);
        for (R r  : b) {
            retVal.add(r);
        }
        return retVal;
    }

    public static <R> List<R> concat(List<R> a, List<R> b){
        List<R> retVal = new ArrayList<R>();
        retVal.addAll(a);
        retVal.addAll(b);
        return retVal;
    }

    public static <R> List<R> append(List<R> a, R b){
        List<R> retVal = new ArrayList<R>();
        retVal.addAll(a);
        retVal.add(b);
        return retVal;
    }
    
    
    /**
     * Computes intersection of two sets. The result is a set.
     * @param <R> type of the elements iterable the sets
     * @param a the first set
     * @param b the second set
     * @return a set containing all elements which are contained iterable both of the sets at the same time
     */
    public static <R> Set<R> intersection(Set<R> a, Set<R> b){
        Set<R> ret = new LinkedHashSet<R>();
        if (a.size() < b.size()){
            for (R r : a)
                if (b.contains(r))
                    ret.add(r);
        }
        else {
            for (R r : b)
                if (a.contains(r))
                    ret.add(r);
        }
        return ret;
    }

    /**
     * Checks if the two given sets are disjoint.
     * @param a the first set
     * @param b the second set
     * @return true if the two sets are disjoint, false otherwise
     */
    public static boolean areDisjoint(Set a, Set b){
        if (a.size() > b.size()){
            Set temp = a;
            a = b;
            b = temp;
        }
        for (Object o : a){
            if (b.contains(o)){
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new map by transforming the given maps.
     * @param <R> type of key-elements
     * @param <S> type of value-elements
     * @param maps the maps to be merged into the new map
     * @return a new map which contains all key-value pairs from the given maps. Caution: if there are different
     * values for the same key then it is not guaranteed which value will be used.
     */
    public static <R,S> Map<R,S> mapFromMaps(Map<R,S> ...maps){
        LinkedHashMap<R,S> map = new LinkedHashMap<R,S>();
        for (Map<R,S> m : maps){
            map.putAll(m);
        }
        return map;
    }
    
    /**
     * Gets all elements from the given map and for the given collection of keys.
     * @param <T> type of the value-elements of the map
     * @param map the map
     * @param keys the keys 
     * @return list of all elements corresponding to keys iterable the collection <em>keys</em>
     */
    public static <T> List<T> getAll(Map<?,T> map, Collection<?> keys){
        List<T> list = new ArrayList<T>();
        for (Object o : keys){
            list.add(map.get(o));
        }
        return list;
    }
    
    /**
     * Gets all elements from the given list and the given collection of indices of the elements iterable the list.
     * @param <T> type of the elements of the list
     * @param list the list
     * @param indices the indices
     * @return the new list
     */
    public static <T> List<T> getAll(List<T> list, Collection<Integer> indices){
        List<T> retVal = new ArrayList<T>(indices.size());
        for (int index : indices){
            retVal.add(list.get(index));
        }
        return retVal;
    }
    
    /**
     * Gets all elements from the given list and the given array of indices of the elements iterable the list.
     * @param <T> type of the elements of the list
     * @param list the list
     * @param indices the indices
     * @return the new list
     */
    public static <T> List<T> getAll(List<T> list, int ...indices){
        List<T> retVal = new ArrayList<T>(indices.length);
        for (int index : indices){
            retVal.add(list.get(index));
        }
        return retVal;
    }
    
    /**
     * Gets all elements from the given array and the given array of indices of the elements iterable the array.
     * @param <T> type of the elements of the list
     * @param array the array
     * @param indices the indices
     * @return the new list
     */
    public static <T> List<T> getAll(T[] array, int ...indices){
        List<T> retVal = new ArrayList<T>(indices.length);
        for (int index : indices){
            retVal.add(array[index]);
        }
        return retVal;
    }
    
    /**
     * Gets and removes all elements from the given list and the given collection of indices of the elements iterable the list.
     * @param <T> type of the elements of the list
     * @param list the list
     * @param indices the indices
     * @return the new list
     */
    public static <T> List<T> removeAll(List<T> list, Collection<Integer> indices){
        Set<Integer> indexSet = Sugar.setFromCollections(indices);
        List<T> retVal = new ArrayList<T>(indices.size());
        int index = 0;
        Iterator<T> iter = list.iterator();
        while (iter.hasNext()){
            T t = iter.next();
            if (indexSet.contains(index)){
                retVal.add(t);
                iter.remove();
            }
            index++;
        }
        return retVal;
    }

    /**
     * Gets and removes one arbitrary Map.Entry object from the given Map
     * @param <T> type of keys
     * @param <U> type of values
     * @param map the map from which an entry should be removed
     * @return the removed entry from the map
     */
    public static <T,U> Map.Entry<T,U> removeOne(Map<T,U> map){
        if (map.isEmpty()){
            return null;
        }
        Map.Entry<T,U> entry = null;
        for (Map.Entry<T,U> e : map.entrySet()){
            entry = e;
            break;
        }
        map.remove(entry.getKey());
        return entry;
    }

    /**
     * Gets and removes one arbitrary element from the given list
     * @param <T> type of elements
     * @param l the list from which a value should be removed
     * @return the removed element from the list
     */
    public static <T> T removeOne(List<T> l){
        if (l.isEmpty()){
            return null;
        }
        if (l instanceof LinkedList){
            return ((LinkedList<T>)l).removeFirst();
        } else {
            T t = l.get(l.size()-1);
            l.remove(l.size()-1);
            return t;
        }
    }

    /**
     * Gets and removes one arbitrary element from the given collection
     * @param <T> type of elements
     * @param c the collection from which a value should be removed
     * @return the removed element from the collection
     */
    public static <T> T removeOne(Collection<T> c){
        T t = null;
        Iterator<T> iter = c.iterator();
        if (iter.hasNext()){
            t = iter.next();
            iter.remove();
        }
        return t;
    }

    /**
     * Gets one arbitrary element from the given collection
     * @param <T> type of elements
     * @param c the collection from which a value should be selected
     * @return the selected element from the collection
     */
    public static <T> T chooseOne(Iterable<T> c){
        T t = null;
        for (T tt : c){
            t = tt;
            break;
        }
        return t;
    }
    
    /**
     * Gets one arbitrary value-element from the given map
     * @param <T> type of value-elements
     * @param map the map from which a value should be selected
     * @return the selected value-element from the map
     */
    public static <T> T chooseOne(Map<? extends Object, T> map){
        T t = null;
        for (T tt : map.values()){
            t = tt;
            break;
        }
        return t;
    }
    
    /**
     * Gets one random element from the given list
     * @param <T> type of elements
     * @param l the list from which an element should be selected
     * @return the randomly selected element from the list
     */
    public static <T> T chooseRandomOne(List<T> l){
        if (l.isEmpty()){
            return null;
        }
        return l.get(random.nextInt(l.size()));
    }

    public static <T> T chooseRandomOne(List<T> l, Random random){
        if (l.isEmpty()){
            return null;
        }
        return l.get(random.nextInt(l.size()));
    }
    
    /**
     * Creates a string representation of an array of Objects
     * @param array the array of Objects
     * @return the string representation of the given array of Objects
     */
    public static String objectArrayToString(Object[] array){
        return objectArrayToString(array, false);
    }

    public static String objectArrayToString(Object[] array, boolean showClassNames){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object a : array){
            if (showClassNames){
                sb.append(a.getClass().getName()).append(":");
            }
            sb.append(a).append(", ");
        }
        if (sb.lastIndexOf(", ") != -1)
            sb.delete(sb.lastIndexOf(", "), sb.length());
        sb.append("]");
        return sb.toString();
    }

    /**
     * Finds the "best" element of a collection using the given MyComparator.
     * @param <T> type of the elements iterable the collection
     * @param c the collection
     * @param comparator the comparator which has method isABetterThanB(a,b)
     * @return the "best" element from the given collection
     */
    public static <T> T findBest(Collection<T> c, MyComparator comparator){
        T best = null;
        for (T t : c){
            if (best == null || comparator.isABetterThanB(t, best)){
                best = t;
            }
        }
        return best;
    }

    /**
     * Sorts the given list iterable ascending order and returns it - it is the same list, this method makes
     * changes to its argument and returns it (this is useful for some one-line expressions iterable the functional-programming style :-))
     * @param <T> types of the elements on the list
     * @param elements the list to be sorted
     * @return the sorted list which was given as the argument to this function
     */
    public static <T extends Comparable> List<T> sortAsc(List<T> elements){
        Collections.sort(elements);
        return elements;
    }

    /**
     * Sorts the given list iterable descending order and returns it - it is the same list, this method makes
     * changes to its argument and returns it (this is useful for some one-line expressions iterable the functional-programming style :-))
     * @param <T> types of the elements on the list
     * @param elements the list to be sorted
     * @return the sorted list which was given as the argument to this function
     */
    public static <T extends Comparable> List<T> sortDesc(List<T> elements){
        Collections.sort(elements);
        Collections.reverse(elements);
        return elements;
    }

    /**
     * Sorts the given list iterable ascending order according to values iterable the Map <em>scores</em> and returns it - it is the same list, this method makes
     * changes to its argument and returns it (this is useful for some one-line expressions iterable the functional-programming style :-))
     * @param <T> types of the elements on the list
     * @param elements the list to be sorted
     * @return the sorted list which was given as the argument to this function
     */
    public static <T> List<T> sortAsc(List<T> elements, Map<T,? extends Comparable> scores){
        Collections.sort(elements, new MapBasedComparator(scores, MapBasedComparator.ASC));
        return elements;
    }
    
    /**
     * Sorts the given list iterable descending order according to values iterable the Map <em>scores</em> and returns it - it is the same list, this method makes
     * changes to its argument and returns it (this is useful for some one-line expressions iterable the functional-programming style :-))
     * @param <T> types of the elements on the list
     * @param elements the list to be sorted
     * @return the sorted list which was given as the argument to this function
     */
    public static <T> List<T> sortDesc(List<T> elements, Map<T,? extends Comparable> scores){
        Collections.sort(elements, new MapBasedComparator(scores, MapBasedComparator.DESC));
        return elements;
    }
    
    private static class MapBasedComparator<T> implements Comparator<T> {

        private final static int ASC = 1, DESC = 2;
        
        private Map<T,? extends Comparable> scores;
        
        private int mode = ASC;
        
        public MapBasedComparator(Map<T,? extends Comparable> scores, int mode){
            this.scores = scores;
            this.mode = mode;
        }
        
        public int compare(T arg0, T arg1) {
            int scoreDiff = scores.get(arg0).compareTo(scores.get(arg1));
            if (scoreDiff == 0){
                return arg0.toString().compareTo(arg1.toString());
            } else if (mode == ASC){
                return scoreDiff;
            } else {
                return -scoreDiff;
            }
        }
    }
    
    /**
     * Creates a new list which contains all elements as the original list iterable this same
     * order except for nulls which are removed.
     * @param <T> type of elements iterable the list
     * @param list the list
     * @return the new list witout null-values
     */
    public static <T> List<T> removeNulls(List<T> list){
        List<T> retVal = new ArrayList<T>();
        for (T t : list){
            if (t != null){
                retVal.add(t);
            }
        }
        return retVal;
    }

    /**
     * Creates a new set which contains all elements as the original list
     *  except for nulls which are removed.
     * @param <T> type of elements iterable the list
     * @param set the set
     * @return the new set witout null-values
     */
    public static <T> Set<T> removeNulls(Set<T> set){
        Set<T> retVal = new LinkedHashSet<T>();
        for (T t : set){
            if (t != null){
                retVal.add(t);
            }
        }
        return retVal;
    }

    /**
     * Counts the number of nulls iterable the given collection
     * @param coll the collection
     * 
     * @return the number of nulls iterable the given collection
     */
    public static int countNulls(Collection coll){
        int count = 0;
        for (Object o : coll){
            if (o == null){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of nulls iterable the given array
     * @param array the array
     * 
     * @return the number of nulls iterable the given array
     */
    public static int countNulls(Object[] array){
        int count = 0;
        for (Object o : array){ 
            if (o == null){
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts the number of occurrences of the given object iterable the given collection
     * @param match the object whose occurrences should be computed
     * @param coll the collection
     * @return the number of occurrences of the given object iterable the given collection
     */
    public static int countOccurences(Object match, Collection coll){
        int count = 0;
        for (Object o : coll){
            if (match.equals(o)){
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of occurrences of the given object iterable the given array
     * @param match the object whose occurrences should be computed
     * @param array the array
     * @return the number of occurrences of the given object iterable the given array
     */
    public static int countOccurences(Object match, Object[] array){
        int count = 0;
        for (Object o : array){
            if (match.equals(o)){
                count++;
            }
        }
        return count;
    }
    
    /**
     * Finds the maximum iterable the given collection of numbers.
     * @param coll the collection of numbers
     * @return the maximum iterable the collection
     */
    public static double max(Collection<? extends Number> coll){
        double max = Double.NEGATIVE_INFINITY;
        for (Number d : coll){
            max = Math.max(max, d.doubleValue());
        }
        return max;
    }

    /**
     * 
     * Finds the minimum iterable the given collection of numbers.
     * @param coll the collection of numbers
     * @return the minimum iterable the collection
     */
    public static double min(Collection<? extends Number> coll){
        double min = Double.POSITIVE_INFINITY;
        for (Number d : coll){
            min = Math.min(min, d.doubleValue());
        }
        return min;
    }

    /**
     * Applies the given function on each element from the given iterable and stores the results iterable a new iterable.
     * @param <T> type of the elements iterable the iterable to be processed
     * @param <U> type of the elements iterable the new iterable
     * @param iterable the iterable to be processed
     * @param fun the function to be applied on the elements from the iterable
     * @return new iterable which contains the results of applying the function <em>fun</em> on the elements of the old iterable
     */
    public static <T,U> List<U> funcall(Iterable<T> iterable, Fun<T,U> fun){
        List<U> us = new ArrayList<U>();
        for (T t : iterable){
            us.add(fun.apply(t));
        }
        return us;
    }

    /**
     * Applies the given function on each element from the given set and stores the results iterable a new set.
     * @param <T> type of the elements iterable the set to be processed
     * @param <U> type of the elements iterable the new set
     * @param set the set to be processed
     * @param fun the function to be applied on the elements from the set
     * @return new set which contains the results of applying the function <em>fun</em> on the elements of the old set
     */
    public static <T,U> Set<U> sfuncall(Set<T> set, Fun<T, U> fun){
        Set<U> us = new LinkedHashSet<U>();
        for (T t : set){
            us.add(fun.apply(t));
        }
        return us;
    }

    /**
     * Applies the given multivalued-function on each element from the given iterable and stores the results into a new iterable.
     * For each call of the multi-valued function, all elements which are returned by it are stored in the new list.
     * @param <T> type of the elements iterable the iterable to be processed
     * @param <U> type of the elements iterable the new iterable
     * @param iterable the iterable to be processed
     * @param fun the multi-valued function to be applied on the elements from the iterable
     * @return new iterable which contains the results of applying the function <em>fun</em> on the elements of the old iterable
     */
    public static <T,U> List<U> funcall(Iterable<T> iterable, MultiFun<T,U> fun){
        List<U> us = new ArrayList<U>();
        for (T t : iterable){
            us.addAll(fun.apply(t));
        }
        return us;
    }

    /**
     * Applies the given multivalued-function on each element from the given set and stores the results into a new set.
     * For each call of the multi-valued function, all elements which are returned by it are stored iterable the new set.
     * @param <T> type of the elements iterable the set to be processed
     * @param <U> type of the elements iterable the new set
     * @param set the set to be processed
     * @param fun the multi-valued function to be applied on the elements from the set
     * @return new set which contains the results of applying the function <em>fun</em> on the elements of the old set
     */
    public static <T,U> Set<U> funcall(Set<T> set, MultiFun<T,U> fun){
        Set<U> us = new LinkedHashSet<U>();
        for (T t : set){
            us.addAll(fun.apply(t));
        }
        return us;
    }

    /**
     * Applies the given function on each element from the given collection. It stores
     * the results iterable a Map iterable which keys are the original values and the associated values
     * are the transformed elements.
     * @param <T> type of the elements iterable the original collection
     * @param <U> type of the values iterable the resulting Map
     * @param coll the collection whose elements should be processed
     * @param fun the function to be applied
     * @return the Map iterable which keys are the original elements and values are the respective transformed
     * elements
     */
    public static <T,U> Map<T,U> mapcall(Collection<T> coll, Fun<T,U> fun){
        Map<T,U> us = new HashMap<T,U>();
        for (T t : coll){
            us.put(t, fun.apply(t));
        }
        return us;
    }
    
    /**
     * Applies the given function on each value-element from the given map and stores the results into a new map.
     * @param <T> type of the value-elements iterable the map to be processed
     * @param <U> type of the value-elements iterable the new map
     * @param map the map to be processed
     * @param fun the function to be applied on the elements from the map
     * @return new map which contains the results of applying the function <em>fun</em> on the elements of the old map - keys are preserved, only values are transformed
     */
    public static <V,T,U> Map<V,U> funcall(Map<V,T> map, Fun<T,U> fun){
        Map<V,U> us = new LinkedHashMap<V,U>();
        for (Map.Entry<V,T> entry : map.entrySet()){
            us.put(entry.getKey(), fun.apply(entry.getValue()));
        }
        return us;
    }


    public static <T,U> List<U> funcallAndRemoveNulls(List<T> set, MultiFun<T,U> fun){
        List<U> us = new ArrayList<U>();
        for (T t : set){
            for (U u : fun.apply(t)) {
                if (u != null) {
                    us.add(u);
                }
            }
        }
        return us;
    }

    public static <T,U> Set<U> funcallAndRemoveNulls(Set<T> set, MultiFun<T,U> fun){
        Set<U> us = new LinkedHashSet<U>();
        for (T t : set){
            for (U u : fun.apply(t)) {
                if (u != null) {
                    us.add(u);
                }
            }
        }
        return us;
    }

    public static <T,U> Set<U> funcallAndRemoveNulls(Set<T> set, Fun<T,U> fun){
        Set<U> us = new LinkedHashSet<U>();
        for (T t : set){
            U u = fun.apply(t);
            if (u != null) {
                us.add(u);
            }
        }
        return us;
    }

    /**
     * Applies the given multivalued-function on each element from the given iterable and stores the results iterable a new iterable.
     * For each call of the multi-valued function, all elements which are returned by it are stored iterable the new iterable.
     * @param <T> type of the elements iterable the iterable to be processed
     * @param <U> type of the elements iterable the new iterable
     * @param iterable the iterable to be processed
     * @param fun the multi-valued function to be applied on the elements from the iterable
     * @return new iterable which contains the results of applying the function <em>fun</em> on the elements of the old iterable
     */
    public static <T,U> List<U> funcallAndRemoveNulls(Iterable<T> iterable, MultiFun<T,U> fun){
        List<U> us = new ArrayList<U>();
        for (T t : iterable){
            for (U u : fun.apply(t)) {
                if (u != null) {
                    us.add(u);
                }
            }
        }
        return us;
    }

    /**
     * Applies the given function on each element from the given list and stores the results iterable a new list.
     * @param <T> type of the elements iterable the list to be processed
     * @param <U> type of the elements iterable the new list
     * @param list the list to be processed
     * @param fun the function to be applied on the elements from the list
     * @param params the parameters of the function <em>fun</em>
     * @return new list which contains the results of applying the function <em>fun</em> on the elements of the old list
     */
    public static <T,U> List<U> funcall(List<T> list, FunWithParams<T,U> fun, Object ...params){
        List<U> us = new ArrayList<U>();
        for (T t : list){
            us.add(fun.apply(t, params));
        }
        return us;
    }

    /**
     * Applies the given function on each element from the given set and stores the results iterable a new set.
     * @param <T> type of the elements iterable the set to be processed
     * @param <U> type of the elements iterable the new set
     * @param set the set to be processed
     * @param fun the function to be applied on the elements from the set
     * @param params the parameters of the function <em>fun</em>
     * @return new set which contains the results of applying the function <em>fun</em> on the elements of the old set
     */
    public static <T,U> Set<U> funcall(Set<T> set, FunWithParams<T,U> fun, Object ...params){
        Set<U> us = new LinkedHashSet<U>();
        for (T t : set){
            us.add(fun.apply(t, params));
        }
        return us;
    }

    /**
     * Applies the given function on each element from the given collection. It stores
     * the results iterable a Map iterable which keys are the original values and the associated values
     * are the transformed elements.
     * @param <T> type of the elements iterable the original collection
     * @param <U> type of the values iterable the resulting Map
     * @param list the collection whose elements should be processed
     * @param fun the function to be applied
     * @param params the parameters of the function <em>fun</em>
     * @return the Map iterable which keys are the original elements and values are the respective transformed
     * elements
     */
    public static <T,U> Map<T,U> mapcall(Collection<T> list, FunWithParams<T,U> fun, Object ...params){
        Map<T,U> us = new HashMap<T,U>();
        for (T t : list){
            us.put(t, fun.apply(t, params));
        }
        return us;
    }

    /**
     * Applies the given function on each value-element from the given map and stores the results iterable a new map.
     * @param <T> type of the value-elements iterable the map to be processed
     * @param <U> type of the value-elements iterable the new map
     * @param map the map to be processed
     * @param fun the function to be applied on the elements from the map
     * @param params the parameters of the function <em>fun</em>
     * @return new map which contains the results of applying the function <em>fun</em> on the elements of the old map - keys are preserved, only values are transformed
     */
    public static <V,T,U> Map<V,U> funcall(Map<V,T> map, FunWithParams<T,U> fun, Object ...params){
        Map<V,U> us = new LinkedHashMap<V,U>();
        for (Map.Entry<V,T> entry : map.entrySet()){
            us.put(entry.getKey(), fun.apply(entry.getValue(), params));
        }
        return us;
    }

    /**
     * Applies the given multivalued-function on each element from the given list and stores the results iterable a new list.
     * For each call of the multi-valued function, all elements which are returned by it are stored iterable the new list.
     * @param <T> type of the elements iterable the list to be processed
     * @param <U> type of the elements iterable the new list
     * @param list the list to be processed
     * @param fun the multi-valued function to be applied on the elements from the list
     * @param params the parameters of the function <em>fun</em>
     * @return new list which contains the results of applying the function <em>fun</em> on the elements of the old list
     */
    public static <T,U> List<U> funcall(List<T> list, MultiFunWithParams<T,U> fun, Object ...params){
        List<U> us = new ArrayList<U>();
        for (T t : list){
            us.addAll(fun.apply(t, params));
        }
        return us;
    }

    /**
     * Applies the given multivalued-function on each element from the given set and stores the results iterable a new set.
     * For each call of the multi-valued function, all elements which are returned by it are stored iterable the new set.
     * @param <T> type of the elements iterable the set to be processed
     * @param <U> type of the elements iterable the new set
     * @param set the set to be processed
     * @param fun the multi-valued function to be applied on the elements from the set
     * @param params the parameters of the function <em>fun</em>
     * @return new set which contains the results of applying the function <em>fun</em> on the elements of the old set
     */
    public static <T,U> Set<U> funcall(Set<T> set, MultiFunWithParams<T,U> fun, Object ...params){
        Set<U> us = new LinkedHashSet<U>();
        for (T t : set){
            us.addAll(fun.apply(t, params));
        }
        return us;
    }



    /**
     * Interface for functions which do not return any value.
     * @param <T> type of the elements processed by the function
     */
    public static interface VoidFun<T> {
        /**
         * Practically anything...
         * @param t the elemet to be processed
         */
        public void apply(T t);
    }
    
    /**
     * Interface for functions which return a value.
     * @param <T> type of the inputs
     * @param <U> type of the outputs
     */
    public static interface Fun<T,U> {
        /**
         * Practically anything...
         * @param t the element to be processed
         * @return the result
         */
        public U apply(T t);
    }

    /**
     * Identity function.
     * @param <T> type of the elements.
     */
    public static class IdentityFun<T> implements Fun<T,T> {
        
        public T apply(T t) {
            return t;
        }
    }
    
    /**
     * Interface for functions returning multiple values for one element.
     * @param <T> type of inputs
     * @param <U> type of outputs
     */
    public static interface MultiFun<T,U> {
        /**
         * Practically anything...
         * @param t the element to be processed
         * @return the result
         */
        public Collection<U> apply(T t);
    }

    /**
     * Interface for functions which get aditional parameters
     * @param <T> type of inputs 
     * @param <U> type of outputs
     */
    public static interface FunWithParams<T,U> {
        /**
         * Practically anything...
         * @param t the element to be processed
         * @param params the additional parameters
         * @return the result
         */
        public U apply(T t, Object ...params);
    }

    /**
     * Interface for functions which get additional parameters and return multiple values for one element.
     * @param <T> type of inputs
     * @param <U> type of outputs
     */
    public static interface MultiFunWithParams<T,U> {
        /**
         * Practically anything...
         * @param t the element to be processed
         * @param params the additional parameters
         * @return collection of results
         */
        public Collection<U> apply(T t, Object ...params);
    }

    /**
     * Interface for comparing which object is "better"
     * @param <T> type of the elements to be compared
     */
    public static interface MyComparator<T> {
        /**
         * Checks if a "is better than " b
         * @param a the element a
         * @param b the element b
         * @return true if <em>a</em> is better than <em>b</em>
         */
        public boolean isABetterThanB(T a, T b);
    }

    private static class Int {
        int value;
    }

    /**
     * Runs the specified tasks iterable parallel - creates as many threads as is the number
     * of tasks iterable the array <em>tasks</em> therefore this method is not recommended
     * when there are many tasks. In such cases, it is better to use other version of this method 
     * which restricts the number of threads running at once.
     * @param tasks the tasks to be performed
     */
    public static void runInParallel(Runnable ...tasks){
        List<Runnable> list = new ArrayList<Runnable>(tasks.length);
        list.addAll(Arrays.asList(tasks));
        runInParallel(list);
    }

    /**
       Runs the specified tasks iterable parallel - creates at most <em>processors</em> tasks.
     * @param tasks the tasks to be performed
     * @param processors number of threads
     */
    public static void runInParallel(final List<? extends Runnable> tasks, int processors){
        final Int counter = new Int();
        counter.value = tasks.size();
        for (int i = 0; i < processors; i++){
            new Thread(new Runnable(){
                public void run() {
                    while (true){
                        Runnable action = null;
                        synchronized (tasks){
                            if (tasks.size() > 0){
                                action = tasks.get(tasks.size()-1);
                                tasks.remove(tasks.size()-1);
                            }
                        }
                        if (action != null){
                            action.run();
                            synchronized (counter){
                                counter.value--;
                                counter.notify();
                            }
                        }
                        if (action == null){
                            break;
                        }
                    }
                }
            }).start();
        }
        synchronized (counter){
            while (counter.value > 0){
                try {
                    counter.wait();
                } catch (InterruptedException ie){
                    System.out.println(ie.getMessage());
                }
            }
        }
    }

    /**
     * Runs the specified tasks iterable parallel - creates as many threads as is the number
     * of tasks iterable the list <em>tasks</em> therefore this method is not recommended
     * when there are many tasks. In such cases, it is better to use other version of this method 
     * which restricts the number of threads running at once.
     * @param tasks the tasks to be performed
     */
    public static void runInParallel(List<? extends Runnable> tasks){
        runInParallel(tasks, tasks.size());
    }

    /**
     * Reads content of file to string.
     * @param r Reader of the file
     * @return content of the file as string
     * @throws java.io.IOException
     */
    public static String readFile(java.io.Reader r) throws java.io.IOException {
        java.io.BufferedReader b = new java.io.BufferedReader(r);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = b.readLine()) != null){
            sb.append(line).append("\n");
        }
        b.close();
        return sb.toString();
    }

    /**
     * Reads file as a list of strings.
     * @param r Reader of the file
     * @return list of strings = lines of the file
     * @throws java.io.IOException
     */
    public static List<String> readLines(java.io.Reader r) throws java.io.IOException {
        java.io.BufferedReader b = new java.io.BufferedReader(r);
        List<String> retVal = new ArrayList<String>();
        String line = null;
        while ((line = b.readLine()) != null){
            retVal.add(line);
        }
        b.close();
        return retVal;
    }

    /**
     * Writes the givn string to file.
     * @param s the string
     * @param writer the Writer for the file
     * @throws java.io.IOException
     */
    public static void saveFile(String s, java.io.Writer writer) throws java.io.IOException {
        java.io.PrintWriter pw = new java.io.PrintWriter(writer);
        pw.print(s);
        pw.close();
    }
    
    /**
     * Hash code for strings which uses all characters iterable the string.
     * @param str the string
     * @return the hash code
     */
    public static int stringHashCode(String str){
        int ret = 0;
        int cap = Integer.MAX_VALUE;
        char[] vector = str.toCharArray();
        for (int i = 0; i < vector.length; i++){
            ret = ((ret+1) * (vector[i] + 1)) % cap;
        }
        if (ret < 0){
            ret = (cap-ret) % cap;
        }
        return ret;
    }

    /**
     * Splits the given collection into specified number of collections
     * @param <R> type of elements iterable the collection
     * @param coll the collection
     * @param count number of collections into which the original collection should be splitted
     * @return list of collections
     */
    public static <R> List<Collection<R>> splitCollection(Collection<R> coll, int count){
        List<Collection<R>> retVal = new ArrayList<Collection<R>>();
        for (int i = 0; i < count; i++){
            retVal.add(new ArrayList<R>());
        }
        int i = 0;
        for (R r : coll){
            retVal.get(i % count).add(r);
            i++;
        }
        return retVal;
    }

    /**
     * Splits the given list into specified number of lists
     * @param <R> type of elements iterable the list
     * @param list the list
     * @param count number of list into which the original list should be splitted
     * @return list of lists
     */
    public static <R> List<List<R>> splitList(List<R> list, int count){
        List<List<R>> retVal = new ArrayList<List<R>>();
        for (int i = 0; i < count; i++){
            retVal.add(new ArrayList<R>());
        }
        int i = 0;
        for (R r : list){
            retVal.get(i % count).add(r);
            i++;
        }
        return retVal;
    }

    /**
     * Splits the given map into specified number of maps
     * @param <R> type of key-elements iterable the map
     * @param <S> type of value-elements iterable the map
     * @param map the map
     * @param count number of maps into which the original map should be splitted
     * @return list of maps
     */
    public static <R,S> List<Map<R,S>> splitMap(Map<R,S> map, int count){
        List<Map<R,S>> retVal = new ArrayList<Map<R,S>>();
        for (int i = 0; i < count; i++){
            retVal.add(new HashMap<R,S>());
        }
        int i = 0;
        for (Map.Entry<R,S> entry : map.entrySet()){
            retVal.get(i % count).put(entry.getKey(), entry.getValue());
            i++;
        }
        return retVal;
    }

    /**
     * Flattens the given list of lists
     * @param <T> elements iterable the lists
     * @param list the list of lists to be flattened
     * @return the flattened list
     */
    public static <T> List<T> flatten(List<List<T>> list){
        List<T> retVal = new ArrayList<T>();
        for (List<T> l : list){
            for (T t : l){
                retVal.add(t);
            }
        }
        return retVal;
    }

    /**
     * Flattens the given collection of collections
     * @param <T> elements iterable the collections
     * @param coll the collection of collections to be flattened
     * @return the flattened list
     */
    public static <T> Collection<T> flatten(Collection<? extends Collection<T>> coll){
        List<T> retVal = new ArrayList<T>();
        for (Collection<T> l : coll){
            for (T t : l){
                retVal.add(t);
            }
        }
        return retVal;
    }

    public static int countRecursively(Collection coll){
        int retVal = 0;
        for (Object o : coll){
            if (o instanceof Collection){
                retVal += countRecursively((Collection)o);
            } else {
                retVal++;
            }
        }
        return retVal;
    }

    /**
     * A simple function which prints an object and the new-line character and then returns it unchanged.
     * @param <T> the type of the object
     * @param t the object
     * @return the unchanged object
     */
    public static <T> T println(T t){
        System.out.println(t);
        return t;
    }

    public static <T> T println(T t, Object... objects){
        for (Object o : objects){
            System.out.print(o);
        }
        System.out.println(t);
        return t;
    }

    /**
     * A simple function which prints an object and then returns it unchanged.
     * @param <T> the type of the object
     * @param t the object
     * @return the unchanged object
     */
    public static <T> T print(T t){
        System.out.println(t);
        return t;
    }

    /**
     * Finds index of an object iterable an array.
     * @param array the array iterable which to find the object
     * @param o the object to be found
     * @return index of the object ot -1 if the object is not contained ion the array
     */
    public static int find(Object[] array, Object o){
        for (int i = 0; i < array.length; i++){
            if (array[i].equals(o)){
                return i;
            }
        }
        return -1;
    }

    /**
     * Reverses the given string.
     * @param str the string
     * @return the reversed string
     */
    public static String reverse(String str){
        char[] ch = str.toCharArray();
        VectorUtils.reverse(ch);
        return String.valueOf(ch);
    }

    public static <T> List<T> reverse(List<T> list){
        List<T> retVal = Sugar.listFromCollections(list);
        Collections.reverse(retVal);
        return retVal;
    }

    /**
     * Makes the first character of the given string upper-case.
     * @param s the string
     * @return the string with the first letter iterable upper-case
     */
    public static String firstCharacterToUpperCase(String s){
        char[] chars = s.toCharArray();
        if (chars.length > 0){
            chars[0] = Character.toUpperCase(chars[0]);
        }
        return new String(chars);
    }

    /**
     * Makes the first character of the given string lower-case.
     * @param s the string
     * @return the string with the first letter iterable lower-case
     */
    public static String firstCharacterToLowerCase(String s){
        char[] chars = s.toCharArray();
        if (chars.length > 0){
            chars[0] = Character.toLowerCase(chars[0]);
        }
        return new String(chars);
    }

    /**
     * Sets the random number generator used by Sugar
     * @param random the random number generator
     */
    public static void setRandomNumberGenerator(Random random){
        Sugar.random = random;
    }

    public static <R,S> Map<R,S> zipMap(List<R> keys, List<S> values){
        Map<R,S> retVal = new HashMap<R,S>();
        Iterator<R> it1;
        Iterator<S> it2;
        for (it1 = keys.iterator(), it2 = values.iterator(); it1.hasNext() & it2.hasNext(); retVal.put(it1.next(), it2.next()));
        return retVal;
    }

    public static <R,S> MultiMap<R,S> zipMultiMap(List<R> keys, List<S> values){
        MultiMap<R,S> retVal = new MultiMap<R,S>();
        Iterator<R> it1;
        Iterator<S> it2;
        for (it1 = keys.iterator(), it2 = values.iterator(); it1.hasNext() & it2.hasNext(); retVal.put(it1.next(), it2.next()));
        return retVal;
    }

    public static <T> Iterable<T> iterable(final Iterable<T>... colls){
        return new Iterable<T>(){
            @Override
            public Iterator<T> iterator() {

                return new Iterator<T>(){

                    private int collectionIndex = 0;

                    private Iterator<T> currentIterator;

                    @Override
                    public boolean hasNext() {
                        if (collectionIndex < colls.length){
                            if (currentIterator == null){
                                currentIterator = colls[collectionIndex].iterator();
                            }
                            if (currentIterator.hasNext()){
                                return true;
                            } else {
                                collectionIndex++;
                                while (collectionIndex < colls.length && !(currentIterator = colls[collectionIndex].iterator()).hasNext()){
                                    collectionIndex++;
                                }
                                return collectionIndex < colls.length && currentIterator.hasNext();
                            }
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public T next() {
                        return currentIterator.next();
                    }
                };
            }
        };
    }

    public static <T> Iterable<T> iterable(final Iterable<? extends Iterable<T>> colls){
        return new Iterable<T>(){

            private final Iterator<? extends Iterable<T>> iterator = colls.iterator();

            @Override
            public Iterator<T> iterator() {

                return new Iterator<T>(){

                    private Iterator<T> currentIterator;

                    @Override
                    public boolean hasNext() {
                        if (iterator.hasNext() || currentIterator.hasNext()){
                            if (currentIterator == null){
                                currentIterator = iterator.next().iterator();
                            }
                            if (currentIterator.hasNext()){
                                return true;
                            } else {
                                while (iterator.hasNext() && !(currentIterator = iterator.next().iterator()).hasNext());
                                return currentIterator.hasNext();
                            }
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public T next() {
                        return currentIterator.next();
                    }
                };
            }
        };
    }

    public static <T> void replace(Set<T> set, T oldItem, T newItem){
        set.remove(oldItem);
        set.add(newItem);
    }

    public static <T> List<T> add(List<T> list, T t){
        list.add(t);
        return list;
    }

    private static final double LOG2 = Math.log(2.0);

    /*
     * This is copied from StackExchange answer by user leonbloy
     */
    public static double logBigInteger(BigInteger val) {
        int blex = val.bitLength() - 1022;
        if (blex > 0)
            val = val.shiftRight(blex);
        double res = Math.log(val.doubleValue());
        return blex > 0 ? res + blex * LOG2 : res;
    }

    public static double log2(BigInteger val){
        return logBigInteger(val)/LOG2;
    }

    public static double square(double x){
        return x*x;
    }

    public static String freshIdentifier(String prefix, Set<String> existingIdentifiers, int index){
        String retVal = null;
        do {
            retVal = prefix+(index++);
        } while (existingIdentifiers.contains(retVal));
        return retVal;
    }

    public static int indicator(boolean b){
        return b ? 1 : 0;
    }

    //crudely approximates log(exp(a)+exp(b))
    public static double approxLogSumExp(double a, double b){
        return 1 + 0.5*a+0.5*b + 0.5 * (0.25*square(a) - 0.5*a*b + 0.25*square(b));
    }

    //crudely approximates log(exp(a)+exp(b)) around [1,0]
    //not properly thought through, it probably needs better theoretically justified approx
    public static double approximateLogExpMinusExp(double x, double y){
        final double e = Math.E;
        if (y == Double.NEGATIVE_INFINITY){
            return x;
        }
        else if (x+y < 200.0){
            return Math.log(Math.exp(x) - Math.exp(y));
        } else if (x-y > 20){
            return x;
        }
        double a = 10;
        double b = 5;
        double expA = Math.exp(a);
        double expB = Math.exp(b);
        return Math.log(expA-expB) + expA/(expA-expB)*(x-a) + expB/(expB-expA)*(y-b);
    }

    public static void main(String[] args){
        for (int i = 0; i < 10; i++){
            double x = Math.random();
            double y = x - x*Math.random();
            x = 400*x+1;
            y = 400*y+1;
            double exact = Math.log(Math.exp(x)-Math.exp(y));
            double approx = approximateLogExpMinusExp(x,y);
            double difference = exact-approx;
            System.out.println("["+x+","+y+"] ~ "+exact+" : "+approx+", "+difference);
        }

    }

}
