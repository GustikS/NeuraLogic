/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.ValueToIndex;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for objects called PredicateDefinition which serve for specifying language-bias.
 * 
 * @author Ondra
 */
public final class PredicateDefinition {
    
    public final static int INPUT = 1, OUTPUT = 2, CONSTANT = 3, GLOBAL_CONSTANT = 4, IGNORE = 5, IDENTIFIER = 6, CLASS = 7, NUMBER = 8, AGGREGATOR = 9;

    private static ValueToIndex<Pair<String,Integer>> integerToPredicate = new ValueToIndex<Pair<String,Integer>>();

    private static ValueToIndex<String> integerToType = new ValueToIndex<String>();

    private static int lastId;

    private int id;

    private int predicate;

    private int input;

    private int[] types;

    private int[] modes;

    private int[] originalModes;

    private int branchingModPredicate;

    private int[] branching;

    private boolean isOutputOnly;

    private boolean isInputOnly;

    private boolean isGlobalConstant;

    private boolean isConstant;

    private boolean hasNumber;

    private boolean containsAggregation;
    
    private int numAggregators;

    /**
     * Creates a new empty PredicateDefinition
     */
    protected PredicateDefinition(){}

    /**
     * Creates a new instance of class PredicateDefinition
     * @param predicate integer representation of the predicate symbol
     * @param types types of the arguments
     * @param modes modes of the arguments (can be INPUT, OUTPUT, CONSTANT, IGNORE, GLOBAL_CONSTANT,
     * IDENTIFIER, CLASS, NUMBER, AGGREGATOR)
     */
    public PredicateDefinition(int predicate, int[] types, int[] modes){
        this(predicate, types, modes, new int[modes.length], 0);
    }

    /**
     * 
     * Creates a new instance of class PredicateDefinition
     * @param predicate integer representation of the predicate symbol
     * @param types types of the arguments
     * @param modes modes of the arguments (can be INPUT, OUTPUT, CONSTANT, IGNORE, GLOBAL_CONSTANT,
     * IDENTIFIER, CLASS, NUMBER, AGGREGATOR)
     * @param branching number of literals which can be connected (through variable-sharing) to the particular arguments
     * @param branchingModPredicate number of literals based on this PredicateDefinition which can be connected to the same variable
     */
    public PredicateDefinition(int predicate, int[] types, int[] modes, int[] branching, int branchingModPredicate){
        this(predicate, types, modes, branching, branchingModPredicate, modes);
    }

    /**
     * 
     * Creates a new instance of class PredicateDefinition
     * @param predicate integer representation of the predicate symbol
     * @param types types of the arguments
     * @param modes modes of the arguments (can be INPUT, OUTPUT, CONSTANT, IGNORE, GLOBAL_CONSTANT,
     * IDENTIFIER, CLASS, NUMBER, AGGREGATOR)
     * @param branching number of literals which can be connected (through variable-sharing) to the particular arguments
     * @param branchingModPredicate number of literals based on this PredicateDefinition which can be connected to the same variable
     * @param originalModes original modes - used for example when due to preprocessing modes of some arguments are changed
     */
    public PredicateDefinition(int predicate, int[] types, int[] modes, int[] branching, int branchingModPredicate, int[] originalModes){
        this.predicate = predicate;
        this.types = types;
        this.modes = modes;
        this.originalModes = originalModes;
        this.branching = branching;
        this.branchingModPredicate = branchingModPredicate;
        this.id = ++lastId;
        this.recomputeEverything();
    }

    private void recomputeEverything(){
        int outputs = 0;
        int inputs = 0;
        this.isConstant = false;
        this.isGlobalConstant = false;
        this.isInputOnly = false;
        this.isOutputOnly = false;
        this.hasNumber = false;
        this.containsAggregation = false;
        this.numAggregators = 0;
        for (int i = 0; i < this.modes.length; i++){
            if (this.modes[i] == INPUT){
                this.input = i;
                inputs++;
            } else if (this.modes[i] == OUTPUT){
                outputs++;
            } else if (this.modes[i] == CONSTANT){
                //outputs++;
            } else if (this.modes[i] == GLOBAL_CONSTANT){
                this.isGlobalConstant = true;
            } else if (this.modes[i] == NUMBER){
                this.hasNumber = true;
            } else if (this.modes[i] == AGGREGATOR){
                this.containsAggregation = true;
                this.numAggregators++;
            }
        }
        if (outputs == 0 && inputs > 0 && !this.isGlobalConstant){
            this.isInputOnly = true;
        }
        if (inputs == 0 && !this.isGlobalConstant){
            this.isOutputOnly = true;
        }
    }

    /**
     * Sets the mode of argument at position <em>index</em>
     * @param mode the new mode
     * @param index index of the argument
     */
    public void setMode(int mode, int index){
        this.modes[index] = mode;
        this.recomputeEverything();
    }

    /**
     * 
     * @return integer representation of the predicate symbol
     */
    public int predicate(){
        return predicate;
    }

    /**
     * 
     * @return arity of the defined predicate
     */
    public int arity(){
        return this.modes.length;
    }

    /**
     * 
     * @return types of arguments
     */
    public int[] types(){
        return types;
    }

    /**
     * 
     * @return modes of arguments
     */
    public int[] modes(){
        return modes;
    }

    /**
     * 
     * @return original modes of arguments
     */
    public int[] originalModes(){
        return originalModes;
    }

    /**
     * 
     * @return branching factors of arguments
     */
    public int[] branchingFactors(){
        return branching;
    }

    /**
     * 
     * @return maximum allowed number of occurences of literals based on this PredicateDefinition
     * with the same input variable
     */
    public int branchingModPredicate(){
        return this.branchingModPredicate;
    }

    /**
     * 
     * @return true if this definition contains no output-arguments, false otherwise
     */
    public boolean isInputOnly(){
        return this.isInputOnly;
    }

    /**
     * 
     * @return true if this definition contains no input-arguments, false otherwise
     */
    public boolean isOutputOnly(){
        return this.isOutputOnly;
    }

    /**
     * 
     * @return true if this definition defines a global constant, false otherwise
     */
    public boolean isGlobalConstant(){
        return this.isGlobalConstant;
    }

    /**
     * 
     * @return true of this definition defines a "number", false otherwise
     */
    public boolean containsNumber(){
        return this.hasNumber;
    }

    /**
     * 
     * @return true if this definition contains at least one AGGREGATOR-mode, false otherwise
     */
    public boolean containsAggregator(){
        return this.containsAggregation;
    }
    
    /**
     * 
     * @return number of aggregator-modes in this predicate definition
     */
    public int numAggregators(){
        return this.numAggregators;
    }

    /**
     * Sets the definition to be "constant"-definition (if <em>isConstant</em> is set to true).
     * @param isConstant 
     */
    public void setConstant(boolean isConstant){
        this.isConstant = isConstant;
    }

    /**
     * 
     * @return true if this definition is "constant"-definition
     */
    public boolean isConstant(){
        return this.isConstant;
    }

    @Override
    public int hashCode(){
        return predicate;
    }

    /**
     * 
     * @return index of input-argument or -1 if there is no input-argument in this predicate definition
     */
    public int input(){
        return input;
    }

    /**
     * 
     * @return string representation of the predicate symbol
     */
    public String stringPredicate(){
        return PredicateDefinition.integerToPredicate.indexToValue(this.predicate).r;
    }

    /**
     * 
     * @param index index of the argument for which we want to get the type
     * @return string representation of the type of argument at position <em>index</em>
     */
    public String stringType(int index){
        return PredicateDefinition.integerToType.indexToValue(this.types[index]);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof PredicateDefinition){
            PredicateDefinition def = (PredicateDefinition)o;
            if (def.predicate != this.predicate)
                return false;
            for (int i = 0;i < def.modes.length; i++){
                if (modes[i] != def.modes[i])
                    return false;
                if (types[i] != def.types[i])
                    return false;
            }
            return true;
            //return def.id == this.id;
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (this.branchingModPredicate == 0 || this.branchingModPredicate == Integer.MAX_VALUE){
            sb.append(integerToPredicate.indexToValue(predicate)).append("(");
        } else {
            sb.append(integerToPredicate.indexToValue(predicate)).append("[").append(this.branchingModPredicate).append("](");
        }
        for (int i = 0; i < this.types.length; i++){
            if (modes[i] == INPUT){
                sb.append("+");
            } else if (modes[i] == OUTPUT){
                sb.append("-");
            } else if (modes[i] == CONSTANT){
                sb.append("#");
            } else if (modes[i] == GLOBAL_CONSTANT){
                sb.append("@");
            } else if (modes[i] == IGNORE){
                sb.append("!");
            } else if (modes[i] == IDENTIFIER){
                sb.append("&");
            } else if (modes[i] == CLASS){
                sb.append("~");
            } else if (modes[i] == NUMBER){
                sb.append("$");
            } else if (modes[i] == AGGREGATOR){
                sb.append("*");
            }
            if (branching[i] > 0 && branching[i] < Integer.MAX_VALUE){
                sb.append(integerToType.indexToValue(types[i])).append("[").append(branching[i]).append("]");
            } else {
                sb.append(integerToType.indexToValue(types[i]));
            }
            if (i < types.length-1)
                sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 
     * @return creates a deep copy of this definition
     */
    public PredicateDefinition cloneDefinition(){
        return new PredicateDefinition(this.predicate, VectorUtils.copyArray(this.types), VectorUtils.copyArray(this.modes), VectorUtils.copyArray(this.branching), this.branchingModPredicate, VectorUtils.copyArray(this.modes));
    }

    /**
     * Parses definitions from string = template 
     * @param definition string template
     * @return set of instances of class PredicateDefinition which represents the parsed template
     */
    public static Set<PredicateDefinition> parseDefinition(String definition){
        Set<PredicateDefinition> definitions = new HashSet<PredicateDefinition>();
        Clause parsedDefinition = Clause.parse(definition);
        for (Literal l : parsedDefinition.literals()){
            int predicate = -1;
            if (l.predicate().name.indexOf('[') > 0){
                predicate = predicateToInteger(l.predicate().name.substring(0, l.predicate().name.lastIndexOf('[')), l.arity());
            } else {
                predicate = predicateToInteger(l.predicate().name, l.arity());
            }
            int[] types = new int[l.arity()];
            int[] modes = new int[l.arity()];
            int[] branching = new int[l.arity()];
            int branchingModPredicate = parseParameter(l.predicate().name);
            for (int i = 0; i < l.arity(); i++){
                types[i] = typeToInteger(l.get(i));
                modes[i] = modeToInteger(l.get(i));
                branching[i] = parseParameter(l.get(i));
            }
            PredicateDefinition pdef = new PredicateDefinition(predicate, types, modes, branching, branchingModPredicate);
            definitions.add(pdef);
        }
        return definitions;
    }

    private static int parseParameter(Term term){
        return parseParameter(term.toString());
    }

    private static int parseParameter(String t){
        if (t.indexOf('[') > 0){
            String substr = t.substring(t.indexOf('[')+1, t.indexOf(']'));
            return Integer.parseInt(substr.trim());
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private static int modeToInteger(Term term){
        if (term.name().charAt(0) == '+'){
            return INPUT;
        } else if (term.name().charAt(0) == '-'){
            return OUTPUT;
        } else if (term.name().charAt(0) == '#'){
            return CONSTANT;
        } else if (term.name().charAt(0) == '@'){
            return GLOBAL_CONSTANT;
        } else if (term.name().charAt(0) == '!'){
            return IGNORE;
        } else if (term.name().charAt(0) == '&'){
            return IDENTIFIER;
        } else if (term.name().charAt(0) == '~'){
            return CLASS;
        } else if (term.name().charAt(0) == '$'){
            return NUMBER;
        } else if (term.name().charAt(0) == '*') {
            return AGGREGATOR;
        } else {
            return -1;
        }
    }

    private static int typeToInteger(Term type){
        String t = type.name().substring(1);
        if (t.indexOf('[') > 0)
            t = t.substring(0, t.indexOf('['));
        return typeToInteger(t.trim());
    }

    /**
     * 
     * @return nuique identifier of this PredicteDefinition
     */
    public int id(){
        return this.id;
    }

    /**
     * Converts string representation of type to integer
     * @param type string representation of type
     * @return integer representation of type
     */
    public static int typeToInteger(String type){
        return integerToType.valueToIndex(type);
    }

    /**
     * Converts integer representation of type to string
     * @param type integer representation of type
     * @return string representation of type
     */
    public static String integerToType(int type){
        return integerToType.indexToValue(type);
    }

    /**
     * Converts string representation of predicate symbol to integer representation
     * @param predicate predicate name
     * @param arity predicate's arity
     * @return integer representing the predicate symbol
     */
    public static int predicateToInteger(String predicate, int arity){
        return integerToPredicate.valueToIndex(new Pair<String,Integer>(predicate, arity));
    }

    /**
     * 
     * @param predicate integer representation of predicate
     * @return predicate name
     */
    public static String integerToPredicate(int predicate){
        return integerToPredicate.indexToValue(predicate).r;
    }
}
