/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.utils.math.collections.IntegerSet;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * Class for representing sets of Blocks joined by their input variables.
 * 
 * @author Ondra
 */
public class Join implements Iterable<Block>, HavingTermDomain {
    
    private int hashCode = -1;
    
    private int id;
    
    private static int lastID = 0;
    
    private int numBlocks = 0;
    
    private int numLiterals = 0;
    
    private Join first;
    
    private Join previous;
    
    private Block block;
    
    private final static Object lock = new Object();
    
    private Join(){
        synchronized (lock){
            this.id = lastID++;
        }
    }
    
    /**
     * Creates new instance of class Join
     * @param block one block in the Join
     */
    public Join(Block block){
        this();
        this.block = block;
        this.previous = null;
        this.first = this;
        this.numBlocks = 1;
        this.numLiterals = block.size();
    }
    
    /**
     * Creates a new Join which contains all blocks originally contained in the old Join
     * plus the Block <em>block</em>
     * @param block the block to be added to the blocks in the join
     * @return the new Join
     */
    public Join addBlock(Block block){
        Join bc = new Join();
        bc.block = block;
        bc.previous = this;
        bc.first = this.first;
        bc.numBlocks = this.numBlocks+1;
        bc.numLiterals = this.numLiterals()+block.size();
        return bc;
    }

    /**
     * 
     * @return first Block that is contained in this Join
     */
    public Block first(){
        return this.first.block;
    }
    
    /**
     * 
     * @return last Block that is contained in this Join
     */
    public Block last(){
        return this.block;
    }
    
    /**
     * Computes term-domain of this block which is the result of cross-operations
     * applied on the blocks in the join.
     * 
     * @param e example w.r.t. which termDomain shoulod be computed
     * @return term-domain
     */
    public Domain termDomain(Example e){
        Domain domain = null;
        if (this.previous == null){
            domain = this.block.termDomain(e);
        } else {
            Domain previousTermDomain = this.previous.termDomain(e);
            if (previousTermDomain.isEmpty()){
                return Domain.emptyDomain;
            }
            domain = Domain.cross(previousTermDomain, this.block.termDomain(e));
        }
        return domain;
    }
    
    /**
     * Computes aggregable for term <em>term</em> and example <em>e</em> - this is
     * the Aggregable computed using the cross-operation applied on the term-aggregables of blocks in
     * this Join.
     * 
     * @param term the term
     * @param e the example
     * @return aggregable for term <em>term</em> w.r.t. the example <em>e</em>
     */
    public Aggregable termAggregable(int term, Example e){
        if (this.previous == null){
            return this.block.termAggregable(term, e);
        } else {
            return previous.termAggregable(term, e).cross(this.block.termAggregable(term, e));
        }
    }
    
    /**
     * Computes term-domain (the integer-set part) of this Join which is intersection
     * of term-domains (the integer-set parts) of the blocks in this Join.
     * 
     * @param e example w.r.t. which term-domain should be computed
     * @return the integer-set part of term-domain of this Join
     */
    public IntegerSet integerSetTermDomain(Example e){
        IntegerSet domain = null;
        if (this.previous == null){
            domain = this.block.integerSetTermDomain(e);
        } else {
            IntegerSet previousTermDomain = this.previous.integerSetTermDomain(e);
            if (previousTermDomain.isEmpty()){
                return IntegerSet.emptySet;
            }
            domain = IntegerSet.intersection(previousTermDomain, this.block.integerSetTermDomain(e));
        }
        return domain;
    }
    
    /**
     * 
     * @return number of Blocks in this Join
     */
    public int numBlocks() {
        return this.numBlocks;
    }
    
    /**
     * 
     * @return number of literals in this Join which is the sum of sizes of the blocks 
     * in this Join
     */
    public int numLiterals(){
        return this.numLiterals;
    }

    @Override
    public int hashCode(){
        if (this.hashCode == -1){
            if (this.previous == null){
                this.hashCode = 67*this.block.hashCode() % Integer.MAX_VALUE/256;
            } else {
                this.hashCode = 67*this.block.hashCode()+37*this.previous.hashCode() % Integer.MAX_VALUE/256;
            }
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Join other = (Join) obj;
        if (this.hashCode != other.hashCode) {
            return false;
        }
        if (this.numBlocks != other.numBlocks) {
            return false;
        }
        if (this.previous != other.previous && (this.previous == null || !this.previous.equals(other.previous))) {
            return false;
        }
        if (this.block != other.block && (this.block == null || !this.block.equals(other.block))) {
            return false;
        }
        return true;
    }
    
    public Iterator<Block> iterator() {
        LinkedList<Block> ll = new LinkedList<Block>();
        Join bc = this;
        while (bc != null){
            ll.addFirst(bc.block);
            bc = bc.previous;
        }
        return ll.iterator();
    }

    /**
     * @return unique identifier of this Join
     */
    public int id() {
        return id;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Join[");
        for (Block b : this){
            sb.append(b);
            if (b != this.block){
                sb.append(", ");
            }
        }
        sb/*.append(", id: ").append(this.id)*/.append("]");
        return sb.toString();
    }
    
    /**
     * 
     * @return number of aggregation-variables in this Join
     */
    public int numAggregators(){
        if (previous == null){
            return this.block.numAggregators();
        } else {
            return this.previous.numAggregators()+this.block.numAggregators();
        }
    }
}
