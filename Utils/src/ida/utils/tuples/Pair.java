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
 * Pair.java
 *
 * Created on 18. listopad 2006, 17:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils.tuples;

/**
 * Class representing pairs of oebjects.
 * @param <R> type of the first object
 * @param <S> type of the second object
 * @author Ondra
 */
public class Pair<R, S> {
    
    /**
     * The first object
     */
    public R r;
    
    /**
     * The second object
     */
    public S s;
    
    /** Creates a new instance of Pair */
    public Pair() {
    }
    
    /**
     * Creates a new instance of class Pair with the given content.
     * @param r the first object
     * @param s the second object
     */
    public Pair(R r, S s){
        this.r = r;
        this.s = s;
    }

    /**
     * Sets the objects iterable the Pair.
     * @param r the first object
     * @param s the second object
     */
    public void set(R r, S s){
        this.r = r;
        this.s = s;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Pair){
            Pair p = (Pair)o;
            return (p.r == this.r || (p.r != null && this.r != null && p.r.equals(this.r))) &&
                    (p.s == this.s || (p.s != null && this.s != null && p.s.equals(this.s)));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int a = 0, b = 0;
        if (this.r != null){
            a = this.r.hashCode();
        }
        if (this.s != null){
            b = this.s.hashCode();
        }
        return (1+a)*(13+b);
    }
    
    @Override
    public String toString(){
        return "["+r+", "+s+"]";
    }

}
