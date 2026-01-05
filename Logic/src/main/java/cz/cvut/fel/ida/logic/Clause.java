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
 * Clause.java
 *
 * Created on 30. listopad 2006, 16:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic;

import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.util.*;

/**
 * Class for storing sets of positive first-order-logic literals. There is only one copy of each literal in in any Clause.
 */
public class Clause {

    private Set<Literal> literals;

    private MultiMap<String, Literal> literalsByName;
    private MultiMap<Term, Literal> literalsByTerms;

    private int hashCode = -1;

    private Set<Variable> variablesCache;
    private Set<Term> termsCache;
    private Set<String> predicateCache;


    public Clause() {
    }

    public Clause(Collection<? extends Literal> literals) {
        this.literals = new HashSet<>(literals);
        for (Literal l : literals) {
            l.allowModifications(false);
        }
    }

    public Clause(Collection<? extends Literal> literals1, Collection<? extends Literal> literals2) {
        this.literals = new HashSet<>(literals1);
        this.literals.addAll(literals2);
        for (Literal l : this.literals) {
            l.allowModifications(false);
        }
    }

    public Clause(Literal... literals) {
        this.literals = new HashSet<>(List.of(literals));
        for (Literal literal : literals) {
            literal.allowModifications(false);
        }
    }

    public void addLiterals(Collection<Literal> c) {
        this.hashCode = -1;
        this.literals.addAll(c);

        this.literalsByName = null;
        this.literalsByTerms = null;

        this.variablesCache = null;
        this.termsCache = null;
        this.predicateCache = null;

        for (Literal literal : c) {
            literal.allowModifications(false);
        }
    }

    public void addLiteral(Literal literal) {
        this.hashCode = -1;
        this.variablesCache = null;
        this.termsCache = null;

        this.literals.add(literal);
        if (this.literalsByName != null) {
            this.literalsByName.put(literal.predicateName(), literal);
        }
        if (this.literalsByTerms != null) {
            for (int i = 0; i < literal.arity(); i++) {
                this.literalsByTerms.put(literal.get(i), literal);
            }
        }
    }

    /**
     * Removes literal
     *
     * @param literal the literal to be removed.
     */
    public void removeLiteral(Literal literal) {
        this.hashCode = -1;
        this.variablesCache = null;
        this.termsCache = null;

        this.literals.remove(literal);
        if (this.literalsByName != null) {
            this.literalsByName.remove(literal.predicateName(), literal);
        }
        if (this.literalsByTerms != null) {
            for (int i = 0; i < literal.arity(); i++) {
                this.literalsByTerms.remove(literal.get(i), literal);
            }
        }
    }

    private void initLiteralsByTerms() {
        this.literalsByTerms = new MultiMap<>(literals.size() * 2);
        for (Literal literal : literals) {
            final int arity = literal.arity();
            for (int i = 0; i < arity; i++) {
                literalsByTerms.put(literal.get(i), literal);
            }
        }
    }

    private void initLiteralsByName() {
        this.literalsByName = new MultiMap<>(literals.size());
        for (Literal literal : literals) {
            this.literalsByName.put(literal.predicateName(), literal);
        }
    }

    /**
     * Checks if the set of literals contained in this clause is a subset of literals in Clause "clause".
     *
     * @param clause the clause for which the relation "subset-of" is tested
     * @return
     */
    public boolean isSubsetOf(Clause clause) {
        if (this.literals.size() > clause.literals.size()) {
            return false; // Early exit optimization
        }

        for (Literal l : literals) {
            if (!clause.literals.contains(l)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return number of unique literals
     */
    public int countLiterals() {
        return literals.size();
    }

    /**
     * Creates a map of frequencies of terms in the Clause "mod literals", which means
     * that it returns a Map in which terms are keys and values are the numbers of occurences
     * of these terms in literals (multiple occurence of a term in one literal is counted as one occurence).
     *
     * @return a map with frequencies of terms in the clause.
     */
    public Map<Term, Integer> termFrequenciesModLiterals() {
        if (literalsByTerms == null) {
            initLiteralsByTerms();
        }
        HashMap<Term, Integer> frequencies = new HashMap<>(literalsByTerms.size());
        for (Map.Entry<Term, Set<Literal>> entry : this.literalsByTerms.entrySet()) {
            frequencies.put(entry.getKey(), entry.getValue().size());
        }
        return frequencies;
    }

    /**
     * Creates a map of frequencies of variables in the Clause "mod literals", which means
     * that it returns a Map in which variables are keys and values are the numbers of occurences
     * of these variables in literals (multiple occurence of a variable in one literal is counted as one occurence).
     *
     * @return a map with frequencies of terms in the clause.
     */
    public Map<Variable, Integer> variableFrequenciesModLiterals() {
        if (literalsByTerms == null) {
            initLiteralsByTerms();
        }
        HashMap<Variable, Integer> frequencies = new HashMap<>();
        for (Map.Entry<Term, Set<Literal>> entry : this.literalsByTerms.entrySet()) {
            Term key = entry.getKey();
            if (key instanceof Variable) {
                frequencies.put((Variable) key, entry.getValue().size());
            }
        }
        return frequencies;
    }

    /**
     * @return literals in the Clause
     */
    public Set<Literal> literals() {
        return literals;
    }

    /**
     * Computes the set of predicate-names which appear in the clause. This method uses caching, so
     * while the first call to it may be a bit expensive, the subsequent calls should be very fast.
     *
     * @return set of all predicate names used in the Clause
     */
    public Set<String> predicates() {
        if (predicateCache != null) {
            return predicateCache;
        }

        Set<String> predicateCache = new HashSet<>(literals.size(), 1f);
        for (Literal literal : literals) {
            predicateCache.add(literal.predicateName());
        }

        this.predicateCache = predicateCache;
        return this.predicateCache;
    }

    /**
     * Collects the set of all literals in the Clause which are based on predicate name
     * "predicate". This method uses caching, so
     * while the first call to it may be a bit expensive, the subsequent calls should be very fast.
     *
     * @param predicate the predicate name of literals that we want to obtain.
     * @return the set of literals based on predicate "predicateName"
     */
    public Collection<Literal> getLiteralsByPredicate(String predicate) {
        if (literalsByName == null) {
            initLiteralsByName();
        }
        return literalsByName.get(predicate);
    }

    /**
     * Collects the set of literals which contain Term term in their arguments. This method uses caching, so
     * while the first call to it may be a bit expensive, the subsequent calls should be very fast.
     *
     * @param term
     * @return the set of literals which contain "term"
     */
    public Collection<Literal> getLiteralsByTerm(Term term) {
        if (literalsByTerms == null) {
            initLiteralsByTerms();
        }
        return literalsByTerms.get(term);
    }

    /**
     * Checks if the Clause contains a given literal.
     *
     * @param literal literal whose presence is tested.
     * @return true if the Clause contains "literal", false otherwise.
     */
    public boolean containsLiteral(Literal literal) {
        return this.literals.contains(literal);
    }

    /**
     * @return the set of all variables contained in the Clause.
     */
    public Set<Variable> variables() {
        if (variablesCache != null) {
            return variablesCache;
        }

        if (literalsByTerms == null) {
            initLiteralsByTerms();
        }

        HashSet<Variable> set = new HashSet<>();
        for (Map.Entry<Term, Set<Literal>> entry : literalsByTerms.entrySet()) {
            Term key = entry.getKey();
            if (key instanceof Variable && !entry.getValue().isEmpty()) {
                set.add((Variable) key);
            }
        }

        this.variablesCache = set;
        return set;
    }

    /**
     * @return the set of all terms (i.e. constants, variables and function symbols) contained in the Clause.
     */
    public Set<Term> terms() {
        if (termsCache != null) {
            return termsCache;
        }

        Set<Term> terms = new HashSet<>();
        for (Literal literal : literals) {
            terms.addAll(literal.termList());
        }

        this.termsCache = terms;
        return termsCache;
    }

    /**
     * Constructs a clause from its string representation. Clauses are assumed to be represented using a prolog-like syntax (which we call pseudo-prolog).
     * Variables start with upper-case letters, constants with lower-case letters, digits or apostrophes.
     * An example of a syntactically correct clause is shown below:<br />
     * <br />
     * literal(a,b), anotherLiteral(A,b), literal(b,'some longer text... ')<br />
     *
     * @param str string representation of the Clause
     * @return new instance of the class Clause corresponding to the string representation.
     */
    public static Clause parse(String str) {
        return parse(str, ',');
    }

    public static Clause parse(String str, char literalSeparator) {
        str = str.trim();
        if (str.isEmpty()) {
            return new Clause(Sugar.<Literal>list());
        }
        if (str.charAt(str.length() - 1) == '.') {
            str = str.substring(0, str.length() - 1);
        }
        str = str + literalSeparator + " ";
        int brackets = 0;//()
        boolean inQuotes = false;
        boolean ignoreNext = false;
        boolean expectingLiteralSeparator = false;
        List<String> split = new ArrayList<String>();
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (ignoreNext) {
                sb.append(c);
                ignoreNext = false;
            } else if (inQuotes) {
                sb.append(c);
                if (c == '\'') {
                    inQuotes = false;
                }
            } else {
                switch (c) {
                    case '\\':
                        ignoreNext = true;
                        break;
                    case '\t':
                    case ' ':
                    case '\n':
                        break;
                    case '\'':
                        inQuotes = !inQuotes;
                        sb.append(c);
                        break;
                    case '(':
                        brackets++;
                        sb.append(c);
                        break;
                    case ')':
                        brackets--;
                        expectingLiteralSeparator = true;
                        sb.append(c);
                        break;
                    default:
                        if (expectingLiteralSeparator && c == literalSeparator) {
                            expectingLiteralSeparator = false;
                            if (brackets == 0) {
                                split.add(sb.toString());
                                sb = new StringBuilder();
                            } else {
                                sb.append(c);
                            }
                        } else {
                            sb.append(c);
                        }
                        break;
                }
            }
        }
        HashMap<Variable, Variable> variables = new HashMap<Variable, Variable>();
        HashMap<Constant, Constant> constants = new HashMap<Constant, Constant>();
        List<Literal> parsedLiterals = new ArrayList<Literal>();
        for (String s : split) {
            if (s.trim().length() > 0) {
                parsedLiterals.add(Literal.parseLiteral(s.trim(), variables, constants));
            }
        }
        int anonymousIndex = 1;
        for (Literal l : parsedLiterals) {
            for (int i = 0; i < l.arity(); i++) {
                if (l.get(i).name().equals("_")) {
                    Variable an = Variable.construct("_" + (anonymousIndex++));
                    while (variables.containsKey(an.name())) {
                        an = Variable.construct("_" + (anonymousIndex++), l.get(i).type());
                    }
                    l.set(an, i);
                    variables.put(an, an);
                }
            }
        }
        return new Clause(parsedLiterals);
    }


    @Override
    public String toString() {
        return this.toPrologLikeString(", ");
    }

    public String toString(String separator) {
        return this.toPrologLikeString(separator);
    }

    private String toPrologLikeString(String separator) {
        if (this.literals.isEmpty()) {
            return "#EmptyClause";
        }
        StringBuilder sb = new StringBuilder();
        int numLiterals = this.literals.size();
        int i = 0;

        for (Literal l : this.literals) {
            sb.append(l.toString());
            if (i++ < numLiterals - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.literals.hashCode();
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Identity check
        if (!(o instanceof Clause)) return false;

        Clause c = (Clause) o;
        if (this.literals.size() != c.literals.size()) {
            return false; // Quick size check
        }

        return this.isSubsetOf(c) && c.isSubsetOf(this);
    }

    /**
     * Returns the set of connected components. Two pipes of a clause are called term-disconnected if they have no term in common,
     * they are called variable-disconnected if they have no variable in common.
     *
     * @return the set of term-connected components
     */
    public Collection<Clause> connectedComponents() {
        return connectedComponents(false);
    }


    /**
     * Returns the set of connected components. Two pipes of a clause are called term-disconnected if they have no term in common,
     * they are called variable-disconnected if they have no variable in common.
     *
     * @param justVariables
     * @return the set of term-connected components if justVariables == true, otherwise returns the set of variable-connected components
     */
    public Collection<Clause> connectedComponents(boolean justVariables) {
        return connectedComponents(justVariables, new HashSet<Term>());
    }

    public Collection<Clause> connectedComponents(boolean justVariables, Set<Term> ignoredTerms) {
        if (literalsByTerms == null) {
            initLiteralsByTerms();
        }
        Collection<? extends Term> remainingTerms;
        Set<Literal> allLiteralsInSomeComponent = new HashSet<Literal>();
        if (justVariables) {
            remainingTerms = Sugar.collectionDifference(this.variables(), ignoredTerms);
        } else {
            remainingTerms = Sugar.collectionDifference(literalsByTerms.keySet(), ignoredTerms);
        }
        List<Clause> components = new ArrayList<Clause>();
        while (!remainingTerms.isEmpty()) {
            Pair<Clause, Set<? extends Term>> pair = connectedComponent(Sugar.chooseOne(remainingTerms), ignoredTerms, justVariables);
            components.add(pair.r);
            allLiteralsInSomeComponent.addAll(pair.r.literals());
            remainingTerms = Sugar.collectionDifference(remainingTerms, pair.s);
        }
        for (Literal l : Sugar.collectionDifference(this.literals, allLiteralsInSomeComponent)) {
            components.add(new Clause(Sugar.set(l)));
        }
        return components;
    }

    private Pair<Clause, Set<? extends Term>> connectedComponent(Term termInComponent, Set<Term> ignoredTerms, boolean justVariables) {
        Set<Term> closed = new HashSet<Term>();
        Stack<Term> open = new Stack<Term>();
        Set<Term> openSet = new HashSet<Term>();
        for (Literal literal : getLiteralsByTerm(termInComponent)) {
            for (int i = 0; i < literal.arity(); i++) {
                if ((!justVariables || literal.get(i) instanceof Variable) && !ignoredTerms.contains(literal.get(i))) {
                    open.push(literal.get(i));
                }
            }
        }
        while (!open.isEmpty()) {
            Term term = open.pop();
            if (!closed.contains(term)) {
                for (Literal literal : getLiteralsByTerm(term)) {
                    for (int i = 0; i < literal.arity(); i++) {
                        if (!closed.contains(literal.get(i)) && !openSet.contains(literal.get(i))) {
                            if ((!justVariables || literal.get(i) instanceof Variable) && !ignoredTerms.contains(literal.get(i))) {
                                open.push(literal.get(i));
                                openSet.add(literal.get(i));
                            }
                        }
                    }
                }
                if (!ignoredTerms.contains(term)) {
                    closed.add(term);
                }
            }
        }
        Set<Literal> lits = new HashSet<Literal>();
        outerLoop:
        for (Literal literal : literals()) {
            for (int i = 0; i < literal.arity(); i++) {
                if ((!justVariables || literal.get(i) instanceof Variable) && closed.contains(literal.get(i))) {
                    lits.add(literal);
                    continue outerLoop;
                }
            }
        }
        return new Pair<Clause, Set<? extends Term>>(new Clause(lits), closed);
    }

    public static void main(String[] args) {
        Clause c = Clause.parse("professor(a1) v !taughtBy(a2,a1,a3) v !courseLevel(a2,Level_500)", 'v');
        for (Clause comp : c.connectedComponents(false, Sugar.<Term>set(Constant.construct("a1")))) {
            System.out.println(comp);
        }
    }
}