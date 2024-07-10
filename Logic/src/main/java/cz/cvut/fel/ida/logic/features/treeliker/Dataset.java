/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import java.util.Collection;
import java.util.List;
/**
 * Interface for representing datasets. The interface is similar to java.util.Iterator
 * in that it has methods reset() which sets index to the first element, hasNextExample()
 * which checks if there are more examples in the dataset (given the position of the index)
 * and nextExample() which returns the next example (similarly as the method next() of interface Iterator)
 * 
 * @author Ondra
 */
public interface Dataset {

    /**
     * 
     * @return number of examples in the dataset
     */
    public int countExamples();

    /**
     * 
     * @param cl class-label
     * @return number of examples with class-label cl
     */
    public int countExamples(String cl);

    /**
     * 
     * @return true if there are more examples that can be retrieved using method nextExample(), false otherwise.
     */
    public boolean hasNextExample();
    
    /**
     * 
     * @return next example if it exists
     */
    public Example nextExample();
    
    /**
     * Resets the index to the first example.
     */
    public void reset();
    
    /**
     * 
     * @return current index
     */
    public int currentIndex();
    
    /**
     * 
     * @return class-label of current example (= the example at which the index points)
     */
    public String classificationOfCurrentExample();

    /**
     * 
     * @return all class labels present in the dataset
     */
    public Collection<String> classes();
    
    /**
     * 
     * @return a shallow copy of the dataset
     */
    public Dataset shallowCopy();

    /**
     * 
     * @param indices indices of examples which should be included in the new Dataset
     * @return new Dataset containing only examples at specified positions
     */
    public Dataset get(int[] indices);

    /**
     * Creates folds (= Datasets) for cross-validation.
     * @param folds number of folds
     * @param seed seed of the random number generator
     * @return
     */
    public List<Dataset> stratifiedCrossValidation(int folds, int seed);
    
    /**
     * Randomly selects a subset of the dataset.
     * @param numFromEachClass number of examples from each class which should be included in the sample
     * @param seed seed of the random number generator
     * @return
     */
    public Dataset subsample(int numFromEachClass, int seed);


    /**
     * Splits dataset int disjoint parts. This method should not be used for cross-validation
     * @param count 
     * @return
     */
    public Dataset[] split(int count);
    
    /**
     * Sorts the examples in the dataset in ascending order according to values 
     * of targetVariable.
     * @param targetVariable target-variable used for sorting
     */
    public void sortAsc(String targetVariable);
    
    /**
     * Sorts the examples in the dataset in ascending order according to values 
     * of targetVariable.
     * @param targetVariable target-variable used for sorting
     */
    public void sortDesc(String targetVariable);
}
