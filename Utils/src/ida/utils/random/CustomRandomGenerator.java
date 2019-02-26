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
 * CustomRandomGenerator.java
 *
 * Created on 25. duben 2007, 10:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils.random;
import ida.utils.*;

import java.util.Random;
/**
 * Histogram-based random number generator. It generates integer random numbers
 * according to histogram of frequencies supplied by user.
 * 
 * @author Ondra
 */
public class CustomRandomGenerator {
    
    private double[] cummulated;
    
    private Random random = new Random();
    
    /**
     * Creates a new instance of class CustomRandomGenerator with the given histogram of frequencies.
     * For example, when histogram = {1,2,3} then the probability of drawing the number 0
     * is 1/6, the probability of drawing the number 1 is 1/3 and the probability of drawing the number 2
     * is 1/2.
     * @param histogram frequencies of the random numbers (non-normalized)
     * the frequency
     */
    public CustomRandomGenerator(int[] histogram) {
        double sum = VectorUtils.sum(histogram);
        double cumm = 0;
        this.cummulated = new double[histogram.length];
        for (int i = 0; i < histogram.length; i++){
            cumm += histogram[i]/sum;
            cummulated[i] = cumm;
        }
    }
    
    /**
     * Creates a new instance of class CustomRandomGenerator with the given histogram of frequencies.
     * For example, when histogram = {1,2,3} then the probability of drawing the number 0
     * is 1/6, the probability of drawing the number 1 is 1/3 and the probability of drawing the number 2
     * is 1/2.
     * @param histogram frequencies of the random numbers (non-normalized)
     * the frequency
     */
    public CustomRandomGenerator(double[] histogram) {
        double sum = VectorUtils.sum(histogram);
        double cumm = 0;
        this.cummulated = new double[histogram.length];
        for (int i = 0; i < histogram.length; i++){
            cumm += histogram[i]/sum;
            cummulated[i] = cumm;
        }
    }

    /**
     * Creates a new instance of class CustomRandomGenerator with the given histogram of frequencies.
     * For example, when histogram = {1,2,3} then the probability of drawing the number 0
     * is 1/6, the probability of drawing the number 1 is 1/3 and the probability of drawing the number 2
     * is 1/2.
     * @param histogram frequencies of the random numbers (non-normalized)
     * the frequency
     * @param random the underlying random number generatoir which should be used
     */
    public CustomRandomGenerator(int[] histogram, Random random) {
        this(histogram);
        this.random = random;
    }

    /**
     * Creates a new instance of class CustomRandomGenerator with the given histogram of frequencies.
     * For example, when histogram = {1,2,3} then the probability of drawing the number 0
     * is 1/6, the probability of drawing the number 1 is 1/3 and the probability of drawing the number 2
     * is 1/2.
     * @param histogram frequencies of the random numbers (non-normalized)
     * the frequency
     * @param random the underlying random number generatoir which should be used
     */
    public CustomRandomGenerator(double[] histogram, Random random) {
        this(histogram);
        this.random = random;
    }

    /**
     * 
     * @return new (pseudo-)random number from the spcified distribution
     */
    public int nextInt(){
        if (cummulated.length == 1)
            return 0;
        double rand = random.nextDouble();
        int start = 0;
        int end = cummulated.length-1;
        int s = (start+end)/2;
        do {
            if (cummulated[s] > rand && (s == 0 || cummulated[s-1] < rand) || s == cummulated.length-1)
                return s;
            else if (cummulated[s] < rand)
                start = s + 1;
            else if (cummulated[s] > rand)
                end = s - 1;
            s = (start+end)/2;
        } while (start <= end);
        throw new RuntimeException("An error occured iterable method nextInt()");
    }
}
