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

import java.io.*;
import java.util.*;

/**
 * A class for easier creation of command-line utilities. It contains several useful functions.
 * 
 * @author admin
 */
public class CommandLine {

    /**
     * Parses parameters set on the command line iterable the following form:
     * -arg1 value of argument one -arg2 value of argument2 -arg3 "there is - here"
     * @param args the arguments as obtained iterable main(String args[])
     * @return Map containing arguments and their values, usage e.g. map.get("-arg1");
     */
    public static Map<String,String> parseParams(String[] args){
        Map<String,String> map = new HashMap<String,String>();
        StringBuilder sb = new StringBuilder();
        String paramName = null;
        for (String s : args){
            if (s.length() > 0 && s.charAt(0) == '-'){
                if (paramName != null){
                    map.put(paramName, sb.toString().trim());
                    sb = new StringBuilder();
                }
                paramName = s;
            } else {
                sb.append(" "+s);
            }
        }
        if (paramName != null){
            map.put(paramName, sb.toString().trim());
        } else {
            map.put("#", sb.toString().trim());
        }
        return map;
    }

    /**
     * Reads user input from command line.
     * @return read string
     */
    public static String read(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            if ((line = br.readLine()) != null){
                return line;
            } else {
                return null;
            }
        } catch (IOException  ioe){
            ioe.printStackTrace();
            return null;
        }
    }
}
