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

import java.util.Arrays;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import ida.utils.Sugar;

/**
 * Class for representing fixed-size vectors of instances of class BigInteger.
 * 
 * @author Ondra
 */
public class BigIntegerVector {

    private BigInteger[] values;

    /**
     * Creates a new empty instance of class BigIntegerVector
     */
    public BigIntegerVector(){
        this.values = new BigInteger[0];
    }

    /**
     * Creates a new instance of class BigIntegerVector
     * @param array the array of BigIntegers to be stored iterable this BigIntegerVector
     */
    public BigIntegerVector(BigInteger[] array){
        this.values = array;
    }

    /**
     * Computes sum of elements iterable this BigIntegerVector.
     * @return the sum of elements iterable this BigIntegerVector
     */
    public BigInteger sum(){
        BigInteger retVal = BigInteger.ZERO;
        for (BigInteger bi : values){
            retVal = retVal.add(bi);
        }
        return retVal;
    }

    /**
     * Computes product of elements iterable this BigIntegerVector.
     * @return the product of elements iterable this BigIntegerVector
     */
    public BigInteger product(){
        BigInteger retVal = BigInteger.ONE;
        for (BigInteger bi : values){
            retVal = retVal.multiply(bi);
        }
        return retVal;
    }

    /**
     * Computes the sum of two BigIntegerVectors. The result is a BigIntegerVector with
     * the same size as <em>a</em> and <em>b</em>
     * @param a the first BigIntegerVector
     * @param b the second BigIntegerVector
     * @return the sum of two BigIntegerVectors
     */
    public static BigIntegerVector plus(BigIntegerVector a, BigIntegerVector b){
        BigInteger[] retVal = new BigInteger[a.values.length];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = a.values[i].add(b.values[i]);
        }
        return new BigIntegerVector(retVal);
    }

    /**
     * Creates a new BigIntegerVector by adding the number <em>b</em> to every element
     * of the given BigIntegerVector <em>a</em>.
     * @param a the BigIntegerVector
     * @param b the number
     * @return the sum of the given BigIntegerGVector and the given number
     */
    public static BigIntegerVector add(BigIntegerVector a, BigInteger b){
        BigInteger[] retVal = new BigInteger[a.values.length];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = a.values[i].add(b);
        }
        return new BigIntegerVector(retVal);
    }

    /**
     * Creates a new BigIntegerVector by subtracting the number <em>b</em> from every element
     * of the given BigIntegerVector <em>a</em>.
     * @param a the BigIntegerVector
     * @param b the number
     * @return the difference of the given BigIntegerGVector and the given number
     */
    public static BigIntegerVector subtract(BigIntegerVector a, BigInteger b){
        BigInteger[] retVal = new BigInteger[a.values.length];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = a.values[i].subtract(b);
        }
        return new BigIntegerVector(retVal);
    }

    /**
     * Creates a new BigIntegerVector by multiplying the number <em>b</em> with every element
     * of the given BigIntegerVector <em>a</em>.
     * @param a the BigIntegerVector
     * @param b the number
     * @return the product of the given BigIntegerGVector and the given number
     */
    public static BigIntegerVector multiply(BigIntegerVector a, BigInteger b){
        BigInteger[] retVal = new BigInteger[a.values.length];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = a.values[i].multiply(b);
        }
        return new BigIntegerVector(retVal);
    }

    /**
     * Computes the product of two BigIntegerVectors. The result is a BigIntegerVector with
     * the same size as <em>a</em> and <em>b</em> with elements being products of the respective numbers iterable
     * the BigIntegerVectors.
     * @param a the first BigIntegerVector
     * @param b the second BigIntegerVector
     * @return the product of two BigIntegerVectors
     */
    public static BigIntegerVector times(BigIntegerVector a, BigIntegerVector b){
        BigInteger[] retVal = new BigInteger[a.values.length];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = a.values[i].multiply(b.values[i]);
        }
        return new BigIntegerVector(retVal);
    }

    /**
     * Computes sum of the given BigIntegerVectors.
     * @param vectors the BigIntegerVectors (they should all have the same sizes)
     * @return the sum of the BigIntegerVector
     */
    public static BigIntegerVector sum(Collection<BigIntegerVector> vectors){
        BigIntegerVector retVal = null;
        for (BigIntegerVector vector : vectors){
            if (retVal == null){
                retVal = vector;
            } else {
                retVal = plus(retVal, vector);
            }
        }
        return retVal;
    }

    /**
     * Computes product of the given BigIntegerVectors.
     * @param vectors the BigIntegerVectors (they should all have the same sizes)
     * @return the product of the BigIntegerVector
     */
    public static BigIntegerVector product(Collection<BigIntegerVector> vectors){
        BigIntegerVector retVal = null;
        for (BigIntegerVector vector : vectors){
            if (retVal == null){
                retVal = vector;
            } else {
                retVal = times(retVal, vector);
            }
        }
        return retVal;
    }

    /**
     * Creates a new BigIntegerVector iterable which all occurrences of the given BigInteger are removed.
     * @param vector the original BigIntegerVector
     * @param toBeRemoved the BigInteger to be removed
     * @return the new BigIntegerVector iterable which all occurrences of the given BigInteger are removed
     */
    public static BigIntegerVector remove(BigIntegerVector vector, BigInteger toBeRemoved){
        return new BigIntegerVector(remove(vector.values, toBeRemoved));
    }

    private static BigInteger[] remove(BigInteger[] vector, BigInteger toBeRemoved){
        BigInteger[] newVector = new BigInteger[vector.length-Sugar.countOccurences(toBeRemoved, vector)];
        int index = 0;
        for (BigInteger bi : vector){
            if (!bi.equals(toBeRemoved)){
                newVector[index] = bi;
                index++;
            }
        }
        return newVector;
    }

    /**
     * 
     * @return the BigIntegers contained iterable this object
     */
    public BigInteger[] values(){
        return values;
    }
    
    @Override
    public String toString(){
        return Sugar.objectArrayToString(values);
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof BigIntegerVector){
            BigIntegerVector ia = (BigIntegerVector)o;
            if (Arrays.equals(ia.values, this.values)){
                return true;
            }
        }
        return false;
    }
}
