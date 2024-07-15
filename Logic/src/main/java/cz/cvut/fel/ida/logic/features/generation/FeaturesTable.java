package cz.cvut.fel.ida.logic.features.generation;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.features.treeliker.Table;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class FeaturesTable {


    public final List<Clause> features;
    public final int[][] table;


    public static FeaturesTable fromTreeLikerTable(Table<Integer, String> propTable) {
        return new FeaturesTable(propTable);
    }

    private FeaturesTable(Table<Integer, String> propTable) {
        List<Pair<String, Clause>> sortedFeatures = sortFeatures(propTable.filteredAttributes()); // [original string, clausal form]
        int noExamples = propTable.getAttributeVector(sortedFeatures.get(0).r).size();

        features = new ArrayList<>(sortedFeatures.size());
        table = new int[noExamples][sortedFeatures.size()];

        int featureIndex = 0;
        for (Pair<String, Clause> featureData : sortedFeatures) {
            String originalFeature = featureData.r;
            Clause clausalFeature = featureData.s;

            features.add(clausalFeature);

            Map<Integer, String> values = propTable.getAttributeVector(originalFeature);
            for (Map.Entry<Integer, String> pair : values.entrySet()) {

                if (FeatureGenerationSettings.COUNT_GROUNDINGS) {
                    table[pair.getKey()][featureIndex] = Integer.parseInt(pair.getValue());
                } else {
                    table[pair.getKey()][featureIndex] = pair.getValue().equals("+") ? 1 : 0;
                }

            }

            featureIndex++;
        }

    }

    private List<Pair<String, Clause>> sortFeatures(Set<String> features) {
        // TODO sorting does not handle isomorphic features

        List<Pair<String, Clause>> sortedFeatures = new ArrayList<>(features.size());

        for (String feature : features) {
            Clause clause = Clause.parse(feature);
            String sortedClause = clause.literals().stream().map(Literal::toString).sorted().collect(Collectors.joining(", "));
            sortedFeatures.add(new Pair<>(feature, Clause.parse(sortedClause)));
        }

        sortedFeatures.sort(Comparator.comparing(o -> o.s.toString()));
        return sortedFeatures;
    }

    @Override
    public String toString() {
        return "FeaturesTable{\n" +
                printFeatures() + '\n' +
                Arrays.deepToString(table) + "\n}";
    }

    private String printFeatures() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Clause feature : features) {
            sb.append(feature);
            sb.append("; ");
        }
        sb.append(']');
        return sb.toString();
    }

}
