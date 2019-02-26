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
 * RandomGenerator.java
 *
 * Created on 13. listopad 2006, 10:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package ida.utils.random;

import java.util.*;
import static java.lang.Math.*;

/**
 * Class for generating random numbers with binomial distribution.
 * 
 * @author Ondrej Kuzelka
 */
public class BinomialGenerator {

    private Random random = new Random();
    private double cummulated[];
    private double logProbability;
    private double p;
    private int maxValue;
    private int min, max;

    /**
     * Creates a new instance of class BinomialGenerator
     * @param p probability of the "positive" event
     * @param maxValue "number of tries"
     */
    public BinomialGenerator(double p, int maxValue) {
        this.p = p;
        this.maxValue = maxValue;
        this.cummulated = new double[maxValue + 1];
        this.init();
    }

    /**
     * Generates a new (pseudo-)random number with binomial distribution with the given parameters.
     * @return new (pseudo-)random number with binomial distribution with the given parameters.
     */
    public int nextInt() {
        if (cummulated.length == 1) {
            return 0;
        }
        double rand = random.nextDouble();
        int start = min;
        int end = max;
        int s = (start + end) / 2;
        do {
            if (cummulated[s] > rand && (s == 0 || cummulated[s - 1] <= rand) || s == cummulated.length - 1) {
                return s;
            } else if (cummulated[s] < rand) {
                start = s + 1;
            } else if (cummulated[s] > rand) {
                end = s - 1;
            }
            s = (start + end) / 2;
        } while (start <= end);
        throw new RuntimeException("An error occured iterable method nextInt()");
    }

    private void init() {
        boolean lastZero = true;
        max = cummulated.length - 1;
        for (int i = 0; i < cummulated.length; i++) {
            computeNextLogProbability(i);
            if (lastZero && exp(logProbability) == 0) {
                min = i;
            }
            if (exp(logProbability) > 0) {
                lastZero = false;
                max = i;
            }
            if (Double.isNaN(logProbability)) {
                throw new java.lang.ArithmeticException("A problem with cummulative distribution occured.");
            }
            if (i != 0) {
                cummulated[i] = exp(logProbability) + cummulated[i - 1];
            } else {
                cummulated[i] = exp(logProbability);
            }
        }
    }

    private void computeNextLogProbability(int k) {
        int length = cummulated.length - 1;
        if (k == 0) {
            for (int i = 0; i < length; i++) {
                logProbability += log(length - i);
            }
            for (int i = 1; i <= length; i++) {
                logProbability -= log(i);
            }
            logProbability += length * log(1 - this.p);
        } else {
            // log(F(k)) = log(F(k-1)) + log(n-k+1) + log(p) - log(k) - log(1-p)
            if (this.p != 0 && this.p != 1) {
                logProbability = logProbability + log(length - k + 1) + log(this.p) - log(k) - log(1 - this.p);
            } else if (this.p == 1 && k == length) {
                logProbability = Double.NEGATIVE_INFINITY;
            }

        }
    }
}
