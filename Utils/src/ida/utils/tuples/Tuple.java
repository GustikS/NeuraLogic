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
 * NTuple.java
 *
 * Created on 11. duben 2007, 12:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils.tuples;

import java.util.*;
/**
 * Class representing n-tuples of objects. It may be preferrable over Lists because
 * it has fixed size and it caches some information (e.g. hashCodes).
 * 
 * @param <T> 
 * @author Ondra
 */
public class Tuple<T> {
    
    private int id;
    
    private static int lastID = 0;
    
    private int extraInt = 0;
    
    private T[] values;
    
    private int hashCode;
    
    private boolean hashCodeComputed = false;
    
    /**
     * Creates a new instance of class Tuple for n-tuples with <em>n</em> given as argument.
     * @param n
     */
    public Tuple(int n) {
        this.values = (T[])new Object[n];
        this.id = lastID++;
    }
    
    /**
     * Creates a new instance of class Tuple containing the given set of elements.
     * @param set the set of elements with which the tuple should be filled.
     */
    public Tuple(Set<T> set){
        this(set.size());
        int index = 0;
        for (T t : set){
            this.set(t, index);
            index++;
        }
    }

    /**
     * Creates a new instance of class Tuple containing the elements from the given array.
     * @param ts the array of elements with which the tuple should be filled.
     */
    public Tuple(T ...ts){
        this(ts.length);
        int index = 0;
        for (T t : ts){
            this.set(t, index);
            index++;
        }
    }

    /**
     * Creates a new instance of class Tuple containing the elements from the given list.
     * @param list the list of elements with which the tuple should be filled.
     */
    public Tuple(List<T> list){
        this(list.size());
        this.setAll(list);
    }
    
    /**
     * Sets the value at the given index.
     * @param value the new value
     * @param index the index
     */
    public void set(T value, int index){
        this.hashCodeComputed = false;
        this.values[index] = value;
    }
    
    /**
     * Gets the value of the element at the given index.
     * @param index the index
     * @return the element at the given index
     */
    public T get(int index){
        return (T)this.values[index];
    }
    
    /**
     * Sets the values iterable the tuple.
     * @param list the list with the new values
     */
    public void setAll(java.util.List<T> list){
        this.hashCodeComputed = false;
        for (int i = 0; i < list.size(); i++){
            this.values[i] = list.get(i);
        }
    }
    
    /**
     * 
     * @return size of the tuple - number of elements iterable it.
     */
    public int size(){
        return this.values.length;
    }
    
    /**
     * Converts the tuple to a list containing the same elements.
     * @return the list containing the same elements as the tuple.
     */
    public List<T> toList(){
        List<T> retVal = new ArrayList<T>();
        for (int i = 0; i < this.values.length; i++){
            retVal.add(this.values[i]);
        }
        return retVal;
    }
    
    /**
     * Converts the tuple to a set containing the same elements.
     * @return the set containing the same elements as the tuple.
     */
    public Set<T> toSet(){
        Set<T> set = new HashSet<T>();
        for (Object t : this.values){
            set.add((T)t);
        }
        return set;
    }
    
    @Override
    public int hashCode(){
        if (hashCodeComputed)
            return hashCode;
        else {
            hashCode = 1;
            for (int i = 0; i < values.length; i++){
                hashCode = ((hashCode+1)*(values[i].hashCode()+i*i*i+1)) % (Integer.MAX_VALUE/128);
            }
            hashCodeComputed = true;
            return hashCode;
        }
    }
    
    /**
     * Creates a new instance of class Tuple<Integer> from the given array of type int[].
     * @param array the array
     * @return the new instance of class Tuple<Integer>
     */
    public static Tuple<Integer> intTuple(int[] array){
        Tuple<Integer> t = new Tuple<Integer>(array.length);
        t.values = new Integer[array.length];
        for (int i = 0; i < array.length; i++){
            t.values[i] = (Integer)(array[i]);
        }
        return t;
    }
    
    /**
     * Creates a new instance of class Tuple<Double> from the given array of type double[].
     * @param array the array
     * @return the new instance of class Tuple<Double>
     */
    public static Tuple<Double> doubleTuple(double[] array){
        Tuple<Double> t = new Tuple<Double>(array.length);
        t.values = new Double[array.length];
        for (int i = 0; i < array.length; i++){
            t.values[i] = new Double(array[i]);
        }
        return t;
    }
    
    /**
     * Creates a new instance of class Tuple<Boolean> from the given array of type boolean[].
     * @param array the array
     * @return the new instance of class Tuple<Boolean>
     */
    public static Tuple<Boolean> booleanTuple(boolean[] array){
        Tuple<Boolean> t = new Tuple<Boolean>(array.length);
        for (int i = 0; i < array.length; i++){
            t.values[i] = array[i];
        }
        return t;
    }
    
    /**
     * Parses a tuple from its string representation. For example:
     * Tuple<String> t = Tuple.parse("[a,b,c,d]"); creates a 4-tuple with elements
     * "a", "b", "c" and "d".
     * @param str the string representation of the tuple
     * @return the parsed tuple
     */
    public static Tuple<String> parse(String str){
        str = str.trim().substring(1, str.length()-1);
        String[] splitted = str.split(",");
        for (int i = 0; i < splitted.length; i++){
            splitted[i] = splitted[i].trim();
        }
        return new Tuple<String>(splitted);
    }
    
    /**
     * Parses a tuple of integers from its string representation. For example:
     * Tuple<Integer> t = Tuple.parseIntegerTuple("[1,2,3,4]"); creates a 4-tuple with elements
     * 1, 2, 3 and 4.
     * @param str the string representation of the tuple
     * @return the parsed tuple of integers
     */
    public static Tuple<Integer> parseIntegerTuple(String str){
        Tuple strTuple = parse(str);
        int[] values = new int[strTuple.size()];
        for (int i = 0; i < strTuple.size(); i++){
            values[i] = Integer.parseInt(strTuple.values[i].toString());
        }
        return Tuple.intTuple(values);
    }
    
    /**
     * Parses a tuple of numbers of type double from its string representation. For example:
     * Tuple<Double> t = Tuple.parseDoubleTuple("[1.1,2.2,3.3,4.4]"); creates a 4-tuple with elements
     * 1.1, 2.2, 3.3 and 4.4.
     * @param str the string representation of the tuple
     * @return the parsed tuple of doubles
     */
    public static Tuple<Double> parseDoubleTuple(String str){
        Tuple<String> strTuple = parse(str);
        double[] values = new double[strTuple.size()];
        for (int i = 0; i < strTuple.values.length; i++){
            values[i] = Integer.parseInt(strTuple.values[i]);
        }
        return Tuple.doubleTuple(values);
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Tuple){
            Tuple nt = (Tuple)o;
            if (nt.values.length != this.values.length || nt.hashCode() != this.hashCode())
                return false;
            else {
                for (int i = 0; i < this.values.length; i++){
                    if (!(this.values[i] == null && nt.values[i] == null) && !this.values[i].equals(nt.values[i]))
                        return false;
                }
                return true;
            }
        }
        else
            return false;
    }
    
    /**
     * Creates a new tuple by appending the given element to the given tuple.
     * @param <T> type of the objects iterable the tuple
     * @param a the tuple
     * @param t the appended element
     * @return the new tuple
     */
    public static <T> Tuple<T> append(Tuple<T> a, T t){
        Tuple<T> c = new Tuple<T>(a.size()+1);
        System.arraycopy(a.values, 0, c.values, 0, a.values.length);
        c.values[c.values.length-1] = t;
        return c;
    }
    
    /**
     * Concatenates the given tuples.
     * @param <T> typ of the elements iterable the tuple.
     * @param a the first tuple
     * @param b the second tuple
     * @return the tuple created by concatenating the tuples <em>a</em> and <em>b</em>.
     */
    public static <T> Tuple<T> merge(Tuple<T> a, Tuple<T> b){
        Tuple<T> c = new Tuple<T>(a.size()+b.size());
        System.arraycopy(a.values, 0, c.values, 0, a.values.length);
        System.arraycopy(b.values, 0, c.values, a.size(), b.values.length);
        return c;
    }
    
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < values.length; i++){
            if (values[i] == null)
                sb.append("null");
            else
                sb.append(values[i].toString());
            if (i < values.length-1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Returns the unique identifier of the tuple.
     * @return the unique identifier
     */
    public int id(){
        return this.id;
    }
    
    /**
     * Every tuple can have an additional piece of information iterable the form of an integer.
     * This method returns this extra piece of information.
     * @return the "extra int"
     */
    public int getExtraInt(){
        return this.extraInt;
    }
    
    /**
     * Every tuple can have an additional piece of information iterable the form of an integer.
     * This method sets this extra piece of information.
     * @param extras the additional int to be stored with the tuple
     */
    public void setExtraInt(int extras){
        this.extraInt = extras;
    }
}
