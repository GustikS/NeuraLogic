/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.features.treeliker.aggregables.VoidAggregablesBuilder;
import cz.cvut.fel.ida.logic.features.treeliker.impl.*;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class implementing the RelF algorithm (Kuzelka, Zelezny, Machine Learning, 2011).
 * @author ondra
 */
public class RelF {

    private List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();

    public final static int EXISTENTIAL = 1, GROUNDINGS = 2;

    private int featureMode = EXISTENTIAL;

    private Dataset examples;

    private boolean[] mask;

    private boolean[] negMask;

    private boolean alive = true;

    protected static int LITERALS = 1, TERMS = 2;
    
    private HSubsumption hsubsumptionEngine = new HSubsumption();
    
    private DomainComputing domainComputing;
    
    private Pruning pruning;
    
    private List<SyntaxChecker> syntaxCheckers = new ArrayList<SyntaxChecker>();
    
    private AggregablesBuilder aggregablesBuilder = VoidAggregablesBuilder.construct();

    /**
     * Creates a new instance of class HiFi.
     * @param examples datset of examples for which features should be constructed or for which propositionalized table should be constructed.
     */
    public RelF(Dataset examples){
        this(examples, "+");
    }

    /**
     * Creates a new instance of class HiFi.
     * @param examples datset of examples for which features should be constructed or for which propositionalized table should be constructed.
     * @param coveredClass the class for which RelF should construct non-redundant features
     * (a feature F1 is redundant for class_1 if there is another feature F2 which covers a superset 
     * of examples from class_1 coverd by F1 and subset of the remaining examples covered by F1).
     */
    public RelF(Dataset examples, String coveredClass){
        this.examples = examples;
        this.mask = new boolean[examples.countExamples()];
        this.domainComputing = new DomainComputing(this.examples.shallowCopy());
        examples.reset();
        for (int i = 0; examples.hasNextExample(); i++){
            examples.nextExample();
            if (examples.classificationOfCurrentExample().equals(coveredClass)){
                this.mask[i] = true;
            }
        }
        this.negMask = VectorUtils.not(mask);
        this.pruning = new Pruning(mask, negMask, 1);
    }

    /**
     * Constructs all non-reducible non-redundant (for the selected class) features complying with the language bias
     * specified using set <em>definitions</em>
     * @param definitions templates - i.e. specification of language bias (similar to mode declarations)
     * @return List of constructed features
     */
    public List<Block> constructFeatures(Set<PredicateDefinition> definitions) {
        if (!alive){
            throw new IllegalStateException("Each instance of class RelF can be used only once.");
        } else {
            alive = false;
        }
        List<Block> constructedFeatures = new ArrayList<Block>();
        MultiList<Integer, Block> mm = new MultiList<Integer, Block>();
        List<PredicateDefinition> open = buildOpenList(definitions);
        for (ProgressListener pl : progressListeners) {
            pl.readingExamples();
        }
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
            Collection<Block> filtered = pruning.filterIrrelevantBlocksInParallel(
                        pruning.filterRedundantBlocksInParallel(entry.getValue(), computedDomains), 
                    computedDomains);
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
                    filtered = pruning.filterIrrelevantBlocksInParallel(filtered, computedDomains);
                    mm.set(def.types()[def.input()], filtered);
                } else {
                    mm.putAll(def.types()[def.input()], newBlocks);
                }
            }
        }
        return constructedFeatures;
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
        ConcurrentHashMap<Integer,Domain[]> computedDomainsOfJoins = domainComputing.computeTermDomainsInParallel(hbl.get(1));
        for (int i = 1; i < branching; i++){
            List<CombinationsBuilder> tasks1 = Collections.synchronizedList(new ArrayList<CombinationsBuilder>());
            for (JoinAndInt tuplePair : hbl.get(i)){
                CombinationsBuilder cb = new CombinationsBuilder(tuplePair, redundancyMap, indices, hSubsumptions, hbl, i, termsInDomainsOf, domainComputing, hsubsumptionEngine, pruning, this.syntaxCheckers);
                cb.setUseIrrelevancyFiltering(true);
                tasks1.add(cb);
            }
            Sugar.runInParallel(tasks1, Runtime.getRuntime().availableProcessors());
            for (CombinationsBuilder cb : tasks1){
                redundancyMap.putAll(cb.getRedundancyMap());
            }
            if (hbl.get(i+1).isEmpty()){
                break;
            }
            ConcurrentHashMap<Integer,Domain[]> compDoms = domainComputing.computeTermDomainsInParallel(hbl.get(i+1));
            Collection<JoinAndInt> filtered = pruning.filterIrrelevantCombinationsOfBlocksInParallel(
                        pruning.filterRedundantCombinationsOfBlocksInParallel(hbl.get(i+1), compDoms), 
                    compDoms);
            computedDomainsOfJoins.putAll(compDoms);
            hbl.set(i+1, filtered);
        }
        List<JoinAndInt> flattened = Collections.synchronizedList(new ArrayList<JoinAndInt>());
        for (Map.Entry<Integer, List<JoinAndInt>> entry : hbl.entrySet()){
            for (JoinAndInt pair : entry.getValue()){
                flattened.add(pair);
            }
        }
        List<Join> retVal = Collections.synchronizedList(new ArrayList<Join>());
        for (JoinAndInt pair : pruning.filterIrrelevantCombinationsOfBlocksInParallel(
                    pruning.filterRedundantCombinationsOfBlocksInParallel(flattened, computedDomainsOfJoins), 
                computedDomainsOfJoins)){
            retVal.add(pair.r);
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
                    BlockBuilder bb = new BlockBuilder(oldPos, arg, bagPosFeaturesWithLiteralsInDomain, domains, current, domainComputing, pruning, this.syntaxCheckers);
                    bb.setUseIrrelevancyFiltering(true);
                    tasks1.add(bb);
                }
                Sugar.runInParallel(tasks1, TreeLikerSettings.PROCESSORS);
                current = Sugar.listFromCollections(
                            pruning.filterIrrelevantBlocksInParallel(pruning.filterRedundantBlocksInParallel(current, domains), 
                        domains));
                old = current;
                current = Collections.synchronizedList(new ArrayList<Block>());
            }
        }
        return old;
    }

    

    /**
     * 
     * @param minFreq minimum frequency on the covered class (absolute - i.e. number of examples that must be covered) - specifies that only features
     * which cover at least minFreq examples from the covered class should be constructed.
     */
    public void setMinFrequencyOnClass(int minFreq){
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
     * 
     * @param mode
     */
    public void setFeatureMode(int mode){
        this.featureMode = mode;
    }

    /**
     * Sets the AggregablesBuilder which is used to bguild Aggregables which are 
     * objects used for various aggregation tasks or extraction tasks etc.
     * 
     * @param aggregablesBuilder the aggregablesBuilder to set
     */
    public void setAggregablesBuilder(AggregablesBuilder aggregablesBuilder) {
        this.aggregablesBuilder = aggregablesBuilder;
    }
    
    /**
     * Adds syntax checker. Syntax checkers are used to constrain the form of Blocks.
     * 
     * @param syntaxChecker the syntax checker
     */
    public void addSyntaxChecker(SyntaxChecker syntaxChecker){
        this.syntaxCheckers.add(syntaxChecker);
    }
}
