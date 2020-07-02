package cz.cvut.fel.ida.learning.results.metrics;

import cz.cvut.fel.ida.algebra.values.ScalarValue;
import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HITSTest {

    private Map<String, Value> getValid() {
        Map<String, Value> data = new HashMap<>();
        data.put("s1:predict(a,b,c)", new ScalarValue(0.1));    //1
        data.put("s2:predict(a,c,d)", new ScalarValue(0.3));    //2
        return data;
    }

    private Map<String, Value> getCorrupt() {
        Map<String, Value> data = new HashMap<>();
        data.put("s3:predict(b,b,c)", new ScalarValue(0.2));    //1-
        data.put("s4:predict(d,a,c)", new ScalarValue(0.7));    //
        data.put("s5:predict(a,b,b)", new ScalarValue(-0.6));   //1+
        data.put("s5:predict(a,a,b)", new ScalarValue(-0.5));   //
        data.put("s5:predict(a,a,d)", new ScalarValue(0.3));    //2+-
        return data;
    }

    @NotNull
    public List<Result> getResultList(Settings settings) {
        Result.Factory factory = new Result.Factory(settings);
        List<Result> resultList = getValid().entrySet().stream().map(entry -> factory.create(entry.getKey(), 0, new ScalarValue(1.0), entry.getValue())).collect(Collectors.toList());
        List<Result> corrupts = getCorrupt().entrySet().stream().map(entry -> factory.create(entry.getKey(), 0, new ScalarValue(0.0), entry.getValue())).collect(Collectors.toList());
        resultList.addAll(corrupts);
        return resultList;
    }

    @TestAnnotations.Fast
    public void corruptAtLeastOneSame() {
        Settings settings = new Settings();
        settings.hitsCorruption = Settings.HitsCorruption.ONE_SAME;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 1.8333,0.0001);
    }

    @TestAnnotations.Fast
    public void corruptExactlyOneDifferent() {
        Settings settings = new Settings();
        settings.storeHitsCorruptions = false;
        settings.hitsCorruption = Settings.HitsCorruption.ONE_DIFF;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 1.25);
    }

    @TestAnnotations.Fast
    public void corruptAny() {
        Settings settings = new Settings();
        settings.hitsCorruption = Settings.HitsCorruption.ALL_DIFF;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 3.25);
    }

    @TestAnnotations.Fast
    public void reifyOneDiff() {
        Settings settings = new Settings();
        settings.hitsCorruption = Settings.HitsCorruption.ONE_DIFF;
        settings.hitsReifyPredicate = true;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 1.1875);
    }

    @TestAnnotations.Fast
    public void reifyOneSame() {
        Settings settings = new Settings();
        settings.hitsCorruption = Settings.HitsCorruption.ONE_SAME;
        settings.hitsReifyPredicate = true;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 2.1875);
    }

    @TestAnnotations.Fast
    public void middleStaysAllDiff() {
        Settings settings = new Settings();
        settings.hitsCorruption = Settings.HitsCorruption.ALL_DIFF;
        settings.hitsPreservation = Settings.HitsPreservation.MIDDLE_STAYS;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 1.5);
    }

    @TestAnnotations.Fast
    public void middleStaysOneSame() {
        Settings settings = new Settings();
        settings.hitsCorruption = Settings.HitsCorruption.ONE_SAME;
        settings.hitsPreservation = Settings.HitsPreservation.MIDDLE_STAYS;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 1.25);  //same as 2 same = 1 different
    }

    @TestAnnotations.Fast
    public void firstStaysOneDiff() {
        Settings settings = new Settings();
        settings.hitsCorruption = Settings.HitsCorruption.ONE_DIFF;
        settings.hitsPreservation = Settings.HitsPreservation.FIRST_STAYS;
        List<Result> resultList = getResultList(settings);
        HITS hits = new HITS(resultList, settings);
        HITS.Stats stats = hits.getStats(resultList);
        System.out.println(stats);
        assertEquals(stats.AVGrank, 1.125);
    }
}