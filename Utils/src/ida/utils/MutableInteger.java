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

package ida.utils;

/**
 * Class for simple mutable integer (unlike java.lang.Integer which is immutable).
 * @author admin
 */
public class MutableInteger {

    private int value;
    
    /**
     * Creates a new instance of class MutableInteger
     * @param value value of the integer
     */
    public MutableInteger(int value){
        this.value = value;
    }

    /**
     * 
     * @return the value opf the integer
     */
    public int value(){
        return value;
    }

    /**
     * Sets the value of the integer.
     * @param value the value of the integer
     */
    public void set(int value){
        this.value = value;
    }

    @Override
    public int hashCode(){
        return value;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof MutableInteger){
            return ((MutableInteger)o).value == this.value;
        }
        return false;
    }

    @Override
    public String toString(){
        return String.valueOf(value);
    }

    /**
     * Increments the integer and returns the new value.
     * @return the new value after incrementation.
     */
    public int incrementPre(){
        return ++this.value;
    }

    /**
     * Decrements the integer and returns the new value.
     * @return the new value after decrementation.
     */
    public int decrementPre(){
        return --this.value;
    }

    /**
     * Increments the integer and returns the old value.
     * @return the new value before incrementation.
     */
    public int incrementPost(){
        return this.value++;
    }

    /**
     * Decrements the integer and returns the old value.
     * @return the new value before decrementation.
     */
    public int decrementPost(){
        return this.value--;
    }

    /**
     * Increments the value of the integer.
     */
    public void increment(){
        this.value++;
    }

    /**
     * Decrements the value of the integer.
     */
    public void decrement(){
        this.value--;
    }
}
