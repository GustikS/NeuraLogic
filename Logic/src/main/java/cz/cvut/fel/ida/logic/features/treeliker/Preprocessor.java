/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;
/**
 * Class implementing some methods used when preprocessing learning examples (e.g. when
 * we have a large example, but not all literals are "reachable" using features built according
 * to selected language bias).
 * 
 * @author admin
 */
public class Preprocessor {

    /**
     * Finds all reachable literals (= literals which have some relevance for features
     * built according to given language bias).
     * 
     * @param template parsed template (i.e. set of PredicateDefinitions) representing the language bias
     * @param globalConstants glo0bal constants which need not be reachable using features complying with the language
     * bias but which should nevertheless be used.
     * @param clause the learning example which should be preprocessed
     * @return the resulting example consisting of reachable literals + global constants
     */
    public static Clause reachableLiterals(Set<PredicateDefinition> template, List<PredicateDefinition> globalConstants, Clause clause){
        MultiMap<PredicateDefinition, Literal> closed = new MultiMap<PredicateDefinition,Literal>();
        Stack<Pair<PredicateDefinition,Literal>> open = new Stack<Pair<PredicateDefinition,Literal>>();
        Set<Literal> literals = new LinkedHashSet<Literal>();
        for (PredicateDefinition def : roots(template)){
            for (Literal l : clause.getLiteralsByPredicate(def.stringPredicate())){
                if (l.arity() == def.arity()){
                    open.push(new Pair<PredicateDefinition,Literal>(def, l));
                    closed.put(def, l);
                }
            }
        }
        MultiMap<Integer,PredicateDefinition> defBag = buildDefBag(template);
        while (!open.isEmpty()){
            Pair<PredicateDefinition,Literal> pair = open.pop();
            Literal l = pair.s;
            PredicateDefinition def = pair.r;
            literals.add(l);
            int[] originalModes = def.originalModes();
            int[] types = def.types();
            for (int i = 0; i < originalModes.length; i++){
                if (originalModes[i] == PredicateDefinition.OUTPUT){
                    for (Literal neighbour : clause.getLiteralsByTerm(l.get(i))){
                        for (int j = 0; j < neighbour.arity(); j++){
                            if (neighbour.get(j).equals(l.get(i))){
                                for (PredicateDefinition candDef : defBag.get(types[i])){
                                    if (candDef.arity() == neighbour.arity() && candDef.stringPredicate().equals(neighbour.predicate().name) &&
                                            candDef.input() == j && candDef.types()[j] == types[i]){
                                        if (!closed.get(candDef).contains(neighbour)){
                                            open.add(new Pair<PredicateDefinition,Literal>(candDef, neighbour));
                                            closed.put(candDef, neighbour);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (PredicateDefinition gc : globalConstants){
            literals.addAll(clause.getLiteralsByPredicate(gc.stringPredicate()));
        }
        return new Clause(literals);
    }

    private static Set<PredicateDefinition> roots(Set<PredicateDefinition> defs){
        Set<PredicateDefinition> retVal = new HashSet<PredicateDefinition>();
        for (PredicateDefinition def : defs){
            if (def.isOutputOnly()){
                retVal.add(def);
            }
        }
        return retVal;
    }

    private static MultiMap<Integer,PredicateDefinition> buildDefBag(Set<PredicateDefinition> defs){
        MultiMap<Integer,PredicateDefinition> defBag = new MultiMap<Integer,PredicateDefinition>();
        for (PredicateDefinition def : defs){
            for (int i = 0; i < def.modes().length; i++){
                if (def.modes()[i] == PredicateDefinition.INPUT){
                    defBag.put(def.types()[i], def);
                }
            }
        }
        return defBag;
    }
}
