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
 * Class for representation of 
 * @author Ondra
 */
public class LinearFunction {
    
    //a0,a1,a2,...
    private double[] coeffs;
    
    /**
     * 
     * @param coeffs
     */
    public LinearFunction(double ...coeffs){
        this.coeffs = coeffs;
    }
    
    /**
     * 
     * @param values
     * @return
     */
    public double evaluate(double ...values){
        if (values.length != coeffs.length-1){
            throw new IllegalArgumentException();
        }
        double retVal = coeffs[0];
        for (int i = 0; i < values.length; i++){
            retVal += coeffs[i+1]*values[i];
        }
        return retVal;
    }
}
