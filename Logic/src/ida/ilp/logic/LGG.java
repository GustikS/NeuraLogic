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

import ida.utils.Sugar;
import ida.utils.tuples.Pair;

import java.util.*;
/**
 * Class for computing least general generalizations of clauses.
 * 
 * @author admin
 */
public class LGG {

    private TermLGG termLGG = new BasicTermLGG();

    private LiteralLGG literalLGG = new BasicLiteralLGG();
    
    private List<LiteralFilter> literalFilters = Sugar.<LiteralFilter>list(new BasicLiteralFilter());
    
    private List<LiteralPrunning> literalPrunnings = new ArrayList<LiteralPrunning>();
    
    /**
     * Computes least general generalization of clauses given as input. It uses Plotkin's algorithm
     * @param clauses an array of clauses
     * @return a clause which is a least general generalization of the clauses given
     * on input
     */
    public Clause lgg(Clause ...clauses){
        Clause c = clauses[0];
        for (int i = 1; i < clauses.length; i++){
            c = lgg(c, clauses[i]);
        }
        return c;
    }

    /**
     * Computes least general generalization of two clauses a and b given as inputs.
     * It uses Plotkin's algorithm.
     * 
     * @param a first clause
     * @param b second clause
     * @return a least general generalization of clauses a, b
     */
    public Clause lgg(Clause a, Clause b){
        return new Lgg().lgg(a,b);
    }

    /**
     * Sets the literal filter. Literal filter is used to filter literals which can
     * appear iterable the result of lgg - it allows us to use a sort of language bias.
     * @param literalFilter the literalFilter to set
     */
    public void addLiteralFilter(LiteralFilter literalFilter) {
        this.literalFilters.add(literalFilter);
    }
    
    /**
     * 
     * @return the literal filter
     */
    public List<LiteralFilter> getLiteralFilters(){
        return this.literalFilters;
    }

    public void addLiteralPrunning(LiteralPrunning literalPrunning){
        this.literalPrunnings.add(literalPrunning);
    }
    
    public List<LiteralPrunning> getLiteralPrunnings(){
        return this.literalPrunnings;
    }
    
    /**
     * @return the termLGG
     */
    public TermLGG getTermLGG() {
        return termLGG;
    }

    /**
     * Sets the TermLGG.
     * @param termLGG the termLGG
     */
    public void setTermLGG(TermLGG termLGG) {
        this.termLGG = termLGG;
    }

    /**
     * @return the literalLGG
     */
    public LiteralLGG getLiteralLGG() {
        return literalLGG;
    }

    /**
     * Sets the LiteralLGG.
     * @param literalLGG the literalLGG 
     */
    public void setLiteralLGG(LiteralLGG literalLGG) {
        this.literalLGG = literalLGG;
    }
    
    private class Lgg {

        public Clause lgg(Clause a, Clause b){
            List<Literal> literals = new ArrayList<Literal>();
            TermLGG tlgg = getTermLGG().constructNew(Sugar.setFromCollections(a.terms(), b.terms()));
            for (Literal litA : a.literals()){
                middleLoop: for (Literal litB : b.literals()){
                    Literal lLGG = getLiteralLGG().lgg(litA, litB, tlgg);
                    if ((lLGG != null)){
                        for (LiteralFilter literalFilter : literalFilters){
                            if (!literalFilter.filter(lLGG)){
                                continue middleLoop;
                            }
                        }
                        literals.add(lLGG);
                    }
                }
            }
            Clause retVal = new Clause(literals);
            for (LiteralPrunning literalPrunning : literalPrunnings){
                retVal = literalPrunning.prune(retVal);
            }
            return retVal;
        }
    }

    /**
     * Interface used to compute LGGs of literals (e.g. according to a language bias).
     */
    public static interface LiteralLGG {

        /**
         *  Computes LGG of given literals a and b
         *
         * @param a
         * @param b
         * @param c term LGG used to generalize terms iterable the literals
         * @return
         */
        public Literal lgg(Literal a, Literal b, TermLGG c);

    }
    
    /**
     * Interface used for objects which can filter literals (e.g. according to a language bias).
     */
    public static interface LiteralFilter {
        
        /**
         * 
         * @param literal literal to be filtered
         * @return true if the literal should be included iterable the result of LGG,
         * false otherwise
         */
        public boolean filter(Literal literal);
        
    }
    
    public static interface LiteralPrunning {
        
        public Clause prune(Clause c);
        
    }
    
    /**
     * An all-pass LiteralFilter.
     */
    public static class BasicLiteralFilter implements LiteralFilter{

        @Override
        public boolean filter(Literal literal) {
            return true;
        }
        
    }
    
    /**
     * An interface that allows us to customize computation of LGGs. 
     * It defines method  termLGG(Term a, Term b) which computes LGGs of two terms.
     * It is possible to use this method to exploit taxonomical information. For example,
     * it is possible to implement the method so that termLGG(dog, mamal) = mamal
     * and not termLGG(dog, mamal) = X where X is a variable.
     */
    public static interface TermLGG {
        
        /**
         * Constructs a new instance of TermLGG class. This is called always when LGG
         * of a new pair of clauses is being computed.
         * @param termsInClauses terms iterable the generalized clauses
         * @return new instance of class TermLGG
         */
        public TermLGG constructNew(Set<Term> termsInClauses);
        
        /**
         * Computes LGG of terms a and b. It is guaranteed that this method will be called
         * only for computing LGG of two fixed clauses.
         * @param a
         * @param b
         * @return
         */
        public Term termLGG(Term a, Term b, String predicate, int arity, int argument);
        
    }
    
    /**
     * Basic implementation of interface TermLGG - it works as specified iterable Plotkin's LGG
     * algorithm.
     */
    public static class BasicTermLGG implements TermLGG {
        
        private int variableIndex = 0;

        private Map<Pair<Term,Term>,Variable> usedVariables = new HashMap<Pair<Term,Term>,Variable>();

        private Set<Term> terms;
        
        /**
         * Constructs a bew instance of class BasicTermLGG
         */
        public BasicTermLGG(){}
        
        /**
         * Constructs a new instance of class BasicTermLGG.
         * @param terms the set of terms which can appear iterable the clauses that should be generalized.
         */
        public BasicTermLGG(Set<Term> terms){
            this.terms = terms;
        }
        
        /**
         * Computes LGG of terms as specified iterable the Plotkin's algorithm for computing LGGs.
         * @param a a term
         * @param b a term
         * @return a if a == b or their generalization (which is a variable) if a != b
         */
        @Override
        public Term termLGG(Term a, Term b, String predicate, int arity, int argument){
            if (a instanceof Function && b instanceof Function){
                return functionLGG((Function)a,(Function)b);
            } else if (a instanceof PrologList && b instanceof PrologList){
                throw new UnsupportedOperationException("Lists not supported yet");
            } else {
                return otherLGG(a,b);
            }
        }

        /**
         * Computes LGG of function symbols.
         * @param a
         * @param b
         * @return LGG of function symbols a and b as specified by Plotkin.
         */
        private Term functionLGG(Function a, Function b){
            if (a.name().equals(b.name()) && a.arity() == b.arity()){
                Function c = new Function(a.name(), a.arity());
                for (int i = 0; i < c.arity(); i++){
                    c.set(termLGG(a.get(i), b.get(i), c.name(), c.arity(), i), i);
                }
                return c;
            } else {
                return newVariable(a, b);
            }
        }

        private Term otherLGG(Term a, Term b){
            if (a instanceof Constant && b instanceof Constant && a.equals(b)){
                return a;
            } else {
                return newVariable(a, b);
            }
        }

        private Variable newVariable(Term a, Term b){
            Pair<Term,Term> pa = new Pair<Term,Term>(a,b);
            if (usedVariables.containsKey(pa)){
                return usedVariables.get(pa);
            }
            do {
                variableIndex++;
            } while (terms.contains(Variable.construct("V"+variableIndex)));
            Variable newVariable = Variable.construct("V"+variableIndex);
            usedVariables.put(new Pair<Term,Term>(a,b), newVariable);
            //usedVariables.put(new Pair<Term,Term>(b,a), newVariable);
            terms.add(newVariable);
            return newVariable;
        }
        
        @Override
        public TermLGG constructNew(Set<Term> termsUsedInClauses) {
            return new BasicTermLGG(termsUsedInClauses);
        }
        
    }

    public static class BasicLiteralLGG implements LiteralLGG{

        /**
         * Constructs a new instance of class BasicLiteralLGG
         */
        public BasicLiteralLGG(){}

        @Override
        public Literal lgg(Literal litA, Literal litB, TermLGG tlgg) {

            Literal c = null;

            if (litA.predicateName().equals(litB.predicateName()) && litA.arity() == litB.arity()) {
                c = new Literal(litA.predicateName(), litA.arity());
                for (int i = 0; i < c.arity(); i++) {
                    Term ta = litA.get(i);
                    Term tb = litB.get(i);
                    if (ta instanceof Term && tb instanceof Term) {
                        c.set(tlgg.termLGG(ta, tb, c.predicateName(), c.arity(), i), i);
                    }
                }
            }

            return c;
        }
    }

    public static class TemplateBasedLiteralFilter implements LGG.LiteralFilter {

        private final Pair<String,Integer> query = new Pair<String,Integer>();

        private Map<Pair<String,Integer>,boolean[]> cannotBeVariableMap = new HashMap<Pair<String,Integer>,boolean[]>();

        public TemplateBasedLiteralFilter(String template){
            Pair<String,Integer> queryPair = new Pair<String,Integer>();
            for (Literal l : Clause.parse(template).literals()){
                if (!isGlobalConstant(l)){
                    boolean[] cannotBeVariable = null;
                    queryPair.set(l.predicateName(), l.arity());
                    if (cannotBeVariableMap.containsKey(queryPair)){
                        cannotBeVariable = cannotBeVariableMap.get(queryPair);
                    } else {
                        cannotBeVariable = new boolean[l.arity()];
                        Arrays.fill(cannotBeVariable, true);
                        cannotBeVariableMap.put(new Pair<String,Integer>(l.predicateName(), l.arity()), cannotBeVariable);
                    }
                    for (int i = 0; i < l.arity(); i++){
                        if (!l.get(i).name().startsWith("#")){
                            cannotBeVariable[i] = false;
                        }
                        if (l.get(i).name().startsWith("$")){
                            cannotBeVariable[i] = true;
                        }
                    }
                }

            }
        }

        private boolean isGlobalConstant(Literal l){
            for (int i = 0; i < l.arity(); i++){
                if (l.get(i).name().startsWith("@")){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean filter(Literal literal) {
            query.set(literal.predicateName(), literal.arity());
            boolean[] cannotBeVariable = cannotBeVariableMap.get(query);
            //template specifies not only where constants must appear but also the set of allowed predicateNames
            if (cannotBeVariable == null){
                return false;
            }
            for (int i = 0; i < literal.arity(); i++){
                if (cannotBeVariable[i] && literal.get(i) instanceof Variable){
                    return false;
                }
            }
            return true;
        }
    }
}
