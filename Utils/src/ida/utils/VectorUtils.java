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
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils;

import ida.utils.collections.NaturalNumbersList;
import ida.utils.tuples.Tuple;

import java.util.*;

/**
 * Class providing useful methods for working with one-doimensional arrays of numbers (vectors).
 * 
 * @author Ondra
 */
public class VectorUtils {
    
    private static Random random = new Random(2015);
    
    private VectorUtils() {
    }

    /**
     * Creates a new vector by multiplying the given vector by the given real number.
     * @param a the vector
     * @param b the number
     * @return the result of multiplying the vector with the real number
     */
    public static double[] times(double[] a, double b){
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++)
            c[i] = a[i]*b;
        return c;
    }

    /**
     * Multiplies the given vector by the given number. This method modifies its
     * inputs and does not return anything.
     * @param a the vector
     * @param b the number
     */
    public static void multiply(double[] a, double b){
        for (int i = 0; i < a.length; i++){
            a[i] = a[i]*b;
        }
    }

    public static void add(double[] a, double b){
        for (int i = 0; i < a.length; i++){
            a[i] = a[i]+b;
        }
    }

    public static void add(double[] a, double[] b){
        for (int i = 0; i < a.length; i++){
            a[i] = a[i]+b[i];
        }
    }

    /**
     * Creates a new vector by summing the given vector with the given real number.
     * @param a the vector
     * @param b the number
     * @return the result of summing the vector with the real number
     */
    public static double[] plus(double a[], double b){
        double[] c = new double[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]+b;
        }
        return c;
    }
    
    /**
     * Creates a new vector by summing the given vectors.
     * @param a the first vector
     * @param b the second vector
     * @return the result of summing the two vectors
     */
    public static double[] plus(double a[], double b[]){
        double[] c = new double[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]+b[i];
        }
        return c;
    }

    /**
     * Creates a new vector by subtracting the given real number from the given vector.
     * @param a the vector
     * @param b the number
     * @return the result of subtracting the given real number from the given vector
     */
    public static double[] minus(double a[], double b){
        double[] c = new double[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]-b;
        }
        return c;
    }

    /**
     * Creates a new vector by subtracting the second vector from the first vector.
     * @param a the first vector
     * @param b the second vector
     * @return the result of subtracting the second vector from the first vector
     */
    public static double[] minus(double a[], double b[]){
        double[] c = new double[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]-b[i];
        }
        return c;
    }

    /**
     * Increments all elements of the vector by 1.
     * @param a the vector whose elements should be incremented
     */
    public static void increment(double[] a){
        for (int i = 0; i < a.length; i++){
            a[i]++;
        }
    }

    /**
     * Increments all elements of the vector by 1.
     * @param a the vector whose elements should be incremented
     */
    public static void increment(int[] a){
        for (int i = 0; i < a.length; i++){
            a[i]++;
        }
    }

    /**
     * Decrements all elements of the vector by 1.
     * @param a the vector whose elements should be decremented
     */
    public static void decrement(double[] a){
        for (int i = 0; i < a.length; i++){
            a[i]--;
        }
    }

    /**
     * Decrements all elements of the vector by 1.
     * @param a the vector whose elements should be decremented
     */
    public static void decrement(int[] a){
        for (int i = 0; i < a.length; i++){
            a[i]--;
        }
    }
    
    /**
     * Creates a new vector by multiplying the given vector by the given real number.
     * @param a the vector
     * @param b the number
     * @return the result of multiplying the vector with the real number
     */
    public static int[] times(int[] a, int b){
        int[] c = new int[a.length];
        for (int i = 0; i < a.length; i++)
            c[i] = a[i]*b;
        return c;
    }
    
    /**
     * Creates a new vector by summing the given vector with the given real number.
     * @param a the vector
     * @param b the number
     * @return the result of summing the vector with the real number
     */
    public static int[] plus(int a[], int b){
        int[] c = new int[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]+b;
        }
        return c;
    }
    
    /**
     * Creates a new vector by summing the given vectors.
     * @param a the first vector
     * @param b the second vector
     * @return the result of summing the two vectors
     */
    public static int[] plus(int a[], int b[]){
        int[] c = new int[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]+b[i];
        }
        return c;
    }
    
    /**
     * Creates a new vector by subtracting the given real number from the given vector.
     * @param a the vector
     * @param b the number
     * @return the result of subtracting the given real number from the given vector
     */
    public static int[] minus(int a[], int b){
        int[] c = new int[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]-b;
        }
        return c;
    }
    
    /**
     * Creates a new vector by subtracting the second vector from the first vector.
     * @param a the first vector
     * @param b the second vector
     * @return the result of subtracting the second vector from the first vector
     */
    public static int[] minus(int a[], int b[]){
        int[] c = new int[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i]-b[i];
        }
        return c;
    }
    
    /**
     * Computes vector whose elements are the results of logical OR of the respective
     * elements iterable the two given vectors.
     * @param a the first vector
     * @param b the second vector
     * @return the vector containing logical ORs of the elements of the two given vectors
     */
    public static boolean[] or(boolean a[], boolean b[]){
        boolean[] c = new boolean[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i] | b[i];
        }
        return c;
    }
    
    /**
     * Computes vector whose elements are the results of logical AND of the respective
     * elements iterable the two given vectors.
     * @param a the first vector
     * @param b the second vector
     * @return the vector containing logical ANDs of the elements of the two given vectors
     */
    public static boolean[] and(boolean a[], boolean b[]){
        boolean[] c = new boolean[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i] & b[i];
        }
        return c;
    }
    
    /**
     * Computes vector whose elements are the results of logical XOR of the respective
     * elements iterable the two given vectors.
     * @param a the first vector
     * @param b the second vector
     * @return the vector containing logical XORs of the elements of the two given vectors
     */
    public static boolean[] xor(boolean a[], boolean b[]){
        boolean[] c = new boolean[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = a[i] ^ b[i];
        }
        return c;
    }

    /**
     * Computes a new vector whose elements are logical NOTs of the elements of the given vector.
     * @param a the vector
     * @return the vector containing logical NOTs of the elements of the iven vector
     */
    public static boolean[] not(boolean[] a){
        boolean[] c = new boolean[a.length];
        for (int i = 0; i < c.length; i++){
            c[i] = !a[i];
        }
        return c;
    }

    /**
     * Computes vector of length values.length iterable which the i-th element is the sum of the elements from 0 to i.
     * @param values the input vector
     * @return cummulative sums of the input vector
     */
    public static double[] cummulative(double values[]){
        double cumm[] = new double[values.length];
        for (int i = 0; i < values.length; i++){
            if (i == 0)
                cumm[0] = values[0];
            else 
                cumm[i] = cumm[i-1]+values[i];
        }
        return cumm;
    }
    
    /**
     * Finds minimum value iterable the given vector.
     * @param values the vector
     * @return the minimum iterable the given vector
     */
    public static double min(double values[]){
        double min = Double.POSITIVE_INFINITY;
        for (double value : values){
            if (value < min)
                min = value;
        }
        return min;
    }

    /**
     * Finds minimum value iterable the given vector .
     * @param values the vector
     * @param mask mask denoting from which elements the minimum should be selected
     * @return the minimum iterable the given vector (w.r.t. the given mask)
     */
    public static double min(double values[], boolean[] mask){
        double min = Double.POSITIVE_INFINITY;
        int i = 0;
        for (double value : values){
            if (mask[i] && value < min){
                min = value;
            }
            i++;
        }
        return min;
    }

    /**
     * Finds the index of the minimum value iterable the given vector.
     * @param values the vector
     * @return index of the minimum iterable the given vector
     */
    public static int minIndex(double values[]){
        double min = Double.NEGATIVE_INFINITY;
        int minIndex = 0;
        int index = 0;
        for (double value : values){
            if (value < min){
                min = value;
                minIndex = index;
            }
            index++;
        }
        return minIndex;
    }

    /**
     * Finds the index of the minimum value iterable the given vector.
     * @param values the vector
     * @param mask mask denoting from which elements the minimum should be selected
     * @return index of the minimum iterable the given vector (w.r.t. the given mask)
     */
    public static int minIndex(double values[], boolean[] mask){
        double min = Double.NEGATIVE_INFINITY;
        int minIndex = 0;
        int index = 0;
        for (double value : values){
            if (mask[index] && value < min){
                min = value;
                minIndex = index;
            }
            index++;
        }
        return minIndex;
    }

    
    /**
     * Finds maximum value iterable the given vector.
     * @param values the vector
     * @return the maximum iterable the given vector
     */
    public static double max(double values[]){
        double max = Double.NEGATIVE_INFINITY;
        for (double value : values){
            if (value > max)
                max = value;
        }
        return max;
    }
    
    /**
     * Finds the index of the maximum value iterable the given vector.
     * @param values the vector
     * @return index of the maximum iterable the given vector
     */
    public static int maxIndex(double values[]){
        double max = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;
        int index = 0;
        for (double value : values){
            if (value > max){
                max = value;
                maxIndex = index;
            }
            index++;
        }
        return maxIndex;
    }
    
    /**
     * Computes sum of the elements of the vector.
     * @param values the vector
     * @return the sum of the elements of the vector
     */
    public static double sum(double values[]){
        double sum = 0;
        for (double value : values)
            sum += value;
        return sum;
    }
    
    /**
     * Computes product of the elements of the vector.
     * @param values the vector
     * @return the product of the elements of the vector
     */
    public static double product(double values[]){
        double product = 1.0;
        for (double value : values)
            product *= value;
        return product;
    }
    
    /**
     * Computes average of the elements of the vector.
     * @param values the vector
     * @return the average of the elements of the vector
     */
    public static double mean(double values[]){
        double sum = 0;
        for (double value : values)
            sum += value;
        return sum/values.length;
    }

    /**
     * Computes the sample variance of the elements of the vector.
     * @param values the vector
     * @return the sample variance of the elements of the vector
     */
    public static double variance(double[] values){
        if (values.length < 2){
            return 0;
        }
        double mean = mean(values);
        double var = 0;
        for (double d : values){
            var += (d-mean)*(d-mean);
        }
        return var/(values.length-1);
    }

    /**
     * Computes the sample kurtosis of the elements of the vector.
     * @param values the vector
     * @return the sample kurtosis of the elements of the vector
     */
    public static double kurtosis(double[] values){
        double s1 = 0;
        double s2 = 0;
        double mean = mean(values);
        for (int i = 0; i < values.length; i++){
            double xi = values[i];
            s1 += (xi-mean)*(xi-mean)*(xi-mean)*(xi-mean);
            s2 += (xi-mean)*(xi-mean);
        }
        return s1/values.length/((s2/values.length)*(s2/values.length))-3;
    }

    /**
     * Computes average of the elements of the vector.
     * @param values the vector
     * @return the average of the elements of the vector
     */
    public static double mean_Double(List<Double> values){
        return mean(toDoubleArray(values));
    }

    /**
     * Computes median of the elements of the vector.
     * @param values the vector
     * @return the median of the elements of the vector
     */
    public static int median(int[] values){
        if (values.length == 0){
            return 0;
        }
        values = VectorUtils.copyArray(values);
        Arrays.sort(values);
        return values[values.length/2];
    }

    /**
     * Computes median of the elements of the vector.
     * @param values the vector
     * @return the median of the elements of the vector
     */
    public static double median(double[] values){
        if (values.length == 0){
            return Double.NaN;
        }
        values = VectorUtils.copyArray(values);
        Arrays.sort(values);
        return values[values.length/2];
    }

    /**
     * Computes vector of length values.length iterable which the i-th element is the sum of the elements from 0 to i.
     * @param values the input vector
     * @return cummulative sums of the input vector
     */
    public static int[] cummulative(int values[]){
        int cumm[] = new int[values.length];
        for (int i = 0; i < values.length; i++){
            if (i == 0)
                cumm[0] = values[0];
            else 
                cumm[i] = cumm[i-1]+values[i];
        }
        return cumm;
    }
    
    /**
     * Finds minimum value iterable the given vector.
     * @param values the vector
     * @return the minimum iterable the given vector
     */
    public static int min(int values[]){
        int min = Integer.MAX_VALUE;
        for (int value : values){
            if (value < min)
                min = value;
        }
        return min;
    }
    
    /**
     * Finds the index of the minimum value iterable the given vector.
     * @param values the vector
     * @return index of the minimum iterable the given vector (w.r.t. the given mask)
     */
    public static int minIndex(int values[]){
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        int index = 0;
        for (int value : values){
            if (value < min){
                min = value;
                minIndex = index;
            }
            index++;
        }
        return minIndex;
    }
    
    /**
     * Finds maximum value iterable the given vector.
     * @param values the vector
     * @return the maximum iterable the given vector
     */
    public static int max(int values[]){
        int max = Integer.MIN_VALUE;
        for (int value : values){
            if (value > max)
                max = value;
        }
        return max;
    }
    
    /**
     * Finds the index of the maximum value iterable the given vector.
     * @param values the vector
     * @return index of the maximum iterable the given vector
     */
    public static int maxIndex(int values[]){
        int max = Integer.MIN_VALUE;
        int maxIndex = 0;
        int index = 0;
        for (int value : values){
            if (value > max){
                max = value;
                maxIndex = index;
            }
            index++;
        }
        return maxIndex;
    }
    
    /**
     * Finds indices of all maximum values iterable the given vector
     * @param values the vector
     * @return list of indices of the maximum values iterable the given vector
     */
    public static List<Integer> maxIndices(int values[]){
        int max = max(values);
        List<Integer> retVal = new ArrayList<Integer>();
        for (int i = 0; i < values.length; i++){
            if (values[i] == max){
                retVal.add(i);
            }
        }
        return retVal;
    }
    
    /**
     * Finds indices of all maximum values iterable the given vector
     * @param values the vector
     * @return list of indices of the maximum values iterable the given vector
     */
    public static List<Integer> maxIndices(double values[]){
        double max = max(values);
        List<Integer> retVal = new ArrayList<Integer>();
        for (int i = 0; i < values.length; i++){
            if (values[i] == max){
                retVal.add(i);
            }
        }
        return retVal;
    }
    
    /**
     * Computes sum of the elements of the vector.
     * @param values the vector
     * @return the sum of the elements of the vector
     */
    public static int sum(int values[]){
        int sum = 0;
        for (int value : values)
            sum += value;
        return sum;
    }
    
    /**
     * Computes product of the elements of the vector.
     * @param values the vector
     * @return the product of the elements of the vector
     */
    public static int product(int values[]){
        int product = 1;
        for (int value : values)
            product *= value;
        return product;
    }
    
    /**
     * Counts the number of occurrences of the given value iterable the given vector.
     * @param values the vector
     * @param value the value
     * @return number of occurrences of the given value iterable the given vector
     */
    public static int occurrences(int values[], int value){
        int sum = 0;
        for (double v : values){
            if (v == value)
                sum++;
        }
        return sum;
    }
    
    /**
     * Counts the number of occurrences of the given value iterable the given vector.
     * @param values the vector
     * @param value the value
     * @return number of occurrences of the given value iterable the given vector
     */
    public static int occurrences(double values[], double value){
        int sum = 0;
        for (double v : values){
            if (v == value)
                sum++;
        }
        return sum;
    }
    
    /**
     * Counts the number of occurrences of the given value iterable the given vector.
     * @param values the vector
     * @param value the value
     * @return number of occurrences of the given value iterable the given vector
     */
    public static int occurrences(boolean values[], boolean value){
        int sum = 0;
        for (boolean v : values){
            if (v == value)
                sum++;
        }
        return sum;
    }
    
    /**
     * Finds the first index of the given value iterable the given vector
     * @param array the vector
     * @param value the value
     * @return the index of the first occurence of the given value iterable the given vector, -1 if the value is not
     * contained iterable the vector
     */
    public static int find(int array[], int value){
        return find(array, value, 0);
    }
    
    /**
     * Finds the first index of the given value iterable the given vector starting from index <em>start</em>.
     * @param array the vector
     * @param value the value
     * @param start the index from which the value should be searched for
     * @return the index of the first occurence of the given value iterable the given vector, -1 if the value is not
     * contained iterable the vector
     */
    public static int find(int array[], int value, int start){
        for (int i = start; i < array.length; i++){
            if (array[i] == value)
                return i;
        }
        return -1;
    }
    
    /**
     * Finds the first index of the given value iterable the given vector
     * @param array the vector
     * @param value the value
     * @return the index of the first occurence of the given value iterable the given vector, -1 if the value is not
     * contained iterable the vector
     */
    public static int find(double array[], double value){
        return find(array, value, 0);
    }
    
    /**
     * Finds the first index of the given value iterable the given vector starting from index <em>start</em>.
     * @param array the vector
     * @param value the value
     * @param start the index from which the value should be searched for
     * @return the index of the first occurence of the given value iterable the given vector, -1 if the value is not
     * contained iterable the vector
     */
    public static int find(double array[], double value, int start){
        for (int i = start; i < array.length; i++){
            if (array[i] == value)
                return i;
        }
        return -1;
    }
    
    /**
     * Finds the first index of the given value iterable the given vector
     * @param array the vector
     * @param value the value
     * @return the index of the first occurence of the given value iterable the given vector, -1 if the value is not
     * contained iterable the vector
     */
    public static int find(boolean array[], boolean value){
        return find(array, value, 0);
    }
    
    /**
     * Finds the first index of the given value iterable the given vector starting from index <em>start</em>.
     * @param array the vector
     * @param value the value
     * @param start the index from which the value should be searched for
     * @return the index of the first occurence of the given value iterable the given vector, -1 if the value is not
     * contained iterable the vector
     */
    public static int find(boolean array[], boolean value, int start){
        for (int i = start; i < array.length; i++){
            if (array[i] == value)
                return i;
        }
        return -1;
    }

    /**
     * Finds all occurrences of the given value iterable the given vector.
     * @param array the vector
     * @param value the value
     * @return indices of all occurrences of the given value
     */
    public static int[] findAll(int[] array, int value){
        int[] retVal = new int[VectorUtils.occurrences(array, value)];
        int index = 0;
        for (int i = 0; i < array.length; i++){
            if (array[i] == value){
                retVal[index++] = i;
            }
        }
        return retVal;
    }

    /**
     * Finds all occurrences of the given value iterable the given vector.
     * @param array the vector
     * @param value the value
     * @return indices of all occurrences of the given value
     */
    public static int[] findAll(double[] array, double value){
        int[] retVal = new int[VectorUtils.occurrences(array, value)];
        int index = 0;
        for (int i = 0; i < array.length; i++){
            if (array[i] == value){
                retVal[index++] = i;
            }
        }
        return retVal;
    }

    /**
     * Finds all occurrences of the given value iterable the given vector.
     * @param array the vector
     * @param value the value
     * @return indices of all occurrences of the given value
     */
    public static int[] findAll(boolean[] array, boolean value){
        int[] retVal = new int[VectorUtils.occurrences(array, value)];
        int index = 0;
        for (int i = 0; i < array.length; i++){
            if (array[i] == value){
                retVal[index++] = i;
            }
        }
        return retVal;
    }

    /**
     * Computes average of the elements of the vector.
     * @param values the vector
     * @return the average of the elements of the vector
     */
    public static double mean(int values[]){
        double sum = 0;
        for (int value : values)
            sum += value;
        return sum/values.length;
    }
    
    /**
     * Computes average of the elements of the vector.
     * @param values the vector
     * @return the average of the elements of the vector
     */
    public static double mean_Integer(List<Integer> values){
        return mean(toIntegerArray(values));
    }
    
    /**
     * Shuffles elements of the given vector.
     * @param array the vector to be shuffled
     */
    public static void shuffle(int[] array){
        for (int i = 0; i < array.length/2; i++){
            if (Math.random() < 0.5){
                swap(array, i, random.nextInt(array.length-i)+i);
            }
        }
    }

    /**
     * Shuffles elements of the given vector.
     * @param array the vector to be shuffled
     * @param random the random number generator to be used to shuffle the vector
     */
    public static void shuffle(int[] array, Random random){
        for (int i = 0; i < array.length/2; i++){
            if (random.nextBoolean()){
                swap(array, i, random.nextInt(array.length-i)+i);
            }
        }
    }

    public static void shuffle(Object[] array){
        shuffle(array, VectorUtils.random);
    }

    public static void shuffle(Object[] array, Random random){
        for (int i = 0; i < array.length/2; i++){
            if (random.nextBoolean()){
                swap(array, i, random.nextInt(array.length-i)+i);
            }
        }
    }

    /**
     * Swaps two elements of a vector
     * @param array te vector
     * @param i1 the index of the first element to be swapped
     * @param i2 the index of the second element to be swapped
     */
    public static void swap(int[] array, int i1, int i2){
        int temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }

    /**
     * Swaps two elements of a vector
     * @param array te vector
     * @param i1 the index of the first element to be swapped
     * @param i2 the index of the second element to be swapped
     */
    public static void swap(boolean[] array, int i1, int i2){
        boolean temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }

    /**
     * Swaps two elements of a vector
     * @param array te vector
     * @param i1 the index of the first element to be swapped
     * @param i2 the index of the second element to be swapped
     */
    public static void swap(double[] array, int i1, int i2){
        double temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }

    /**
     * Swaps two elements of a vector
     * @param array te vector
     * @param i1 the index of the first element to be swapped
     * @param i2 the index of the second element to be swapped
     */
    public static void swap(char[] array, int i1, int i2){
        char temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }

    public static void swap(Object[] array, int i1, int i2){
        Object temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }

    /**
     * Creates an array of type double[] from a list of instances of class Double.
     * @param values the list of instances of class Double
     * @return the array of type double[] containing the values from the given list
     */
    public static double[] toDoubleArray(Collection<Double> values){
        double ret[] = new double[values.size()];
        int i = 0;
        for (double d : values) {
            ret[i] = d;
            i++;
        }
        return ret;
    }
    
    /**
     * Creates an array of type double[] from a list of instances of class Double.
     * @param values the list of instances of class Double
     * @return the array of type double[] containing the values from the given list
     */
    public static boolean[] toBooleanArray(Collection<Boolean> values){
        boolean ret[] = new boolean[values.size()];
        int i = 0;
        for (boolean b : values) {
            ret[i] = b;
            i++;
        }
        return ret;
    }
    
    /**
     * Creates an array of type int[] from a list of instances of class Integer.
     * @param values the list of instances of class Integer
     * @return the array of type int[] containing the values from the given list
     */
    public static int[] toIntegerArray(Collection<Integer> values){
        int ret[] = new int[values.size()];
        int i = 0;
        for (int integer : values) {
            ret[i] = integer;
            i++;
        }
        return ret;
    }
    
    /**
     * Creates an array of type int[] from a set of instances of class Integer.
     * @param values the set of instances of class Integer
     * @return the array of type int[] containing the values from the given list
     */
    public static int[] toIntegerArray(Set<Integer> values){
        int ret[] = new int[values.size()];
        int i = 0;
        for (int val : values){
            ret[i++] = val;
        }
        return ret;
    }
    
    /**
     * Creates a copy of the given array.
     * @param array the array
     * @return the copy of the given array
     */
    public static double[] copyArray(double[] array){
        double[] retVal = new double[array.length];
        System.arraycopy(array, 0, retVal, 0, array.length);
        return retVal;
    }
    
    /**
     * Creates a copy of the given array.
     * @param array the array
     * @return the copy of the given array
     */
    public static int[] copyArray(int[] array){
        int[] retVal = new int[array.length];
        System.arraycopy(array, 0, retVal, 0, array.length);
        return retVal;
    }
    
    /**
     * Creates a copy of the given array.
     * @param array the array
     * @return the copy of the given array
     */
    public static boolean[] copyArray(boolean[] array){
        boolean[] retVal = new boolean[array.length];
        System.arraycopy(array, 0, retVal, 0, array.length);
        return retVal;
    }

    /**
     * Creates a copy of a sub-array of the given array.
     * @param array the array
     * @param start the start index (inclusive) of the sub-array
     * @param length number of elements iterable the sub-array
     * @return copy of sub-array of the given array.
     */
    public static double[] copyArray(double[] array, int start, int length){
        double[] retVal = new double[length];
        System.arraycopy(array, 0, retVal, start, length);
        return retVal;
    }

    /**
     * Creates a copy of a sub-array of the given array.
     * @param array the array
     * @param start the start index (inclusive) of the sub-array
     * @param length number of elements iterable the sub-array
     * @return copy of sub-array of the given array.
     */
    public static int[] copyArray(int[] array, int start, int length){
        int[] retVal = new int[length];
        System.arraycopy(array, 0, retVal, start, length);
        return retVal;
    }

    /**
     * Creates a copy of a sub-array of the given array.
     * @param array the array
     * @param start the start index (inclusive) of the sub-array
     * @param length number of elements iterable the sub-array
     * @return copy of sub-array of the given array.
     */
    public static boolean[] copyArray(boolean[] array, int start, int length){
        boolean[] retVal = new boolean[length];
        System.arraycopy(array, 0, retVal, start, length);
        return retVal;
    }

    /**
     * Concatenates two vectors.
     * @param array1 the first vector
     * @param array2 the second vector
     * @return the vector obtained by concatenating the first and second vector
     */
    public static double[] concat(double[] array1, double[] array2){
        double[] retVal = new double[array1.length+array2.length];
        System.arraycopy(array1, 0, retVal, 0, array1.length);
        System.arraycopy(array2, 0, retVal, array1.length, array2.length);
        return retVal;
    }
    
    /**
     * Concatenates two vectors.
     * @param array1 the first vector
     * @param array2 the second vector
     * @return the vector obtained by concatenating the first and second vector
     */
    public static int[] concat(int[] array1, int[] array2){
        int[] retVal = new int[array1.length+array2.length];
        System.arraycopy(array1, 0, retVal, 0, array1.length);
        System.arraycopy(array2, 0, retVal, array1.length, array2.length);
        return retVal;
    }

    /**
     * Concatenates two vectors.
     * @param array1 the first vector
     * @param array2 the second vector
     * @return the vector obtained by concatenating the first and second vector
     */
    public static long[] concat(long[] array1, long[] array2){
        long[] retVal = new long[array1.length+array2.length];
        System.arraycopy(array1, 0, retVal, 0, array1.length);
        System.arraycopy(array2, 0, retVal, array1.length, array2.length);
        return retVal;
    }

    /**
     * Concatenates two vectors.
     * @param array1 the first vector
     * @param array2 the second vector
     * @return the vector obtained by concatenating the first and second vector
     */
    public static boolean[] concat(boolean[] array1, boolean[] array2){
        boolean[] retVal = new boolean[array1.length+array2.length];
        System.arraycopy(array1, 0, retVal, 0, array1.length);
        System.arraycopy(array2, 0, retVal, array1.length, array2.length);
        return retVal;
    }
    
    /**
     * Parses an array of type double[] from its string representation,
     * e.g.: double[] a = VectorUtils.parseDoubleArray("[1, 2.5, 3]");
     * @param s the string representation of the double[] array
     * @return the parsed array of type double[]
     */
    public static double[] parseDoubleArray(String s){
        s = s.substring(s.indexOf("[")+1, s.indexOf("]"));
        String[] items = s.split(",");
        List<Double> d = new ArrayList<Double>();
        for (String strItem : items){
            strItem = strItem.trim();
            if (strItem.length() > 0)
                d.add(Double.parseDouble(strItem));
        }
        return VectorUtils.toDoubleArray(d);
    }
    
    /**
     * Parses an array of type int[] from its string representation,
     * e.g.: int[] a = VectorUtils.parseIntegerArray("[1, 2, 3]");
     * @param s the string representation of the int[] array
     * @return the parsed array of type int[]
     */
    public static int[] parseIntegerArray(String s){
        s = s.substring(s.indexOf("[")+1, s.indexOf("]"));
        String[] items = s.split(",");
        List<Integer> d = new ArrayList<Integer>();
        for (String strItem : items){
            strItem = strItem.trim();
            if (strItem.length() > 0)
                d.add(Integer.parseInt(strItem));
        }
        return VectorUtils.toIntegerArray(d);
    }
    
    /**
     * Creates a string representation of the given array of type int[] (this representation
     * can be parsed using the method parseIntegerArray).
     * @param array the array
     * @return string representation of the given array
     */
    public static String intArrayToString(int[] array){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int a : array){
            sb.append(a).append(", ");
        }
        if (sb.lastIndexOf(", ") != -1)
            sb.delete(sb.lastIndexOf(", "), sb.length());
        sb.append("]");
        return sb.toString();
    }

    public static String longArrayToString(long[] array){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (long a : array){
            sb.append(a).append(", ");
        }
        if (sb.lastIndexOf(", ") != -1)
            sb.delete(sb.lastIndexOf(", "), sb.length());
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Creates a string representation of the given array of type double[] (this representation
     * can be parsed using the method parseDoubleArray).
     * @param array the array
     * @return string representation of the given array
     */
    public static String doubleArrayToString(double[] array){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (double a : array){
            sb.append(a).append(", ");
        }
        if (sb.lastIndexOf(", ") != -1)
            sb.delete(sb.lastIndexOf(", "), sb.length());
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Creates a string representation of the given array of type boolean[].
     * @param array the array
     * @return string representation of the given array
     */
    public static String booleanArrayToString(boolean[] array){
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (boolean a : array){
            sb.append((a ? 1 : 0)+", ");
        }
        if (sb.lastIndexOf(", ") != -1)
            sb.delete(sb.lastIndexOf(", "), sb.length());
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Creates a random vector of integers of length <em>size</em>
     * @param size the length of the random vector
     * @return a random vector with elements from 0 to Integer.MAX_VALUE
     */
    public static int[] randomVector(int size){
        return randomVector(size, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a random vector of integers of length <em>size</em>
     * @param size the length of the random vector
     * @param max the upper limit (exclusive) on the random numbers
     * @return a random vector with elements from 0 to max-1
     */
    public static int[] randomVector(int size, int max){
        return randomVector(size, max, VectorUtils.random);
    }

    /**
     * Creates a random vector of integers of length <em>size</em>
     * @param size the length of the random vector
     * @param max the upper limit (exclusive) on the random numbers
     * @param rand the random number generator to be used to create the random vector
     * @return a random vector with elements from 0 to max-1
     */
    public static int[] randomVector(int size, int max, Random rand){
        int[] retVal = new int[size];
        for (int i = 0; i < size; i++){
            retVal[i] = rand.nextInt(max);
        }
        return retVal;
    }

    /**
     * Creates a random vector of doubles of length <em>size</em>
     * @param size the length of the random vector
     * @return a random vector with elements from 0 to 1
     */
    public static double[] randomDoubleVector(int size){
        return randomDoubleVector(size, 1.0);
    }

    /**
     * Creates a random vector of doubles of length <em>size</em>
     * @param size the length of the random vector
     * @param max the upper limit (exclusive) on the random numbers
     * @return a random vector with elements from 0 to max
     */
    public static double[] randomDoubleVector(int size, double max){
        return randomDoubleVector(size, max, VectorUtils.random);
    }

    /**
     * Creates a random vector of integers of length <em>size</em>
     * @param size the length of the random vector
     * @param max the upper limit (exclusive) on the random numbers
     * @param rand the random number generator to be used to create the random vector
     * @return a random vector with elements from 0 to max-1
     */
    public static double[] randomDoubleVector(int size, double max, Random rand){
        double[] retVal = new double[size];
        for (int i = 0; i < size; i++){
            retVal[i] = rand.nextDouble()*max;
        }
        return retVal;
    }

    /**
     * Creates a random vector of booleans of length <em>size</em>.
     * @param size the length of the random vector
     * @return a random vector of boolean elements
     */
    public static boolean[] randomBooleanVector(int size){
        return randomBooleanVector(size, random);
    }

    /**
     * Creates a random vector of booleans of length <em>size</em>.
     * @param size the length of the random vector
     * @param random the random number generator used to generate the random vactor
     * @return a random vector of boolean elements
     */
    public static boolean[] randomBooleanVector(int size, Random random){
        boolean[] retVal = new boolean[size];
        for (int i = 0; i < size; i++){
            retVal[i] = random.nextBoolean();
        }
        return retVal;
    }

    public static boolean[] randomBooleanVector(int size, int numTrues){
        return randomBooleanVector(size, numTrues, random);
    }

    public static boolean[] randomBooleanVector(int size, int numTrues, Random random){
        Tuple<Integer> tuple = Combinatorics.randomCombination(new NaturalNumbersList(0, size), numTrues, random);
        boolean[] retVal = new boolean[size];
        for (int i = 0; i < tuple.size(); i++){
            retVal[tuple.get(i)] = true;
        }
        return retVal;
    }

    /**
     * Creates a random vector of chars of length <em>size</em>.
     * @param size the length of the random vector
     * @return a random vector of char elements
     */
    public static char[] randomCharacterVector(int size){
        char[] retVal = new char[size];
        for (int i = 0; i < size; i++){
            retVal[i] = (char)('A'+random.nextInt((int)('Z'-'A')));
        }
        return retVal;
    }

    /**
     * Converts the given array of integers to a list of instances of class Integer
     * @param array the array
     * @return the list of instances of class Integer representing the original array
     */
    public static List<Integer> toList(int[] array){
        List<Integer> list = new ArrayList<Integer>();
        for (int i : array){
            list.add(i);
        }
        return list;
    }
    
    /**
     * Converts the given array of doubles to a list of instances of class Double
     * @param array the array
     * @return the list of instances of class Double representing the original array
     */
    public static List<Double> toList(double[] array){
        List<Double> list = new ArrayList<Double>();
        for (double i : array){
            list.add(i);
        }
        return list;
    }
    
    /**
     * Converts the given array of booleans to a list of instances of class Boolean
     * @param array the array
     * @return the list of instances of class Boolean representing the original array
     */
    public static List<Boolean> toList(boolean[] array){
        List<Boolean> list = new ArrayList<Boolean>();
        for (boolean i : array){
            list.add(i);
        }
        return list;
    }

    /**
     * Creates a boolean vector of length <em>size</em> containing only values <em>true</em>.
     * @param size the length of the vector
     * @return the boolean vector
     */
    public static boolean[] trues(int size){
        boolean[] b = new boolean[size];
        Arrays.fill(b, true);
        return b;
    }

    /**
     * 
     * Creates a boolean vector of length <em>size</em> containing only values <em>false</em>.
     * @param size the length of the vector
     * @return the boolean vector
     */
    public static boolean[] falses(int size){
        boolean[] b = new boolean[size];
        Arrays.fill(b, false);
        return b;
    }

    /**
     * Checks if elements iterable the given array are all equal.
     * @param values the array
     * @return true if all elements iterable the given array are equal
     */
    public static boolean allEqual(int[] values){
        if (values.length == 0){
            return true;
        }
        int value = values[0];
        for (int i = 1; i < values.length; i++){
            if (values[i] != value){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if elements iterable the given array are all equal.
     * @param values the array
     * @return true if all elements iterable the given array are equal
     */
    public static boolean allEqual(double[] values){
        if (values.length == 0){
            return true;
        }
        double value = values[0];
        for (int i = 1; i < values.length; i++){
            if (values[i] != value){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if elements iterable the given array are all equal.
     * @param values the array
     * @return true if all elements iterable the given array are equal
     */
    public static boolean allEqual(boolean[] values){
        if (values.length == 0){
            return true;
        }
        boolean value = values[0];
        for (int i = 1; i < values.length; i++){
            if (values[i] != value){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a[i] < b[i] for all i from 0 to a.length (= b.length)
     * @param a the first array
     * @param b the second array
     * @return true if a[i] < b[i] for all i from 0 to a.length (= b.length), false otherwise
     */
    public static boolean lowerThan(int[] a, int[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i] >= b[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a[i] > b[i] for all i from 0 to a.length (= b.length)
     * @param a the first array
     * @param b the second array
     * @return true if a[i] > b[i] for all i from 0 to a.length (= b.length), false otherwise
     */
    public static boolean greaterThan(int[] a, int[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i] <= b[i]){
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if a[i] <= b[i] for all i from 0 to a.length (= b.length)
     * @param a the first array
     * @param b the second array
     * @return true if a[i] <= b[i] for all i from 0 to a.length (= b.length), false otherwise
     */
    public static boolean lowerOrEqual(int[] a, int[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i] > b[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a[i] >= b[i] for all i from 0 to a.length (= b.length)
     * @param a the first array
     * @param b the second array
     * @return true if a[i] >= b[i] for all i from 0 to a.length (= b.length), false otherwise
     */
    public static boolean greaterThanOrEqual(int[] a, int[] b){
        for (int i = 0; i < a.length; i++){
            if (a[i] < b[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a[i] == b[i] for all i from 0 to a.length (= b.length)
     * @param a the first array
     * @param b the second array
     * @return true if a[i] == b[i] for all i from 0 to a.length (= b.length), false otherwise
     */
    public static boolean equal(int[] a, int[] b){
        return Arrays.equals(a, b);
    }

    /**
     * Checks if a[i] == b[i] for all i from 0 to a.length (= b.length)
     * @param a the first array
     * @param b the second array
     * @return true if a[i] == b[i] for all i from 0 to a.length (= b.length), false otherwise
     */
    public static boolean equal(boolean[] a, boolean[] b){
        return Arrays.equals(a, b);
    }

    /**
     * Checks if a[i] == b[i] for all i from 0 to a.length (= b.length)
     * @param a the first array
     * @param b the second array
     * @return true if a[i] == b[i] for all i from 0 to a.length (= b.length), false otherwise
     */
    public static boolean equal(double[] a, double[] b){
        return Arrays.equals(a, b);
    }

    /**
     * Reverses order of elements iterable the array.
     * @param ch the array to be reversed
     */
    public static void reverse(int[] ch){
        for (int i = 0; i < ch.length/2; i++){
            swap(ch, i, ch.length-i-1);
        }
    }

    /**
     * Reverses order of elements iterable the array.
     * @param ch the array to be reversed
     */
    public static void reverse(boolean[] ch){
        for (int i = 0; i < ch.length/2; i++){
            swap(ch, i, ch.length-i-1);
        }
    }

    /**
     * Reverses order of elements iterable the array.
     * @param ch the array to be reversed
     */
    public static void reverse(double[] ch){
        for (int i = 0; i < ch.length/2; i++){
            swap(ch, i, ch.length-i-1);
        }
    }

    /**
     * Reverses order of elements iterable the array.
     * @param ch the array to be reversed
     */
    public static void reverse(char[] ch){
        for (int i = 0; i < ch.length/2; i++){
            swap(ch, i, ch.length-i-1);
        }
    }

    /**
     * Checks if all elements of the given array are smaller than the given value
     * @param vector the array
     * @param value the value
     * @return true if all elements from the array are smaller than the given number, false otherwise
     */
    public static boolean allSmallerThan(double[] vector, double value){
        for (double d : vector){
            if (d >= value){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all elements of the given array are greater than the given value
     * @param vector the array
     * @param value the value
     * @return true if all elements from the array are greater than the given number, false otherwise
     */
    public static boolean allGreaterThan(double[] vector, double value){
        for (double d : vector){
            if (d <= value){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all elements of the given array are smaller than the given value
     * @param vector the array
     * @param value the value
     * @return true if all elements from the array are smaller than the given number, false otherwise
     */
    public static boolean allSmallerThan(int[] vector, int value){
        for (double d : vector){
            if (d >= value){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all elements of the given array are greater than the given value
     * @param vector the array
     * @param value the value
     * @return true if all elements from the array are greater than the given number, false otherwise
     */
    public static boolean allGreaterThan(int[] vector, int value){
        for (double d : vector){
            if (d <= value){
                return false;
            }
        }
        return true;
    }

    /**
     * Computes the value of the definite integral of the function given by
     * its x- and y-values. Uses trapezoidal rule for integration.
     * @param x the x-coordinates
     * @param y the function values iterable the points given by the x-coordinates
     * @return the value of the definite integral
     */
    public static double integrate(double[] x, double[] y){
        double area = 0;
        for (int i = 0; i < x.length-1; i++){
            area += (x[i+1]-x[i])*(y[i]+y[i+1])/2.0;
        }
        return area;
    }

    /**
     * Creates an array filled with values start, start+1, ..., end-1
     * @param start start (inclusive)
     * @param end end (exclusive)
     * @return array filled with values start, start+1, ..., end-1
     */
    public static int[] sequence(int start, int end){
        return sequence(start, end, 1);
    }
    
    /**
     * Creates an array filled with values start, start+increment, ..., end-1
     * @param start start (inclusive)
     * @param end end (exclusive)
     * @param increment increment
     * @return array filled with values start, start+increment, ..., end-1
     */
    public static double[] doubleSequence(double start, double end, double increment){
        if (start == end){
            return new double[]{start};
        }
        double[] retVal = new double[(int)Math.ceil((end-start)/increment)+1];
        double a = start;
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = a;
            a += increment;
        }
        return retVal;
    }

    /**
     * Creates an array filled with values start, start+increment, ..., end-1
     * @param start start (inclusive)
     * @param end end (exclusive)
     * @param increment increment
     * @return array filled with values start, start+increment, ..., end-1
     */
    public static int[] sequence(int start, int end, int increment){
        if (start == end){
            return new int[]{start};
        }
        int[] retVal = new int[(int)Math.ceil((end-start)/(double)increment)+1];
        int a = start;
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = a;
            a += increment;
        }
        return retVal;
    }

//    public static void main(String[] a){
//        System.out.println(intArrayToString(sequence(0,10)));
//    }

    /**
     * Creates a sub-vector of the given vector.
     * @param vector the vector
     * @param start the start index (inclusive) of the sub-vector
     * @param end the end index (exclusive) of the sub-array
     * @return the sub-vector
     */
    public static double[] subvector(double[] vector, int start, int end){
        double[] retVal = new double[end-start];
        for (int i = start; i < end; i++){
            retVal[i-start] = vector[i];
        }
        return retVal;
    }

    /**
     * Creates a sub-vector of the given vector.
     * @param vector the vector
     * @param start the start index (inclusive) of the sub-vector
     * @param end the end index (exclusive) of the sub-array
     * @return the sub-vector
     */
    public static int[] subvector(int[] vector, int start, int end){
        int[] retVal = new int[end-start];
        for (int i = start; i < end; i++){
            retVal[i-start] = vector[i];
        }
        return retVal;
    }
    
    /**
     * Creates a sub-vector of the given vector.
     * @param vector the vector
     * @param indexes indexes of the elements which should be inserted into the new array
     * @return the sub-vector
     */
    public static int[] subvector(int[] vector, int[] indexes){
        int[] retVal = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++){
            retVal[i] = vector[indexes[i]];
        }
        return retVal;
    }
    
    /**
     * Creates a sub-vector of the given vector.
     * @param vector the vector
     * @param start the start index (inclusive) of the sub-vector
     * @param end the end index (exclusive) of the sub-array
     * @return the sub-vector
     */
    public static boolean[] subvector(boolean[] vector, int start, int end){
        boolean[] retVal = new boolean[end-start];
        for (int i = start; i < end; i++){
            retVal[i-start] = vector[i];
        }
        return retVal;
    }

    public static int[] repeat(int repeats, int value){
        int[] retVal = new int[repeats];
        Arrays.fill(retVal, value);
        return retVal;
    }

    public static double[] repeat(int repeats, double value){
        double[] retVal = new double[repeats];
        Arrays.fill(retVal, value);
        return retVal;
    }

    public static boolean[] repeat(int repeats, boolean value){
        boolean[] retVal = new boolean[repeats];
        Arrays.fill(retVal, value);
        return retVal;
    }

    public static int[] repeat(int repeats, int[] seq){
        int[] retVal = new int[repeats*seq.length];
        for (int i = 0; i < repeats; i++){
            System.arraycopy(seq, 0, retVal, i*seq.length, seq.length);
        }
        return retVal;
    }

    public static int distinct(long[] vector){
        Set<Long> set = new HashSet<Long>();
        for (long l : vector){
            set.add(l);
        }
        return set.size();
    }

    public static int hammingDistance(boolean[] a, boolean[] b){
        int hd = 0;
        for (int i = 0; i < a.length; i++){
            if (a[i] != b[i]){
                hd++;
            }
        }
        return hd;
    }

    public static double[] exp(double[] a){
        double[] retVal = new double[a.length];
        for (int i = 0; i < a.length; i++){
            retVal[i] = Math.exp(a[i]);
        }
        return retVal;
    }

    public static double[] fun(double[] a, Sugar.Fun<Double,Double> fun){
        double[] retVal = new double[a.length];
        for (int i = 0; i < a.length; i++){
            retVal[i] = fun.apply(a[i]);
        }
        return retVal;
    }

    public static double dotProduct(double[] a, double[] b){
        double retVal = 0;
        for (int i = 0; i < a.length; i++){
            retVal += a[i]*b[i];
        }
        return retVal;
    }

    public static BitSet toBitSet(boolean[] b){
        BitSet bs = new BitSet(b.length);
        for (int i = 0; i < b.length; i++){
            bs.set(i, b[i]);
        }
        return bs;
    }

    public static BitSet[] toBitSets(boolean[][] b){
        BitSet[] retVal = new BitSet[b.length];
        for (int i = 0; i < b.length; i++){
            retVal[i] = toBitSet(b[i]);
        }
        return retVal;
    }
}
