/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker.utils.graphs;

import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.*;

/**
 * A class for simple problems with directed graphs (not very good but sufficient for the simple
 * problems needed for the feature construction algorithms).
 * 
 * @author Ondra
 */
public class DirectedGraph {

    private Map<Integer, DirectedVertex> vertices = new HashMap<Integer, DirectedVertex>();
    
    private Map<Integer, DirectedVertex> inDegreeZeroVertices = new HashMap<Integer, DirectedVertex>();
    
    private Map<Integer, DirectedVertex> outDegreeZeroVertices = new HashMap<Integer, DirectedVertex>();
    
    private Map<Integer, DirectedVertex> inDegreeOneVertices = new HashMap<Integer, DirectedVertex>();
    
    private Map<Integer, DirectedVertex> outDegreeOneVertices = new HashMap<Integer, DirectedVertex>();
    
    /**
     * Creates a new instance of class DirectedGraph containing the given vertices,
     * @param vertices the vertices in the graph (vertices bear informatiojn about 
     * their mutual interconnectedness)
     */
    public DirectedGraph(Collection<DirectedVertex> vertices){
        for (DirectedVertex vertex : vertices){
            this.vertices.put(vertex.id(), vertex);
            addToDegreeVertices(vertex);
        }
    }
    
    /**
     * Checks if the graph is simply connected (i.e. it does nhot check strong-connectedness).
     * @return true if the graph is simply connected
     */
    public boolean isConnected(){
        HashSet<DirectedVertex> closed = new HashSet<DirectedVertex>();
        LinkedList<DirectedVertex> open = new LinkedList<DirectedVertex>();
        open.add(Sugar.chooseOne(vertices));
        while (open.size() > 0){
            DirectedVertex v = open.poll();
            for (DirectedVertex expanded : v.outVertices()){
                if (!closed.contains(expanded)){
                    open.add(expanded);
                }
            }
            closed.add(v);
        }
        return closed.size() == vertices.size();
    }
    
    @Override
    public DirectedGraph clone(){
        Map<Integer,DirectedVertex> map = new HashMap<Integer,DirectedVertex>();
        List<DirectedVertex> verts = new ArrayList<DirectedVertex>();
        for (DirectedVertex v : this.vertices()){
            DirectedVertex clonedVertex = new DirectedVertex(v.id());
            clonedVertex.setNote(v.getNote());
            verts.add(clonedVertex);
            map.put(clonedVertex.id(), clonedVertex);
        }
        for (DirectedVertex v : this.vertices()){
            for (DirectedVertex outAdj : v.outVertices()){
                if (v.outDistance(outAdj) == Integer.MAX_VALUE)
                    DirectedVertex.connectDirected(map.get(v.id()), map.get(outAdj.id()));
                else
                    DirectedVertex.connectDirected(map.get(v.id()), map.get(outAdj.id()), v.outDistance(outAdj));
            }
        }
        return new DirectedGraph(verts);
    }
    
    /**
     * 
     * @return number of vertices in the graph
     */
    public int vertexCount(){
        return vertices.size();
    }
    
    /**
     * 
     * @return the collection of vertices in the graph
     */
    public Collection<DirectedVertex> vertices(){
        return vertices.values();
    }
    
    /**
     * 
     * @param id the ID of the vertex we want to get
     * @return the vertex with the given ID
     */
    public DirectedVertex getVertex(int id){
        return vertices.get(id);
    }
    
    /**
     * Temporarily removes (hides) the given vertex from the graph. It still keeps
     * information about to which vertices the vertex had been originally connected
     * so that it could later restore it using the method <em>restore</em>.
     * @param vertex the vertex to be temporarily removed
     */
    public void hide(DirectedVertex vertex){
        if (vertices.containsKey(vertex.id())){
            vertices.remove(vertex.id());
            Collection<DirectedVertex> inVertices = Sugar.listFromCollections(vertex.inVertices());
            Collection<DirectedVertex> outVertices = Sugar.listFromCollections(vertex.outVertices());
            vertex.disconnectFromInVertices();
            vertex.disconnectFromOutVertices();
            inDegreeOneVertices.remove(vertex.id());
            outDegreeOneVertices.remove(vertex.id());
            inDegreeZeroVertices.remove(vertex.id());
            outDegreeZeroVertices.remove(vertex.id());
            for (DirectedVertex adj : inVertices){
                addToDegreeVertices(adj);
                removeFromDegreeVertices(adj);
            }
            for (DirectedVertex adj : outVertices){
                addToDegreeVertices(adj);
                removeFromDegreeVertices(adj);
            }
        }
    }
    
    /**
     * Puts back the given vertex which was temporarily removed from the graph using
     * method <em>hide</em>.
     * @param vertex the vertex to be restored
     */
    public void restore(DirectedVertex vertex){
        if (!vertices.containsKey(vertex.id())){
            vertices.put(vertex.id(), vertex);
            vertex.restoreLinksToInVertices();
            vertex.restoreLinksToOutVertices();
            addToDegreeVertices(vertex);
            for (DirectedVertex adj : vertex.inVertices()){
                addToDegreeVertices(adj);
                removeFromDegreeVertices(adj);
            }
            for (DirectedVertex adj : vertex.outVertices()){
                addToDegreeVertices(adj);
                removeFromDegreeVertices(adj);
            }
        }
    }
    
    private void addToDegreeVertices(DirectedVertex v){
        if (v.outDegree() == 1)
            outDegreeOneVertices.put(v.id(), v);
        if (v.inDegree() == 1)
            inDegreeOneVertices.put(v.id(), v);
        if (v.outDegree() == 0)
            outDegreeZeroVertices.put(v.id(), v);
        if (v.inDegree() == 0)
            inDegreeZeroVertices.put(v.id(), v);
    }
    
    private void removeFromDegreeVertices(DirectedVertex v){
        if (v.outDegree() != 1 && outDegreeOneVertices.containsKey(v.id()))
            outDegreeOneVertices.remove(v.id());
        if (v.inDegree() != 1 && inDegreeOneVertices.containsKey(v.id()))
            inDegreeOneVertices.remove(v.id());
        if (v.outDegree() != 0 && outDegreeZeroVertices.containsKey(v.id()))
            outDegreeZeroVertices.remove(v.id());
        if (v.inDegree() != 0 && inDegreeZeroVertices.containsKey(v.id()))
            inDegreeZeroVertices.remove(v.id());
    }
    
    @Override
    public String toString(){
        return vertices.toString();
    }
    
    /**
     * 
     * @return
     */
    public Collection<DirectedVertex> inDegreeZeroVertices(){
        return this.inDegreeZeroVertices.values();
    }
    
    /**
     * 
     * @return all verteices from which there are no edges
     */
    public Collection<DirectedVertex> outDegreeZeroVertices(){
        return this.outDegreeZeroVertices.values();
    }
    
    /**
     * 
     * @return all verteices to which there are no edges
     */
    public Collection<DirectedVertex> inDegreeOneVertices(){
        return this.inDegreeOneVertices.values();
    }
}
