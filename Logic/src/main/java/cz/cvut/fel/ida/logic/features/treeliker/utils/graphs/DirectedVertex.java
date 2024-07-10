/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker.utils.graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class which can represent a vertex in a directed graph.
 * 
 * @author Ondra
 */
public class DirectedVertex {
    
    private Map<Integer, DirectedVertex> outVertices = new HashMap<Integer, DirectedVertex>();
    
    private Map<Integer, Integer> outDistances = new HashMap<Integer, Integer>();
    
    private Map<Integer, Integer> inDistances = new HashMap<Integer, Integer>();
    
    private Map<Integer, DirectedVertex> inVertices = new HashMap<Integer, DirectedVertex>();
    
    private Map<Integer, DirectedVertex> removedOutConnections = new HashMap<Integer, DirectedVertex>();
    
    private Map<Integer, DirectedVertex> removedInConnections = new HashMap<Integer, DirectedVertex>();

    private Object note;

    private int id;
    
    private int inDegree;
    
    private int outDegree;
    
    /**
     * Creates a new instance of class DirectedVertex with the given ID (every vertex
     * must have a unique ID).
     * @param id the ID of the vertex
     */
    public DirectedVertex(int id){
        this.id = id;
    }
    
    /**
     * 
     * @return all vertices pointing to this vertex
     */
    public Collection<DirectedVertex> inVertices(){
        return inVertices.values();
    }
    
    /**
     * 
     * @return all vertices this vertex points to
     */
    public Collection<DirectedVertex> outVertices(){
        return outVertices.values();
    }
    
    /**
     * 
     * @return the in-degree
     */
    public int inDegree(){
        return inDegree;
    }
    
    /**
     * 
     * @return the out-degree
     */
    public int outDegree(){
        return outDegree;
    }
    
    /**
     * 
     * @return the ID
     */
    public int id(){
        return id;
    }
    
    @Override
    public int hashCode(){
        return id;
    }
    
    /**
     * 
     * @return inverses direction of all edges incident with this vertex,
     */
    public DirectedVertex reverseEdges(){
        DirectedVertex dv = new DirectedVertex(this.id);
        dv.inDegree = this.outDegree;
        dv.outDegree = this.inDegree;
        dv.outDistances.putAll(this.inDistances);
        dv.inVertices.putAll(this.outVertices);
        dv.outVertices.putAll(dv.inVertices);
        dv.outDistances.putAll(this.inDistances);
        return dv;
    }
    
    void disconnectFromOutVertices(){
        this.outDegree = 0;
        for (DirectedVertex adj : outVertices.values()){
            adj.inVertices.remove(id());
            adj.inDegree--;
            removedOutConnections.put(adj.id(), adj);
        }
        this.outVertices.clear();
    }
    
    void disconnectFromInVertices(){
        this.inDegree = 0;
        for (DirectedVertex adj : inVertices.values()){
            adj.outVertices.remove(id());
            adj.outDegree--;
            removedInConnections.put(adj.id(), adj);
        }
        this.inVertices.clear();;
    }
    
    void restoreLinksToOutVertices(){
        for (DirectedVertex adj : removedOutConnections.values()){
            this.inVertices.put(adj.id(), adj);
            adj.inVertices.put(id(), this);
            adj.inDegree++;
        }
        removedOutConnections.clear();
    }
    
    void restoreLinksToInVertices(){
        for (DirectedVertex adj : removedInConnections.values()){
            this.inVertices.put(adj.id(), adj);
            adj.outVertices.put(id(), this);
            adj.outDegree++;
        }
        removedInConnections.clear();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof  DirectedVertex)
            return ((DirectedVertex) o).id == id;
        else
            return false;
    }
    
    @Override
    public String toString(){
        return "["+String.valueOf(id)+", inDegree="+inDegree+", outDegree="+outDegree+"]";
        //return "DirectedVertex"+id;
    }
    

    int outDistance(DirectedVertex v){
        if (this.outDistances.containsKey(v.id()))
            return this.outDistances.get(v.id());
        else
            return Integer.MAX_VALUE;
    }
    
    int inDistance(DirectedVertex v){
        if (this.inDistances.containsKey(v.id()))
            return this.inDistances.get(v.id());
        else
            return Integer.MAX_VALUE;
    }

    void setNote(Object note){
        this.note = note;
    }

    Object getNote(){
        return this.note;
    }

    /**
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static boolean areDirectlyConnected(DirectedVertex v1, DirectedVertex v2){
        return v1.outVertices.containsKey(v2.id()) && v2.inVertices.containsKey(v1.id());
    }
    
    /**
     * Connectes the two given vertices by a directed edge pointing from the vertex <em>from</em>
     * to the vertex <em>to</em>,
     * @param from the first vertex
     * @param to the second vertex
     */
    public static void connectDirected(DirectedVertex from, DirectedVertex to){
        if (!from.equals(to) && !from.outVertices.containsKey(to.id())){
            from.outVertices.put(to.id(), to);
            from.outDegree++;
            to.inVertices.put(from.id(), from);
            to.inDegree++;
        }
    }
    
    /**
     * Connectes the two given vertices by a directed edge pointing from the vertex <em>from</em>
     * to the vertex <em>to</em>,
     * @param from the first vertex
     * @param to the second vertex
     * @param distance distance associated to the edge (e.g. for the purpose of computing shortest paths),
     */
    public static void connectDirected(DirectedVertex from, DirectedVertex to, int distance){
        if (!from.equals(to) && !from.outVertices.containsKey(to.id())){
            from.outVertices.put(to.id(), to);
            from.outDegree++;
            from.outDistances.put(to.id(), distance);
            to.inVertices.put(from.id(), from);
            to.inDegree++;
            to.inDistances.put(from.id(), distance);
        }
    }
    
    /**
     * Removes the directed edge pointing from the vertex <em>from</em> to the vertex <em>to</em>.
     * @param from the first vertex
     * @param to the second vertex
     */
    public static void disconnect(DirectedVertex from, DirectedVertex to){
        if (from.outVertices.containsKey(to.id())){
            from.outVertices.remove(to.id());
            from.outDegree--;
            if (from.outDistances.containsKey(to.id()))
                from.outDistances.remove(to.id());
            to.inVertices.remove(from.id());
            to.inDegree--;
            if (to.inDistances.containsKey(from.id()))
                to.inDistances.remove(to.id());
        }
    }
}
