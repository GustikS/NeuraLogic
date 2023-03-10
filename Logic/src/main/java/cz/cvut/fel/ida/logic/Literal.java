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
 * Predicate.java
 *
 * Created on 30. listopad 2006, 16:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic;

import cz.cvut.fel.ida.utils.math.UniqueIDs;
import cz.cvut.fel.ida.utils.math.collections.FakeMap;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class for representing positive (i.e. nonnegated) first-order-logic literals.
 *
 * @author Ondra
 */
public class Literal implements Serializable {
    private static final Logger LOG = Logger.getLogger(Literal.class.getName());

    private Term[] terms;

    private Predicate predicate;

    private boolean negated = false;

    private int id;

    private int hashCode = -1;

    private boolean changePermitted = true;

    private static Map<Variable, Variable> fakeMapVar = new FakeMap<Variable, Variable>();

    private static Map<Constant, Constant> fakeMapConst = new FakeMap<Constant, Constant>();

    private Literal() {
    }

    public Literal(Predicate predicate) {
        this.predicate = predicate;
        this.id = (int) UniqueIDs.getUniqueName();
        this.terms = new Term[predicate.arity];
    }

    /**
     * Creates a new instance of class Literal. When name = "somePredicate" and arity = 3,
     * then the result is Literal somePredicate(...,...,...)
     *
     * @param name  predicate-name of the literal
     * @param arity arity of the literal
     */
    public Literal(String name, int arity) {
        this.id = (int) UniqueIDs.getUniqueName();
        this.terms = new Term[arity];
        this.predicate = new Predicate(name, arity);
    }

    /**
     * Creates a new instance of class Literal. When name = "somePredicate" and terms = {A,b,c} (where
     * A, b, c are instances of class Term) then the result is Literal somePredicate(A,b,c)
     *
     * @param name  predicate-name of the literal
     * @param terms arguments of the literal
     */
    public Literal(String name, Term... terms) {
        this(name, terms.length);
        this.set(terms);
    }

    /**
     * Creates a new instance of class Literal. When name = "somePredicate" and terms = {A,b,c} (where
     * A, b, c are instances of class Term) then the result is Literal somePredicate(A,b,c)
     *
     * @param name  predicate-name of the literal
     * @param terms arguments of the literal
     */
    public Literal(String name, List<? extends Term> terms) {
        this.id = (int) UniqueIDs.getUniqueName();
        this.terms = new Term[terms.size()];
        System.arraycopy(terms.toArray(), 0, this.terms, 0, terms.size());
        this.predicate = new Predicate(name, terms.size());
    }

    public Literal(String name, boolean negated, int arity) {
        this(name, arity);
        this.negated = negated;
    }

    public Literal(String name, boolean negated, Term... terms) {
        this(name, terms);
        this.negated = negated;
    }

    public Literal(String name, boolean negated, List<Term> terms) {
        this(name, terms);
        this.negated = negated;
    }

    public Literal(Predicate predicate, boolean negated, List<? extends Term> terms) {
        this(predicate);
        if (predicate.arity != terms.size()) {
            LOG.severe("Arity mismatch while creating a literal from predicate+terms");
        }
        System.arraycopy(terms.toArray(), 0, this.terms, 0, terms.size());
        this.negated = negated;
    }

    /**
     * @param index
     * @return term iterable argument at position index
     */
    public Term get(int index) {
        return terms[index];
    }

    /**
     * This method is used by class Clause to forbid changes of literals iterable it.
     * It is possible to revert this by calling this method: allowModifications(true)
     *
     * @param allow a boolean flag indicating whether this literal can be modified or not
     */
    public void allowModifications(boolean allow) {
        this.changePermitted = allow;
    }

    /**
     * This method can be used to set a term iterable an argument (at position index) under the condition that this literal
     * has not been locked for changes.
     *
     * @param term  term to be set
     * @param index index of the argument iterable which the term should be set
     */
    public void set(Term term, int index) {
        if (!changePermitted)
            throw new IllegalStateException("This particular literal has been locked for changes. Create new instance and change it.");
        hashCode = -1;
        terms[index] = term;
    }

    /**
     * This method can be used to set more than one argument at once under the condition that this literal has not been
     * locked for changes.
     *
     * @param terms array of terms which should be set as arguments, it must hold terms.length < this.arity()
     */
    public void set(Term... terms) {
        for (int i = 0; i < terms.length; i++) {
            set(terms[i], i);
        }
    }

    /**
     * @return arity of the literal (i.e. number of arguments)
     */
    public int arity() {
        return terms.length;
    }

    /**
     * @return number of non-unique constants iterable the arguments
     */
    public int countConstants() {
        int count = 0;
        for (int i = 0; i < terms.length; i++) {
            if (terms[i] instanceof Constant) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return predicate name of this literal
     */
    public String predicateName() {
        return predicate.name;
    }

    /**
     * return Predicate of this literal
     *
     * @return
     */
    public Predicate predicate() {
        return predicate;
    }

    /**
     * Parses Literal from its string representation, for examle:
     * Literal parsed = Literal.parse("somePredicate(X,a,f(a,b))"); It uses Prolog-convention
     * that variables start with upper-case letters.
     *
     * @param str string representation of literal
     * @return parsed Literal
     */
    public static Literal parseLiteral(String str) {
        return parseLiteral(str, fakeMapVar, fakeMapConst);
    }


    /**
     * Parses Literal from its string representation, for examle:
     * Literal parsed = Literal.parse("somePredicate(X,a,f(a,b))"); It uses Prolog-convention
     * that variables start with upper-case letters.
     *
     * @param str       string representation of literal
     * @param variables Map of used variables - it does not have to contain the variables contained iterable the arguments of the literal,
     *                  if they are not there, they will be added automatically
     * @param constants Map of used constants - it does not have to contain the constants contained iterable the arguments of the literal,
     *                  if they are not there, they will be added automatically
     * @return parsed Literal
     */
    public static Literal parseLiteral(String str, Map<Variable, Variable> variables, Map<Constant, Constant> constants) {
        char c[] = str.toCharArray();
        StringBuilder predicateName = new StringBuilder();
        ArrayList<Term> arguments = new ArrayList<Term>(5);
        int index = 0;
        boolean inQuotes = false;
        boolean inDoubleQuotes = false;
        boolean ignoreNext = false;
        while (true) {
            if (c[index] == '\\' && !ignoreNext) {
                ignoreNext = true;
            } else {
                if (!inQuotes && !inDoubleQuotes && c[index] == '\'' && !ignoreNext) {
                    predicateName.append(c[index]);
                    inQuotes = true;
                } else if (!inQuotes && !inDoubleQuotes && c[index] == '\"' && !ignoreNext) {
                    predicateName.append(c[index]);
                    inDoubleQuotes = true;
                } else if (inQuotes && c[index] == '\'' && !ignoreNext) {
                    predicateName.append(c[index]);
                    inQuotes = false;
                } else if (inDoubleQuotes && c[index] == '\"' && !ignoreNext) {
                    predicateName.append(c[index]);
                    inDoubleQuotes = false;
                } else if (!inQuotes && !inDoubleQuotes && c[index] == '(') {
                    break;
                } else {
                    predicateName.append(c[index]);
                }
                ignoreNext = false;
            }
            index++;
        }
        boolean negated = false;
        String predicateNameString = predicateName.toString().trim();
        if (predicateNameString.startsWith("!")) {
            predicateNameString = predicateNameString.substring(1);
            negated = true;
        }
        inQuotes = false;
        ignoreNext = false;
        while (true) {
            if (index >= c.length || c[index] == ')') {
                break;
            } else if (c[index] == '(' || Character.isSpaceChar(c[index])) {
                //do nothing
            } else {
                Pair<Term, Integer> pair = ParserUtils.parseTerm(c, index, ')', variables, constants);
                arguments.add(pair.r);
                index = pair.s;
            }
            index++;
        }
        Literal retVal = new Literal(predicateNameString, negated, arguments.size());
        for (int i = 0; i < retVal.arity(); i++) {
            retVal.set(arguments.get(i), i);
        }
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.negated) {
            sb.append("!");
        }
        sb.append(predicate.name);
        if (terms.length > 0) {
            sb.append("(");
            for (Term t : terms)
                sb.append(t).append(", ");
            if (sb.charAt(sb.length() - 2) == ',')
                sb.delete(sb.length() - 2, sb.length());
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Literal)) {
            return false;
        } else {
            Literal other = (Literal) o;
            if (other.negated != this.negated) {
                return false;
            }
            if (other.terms.length != this.terms.length) {
                return false;
            }
            if (!other.predicate.name.equals(this.predicate.name)) {
                return false;
            }
            for (int i = 0; i < this.terms.length; i++) {
                if (!terms[i].equals(other.terms[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        if (hashCode != -1) {
            return hashCode;
        }
        int hash = this.predicate.name.hashCode();
        for (int i = 0; i < terms.length; i++) {
            hash = (int) ((long) terms[i].hashCode() * (long) hash);
        }
        hash *= (this.negated ? -1 : 1);
        return (hashCode = hash);
    }

    public int liftedHashCode() {
        int hash = this.predicate.name.hashCode();
        for (int i = 0; i < terms.length; i++) {
            long varcode;
            if (terms[i] instanceof Variable){
                varcode = 7;   // all variables map to the same hash number
            } else {
                varcode = (long) terms[i].hashCode();
            }
            hash = (int) (varcode * (long) hash);
        }
//        hash *= (this.negated ? -1 : 1);  we want to connect positive and negated literals just the same
        return hash;
    }

    /**
     * Every instance of class Literal has a unique id (integer). This method allows the user to get it.
     * It can be the case that l1.equals(l2) but l1.id() != l2.id()
     *
     * @return universal identifier of the literal
     */
    public int id() {
        return id;
    }

    /**
     * Checks if the literal contains at least one variable
     *
     * @return true if the literal contains at least one variable, false otherwise
     */
    public boolean containsVariable() {
        for (Term t : terms)
            if (t instanceof Variable)
                return true;
        return false;
    }

    /**
     * Checks if the literal contains at least one constant
     *
     * @return true if the literal contains at least one constant, false otherwise
     */
    public boolean containsConstant() {
        for (Term t : terms)
            if (t instanceof Constant)
                return true;
        return false;
    }


    /**
     * @return the arguments of the literal iterable the form of a set
     */
    public Set<Term> terms() {
        Set<Term> set = new HashSet<Term>();
        for (Term t : terms) {
            set.add(t);
        }
        return set;
    }

    public Term[] arguments() {
        return this.terms;
    }

    public List<Term> termList() {
        return Arrays.asList(terms);
    }

    /**
     * Converts Literal to Function
     *
     * @return instance of class Function which is syntactically equivalent to this literal
     */
    public Function toFunction() {
        Function f = new Function(this.predicate.name, this.arity());
        for (int i = 0; i < f.arity(); i++) {
            f.set(this.get(i), i);
        }
        return f;
    }

    /**
     * Creates a copy of this literal WITHOUT TERMS.
     *
     * @return copy of this literal
     */
    public Literal emptyCopy() {
        Literal p = new Literal();
        p.predicate = this.predicate;
        p.terms = new Term[this.terms.length];
        //p.id = this.id;
        //p.hashCode = -1;
        p.negated = this.negated;
        p.hashCode = -1;
        //for (int i = 0; i < terms.length; i++)
        //    p.terms[i] = terms[i];
        return p;
    }

    /**
     * This is SLOW, use direct indexing instead
     *
     * @param substitution
     * @return
     */
    @Deprecated
    public Literal subsCopy(Map<? extends Term, ? extends Term> substitution) {
        Literal copy = this.emptyCopy();
        copy.hashCode = -1;
        for (int j = 0; j < this.arity(); j++) {
            if (substitution.containsKey(copy.get(j))) {
                copy.set(substitution.get(get(j)), j);
            }
        }
        return copy;
    }

    public Literal subsCopy(Term[] substitution) {
        Literal copy = this.emptyCopy();
        for (int j = 0; j < this.arity(); j++) {
            int indexWithinSubstitution = this.terms[j].getIndexWithinSubstitution();
            if (indexWithinSubstitution >= 0)   //if a variable
                copy.terms[j] = substitution[indexWithinSubstitution];
            else    // if a constant
                copy.terms[j] = this.terms[j];
        }
        return copy;
    }

    /**
     * Creates a negation of this literal.
     *
     * @return copy of this literal
     */
    public Literal negation() {
        return new Literal(this.predicate.name, !this.negated, this.terms);
    }

    public boolean isNegated() {
        return this.negated;
    }

    /**
     * Creates a new instance of the literal with masked terms
     *
     * @param maskedTerms
     * @return
     */
    public Literal maskTerms(int[] maskedTerms) {
        List<Term> terms = new ArrayList<>(this.termList());

        for (int index : maskedTerms) {
            terms.set(index, Constant.construct("_"));
        }

        return new Literal(this.predicate, this.negated, terms);
    }
}