/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cz.cvut.fel.ida.logic.subsumption;

import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.utils.math.StringUtils;
import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kuzelkao_cardiff on 23/02/15.
 */
public class SpecialVarargPredicates {

    /**
     * ANYPRED cannot be used in TheorySolver (so far)!
     */
    public final static String ALLDIFF = "@alldiff", ANYPRED = "@anypred", TRUE = "@true", IN = "@in", MAX_CARD = "@maxcard", FALSE = "@false";
    public final static String ADD = "@add", SUB = "@sub", MOD = "@mod";

    public final static Set<String> SPECIAL_PREDICATES = Sugar.<String>set(ALLDIFF, ANYPRED, TRUE, IN, MAX_CARD, FALSE, ADD, SUB, MOD);

    public static Boolean isTrueGround(Literal l){
        if (l.predicateName().equals(ALLDIFF)) {
            Set<Term> elements = new HashSet<Term>();
            for (int i = 0; i < l.arity(); i++) {
                if (elements.contains(l.get(i))) {
                    return l.isNegated() ? true : false;
                }
                elements.add(l.get(i));
            }
            return l.isNegated() ? false : true;
        } else if (l.predicateName().equals(TRUE)){
            return true;
        } else if (l.predicateName().equals(FALSE)){
            return false;
        }else if (l.predicateName().equals(ANYPRED)) {
            return null;
        } else if (l.predicateName().equals(IN)){
            Term first = l.get(0);
            for (int i = 1; i < l.arity(); i++){
                if (l.get(i).equals(first)){
                    return true;
                }
            }
            return false;
        } else if (l.predicateName().equals(MAX_CARD)){
            Term first = l.get(0);
            Number cardinality;
            if (StringUtils.isNumeric(first.name())){
                cardinality = Integer.parseInt(first.name());
            } else {
                return false;
            }
            Set<Term> terms = new HashSet<Term>();
            for (int i = 1; i < l.arity(); i++){
                terms.add(l.get(i));
                if (terms.size() > cardinality.intValue()){
                    return false;
                }
            }
            return true;
        } else {
            throw new IllegalArgumentException("Something went terribly wrong...");
        }
    }

}
