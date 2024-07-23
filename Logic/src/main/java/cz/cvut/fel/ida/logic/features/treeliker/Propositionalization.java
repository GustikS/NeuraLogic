/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Constant;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.LogicUtils;
import cz.cvut.fel.ida.logic.features.treeliker.aggregables.GroundingCountingAggregablesBuilder;
import cz.cvut.fel.ida.logic.features.treeliker.aggregables.VoidAggregablesBuilder;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.generic.tuples.Triple;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.VectorUtils;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.io.*;
import java.util.*;
/**
 * Class wrapping the algorithms RelF, HiFi and Poly so that they could be used easily. It also
 * contains preprocessing of learning examples which isessential for the algorithms because,
 * for example, it would be impossible to construct features with constants without preprocessing
 * (because the raw implementations of RelF, HiFi and Poly do not explicitly support constants but,
 * instead, treat them as unary literals - e.g. bond(A,B,single) ~ bond(A,B,C), single(C)).
 * 
 * @author Ondra
 */
public class Propositionalization {

    private double minFrequency = 0;

    //only for debugging
    static boolean filterIrrelevantSubfeatures = true;
    
    private int seed = 1;
    
    private int maxDegree = 1;

    public static final int RELF = 3, HIFI = 4, HIFI_GROUNDING_COUNTING = 6, RELF_GROUNDING_COUNTING = 7, POLY = 9, POLY_GROUNDING_COUNTING = 10;

    private Set<String> coveredClasses = null;

    private int foldsCount = 10;
    
    private boolean constructFeaturesOnlyFromFirstExample = false;
    
    private boolean useSampling = false;
    
    private int sampleSize = 10;
    
    private int numSamples = 10;
    
    private Block normalizationFactor;

    /**
     * Runs RelF.
     * @param template language bias specified as so-called template
     * @param reader reader for the training examples in pseudo-prolog format (when on wants
     * to read examples represented in Prolog format then it is possible to use class Prolog2PseudoPrologReader
     * which reads Prolog file but acts as if it was reading pseudo-prolog)
     * @return propositionalizaed table
     * @throws IOException
     */
    public Table<String,String> relf(String template, Reader reader) throws IOException {
        return propositionalize(template, Integer.MAX_VALUE, reader, RELF);
    }

    /**
     * 
     * Runs RelF.
     * @param template language bias specified as so-called template
     * @param reader reader for the training examples in pseudo-prolog format (when on wants
     * to read examples represented in Prolog format then it is possible to use class Prolog2PseudoPrologReader
     * which reads Prolog file but acts as if it was reading pseudo-prolog)
     * @param mode can be either RELF (only checks if a feature is true for a given example) or RELF_GROUNDING_COUNTING (counts true groundings of features w.r.t. the examples)
     * @return propositionalizaed table
     * @throws IOException
     */
    public Table<String,String> relf(String modeDeclarations, Reader reader, int mode) throws IOException {
        if (mode != RELF && mode != RELF_GROUNDING_COUNTING){
            throw new IllegalArgumentException("The argument must be either RELF or RELF_GROUNDING_COUNTING");
        }
        return propositionalize(modeDeclarations, Integer.MAX_VALUE, reader, mode);
    }

    /**
     * Runs HiFi.
     * @param template language bias specified as so-called template
     * @param reader reader for the training examples in pseudo-prolog format (when on wants
     * to read examples represented in Prolog format then it is possible to use class Prolog2PseudoPrologReader
     * which reads Prolog file but acts as if it was reading pseudo-prolog)
     * @param maxFeatureLength maximum allowed size of features (i.e. maximum number of literals in it)
     * @param mode can be either HIFI (only checks if a feature is true for a given example) or HIFI_GROUNDING_COUNTING (counts true groundings of features w.r.t. the examples)
     * @return propositionalizaed table
     * @throws IOException
     */
    public Table<String,String> hifi(String template, int maxFeatureLength, Reader reader, int mode) throws IOException {
        if (mode != HIFI && mode != HIFI_GROUNDING_COUNTING){
            throw new IllegalArgumentException("The argument must be either HIFI or HIFI_GROUNDING_COUNTING");
        }
        return propositionalize(template, maxFeatureLength, reader, mode);
    }

    /**
     * 
     * Runs Poly.
     * @param template language bias specified as so-called template
     * @param reader reader for the training examples in pseudo-prolog format (when on wants
     * to read examples represented in Prolog format then it is possible to use class Prolog2PseudoPrologReader
     * which reads Prolog file but acts as if it was reading pseudo-prolog)
     * @param maxFeatureLength maximum allowed size of features (i.e. maximum number of literals in it)
     * @return propositionalizaed table
     * @throws IOException
     */
    public Table<String,String> poly(String template, int maxFeatureLength, Reader reader) throws IOException {
        return propositionalize(template, maxFeatureLength, reader, POLY);
    }
   
    /**
     * 
     * Runs Poly.
     * @param template language bias specified as so-called template
     * @param reader reader for the training examples in pseudo-prolog format (when on wants
     * to read examples represented in Prolog format then it is possible to use class Prolog2PseudoPrologReader
     * which reads Prolog file but acts as if it was reading pseudo-prolog)
     * @param maxFeatureLength maximum allowed size of features (i.e. maximum number of literals in it)
     * @param mode can be either POLY (only checks if a non-polynomial feature is true for a given example) or POLY_GROUNDING_COUNTING (counts true groundings of non-polynomial features w.r.t. the examples)
     * @return propositionalizaed table
     * @throws IOException
     */
    public Table<String,String> poly(String template, int maxFeatureLength, Reader reader, int mode) throws IOException {
        if (mode != POLY && mode != POLY_GROUNDING_COUNTING){
            throw new IllegalArgumentException("The argument must be either POLY or POLY_GROUNDING_COUNTING");
        }
        return propositionalize(template, maxFeatureLength, reader, mode);
    }
    
    private Table<String,String> propositionalize(String template, int maxSize, Reader reader, int mode) throws IOException {
        Triple<List<Set<PredicateDefinition>>,List<PredicateDefinition>,Dataset> preprocessed = readDataset(template, reader);
        Set<Block> features = constructFeatures(preprocessed.r, preprocessed.s, preprocessed.t, mode, maxSize);
        return buildTable(features, preprocessed.s, preprocessed.t, mode).transform(
                new Sugar.Fun<Integer,String>(){
            @Override
                    public String apply(Integer t) {
                        return String.valueOf(t);
                    }
                }, 
                new Sugar.IdentityFun<String>(), 
                new Sugar.IdentityFun<String>());
    }
    
    private Set<Block> constructFeaturesUsingSampling_impl(Collection<Set<PredicateDefinition>> templates, Dataset dataset, int mode, int maxSize){
        Set<Block> retVal = new HashSet<Block>();
        Random rand = new Random(seed);
        for (int i = 0; i < this.numSamples; i++){
            Dataset subsampled = dataset.subsample(this.sampleSize, rand.nextInt());
            retVal.addAll(constructFeatures_impl(templates, subsampled, mode, maxSize));
        }
        return retVal;
    }
    
    private Set<Block> constructFeatures(Collection<Set<PredicateDefinition>> templates, List<PredicateDefinition> globalConstants, Dataset dataset, int mode, int maxSize){
        if (useSampling){
            return constructFeaturesUsingSampling_impl(templates, dataset, mode, maxSize);
        } else {
            return constructFeatures_impl(templates, dataset, mode, maxSize);
        }
    }
    
    private Set<Block> constructFeatures_impl(Collection<Set<PredicateDefinition>> templates, Dataset dataset, int mode, int maxSize){
        Set<Block> retVal = new HashSet<Block>();
        for (Set<PredicateDefinition> def : templates){
            if (mode == RELF || mode == RELF_GROUNDING_COUNTING){
                Collection<String> tsclasses = dataset.classes();
                for (String cl : tsclasses){
                    if (this.coveredClasses == null || coveredClasses.contains(cl) || coveredClasses.contains("'"+cl+"'") 
                            || coveredClasses.contains(LogicUtils.unquote(Constant.construct(cl)).name())){
                        RelF relf = new RelF(dataset, cl);
                        if (mode == RELF_GROUNDING_COUNTING){
                            relf.setFeatureMode(RelF.GROUNDINGS);
                        }
                        if (minFrequency > 0){
                            relf.setMinFrequencyOnClass(Math.max(1, (int)(minFrequency*dataset.countExamples(cl))));
                        }
                        retVal.addAll(relf.constructFeatures(def));
                    }
                }
            } else if (mode == HIFI || mode == HIFI_GROUNDING_COUNTING){
                HiFi featureConstructionHifi = new HiFi(dataset);
                featureConstructionHifi.setMaxSize(maxSize);
                if (mode == HIFI_GROUNDING_COUNTING){
                    featureConstructionHifi.setAggregablesBuilder(GroundingCountingAggregablesBuilder.construct());
                    featureConstructionHifi.setPostProcessingAggregablesBuilder(GroundingCountingAggregablesBuilder.construct());
                }
                if (minFrequency > 0){
                    featureConstructionHifi.setMinFrequency(Math.max(1, (int)(minFrequency*dataset.countExamples())));
                }
                //hifi.setConstructFeaturesOnlyFromFirstExample(this.constructFeaturesOnlyFromFirstExample);
                featureConstructionHifi.setMaxSize(maxSize);
                if (minFrequency > 0){
                    featureConstructionHifi.setMinFrequency(Math.max(1, (int)(minFrequency*dataset.countExamples())));
                }
                retVal.addAll(featureConstructionHifi.constructFeatures(def));
            } else if (mode == POLY || mode == POLY_GROUNDING_COUNTING){
                Poly featureConstructionHiFi = null;
                if (this.constructFeaturesOnlyFromFirstExample){
                    MemoryBasedDataset smallDataset = new MemoryBasedDataset();
                    dataset.reset();
                    if (dataset.hasNextExample()){
                        smallDataset.addExample(dataset.nextExample(), dataset.classificationOfCurrentExample());
                    }
                    featureConstructionHiFi = new Poly(smallDataset);
                } else {
                    featureConstructionHiFi = new Poly(dataset);
                }
                if (mode == POLY){
                    featureConstructionHiFi.setUseGroundingCounting(false);
                } else if (mode == POLY_GROUNDING_COUNTING){
                    featureConstructionHiFi.setUseGroundingCounting(true);
                }
                //hifi.setConstructFeaturesOnlyFromFirstExample(this.constructFeaturesOnlyFromFirstExample);
                featureConstructionHiFi.setMaxSize(maxSize);
                if (minFrequency > 0){
                    featureConstructionHiFi.setMinFrequency(Math.max(1, (int)(minFrequency*dataset.countExamples())));
                }
                retVal.addAll(featureConstructionHiFi.constructFeatures(def, maxDegree));
            }
        }
        return retVal;
    }
    
    private void makeCompatible(Table<Integer,String> trainTable, Table<Integer,String> testTable, int mode){
        if (mode == HIFI || mode == RELF){
            for (String attribute : trainTable.attributes()){
                Set<String> values = trainTable.getAttributeValues(attribute);
                if (values.size() == 1 && Sugar.chooseOne(values).equals("+")){
                    trainTable.addAdditionalUnseenValue(attribute, "-");
                }
            }
            for (String attribute : testTable.attributes()){
                Set<String> values = testTable.getAttributeValues(attribute);
                if (values.size() == 1 && Sugar.chooseOne(values).equals("+")){
                    testTable.addAdditionalUnseenValue(attribute, "-");
                }
            }
        }
        trainTable.makeCompatible(testTable);
        testTable.makeCompatible(trainTable);
    }
    
    /**
     * Builds propositionalized table from the given features (Blocks) and the given Dataset. The Dataset is expected
     * to be preprocessed.
     * @param features the constructed features
     * @param globalConstants global constants
     * @param dataset dataset for which the table should be constructed
     * @param mode can be one of the following: HIFI, HIFI_GROUNDING_COUNTING, RELF, RELF_GROUNDING_COUNTING, POLY
     * @return the resulting propositionalized table
     */
    private Table<Integer,String> buildTable(Collection<Block> features, List<PredicateDefinition> globalConstants, Dataset dataset, int mode){
        Table<Integer,String> table = new Table<Integer,String>();
        //i.e. not global constants
        List<Block> nonConstantAttributes = new ArrayList<Block>();
        List<Block> globalConstantAttributes = new ArrayList<Block>();
        for (Block attribute : features){
            if (attribute.definition().isGlobalConstant()){
                globalConstantAttributes.add(attribute);
            } else {
                nonConstantAttributes.add(attribute);
            }
        }
        Dataset copyOfDataset = dataset.shallowCopy();
        copyOfDataset.reset();
        while (copyOfDataset.hasNextExample()){
            Example example = copyOfDataset.nextExample();
            table.addClassification(copyOfDataset.currentIndex(), copyOfDataset.classificationOfCurrentExample());
            addGlobalConstants(example, copyOfDataset.currentIndex(), table, globalConstants);
        }
        if (mode == HIFI || mode == HIFI_GROUNDING_COUNTING || mode == RELF || mode == RELF_GROUNDING_COUNTING){
            HiFi tableConstructionHifi = new HiFi(dataset);
            if (mode == HIFI_GROUNDING_COUNTING || mode == RELF_GROUNDING_COUNTING){
                tableConstructionHifi.setAggregablesBuilder(VoidAggregablesBuilder.construct());
                tableConstructionHifi.setPostProcessingAggregablesBuilder(GroundingCountingAggregablesBuilder.construct());
            }
            if (this.normalizationFactor != null){
                tableConstructionHifi.setNormalizationFactor(normalizationFactor);
            }
            table.addAll(tableConstructionHifi.constructTable(nonConstantAttributes));
        } else if (mode == POLY || mode == POLY_GROUNDING_COUNTING){
            Poly tableConstructionHiFi = new Poly(dataset);
            if (mode == POLY){
                tableConstructionHiFi.setUseGroundingCounting(false);
            } else if (mode == POLY_GROUNDING_COUNTING){
                tableConstructionHiFi.setUseGroundingCounting(true);
            }
            table.addAll(tableConstructionHiFi.constructTable(nonConstantAttributes));
        }
        return table;
    }
    
    /**
     * Constructs train and test arff (WEKA) files. Features are built only using the training data (specified through indices in <em>trainSet</em>).
     * 
     * @param template language bias specified through so-called template
     * @param maxSize maximum allowed size of feature (is ignored for RelF)
     * @param trainSet indices of the examples that should be used as training set
     * @param testSet indices of examples that should be sued as testing set
     * @param examples the dataset (to which the indices above point)
     * @param directory directory to which the new files train.arff and test.arff should be saved
     * @param mode can be one of the following: HIFI, HIFI_GROUNDING_COUNTING, RELF, RELF_GROUNDING_COUNTING, POLY
     * @throws IOException
     */
    public void trainTest(String template, int maxSize, int[] trainSet, int[] testSet, Reader examples, String directory, int mode) throws IOException {
        trainTest_impl(template, maxSize, trainSet, testSet, examples, new File(directory+"/train.arff"), new File(directory+"/train.arff"), mode);
    }

    /**
     * Creates train and test arff (WEKA) files. Features are built only using the training data (i.e. examples read using <em>trainReader</em>).
     * 
     * @param template language bias specified through so-called template
     * @param maxSize maximum allowed size of feature (is ignored for RelF)
     * @param trainReader reader for the training examples
     * @param testReader reader for the test-set examples
     * @param directory directory to which the new files train.arff and test.arff should be saved
     * @param mode can be one of the following: HIFI, HIFI_GROUNDING_COUNTING, RELF, RELF_GROUNDING_COUNTING, POLY
     * @throws IOException
     */
    public void trainTest(String template, int maxSize, Reader trainReader, Reader testReader, String directory, int mode) throws IOException {
        List<Clause> clauses = new ArrayList<Clause>();
        List<String> classifications = new ArrayList<String>();
        readExamples(trainReader, clauses, classifications);
        int[] trainSetIndices = new int[clauses.size()];
        for (int i = 0; i < trainSetIndices.length; i++){
            trainSetIndices[i] = i;
        }
        readExamples(testReader, clauses, classifications);
        int[] testSetIndices = new int[clauses.size()-trainSetIndices.length];
        for (int i = 0; i < testSetIndices.length; i++){
            testSetIndices[i] = i + trainSetIndices.length;
        }
        Triple<List<Set<PredicateDefinition>>,List<PredicateDefinition>,Dataset> preprocessed = preprocessDataset(clauses, classifications, PredicateDefinition.parseDefinition(template));
        trainTest_impl(preprocessed.t, preprocessed.r, preprocessed.s, maxSize, trainSetIndices, testSetIndices, new File(directory+"/train.arff"), new File(directory+"/test.arff"), mode);
    }
    
    private void trainTest_impl(String template, int maxSize, int[] trainSetIndices, int[] testSetIndices, Reader exampleReader, File trainOutput, File testOutput, int mode) throws IOException {
        List<Clause> clauses = new ArrayList<Clause>();
        List<String> classifications = new ArrayList<String>();
        readExamples(exampleReader, clauses, classifications);
        Triple<List<Set<PredicateDefinition>>,List<PredicateDefinition>,Dataset> preprocessed = preprocessDataset(clauses, classifications, PredicateDefinition.parseDefinition(template));
        trainTest_impl(preprocessed.t, preprocessed.r, preprocessed.s, maxSize, trainSetIndices, testSetIndices, trainOutput, testOutput, mode);
    }
    
    private void trainTest_impl(Dataset allExamples, List<Set<PredicateDefinition>> preprocessedTemplate, List<PredicateDefinition> globalConstants, int maxSize, int[] trainSetIndices, int[] testSetIndices, File trainOutput, File testOutput, int mode) throws IOException {
        //[[preprocessed templates], [preprocessed global constants], dataset]
        Dataset trainSet = allExamples.get(trainSetIndices);
        Set<Block> features = constructFeatures(preprocessedTemplate, globalConstants, trainSet, mode, maxSize);
        Table<Integer,String> trainTable = buildTable(features, globalConstants, trainSet, mode);
        Dataset testSet = allExamples.get(testSetIndices);
        Table<Integer,String> testTable = buildTable(features, globalConstants, testSet, mode);
        makeCompatible(trainTable, testTable, mode);
        //save
        File dir1 = trainOutput.getParentFile();
        if (!dir1.exists()){
            dir1.mkdirs();
        }
        File dir2 = trainOutput.getParentFile();
        if (!dir2.exists()){
            dir2.mkdirs();
        }
        Writer trainWriter = new FileWriter(trainOutput);
        Writer testWriter = new FileWriter(testOutput);
        trainTable.saveWithoutFiltering(trainWriter);
        testTable.saveWithoutFiltering(testWriter);
        trainWriter.close();
        testWriter.close();
        trainSet.reset();
    }
    
    /**
     * Creates pairs of train- and test-files for cross-validation. For each pair of train- and test-files, 
     * the features are always built only using the respective training data.
     * 
     * @param template language bias specified through so-called template
     * @param maxSize maximum allowed size of feature (is ignored for RelF)
     * @param examples reader for learning examples
     * @param directory directory to which the new files train_i.arff and test_i.arff (i = 1...numFolds) should be saved
     * @param mode can be one of the following: HIFI, HIFI_GROUNDING_COUNTING, RELF, RELF_GROUNDING_COUNTING, POLY
     * @throws IOException
     */
    public void cv(String template, int maxSize, Reader examples, String directory, int mode) throws IOException {
        Set<PredicateDefinition> definitions = PredicateDefinition.parseDefinition(template);
        cv_impl(definitions, maxSize, examples, directory, mode);
    }

    private void cv_impl(Set<PredicateDefinition> template, int maxSize, Reader exampleReader, String directory, int mode) throws IOException {
        List<Clause> clauses = new ArrayList<Clause>();
        List<String> classifications = new ArrayList<String>();
        readExamples(exampleReader, clauses, classifications);
        Triple<List<Set<PredicateDefinition>>,List<PredicateDefinition>,Dataset> preprocessed = preprocessDataset(clauses, classifications, template);
        List<int[]> folds = constructStratifiedFolds(classifications, this.foldsCount, this.seed);
        for (int i = 0; i < this.foldsCount; i++){
            Pair<int[],int[]> trainTestSplit = constructTrainTestSplit(folds, i);
            trainTest_impl(preprocessed.t, preprocessed.r, preprocessed.s, maxSize, trainTestSplit.r, trainTestSplit.s, new File(directory+"/train_"+(i+1)+".arff"), new File(directory+"/test_"+(i+1)+".arff"), mode);
            System.out.println("Fold "+(i+1)+" done!\n");
        }
    }

    private Pair<int[],int[]> constructTrainTestSplit(List<int[]> folds, int foldIndex){
        if (folds.size() == 1){//no cross-validation
            return new Pair<int[],int[]>(folds.get(0), new int[0]);
        } else {
            List<Integer> train = new ArrayList<Integer>();
            int[] test = folds.get(foldIndex);
            for (int i = 0; i < folds.size(); i++){
                if (i != foldIndex){
                    for (int exampleIndex : folds.get(i)){
                        train.add(exampleIndex);
                    }
                }
            }
            return new Pair<int[],int[]>(VectorUtils.toIntegerArray(train), test);
        }
    }
    
    private List<int[]> constructStratifiedFolds(List<String> classifications, int foldsCount, int seed) {
        MultiList<String,Integer> classes = new MultiList<String,Integer>();
        for (int i = 0; i < classifications.size(); i++){
            String classification = classifications.get(i);
            classes.put(classification, i);
        }
        Random random = new Random(seed);
        for (List<Integer> list : classes.values()){
            Collections.shuffle(list, random);
        }
        List<int[]> folds = new ArrayList<int[]>();
        for (int i = 0; i < foldsCount; i++){
            List<Integer> fold = new ArrayList<Integer>();
            for (Map.Entry<String,List<Integer>> entry : classes.entrySet()){
                List<Integer> c = entry.getValue();
                if (i < foldsCount-1){
                    for (int j = i*c.size()/foldsCount; j < (i+1)*c.size()/foldsCount; j++){
                        fold.add(c.get(j));
                    }
                } else {
                    for (int j = i*c.size()/foldsCount; j < c.size(); j++){
                        fold.add(c.get(j));
                    }
                }
            }
            folds.add(VectorUtils.toIntegerArray(fold));
        }
        return folds;
    }
    
    private Triple<List<Set<PredicateDefinition>>,List<PredicateDefinition>,Dataset> readDataset(String template, Reader datasetReader) throws IOException {
        List<Clause> clauses = new ArrayList<Clause>();
        List<String> classifications = new ArrayList<String>();
        readExamples(datasetReader, clauses, classifications);
        return preprocessDataset(clauses, classifications, PredicateDefinition.parseDefinition(template));
    }
    
    //[[preprocessed templates], [preprocessed global constants], dataset]
    private Triple<List<Set<PredicateDefinition>>,List<PredicateDefinition>,Dataset> preprocessDataset(List<Clause> clauses, List<String> classifications, Set<PredicateDefinition> literalDefinitions){
        List<PredicateDefinition> globalConstants = new ArrayList<PredicateDefinition>();
        for (PredicateDefinition def : literalDefinitions){
            if (def.isGlobalConstant()){
                globalConstants.add(def);
            }
        }
        Pair<Set<PredicateDefinition>,Dataset> pair = preprocess_impl(literalDefinitions, globalConstants, clauses, classifications);
        clauses = null;
        List<PredicateDefinition> outputOnlyLiteralDefinitions = new ArrayList<PredicateDefinition>();
        Set<PredicateDefinition> remainingLiteralDefinitions = new LinkedHashSet<PredicateDefinition>();
        for (PredicateDefinition def : pair.r){
            if (def.isOutputOnly()){
                outputOnlyLiteralDefinitions.add(def);
            } else {
                remainingLiteralDefinitions.add(def);
            }
        }
        List<Set<PredicateDefinition>> definitionsList = new ArrayList<Set<PredicateDefinition>>();
        for (PredicateDefinition output : outputOnlyLiteralDefinitions){
            definitionsList.add(Sugar.setFromCollections(connectedComponent(output, remainingLiteralDefinitions), Sugar.set(output)));
        }
        return new Triple<List<Set<PredicateDefinition>>,List<PredicateDefinition>,Dataset>(definitionsList, globalConstants, pair.s);
    }
    
    private void readExamples(Reader exampleReader, List<Clause> clauses, List<String> classifications) throws IOException {
        int index = 0;
        BufferedReader br = new BufferedReader(exampleReader);
        String line = null;
        index = 0;
        while ((line = br.readLine()) != null){
            line = line.trim();
            if (line.length() > 1 && (line.indexOf(" ") != -1 || line.indexOf("\"") != -1 || line.indexOf("\'") != -1)){
                String classification = null;
                String clausePart = null;
                if (line.charAt(0) == '"'){
                    int indexOfSecondQuote = line.indexOf("\"", 1);
                    classification = line.substring(1, indexOfSecondQuote);
                    clausePart = line.substring(indexOfSecondQuote+1);
                } else if (line.charAt(0) == '\''){
                    int indexOfSecondQuote = line.indexOf("'", 1);
                    classification = "'"+line.substring(1, indexOfSecondQuote)+"'";
                    clausePart = line.substring(indexOfSecondQuote+1);
                } else {
                    classification = "'"+line.substring(0, line.indexOf(" "))+"'";
                    clausePart = line.substring(line.indexOf(" ")+1);
                }
                classifications.add(classification);
                clauses.add(Clause.parse(clausePart));
                index++;
            }
        }
    }

    private void addGlobalConstants(Example example, int exampleIndex, Table<Integer,String> t, List<PredicateDefinition> globalConstants){
        for (PredicateDefinition def : globalConstants){
            IntegerSet domain = example.getLiteralDomain(def.predicate());
            //otherwise it can't be considered a global-constant-feature
            if (domain.size() == 1){
                int literalId = domain.values()[0];
                Literal literal = example.integerToLiteral(literalId);
                for (int i = 0; i < def.modes().length; i++){
                    if (def.modes()[i] == PredicateDefinition.GLOBAL_CONSTANT){
                        if (literal.arity() == 1){
                            t.add(exampleIndex, def.stringPredicate(), literal.get(i).toString());
                            //System.out.println("adding global constant: "+exampleIndex+" -- "+def.stringPredicate()+" "+literal.get(i).toString());
                        } else {
                            System.out.println("Warning: "+def+" cannot be used as global constant because its arity is not equal to one!!!!");
                        }
                    }
                }
            } else {
                if (domain.size() > 1){
                    System.out.println("Warning: "+def+" cannot be used as global constant because there are more than one literals of the kind "+def+" in the example being processed or there is none!!!!");
                } else {
                    System.out.println("Warning: "+def+" cannot be used as global constant because there are no literals of the kind "+def+" in the example being processed or there is none!!!!");
                }
            }
        }
    }

    private Set<PredicateDefinition> processDefinitions(Set<PredicateDefinition> definitions){
        Set<PredicateDefinition> retVal = new HashSet<PredicateDefinition>();
        for (PredicateDefinition def : definitions){
            PredicateDefinition clone = def.cloneDefinition();
            int[] modes = def.modes();
            for (int i = 0; i < def.arity(); i++){
                if (modes[i] == PredicateDefinition.IDENTIFIER || modes[i] == PredicateDefinition.CLASS){
                    clone.setMode(PredicateDefinition.IGNORE, i);
                }
            }
            retVal.add(clone);
        }
        return retVal;
    }

    private Pair<Set<PredicateDefinition>,Dataset> preprocess_impl(Set<PredicateDefinition> template, List<PredicateDefinition> globalConstants, List<Clause> examples, List<String> classifications){
        Set<PredicateDefinition> modifiedTemplate = new LinkedHashSet<PredicateDefinition>();
        MemoryBasedDataset mbe = new MemoryBasedDataset();
        int index = 0;
        template = processDefinitions(template);
        ListIterator<Clause> li = examples.listIterator();
        while (li.hasNext()){
            Clause reachableExample = Preprocessor.reachableLiterals(template, globalConstants, li.next());
            li.set(null);
            Set<Literal> literals = new LinkedHashSet<Literal>();
            Set<String> constants = new LinkedHashSet<String>();
            for (PredicateDefinition def : template){
                Collection<Literal> eLiterals = reachableExample.getLiteralsByPredicate(def.stringPredicate());
                PredicateDefinition cloned = def.cloneDefinition();
                for (int i = 0; i < def.modes().length; i++){
                    if (def.modes()[i] == PredicateDefinition.CONSTANT){
                        cloned.setMode(PredicateDefinition.OUTPUT, i);
                        for (Literal constLit : eLiterals){
                            StringBuilder sb = new StringBuilder();
                            try {
                                sb.append(constLit.get(i).toString());
                            } catch (ArrayIndexOutOfBoundsException e){
                                System.err.println("Problem with "+reachableExample);
                            }
                            constants.add(constLit.get(i).toString());
                            sb.append("(+");
                            sb.append(def.stringType(i));
                            sb.append("[1]), ");
                            PredicateDefinition constantDefinition = Sugar.chooseOne(PredicateDefinition.parseDefinition(sb.toString()));
                            if (constantDefinition != null){
                                boolean prune = false;
                                if (!prune){
                                    constantDefinition.setConstant(true);
                                    modifiedTemplate.add(constantDefinition);
                                }
                            }
                            Literal newLiteral = new Literal(constLit.get(i).toString(), 1);
                            newLiteral.set(constLit.get(i), 0);
                            literals.add(newLiteral);
                        }
                    }
                }
                modifiedTemplate.add(cloned);
            }
            literals.addAll(reachableExample.literals());

            mbe.addExample(new Example(new Clause(literals)), classifications.get(index));
            index++;
        }
        return new Pair<Set<PredicateDefinition>,Dataset>(modifiedTemplate, mbe);
    }

    private static boolean consistsOfMegaExamples(Set<PredicateDefinition> defs){
        for (PredicateDefinition def : defs){
            int[] modes = def.modes();
            for (int i = 0; i < def.arity(); i++){
                if (modes[i] == PredicateDefinition.IDENTIFIER){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 
     * @param minFreq minimum frequency (relative - i.e. number from 0...1)
     */
    public void setMinFrequency(double minFreq){
        this.minFrequency = minFreq;
    }

    /**
     * By default, RelF constructs all features which are non-redundant for at least one class
     * (a feature F1 is redundant for class_1 if there is another feature F2 which covers a superset 
     * of examples from class_1 coverd by F1 and subset of the remaining examples covered by F1).
     * It is possible to let RelF construct only features non-redundant for certain classes - this can
     * be set using this method.
     * @param coveredClasses the classes for which RelF should construct non-redundant features
     */
    public void setRelfCoveredClasses(Set<String> coveredClasses){
        this.coveredClasses = coveredClasses;
    }

    private Set<PredicateDefinition> connectedComponent(PredicateDefinition output, Set<PredicateDefinition> other){
        Set<PredicateDefinition> closed = new HashSet<PredicateDefinition>();
        MultiMap<String,PredicateDefinition> inputToPredicate = new MultiMap<String,PredicateDefinition>();
        for (PredicateDefinition def : other){
            String inputType = def.stringType(def.input());
            inputToPredicate.put(inputType, def);
        }
        Stack<PredicateDefinition> open = new Stack<PredicateDefinition>();
        open.push(output);
        while (open.size() > 0){
            PredicateDefinition def = open.pop();
            closed.add(def);
            for (int i = 0; i < def.modes().length; i++){
                if (def.modes()[i] == PredicateDefinition.OUTPUT){
                    for (PredicateDefinition d : inputToPredicate.get(def.stringType(i))){
                        if (!closed.contains(d)){
                            open.push(d);
                        }
                    }
                }
            }
        }
        return closed;
    }

    /**
     * 
     * @param folds
     */
    public void setFoldsCount(int folds){
        this.foldsCount = folds;
    }

    /**
     * @param constructFeaturesOnlyFromFirstExample the constructFeaturesOnlyFromFirstExample to set
     */
    public void setConstructFeaturesOnlyFromFirstExample(boolean constructFeaturesOnlyFromFirstExample) {
        this.constructFeaturesOnlyFromFirstExample = constructFeaturesOnlyFromFirstExample;
    }

    /**
     * @param maxDegree the maxDegree to set
     */
    public void setMaxDegree(int maxDegree) {
        this.maxDegree = maxDegree;
    }

    /**
     * @param sampleSize the sampleSize to set
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * @param numSamples the numSamples to set
     */
    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    /**
     * @param useSampling the useSampling to set
     */
    public void setUseSampling(boolean useSampling) {
        this.useSampling = useSampling;
    }
    
    /**
     * 
     * @param feature
     */
    public void setNormalizationFactor(Block feature){
        this.normalizationFactor = feature;
    }
}

