/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.*;
import cz.cvut.fel.ida.logic.features.treeliker.utils.graphs.DirectedGraph;
import cz.cvut.fel.ida.logic.features.treeliker.utils.graphs.DirectedVertex;
import cz.cvut.fel.ida.logic.features.treeliker.utils.graphs.GraphAlgorithms;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing methods for checking if a sub-block can be extended to a full
 * feature complying to the given language bias.
 * 
 * @author Ondra
 */
public class MaxSizeChecker implements SyntaxChecker {

    private int maxSize;
    
    private Map<Integer,Integer> sizesDown;
    
    private Map<Integer,Integer> sizesUp;
    
    /**
     * Creates a new instance of class MaxSizeChecker
     * @param maxSize maximum allowed size of features 
     * @param topologicallyOrderedTemplate list of PredicateDefinition objects sorted topologically
     */
    public MaxSizeChecker(int maxSize, List<PredicateDefinition> topologicallyOrderedTemplate){
        this.maxSize = maxSize;
        this.sizesDown = computeSmallestFeatureSizesDown(topologicallyOrderedTemplate);
        this.sizesUp = computeSmallestFeatureSizesUp(topologicallyOrderedTemplate, sizesDown);
    }
    
    public boolean check(Block block) {
        int s = block.size();
        int[] modes = block.definition().modes();
        int[] types = block.definition().types();
        for (int i = 0; i < block.arity(); i++){
            if (block.children(i) == null && modes[i] == PredicateDefinition.OUTPUT){
                s += sizesDown.get(types[i]);
            }
        }
        //System.out.println(block+" --> "+s);
        return s <= maxSize;
    }

    public boolean check(Join join) {
        //System.out.println(join+" --> "+join.numLiterals()+sizesUp.get(join.first().definition().types()[join.first().input()]));
        return join.numLiterals()+sizesUp.get(join.first().definition().types()[join.first().input()]) <= maxSize;
    }
    
    private static Map<Integer,Integer> computeSmallestFeatureSizesUp(List<PredicateDefinition> topologicallyOrdered, Map<Integer,Integer> smallestFeaturesDown){
        MultiMap<Integer,PredicateDefinition> havingOutputType = new MultiMap<Integer,PredicateDefinition>();
        HashMap<Integer,PredicateDefinition> definitions = new HashMap<Integer,PredicateDefinition>();
        for (PredicateDefinition def : topologicallyOrdered){
            int[] modes = def.modes();
            int[] types = def.types();
            for (int i = 0; i < modes.length; i++){
                if (modes[i] == PredicateDefinition.OUTPUT){
                    havingOutputType.put(types[i], def);
                }
            }
            definitions.put(def.id(), def);
        }
        HashMap<Integer, DirectedVertex> vertices = new HashMap<Integer,DirectedVertex>();
        DirectedVertex dummy = new DirectedVertex(-1);
        vertices.put(dummy.id(), dummy);
        for (PredicateDefinition def : topologicallyOrdered){
            if (!def.isOutputOnly()){
                DirectedVertex from = null;
                if (vertices.containsKey(def.id())){
                    from = vertices.get(def.id());
                } else {
                    from = new DirectedVertex(def.id());
                }
                int inputType = def.types()[def.input()];
                Set<PredicateDefinition> havingDesiredOutput = havingOutputType.get(inputType);
                for (PredicateDefinition desiredDef : havingDesiredOutput){
                    DirectedVertex to = null;
                    if (vertices.containsKey(desiredDef.id())){
                        to = vertices.get(desiredDef.id());
                    } else {
                        to = new DirectedVertex(desiredDef.id());
                    }
                    int distance = 1;
                    boolean desiredTypeFound = false;
                    for (int j = 0; j < desiredDef.modes().length; j++){
                        if (desiredDef.modes()[j] == PredicateDefinition.OUTPUT && (desiredTypeFound || desiredDef.types()[j] != inputType)){
                            if (smallestFeaturesDown.containsKey(desiredDef.types()[j])){
                                distance += smallestFeaturesDown.get(desiredDef.types()[j]);
                            } else {
                                distance = Integer.MAX_VALUE;
                            }
                        }
                        if (desiredDef.types()[j] == inputType){
                            desiredTypeFound = true;
                        }
                    }
                    DirectedVertex.connectDirected(from, to, distance);
                    vertices.put(from.id(), from);
                    vertices.put(to.id(), to);
                }
            } else {
                DirectedVertex from = null;
                if (vertices.containsKey(def.id())){
                    from = vertices.get(def.id());
                } else {
                    from = new DirectedVertex(def.id());
                }
                DirectedVertex.connectDirected(from, dummy, 0);
            }
        }
        DirectedGraph graph = new DirectedGraph(vertices.values());
        Map<DirectedVertex,Integer> distances = GraphAlgorithms.shortestPathsInAcyclicGraphToVertex(graph, dummy);
        Map<Integer,Integer> distancesFromTypes = new HashMap<Integer,Integer>();
        for (Map.Entry<DirectedVertex,Integer> entry : distances.entrySet()){
            if (entry.getKey().id() != -1){
                PredicateDefinition def = definitions.get(entry.getKey().id());
                if (!def.isOutputOnly()){
                    int inputType = def.types()[def.input()];
                    distancesFromTypes.put(inputType, entry.getValue());
                }
            }
        }
        return distancesFromTypes;
    }

    //[type,size]
    private static HashMap<Integer,Integer> computeSmallestFeatureSizesDown(List<PredicateDefinition> topologicallyOrdered){
        HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
        for (PredicateDefinition def : topologicallyOrdered){
            int size = 1;
            if (def.isConstant() && !TreeLikerSettings.COUNT_CONSTANTS_AS_LITERALS){
                size = 0;
            }
            int[] modes = def.modes();
            int[] types = def.types();
            for (int i = 0; i < def.arity(); i++){
                if (modes[i] == PredicateDefinition.OUTPUT){
                    if (map.containsKey(types[i])){
                        size += map.get(types[i]);
                    } else {
                        size = Integer.MAX_VALUE;
                    }
                }
            }
            if (!map.containsKey(types[def.input()]) || map.get(types[def.input()]) > size){
                map.put(types[def.input()], size);
            }
        }
        return map;
    }
    
}
