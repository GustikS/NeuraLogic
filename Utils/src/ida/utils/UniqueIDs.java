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
 * UniqueNames.java
 *
 * Created on 13. listopad 2006, 17:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.utils;

/**
 * Simple class for giving unique ids. Each time the method getUniqueName is called,
 * a variable is incremented and the unique number is returned.
 * @author Ondra
 */
public class UniqueIDs {
    
    private static long unique = 1;

    private final static Object lock = new Object();
    
    /** Creates a new instance of UniqueNames */
    private UniqueIDs() {
    }
    
    /**
     * 
     * @return a unique long
     */
    public static long getUniqueName(){
        long retVal = 0;
        synchronized (lock) {
            retVal = unique++;
        }
        return retVal;
    }
}
