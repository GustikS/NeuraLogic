/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

/**
 * Settings of the feature construction algorithms.
 * 
 * @author Ondra
 */
public class TreeLikerSettings {

    /**
     * if constants should be computed as literals for the purpose of computing size
     * of features (Blocks), default = false
     */
    public static boolean COUNT_CONSTANTS_AS_LITERALS = false;


    /**
     * whether H-reduction filtering should be used, default = true.
     */
    public static boolean USE_H_REDUCTION = true;

    /**
     * whether redundancy filtering should be used, default = true
     */
    public static boolean USE_REDUNDANCY_FILTERING = true;

    /**
     * number of processors that should be attempted to be used
     */
    public static int PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * verbosity, default = 1
     */
    public static int VERBOSITY = 1;
    
    /**
     * 
     */
    public static int REGRESSION_REDUNDANCY_TOLERANCE = 0;
    
    /**
     * random number generator seed
     */
    public static int SEED = 2012;
}
