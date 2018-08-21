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

package ida.ilp.logic.subsumption;

import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Term;
import ida.utils.Sugar;
import ida.utils.VectorUtils;
import ida.utils.collections.Counters;
import ida.utils.tuples.Pair;

import java.util.*;
/**
 * Class providing some useful methods for computing theta-subsumption.
 * 
 * @author admin
 */
public class SubsumptionUtils {

    /**
     * Computes neighbours of given iteral iterable a given clause (neighbours are literals
     * which share with the given literal at least one term - beware this can result iterable
     * unexpected results when one forgets that neighbours may be sometimes connected 
     * just by constants and not variables).
     * @param l the literal for which we want to get the neighbours
     * @param c the clause iterable which we want to find the neighbours
     * @return set of literals which are neighbours of the given literal.
     */
    public static Set<Literal> neighbours(Literal l, Clause c){
        Set<Literal> retVal = new HashSet<Literal>();
        for (int i = 0; i < l.arity(); i++){
            for (Literal neighb : c.getLiteralsByTerm(l.get(i))){
                if (!l.equals(neighb)){
                    retVal.add(neighb);
                }
            }
        }
        return retVal;
    }

    /**
     * Computes GYO-decomposition of the given clause.
     * @param c the clause for which we want to find the decomposition
     * @return pair: cyclic part of the clause (which cannot be decomposed) and decomposition of the clause iterable the form of joins (represented as pairs of literals)
     */
    public static Pair<Clause,List<Pair<Literal,Literal>>> gyoDecomposition(Clause c){
        List<Pair<Literal,Literal>> joins = new ArrayList<Pair<Literal,Literal>>();
        Map<Literal,boolean[]> hypergraph = new HashMap<Literal,boolean[]>();
        Map<Literal,boolean[]> suspected = new HashMap<Literal,boolean[]>();
        Counters<Term> counters = new Counters<Term>();
        for (Literal l : c.literals()){
            hypergraph.put(l, VectorUtils.trues(l.arity()));
            suspected.put(l, VectorUtils.trues(l.arity()));
            for (Term t : l.terms()){
                counters.increment(t);
            }
        }
        Set<Literal> used = new HashSet<Literal>();
        whileLoop: while (suspected.size() > 0){
            Map.Entry<Literal,boolean[]> s = Sugar.removeOne(suspected);
            for (int i = 0; i < s.getKey().arity(); i++){
                if (counters.get(s.getKey().get(i)) == 1){
                    s.getValue()[i] = false;
                }
            }
            Set<Literal> neighbours = new HashSet<Literal>();
            for (int i = 0; i < s.getKey().arity(); i++){
                neighbours.addAll(c.getLiteralsByTerm(s.getKey().get(i)));
            }
            for (Literal testLiteral : neighbours){
                if (!s.getKey().equals(testLiteral) && !used.contains(testLiteral) && aIsInB(s.getKey(), testLiteral, s.getValue())){
                    used.add(s.getKey());
                    joins.add(new Pair<Literal,Literal>(s.getKey(), testLiteral));
                    for (Term t : commonTerms(s.getKey(), testLiteral, s.getValue())){
                        if (counters.decrementPre(t) == 1){
                            suspected.put(testLiteral, hypergraph.get(testLiteral));
                        }
                    }
                    continue whileLoop;
                }
            }
        }
        if (used.size() == c.countLiterals()-1){
            Literal root = null;
            for (Literal l : c.literals()){
                if (!used.contains(l)){
                    root = l;
                    break;
                }
            }
            used.add(root);
            joins.add(new Pair<Literal,Literal>(root, null));
        }
        return new Pair<Clause,List<Pair<Literal,Literal>>>(new Clause(Sugar.collectionDifference(c.literals(), used)), joins);
    }

    /**
     * Computes GYO-decomposition of the given clause.
     * @param c the clause for which we want to find the decomposition
     * @return decomposition of the clause iterable the form of joins (represented as pairs of literals)
     */
    public static List<Pair<Literal,Literal>> gyo(Clause c){
        List<Pair<Literal,Literal>> joins = new ArrayList<Pair<Literal,Literal>>();
        Map<Literal,boolean[]> hypergraph = new HashMap<Literal,boolean[]>();
        Map<Literal,boolean[]> suspected = new HashMap<Literal,boolean[]>();
        Counters<Term> counters = new Counters<Term>();
        for (Literal l : c.literals()){
            hypergraph.put(l, VectorUtils.trues(l.arity()));
            suspected.put(l, VectorUtils.trues(l.arity()));
            for (Term t : l.terms()){
                counters.increment(t);
            }
        }
        Set<Literal> used = new HashSet<Literal>();
        whileLoop: while (suspected.size() > 0){
            Map.Entry<Literal,boolean[]> s = Sugar.removeOne(suspected);
            for (int i = 0; i < s.getKey().arity(); i++){
                if (counters.get(s.getKey().get(i)) == 1){
                    s.getValue()[i] = false;
                }
            }
            Set<Literal> neighbours = new HashSet<Literal>();
            for (int i = 0; i < s.getKey().arity(); i++){
                neighbours.addAll(c.getLiteralsByTerm(s.getKey().get(i)));
            }
            for (Literal testLiteral : neighbours){
                if (!s.getKey().equals(testLiteral) && !used.contains(testLiteral) && aIsInB(s.getKey(), testLiteral, s.getValue())){
                    used.add(s.getKey());
                    joins.add(new Pair<Literal,Literal>(s.getKey(), testLiteral));
                    for (Term t : commonTerms(s.getKey(), testLiteral, s.getValue())){
                        if (counters.decrementPre(t) == 1){
                            suspected.put(testLiteral, hypergraph.get(testLiteral));
                        }
                    }
                    continue whileLoop;
                }
            }
        }
        if (used.size() < c.countLiterals()-1){
            return null;
        }
        return joins;
    }

    private static Set<Term> commonTerms(Literal a, Literal b, boolean[] maskA){
        Set<Term> setA = new HashSet<Term>();
        Set<Term> setB = b.terms();
        for (int i = 0; i < a.arity(); i++){
            if (maskA[i] && setB.contains(a.get(i))){
                setA.add(a.get(i));
            }
        }
        return setA;
    }

    private static boolean aIsInB(Literal a, Literal b, boolean[] maskA){
        Set<Term> setA = new HashSet<Term>();
        for (int i = 0; i < a.arity(); i++){
            if (maskA[i]){
                setA.add(a.get(i));
            }
        }
        return Sugar.isSubsetOf(setA, b.terms());
    }

//    public static void main(String args[]){
//        //Clause c = Clause.parsePrologLikeClause("a(A,B,C), b(A,E,F), c(C,D,E), d(A,C,E), e(E,X,Y), f(X,Z), g(Z,AA), f(AA,BB), g(BB,CC), h(CC,AA), i(CC,DD), j(DD,EE)");
//        //Clause c = Clause.parsePrologLikeClause("lit1(C3, C3), lit1(C0, C4), lit1(C2, C3), lit0(C2, C0), lit1(C0, C1)");
//        Clause c = LogicUtils.variabilizeClause(Clause.parsePrologLikeClause("a(a,b), b(c,b), c(b,d), d(e,f), e(g,f), f(f,h), g(h,x,i), h(d,x,j), i(i,k), j(k,l), k(l,j)"));
//        Pair<Clause,List<Pair<Literal,Literal>>> cut = gyoDecomposition(c);
//        System.out.println("cyclic: "+cut.r);
//        for (Pair<Literal,Literal> ac : cut.s){
//            System.out.println("acyclic: "+ac);
//        }
//        System.out.println();
//    }
}

