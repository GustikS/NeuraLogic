/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.aggregables;

import cz.cvut.fel.ida.logic.features.treeliker.Aggregable;
import cz.cvut.fel.ida.logic.features.treeliker.Aggregables;

import java.lang.ref.SoftReference;
import java.util.*;

/**
 * An efficient representation of Aggregables objects which contain just copies of one particular Aggregable.
 * This class uses caching so it is quite memory-efficient.
 * 
 * @param <T>  type of the Aggregable objects
 * @author Ondra
 */
public class AggregablesFilledWithOneElement<T extends Aggregable> extends Aggregables<T> {
    
    private int size = 0;
    
    private T theOne;
    
    private static SoftReference<HashMap<Integer,SoftReference<AggregablesFilledWithOneElement>>> cache = new SoftReference<HashMap<Integer,SoftReference<AggregablesFilledWithOneElement>>>(new HashMap<Integer,SoftReference<AggregablesFilledWithOneElement>>());
    
    private final static Object lock = new Object();
    
    /**
     * Creates a new instance of class AggregablesFilledWithOneElement
     * @param t the one element
     * @param size the size of the Aggregables object (i.e. the number of times there is the one element)
     */
    protected AggregablesFilledWithOneElement(T t, int size){
        super(-1);
        this.theOne = t;
        this.size = size;
    }
    
    /**
     * Returns an instance of class AggregablesFilledWithOneElement (it either creates a new instance
     * or gets one from cache)
     * @param r the one element with which it should be filled
     * @param size the number of times the one element should be contained in it
     * @return an instance of class AggregablesFilledWithOneElement (it either creates a new instance
     * or gets one from cache)
     */
    public static AggregablesFilledWithOneElement construct(Aggregable r, int size){
        AggregablesFilledWithOneElement retVal = null;
        HashMap<Integer,SoftReference<AggregablesFilledWithOneElement>> map = null;
        SoftReference<AggregablesFilledWithOneElement> softRef = null;
        synchronized (lock){
            if ((map = cache.get()) != null && (softRef = map.get(size)) != null && (retVal = softRef.get()) != null){
                return (AggregablesFilledWithOneElement)retVal;
            }
            retVal = new AggregablesFilledWithOneElement(r, size);
            if (map == null){
                map = new HashMap<Integer,SoftReference<AggregablesFilledWithOneElement>>();
                cache = new SoftReference<HashMap<Integer,SoftReference<AggregablesFilledWithOneElement>>>(map);
            }
            map.put(size, new SoftReference<AggregablesFilledWithOneElement>(retVal));
        }
        return retVal;
    }
    
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size != 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof VoidAggregable){
            return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return this.listIterator();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> R[] toArray(R[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public T get(int index) {
        if (index < this.size){
            return theOne;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        if (index >= this.size){
            throw new ArrayIndexOutOfBoundsException();
        }
        this.size--;
        return theOne;
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        final int j = i;
        return new ListIterator<T>(){
            
            int index = j;
            
            public boolean hasNext() {
                return index < size;
            }

            public T next() {
                index++;
                return theOne;
            }

            public boolean hasPrevious() {
                return index > 0;
            }

            public T previous() {
                if (index <= 0){
                    throw new ArrayIndexOutOfBoundsException();
                }
                return theOne;
            }

            public int nextIndex() {
                return index;
            }

            public int previousIndex() {
                return index-1;
            }

            public void remove() {
                
                
            }

            public void set(T e) {
                throw new UnsupportedOperationException();
            }

            public void add(T e) {
                size++;
            }
            
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof AggregablesFilledWithOneElement){
            AggregablesFilledWithOneElement av = (AggregablesFilledWithOneElement)o;
//            if (av.size == this.size && av.theOne.equals(this.theOne) && o != this){
//                System.out.println("equal: "+this+" ==== "+o);
//            }
            return av.size == this.size && av.theOne.equals(this.theOne);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode(){
        return this.size;
    }
    
}
