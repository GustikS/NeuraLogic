/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.features.treeliker.aggregables.PolyAggregablesBuilder;
import cz.cvut.fel.ida.logic.features.treeliker.aggregables.PolyAggregable;
import cz.cvut.fel.ida.logic.features.treeliker.impl.*;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Tuple;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class implementing RelF-based algorithm for construction of monomial features (Kuzelka, Szaboova, Zelezny, ICTAI, 2012, submitted).
 * 
 * @author Ondra
 */
public class Poly {
    
    private List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();

    private Dataset examples;

    private int maxSize = Integer.MAX_VALUE;
    
    private boolean[] mask;

    private boolean[] negMask;
    
    private boolean alive = true;
    
    private HSubsumption hsubsumptionEngine = new HSubsumption();
    
    private DomainComputing domainComputing;
    
    private Pruning pruning;
    
    private List<SyntaxChecker> syntaxCheckers = new ArrayList<SyntaxChecker>();
    
    private PolyAggregablesBuilder aggregablesBuilder;

    private boolean useGroundingCounting = true;
    /**
     * Creates a new instance of class Poly.
     * @param examples datset of examples for which features should be constructed or for which propositionalized table should be constructed.
     */
    public Poly(Dataset examples){
        this.examples = examples;
        this.domainComputing = new DomainComputing(this.examples.shallowCopy());
        this.mask = new boolean[examples.countExamples()];
        Arrays.fill(this.mask, true);
        this.negMask = VectorUtils.not(mask);
        this.pruning = new Pruning(mask, negMask, 1);
    }

    /**
     * 
     * Constructs all non-reducible non-redundant features complying with the language bias
     * specified using set <em>definitions</em>
     * @param definitions templates - i.e. specification of language bias (similar to mode declarations)
     * @param maxDegree maximum degree of the constructed monomials
     * @return List of constructed features
     */
    public List<Block> constructFeatures(Set<PredicateDefinition> definitions, int maxDegree){
        if (!alive){
            throw new IllegalStateException("Each instance of class RelF can be used only once.");
        } else {
            alive = false;
        }
        this.aggregablesBuilder = PolyAggregablesBuilder.construct(maxDegree);
        List<Block> constructedFeatures = new ArrayList<Block>();
        MultiList<Integer, Block> mm = new MultiList<Integer, Block>();
        List<PredicateDefinition> open = buildOpenList(definitions);
        this.syntaxCheckers.add(new MaxSizeChecker(this.maxSize, open));
        this.syntaxCheckers.add(new NumAggregatorsChecker(maxDegree));
        for (ProgressListener pl : progressListeners) {
            pl.readingExamples();
        }
        examples.reset();
        List<PredicateDefinition> inputOnlyDefinitions = new ArrayList<PredicateDefinition>();
        for (PredicateDefinition def : open){
            if (def.isInputOnly()){
                inputOnlyDefinitions.add(def);
            }
        }
        open = Sugar.listDifference(open, inputOnlyDefinitions);
        for (PredicateDefinition def : inputOnlyDefinitions){
            if (TreeLikerSettings.VERBOSITY > 0){
                System.out.println("Processing: " + def);
            }
            for (ProgressListener pl : this.progressListeners){
                pl.processingPredicate(def);
            }
            if (def.isInputOnly()) {
                List<Block> generatedPosFeatures = new ArrayList<Block>();
                Block posFeat = new Block(def, this.aggregablesBuilder);
                int[] modes = def.modes();
                for (int j = 0; j < modes.length; j++) {
                    if (modes[j] == PredicateDefinition.INPUT){
                        generatedPosFeatures.add(posFeat);
                    }
                }
                mm.putAll(def.types()[def.input()], generatedPosFeatures);
            }
        }
        for (Map.Entry<Integer,List<Block>> entry : mm.copy().entrySet()){
            Map<Integer, Domain[]> computedDomains = domainComputing.computeTermDomainsInParallel(entry.getValue());
            Collection<Block> filtered = pruning.filterRedundantBlocksInParallel(entry.getValue(), computedDomains);
            mm.set(entry.getKey(), filtered);
        }
        examples.reset();
        for (int i = 0; i < open.size(); i++) {
            PredicateDefinition def = open.get(i);
            if (TreeLikerSettings.VERBOSITY > 0){
                System.out.println("Processing: " + def);
            }
            for (ProgressListener pl : this.progressListeners){
                pl.processingPredicate(def);
            }
            List<Collection<Join>> combinations = Collections.synchronizedList(new ArrayList<Collection<Join>>());
            for (int j = 0; j < def.modes().length; j++) {
                if (def.modes()[j] == PredicateDefinition.OUTPUT) {
                    combinations.add(combinationsOfPosFeatures(mm, def.types()[j], def.branchingFactors()[j]));
                } else {
                    combinations.add(new ArrayList<Join>());
                }
            }
            Collection<Block> newBlocks = buildBlocksFromCombinations(def, combinations);
            if (def.isOutputOnly()){
                for (Block newFeature : newBlocks){
                    constructedFeatures.add(newFeature);
                }
            } else {
                if (mm.containsKey(def.types()[def.input()])){
                    Map<Integer, Domain[]> computedDomains = domainComputing.computeTermDomainsInParallel(Sugar.listFromCollections(newBlocks, mm.get(def.types()[def.input()])));
                    Collection<Block> filtered = pruning.filterRedundantBlocksInParallel(Sugar.setFromCollections(newBlocks, mm.get(def.types()[def.input()])), computedDomains);
                    mm.set(def.types()[def.input()], filtered);
                } else {
                    mm.putAll(def.types()[def.input()], newBlocks);
                }
            }
        }
        return constructedFeatures;
    }
    
   /**
     * Given a set of features (instances of class Block), this method computes the propositionalized table.
     * @param constructedFeatures set of constructed features
     * @return propositionalized table (i.e. an attribute-value table) in which the features play the role of attributes.
     */
    public Table<Integer,String> constructTable(List<Block> constructedFeatures){
        Table<Integer,String> table = new Table<Integer,String>();
        examples.reset();
        while (examples.hasNextExample()){
            examples.nextExample();
            table.addClassification(examples.currentIndex(), examples.classificationOfCurrentExample());
        }
        Map<Integer,Domain[]> domains = domainComputing.computeLiteralDomainsInParallel(constructedFeatures);
        examples.reset();
        while (examples.hasNextExample()) {
            Example example = examples.nextExample();
            for (Block block : constructedFeatures){
                if (block.numAggregators() > 0){
                    String toClause = block.toClause();
                    if (!domains.get(block.id())[examples.currentIndex()].isEmpty()){
                        Domain domain = block.termDomain(-1, example);
                        PolyAggregable agg = (PolyAggregable)domain.getAggregableByDomainElement(-1);
                        double[] monomials = agg.monomials();
                        List<Tuple<Integer>> monomialExponents = PolyAggregable.allMonomialExponents(block.numAggregators(), agg.maxDegree());
                        for (int i = 0; i < monomials.length; i++){
                            table.add(examples.currentIndex(), toClause+" -> "+monomialToString(monomialExponents.get(i)), String.valueOf(monomials[i]), block.size());
                        }
                    }
                } else {
                    String toClause = block.toClause();
                    if (!domains.get(block.id())[examples.currentIndex()].isEmpty()){
                        if (useGroundingCounting){
                            Domain domain = block.termDomain(-1, example);
                            PolyAggregable agg = (PolyAggregable)domain.getAggregableByDomainElement(-1);
                            table.add(examples.currentIndex(), toClause, String.valueOf(agg.samples()), block.size());
                        } else {
                            table.add(examples.currentIndex(), toClause, "+", block.size());
                        }
                    } else {
                        if (useGroundingCounting){
                            table.add(examples.currentIndex(), toClause, "0", block.size());
                        } else {
                            table.add(examples.currentIndex(), toClause, "-", block.size());
                        }
                    }
                }
            }
        }
        //missing values, i.e. monomials for which the features do not subsume the examples which results in empty sampling set
        for (String attribute : Sugar.listFromCollections(table.attributes())){
            Map<Integer,String> attributeVector = table.getAttributeVector(attribute);
            for (int example : Sugar.listFromCollections(table.examples())){
                if (!attributeVector.containsKey(example)){
                    table.add(example, attribute, "?");
                }
            }
        }
        for (ProgressListener pl : progressListeners) {
            pl.finished(table.countOfAttributes());
        }
        return table;
    }

    private Collection<Join> combinationsOfPosFeatures(MultiList posFeatures, int inputType, int branching){
        //[example,term] -> [pos feature, which has term in its domain]
        MultiMap<Pair<Integer,Integer>,Block> termsInDomainsOf = new MultiMap<Pair<Integer,Integer>,Block>();
        List<Block> listOfBlocks = Sugar.listFromCollections(posFeatures.get(inputType));
        ConcurrentHashMap<Block,Integer> indices = new ConcurrentHashMap<Block,Integer>();
        ConcurrentHashMap<Integer,Block> posFeaturesLookup = new ConcurrentHashMap<Integer,Block>();
        for (int i = 0; i < listOfBlocks.size(); i++){
            indices.put(listOfBlocks.get(i), i);
            posFeaturesLookup.put(listOfBlocks.get(i).id(), listOfBlocks.get(i));
        }
        ConcurrentHashMap<Integer,Domain[]> computedDomainsOfBlocks = domainComputing.computeTermDomainsInParallel(listOfBlocks);
        List<TermsInDomainsOfBuilder> tasks0 = new ArrayList<TermsInDomainsOfBuilder>();
        for (Map<Integer,Domain[]> taskMap : Sugar.splitMap(computedDomainsOfBlocks, Runtime.getRuntime().availableProcessors())){
            tasks0.add(new TermsInDomainsOfBuilder(termsInDomainsOf, taskMap, posFeaturesLookup, mask));
        }
        Sugar.runInParallel(tasks0);
        //[number of pos features in combination] -> [combination of pos features, index of the last pos feature in the combination]
        MultiList<Integer,JoinAndInt> hbl = new MultiList<Integer,JoinAndInt>();
        ConcurrentHashMap<Pair<Block,Block>,Boolean> hSubsumptions = new ConcurrentHashMap<Pair<Block,Block>,Boolean>();
        ConcurrentHashMap<List<Domain>,Integer> redundancyMap = new ConcurrentHashMap<List<Domain>,Integer>();
        for (int i = 0; i < listOfBlocks.size(); i++){
            hbl.put(1, new JoinAndInt(new Join(listOfBlocks.get(i)), i));
        }
        for (int i = 1; i < branching; i++){
            List<CombinationsBuilder> tasks1 = Collections.synchronizedList(new ArrayList<CombinationsBuilder>());
            for (JoinAndInt tuplePair : hbl.get(i)){
                //suspicious - maybe race condition - check later!!!!
                if (tuplePair != null){
                    CombinationsBuilder cb = new CombinationsBuilder(tuplePair, redundancyMap, indices, hSubsumptions, hbl, i, termsInDomainsOf, domainComputing, hsubsumptionEngine, pruning, syntaxCheckers);
                    cb.setUseRepetitions(true);
                    tasks1.add(cb);
                }
            }
            Sugar.runInParallel(tasks1, Runtime.getRuntime().availableProcessors());
            for (CombinationsBuilder cb : tasks1){
                redundancyMap.putAll(cb.getRedundancyMap());
            }
            if (hbl.get(i+1).isEmpty()){
                break;
            }
            Collection<JoinAndInt> filtered = hbl.get(i+1);
            hbl.set(i+1, filtered);
        }
        List<JoinAndInt> flattened = Collections.synchronizedList(new ArrayList<JoinAndInt>());
        for (Map.Entry<Integer, List<JoinAndInt>> entry : hbl.entrySet()){
            for (JoinAndInt pair : entry.getValue()){
                flattened.add(pair);
            }
        }
        List<Join> retVal = Collections.synchronizedList(new ArrayList<Join>());
        for (JoinAndInt pair : flattened){
            if (pair != null){
                retVal.add(pair.r);
            }
        }
        return retVal;
    }

    private Collection<Block> buildBlocksFromCombinations(PredicateDefinition def, List<Collection<Join>> combinations){
        //[argument,literal's] id (in domain of pos feature F) -> pos feature F
        MultiMap<Pair<Integer,Integer>,Join> bagPosFeaturesWithLiteralsInDomain = new MultiMap<Pair<Integer,Integer>,Join>();
        int argument = 0;
        for (Collection<Join> list : combinations){
            List<BlocksWithLiteralsInDomainBuilder> tasks0 = new ArrayList<BlocksWithLiteralsInDomainBuilder>();
            for (Join join : list){
                tasks0.add(new BlocksWithLiteralsInDomainBuilder(join, def.predicate(), argument, bagPosFeaturesWithLiteralsInDomain, this.examples));
            }
            Sugar.runInParallel(tasks0, Runtime.getRuntime().availableProcessors());
            argument++;
        }
        Block root = new Block(def, this.aggregablesBuilder);
        List<Block> old = Sugar.<Block>list(root);
        List<Block> current = Collections.synchronizedList(new ArrayList<Block>());
        ConcurrentHashMap<Integer,Domain[]> domains = new ConcurrentHashMap<Integer,Domain[]>();
        domains.put(root.id(), domainComputing.computeLiteralDomains(root));
        for (int arg = 0; arg < def.modes().length; arg++){
            if (def.modes()[arg] == PredicateDefinition.OUTPUT){
                List<BlockBuilder> tasks1 = new ArrayList<BlockBuilder>();
                for (Block oldPos : old){
                    tasks1.add(new BlockBuilder(oldPos, arg, bagPosFeaturesWithLiteralsInDomain, domains, current, domainComputing, pruning, this.syntaxCheckers));
                }
                Sugar.runInParallel(tasks1, TreeLikerSettings.PROCESSORS);
                current = Sugar.listFromCollections(pruning.filterRedundantBlocksInParallel(current, domains));
                old = current;
                current = Collections.synchronizedList(new ArrayList<Block>());
            }
        }
        return old;
    }

    /**
     * Sets minimum frequency of features.
     * @param minFreq absolute minimum-frequency (i.e. not relative minimum frequency represented as percentage or something similar)
     */
    public void setMinFrequency(int minFreq){
        this.pruning.setMinFrequencyOnCoveredClass(minFreq);
    }

    private static List<PredicateDefinition> buildOpenList(Collection<PredicateDefinition> defs){
        return FeatureSearchUtils.buildPredicateList(defs);
    }

    /**
     * Adds progress listeners
     * @param pl progress listener
     */
    public void addProgressListener(ProgressListener pl) {
        progressListeners.add(pl);
    }
    
    /**
     * Adds syntax checker. Syntax checkers are used to constrain the form of Blocks,
     * for example it is possible to have MaxSizeChecker, which efficiently checks if
     * a given block can be extended to a full feature (complying to the language bias)
     * with size at most a given limit (note that this problem is NP-hard for non-tree-like features - see (Kuzelka, 2009 - Master's thesis).
     * However, this MaxSizeChecker is used by default by HiFi so it is not neccessary to set it explicitly - but still, it
     * is possible to have some custom syntax checkers.
     * 
     * @param syntaxChecker the syntax checker
     */
    public void addSyntaxChecker(SyntaxChecker syntaxChecker){
        this.syntaxCheckers.add(syntaxChecker);
    }

    /**
     * Sets the maximum allowed size of features to be constructed.
     * @param maxSize the maxSize
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    private static String monomialToString(Tuple<Integer> exponents){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < exponents.size(); i++){
            if (exponents.get(i) != 0){
                if (first){
                    first = false;
                } else {
                    sb.append("*");
                }
                if (exponents.get(i) == 1){
                    sb.append("x").append(i+1);
                } else {
                    sb.append("x").append(i+1).append("^").append(exponents.get(i));
                }
            }
        }
        return sb.toString();
    }

    /**
     * @param countTrueGroundings the countTrueGroundings to set
     */
    public void setUseGroundingCounting(boolean countTrueGroundings) {
        this.useGroundingCounting = countTrueGroundings;
    }
}
