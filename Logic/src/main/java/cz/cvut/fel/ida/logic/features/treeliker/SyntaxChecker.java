/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

/**
 * Interface for syntax checkers which are used to prune the space of blocks
 * based purely on syntactical constraint (e.g. maximum size, maximum number of
 * predicates of some type etc.)
 * @author Ondra
 */
public interface SyntaxChecker {
    
    /**
     * Checks whether a block conforms to the syntactical constraints.
     * @param block block whose syntax should be checked
     * @return true if the block conforms to the syntactical constraints
     */
    public boolean check(Block block);
    
    /**
     * Checks whether a Join conforms to the syntactical constraints.
     * @param join Join whose syntax should be checked
     * @return true if the block conforms to the syntactical constraints
     */
    public boolean check(Join join);
}
