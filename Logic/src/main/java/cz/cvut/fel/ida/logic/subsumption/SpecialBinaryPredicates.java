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
import cz.cvut.fel.ida.utils.math.StringUtils;
import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.Set;

/**
 * Created by kuzelkao_cardiff on 20/02/15.
 */
public class SpecialBinaryPredicates {

    public final static String NEQ = "@neq", LEQ = "@leq", GEQ = "@geq", LT = "@lt", GT = "@gt", EQ = "@eq", NEXT = "@next";

    public final static String ADD_EVAL = "@add_eval", SUB_EVAL = "@sub_eval", MOD_EVAL = "@mod_eval", MUL_EVAL = "@mul_eval", DIV_EVAL = "@div_eval";
    public final static String MAX_EVAL = "@max_eval", MIN_EVAL = "@min_eval";

    public final static Set<String> SPECIAL_PREDICATES = Sugar.<String>set(
        NEQ, LEQ, GEQ, LT, GT, EQ, NEXT,
        ADD_EVAL, SUB_EVAL, MOD_EVAL, MUL_EVAL, DIV_EVAL, MAX_EVAL, MIN_EVAL
    );

    private static final int addHash = "add_eval".hashCode(), subHash = "sub_eval".hashCode(), modHash = "mod_eval".hashCode(),
            maxHash = "max_eval".hashCode(), minHash = "min_eval".hashCode(), mulHash = "mul_eval".hashCode(), divHash = "div_eval".hashCode();

    private static final int atAddHash = "@add_eval".hashCode(), atSubHash = "@sub_eval".hashCode(), atModHash = "@mod_eval".hashCode(),
            atMaxHash = "@max_eval".hashCode(), atMinHash = "@min_eval".hashCode(), atMulHash = "@mul_eval".hashCode(), atDivHash = "@div_eval".hashCode();


    public static boolean isTrueGround(Literal l) {
        String predicate = l.predicateName();
        boolean ret = false;

        Comparable comparable1 = null;
        Comparable comparable2 = null;
        String str1 = l.get(0).name();
        String str2 = l.get(1).name();
        if (StringUtils.isNumeric(str1) && StringUtils.isNumeric(str2)) {
            Number n1 = StringUtils.parseNumber(str1);
            Number n2 = StringUtils.parseNumber(str2);
            if (predicate.equals(NEQ)) {
                ret = n1.doubleValue() != n2.doubleValue();
            } else if (predicate.equals(EQ)) {
                ret = n1.doubleValue() == n2.doubleValue();
            } else if (predicate.equals(SpecialBinaryPredicates.GT)) {
                ret = n1.doubleValue() > n2.doubleValue();
            } else if (predicate.equals(SpecialBinaryPredicates.GEQ)) {
                ret = n1.doubleValue() >= n2.doubleValue();
            } else if (predicate.equals(SpecialBinaryPredicates.LT)) {
                ret = n1.doubleValue() < n2.doubleValue();
            } else if (predicate.equals(SpecialBinaryPredicates.LEQ)) {
                ret = n1.doubleValue() <= n2.doubleValue();
            } else if (predicate.equals(SpecialBinaryPredicates.NEXT)) {
                ret = n1.doubleValue() + 1 == n2.doubleValue();
            }
        } else {
            comparable1 = str1;
            comparable2 = str2;
            if (predicate.equals(NEQ)) {
                ret = !l.get(0).equals(l.get(1));
            } else if (predicate.equals(EQ)) {
                ret = l.get(0).equals(l.get(1));
            } else if (predicate.equals(SpecialBinaryPredicates.GT)) {
                ret = comparable1.compareTo(comparable2) > 0;
            } else if (predicate.equals(SpecialBinaryPredicates.GEQ)) {
                ret = comparable1.compareTo(comparable2) >= 0;
            } else if (predicate.equals(SpecialBinaryPredicates.LT)) {
                ret = comparable1.compareTo(comparable2) < 0;
            } else if (predicate.equals(SpecialBinaryPredicates.LEQ)) {
                ret = comparable1.compareTo(comparable2) <= 0;
            }
        }
        if (l.isNegated()) {
            ret = !ret;
        }
        return ret;
    }

    public static boolean isEvalSpecial(String name) {
        int h = name.hashCode();
        if (h == addHash) return "add_eval".equals(name);
        if (h == divHash) return "div_eval".equals(name);
        if (h == mulHash) return "mul_eval".equals(name);
        if (h == maxHash) return "max_eval".equals(name);
        if (h == minHash) return "min_eval".equals(name);
        if (h == modHash) return "mod_eval".equals(name);
        if (h == subHash) return "sub_eval".equals(name);

        return false;
    }

    public static double getEvalValue(String name, double a, double b) {
        int h = name.hashCode();
        if (h == atAddHash && "@add_eval".equals(name)) return a + b;
        if (h == atDivHash && "@div_eval".equals(name)) return a / b;
        if (h == atMulHash && "@mul_eval".equals(name)) return a * b;
        if (h == atMaxHash && "@max_eval".equals(name)) return Math.max(a, b);
        if (h == atMinHash && "@min_eval".equals(name)) return Math.min(a, b);
        if (h == atModHash && "@mod_eval".equals(name)) return a % b;
        if (h == atSubHash && "@sub_eval".equals(name)) return a - b;

        return 0;
    }
}
