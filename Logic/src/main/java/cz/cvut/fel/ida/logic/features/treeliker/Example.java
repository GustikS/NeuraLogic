/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Triple;
import cz.cvut.fel.ida.utils.math.StringUtils;
import cz.cvut.fel.ida.utils.math.collections.IntegerMultiMap;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;
import cz.cvut.fel.ida.utils.math.collections.ValueToIndex;

import java.util.*;

/**
 * Class for efficiently representing learning examples.
 * 
 * @author Ondra
 */
public class Example {
    
    private int id;
    
    private int maxArity;
    
    private static int lastId;

    private static ValueToIndex<String> termsToIntegers = new ValueToIndex<String>();

    private Map<Integer,int[]> termsInLiterals = new HashMap<Integer,int[]>();

    //predicate -> literals
    private IntegerMultiMap<Integer> predicateMultiMap;

    //[predicate, argument, term] -> literals
    private IntegerMultiMap<Triple<Integer,Integer,Integer>> literalMultiMap;

    //[predicate, argument] -> term
    private IntegerMultiMap<Pair<Integer,Integer>> predicateTermMultiMap = new IntegerMultiMap<Pair<Integer,Integer>>();

    private HashMap<Integer, Literal> originalLiterals = new HashMap<Integer,Literal>();

    /**
     * Creates a new instance of class Examlpe based on Clause e (which is original representation
     * of the learning example which is not very efficient).
     * @param e the learning example in the for of an instance of class Clause (which can be created e.g.
     * from string representation of a clause using the method Clause.parse(...)
     */
    public Example(Clause e){
        e = preprocessClause(e);
        buildLiteralMap(e);
        buildPredicateMap(e);
        buildTermsInLiterals(e);
        buildPredicateTermMultiMap(e);
        this.id = lastId++;
    }

    private Clause preprocessClause(Clause clause){
        List<Literal> literals = new ArrayList<Literal>();
        for (Literal l : clause.literals()){
            this.maxArity = Math.max(this.maxArity, l.arity());
            Literal newLit = new Literal(l.predicate());
            for (int i = 0; i < l.arity(); i++){
                newLit.set(l.get(i), i);
            }
            literals.add(newLit);
        }
        return new Clause(literals);
    }

    private void buildLiteralMap(Clause e){
        this.literalMultiMap = new IntegerMultiMap<Triple<Integer,Integer,Integer>>();
        MultiMap<Triple<Integer,Integer,Integer>,Integer> hb = new MultiMap<Triple<Integer,Integer,Integer>,Integer>();
        Set<Integer> doubleTerms = new HashSet<Integer>();
        for (Literal l : e.literals()){
            for (int i = 0; i < l.arity(); i++){
                hb.put(new Triple<Integer,Integer,Integer>(PredicateDefinition.predicateToInteger(l.predicate().name, l.arity()), i, Example.termsToIntegers.valueToIndex(l.get(i).toString())), l.id());
                if (StringUtils.isNumeric(l.get(i).name())){
                    doubleTerms.add(Example.termsToIntegers.valueToIndex(l.get(i).toString()));
                }
            }
            this.originalLiterals.put(l.id(), l);
        }
        for (Map.Entry<Triple<Integer,Integer,Integer>,Set<Integer>> entry : hb.entrySet()){
            this.literalMultiMap.add(entry.getKey(), IntegerSet.createIntegerSet(entry.getValue()));
        }
    }

    private void buildPredicateMap(Clause e){
        this.predicateMultiMap = new IntegerMultiMap<Integer>();
        for (String predicate : e.predicates()){
            MultiMap<Integer,Integer> lits = new MultiMap<Integer,Integer>();
            for (Literal l : e.getLiteralsByPredicate(predicate)){
                lits.put(l.arity(), l.id());
            }
            for (Map.Entry<Integer,Set<Integer>> entry : lits.entrySet()){
                this.predicateMultiMap.add(PredicateDefinition.predicateToInteger(predicate, entry.getKey()), IntegerSet.createIntegerSet(entry.getValue()));
            }
        }
    }

    private void buildPredicateTermMultiMap(Clause e){
        MultiMap<Pair<Integer,Integer>,Integer> auxMultiMap = new MultiMap<Pair<Integer,Integer>,Integer>();
        for (Literal l : e.literals()){
            for (int i = 0; i < l.arity(); i++){
                auxMultiMap.put(new Pair<Integer,Integer>(PredicateDefinition.predicateToInteger(l.predicate().name, l.arity()), i), Example.termsToIntegers.valueToIndex(l.get(i).name()));
            }
        }
        this.predicateTermMultiMap = IntegerMultiMap.createIntegerMultiMap(auxMultiMap);
    }

    private void buildTermsInLiterals(Clause e){
        MultiMap<Pair<Integer,Integer>,Integer> hb = new MultiMap<Pair<Integer,Integer>,Integer>();
        for (Literal l : e.literals()){
            int args[] = new int[l.arity()];
            for (int i = 0; i < l.arity(); i++){
                hb.put(new Pair<Integer,Integer>(PredicateDefinition.predicateToInteger(l.predicate().name, l.arity()), i), Example.termsToIntegers.valueToIndex(l.get(i).toString()));
                args[i] = Example.termsToIntegers.valueToIndex(l.get(i).toString());
            }
            this.termsInLiterals.put(l.id(), args);
        }
    }

    /**
     * 
     * @param integer integer representation of literal - i.e. its unique identifier (for example used 
     * in domains of Blocks etc.), the unique identifier is guaranteed to be unique only w.r.t. literals
     * from one given Example
     * @return literal from this example whose unique identifier is <em>integer</em>
     */
    public Literal integerToLiteral(int integer){
        return this.originalLiterals.get(integer);
    }

    /**
     * 
     * @param predicate integer representration of predicate symbol
     * @return set of all identifiers of literals which have predicate symbol (name and arity) <em>predicate</em>
     */
    public IntegerSet getLiteralDomain(int predicate){
        IntegerSet domain = this.predicateMultiMap.get(predicate);
        if (domain == null){
            return IntegerSet.emptySet;
        } else {
            return domain;
        }
    }

    /**
     * 
     * @param predicate predicate represented as integer
     * @param term term represented as integer
     * @param argument index of the argument
     * @return set of identifiers of literals which have predicate symbol <em>predicate</em> and
     * which, at the same time, have term <em>term</em> in the argument at position <em>argument</em>
     */
    public IntegerSet getLiteralDomain(int predicate, int term, int argument){
        return this.literalMultiMap.get(new Triple<Integer,Integer,Integer>(predicate, argument, term));
    }

    /**
     * 
     * @param predicate predicate represented as integer
     * @param terms set of terms represented as integers
     * @param argument index of the argument
     * @return set of identifiers of literals which have predicate symbol <em>predicate</em> and
     * which, at the same time, have one of the terms from the set <em>terms</em> in the argument at position <em>argument</em>
     */
    public IntegerSet getLiteralDomain(int predicate, IntegerSet terms, int argument){
        int domSize = 0;
        int index = 0;
        //these sets are disjoint
        IntegerSet[] domains = new IntegerSet[terms.size()];
        for (int term : terms.values()){
            domains[index] = getLiteralDomain(predicate, term, argument);
            domSize += domains[index].size();
            index++;
        }
        index = 0;
        int[] values = new int[domSize];
        for (int i = 0; i < domains.length; i++){
            int[] valuesI = domains[i].values();
            for (int j = 0; j < valuesI.length; j++){
                values[index] = valuesI[j];
                index++;
            }
        }
        Arrays.sort(values);
        return IntegerSet.createIntegerSetFromSortedArray(values);
    }

    /**
     * 
     * @param predicate predicate symbol represented as integer
     * @param argument index of argument 
     * @return the set of all terms (represented as integers) which are contained
     * in argument at position <em>argument</em> in literals with predicate symbol
     * represented by integer <em>predicate</em>
     */
    public IntegerSet getTermDomain(int predicate, int argument){
        return this.predicateTermMultiMap.get(new Pair<Integer,Integer>(predicate, argument));
    }

    /**
     * 
     * @param predicate predicate symbol represented as integer
     * @param argument index of argument 
     * @return the set of all terms (represented as integers) which are contained
     * in argument at position <em>argument</em> in literals with IDs contained in set <em>literalDomain</em>
     */
    public IntegerSet getTermDomain(IntegerSet literalDomain, int argument){
        IntegerSet retVal = null;
        Set<Integer> termDomain = new LinkedHashSet<Integer>();
        for (int i : literalDomain.values()){
            if (i != -1){
                termDomain.add(getTerm(i,argument));
            }
        }
        retVal = IntegerSet.createIntegerSet(termDomain);
        return retVal;
    }

    /**
     * 
     * @param literalID unique identifier of literal (valid in the particular example)
     * @param argument index of the argument
     * @return term in argument at position <em>argument</em> in literal with ID <em>literalID</em>
     */
    public int getTerm(int literalID, int argument){
        int[] args = null;
        if ((args = this.termsInLiterals.get(literalID)) != null){
            return args[argument];
        }
        return -1;
    }

    @Override
    public String toString(){
        return originalLiterals.toString();
    }
    
    /**
     * 
     * @return
     */
    public Collection<Literal> literals(){
        return this.originalLiterals.values();
    }
    
    @Override
    public int hashCode(){
        return this.id;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Example){
            return ((Example)o).id == this.id;
        }
        return false;
    }
    
    /**
     * 
     * @return
     */
    public int id(){
        return this.id;
    }

    /**
     * Converts integer representation of term to string
     * @param term integer representation of term
     * @return string representation of term
     */
    public static String termToString(int term){
        return Example.termsToIntegers.indexToValue(term);
    }

    /**
     * Converts string representation of term to its integer representation
     * @param term string representation of term
     * @return integer representation of term
     */
    public static int stringToTerm(String term){
        return Example.termsToIntegers.valueToIndex(term);
    }

    /**
     * 
     * @return IDs of all literals contained in this example.
     */
    public Set<Integer> literalIDs(){
        return this.originalLiterals.keySet();
    }
}
