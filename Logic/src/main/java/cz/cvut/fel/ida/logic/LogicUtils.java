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
 * LogicUtils.java
 *
 * Created on 12. leden 2008, 17:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic;

import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SpecialBinaryPredicates;
import cz.cvut.fel.ida.logic.subsumption.SpecialVarargPredicates;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.FakeMap;
import cz.cvut.fel.ida.utils.math.collections.ValueToIndex;
import cz.cvut.fel.ida.utils.math.hypergraphs.Hypergraph;
import cz.cvut.fel.ida.utils.math.hypergraphs.HypergraphUtils;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.util.*;

/**
 * Class harbouring several useful methods for manipulation with Clauses, Literals and Terms
 * @author Ondra
 */
public class LogicUtils {

    /** Creates a new instance of LogicUtils */
    private LogicUtils() {
    }

    /**
     * Constructs a new variable which is not contained iterable the given clause.
     * @param c clause that is used to constrain the possible new variables - the new variable cannot be contained iterable it
     * @return new variable which is not contained iterable Clause c
     */
    public static Variable freshVariable(Clause c){
        return freshVariable(c.variables(), 0);
    }
    
    /**
     * Constructs a new variable which is not contained iterable the given set of variables.
     * @param variables set of variables that is used to constrain the possible new variables - the new variable cannot be contained iterable it
     * @return
     */
    public static Variable freshVariable(Set<Variable> variables){
        return freshVariable(variables, 0);
    }
    
    /**
     * Constructs a new variable which is not contained iterable the given set of variables. The name of the variable will be
     * Vi where i >= index and Vi is not jet contained iterable the set <em>variables</em>,
     * @param variables set of variables that is used to constrain the possible new variables - the new variable cannot be contained iterable it
     * @param index  the index from which the name of the new variable should be searched for.
     * @return
     */
    public static Variable freshVariable(Set<Variable> variables, int index){
        Variable var = null;
        do {
            var = Variable.construct("V"+(index++));
        } while (variables.contains(var));
        return var;
    }

    public static Set<Variable> freshVariables(Set<Variable> variables, int num){
        Set<Variable> retVal = new HashSet<Variable>();
        Set<Variable> all = new HashSet<Variable>(variables);
        for (int i = 0; i < num; i++){
            Variable v = freshVariable(all, i+1+variables.size());
            all.add(v);
            retVal.add(v);
        }
        return retVal;
    }

    /**
     * Converts a PrologList containing just instances of class Function into a Clause
     * @param pl PrologList containing just instances of class Function
     * @return clause constructed from the set of function symbols (using the method toLiteral of class Function)
     */
    public static Clause clauseFromFunctionsList(PrologList pl){
        Set<Literal> literals = new HashSet<Literal>();
        for (int i = 0; i < pl.countItems(); i++){
            Function f = (Function)pl.get(i);
            literals.add(f.toLiteral());
        }
        return new Clause(literals);
    }

    /**
     * Creates a clause from Clause c iterable which all occurences of Term a are replaced by Term b
     * 
     * @param c the clause
     * @param a the term to be replaced
     * @param b the term by which it should be replaced
     * @return new clause with substituted values
     */
    public static Clause substitute(Clause c, Term a, Term b){
        return substitute(c, new Term[]{a}, new Term[]{b});
    }


    public static Literal substitute(Literal l, Term a, Term b) {
        return substitute(l, new Term[]{a}, new Term[]{b});
    }
    /**
     * Creates a new literal from l iterable which all occurences of Terms from source are replaced by respective Terms iterable image
     * @param l the literal
     * @param source the terms to be replaced
     * @param image the terms by which they should be replaced
     * @return the new substituted literal
     */
    public static Literal substitute(Literal l, Term[] source, Term[] image){
        Map<Term,Term> substitution = new HashMap<Term,Term>();
        for (int i = 0; i < source.length; i++){
            substitution.put(source[i], image[i]);
        }
        return substitute(l, substitution);
    }

    /**
     * Creates a new Literal from Literal l by substituting values according to a substitution represented
     * by the Map substitution. For each pair "key"-"value" iterable the Map, all occurences of "key" are replaced by
     * "value".
     * 
     * @param l the literal
     * @param substitution the substitution represented as Map
     * @return the substituted literal
     */
    public static Literal substitute(Literal l, Map<Term,Term> substitution){
        Literal newLiteral = new Literal(l.predicateName(), l.isNegated(), l.arity());
        for (int i = 0; i < l.arity(); i++){
            if (substitution.containsKey(l.get(i))) {
                newLiteral.set(substitution.get(l.get(i)), i);
            } else {
                newLiteral.set(l.get(i), i);
            }
        }
        return newLiteral;
    }

    /**
     * Removes enclosing apostrophes (quotes) from a term
     * @param term the term to be unquoted
     * @return Term with removed apostrophes (quotes)
     */
    public static Term unquote(Term term){
        String name = term.name();
        if (name.length() > 0 && name.charAt(0) == '\'' && name.charAt(name.length()-1) == '\''){
            name = name.substring(1, name.length()-1);
        }
        return ParserUtils.parseTerm(name.toCharArray(), 0, ')', new HashMap<Variable,Variable>(), new HashMap<Constant,Constant>()).r;
    }

    /**
     * Creates a nice variable name for a given id. For example, for id = 0, we get
     * A, for id = 1 we get B... then A1, ..., Z1, A2,... etc.
     * 
     * @param id unique identifier of variable
     * @return string which is a name of the variable assigned to the given id
     */
    public static String niceVariableName(int id){
        if (id <= ((int)'Z'-(int)'A')){
            return String.valueOf((char)((int)'A'+id));
        } else {
            return String.valueOf((char)((int)'A'+id%((int)'Z'-(int)'A')))+(id/((int)'Z'-(int)'A'));
        }
    }

    /**
     * Creates a new clause iterable which it replaces all terms iterable a clause by the respective variables (basically it makes the
     * first letters of constants upper-case and replaces them by instances of class Variable).
     * @param c the clause
     * @return the new variabilized clause
     */
    public static Clause variabilizeClause(Clause c){
       return variabilizeClause(c, null);
    }

    public static Clause variabilizeClause(Clause c, Clause template){
        Map<Pair<String,Integer>,Literal> map = new HashMap<Pair<String,Integer>,Literal>();
        if (template != null) {
            for (Literal l : template.literals()) {
                map.put(new Pair<String, Integer>(l.predicateName(), l.arity()), l);
            }
        }
        Map<Term,Term> substitution = new HashMap<Term,Term>();
        Set<Variable> usedVariables = new HashSet<Variable>(c.variables());
        Pair<String,Integer> query = new Pair<String,Integer>();
        int freshVariableIndex = 1;
        for (Literal l : c.literals()){
            query.set(l.predicateName(), l.arity());
            Literal templateLit = map.get(query);
            for (int i = 0; i < l.arity(); i++){
                if ((template == null || !templateLit.get(i).name().equals("#")) && l.get(i) instanceof Constant && !substitution.containsKey(l.get(i))) {
                    Variable newVar = toVariable(l.get(i));
                    if (usedVariables.contains(newVar)){
                        newVar = LogicUtils.freshVariable(usedVariables, freshVariableIndex++);
                    }
                    substitution.put(l.get(i), newVar);
                    usedVariables.add(newVar);
                }
            }
        }
        return LogicUtils.substitute(c, substitution);
    }

    public static Variable toVariable(Term term){
        return Variable.construct(Sugar.firstCharacterToUpperCase(term.name()), term.type());
    }

    public static Variable toVariable(Term term, String type){
        return Variable.construct(Sugar.firstCharacterToUpperCase(term.name()), type);
    }

    public static Constant toConstant(Term term){
        return Constant.construct(Sugar.firstCharacterToLowerCase(term.name()), term.type());
    }

    public static Constant toConstant(Term term, String type){
        return Constant.construct(Sugar.firstCharacterToLowerCase(term.name()), type);
    }

    public static Term parseTerm(String s){
        s = s.trim();
        if (s.charAt(0) == '['){
            return Function.parseFunction(s, new FakeMap<Variable, Variable>(), new FakeMap<Constant, Constant>());
        } else if (Character.isLowerCase(s.charAt(0))){
            return Constant.construct(s);
        } else {
            return Variable.construct(s);
        }
    }

    /**
     * Creates a new clause iterable which it replaces all terms iterable a clause by the respective variables (basically it makes the
     * first letters of variables lower-case and replaces them by instances of class Constant).
     * 
     * @param c the clause
     * @return the new "constantized" clause
     */
    public static Clause constantizeClause(Clause c){
       Set<Literal> predicates = new LinkedHashSet<Literal>();
        for (Literal l : c.literals()){
            Literal newPred = new Literal(l.predicateName(), l.isNegated(), l.arity());
            for (int i = 0; i < l.arity(); i++){
                newPred.set(Constant.construct(Sugar.firstCharacterToLowerCase(l.get(i).name())), i);
            }
            predicates.add(newPred);
        }
        return new Clause(predicates);
    }

    /**
     * Creates a list of predicate names which are not contained iterable Clause c. The list
     * will contain "count" elements.
     * @param c Clause which is used to constrain the possible predicate names - predicateName already contained
     * iterable c cannto be contained iterable the generated list.
     * @param count number of predicate names to be generated
     * @return list of new predicate names
     */
    public static List<String> freshPredicateNames(Clause c, int count){
        List<String> retVal = new ArrayList<String>();
        for (int i = 0; i < Integer.MAX_VALUE; i++){
            if (retVal.size() == count){
                break;
            }
            String pred = "pred_"+i;
            if (c.getLiteralsByPredicate(pred).isEmpty()){
                retVal.add("pred"+i);
            }
        }
        return retVal;
    }

    public static List<String> freshPredicateNames(Set<String> c, int count){
        List<String> retVal = new ArrayList<String>();
        for (int i = 0; i < Integer.MAX_VALUE; i++){
            if (retVal.size() == count){
                break;
            }
            String pred = "pred_"+i;
            if (!c.contains(pred)){
                retVal.add("pred"+i);
            }
        }
        return retVal;
    }

    /**
     * Checks if the given clause is ground (a clause is groundCopy if it contains no variables)
     * @param c clause to be checked
     * @return true if c contains no variables, false otherwise
     */
    public static boolean isGround(Clause c){
        return c.variables().isEmpty();
    }

    public static boolean isGround(Literal l){
        for (int i = 0; i < l.arity(); i++){
            if (l.get(i) instanceof Variable){
                return false;
            }
        }
        return true;
    }

    /**
     * Given two Clauses a and b, it constructs two new equivalent clauses which
     * do not share any variables.
     * @param a the first clause
     * @param b the second clause
     * @return pair of Clauses (x,y) such that the intersection of a.variables() and b.variables() is empty
     */
    public static Pair<Clause,Clause> standardizeApart(Clause a, Clause b){
        Pair<Clause,Clause> retVal = new Pair<Clause,Clause>();
        int i = 0;
        for (Clause c : standardizeApart(Sugar.list(a,b))){
            if (i == 0){
                retVal.r = c;
            } else {
                retVal.s = c;
            }
            i++;
        }
        return retVal;
    }

    /**
     * Given two Clauses a and b, it constructs two new equivalent clauses which
     * do not share any variables.
     * @param clauses the clauses which should be standardized apart
     * @return collection of Clauses such that for any two a, b of them, the intersection of a.variables() and b.variables() is empty
     */
    public static Collection<Clause> standardizeApart(Collection<Clause> clauses){
        List<Clause> retVal = new ArrayList<Clause>();
        Map<Pair<Variable,Integer>,Variable> vars = new HashMap<Pair<Variable,Integer>,Variable>();
        Set<Variable> allVariables = new HashSet<Variable>();
        for (Clause c : clauses){
            for (Variable v : c.variables()){
                allVariables.add(v);
            }
        }
        int i = 0;
        for (Clause c : clauses){
            Set<Literal> literals = new HashSet<Literal>();
            Pair<Variable,Integer> queryPair = new Pair<Variable,Integer>();
            for (Literal l : c.literals()){
                Literal newLiteral = new Literal(l.predicateName(), l.isNegated(), l.arity());
                for (int j = 0; j < l.arity(); j++){
                    if (l.get(j) instanceof Variable){
                        queryPair.set((Variable)l.get(j), i);
                        Variable var = null;
                        if ((var = vars.get(queryPair)) == null){
                            Pair<Variable,Integer> insertPair = new Pair<Variable,Integer>(queryPair.r, queryPair.s);
                            var = freshVariable(allVariables);
                            allVariables.add(var);
                            vars.put(insertPair, var);
                        }
                        newLiteral.set(var, j);
                    } else {
                        newLiteral.set(l.get(j), j);
                    }
                }
                literals.add(newLiteral);
            }
            retVal.add(new Clause(literals));
            i++;
        }
        return retVal;
    }

    public static boolean isTreelike(Clause c){
        return HypergraphUtils.isTreelike(clause2hypergraph(c));
    }

    public static boolean isAcyclic(Clause c){
        return HypergraphUtils.isAcyclic(clause2hypergraph(c));
    }

    private static Hypergraph clause2hypergraph(Clause c){
        ValueToIndex<Variable> vertexIDs = new ValueToIndex<Variable>();
        ValueToIndex<Set<Integer>> edgeIDs = new ValueToIndex<Set<Integer>>();
        Hypergraph h = new Hypergraph();
        for (Literal l : c.literals()){
            Set<Integer> edge = new HashSet<Integer>();
            for (int i = 0; i < l.arity(); i++){
                if (l.get(i) instanceof Variable) {
                    edge.add(vertexIDs.valueToIndex((Variable) l.get(i)));
                }
            }
            h.addEdge(edgeIDs.valueToIndex(edge), edge);
        }
        return h;
    }

    public static Clause randomlyRenameVariables(Clause clause, int newVariableIndex){
        return randomlyRenameVariables(clause, newVariableIndex, new Random());
    }

    public static Clause randomlyRenameVariables(Clause clause, int newVariableIndex, Random random){
        List<Variable> newVariables = new ArrayList<Variable>();
        int numVariables = clause.variables().size();
        for (int i = 0; i < numVariables; i++) {
            newVariables.add(Variable.construct(niceVariableName(newVariableIndex+i)));
        }
        Collections.shuffle(newVariables, random);
        Map<Term,Term> substitution = new HashMap<Term,Term>();
        int index = 0;
        for (Variable oldVar : clause.variables()){
            substitution.put(oldVar, newVariables.get(index++));
        }
        return substitute(clause, substitution);
    }

    public static Clause randomlyRenameConstants(Clause clause, int newConstantIndex){
        return randomlyRenameConstants(clause, newConstantIndex, new Random());
    }

    public static Clause randomlyRenameConstants(Clause clause, int newConstantIndex, Random random){
        List<Constant> newConstants = new ArrayList<Constant>();
        int numConstants = 0;
        for (Term term : clause.terms()){
            if (term instanceof Constant){
                numConstants++;
            }
        }
        for (int i = 0; i < numConstants; i++) {
            newConstants.add(Constant.construct(niceVariableName(newConstantIndex + i).toLowerCase()));
        }
        Collections.shuffle(newConstants, random);
        Map<Term,Term> substitution = new HashMap<Term,Term>();
        int index = 0;
        for (Term oldTerm : clause.terms()){
            if (oldTerm instanceof Constant) {
                substitution.put(oldTerm, newConstants.get(index++));
            }
        }
        return substitute(clause, substitution);
    }

    public static Set<Variable> variables(Collection<Clause>... clauses){
        Set<Variable> retVal = new HashSet<Variable>();
        for (Collection<Clause> clauseColl : clauses) {
            for (Clause c : clauseColl) {
                retVal.addAll(c.variables());
            }
        }
        return retVal;
    }

    public static Set<Term> terms(Collection<Clause>... clauses){
        Set<Term> retVal = new HashSet<Term>();
        for (Collection<Clause> clauseColl : clauses) {
            for (Clause c : clauseColl) {
                retVal.addAll(c.terms());
            }
        }
        return retVal;
    }

    public static Set<String> predicateNamesOfLiterals(Collection<Literal>... literals){
        Set<String> retVal = new HashSet<String>();
        for (Collection<Literal> literalColl : literals){
            for (Literal l : literalColl){
                retVal.add(l.predicateName());
            }
        }
        return retVal;
    }

    public static Set<Pair<String,Integer>> predicates(Collection<Clause> clauses){
        return predicates(clauses, false);
    }

    public static Set<Pair<String,Integer>> predicates(Collection<Clause> clauses, boolean ignoreSpecialPredicates){
        Set<Pair<String,Integer>> retVal = new HashSet<Pair<String, Integer>>();
        for (Clause c : clauses){
            retVal.addAll(predicates(c, ignoreSpecialPredicates));
        }
        return retVal;
    }

    public static Set<Pair<String,Integer>> predicates(Clause c){
        return predicates(c, false);
    }

    public static Set<Pair<String,Integer>> predicates(Clause c, boolean ignoreSpecialPredicates){
        Set<Pair<String,Integer>> retVal = new HashSet<Pair<String, Integer>>();
        for (Literal l : c.literals()){
            if (!ignoreSpecialPredicates || (!SpecialBinaryPredicates.SPECIAL_PREDICATES.contains(l.predicateName()) && !SpecialVarargPredicates.SPECIAL_PREDICATES.contains(l.predicateName()))) {
                retVal.add(new Pair<String, Integer>(l.predicateName(), l.arity()));
            }
        }
        return retVal;
    }

    public static Set<String> predicateNames(Collection<Clause>... clauses){
        Set<String> retVal = new HashSet<String>();
        for (Collection<Clause> clauseColl : clauses) {
            for (Clause c : clauseColl) {
                retVal.addAll(c.predicates());
            }
        }
        return retVal;
    }

    public static Set<Literal> atoms(Clause clause){
        Set<Literal> retVal = new HashSet<Literal>();
        for (Literal l : clause.literals()){
            if (l.isNegated()){
                retVal.add(l.negation());
            } else {
                retVal.add(l);
            }
        }
        return retVal;
    }

    public static Set<Literal> atoms(Collection<Clause>... clauses){
        Set<Literal> retVal = new HashSet<Literal>();
        for (Collection<Clause> clauseColl : clauses) {
            for (Clause c : clauseColl) {
                retVal.addAll(atoms(c));
            }
        }
        return retVal;
    }

    public static boolean isGround(Collection<Clause>... clauses){
        for (Collection<Clause> clauseColl : clauses) {
            for (Clause c : clauseColl) {
                if (!isGround(c)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args){
        System.out.println(variabilizeClause(Clause.parse("lit(a,A), lit(b,c)"), Clause.parse("lit(x,#)")));
        System.out.println(randomlyRenameConstants(Clause.parse("l1(a,b), l2(b,c), l3(c,d)"), 1));
    }

    public static Clause flipSigns(Clause c){
        List<Literal> literals = new ArrayList<Literal>();
        for (Literal l : c.literals()){
            literals.add(l.negation());
        }
        return new Clause(literals);
    }

    public static List<Literal> flipSigns(Collection<Literal> literals){
        List<Literal> retVal = new ArrayList<Literal>();
        for (Literal l : literals){
            retVal.add(l.negation());
        }
        return retVal;
    }

    public static Clause substitute(Clause c, Term[] variables, Term[] terms){
        Map<Term,Term> subs = new HashMap<Term,Term>();
        for (int i = 0; i < variables.length; i++){
            subs.put(variables[i], terms[i]);
        }
        return substitute(c, subs);
    }

    public static Clause substitute(Clause c, Map<Term,Term> substitution){
        Set<Literal> literals = new HashSet<Literal>();
        for (Literal l : c.literals()){
            Literal groundCopy = substitute(l, substitution);
            literals.add(groundCopy);
        }
        return new Clause(literals);
    }

    public static boolean isModelOf(Set<Literal> possibleWorld, Clause clause){
        for (Literal l : clause.literals()){
            if (possibleWorld.contains(l)){
                return true;
            }
        }
        return false;
    }

    public static boolean isModelOf(Set<Literal> possibleWorld, Collection<Clause> theory){
        for (Clause c : theory){
            if (!isModelOf(possibleWorld, c)){
                return false;
            }
        }
        return true;
    }

    public static Literal termToLiteral(Term term){
        if (term instanceof Constant){
            return new Literal(term.name());
        } else if (term instanceof Function){
            return ((Function)term).toLiteral();
        } else if (term instanceof Variable){
            throw new IllegalArgumentException("Variables cannot be converted into literals.");
        } else {
            throw new IllegalArgumentException("Only constants and functions supported.");
        }
    }

    public static Set<Constant> constants(Clause c){
        Set<Constant> retVal = new HashSet<Constant>();
        for (Term t : c.terms()){
            if (t instanceof Constant){
                retVal.add((Constant)t);
            }
        }
        return retVal;
    }

    public static Set<Constant> constants(Collection<Clause> coll){
        Set<Constant> retVal = new HashSet<Constant>();
        for (Clause c : coll){
            retVal.addAll(constants(c));
        }
        return retVal;
    }



    private static Literal literalTemplate(Literal l){
        int j = 0;
        Literal normalized = new Literal(l.predicateName(), l.isNegated(), l.arity());
        for (int i = 0; i < l.arity(); i++){
            if (l.get(i) instanceof Variable){
                Variable v = (Variable)l.get(i);
                normalized.set(Variable.construct("V" + (j++), v.type()), i);
            } else {
                normalized.set(l.get(i), i);
            }
        }
        return normalized;
    }

    public static Set<Literal> allGroundAtoms(Collection<Clause> clauses){
        return allGroundAtoms_impl(clauses, new HashSet<Constant>());
    }

    public static Set<Literal> allGroundAtoms(Collection<Pair<String,Integer>> predicates, Collection<Constant> constants){
        List<Clause> clauses = new ArrayList<Clause>();
        for (Pair<String,Integer> p : predicates){
            clauses.add(new Clause(newLiteral(p.r, p.s)));
        }
        return allGroundAtoms_impl(clauses, constants);
    }

    private static Set<Literal> allGroundAtoms_impl(Collection<Clause> clauses, Collection<Constant> constants){
        Set<Literal> retVal = new HashSet<Literal>();
        Set<Constant> constantSet = new HashSet<Constant>();
        constantSet.addAll(constants);
        for (Clause c : clauses){
            constantSet.addAll(constants(c));
        }
        Literal lit = new Literal("", true, constantSet.size());
        int i = 0;
        for (Constant c : constantSet){
            lit.set(c, i++);
        }
        Set<Literal> normalized = new HashSet<Literal>();
        //not optimal but not a bottleneck
        for (Clause c : clauses){
            for (Literal l : c.literals()){
                normalized.add(literalTemplate(l));
            }
        }
        Matching m = new Matching(Sugar.list(new Clause(lit)));
        for (Literal l : normalized){
            if (!l.isNegated()){
                l = l.negation();
            }
            Pair<Term[],List<Term[]>> p = m.allSubstitutions(new Clause(l), 0, Integer.MAX_VALUE);
            for (Term[] subs : p.s){
                retVal.add(substitute(l, p.r, subs).negation());
            }
        }
        return retVal;
    }

    public static Set<Clause> allGroundings(Collection<Clause> clauses, Collection<Constant> constants){
        Set<Clause> retVal = new HashSet<Clause>();
        Literal lit = new Literal("", true, constants.size());
        int i = 0;
        for (Constant c : constants){
            lit.set(c, i++);
        }
        Matching m = new Matching(Sugar.list(new Clause(lit)));
        for (Clause c : clauses){
            Literal template = new Literal("l", true, c.variables().size());
            i = 0;
            for (Variable v : c.variables()) {
                template.set(v,i++);
            }
            Pair<Term[],List<Term[]>> p = m.allSubstitutions(new Clause(template), 0, Integer.MAX_VALUE);
            for (Term[] subs : p.s){
                retVal.add(substitute(c, p.r, subs));
            }
        }
        return retVal;
    }

    public static Literal newLiteral(String predicate, int arity){
        Literal l = new Literal(predicate, arity);
        for (int i = 0; i < arity; i++){
            l.set(Variable.construct("V"+i),i);
        }
        return l;
    }

    public static Literal newLiteral(String predicate, int arity, Collection<Variable> freshVariables){
        Literal l = new Literal(predicate, arity);
        int i = 0;
        for (Variable v : freshVariables){
            l.set(v, i);
            i++;
            if (i >= arity){
                break;
            }
        }
        return l;
    }

    public static Clause induced(Clause clause, Set<? extends Term> terms){
        List<Literal> literals = new ArrayList<Literal>();
        outerLoop: for (Literal l : clause.literals()){
            for (int i = 0; i < l.arity(); i++){
                if (!terms.contains(l.get(i))){
                    continue outerLoop;
                }
            }
            literals.add(l);
        }
        return new Clause(literals);
    }
}
