/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.utils.graphs.DirectedGraph;
import cz.cvut.fel.ida.logic.features.treeliker.utils.graphs.DirectedVertex;
import cz.cvut.fel.ida.logic.features.treeliker.utils.graphs.GraphAlgorithms;
import cz.cvut.fel.ida.logic.features.treeliker.PredicateDefinition;
import cz.cvut.fel.ida.utils.math.collections.*;

import java.util.*;
/**
 * Helper-class with useful methods fot construction of features.
 * @author Ondra
 */
public class FeatureSearchUtils {
    
//
    /**
     * Builds a topologically ordered list of PredicateDefinitions which can be then used by the feature construction algorithms.
     * @param definitions possibly unordered collection of PredicateDefinition objects
     * @return
     */
    public static List<PredicateDefinition> buildPredicateList(Collection<PredicateDefinition> definitions){
        List<PredicateDefinition> retVal = topologicalOrderOnTypes(buildPartialOrderOnTypes(definitions));
        for (PredicateDefinition def : definitions){
            if (hasNoInputsAndOutputs(def)){
                retVal.add(def);
            }
        }
        return retVal;
    }
    
    private static boolean hasNoInputsAndOutputs(PredicateDefinition def){
        for (int i = 0; i < def.arity(); i++){
            if (def.modes()[i] == PredicateDefinition.INPUT || def.modes()[i] == PredicateDefinition.OUTPUT){
                return false;
            }
        }
        return true;
    }

    static List<PredicateDefinition> topologicalOrderOnTypes(MultiMap<PredicateDefinition,PredicateDefinition> prerequisities){
        ValueToIndex<PredicateDefinition> definitions = new ValueToIndex<PredicateDefinition>();
        HashMap<Integer, DirectedVertex> map = new HashMap<Integer,DirectedVertex>();
        for (Map.Entry<PredicateDefinition,Set<PredicateDefinition>> entry : prerequisities.entrySet()){
            int startVertexID = definitions.valueToIndex(entry.getKey());
            if (!map.containsKey(startVertexID)){
                map.put(startVertexID, new DirectedVertex(startVertexID));
            }
            DirectedVertex start = map.get(startVertexID);
            for (PredicateDefinition def : entry.getValue()){
                int endVertexID = definitions.valueToIndex(def);
                if (!map.containsKey(endVertexID)){
                    map.put(endVertexID, new DirectedVertex(endVertexID));
                }
                DirectedVertex end = map.get(endVertexID);
                DirectedVertex.connectDirected(end, start);
            }
        }
        DirectedGraph graph = new DirectedGraph(map.values());
        List<DirectedVertex> ordered = GraphAlgorithms.topologicalOrder(graph);
        List<PredicateDefinition> list = new ArrayList<PredicateDefinition>();
        //if it is null then it means that the predicate definitions contain a cycle
        if (ordered != null){
            for (DirectedVertex dv : ordered){
                list.add(definitions.indexToValue(dv.id()));
            }
        }
        return list;
    }

    static MultiMap<PredicateDefinition,PredicateDefinition> buildPartialOrderOnTypes(Collection<PredicateDefinition> definitions){
        MultiMap<Integer,PredicateDefinition> defByType = new MultiMap<Integer,PredicateDefinition>();
        for (PredicateDefinition def : definitions){
            int[] modes = def.modes();
            int[] types = def.types();
            for (int i = 0; i < def.types().length; i++){
                if (modes[i] == PredicateDefinition.INPUT)
                    defByType.put(types[i], def);
            }
        }
        MultiMap<PredicateDefinition,PredicateDefinition> bag = new MultiMap<PredicateDefinition,PredicateDefinition>();
        for (PredicateDefinition def : definitions){
            int[] modes = def.modes();
            int[] types = def.types();
            for (int i = 0; i < def.types().length; i++){
                if (modes[i] == PredicateDefinition.OUTPUT){
                    for (PredicateDefinition prereq : defByType.get(types[i])){
                        bag.put(def, prereq);
                    }
                }
            }
        }
        return bag;
    }

}