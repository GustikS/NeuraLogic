package constructs.template;

import constructs.template.metadata.RuleMetadata;
import ida.ilp.logic.Clause;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import networks.evaluation.functions.Activation;
import networks.structure.Weight;

import java.util.Collection;
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

    Weight weight;
    public Weight offset;

    public Atom head;
    public List<BodyAtom> body;

    Activation aggregationFcn;
    Activation activationFcn;

    public RuleMetadata metadata;
    public String originalString;

    HornClause toHornClause(){
        List<Literal> collected = body.stream().map(bodyLit -> bodyLit.atom.literal).collect(Collectors.toList());
        return new HornClause(head.literal,new Clause(collected));
    }

    public Collection<? extends Atom> getAllAtoms() {
        List<Atom> collected = body.stream().map(bodyLit -> bodyLit.atom).collect(Collectors.toList());
        collected.add(head);
        return collected;
    }
}
