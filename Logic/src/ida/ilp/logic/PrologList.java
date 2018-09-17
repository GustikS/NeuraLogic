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

import ida.utils.UniqueIDs;
import ida.utils.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Class representing Prolog lists.
 * 
 * @author Ondra
 */
public class PrologList implements Term {
    
    private int hashCode = -1;
    
    private Term items[];
    
    private String toString;
    
    /**
     * Creates a new instance of class PrologList
     * @param itemsCount number of elements iterable the list
     */
    public PrologList(int itemsCount){
        this.items = new Term[itemsCount];
    }
    
    /**
     * Creates a new instance of class PrologList
     * @param termList list of terms iterable the list
     */
    public PrologList(List<Term> termList){
        this(termList.size());
        int index = 0;
        for (Term t : termList){
            this.items[index] = t;
            index++;
        }
    }
    
    /**
     * 
     * @param index
     * @return element of the list at position index
     */
    public Term get(int index){
        return this.items[index];
    }
    
    /**
     * Sets the element at position index iterable the list
     * @param term the term to be placed at positin index
     * @param index the position where the term should be placed
     */
    public void set(Term term, int index){
        this.items[index] = term;
    }
    
    /**
     * 
     * @return number of items iterable this list
     */
    public int countItems(){
        return this.items.length;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof PrologList){
            PrologList pl = (PrologList)o;
            return pl.toString().equals(this.toString());
        }
        return false;
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
        if (this.toString == null){
            this.toString = toString(this.items, null);
        }
        return this.toString;
    }

    private String toString(Term[] items, String type){
        StringBuilder sb = new StringBuilder();
        if (type != null){
            sb.append(type+":");
        }
        sb.append("[");
        for (int i = 0; i < items.length; i++){
            sb.append(items[i]);
            if (i < items.length-1){
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @return string representation of this list
     */
    public String name() {
        return this.toString(this.items, null);
    }

    public String type(){
        return null;
        //return this.type;
    }

    /**
     * Creates a flattened version of the list.
     * @return flattened version of the list.
     */
    public PrologList flatten(){
        List<Term> termList = new ArrayList<Term>();
        flatten(this, termList);
        return new PrologList(termList);
    }
    
    private void flatten(PrologList prologList, List<Term> termList){
        for (Term t : prologList.items){
            if (t instanceof PrologList){
                flatten((PrologList)t, termList);
            } else {
                termList.add(t);
            }
        }
    }
    
    /**
     * Parses the list from its string representation.
     * @param str the string representation of the list
     * @param variables Map of used variables - it does not have to contain the variables contained iterable the arguments of the list,
     * if they are not there, they will be added automatically
     * @param constants Map of used constants - it does not have to contain the constants contained iterable the arguments of the list,
     * if they are not there, they will be added automatically
     * @return
     */
    public static PrologList parseList(String str, Map<Variable,Variable> variables, Map<Constant,Constant> constants){
        char[] c = str.toCharArray();
//        String type = null;
        int index = 0;
        while (c[index++] != '[');
//        if (index > 1){
//            type = new String(c, 0, index-2);
//        }
        ArrayList<Term> items = new ArrayList<Term>();
        while (true){
            if (index >= c.length || c[index] == ']'){
                break;
            } else if (c[index] == ' '){
                //do nothing
            } else {
                Pair<Term,Integer> pair = ParserUtils.parseTerm(c, index, ']', variables, constants);
                items.add(pair.r);
                index = pair.s;
            }
            index++;
        }
        PrologList retVal = new PrologList(items.size());
        for (int i = 0; i < items.size(); i++){
            retVal.set(items.get(i), i);
        }
        return retVal;
    }

}
