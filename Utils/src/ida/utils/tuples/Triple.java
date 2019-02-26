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
 * Triple.java
 *
 * Created on 6. prosinec 2006, 19:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils.tuples;

/**
 * Class representing 3-tuples of objects.
 * @param <R> type of the first object
 * @param <S> type of the second object
 * @param <T> type of the third object
 * 
 * @author Ondra
 */
public class Triple<R, S, T> {
    
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
    
    /** Creates a new instance of Pair */
    public Triple() {
    }
    
    /**
     * Creates a new instance of class Triple with the given objects.
     * @param r the first object
     * @param s the second object
     * @param t the third object
     */
    public Triple(R r, S s, T t){
        this.r = r;
        this.s = s;
        this.t = t;
    }

    /**
     * Sets the objects iterable the Triple.
     * @param r the first object
     * @param s the second object
     * @param t the third object
     */
    public void set(R r, S s, T t){
        this.r = r;
        this.s = s;
        this.t = t;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Triple){
            Triple t = (Triple)o;
            return ((t.r == null && this.r == null) || (t.r != null && this.r != null && t.r.equals(this.r))) &&
                    ((t.s == null && this.s == null) || (t.s != null && this.s != null && t.s.equals(this.s))) &&
                    ((t.t == null && this.t == null) || (t.t != null && this.t != null && t.t.equals(this.t)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (this.r != null){
            hashCode += this.r.hashCode();
        }
        if (this.s != null){
            hashCode += this.s.hashCode();
        }
        if (this.t != null){
            hashCode += this.t.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public String toString(){
        return "["+r+", "+s+", "+t+"]";
    }
}

