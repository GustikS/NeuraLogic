package constructs.template.components;

import constructs.template.metadata.RuleMetadata;
import ida.ilp.logic.Clause;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Term;
import networks.computation.evaluation.functions.Activation;
import networks.structure.components.weights.Weight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Gusta on 04.10.2016.
 *
 * todo create hash?
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

    public Activation aggregationFcn;
    public Activation activationFcn;

    public RuleMetadata metadata;
    public String originalString;

    /**
     * Apply {@link networks.computation.evaluation.functions.CrossProduct} activation on the inputs of the rule?
     * todo must process this from metadata of the rule
     */
    public boolean crossProduct;

    public WeightedRule() {

    }

    public WeightedRule(WeightedRule other) {
        this.weight = other.weight;
        this.head = other.head;
        this.body = other.body;
        this.offset = other.offset;
        this.aggregationFcn = other.aggregationFcn;
        this.activationFcn = other.activationFcn;
        this.metadata = other.metadata;
        this.originalString = other.originalString;
        this.isEditable = other.isEditable;
    }

    public HornClause toHornClause() {
        List<Literal> collected = body.stream().map(bodyLit -> bodyLit.getLiteral()).collect(Collectors.toList());
        return new HornClause(head.getLiteral(), new Clause(collected));
    }

    public WeightedRule ground(Term[] variables, Term[] terms) {
        WeightedRule ruleCopy = new WeightedRule(this);

        Map<Term, Term> var2term = new HashMap<Term, Term>();
        for (int i = 0; i < variables.length; i++) {
            var2term.put(variables[i], terms[i]);
        }

        ruleCopy.head = ruleCopy.head.ground(var2term);
        for (int i = 0; i < body.size(); i++) {
            body.set(i, body.get(i).ground(var2term));
        }

        return ruleCopy;
    }

    public String signatureString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head.getPredicate()).append(":-");
        for (BodyAtom bodyAtom : body) {
            sb.append(bodyAtom.getPredicate()).append(",");
        }
        return sb.toString();
    }

    public boolean hasWeightedBody() {
        for (BodyAtom bodyAtom : body) {
            if (bodyAtom.weight != null) {
                return false;
            }
        }
        return true;
    }
}