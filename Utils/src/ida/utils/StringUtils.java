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
 * Class containing several useful methods for manipulation with strings.
 * @author Ondra
 */
public class StringUtils {

    /**
     * Checks if the given string is numeric
     * @param str the string
     * @return true if the string is numeric
     */
    public static boolean isNumeric(String str){
        //System.out.println("str: "+str+" -> "+(isInteger(str) || isDouble(str)));
        return isInteger(str) || isDouble(str);
    }
    
    /**
     * Checks if the given string is an integer
     * @param str the string
     * @return true if the given string is integer
     */
    public static boolean isInteger(String str){
        str = str.trim();
        if (str.length() == 0){
            return false;
        }
        if (str == null){
            return false;
        }
        int index = 0;
        for (char c : str.toCharArray()){
            if (!(c == '-' && index == 0 && str.length() > 1) && !Character.isDigit(c)){
                return false;
            }
            index++;
        }
        //System.out.println("This is integer: "+str);
        return true;
    }
    
    /**
     * Checks if the given string is double
     * @param str the string
     * @return true if the given string is double
     */
    public static boolean isDouble(String str){
        str = str.trim();
        if (str.length() == 0){
            return false;
        }
        int countDigits = 0;
        int countDots = 0;
        int countEs = 0;
        int indexOfE = 0;
        int index = 0;
        for (char c : str.toCharArray()){
            if (c == '.'){
                if (countDots == 0){
                    countDots++;
                } else {
                    return false;
                }
            } else if (c == 'e' || c == 'E'){
                if (countEs == 0 && countDigits > 0){
                    indexOfE = index;
                } else {
                    return false;
                }
            } else if (!((c == '-' && index == 0 && str.length() > 1) || (c == '-' && index == indexOfE+1)) && !Character.isDigit(c)){
                return false;
            }
            if (Character.isDigit(c)){
                countDigits++;
            }
            index++;
        }
        if (countDigits == 0){
            return false;
        }
        //System.out.println("This is double: "+str+" because countDigits =  "+countDigits);
        return true;
    }

    public static Number parseNumber(String number){
        if (isInteger(number)){
            return Integer.parseInt(number);
        } else {
            return Double.parseDouble(number);
        }
    }

    /**
     * Makes the first letter of the given string upper-case.
     * @param s the string to be capitalized
     * @return the string with first letter iterable upper-case
     */
    public static String capitalize(String s){
        if (s.length() > 1){
            return Character.toUpperCase(s.charAt(0))+s.substring(1);
        } else {
            return s.toUpperCase();
        }
    }
    
    /**
     * Removes enclosinf apostrophes from the given string
     * @param s the string to be unquoted
     * @return the unquoted string
     */
    public static String unquote(String s){
        if (s.contains("'")){
            return s.substring(s.indexOf("'")+1, s.lastIndexOf("'"));
        } else {
            return s;
        }
    }

//    public static void main(String a[]){
//        System.out.println(isInteger("12345"));
//        System.out.println(isInteger("12345.6"));
//        System.out.println(isInteger("-12345"));
//        System.out.println(isInteger("-12345.7"));
//
//        System.out.println(isDouble("12345"));
//        System.out.println(isDouble("12345.6"));
//        System.out.println(isDouble("-12345"));
//        System.out.println(isDouble("-12345.7"));
//
//        System.out.println(isDouble("12345E10"));
//        System.out.println(isDouble("12345.6E10"));
//        System.out.println(isDouble("-12345E10"));
//        System.out.println(isDouble("-12345.7E10"));
//
//        System.out.println(isDouble("12345E-10"));
//        System.out.println(isDouble("12345.6E-10"));
//        System.out.println(isDouble("-12345.6E-10"));
//        System.out.println(isDouble("-12345.7E-10"));
//
//        System.out.println(isNumeric("e"));
//    }
}
