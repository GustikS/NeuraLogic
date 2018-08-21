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

package ida.ilp.logic;

import java.util.Map;

/**
 *  Class for representing logical function symbols. 
 * @author Ondra
 */
public class Function implements Term {
    
    private int hashCode = -1;
    
    private String toString;
    
    private Term[] arguments;
    
    private String functionName;
    
    /**
     * Creates a new instance of class Function. It gets a name and arity and constructs a Function/
     * For example, when name = "foo" and arity = 3, it creates function foo(..., ..., ...)
     * 
     * @param functionName name of the function symbol
     * @param arity arity of the function symbol
     */
    public Function(String functionName, int arity){
        this.functionName = functionName;
        this.arguments = new Term[arity];
    }

    /**
     * Creates a new instance of class Function. It gets a name and an array of arguments
     * and constructs a function. For example, when name = "foo" and arguments = a,b,c then
     * the result is function foo(a,b,c)
     * 
     * @param functionName name of the function symbol
     * @param arguments arguments of the function symbol
     */
    public Function(String functionName, Term ...arguments){
        this(functionName, arguments.length);
        for (int i = 0; i < arguments.length; i++){
            set(arguments[i], i);
        }
    }

    /**
     * 
     * @param index index of the argument
     * @return term iterable the argument at position "index"
     */
    public Term get(int index){
        return this.arguments[index];
    }
    
    /**
     * Sets the argument at position "index"
     * @param term the new argument
     * @param index the index of the argument
     */
    public void set(Term term, int index){
        this.arguments[index] = term;
        this.toString = null;
        this.hashCode = -1;
    }
    
    /**
     * 
     * @return function symbol's name
     */
    public String name() {
        return this.functionName;
    }

    public String type(){
        return null;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Function){
            Function f = (Function)o;
            return this.toString().equals(f.toString());
        }
        return false;
    }
    
    /**
     * 
     * @return arity of the function symbol
     */
    public int arity(){
        return this.arguments.length;
    }
    
    @Override
    public int hashCode(){
        if (this.hashCode == -1){
            this.hashCode = this.toString().hashCode();
        }
        return this.hashCode;
    }
    
    @Override
    public String toString(){
        if (toString == null){
            StringBuffer sb = new StringBuffer();
            sb.append(functionName);
            sb.append("(");
            for (int i = 0; i < arguments.length; i++){
                sb.append(arguments[i].toString());
                if (i < arguments.length-1){
                    sb.append(", ");
                }
            }
            sb.append(")");
            this.toString = sb.toString();
        }
        return toString;
    }

    /**
     * Converts Function into Literal (note that literals and functions both have the same structure - a "name" followed by arguments)
     * @return the literal with the same name and the same arguments as the Function
     */
    public Literal toLiteral(){
        String name = this.functionName;
        boolean negated = name.startsWith("!");
        if (negated){
            name = name.substring(1);
        }
        Literal l = new Literal(name, negated, this.arguments.length);
        for (int i = 0; i < this.arguments.length; i++){
            l.set(this.arguments[i], i);
        }
        return l;
    }

    /**
     * Parses a function from its string representation.
     * 
     * @param str string representation of the function symbol
     * @param variables Map of used variables - it does not have to contain the variables contained iterable the arguments of the function symbol,
     * if they are not there, they will be added automatically
     * @param constants Map of used constants - it does not have to contain the constants contained iterable the arguments of the function symbol,
     * if they are not there, they will be added automatically
     * @return a new instance of class Function corresponding to the string representation given on input
     */
    public static Function parseFunction(String str, Map<Variable,Variable> variables, Map<Constant,Constant> constants){
        //literals and functions have the same syntax
        Literal fLit = Literal.parseLiteral(str, variables, constants);
        Function f = new Function(fLit.predicateName(), fLit.arity());
        for (int i = 0; i < fLit.arity(); i++){
            f.set(fLit.get(i), i);
        }
        return f;
    }
}
