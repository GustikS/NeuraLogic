package cz.cvut.fel.ida.learning.results;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetailedClassificationResultsTest {
    private static final Logger LOG = Logger.getLogger(DetailedClassificationResultsTest.class.getName());

    static List<Result> resultList = setup();

    private static List<Result> setup() {
        List<Result> resultList = new ArrayList<>();

        Result.Factory factory = new Result.Factory(new Settings());
        resultList.add(factory.create("s1", 1, new ScalarValue(1), new ScalarValue(0.8)));
        resultList.add(factory.create("s2", 2, new ScalarValue(0), new ScalarValue(0.6)));
        resultList.add(factory.create("s3", 3, new ScalarValue(1), new ScalarValue(0.7)));
        resultList.add(factory.create("s4", 4, new ScalarValue(1), new ScalarValue(0.4)));
        resultList.add(factory.create("s5", 5, new ScalarValue(0), new ScalarValue(0.9)));
        return resultList;
    }

    @TestAnnotations.Fast
    public void testSorting() {
        int[] sortedIndices = IntStream.range(0, resultList.size())
                .boxed().sorted(Comparator.comparing(resultList::get))
                .mapToInt(ele -> ele).toArray();
        LOG.fine(Arrays.toString(sortedIndices));

        Collections.sort(resultList);
        String res = Arrays.toString(resultList.toArray());
        LOG.fine(res);
        assertEquals("[s4 -> 0.4 : 1, s2 -> 0.6 : 0, s3 -> 0.7 : 1, s1 -> 0.8 : 1, s5 -> 0.9 : 0]", res);
    }

    @TestAnnotations.Fast
    void calculateAUC() {
        DetailedClassificationResults detailedClassificationResults = new DetailedClassificationResults(resultList, new Settings());
        double auc = detailedClassificationResults.calculateAUCsmaller(resultList);
        LOG.fine("empirical auc=" + auc);
        assertEquals(0.33333333, auc, 0.00000001);
    }

    @TestAnnotations.Fast
    void interpolatedAUC() {
        DetailedClassificationResults detailedClassificationResults = new DetailedClassificationResults(resultList, new Settings());
        detailedClassificationResults.setFullAUC(resultList);
        LOG.fine("interpolated AUC=" + detailedClassificationResults.AUCroc);
        assertEquals(0.416666666666, detailedClassificationResults.AUCroc, 0.00000001);
    }
}