package cz.cvut.fel.ida.logic.features.generation;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.features.treeliker.*;
import cz.cvut.fel.ida.logic.features.treeliker.aggregables.GroundingCountingAggregablesBuilder;
import cz.cvut.fel.ida.logic.features.treeliker.aggregables.VoidAggregablesBuilder;
import cz.cvut.fel.ida.utils.math.collections.IntegerSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wrapper class for calls to TreeLiker requesting construction of symbolic features.
 */
public class FeatureGenerator {

    static {
        TreeLikerSettings.VERBOSITY = 0; // By default, suppress all TreeLiker logs
    }

    /**
     * Construct TreeLiker features for the dataset using TuplesSettings.TEMPLATE_DEPTH parameter.
     *
     * @param dataset symbolic examples
     * @return constructed features along with a table of their evaluations
     */
    public static FeaturesTable generateFeatures(List<Clause> dataset) {
        return generateFeatures(dataset, FeatureGenerationSettings.TEMPLATE_DEPTH);
    }

    /**
     * Construct TreeLiker features for the dataset.
     *
     * @param dataset      symbolic examples
     * @param featureDepth max depth of the constructed features
     * @return constructed features along with a table of their evaluations
     */
    public static FeaturesTable generateFeatures(List<Clause> dataset, int featureDepth) {
        FeatureGenerator featureGenerator = new FeatureGenerator();
        return featureGenerator.generateFeatures_impl(dataset, featureDepth);
    }

    /**
     * Construct TreeLiker features for the dataset using the provided template.
     *
     * @param dataset  symbolic examples
     * @param template language bias
     * @return constructed features along with a table of their evaluations
     */
    public static FeaturesTable generateFeatures(List<Clause> dataset, String template) {
        FeatureGenerator featureGenerator = new FeatureGenerator();
        return featureGenerator.generateFeatures_impl(dataset, template);
    }



    private FeaturesTable generateFeatures_impl(List<Clause> dataset, int featureDepth) {
        PreprocessedInput preprocessed = preprocess(dataset, featureDepth);
        return constructAndEvaluateFeatures(preprocessed);
    }

    private FeaturesTable generateFeatures_impl(List<Clause> dataset, String template) {
        PreprocessedInput preprocessed = new PreprocessedInput(template, dataset);
        return constructAndEvaluateFeatures(preprocessed);
    }

    private PreprocessedInput preprocess(List<Clause> dataset, int depth) {
        GraphTemplateBuilder templateBuilder = new GraphTemplateBuilder(dataset);
        String stringTemplate = templateBuilder.inferTemplate(depth);
        return new PreprocessedInput(stringTemplate, dataset);
    }

    private FeaturesTable constructAndEvaluateFeatures(PreprocessedInput preprocessed) {
        Set<Block> features = constructFeatures(preprocessed);
        return evaluateFeatures(features, preprocessed.getGlobalConstants(), preprocessed.getDataset());
    }

    private Set<Block> constructFeatures(PreprocessedInput preprocessed) {
        Set<Block> features = new HashSet<>();

        for (Set<PredicateDefinition> def : preprocessed.getTemplates()) {
            HiFi hifi = new HiFi(preprocessed.getDataset());
//            hifi.setMaxSize(Integer.MAX_VALUE);
            if (FeatureGenerationSettings.COUNT_GROUNDINGS) {
                hifi.setAggregablesBuilder(GroundingCountingAggregablesBuilder.construct());
                hifi.setPostProcessingAggregablesBuilder(GroundingCountingAggregablesBuilder.construct());
            }

            features.addAll(hifi.constructFeatures(def));
        }

        return features;
    }

    private FeaturesTable evaluateFeatures(Set<Block> features, List<PredicateDefinition> globalConstants, Dataset dataset) {
        Table<Integer, String> table = new Table<>();

        List<Block> nonConstantAttributes = new ArrayList<Block>();
        List<Block> globalConstantAttributes = new ArrayList<Block>();
        for (Block attribute : features) {
            if (attribute.definition().isGlobalConstant()) {
                globalConstantAttributes.add(attribute);
            } else {
                nonConstantAttributes.add(attribute);
            }
        }

        Dataset copyOfDataset = dataset.shallowCopy();
        copyOfDataset.reset();

        while (copyOfDataset.hasNextExample()) {
            Example example = copyOfDataset.nextExample();
            table.addClassification(copyOfDataset.currentIndex(), copyOfDataset.classificationOfCurrentExample());
            addGlobalConstants(example, copyOfDataset.currentIndex(), table, globalConstants);
        }

        HiFi hifi = new HiFi(dataset);
        if (FeatureGenerationSettings.COUNT_GROUNDINGS) {
            hifi.setAggregablesBuilder(VoidAggregablesBuilder.construct());
            hifi.setPostProcessingAggregablesBuilder(GroundingCountingAggregablesBuilder.construct());
        }

        table.addAll(hifi.constructTable(nonConstantAttributes));
        return FeaturesTable.fromTreeLikerTable(table);
    }

    private void addGlobalConstants(Example example, int exampleIndex, Table<Integer, String> t, List<PredicateDefinition> globalConstants) {
        for (PredicateDefinition def : globalConstants) {
            IntegerSet domain = example.getLiteralDomain(def.predicate());
            //otherwise it can't be considered a global-constant-feature
            if (domain.size() == 1) {
                int literalId = domain.values()[0];
                Literal literal = example.integerToLiteral(literalId);
                for (int i = 0; i < def.modes().length; i++) {
                    if (def.modes()[i] == PredicateDefinition.GLOBAL_CONSTANT) {
                        if (literal.arity() == 1) {
                            t.add(exampleIndex, def.stringPredicate(), literal.get(i).toString());
                            //System.out.println("adding global constant: "+exampleIndex+" -- "+def.stringPredicate()+" "+literal.get(i).toString());
                        } else {
                            System.out.println("Warning: " + def + " cannot be used as global constant because its arity is not equal to one!!!!");
                        }
                    }
                }
            } else {
                if (domain.size() > 1) {
                    System.out.println("Warning: " + def + " cannot be used as global constant because there are more than one literals of the kind " + def + " in the example being processed or there is none!!!!");
                } else {
                    System.out.println("Warning: " + def + " cannot be used as global constant because there are no literals of the kind " + def + " in the example being processed or there is none!!!!");
                }
            }
        }
    }

}
