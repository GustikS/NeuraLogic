package grounding.bottomUp;

import com.sun.istack.internal.NotNull;
import constructs.example.LiftedExample;
import constructs.example.ValuedFact;
import constructs.template.Template;
import constructs.template.components.WeightedRule;
import grounding.GroundTemplate;
import grounding.Grounder;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Term;
import settings.Settings;
import utils.generic.Pair;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Gusta on 06.10.2016.
 * <p>
 * TODO create a streaming version for single examples?
 * todo add the version with continual rule creation instead of final substitutions
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
        Map<HornClause, List<WeightedRule>> ruleMap = template.hornClauses;
        Map<Literal, ValuedFact> groundFacts = null;
        if (example.rules.isEmpty() && ruleMap != null) {  //no new rules here, only facts, reuse the rules mapping from template
            groundFacts = mapToLogic(rulesAndFacts(example, template).s);
        } else {
            Pair<Map<HornClause, List<WeightedRule>>, Map<Literal, ValuedFact>> rulesAndFacts = mapToLogic(rulesAndFacts(example, template));
            ruleMap = rulesAndFacts.r;
            groundFacts = rulesAndFacts.s;
            template.hornClauses = ruleMap;
        }

        LinkedHashMap<Literal, LinkedHashMap<WeightedRule, LinkedHashSet<WeightedRule>>> groundRules = new LinkedHashMap<>();

        Set<Literal> facts = groundFacts.keySet();
        // add already inferred facts as a hack to speedup the Herbrand model calculation
        if (settings.inferTemplateFacts) {
            Set<Literal> templateAllFacts = template.getAllFacts();
            if (templateAllFacts != null)
                facts.addAll(templateAllFacts);
        }

        herbrandModel.inferModel(ruleMap.keySet(), facts);

        for (Map.Entry<HornClause, List<WeightedRule>> ruleEntry : ruleMap.entrySet()) {
            Pair<Term[], List<Term[]>> groundingSubstitutions = herbrandModel.groundingSubstitutions(ruleEntry.getKey());
            for (WeightedRule weightedRule : ruleEntry.getValue()) {
                List<WeightedRule> groundings = herbrandModel.groundRules(weightedRule, groundingSubstitutions);
                for (WeightedRule grounding : groundings) {
                    Map<WeightedRule, LinkedHashSet<WeightedRule>> rules2groundings =
                            groundRules.computeIfAbsent(grounding.head.getLiteral(), k -> new LinkedHashMap<>());
                    LinkedHashSet<WeightedRule> ruleGroundings =
                            rules2groundings.computeIfAbsent(weightedRule, k -> new LinkedHashSet<>());
                    ruleGroundings.add(grounding);
                }
            }
        }
        GroundTemplate groundTemplate = new GroundTemplate(groundRules, groundFacts);
        herbrandModel.clear();
        return groundTemplate;
    }

    /**
     * Performs incremental grounding over the memory GroundTemplate.
     * Returns GroundTemplate that carries diff w.r.t. ground rules and facts but union w.r.t. neurons.
     * Updates the memory with the union of ground rules and facts.
     *
     * @param example
     * @param template
     * @param memory
     * @return
     */
    @Override
    public GroundTemplate groundRulesAndFacts(LiftedExample example, Template template, GroundTemplate memory) {
        if (memory == null) {
            memory = new GroundTemplate();
        }
        herbrandModel.populateHerbrand(memory.groundFacts.keySet());
        GroundTemplate bigger = groundRulesAndFacts(example, template);
        GroundTemplate diff = bigger.diffAgainst(memory);
        memory.groundRules = bigger.groundRules;
        memory.groundFacts = bigger.groundFacts;
        return diff;
    }
}