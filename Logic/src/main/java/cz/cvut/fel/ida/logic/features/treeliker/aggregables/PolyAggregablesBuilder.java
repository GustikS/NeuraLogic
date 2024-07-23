/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.aggregables;


import cz.cvut.fel.ida.logic.features.treeliker.AggregablesBuilder;
import cz.cvut.fel.ida.logic.features.treeliker.Aggregables;
import cz.cvut.fel.ida.logic.features.treeliker.Example;
import cz.cvut.fel.ida.logic.features.treeliker.PredicateDefinition;
import cz.cvut.fel.ida.utils.generic.tuples.Tuple;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of interface AggregablesBuilder for constructing Aggregables of instances of class
 * PolyAggregable.
 * 
 * @author Ondra
 */
public class PolyAggregablesBuilder implements AggregablesBuilder {

    private final static Map<Integer,PolyAggregablesBuilder> one = new HashMap<Integer,PolyAggregablesBuilder>();
    
    //degree -> empty aggregable
    private static PolyAggregable empty;
    
    private int maxDegree;
    
    private PolyAggregablesBuilder(int maxDegree){
        this.maxDegree = maxDegree;
        this.empty = new PolyAggregable(0, maxDegree, new double[0], 1);
    }
    
    /**
     * Parametrized singleton-method - creates one PolyAggregablesBuilder for every value of maxDegree (which is used when calling the method construct).
     * @param maxDegree maximum degree of monomials that this PolyAggregablesBuilder should build.
     * @return
     */
    public static PolyAggregablesBuilder construct(int maxDegree){
        synchronized (one){
            PolyAggregablesBuilder pab = null;
            if ((pab = one.get(maxDegree)) == null){
                pab = new PolyAggregablesBuilder(maxDegree);
                one.put(maxDegree, pab);
            }
            return pab;
        }
    }
    
    public Aggregables construct(PredicateDefinition def, IntegerSet literalIDs, Example example) {
        Aggregables aggregables = new Aggregables(literalIDs.size());
        if (def.containsAggregator()){
            int aggregationArgument = 0;
            int[] modes = def.modes();
            for (int i = 0; i < def.arity(); i++){
                if (modes[i] == PredicateDefinition.AGGREGATOR){
                    aggregationArgument = i;
                    break;
                }
            }
            //we assume that there is at most one aggregation variable in a literal - should 
            //be generalized in the future (although it is even now w.l.o.g.)
            List<Tuple<Integer>> exponents = PolyAggregable.allMonomialExponents(1, maxDegree);
            for (int literal : literalIDs.values()){
                double val = Double.parseDouble(Example.termToString(example.getTerm(literal, aggregationArgument)));
                double[] values = new double[exponents.size()];
                for (int i = 0; i < exponents.size(); i++){
                    values[i] = Math.pow(val, exponents.get(i).get(0));
                }
                aggregables.add(new PolyAggregable(1, maxDegree, values, 1));
            }
        } else {
            for (int i = 0; i < literalIDs.size(); i++){
                aggregables.add(empty);
            }
        }
        return aggregables;
    }
}
