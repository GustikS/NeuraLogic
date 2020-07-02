package cz.cvut.fel.ida.learning.results.metrics;

import cz.cvut.fel.ida.algebra.values.Value;
import cz.cvut.fel.ida.learning.results.Result;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HITS {
    private static final Logger LOG = Logger.getLogger(HITS.class.getName());
    private final Random random;

    boolean hitsReifyPredicate;
    Settings.HitsCorruption corruption;
    Settings.HitsPreservation hitsPreservation;
    Settings.HitsClashes hitsClashes;

    LinkedHashSet<String> validSamples;
    LinkedHashSet<String> corruptedSamples;

    Map<String, String[]> samples2terms;
    int maxArity = -1;
    int keepFixedIndex = -1;

    /**
     * Store precalculated corrupted tuples for each valid query for speedup - this might require a lot of memory!
     */
    boolean storeCorruptions;
    Map<String, List<String>[]> storedCorruptions;

    /**
     * Only used to speedup the ONE_SAME mode
     */
    Map<String, LinkedHashSet<String>>[] sameTermSamples;
    /**
     * The corruptedSamples set transformed into terms (String[])
     */
    List<String[]> corruptedTerms;

    Map<String[], String> terms2sample;

    public HITS(List<Result> results, Settings settings) {
        random = settings.random;
        hitsReifyPredicate = settings.hitsReifyPredicate;
        corruption = settings.hitsCorruption;
        hitsPreservation = settings.hitsPreservation;
        hitsClashes = settings.hitsClashes;
        storeCorruptions = settings.storeHitsCorruptions;
        if (storeCorruptions) {
            storedCorruptions = new HashMap<>();
        }
        switch (hitsPreservation) {
            case FIRST_STAYS:
                keepFixedIndex = 0;
                break;
            case MIDDLE_STAYS:
                keepFixedIndex = 1;
                break;
            case NONE:
                keepFixedIndex = -1;
                break;
        }
        samples2terms = samples2terms(results);
        Pair<LinkedHashSet<String>, LinkedHashSet<String>> validAndCorrupted = validAndCorrupted(results);
        validSamples = validAndCorrupted.r;
        corruptedSamples = validAndCorrupted.s;

        if (corruption == Settings.HitsCorruption.ONE_SAME || (corruption == Settings.HitsCorruption.ALL_DIFF && keepFixedIndex >= 0)) {
            sameTermSamples = getDatabase(corruptedSamples);
        } else if (corruption == Settings.HitsCorruption.ONE_DIFF) {
            corruptedTerms = corruptedSamples.stream().map(samples2terms::get).collect(Collectors.toList());
            terms2sample = samples2terms.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        }
    }

    /**
     * This can be potentially used to pass the queries and metrics from train to validation set and merge them
     *
     * @param other
     */
    public void mergeWith(HITS other) {
        samples2terms.putAll(other.samples2terms);
        validSamples.addAll(other.validSamples);
        corruptedSamples.addAll(other.corruptedSamples);

        if (sameTermSamples != null) {
            for (int i = 0; i < sameTermSamples.length; i++) {
                sameTermSamples[i].putAll(other.sameTermSamples[i]);
            }
        } else if (corruptedTerms != null) {
            corruptedTerms.addAll(other.corruptedTerms);
        }
    }

    /**
     * Helper class encapsulating different stats that can be reported
     */
    public static class Stats {
        double MRR;
        double AVGrank;
        int[] HITSindices = {1, 3, 5, 10};
        double[] HITSresults = new double[HITSindices.length];

        private int counter = 0;
        private boolean finalized = false;

        public Stats() {
        }

        public Stats(int[] HITSindices) {
            this.HITSindices = HITSindices;
            this.HITSresults = new double[HITSindices.length];
        }

        public void consume(double rank) {
            AVGrank += rank;
            MRR += 1 / rank;
            for (int i = 0; i < HITSindices.length; i++) {
                if (rank <= HITSindices[i])
                    HITSresults[i]++;
            }
            counter++;
        }

        public Stats finish() {
            if (finalized) return this;
            AVGrank /= counter;
            MRR /= counter;
            for (int i = 0; i < HITSindices.length; i++) {
                HITSresults[i] /= counter;
            }
            finalized = true;
            return this;
        }

        @Override
        public String toString() {
            if (!finalized) return "stats are not finalized yet!";
            StringBuilder sb = new StringBuilder();
            sb.append("MRR=").append(Settings.shortNumberFormat.format(MRR));
            sb.append(", MeanRank=").append(Settings.shortNumberFormat.format(AVGrank));
            sb.append(", HITS at ").append(Arrays.toString(HITSindices)).append("=(");
            for (double hitSresult : HITSresults) {
                sb.append(Settings.shortNumberFormat.format(hitSresult)).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
            return sb.toString();
        }
    }


    /**
     * Returns aggregated stats from all the valid query literals
     *
     * @return
     */
    public Stats getStats(List<Result> results) {
        Map<String, Value> predictions = getPredictions(results);
        Stats stats = new Stats();
        for (String validSample : validSamples) {
            Value predictedValue = predictions.get(validSample);
            List<String>[] corruptions = getCorruptions(validSample);
            for (List<String> corruptionsAtIndex : corruptions) {
                if (corruptionsAtIndex == null) {
                    continue;   //preserved index
                }

                double rank = 1;
                if (!corruptionsAtIndex.isEmpty()) {
                    List<Value> corruptedValues = corruptionsAtIndex.stream().map(predictions::get).collect(Collectors.toList());
                    rank = getRank(predictedValue, corruptedValues);
                }
                stats.consume(rank);    //each corruption set is completely independent
            }
        }
        return stats.finish();
    }

    private Map<String, Value> getPredictions(List<Result> results) {
        return results.stream().collect(Collectors.toMap(result -> result.sampleId, Result::getOutput));
    }


    /**
     * Get Values from each corruption set respectively
     *
     * @param etalon
     * @return
     */
    private List<String>[] getCorruptions(String etalon) {

        if (storedCorruptions != null) {
            List<String>[] corruptions = storedCorruptions.get(etalon);
            if (corruptions != null) return corruptions;
        }

        switch (corruption) {
            case ONE_SAME:
                return corruptAtLeastOneSame(etalon);
            case ONE_DIFF:
                return corruptExactlyOneDifferent(etalon);
            case ALL_DIFF:
                return corruptAnything(etalon);
            default:
                throw new RuntimeException("Uknown HITS corruption definition");
        }
    }

    /**
     * Returns rank of the predictedValue
     *
     * @param predictedValue
     * @param corruptionsAtIndex
     */
    public double getRank(Value predictedValue, List<Value> corruptionsAtIndex) {
        int same = 0;
        double rank = 1;
        if (corruptionsAtIndex != null)
            for (Value corrupt : corruptionsAtIndex) {
                if (corrupt.greaterThan(predictedValue)) {
                    rank += 1; //the bad case
                } else if (predictedValue.greaterThan(corrupt)) {
                    //the good case
                } else {
                    same++; //the edge case
                }
            }
        switch (hitsClashes) {
            case AVG:
                return rank + same / 2.0;   //take the middle rank from the possible clashes
            case NONE:
                return rank;    //i.e. the cheating version from previous works
            case RANDOM:
                return rank + random.nextInt(same + 1); //just bs
        }
        return rank;
    }

    /**
     * Transforms the LRNN samples (results associated with String IDs) into simple terms = String[]
     *
     * @param results
     * @return
     */
    private Map<String, String[]> samples2terms(List<Result> results) {
        Map<String, String[]> samples2terms = new HashMap<>();
        for (Result result : results) {
            int separator = -1;
            if (hitsReifyPredicate) {
                if (hitsPreservation == Settings.HitsPreservation.MIDDLE_STAYS) {
                    LOG.warning("Including predicate, but also probably assuming it is reified in the middle of the terms.");
                }
                separator = result.sampleId.indexOf(":") + 1;
            } else
                separator = result.sampleId.indexOf("(") + 1;

            String[] terms = result.sampleId.substring(separator > 0 ? separator : 0)
                    .replace("(", ",")
                    .replace(")", "")
                    .replace(" ", "")
                    .split(",");

            if (hitsPreservation == Settings.HitsPreservation.MIDDLE_STAYS) { //3-tuple, predicate in the middle stays
                if (terms.length != 3) {
                    LOG.severe("trying to calculate predicate-in-the-middle HITs from literal of length: " + terms.length);
                }
            }

            samples2terms.put(result.sampleId, terms);
            if (terms.length > maxArity) {
                maxArity = terms.length;
            }
        }
        return samples2terms;
    }

    /**
     * Given list of results (output values and targets), returns sets of positive and negative samples represented as String[]
     *
     * @param results
     * @return
     */
    public Pair<LinkedHashSet<String>, LinkedHashSet<String>> validAndCorrupted(List<Result> results) {
        LinkedHashSet<String> corrupted = new LinkedHashSet<>();
        LinkedHashSet<String> valid = new LinkedHashSet<>();
        for (Result result : results) {
            if (result.getTarget().greaterThan(Value.ZERO)) {
                valid.add(result.sampleId);
            } else {
                corrupted.add(result.sampleId);
            }
        }
        return new Pair<>(valid, corrupted);
    }


    /**
     * Returns sets of corruptions with exactly 1 element different for each respective position
     *
     * @param etalon
     * @return
     */
    private List<String>[] corruptExactlyOneDifferent(String etalon) {
        String[] etalonTerms = samples2terms.get(etalon);
        List<String>[] corruptions = new ArrayList[etalonTerms.length];

        List<String>[] leftSeries = precalculateIntersections(etalonTerms, true);
        List<String>[] rightSeries = precalculateIntersections(etalonTerms, false);

        for (int i = 0; i < etalonTerms.length; i++) {
            if (i == keepFixedIndex) continue;

            if (i - 1 >= 0)
                corruptions[i] = new ArrayList(leftSeries[i - 1]);

            if (i + 1 < etalonTerms.length)
                if (corruptions[i] != null)
                    corruptions[i].retainAll(rightSeries[i + 1]);  //intersection
                else
                    corruptions[i] = new ArrayList(rightSeries[i + 1]);   //just take the right one
        }
        if (storeCorruptions) {
            storedCorruptions.put(etalon, corruptions);
        }
        return corruptions;
    }

    /**
     * Helper structure to save recalculations of the corruption sets for a single etalon but at different positions
     *
     * @param etalon
     * @param left
     * @return
     */
    private List<String>[] precalculateIntersections(String[] etalon, boolean left) {

        IntStream intStream;
        if (left) {
            intStream = IntStream.rangeClosed(0, etalon.length - 1);
        } else {
            intStream = IntStream.rangeClosed(0, etalon.length - 1).map(i -> etalon.length - 1 - i);
        }

        List<String[]>[] series = new ArrayList[etalon.length];
        final List<String[]>[] previous = new ArrayList[1];
        previous[0] = corruptedTerms;
        intStream.forEach(i -> {
            if (previous[0] != null)
                series[i] = previous[0].stream()
                        .filter(terms -> etalon[i].equals(terms[i]))
                        .collect(Collectors.toList());
            previous[0] = series[i];
        });
        List<String>[] corruptions = new ArrayList[etalon.length];
        for (int i = 0; i < corruptions.length; i++) {
            corruptions[i] = series[i].stream().map(terms2sample::get).collect(Collectors.toList());
        }
        return corruptions;
    }

    /**
     * Returns sets of corruption with at least 1 equivalent element at each respective position
     *
     * @param etalon
     * @return
     */
    private List<String>[] corruptAtLeastOneSame(String etalon) {
        String[] etalonTerms = samples2terms.get(etalon);
        List<String>[] corruptions = new ArrayList[etalonTerms.length];

        for (int i = 0; i < etalonTerms.length; i++) {
            if (i == keepFixedIndex) continue;

            LinkedHashSet<String> corruptsWithSameTerm = sameTermSamples[i].get(etalonTerms[i]);
            if (keepFixedIndex >= 0) {
                LinkedHashSet<String> fixedSubset = sameTermSamples[keepFixedIndex].get(etalonTerms[keepFixedIndex]);
                if (fixedSubset == null) {
                    corruptions[i] = new ArrayList<>();
                    continue;
                }
                LinkedHashSet<String> corruptedSubSelection = new LinkedHashSet<>(fixedSubset);
                corruptedSubSelection.retainAll(corruptsWithSameTerm);
                corruptions[i] = new ArrayList<>(corruptedSubSelection);
            } else {
                corruptions[i] = corruptsWithSameTerm != null ? new ArrayList<>(corruptsWithSameTerm) : new ArrayList<>();
            }
        }
        if (storeCorruptions) {
            storedCorruptions.put(etalon, corruptions);
        }
        return corruptions;
    }

    /**
     * Helper structure for fast retrieval of the "same element and the same index" tuples
     *
     * @param corrupted
     * @return
     */
    private Map<String, LinkedHashSet<String>>[] getDatabase(LinkedHashSet<String> corrupted) {
        Map<String, LinkedHashSet<String>>[] corruptedDatabase = new HashMap[maxArity];
        for (int i = 0; i < corruptedDatabase.length; i++) {
            corruptedDatabase[i] = new HashMap<>();
        }
        for (String sample : corrupted) {
            String[] terms = samples2terms.get(sample);
            for (int i = 0; i < terms.length; i++) {
                LinkedHashSet<String> otherSamplesWithTermAtIndex = corruptedDatabase[i].get(terms[i]);
                if (otherSamplesWithTermAtIndex != null) {
                    otherSamplesWithTermAtIndex.add(sample);
                } else {
                    otherSamplesWithTermAtIndex = new LinkedHashSet<>();
                    otherSamplesWithTermAtIndex.add(sample);
                    corruptedDatabase[i].put(terms[i], otherSamplesWithTermAtIndex);
                }
            }
        }
        return corruptedDatabase;
    }

    private List<String>[] corruptAnything(String etalon) {
        if (storedCorruptions != null && !storedCorruptions.isEmpty()) {
            return storedCorruptions.get("");
        }
        if (keepFixedIndex >= 0) {
            String[] etalonTerms = samples2terms.get(etalon);
            LinkedHashSet<String> sameFixedCorruptions = sameTermSamples[keepFixedIndex].get(etalonTerms[keepFixedIndex]);
            if (sameFixedCorruptions != null)
                return new List[]{new ArrayList(sameFixedCorruptions)};
            else
                return new List[]{new ArrayList()};
        }
        ArrayList[] corruptions = {new ArrayList(corruptedSamples)};
        if (storeCorruptions) {
            storedCorruptions.put("", corruptions);
        }
        return corruptions;
    }

}
