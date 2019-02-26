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
 * IntegerFunction.java
 *
 * Created on 30. duben 2007, 10:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils;

/**
 *
 * Abstract class for representing integer functions of one variable.
 * 
 * @author Ondra
 */
public abstract class IntegerFunction {
    
    /**
     * Computes value of the function with argument <em>n</em>
     * @param n the argument of the function
     * @return value of the function for argument <em>n</em>
     */
    public abstract int f(int n);
    
    /**
     * Constant function.
     */
    public static class ConstantFunction extends IntegerFunction {
        
        private int value;
        
        /**
         * Creates a new instance of class ConstantFunction
         * @param value the constant value of this function
         */
        public ConstantFunction(int value){
            this.value = value;
        }
        
        public int f(int n){
            return value;
        }
    }
    
    /**
     * Funcion f(n) = n
     */
    public static class Identity extends IntegerFunction {
        
        /**
         * Creates a new instance of class Identity
         */
        public Identity(){
        }
        
        public int f(int n){
            return n;
        }
    }
    
    /**
     * Function a*n^2 + b*n + c
     */
    public static class Quadratic extends IntegerFunction {
        
        private int a, b, c;
        
        /**
         * Creates a new instance of class Quadratic
         * @param a coefficient of the quadratic term
         * @param b coefficient of the linear term
         * @param c coefficient of the constant term
         */
        public Quadratic(int a, int b, int c){
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
        public int f(int n){
            return a*n*n + b*n + c;
        }
    }
    
    /**
     * Function a*n^3 + b*n^2 + c*n + d
     */
    public static class Cubic extends IntegerFunction {
        
        private int a, b, c, d;
        
         /**
         * Creates a new instance of class Cubic
         * @param a coefficient of the cubic term
         * @param b coefficient of the quadratic term
         * @param c coefficient of the linear term
         * @param d coefficient of the constant term
         */
        public Cubic(int a, int b, int c, int d){
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
        
        public int f(int n){
            return a*n*n*n + b*n*n + c*n+d;
        }
    }
    
    //a*exp(x/b) + b
    /**
     * Function a*(int)Math.exp(n/(double)b)+c
     */
    public static class Exponential extends IntegerFunction {
        
        private int a, b, c;
        
        /**
         * Creates a new instance of class Exponential
         * @param a
         * @param b
         */
        public Exponential(int a, int b){
            this.a = a;
            this.b = b;
        }
        
        /**
         * Creates a new instance of class Exponential
         * @param a
         * @param b
         * @param c
         */
        public Exponential(int a, int b, int c){
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
        public int f(int n){
            return a*(int)Math.exp(n/(double)b)+c;
        }
    }
}
