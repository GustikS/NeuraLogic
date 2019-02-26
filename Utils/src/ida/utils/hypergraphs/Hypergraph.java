/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ida.utils.hypergraphs;

import ida.utils.Sugar;
import ida.utils.collections.Counters;
import ida.utils.collections.MultiMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ondrejkuzelka on 26/06/14.
 */
public class Hypergraph {

    private int lastID = -1;

    private Set<Integer> vertices = new HashSet<Integer>();

    private MultiMap<Integer,Integer> edges = new MultiMap<Integer,Integer>();

    //vertex -> edges iterable which it is contained
    private MultiMap<Integer,Integer> vertex2edges = new MultiMap<Integer,Integer>();

    private Counters<Integer> vertexDegrees = new Counters<Integer>();

    private Counters<Integer> edgeDegrees = new Counters<Integer>();

    private MultiMap<Integer,Integer> degree2vertex = new MultiMap<Integer,Integer>();

    private MultiMap<Integer,Integer> degree2edge = new MultiMap<Integer,Integer>();

    private Counters<Integer> edgesFullyContainedInOtherEdges = new Counters<Integer>();

    public Hypergraph(){}

    public Hypergraph(Collection<Set<Integer>> edges){
        for (Set<Integer> edge : edges){
            this.addEdge(edge);
        }
    }

    public Hypergraph copy(){
        Hypergraph h = new Hypergraph();
        for (Map.Entry<Integer,Set<Integer>> entry : this.edges.entrySet()){
            h.addEdge(entry.getKey(), entry.getValue());
        }
        return h;
    }

    public void addEdge(Set<Integer> edge){
        int eid = ++lastID;
        this.addEdge(eid, edge);
    }

    public void addEdge(int edgeID, Set<Integer> edge){
        if (!this.edges.containsKey(edgeID)) {
            this.vertices.addAll(edge);
            this.edges.putAll(edgeID, edge);
            this.degree2edge.remove(this.edgeDegrees.get(edgeID), edgeID);
            for (int vertex : edge) {
                this.vertex2edges.put(vertex, edgeID);
                this.degree2vertex.remove(this.vertexDegrees.get(vertex), vertex);
                this.degree2vertex.put(this.vertexDegrees.incrementPre(vertex), vertex);
                this.edgeDegrees.increment(edgeID);
            }
            this.degree2edge.put(this.edgeDegrees.get(edgeID), edgeID);

            this.edgesFullyContainedInOtherEdges.add(edgeID, numSuperEdges(this.edges.get(edgeID)));

            Set<Integer> alreadyProcessed = new HashSet<Integer>();
            for (int vertex : edge) {
                for (int ee : this.vertex2edges.get(vertex)) {
                    if (!alreadyProcessed.contains(ee) && ee != edgeID) {
                        alreadyProcessed.add(ee);
                        if (Sugar.isSubsetOf(this.edges.get(ee), edge)) {
                            this.edgesFullyContainedInOtherEdges.increment(ee);
                        }
                    }
                }
            }
        }
    }

    public void removeEdge(int edgeID){
        if (this.edges.containsKey(edgeID)) {
            this.degree2edge.remove(this.edgeDegrees.get(edgeID), edgeID);
            for (int v : edges.get(edgeID)) {
                this.degree2vertex.remove(this.vertexDegrees.get(v), v);
                if (this.vertexDegrees.get(v) > 1) {
                    this.degree2vertex.put(this.vertexDegrees.decrementPre(v), v);
                }
                this.edgeDegrees.decrement(edgeID);
                this.vertex2edges.remove(v, edgeID);
            }
            Set<Integer> edge = this.edges.remove(edgeID);
            this.edgesFullyContainedInOtherEdges.add(edgeID, -this.edgesFullyContainedInOtherEdges.get(edgeID));

            Set<Integer> alreadyProcessed = new HashSet<Integer>();
            for (int vertex : edge) {
                for (int ee : this.vertex2edges.get(vertex)) {
                    if (!alreadyProcessed.contains(ee) && ee != edgeID) {
                        alreadyProcessed.add(ee);
                        if (Sugar.isSubsetOf(this.edges.get(ee), edge)) {
                            this.edgesFullyContainedInOtherEdges.decrement(ee);
                        }
                    }
                }
            }
        }
    }

    public void removeVertex(int vertex){
        if (this.vertices.contains(vertex)) {
            Set<Integer> edgesContainingVertex = this.vertex2edges.get(vertex);
            this.vertices.remove(vertex);
            this.degree2vertex.remove(vertexDegrees.get(vertex), vertex);
            for (int edge : this.vertex2edges.get(vertex)) {
                this.edges.remove(edge, vertex);
                this.degree2edge.remove(this.edgeDegrees.get(edge), edge);
                if (this.edgeDegrees.get(edge) > 1){
                    this.degree2edge.put(this.edgeDegrees.decrementPre(edge), edge);
                }
            }
            this.vertexDegrees.add(vertex, -this.vertexDegrees.get(vertex));

            for (int edge : edgesContainingVertex){
                this.edgesFullyContainedInOtherEdges.add(
                        edge, numSuperEdges(this.edges.get(edge))-
                        numSuperEdges(Sugar.setFromCollections(this.edges.get(edge), Sugar.set(vertex)))
                );

            }
        }
    }

    public void removeVertices(Collection<Integer> vertices){
        for (Integer vertex : vertices){
            this.removeVertex(vertex);
        }
    }

    public void removeEdges(Collection<Integer> edges){
        for (Integer edge : edges){
            this.removeEdge(edge);
        }
    }

    protected Set<Integer> getVerticesByDegree(int degree){
        return this.degree2vertex.get(degree);
    }

    protected Set<Integer> getEdgesByDegree(int degree){
        return this.degree2edge.get(degree);
    }

    public int countEdges(){
        return this.edges.size();
    }

    public int countVertices(){
        return this.vertices.size();
    }

    private int numSuperEdges(Set<Integer> edge){
        Set<Integer> candidateSuperEdges = null;
        for (int vertex : edge){
            if (candidateSuperEdges == null){
                candidateSuperEdges = this.vertex2edges.get(vertex);
            } else {
                candidateSuperEdges = Sugar.intersection(candidateSuperEdges, this.vertex2edges.get(vertex));
            }
        }
        if (candidateSuperEdges == null){
            return 0;
        } else {
            return candidateSuperEdges.size() - 1;
        }
    }

    protected Set<Integer> getEdgesContainedInAtLeastKEdges(int k){
        Set<Integer> retVal = new HashSet<Integer>();
        for (int edge : this.edges.keySet()){
            if (this.edgesFullyContainedInOtherEdges.get(edge) >= k){
                retVal.add(edge);
            }
        }
        return retVal;
    }

//    public static void main(String[] args){
//        Hypergraph h = new Hypergraph();
//        h.addEdge(1, Sugar.<Integer>set(1,2,3));
//        h.addEdge(2, Sugar.<Integer>set(1,2,4,5));
//        h.addEdge(3, Sugar.<Integer>set(2,3,5));
//
//        System.out.println("Num edges: "+h.countEdges());
//        System.out.println("Degree 1 vertices: "+h.getVerticesByDegree(1));
//        System.out.println("Degree 2 vertices: "+h.getVerticesByDegree(2));
//        System.out.println("Degree 3 vertices: "+h.getVerticesByDegree(3));
//
//        System.out.println("Now removing edge no 2...");
//
//        h.removeEdge(2);
//
//        System.out.println("Num edges: "+h.countEdges());
//        System.out.println("Degree 0 vertices: "+h.getVerticesByDegree(0));
//        System.out.println("Degree 1 vertices: "+h.getVerticesByDegree(1));
//        System.out.println("Degree 2 vertices: "+h.getVerticesByDegree(2));
//        System.out.println("Degree 3 vertices: "+h.getVerticesByDegree(3));
//
//        System.out.println("Now removing edge no 1...");
//
//        h.removeEdge(1);
//
//        System.out.println("Num edges: "+h.countEdges());
//        System.out.println("Degree 0 vertices: "+h.getVerticesByDegree(0));
//        System.out.println("Degree 1 vertices: "+h.getVerticesByDegree(1));
//        System.out.println("Degree 2 vertices: "+h.getVerticesByDegree(2));
//        System.out.println("Degree 3 vertices: "+h.getVerticesByDegree(3));
//
//        System.out.println("Now removing edge no 3...");
//
//        h.removeEdge(3);
//
//        System.out.println("Num edges: "+h.countEdges());
//        System.out.println("Degree 0 vertices: "+h.getVerticesByDegree(0));
//        System.out.println("Degree 1 vertices: "+h.getVerticesByDegree(1));
//        System.out.println("Degree 2 vertices: "+h.getVerticesByDegree(2));
//        System.out.println("Degree 3 vertices: "+h.getVerticesByDegree(3));
//    }
}
