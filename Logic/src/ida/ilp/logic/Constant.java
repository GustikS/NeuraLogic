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
 * Constant.java
 *
 * Created on 30. listopad 2006, 16:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ida.ilp.logic;

import ida.utils.Cache;
import ida.utils.StringUtils;
import ida.utils.tuples.Pair;

/**
 * Class for representing logical constants. It uses caching so if there are constants with long names
 * which appear iterable many literals or even iterable many clauses, the consumed memory will not be too big.
 * 
 * @author Ondra
 */
public class Constant implements Term {

    private static int GENERIC = 1, INTEGER = 2, DOUBLE = 3;

    private int constantType = -1;

    private int intValue;

    private double doubleValue;

    private String type;

    private String name;

    private String toString;
    
    private int hashCode = Integer.MIN_VALUE;
    
    private static Cache<String,Constant> cache = new Cache<String,Constant>();

    private static Cache<Pair<String,String>,Constant> cache2 = new Cache<Pair<String,String>,Constant>();
    
    /** Creates a new instance of Constant */
    private Constant(String name) {
        this.name = name.trim().intern();
        if (StringUtils.isNumeric(this.name())){
            if (StringUtils.isInteger(this.name())){
                this.intValue = Integer.parseInt(this.name());
                this.constantType = INTEGER;
            } else if (StringUtils.isDouble(this.name())){
                this.doubleValue = Double.parseDouble(this.name());
                this.constantType = DOUBLE;
            }
        } else {
            this.constantType = GENERIC;
        }
    }

    private Constant(String name, String type){
        this(name);
        this.type = type;
        this.toString = type+":"+name;
    }

    /**
     * Creates an instance of class Constant. before constructing a new instance,
     * it checks if it had not been constructed yet - if it had been constructed then
     * it returns the old instance, otherwise it returns a new instance.
     * @param name
     * @return
     */
    public static Constant construct(String name){
        Constant retVal = cache.get(name);
        if (retVal == null) {
            retVal = new Constant(name);
            cache.put(name, retVal);
        }
        return retVal;
    }

    public static Constant construct(String name, String type){
        if (type == null){
            return construct(name);
        } else {
            Pair<String, String> queryPair = new Pair<String, String>(name, type);
            Constant retVal = cache2.get(queryPair);
            if (retVal == null) {
                retVal = new Constant(name, type);
                cache2.put(queryPair, retVal);
            }
            return retVal;
        }
    }
    
    /**
     * 
     * @return string representation of the constant
     */
    public String name(){
        return this.name;
    }

    public String type(){
        return this.type;
    }
    
    @Override
    public String toString(){
        if (this.type == null) {
            return name;
        } else {
            return this.toString;
        }
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Constant){
            Constant c = (Constant)o;
            return o == this ||
                    (c.name.equals(this.name) &&
                        !((c.type == null && this.type != null) || (c.type != null && this.type == null)) &&
                        (c.type == this.type || c.type.equals(this.type)));
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        if (hashCode != Integer.MIN_VALUE)
            return hashCode;
        return this.type == null ? (hashCode = name.hashCode()) : (hashCode = toString.hashCode());
    }
    
    /**
     * Clears the cache of Constants - the cache is implemented using soft-references so
     * it is not necessary to call this method manually but it may be useful at some occasions.
     */
    public static void clearCache(){
        cache.clear();
        cache2.clear();
    }

    public boolean isInteger(){
        return this.constantType == INTEGER;
    }

    public boolean isDouble(){
        return this.constantType == DOUBLE;
    }

    public boolean isNumeric(){
        return isInteger() || isDouble();
    }

    public int intValue(){
        return this.intValue;
    }

    public double doubleValue(){
        return this.doubleValue;
    }


}
