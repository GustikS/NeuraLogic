/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

import java.util.*;

/**
 * Class for storing lists of Aggregable elements. It is preferable over plain implementations
 * of Lists in Java because it caches results of some operations (for example the hashCode etc)
 * which are often needed in operations with lists of aggregables. Moreover, there are some
 * very useful and fast sub-classes of this class for special types of aggregables (e.g. VoidAggregables).
 * 
 * @param <T> 
 * @author Ondra
 */
public class Aggregables<T extends Aggregable> implements List<T> {

    private int hashCode = -1;
    
    private List<T> list;
    
    /**
     * Creates a new instance of class Aggregables with capacity set to size
     * @param size the initial capacity
     */
    public Aggregables(int size){
        if (size > 0){
            this.list = new ArrayList<T>(size);
        }
    }
    
    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    public Iterator<T> iterator() {
        return this.list.listIterator();
    }

    public Object[] toArray() {
        return this.list.toArray();
    }

    public <R> R[] toArray(R[] a) {
        return this.list.toArray(a);
    }

    public boolean add(T e) {
        this.hashCode = -1;
        return this.list.add(e);
    }

    public boolean remove(Object o) {
        this.hashCode = -1;
        return this.list.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    public boolean addAll(Collection<? extends T> c) {
        this.hashCode = -1;
        return this.list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        this.hashCode = -1;
        return this.list.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        this.hashCode = -1;
        return this.list.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        this.hashCode = -1;
        return this.list.retainAll(c);
    }

    public void clear() {
        this.hashCode = -1;
    }

    public T get(int index) {
        return this.list.get(index);
    }

    public T set(int index, T element) {
        this.hashCode = -1;
        return this.list.set(index, element);
    }

    public void add(int index, T element) {
        this.hashCode = -1;
        this.list.add(index, element);
    }

    public T remove(int index) {
        this.hashCode = -1;
        return this.list.remove(index);
    }

    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return this.list.listIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return this.list.listIterator(index);
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }
    
    @Override
    public String toString(){
        return this.list.toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Aggregables){
            Aggregables av = (Aggregables)o;
            if (av.hashCode() != this.hashCode()){
                return false;
            }
            return this.list.equals(av.list);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode(){
        if (this.hashCode == -1){
            this.hashCode = this.list.hashCode();
        }
        return this.hashCode;
    }
}
