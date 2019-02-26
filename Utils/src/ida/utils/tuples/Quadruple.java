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
 * Quadruple.java
 *
 * Created on 30. listopad 2006, 20:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils.tuples;


/**
 * Class representing 4-tuples of objects.
 * @param <R> type of the first object
 * @param <S> type of the second object
 * @param <T> type of the third object
 * @param <U> type of the fourth object
 * 
 * @author Ondra
 */
public class Quadruple<R, S, T, U> {
    
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
    
    /** Creates a new instance of Pair */
    public Quadruple() {
    }
    
    /**
     * Creates a new instance of class Quadruple with the given objects.
     * @param r the first object
     * @param s the second object
     * @param t the third object
     * @param u the fourth object
     */
    public Quadruple(R r, S s, T t, U u){
        this.r = r;
        this.s = s;
        this.t = t;
        this.u = u;
    }

    /**
     * Sets the objects iterable the Quadruple.
     * @param r the first object
     * @param s the second object
     * @param t the third object
     * @param u the fourth object
     */
    public void set(R r, S s, T t, U u){
        this.r = r;
        this.s = s;
        this.t = t;
        this.u = u;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Quadruple){
            Quadruple q = (Quadruple)o;
            return q.r.equals(this.r) && q.s.equals(this.s) && q.t.equals(this.t) && q.u.equals(this.u);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.r.hashCode()+this.s.hashCode()+this.t.hashCode()+this.u.hashCode();
    }
    
    @Override
    public String toString(){
        return "["+r+", "+s+", "+t+", "+u+"]";
    }
}
