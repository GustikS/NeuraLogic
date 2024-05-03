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
 * Variable.java
 *
 * Created on 30. listopad 2006, 16:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic;

import cz.cvut.fel.ida.utils.math.Cache;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

/**
 * Class for representing first-order-logic variables.
 *
 * @author Ondra
 */
public class Variable implements Term {

    private String type;

    private String name;

    private String toString;

    private int indexWithinSubstitution = -1;

    private int hashCode = Integer.MIN_VALUE;

    private static Cache<String, Variable> cache = new Cache<String, Variable>();

    private static Cache<Pair<String, String>, Variable> cache2 = new Cache<Pair<String, String>, Variable>();

    /**
     * Creates a new instance of Variable
     */
    protected Variable(String name) {
        this.name = name.trim().intern();
        this.toString = this.name;
    }

    private Variable(String name, String type) {
        this(name);
        this.type = type;
        this.toString = type + ":" + name;
    }

    /**
     * Creates a new variable - it uses caching so that variables of the same name would be
     * represented by one object.
     *
     * @param name name of the variable to be constructed
     * @return constructed variable (either cached or created as new)
     */
    public static Variable construct(String name) {
        Variable retVal = cache.get(name);
        if (retVal == null) {
            retVal = new Variable(name);
            cache.put(name, retVal);
        }
        return retVal;
    }

    public static Variable construct(String name, String type) {
        if (type == null) {
            return construct(name);
        } else {
            Pair<String, String> queryPair = new Pair<String, String>(name, type);
            Variable retVal = cache2.get(queryPair);
            if (retVal == null) {
                retVal = new Variable(name, type);
                cache2.put(queryPair, retVal);
            }
            return retVal;
        }
    }

    public String name() {
        return this.name;
    }

    public String type() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.toString;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Variable) {
            Variable v = (Variable) o;
            return o == this ||
                    (v.name.equals(this.name) &&
                            !((v.type == null && this.type != null) || (v.type != null && this.type == null)) &&
                            (v.type == this.type || v.type.equals(this.type)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode != Integer.MIN_VALUE)
            return hashCode;
        return this.type == null ? (hashCode = name.hashCode()) : (hashCode = toString.hashCode());
    }

    /**
     * Clears the cache of variables. This method does not have to be called because
     * the caching mechanism uses soft-references therefore the cache should be cleared
     * automatically by the garbage collector if neccessary.
     */
    public static void clearCache() {
        cache.clear();
        cache2.clear();
    }

    @Override
    public int getIndexWithinSubstitution() {
        return indexWithinSubstitution;
    }

    @Override
    public void setIndexWithinSubstitution(int indexWithinSubstitution) {
        this.indexWithinSubstitution = indexWithinSubstitution;
    }
}
