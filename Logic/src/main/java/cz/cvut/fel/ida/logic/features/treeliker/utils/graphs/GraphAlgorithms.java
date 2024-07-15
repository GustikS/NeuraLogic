/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker.utils.graphs;

import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.*;
/**
 * Class with some simple graph algorithms used by the feature construction algorithms.
 * @author Ondra
 */
public class GraphAlgorithms {
    
    /**
     * Computes lengths of shortest paths from all vertices to the given vertex in the given acyclic graph.
     * @param dg the acyclic graph
     * @param target the vertex to which the distances should be conputed
     * @return Map: key = a vertex V, value = distance from V to <em>target</em>
     */
    @SuppressWarnings("empty-statement")
    public static Map<DirectedVertex, Integer> shortestPathsInAcyclicGraphToVertex(DirectedGraph dg, DirectedVertex target){
        Map<DirectedVertex,Integer> distances = new HashMap<DirectedVertex,Integer>();
        List<DirectedVertex> topological = topologicalOrder(dg);
        Collections.reverse(topological);
        distances.put(target, 0);
        int i = 0;
        for (; i < topological.size() && !topological.get(i).equals(target); i++);
        for (; i < topological.size(); i++){
            DirectedVertex v = topological.get(i);
            for (DirectedVertex in : v.inVertices()){
                if (distances.containsKey(v)){
                    int distance = distances.get(v)+v.inDistance(in);
                    //System.out.println(in+": distance="+distances.get(v)+"+"+v.inDistance(in)+"="+distance);
                    if (!distances.containsKey(in) || distances.get(in) > distance)
                        distances.put(in, distance);
                } else {
                    distances.put(in, Integer.MAX_VALUE);
                }
            }
        }
        return distances;
    }
    
    /**
     * Creates topological ordering of vertices in the given acyclic graph.
     * @param graph the acyclic graph
     * @return list of topologically ordered vertices
     */
    public static List<DirectedVertex> topologicalOrder(DirectedGraph graph){
        HashMap<Integer, DirectedVertex> vertices = new HashMap<Integer, DirectedVertex>();
        for (DirectedVertex v : graph.vertices()){
            vertices.put(v.id(), v);
        }
        graph = graph.clone();
        List<DirectedVertex> list = new ArrayList<DirectedVertex>();
        while (graph.vertexCount() > 0){
            if (graph.inDegreeZeroVertices().size() > 0){
                for (DirectedVertex dv : Sugar.listFromCollections(graph.inDegreeZeroVertices())){
                    list.add(dv);
                    graph.hide(dv);
                }
            } else {
                return null;
            }
        }
        List<DirectedVertex> retVal = new ArrayList<DirectedVertex>();
        for (DirectedVertex v : list){
            retVal.add(vertices.get(v.id()));
        }
        return retVal;
    }

    /**
     * 
     * @param dg
     * @return
     */
    public static List<Set<DirectedVertex>> connectedComponents(DirectedGraph dg){
        dg = dg.clone();
        List<Set<DirectedVertex>> components = new ArrayList<Set<DirectedVertex>>();
        while (dg.vertexCount() > 0){
            DirectedVertex start = Sugar.chooseOne(dg.vertices());
            Stack<DirectedVertex> open = new Stack<DirectedVertex>();
            Set<DirectedVertex> closed = new HashSet<DirectedVertex>();
            open.push(start);
            closed.add(start);
            while (!open.empty()){
                DirectedVertex expanded = open.pop();
                for (DirectedVertex v : expanded.outVertices()){
                    if (!closed.contains(v)){
                        open.push(v);
                        closed.add(v);
                    }
                }
                for (DirectedVertex v : expanded.inVertices()){
                    if (!closed.contains(v)){
                        open.push(v);
                        closed.add(v);
                    }
                }
                dg.hide(expanded);
            }
            components.add(closed);
        }
        return components;
    }

//    /**
//     * 
//     * @param a
//     */
//    public static void main(String a[]){
//        DirectedVertex v1 = new DirectedVertex(1);
//        DirectedVertex v2 = new DirectedVertex(2);
//        DirectedVertex v3 = new DirectedVertex(3);
//        DirectedVertex v4 = new DirectedVertex(4);
//        DirectedVertex.connectDirected(v1, v3, 2);
//        DirectedVertex.connectDirected(v2, v3, 1);
//        //DirectedVertex.connectDirected(v3, v4, 5);
//        DirectedGraph graph = new DirectedGraph(Sugar.list(v1,v2,v3,v4));
//        System.out.println(shortestPathsInAcyclicGraphToVertex(graph, v4));
//        System.out.println(GraphAlgorithms.connectedComponents(graph));
//    }
}
