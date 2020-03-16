package cz.cvut.fel.ida.logic.constructs.template.transforming;

import cz.cvut.fel.ida.algebra.utils.metadata.WeightMetadata;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.constructs.template.types.ParsedTemplate;
import cz.cvut.fel.ida.logic.constructs.template.metadata.PredicateMetadata;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.setup.Settings;

import java.util.logging.Logger;

/**
 * Created by gusta on 6.3.18.
 */
public class MetadataProcessor {
    private static final Logger LOG = Logger.getLogger(MetadataProcessor.class.getName());

    public MetadataProcessor(Settings settings) {
    }

    /**
     * Assign and process all the metadata.
     *
     * @param template
     * @return
     */
    public ParsedTemplate processMetadata(ParsedTemplate template) {
        for (Pair<WeightedPredicate, PredicateMetadata> predicatesMetadatum : template.predicatesMetadata) {
            if (predicatesMetadatum.r.metadata == null) {
                predicatesMetadatum.r.metadata = predicatesMetadatum.s;
            } else {
                predicatesMetadatum.r.metadata.addAll(predicatesMetadatum.s);
            }

            predicatesMetadatum.r.metadata.applyTo(predicatesMetadatum.r);
        }

        for (Pair<Weight, WeightMetadata> weightsMetadatum : template.weightsMetadata) {
            if (weightsMetadatum.r.metadata == null) {
                weightsMetadatum.r.metadata = weightsMetadatum.s;
            } else {
                weightsMetadatum.r.metadata.addAll(weightsMetadatum.s);
            }
            weightsMetadatum.s.applyTo(weightsMetadatum.r);
        }

        for (WeightedRule rule : template.rules) {
            if (rule.getMetadata() != null)
                rule.getMetadata().applyTo(rule);
        }

        if (template.templateMetadata != null)
        template.templateMetadata.applyTo(template);

        return template;
    }
}