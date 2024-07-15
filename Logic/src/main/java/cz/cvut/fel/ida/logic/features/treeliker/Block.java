/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.Variable;
import cz.cvut.fel.ida.logic.features.treeliker.aggregables.VoidAggregables;
import cz.cvut.fel.ida.logic.features.treeliker.aggregables.VoidAggregablesBuilder;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.Counters;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;

import java.lang.ref.SoftReference;
import java.util.*;

/**
 * This is one of the most important classes for RelF, HiFi and Poly.
 * Instances of class Block serve both as features as well as their building blocks.
 * 
 * @author Ondra
 */
public class Block implements HavingLiteralDomain, HavingTermDomain {
    
    /**
     * 
     * Literals with higher id were created later than those with smaller id
     */
    private int id;

    private int arity;
    
    private int predicate;
    
    private static int idCounter;
    
    private int size = 1;
    
    private Join[] children;
    
    private PredicateDefinition definition;
    
    private int input = -1;

    private AggregablesBuilder aggregablesBuilder;
    
    private SoftReference<String> canonicalString = new SoftReference<String>(null);
    
    private int hashCode = -1;

    private final Object lock = new Object();
    
    private int numAggregators = 0;
    
    private SoftReference<HashMap<Pair<Integer,Example>,SoftReference<IntegerSet>>> integerSetTermDomainsCache = new SoftReference<HashMap<Pair<Integer,Example>,SoftReference<IntegerSet>>>(null);
    
    private SoftReference<HashMap<Pair<Integer,Example>,SoftReference<Aggregable>>> termAggregablesCache = new SoftReference<HashMap<Pair<Integer,Example>,SoftReference<Aggregable>>>(null);
    
    private SoftReference<HashMap<Pair<Integer,Example>,SoftReference<Domain>>> termDomainsCache = new SoftReference<HashMap<Pair<Integer,Example>,SoftReference<Domain>>>(null);
    
    private SoftReference<IntegerSet> predicates = new SoftReference<IntegerSet>(null);

    private SoftReference<Counters<Integer>> predicateCounts = new SoftReference<Counters<Integer>>(null);

    /**
     * Creates a new instance of class Block. The new Block is composed of only one literal (to which other blocks can be connected).
     * The predicate name, arity, types and modes of the arguments of this literal are given by the PredicateDefinition definition
     * @param definition specification of predicate name, arity, modes, types etc.
     */
    public Block(PredicateDefinition definition){
        this(definition, VoidAggregablesBuilder.construct());
    }
    
    /**
     * Creates a new instance of class Block. The new Block is composed of only one literal (to which other blocks can be connected).
     * The predicate name, arity, types and modes of the arguments of this literal are given by the PredicateDefinition definition
     * @param definition specification of predicate name, arity, modes, types etc.
     * @param aggregableBuilder AggregablesBuilder that should be used to construct Aggregables for aggregation using this Block.
     */
    public Block(PredicateDefinition definition, AggregablesBuilder aggregableBuilder){
        synchronized (Block.class){
            this.id = ++idCounter;
        }
        this.aggregablesBuilder = aggregableBuilder;
        this.arity = definition.modes().length;
        this.predicate = definition.predicate();
        this.children = new Join[arity];
        this.definition = definition;
        this.input = definition.input();
        if (!TreeLikerSettings.COUNT_CONSTANTS_AS_LITERALS && this.definition.isConstant()){
            this.size = 0;
        }
        if (this.definition.containsAggregator()){
            this.numAggregators = this.definition.numAggregators();
        }
    }
    
    /**
     * Connects Block child to argument at position <em>output</em>. The Block child
     * is connected through its <em>input</em> argument which is specified in its PredicateDefinition object.
     * @param child the Block to be connected with this Block
     * @param output index of the argument to which it should be connected
     * @return children of this block as they were before the new Block was added
     */
    public Join addChild(Block child, int output){
        Join retVal = this.children[output];
        if (retVal == null){
            this.children[output] = new Join(child);
        } else {
            this.children[output] = retVal.addBlock(child);
        }
        this.size += child.size();
        this.numAggregators += child.numAggregators();
        this.deleteEverythingCached();
        return retVal;
    }
    
    /**
     * Removes all blocks which were originally connected to this block through variable at position <em>argument</em>
     * and replaces them by <em>newChildren</em>
     * @param newChildren new blocks that should be connected to this block
     * @param argument index of the argument where the new blocks should be connected
     * @return hildren of this block as they were before
     */
    public Join setChildren(Join newChildren, int argument){
        Join retVal = this.children[argument];
        if (this.children[argument] != null){
            this.size -= this.children[argument].numLiterals();
            this.numAggregators -= this.children[argument].numAggregators();
        }
        this.children[argument] = newChildren;
        this.size += newChildren.numLiterals();
        this.numAggregators += newChildren.numAggregators();
        this.deleteEverythingCached();
        return retVal;
    }

    /**
     * 
     * @return modes according to PredicateDefinition
     */
    public int[] modes(){
        return definition.modes();
    }
    
    /**
     * 
     * @return types according to PredicateDefinition
     */
    public int[] types(){
        return definition.types();
    }
    
    /**
     * 
     * @return branching factors according to PredicateDefinition
     */
    public int[] branchingFactors(){
        return this.definition.branchingFactors();
    }
    
    /**
     * 
     * @return number of literals in this Block
     */
    public int size(){
        return this.size;
    }
    
    /**
     * 
     * @return arity of root of this Block
     */
    public int arity(){
        return this.arity;
    }
    
    /**
     * 
     * @return unique identifier
     */
    public int id(){
        return this.id;
    }
    
    /**
     * 
     * @return predicate definition from the template
     */
    public PredicateDefinition definition(){
        return this.definition;
    }

    /**
     * 
     * @return branching-mod-predicate according to PredicateDefinition)
     */
    public int branchingModPredicate(){
        return this.definition.branchingModPredicate();
    }

    /**
     * 
     * @return number of aggregation-variables in this block
     */
    public int numAggregators(){
        return this.numAggregators;
    }
    
    /**
     * Computes literal-domain (see e.g. Kuzelka and Zelezny, MLJ, 2011 for definition)
     * of this Block. Importantly, caching is used so it is usually quite fast,
     * especially with features constructed by HiFi, RelF or Poly which reuse blocks of features.
     * @param e example w.r.t. that the domain is computed
     * @return literal-domain
     */
    public Domain literalDomain(Example e){
        Domain domain = null;
        IntegerSet integerSetLiteralDomain = integerSetLiteralDomain(e);
        if (integerSetLiteralDomain.isEmpty()){
            domain = Domain.emptyDomain;
        } else {
            Aggregables aggregables = this.aggregablesBuilder.construct(this.definition, integerSetLiteralDomain, e);
            if (!(aggregables instanceof VoidAggregables)){
                int index = 0;
                for (int literalID : integerSetLiteralDomain.values()){
                    Aggregable literalAggregable = aggregables.get(index);
                    for (int argument = 0; argument < this.arity; argument++){
                        if (this.children[argument] != null){
                            literalAggregable = literalAggregable.cross(this.children[argument].termAggregable(e.getTerm(literalID, argument), e));
                        }
                    }
                    aggregables.set(index, literalAggregable);
                    index++;
                }
            }
            domain = new Domain(integerSetLiteralDomain, aggregables);
        }
        return domain;
    }
    
    /**
     * Computes Aggregable which is the result of aggregation when the input Variable
     * of this Block is set to <em>term</em>
     * @param term term which is substituted for the input variable when aggregation is computed
     * @param e example w.r.t. that aggregation is computed
     * @return Aggregable which is the result of aggregation with the input variable set to <em>term</em>
     */
    public Aggregable termAggregable(int term, Example e){
        Pair<Integer,Example> queryPair = new Pair<Integer,Example>(term, e);
        Aggregable aggregable = null;
        HashMap<Pair<Integer,Example>,SoftReference<Aggregable>> map = null;
        SoftReference<Aggregable> softRef = null;
        synchronized (this.lock){
            if ((map = this.termAggregablesCache.get()) != null && (softRef = map.get(queryPair)) != null && (aggregable = softRef.get()) != null){
                return aggregable;
            }
        }
        for (int literalID : this.integerSetLiteralDomain(e).values()){
            if (e.getTerm(literalID, this.definition.input()) == term){
                if (aggregable == null){
                    aggregable = literalAggregable(literalID, e);
                } else {
                    aggregable = aggregable.plus(literalAggregable(literalID, e));
                }
            }
        }
        synchronized (this.lock){
            if (map == null){
                map = new HashMap<Pair<Integer,Example>,SoftReference<Aggregable>>();
                this.termAggregablesCache = new SoftReference<HashMap<Pair<Integer,Example>,SoftReference<Aggregable>>>(map);
            }
            map.put(queryPair, new SoftReference<Aggregable>(aggregable));
        }
        return aggregable;
    }
    
    /**
     * Computes Aggregable which is the result of aggregation when the variables in the root are grounded
     * so that root = <em>literal</em> which is input of this method.
     * 
     * @param literal the id of the ground literal
     * @param e example w.r.t. that aggregation is performed
     * @return result of aggregation with root fixed to <em>literal</em>
     */
    public Aggregable literalAggregable(int literal, Example e){
        //one literal = one aggregable
        Aggregable aggregable = this.aggregablesBuilder.construct(definition, IntegerSet.createIntegerSet(literal), e).get(0);
        int arg = 0;
        for (Join join : this.children){
            if (join != null){
                aggregable = aggregable.cross(join.termAggregable(e.getTerm(literal, arg), e));
            }
            arg++;
        }
        return aggregable;
    }
    
    /**
     * Computes the set of literals which can be substituted for the root of this Block
     * while still being true w.r.t. e (i.e. while there is still some substitution 
     * of the variables of Block which is subset of e).
     * @param e the example w.r.t. that domain is cmoputed
     * @return literal-domain
     */
    public IntegerSet integerSetLiteralDomain(Example e){
        boolean hasAtLeastOneChild = false;
        IntegerSet[] literalDomains = new IntegerSet[this.children.length];
        for (int i = 0; i < this.arity; i++){
            if (this.children[i] != null){
                hasAtLeastOneChild = true;
                literalDomains[i] = e.getLiteralDomain(this.predicate, this.children[i].integerSetTermDomain(e), i);
            }
        }


        IntegerSet integerSetLiteralDomain = null;
        if (hasAtLeastOneChild) {

            // remove possible null entries
            List<IntegerSet> clonedLitDomains = new ArrayList<>(literalDomains.length);
            for (IntegerSet literalDomain : literalDomains) {
                if (literalDomain != null) {
                    clonedLitDomains.add(literalDomain);
                }
            }

            integerSetLiteralDomain = IntegerSet.intersection(clonedLitDomains);
        } else {
            integerSetLiteralDomain = e.getLiteralDomain(this.predicate);
        }
        return integerSetLiteralDomain;
    }
    
    /**
     * Computes the set of terms which can be substituted for the input variable of this Block
     * while still being true w.r.t. e (i.e. while there is still some substitution 
     * of the variables of Block which is subset of e).
     * @param e the example w.r.t. that domain is cmoputed
     * @return term-domain
     */
    public IntegerSet integerSetTermDomain(Example e){
        return integerSetTermDomain(this.input(), e);
    }
    
    /**
     * Computes the set of terms which can be substituted for the variable at position <em>argument</em> of this Block
     * while still being true w.r.t. e (i.e. while there is still some substitution 
     * of the variables of Block which is subset of e).
     * @param argument index of the argument for which the term domain should be computed
     * @param e the example w.r.t. that domain is cmoputed
     * @return term-domain of the variable at position <em>argument</em> w.r.t. the example e
     */
    public IntegerSet integerSetTermDomain(int argument, Example e){
        Pair<Integer,Example> queryPair = new Pair<Integer,Example>(argument, e);
        IntegerSet domain = null;
        HashMap<Pair<Integer,Example>,SoftReference<IntegerSet>> map = null;
        SoftReference<IntegerSet> softRef = null;
        synchronized (this.lock){
            if ((map = this.integerSetTermDomainsCache.get()) != null && (softRef = map.get(queryPair)) != null && (domain = softRef.get()) != null){
                return domain;
            }
        }
        IntegerSet literalDomain = this.integerSetLiteralDomain(e);
        if (literalDomain.isEmpty()){
            domain = IntegerSet.emptySet;
        } else {
            domain = e.getTermDomain(literalDomain, argument);
        }
        synchronized (this.lock){
            if (map == null){
                map = new HashMap<Pair<Integer,Example>,SoftReference<IntegerSet>>();
                this.integerSetTermDomainsCache = new SoftReference<HashMap<Pair<Integer,Example>,SoftReference<IntegerSet>>>(map);
            }
            map.put(queryPair, new SoftReference<IntegerSet>(domain));
        }
        return domain;
    }
    
    /**
     * Computes term-domain (including aggregables) of this Block w.r.t. example e. Term-domain is computed
     * for the variable in the input-argument of this Block.
     * @param e example w.r.t. that term-domain should be computed
     * @return term-domain w.r.t. example
     */
    public Domain termDomain(Example e){
        return termDomain(this.input(), e);
    }
    
    /**
     * 
     * Computes term-domain (including aggregables) of this Block w.r.t. example e. Term-domain is computed
     * for the variable in the position <em>argument</em> of this Block.
     * @param argument index of the argument for which term-domain should be computed
     * @param e example w.r.t. that term-domain should be computed
     * @return term-domain w.r.t. example
     */
    public Domain termDomain(int argument, Example e){
        Pair<Integer,Example> queryPair = new Pair<Integer,Example>(argument, e);
        Domain domain = null;
        HashMap<Pair<Integer,Example>,SoftReference<Domain>> map = null;
        SoftReference<Domain> softRef = null;
        synchronized (this.lock){
            if ((map = this.termDomainsCache.get()) != null && (softRef = map.get(queryPair)) != null && (domain = softRef.get()) != null){
                return domain;
            }
        }
        IntegerSet literalDomain = this.integerSetLiteralDomain(e);
        if (literalDomain.isEmpty()){
            domain = Domain.emptyDomain;
        } else {
            IntegerSet termIntegerSetDomain = null;
            if (argument == -1){
                termIntegerSetDomain = IntegerSet.createIntegerSet(-1);
            } else {
                termIntegerSetDomain = e.getTermDomain(literalDomain, argument);
            }
            if (this.aggregablesBuilder instanceof VoidAggregablesBuilder){
                domain = new Domain(termIntegerSetDomain, VoidAggregables.construct(termIntegerSetDomain.size()));
            } else {
                Map<Integer,Aggregable> aggregablesMap = new HashMap<Integer,Aggregable>();
                for (int literal : literalDomain.values()){
                    int term = -1;
                    if (argument != -1){
                        term = e.getTerm(literal, argument);
                    }
                    Aggregable agg = this.literalAggregable(literal, e);
                    Aggregable aggOld = null;
                    if ((aggOld = aggregablesMap.get(term)) != null){
                        aggregablesMap.put(term, aggOld.plus(agg));
                    } else {
                        aggregablesMap.put(term, agg);
                    }
                }
                Aggregables aggregables = new Aggregables(termIntegerSetDomain.size());
                for (int term : termIntegerSetDomain.values()){
                    aggregables.add(aggregablesMap.get(term));
                }
                domain = new Domain(termIntegerSetDomain, aggregables);
            }
        }
        synchronized (this.lock){
            if (map == null){
                map = new HashMap<Pair<Integer,Example>,SoftReference<Domain>>();
                this.termDomainsCache = new SoftReference<HashMap<Pair<Integer,Example>,SoftReference<Domain>>>(map);
            }
            map.put(queryPair, new SoftReference<Domain>(domain));
        }
        return domain;
    }
    
    /**
     * Blocks (children) connected to variable at position <em>argument</em>
     * @param argument
     * @return
     */
    public Join children(int argument){
        return this.children[argument];
    }
    
    /**
     * 
     * @return integer-representation of predicate of root of this Block
     */
    public int predicate(){
        return this.predicate;
    }
    
    /**
     * 
     * @return index of the input variable of this Block
     */
    public int input(){
        return this.input;
    }
    
    /**
     * 
     * @return type of the input-variable of this Block
     */
    public int inputType(){
        if (this.definition.isOutputOnly()){
            return -1;
        } else {
            return this.types()[this.input()];
        }
    }
    
    @Override
    public String toString(){
        return toClause();
    }
    
    @Override
    public int hashCode(){
        if (this.hashCode == -1){
            long h = (Math.abs(this.predicate)+1)*(this.arity+1);
            for (Join join : this.children){
                if (join != null){
                    for (Block child : join){
                        if (child != null){
                            h = (h * child.hashCode()) % Integer.MAX_VALUE;
                        }
                    }
                }
            }
            this.hashCode = (int)h;
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Block){
            Block thlc = (Block)o;
            if (thlc.definition.isOutputOnly() || this.definition.isOutputOnly()){
                return thlc.size() == this.size() && this.hashCode() == thlc.hashCode() && thlc.toCanonicalString().equals(this.toCanonicalString());
            } else {
                return thlc.definition.types()[thlc.definition().input()] == this.definition.types()[this.definition().input()] && thlc.toCanonicalString().equals(this.toCanonicalString());
            }
        }
        return false;
    }

    /**
     * Deletes everything that is cached for this Block (but not neccessarily for its 
     * children).
     */
    public void deleteEverythingCached(){
        synchronized (lock){
            this.deleteCachedDomains();
            this.canonicalString.clear();
            this.hashCode = -1;
        }
    }
    
    /**
     * Deletes cached domains for this Block (but not neccessarily for its 
     * children).
     */
    public void deleteCachedDomains(){
        this.termDomainsCache.clear();
    }
    
    /**
     * Sets the AggregablesBuilder for this Block (used for aggregation)
     * @param builder the AggregablesBuilder to be set
     */
    public void setAggregablesBuilder(AggregablesBuilder builder){
        synchronized (this.lock){
            this.termAggregablesCache.clear();
            this.termDomainsCache.clear();
            this.aggregablesBuilder = builder;
        }
    }

    private String niceVariableName(int index){
        if (index <= ((int)'Z'-(int)'A')){
            return String.valueOf((char)((int)'A'+index));
        } else {
            return String.valueOf((char)((int)'A'+index%((int)'Z'-(int)'A')))+(index/((int)'Z'-(int)'A'));
        }
    }
    
    /**
     * 
     * @return string representation of this Block which can be parsed by Clause.parse(...) method.
     */
    public String toClause(){
        StringBuffer buffer = new StringBuffer();
        toClause(buffer, null, new Counters(), new Counters());
        if (buffer.length() > 0 && buffer.charAt(buffer.length()-1) == ' ')
            buffer.delete(buffer.length()-2, buffer.length());
        return buffer.toString();
    }
    
    private void toClause(StringBuffer buffer, String parentOutput, Counters counter, Counters realNumberCounters){
        buffer.append(PredicateDefinition.integerToPredicate(this.predicate));
        buffer.append("(");
        String[] variableNames = new String[this.arity];
        for (int i = 0; i < this.arity; i++){
            if (this.definition().modes()[i] == PredicateDefinition.INPUT){
                if (parentOutput == null){
                    buffer.append("_");
                } else {
                    buffer.append(parentOutput);
                }
                if (i < this.arity-1){
                    buffer.append(", ");
                }
            } else if (this.definition.originalModes()[i] == PredicateDefinition.CONSTANT){
                if (this.children[i] != null){
                    Block child = this.children[i].first();
                    if (child != null){
                        String constant = PredicateDefinition.integerToPredicate(child.predicate);
                        buffer.append(constant);
                    } else {
                        buffer.append("_");
                    }
                    if (i < this.arity-1)
                        buffer.append(", ");

                } else {
                    buffer.append("_");
                    if (i < this.arity-1){
                        buffer.append(", ");
                    }
                }
            } else if (this.definition.modes()[i] == PredicateDefinition.AGGREGATOR){
                buffer.append("real(x").append(realNumberCounters.incrementPost(true)+1).append(")");
                if (i < this.arity-1){
                    buffer.append(", ");
                }
            } else {
                if (this.children[i] != null){
                    String nice = niceVariableName(counter.incrementPost(true));
                    variableNames[i] = nice;
                    buffer.append(nice);
                    if (i < this.arity-1)
                        buffer.append(", ");
                } else {
                    buffer.append("_");
                    if (i < this.arity-1){
                        buffer.append(", ");
                    }
                }
            } 
        }
        buffer.append(")");
        buffer.append(", ");
        for (int i = 0; i < this.arity; i++){
            if (this.definition().originalModes()[i] != PredicateDefinition.CONSTANT){
                if (this.children[i] != null){
                    for (Block child : this.children[i]){
                        child.toClause(buffer, variableNames[i], counter, realNumberCounters);
                    }
                }
            }
        }
    }

    /**
     * 
     * @return canonical-string representation of this block.
     */
    public String toCanonicalString(){
        String retVal = this.canonicalString.get();
        if (retVal == null){
            StringBuilder buffer = new StringBuilder();
            buffer.append(PredicateDefinition.integerToPredicate(predicate));
            buffer.append("[");
            for (int i = 0; i < this.arity; i++){
                if (this.definition.modes()[i] == PredicateDefinition.IGNORE){
                    buffer.append("[!]");
                } else if (this.definition.modes()[i] == PredicateDefinition.NUMBER){
                    buffer.append("$").append(PredicateDefinition.integerToType(this.definition.types()[i]));
                } else if (this.definition.modes()[i] == PredicateDefinition.AGGREGATOR){
                    buffer.append("AGG:").append(PredicateDefinition.integerToType(this.definition.types()[i]));
                } else {
                    buffer.append("[");
                    List<String> childrenStrings = new ArrayList<String>();
                    if (this.children[i] != null){
                        for (Block child : this.children[i]){
                            if (child != null){
                                childrenStrings.add(child.toCanonicalString());
                            }
                        }
                    }
                    Collections.sort(childrenStrings);
                    for (int j = 0; j < childrenStrings.size(); j++){
                        buffer.append(childrenStrings.get(j));
                        if (j < childrenStrings.size()-1)
                            buffer.append(",");
                    }
                    buffer.append("]");
                }
                if (i < this.arity-1)
                    buffer.append(",");
            }
            buffer.append("]");
            retVal = buffer.toString();
            this.canonicalString = new SoftReference(retVal);
            //this.hashCode = this.canonicalString.get().hashCode();
        }
        return retVal;
    }
    
    /**
     * 
     * @return copy of this Block which shares all children with this block (therefore it is called "shallow")
     */
    public Block shallowCopy(){
        Block myCopy = new Block(this.definition, this.aggregablesBuilder);
        for (int i = 0; i < this.children.length; i++){
            if (this.definition.modes()[i] == PredicateDefinition.OUTPUT){
                if (this.children[i] != null){
                    myCopy.setChildren(this.children[i], i);
                }
            }
        }
        return myCopy;
    }

    /**
     * 
     * @return blocks which have no children
     */
    public Set<Block> leaves(){
        Set<Block> retVal = new HashSet<Block>();
        for (int i = 0; i < this.children.length; i++){
            for (Block child : this.children[i]){
                if (child != null){
                    retVal.addAll(child.leaves());
                }
            }
        }
        if (retVal.isEmpty()){
            retVal.add(this);
        }
        return retVal;
    }

    /**
     * 
     * @return set of all predicates (represented as integers) present in this Block.
     */
    public IntegerSet predicates(){
        IntegerSet retVal = this.predicates.get();
        if (retVal != null){
            return retVal;
        } else {
            Set<IntegerSet> descendantPredicates = new HashSet<IntegerSet>();
            for (int i = 0; i < this.children.length; i++){
                for (Block b : this.children[i]){
                    if (b != null){
                        descendantPredicates.add(b.predicates());
                    }
                }
            }
            descendantPredicates.add(IntegerSet.createIntegerSet(new int[]{this.predicate}));
            retVal = IntegerSet.union(descendantPredicates);
            this.predicates = new SoftReference<IntegerSet>(retVal);
            return retVal;
        }
    }

    /**
     * 
     * @return frequencies (absolute = counts) of predicates in this Block (predicates represented as integers).
     */
    public Counters<Integer> predicateCounts(){
        Counters<Integer> cachedRetVal = this.predicateCounts.get();
        if (cachedRetVal != null){
            return cachedRetVal;
        } else {
            Counters<Integer> retVal = new Counters<Integer>();
            retVal.increment(this.predicate);
            for (int i = 0; i < this.children.length; i++){
                for (Block b : this.children[i]){
                    if (b != null){
                        retVal.addAll(b.predicateCounts());
                    }
                }
            }
            this.predicateCounts = new SoftReference<Counters<Integer>>(retVal);
            return retVal;
        }
    }
    
    /**
     * Method for parsing string representations of Blocks. The String clauseAsString must conform with the toClause() method of class Block, e.g. it must start with the root etc.
     * @param clauseAsString string representation of the block
     * @return parsed Block
     */
    public static Block parse(String clauseAsString){
        Clause clause = Clause.parse(clauseAsString);
        LinkedHashSet<Literal> literals = clause.literals();
        Map<Term,Pair<Block,Integer>> outputVariables = new HashMap<Term,Pair<Block,Integer>>();
        Map<Term,Block> constants = new HashMap<Term,Block>();
        Block root = null;
        for (Literal l : literals){
            int input = -1;
            if (!outputVariables.isEmpty()){
                for (int i = 0; i < l.arity(); i++){
                    if (outputVariables.containsKey(l.get(i))){
                        input = i;
                        break;
                    }
                }
            }
            int predicate = PredicateDefinition.predicateToInteger(l.predicate().name, l.arity());
            int[] types = new int[l.arity()];
            int[] modes = new int[l.arity()];
            int[] branching = new int[l.arity()];
            Arrays.fill(branching, Integer.MAX_VALUE);
            int[] originalModes = new int[l.arity()];
            for (int i = 0; i < l.arity(); i++){
                if (l.get(i) instanceof Variable){
                    if (l.get(i).name().equals("_")){
                        modes[i] = PredicateDefinition.IGNORE;
                        originalModes[i] = PredicateDefinition.IGNORE;
                    } else {
                        if (i == input){
                            modes[i] = PredicateDefinition.INPUT;
                            originalModes[i] = PredicateDefinition.INPUT;
                        } else {
                            modes[i] = PredicateDefinition.OUTPUT;
                            originalModes[i] = PredicateDefinition.OUTPUT;
                        }
                    }
                } else {
                    modes[i] = PredicateDefinition.OUTPUT;
                    originalModes[i] = PredicateDefinition.CONSTANT;
                    PredicateDefinition constantDefinition = new PredicateDefinition(PredicateDefinition.predicateToInteger(l.get(i).name(), l.arity()), 
                            new int[]{0}, new int[]{PredicateDefinition.INPUT}, new int[]{Integer.MAX_VALUE}, Integer.MAX_VALUE, new int[]{PredicateDefinition.INPUT});
                    constants.put(l.get(i), new Block(constantDefinition));
                }
            }
            Block block = new Block(new PredicateDefinition(predicate, types, modes, branching, Integer.MAX_VALUE, originalModes));
            if (root == null){
                root = block;
            }
            for (int i = 0; i < l.arity(); i++){
                if (i == input){
                    Pair<Block,Integer> parent = outputVariables.get(l.get(i));
                    parent.r.addChild(block, parent.s);
                } else {
                    if (l.get(i) instanceof Variable){
                        outputVariables.put(l.get(i), new Pair<Block,Integer>(block, i));
                    } else {
                        block.addChild(constants.get(l.get(i)), i);
                    }
                }
            }
        }
        return root;
    }
    
    /**
     * Applies the given function on all Blocks in this Block (i.e. on children and children of children...)
     * @param function function which should be applied on blocks
     */
    public void applyRecursively(Sugar.Fun<Block,Block> function){
        function.apply(this);
        for (Join join : this.children){
            if (join != null){
                for (Block child : join){
                    child.applyRecursively(function);
                }
            }
        }
    }
}
