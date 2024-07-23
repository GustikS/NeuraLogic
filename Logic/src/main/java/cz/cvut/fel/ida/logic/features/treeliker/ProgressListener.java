/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

/**
 * Interface for ProgressListeners - progress listeners can be added to RelF, HiFi or Poly
 * so that these algorithms could inform the other parts of the application about their progress.
 * 
 * @author Ondra
 */
public interface ProgressListener {

    /**
     * 
     */
    public void readingExamples();
    
    /**
     * 
     * @param currentDefinition
     */
    public void processingPredicate(PredicateDefinition currentDefinition);
    
    /**
     * 
     * @param numberOfFeatures
     */
    public void finished(int numberOfFeatures);
    
}
