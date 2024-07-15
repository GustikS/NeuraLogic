package cz.cvut.fel.ida.logic.features.generation;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.features.treeliker.TreeLikerSettings;

import java.util.ArrayList;
import java.util.List;

public class FeatureGenerationMain {

    private final String[] data = {
            "_vert(1), _vert(2), _vert(3), _vert(4), bond(1, 2), bond(2, 1), bond(2, 3), bond(3, 2), bond(2, 4), bond(4, 2), red(1), blue(2), red(3), red(4)",
            "_vert(1), _vert(2), _vert(3), bond(1, 2), bond(2, 1), bond(1, 3), bond(3, 1), bond(2, 3), bond(3, 2), red(1), blue(2), green(3)"
    };

    private List<Clause> parseDataset() {
        List<Clause> clauses = new ArrayList<>();

        for (String cl : data) {
            clauses.add(Clause.parse(cl));
        }

        return clauses;
    }

    public void debugAll() {
        List<Clause> dataset = parseDataset();

        FeaturesTable featureData = FeatureGenerator.generateFeatures(dataset, 1);
        System.out.println(featureData);

        FeatureGenerationSettings.COUNT_GROUNDINGS = false;
        featureData = FeatureGenerator.generateFeatures(dataset, 2);
        System.out.println(featureData);
    }

    public void debugParts() {
        List<Clause> dataset = parseDataset();

        GraphTemplateBuilder builder = new GraphTemplateBuilder();
        builder.processExamples(dataset);

        FeatureGenerationSettings.TEMPLATE_DEPTH = 2;

        String template = builder.inferTemplate(1);
        System.out.println(template);

        template = builder.inferTemplate(); // using TEMPLATE_DEPTH
        System.out.println(template);

        PreprocessedInput input = new PreprocessedInput(template, dataset);

        System.out.println(input);

        FeatureGenerationSettings.TEMPLATE_DEPTH = 1;
    }


    public static void main(String[] args) {
        FeatureGenerationMain obj = new FeatureGenerationMain();
        obj.debugParts();
        obj.debugAll();

        TreeLikerSettings.VERBOSITY = 1; // Change the default VERBOSITY = 0

        obj.debugParts();
        obj.debugAll();
    }
}
