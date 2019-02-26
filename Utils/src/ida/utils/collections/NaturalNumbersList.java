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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Class for efficiently (and lazily) representing lists of consecutive integers.
 * The instances of this class are immutable - calling some of the methods modifying it
 * will throw an exception.
 * 
 * @author ondra
 */
public class NaturalNumbersList implements List<Integer> {

    private int start, end;
    
    /**
     * Creates a new instance of class NaturalNumbersList "filled"with numbers
     * start, start+1, ..., end-1.
     * @param start the first number iterable the list
     * @param end the upper-bound (exclusive)
     */
    public NaturalNumbersList(int start, int end){
        this.start = start;
        this.end = end;
    }
    
    @Override
    public int size() {
        return end-start;
    }

    @Override
    public boolean isEmpty() {
        return start >= end;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Integer){
            Integer i = (Integer)o;
            return i >= start && i < end;
        }
        return false;
    }

    @Override
    public Iterator<Integer> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        if (this.isEmpty()){
            return new Object[0];
        }
        Object[] retVal = new Object[end-start];
        int index = 0;
        for (int i = start; i < end; i++){
            retVal[index] = i;
            index++;
        }
        return retVal;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int index = 0;
        for (int i = start; i < end; i++){
            a[index] = (T)new Integer(i);
            index++;
        }
        return a;
    }

    @Override
    public boolean add(Integer e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c){
            if (!contains(o)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer get(int index) {
        if (index < 0 || index >= end-start){
            throw new IndexOutOfBoundsException();
        }
        return index+start;
    }

    @Override
    public Integer set(int index, Integer element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(int index, Integer element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer remove(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Integer){
            Integer i = (Integer)o;
            if (contains(i)){
                return i-start;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<Integer> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        return new Iter(index);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        if (fromIndex >= start && toIndex <= end){
            return new NaturalNumbersList(fromIndex, toIndex);
        }
        throw new IndexOutOfBoundsException();
    }
    
    private class Iter implements ListIterator<Integer> {

        private int i;
        
        public Iter(){
            i = start;
        }
        
        public Iter(int index){
            if (index >= size()){
                throw new IndexOutOfBoundsException();
            }
            i = start+index;
        }
        
        @Override
        public boolean hasNext() {
            return i < end;
        }

        @Override
        public Integer next() {
            if (!hasNext()){
                throw new IndexOutOfBoundsException();
            }
            return i++;
        }

        @Override
        public boolean hasPrevious() {
            return i > start;
        }

        @Override
        public Integer previous() {
            if (!hasPrevious()){
                throw new IndexOutOfBoundsException();
            }
            return --i;
        }

        @Override
        public int nextIndex() {
            return i-start;
        }

        @Override
        public int previousIndex() {
            return i-start-1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void set(Integer e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(Integer e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
