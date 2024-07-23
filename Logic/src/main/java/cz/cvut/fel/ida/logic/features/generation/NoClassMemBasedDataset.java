package cz.cvut.fel.ida.logic.features.generation;

import cz.cvut.fel.ida.logic.features.treeliker.Dataset;
import cz.cvut.fel.ida.logic.features.treeliker.Example;
import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.*;

public class NoClassMemBasedDataset implements Dataset {

    public static final String DUMMY_CLASS_LABEL = "DUMMY";

    private int index = 0;

    private final List<Example> examples;

    public NoClassMemBasedDataset() {
        this.examples = new ArrayList<>();
    }

    public NoClassMemBasedDataset(List<Example> examples) {
        this.examples = examples;
    }

    public void addExample(Example example) {
        examples.add(example);
    }

    @Override
    public int countExamples() {
        return this.examples.size();
    }

    @Override
    public int countExamples(String cl) {
        if (cl.equals(DUMMY_CLASS_LABEL)) {
            return countExamples();
        }

        return 0;
    }

    @Override
    public boolean hasNextExample() {
        return index < examples.size();
    }

    @Override
    public Example nextExample() {
        return examples.get(index++);
    }

    @Override
    public void reset() {
        this.index = 0;
    }

    @Override
    public int currentIndex() {
        return index - 1;
    }

    @Override
    public String classificationOfCurrentExample() {
        return DUMMY_CLASS_LABEL;
    }

    @Override
    public Collection<String> classes() {
        return Sugar.list(DUMMY_CLASS_LABEL);
    }

    @Override
    public Dataset shallowCopy() {
        return new NoClassMemBasedDataset(examples);
    }

    @Override
    public Dataset get(int[] indices) {
        NoClassMemBasedDataset retVal = new NoClassMemBasedDataset();

        for (int i : indices) {
            retVal.addExample(examples.get(i));
        }

        return retVal;
    }

    @Override
    public List<Dataset> stratifiedCrossValidation(int folds, int seed) {
        throw new UnsupportedOperationException("TUPLES dataset has no need for this.");
//        return List.of();
    }

    @Override
    public Dataset subsample(int numFromEachClass, int seed) {
        throw new UnsupportedOperationException("TUPLES dataset has no need for this.");
//        return null;
    }

    @Override
    public Dataset[] split(int count) {
        MirroredExamples[] exs = new MirroredExamples[count];
        for (int i = 0; i < count; i++) {
            exs[i] = new MirroredExamples();
        }
        for (int i = 0; i < this.countExamples(); i++) {
            exs[i % count].addExample(this.examples.get(i), i);
        }
        return exs;
    }

    @Override
    public void sortAsc(String targetVariable) {
        throw new UnsupportedOperationException("TUPLES dataset has no need for this.");
    }

    @Override
    public void sortDesc(String targetVariable) {
        throw new UnsupportedOperationException("TUPLES dataset has no need for this.");
    }


    private static class MirroredExamples implements Dataset {

        private int currentIndex = 0;

        private List<Example> examples = new ArrayList<Example>();

        private List<Integer> indices = new ArrayList<Integer>();

        private Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();


        public MirroredExamples() {
        }

        public void addExample(Example example, int index) {
            examples.add(example);
            indices.add(index);
            indexMap.put(index, examples.size() - 1);
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
            return indices.get(currentIndex - 1);
        }

        @Override
        public String classificationOfCurrentExample() {
            return NoClassMemBasedDataset.DUMMY_CLASS_LABEL;
        }

        @Override
        public Dataset shallowCopy() {
            MirroredExamples me = new MirroredExamples();
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
            for (int i = 0; i < splitCount; i++) {
                exs[i] = new MirroredExamples();
            }
            for (int i = 0; i < this.countExamples(); i++) {
                exs[i % splitCount].addExample(this.examples.get(i), this.indices.get(i));
            }
            return exs;
        }

        @Override
        public int countExamples(String cl) {
            return cl.equals(DUMMY_CLASS_LABEL) ? countExamples() : 0;
        }

        @Override
        public Collection<String> classes() {
            return Sugar.list(NoClassMemBasedDataset.DUMMY_CLASS_LABEL);
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

