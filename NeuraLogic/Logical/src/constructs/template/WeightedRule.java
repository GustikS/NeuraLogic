package constructs.template;

import constructs.template.metadata.RuleMetadata;
import ida.ilp.logic.Clause;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import networks.evaluation.functions.Activation;
import networks.structure.Weight;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 04.10.2016.
 */
public class WeightedRule {

    /**
     * changable by structure learning?
     */
    boolean isEditable = false;

    public Weight weight;
    public Weight offset;

    public HeadAtom head;
    public List<BodyAtom> body;

    Activation aggregationFcn;
    Activation activationFcn;

    public RuleMetadata metadata;
    public String originalString;

    HornClause toHornClause(){
        List<Literal> collected = body.stream().map(bodyLit -> bodyLit.getLiteral()).collect(Collectors.toList());
        return new HornClause(head.getLiteral(),new Clause(collected));
    }
}