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
 * Quintuple.java
 *
 * Created on 13. leden 2008, 11:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package ida.utils.tuples;

/**
 * Class representing 5-tuples of objects.
 * @param <R> type of the first object
 * @param <S> type of the second object
 * @param <T> type of the third object
 * @param <U> type of the fourth object
 * @param <V> type of the fifth object
 * 
 * @author Ondra
 */
public class Quintuple<R,S,T,U,V> {
    
    /**
     * The first object
     */
    public R r;
    
    /**
     * The second object
     */
    public S s;
    
    /**
     * The third object
     */
    public T t;
    
    /**
     * The fourth object
     */
    public U u;
    
    /**
     * The fifth object
     */
    public V v;
    
    /** Creates a new instance of Quintuple */
    public Quintuple() {
    }
    
    /**
     * Creates a new instance of class Quintuple with the given objects.
     * @param r the first object
     * @param s the second object
     * @param t the third object
     * @param u the fourth object
     * @param v the fifth object
     */
    public Quintuple(R r, S s, T t, U u, V v){
        this.r = r;
        this.s = s;
        this.t = t;
        this.u = u;
        this.v = v;
    }

    /**
     * Sets the objects iterable the Quintuple.
     * @param r the first object
     * @param s the second object
     * @param t the third object
     * @param u the fourth object
     * @param v the fifth object
     */
    public void set(R r, S s, T t, U u, V v){
        this.r = r;
        this.s = s;
        this.t = t;
        this.u = u;
        this.v = v;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Quintuple){
            Quintuple q = (Quintuple)o;
            return q.r.equals(this.r) && q.s.equals(this.s) && q.t.equals(this.t) && q.u.equals(this.u) && q.v.equals(this.v);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.r.hashCode()+this.s.hashCode()+this.t.hashCode()+this.u.hashCode()+this.v.hashCode();
    }
    
    @Override
    public String toString(){
        return "["+r+", "+s+", "+t+", "+u+", "+v+"]";
    }
}
