/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.ida.logic.features.treeliker;

import cz.cvut.fel.ida.logic.Constant;
import cz.cvut.fel.ida.logic.LogicUtils;
import cz.cvut.fel.ida.utils.math.Combinatorics;
import cz.cvut.fel.ida.utils.math.StringUtils;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.Counters;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;
import cz.cvut.fel.ida.utils.math.collections.NaturalNumbersList;

import java.util.*;
/**
 * Implementation of class Dataset which stores all examples in the main memory.
 * @author Ondra
 */
public class MemoryBasedDataset implements Dataset {

    private int index;
    
    private List<Example> examples = new ArrayList<Example>();
    
    private List<String> classifications = new ArrayList<String>();

    private Set<String> classes = new LinkedHashSet<String>();
    
    private Counters<String> classCounts = new Counters<String>();
    
    /**
     * Creates a new instance of class MemoryBasedDataset
     */
    public MemoryBasedDataset(){};
    
    /**
     * Adds new example with given classification to the end of this dataset.
     * 
     * @param example the exampe to be added
     * @param classification the class-label of the example
     */
    public void addExample(Example example, String classification){
        examples.add(example);
        classifications.add(classification);
        classes.add(classification);
        classCounts.increment(classification);
    }
    
    @Override
    public boolean hasNextExample() {
        return index < examples.size();
    }

    @Override
    public Example nextExample() {
        return examples.get(index++);
    }

    /**
     * 
     * @param index positiion if the example in the dataset
     * @return example at position <em>index</em>
     */
    public Example getExample(int index){
        return this.examples.get(index);
    }
    
    @Override
    public void reset() {
        index = 0;
    }

    @Override
    public int currentIndex() {
        return index-1;
    }

    @Override
    public String classificationOfCurrentExample(){
        return this.classifications.get(index-1);
    }
    
    @Override
    public int countExamples() {
        return examples.size();
    }
    
    @Override
    public Dataset shallowCopy(){
        MemoryBasedDataset mbe = new MemoryBasedDataset();
        mbe.classifications = this.classifications;
        mbe.examples = this.examples;
        mbe.index = 0;
        return mbe;
    }

    @Override
    public List<Dataset> stratifiedCrossValidation(int foldsCount, int seed) {
        MultiList<String,Integer> classes = new MultiList<String,Integer>();
        for (int i = 0; i < examples.size(); i++){
            String classification = classifications.get(i);
            classes.put(classification, i);
        }
        Random random = new Random(seed);
        for (List<Integer> list : classes.values()){
            Collections.shuffle(list, random);
        }
        List<Dataset> folds = new ArrayList<Dataset>();
        for (int i = 0; i < foldsCount; i++){
            MemoryBasedDataset fold = new MemoryBasedDataset();
            for (Map.Entry<String,List<Integer>> entry : classes.entrySet()){
                List<Integer> c = entry.getValue();
                if (i < foldsCount-1){
                    for (int j = i*c.size()/foldsCount; j < (i+1)*c.size()/foldsCount; j++){
                        fold.addExample(examples.get(c.get(j)), entry.getKey());
                    }
                } else {
                    for (int j = i*c.size()/foldsCount; j < c.size(); j++){
                        fold.addExample(examples.get(c.get(j)), entry.getKey());
                    }
                }
            }
            folds.add(fold);
        }
        return folds;
    }

    @Override
    public synchronized Dataset[] split(int splitCount) {
        MirroredExamples[] exs = new MirroredExamples[splitCount];
        for (int i = 0; i < splitCount; i++){
            exs[i] = new MirroredExamples();
        }
        for (int i = 0; i < this.countExamples(); i++){
            exs[i % splitCount].addExample(this.examples.get(i), this.classifications.get(i), i);
        }
        return exs;
    }

    @Override
    public int countExamples(String cl) {
        return Sugar.countOccurences(cl, classifications);
    }

    @Override
    public Collection<String> classes() {
        return classes;
    }

    @Override
    public Dataset get(int[] indices) {
        MemoryBasedDataset mbe = new MemoryBasedDataset();
        for (int i : indices){
            mbe.addExample(examples.get(i), classifications.get(i));
        }
        return mbe;
    }

    @Override
    public void sortDesc(String targetVariable){
        sortAsc(targetVariable);
        Collections.reverse(this.examples);
        Collections.reverse(this.classifications);
    }
    
    @Override
    public void sortAsc(String targetVariable) {
        Map<Integer,Comparable> scores = new HashMap<Integer,Comparable>();
        List<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < this.examples.size(); i++){
            Example e = this.examples.get(i);
            Constant value = Constant.construct(Example.termToString(e.getTerm(e.getLiteralDomain(PredicateDefinition.predicateToInteger(targetVariable, 1)).values()[0], 0)));
            String strVal = LogicUtils.unquote(value).name();
            if (StringUtils.isNumeric(strVal)){
                scores.put(i, Double.parseDouble(strVal));
            } else {
                scores.put(i, strVal);
            }
            indices.add(i);
        }
        List<Integer> sorted = Sugar.sortAsc(indices, scores);
        List<String> newClassifications = new ArrayList<String>();
        List<Example> newExamples = new ArrayList<Example>();
        for (int i = 0; i < indices.size(); i++){
            newClassifications.add(this.classifications.get(sorted.get(i)));
            newExamples.add(this.examples.get(sorted.get(i)));
        }
        this.examples = newExamples;
        this.classifications = newClassifications;
    }

    @Override
    public Dataset subsample(int numFromEachClass, int seed) {
        Random random = new Random(seed);
        MemoryBasedDataset mbd = new MemoryBasedDataset();
        MultiMap<String,Integer> selectedIndices = new MultiMap<String,Integer>();
        for (String classLabel : this.classes()){
            Set<Integer> selectedIndicesForClass = Combinatorics.randomCombination(
                    new NaturalNumbersList(0, classCounts.get(classLabel)), Math.min(classCounts.get(classLabel), numFromEachClass), random).toSet();
            selectedIndices.putAll(classLabel, selectedIndicesForClass);
        }
        Counters<String> indexes = new Counters<String>();
        for (int i = 0; i < this.examples.size(); i++){
            String classLabel = classifications.get(i);
            int indexInClass = indexes.incrementPost(classLabel);
            if (selectedIndices.get(classLabel).contains(indexInClass)){
                mbd.addExample(examples.get(i), classLabel);
            }
        }
        return mbd;
    }
    
    private static class MirroredExamples implements Dataset {

        private int currentIndex = 0;

        private List<Example> examples = new ArrayList<Example>();

        private List<Integer> indices = new ArrayList<Integer>();

        private List<String> classifications = new ArrayList<String>();

        private Map<Integer,Integer> indexMap = new HashMap<Integer,Integer>();

        private Set<String> classes = new LinkedHashSet<String>();

        public MirroredExamples(){
        }

        public void addExample(Example example, String classification, int index){
            examples.add(example);
            classifications.add(classification);
            classes.add(classification);
            indices.add(index);
            indexMap.put(index, examples.size()-1);
        }

        @Override
        public int countExamples() {
            return this.indices.size();
        }

        @Override
        public boolean hasNextExample() {
            return currentIndex < examples.size();
        }

        @Override
        public Example nextExample() {
            return examples.get(currentIndex++);
        }

        @Override
        public void reset() {
            currentIndex = 0;
        }

        @Override
        public int currentIndex() {
            return indices.get(currentIndex-1);
        }

        @Override
        public String classificationOfCurrentExample() {
            return this.classifications.get(currentIndex-1);
        }

        @Override
        public Dataset shallowCopy() {
            MirroredExamples me = new MirroredExamples();
            me.classifications = this.classifications;
            me.examples = this.examples;
            me.indexMap = this.indexMap;
            me.indices = this.indices;
            return me;
        }

        @Override
        public List<Dataset> stratifiedCrossValidation(int folds, int seed) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Dataset[] split(int splitCount) {
            MirroredExamples[] exs = new MirroredExamples[splitCount];
            for (int i = 0; i < splitCount; i++){
                exs[i] = new MirroredExamples();
            }
            for (int i = 0; i < this.countExamples(); i++){
                exs[i % splitCount].addExample(this.examples.get(i), this.classifications.get(i), this.indices.get(i));
            }
            return exs;
        }

        @Override
        public int countExamples(String cl) {
            return Sugar.countOccurences(cl, classifications);
        }

        @Override
        public Collection<String> classes() {
            return classes;
        }

        @Override
        public Dataset get(int[] indices) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void sortAsc(String targetVariable) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void sortDesc(String targetVariable) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Dataset subsample(int numFromEachClass, int seed) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
