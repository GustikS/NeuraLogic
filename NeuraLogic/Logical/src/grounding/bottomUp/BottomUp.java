package grounding.bottomUp;

import constructs.example.LiftedExample;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.WeightedRule;
import grounding.GroundTemplate;
import grounding.Grounder;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.utils.tuples.Pair;
import org.jetbrains.annotations.NotNull;
import settings.Settings;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Gusta on 06.10.2016.
 *
 * TODO create a streaming version for single examples?
 * todo add version with continual rule creation instead of final substitutions
 */
public class BottomUp extends Grounder {
    private static final Logger LOG = Logger.getLogger(BottomUp.class.getName());

    HerbrandModel herbrandModel;

    public BottomUp(Settings settings) {
        super(settings);
        herbrandModel = new HerbrandModel();
    }

    @NotNull
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template) {
        Pair<Map<HornClause, WeightedRule>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(rulesAndFacts(example, template));
        Map<HornClause, WeightedRule> ruleMap = rulesAndFacts.r;
        Map<Literal, ValuedFact> groundFacts = rulesAndFacts.s;

        LinkedHashMap<Literal, LinkedHashMap<WeightedRule, List<WeightedRule>>> groundRules = new LinkedHashMap<>();

        Set<Literal> facts = groundFacts.keySet();
        // add already inferred facts as a hack to speedup the Herbrand model calculation
        if (settings.inferTemplateFacts)
            facts = template.getAllFacts();

        if (!storeGroundings) {
            herbrandModel = new HerbrandModel();
        }

        herbrandModel.inferModel(ruleMap.keySet(), facts);

        for (Map.Entry<HornClause, WeightedRule> ruleEntry : ruleMap.entrySet()) {
            List<WeightedRule> groundings = herbrandModel.groundRules(ruleEntry.getValue(), ruleEntry.getKey());
            for (WeightedRule grounding : groundings) {
                Map<WeightedRule, List<WeightedRule>> rules2groundings =
                        groundRules.computeIfAbsent(grounding.head.getLiteral(), k -> new LinkedHashMap<>());
                List<WeightedRule> ruleGroundings =
                        rules2groundings.computeIfAbsent(ruleEntry.getValue(), k -> new LinkedList<>());
                ruleGroundings.add(grounding);
            }
        }
        return new GroundTemplate(groundRules, groundFacts);
    }

}