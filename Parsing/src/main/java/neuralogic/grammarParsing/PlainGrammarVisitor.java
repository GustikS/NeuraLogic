package neuralogic.grammarParsing;

import com.sun.istack.internal.NotNull;
import constructs.example.WeightedFact;
import constructs.factories.VariableFactory;
import constructs.template.Atom;
import constructs.template.BodyAtom;
import constructs.template.WeightedPredicate;
import constructs.template.WeightedRule;
import constructs.template.metadata.RuleMetadata;
import ida.ilp.logic.Literal;
import ida.ilp.logic.Term;
import ida.utils.tuples.Pair;
import networks.evaluation.values.ScalarValue;
import networks.evaluation.values.Value;
import networks.evaluation.values.VectorValue;
import networks.structure.Weight;
import parsers.neuralogic.NeuralogicBaseVisitor;
import parsers.neuralogic.NeuralogicParser;
import parsing.Builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Contains logic of construction of logical elements while walking/visiting parse trees of logic programs (templates or samples)
 */
public class PlainGrammarVisitor extends GrammarVisitor{
    private static final Logger LOG = Logger.getLogger(PlainGrammarVisitor.class.getName());

    public PlainGrammarVisitor(Builder builder) {
        super(builder);
    }




    public class RuleLineVisitor extends NeuralogicBaseVisitor<WeightedRule> {
        // Variable factory gets initialized here - variable scope is per rule
        VariableFactory variableFactory = new VariableFactory();

        @Override
        public WeightedRule visitLrnn_rule(@NotNull NeuralogicParser.Lrnn_ruleContext ctx) {

            WeightedRule rule = new WeightedRule();

            rule.originalString = ctx.getText();

            AtomVisitor headVisitor = new AtomVisitor();
            headVisitor.variableFactory = this.variableFactory;
            BodyAtom headAtom = ctx.atom().accept(headVisitor);

            rule.head = new Atom();
            rule.head.weightedPredicate = headAtom.weightedPredicate;
            rule.head.literal = headAtom.literal;

            rule.weight = headAtom.weight;

            ConjunctionVisitor bodyVisitor = new ConjunctionVisitor();
            bodyVisitor.variableFactory = this.variableFactory;
            rule.body = ctx.conjunction().accept(bodyVisitor);

            rule.offset = ctx.offset() != null ? ctx.offset().accept(new WeightVisitor()) : null;
            rule.metadata = ctx.metadata_list() != null ? new RuleMetadata(ctx.metadata_list().accept(new MetadataListVisitor())) : null;

            return rule;
        }
    }


    public class ConjunctionVisitor extends NeuralogicBaseVisitor<List<BodyAtom>> {
        VariableFactory variableFactory;

        @Override
        public List<BodyAtom> visitConjunction(@NotNull NeuralogicParser.ConjunctionContext ctx) {
            List<BodyAtom> body = new ArrayList<>();
            AtomVisitor atomVisitor = new AtomVisitor();
            atomVisitor.variableFactory = this.variableFactory;
            List<BodyAtom> atomList = ctx.atom()
                    .stream()
                    .map(atom -> atom.accept(atomVisitor))
                    .collect(Collectors.toList());
            return body;
        }
    }

    public class AtomVisitor extends NeuralogicBaseVisitor<BodyAtom> {
        VariableFactory variableFactory;

        @Override
        public BodyAtom visitAtom(@NotNull NeuralogicParser.AtomContext ctx) {
            BodyAtom bodyAtom = new BodyAtom();
            bodyAtom.originalString = ctx.getText();

            WeightedPredicate predicate = ctx.predicate().accept(new PredicateVisitor());
            bodyAtom.weightedPredicate = predicate;
            TermVisitor termVisitor = new TermVisitor();
            termVisitor.variableFactory = this.variableFactory;
            List<Term> terms = ctx.term_list().term()
                    .stream()
                    .map(term -> term.accept(termVisitor))
                    .collect(Collectors.toList());

            bodyAtom.isNegated = ctx.negation() != null;    //TODO derive proper activation function for negation here already and remove the flag
            bodyAtom.literal = new Literal(predicate.predicate.name, bodyAtom.isNegated, terms);
            bodyAtom.weight = ctx.weight().accept(new WeightVisitor());

            return bodyAtom;
        }
    }

    public class FactVisitor extends NeuralogicBaseVisitor<WeightedFact> {
        VariableFactory variableFactory = new VariableFactory();

        @Override
        public WeightedFact visitFact(@NotNull NeuralogicParser.FactContext ctx) {
            WeightedFact fact = new WeightedFact();
            fact.originalString = ctx.getText();

            WeightedPredicate predicate = ctx.atom().predicate().accept(new PredicateVisitor());
            fact.weightedPredicate = predicate;

            TermVisitor termVisitor = new TermVisitor();
            termVisitor.variableFactory = this.variableFactory;
            List<Term> terms = ctx.atom().term_list().term()
                    .stream()
                    .map(term -> term.accept(termVisitor))
                    .collect(Collectors.toList());

            fact.literal = new Literal(predicate.predicate.name, ctx.atom().negation() != null, terms);
            fact.value = ctx.atom().weight().accept(new WeightVisitor());
            return fact;
        }
    }

    private class PredicateVisitor extends NeuralogicBaseVisitor<WeightedPredicate> {

        @Override
        public WeightedPredicate visitPredicate(@NotNull NeuralogicParser.PredicateContext ctx) {
            int arity = -1;
            try {
                arity = Integer.parseInt(ctx.INT().getText());
            } catch (Exception ex) {
                LOG.severe("Cannot parse arity of a predicate from " + ctx.getText());
            }
            WeightedPredicate predicate = builder.predicateFactory.construct(ctx.ATOMIC_NAME().getText(), arity, ctx.SPECIAL() != null);

            return predicate;
        }
    }

    private class WeightVisitor extends NeuralogicBaseVisitor<Weight> {
        @Override
        public Weight visitWeight(@NotNull NeuralogicParser.WeightContext ctx) {
            String name = ctx.ATOMIC_NAME().getText();
            Value value = null;
            boolean fixed = false;
            if (ctx.fixed_value() != null) {
                fixed = true;
                value = parseValue(ctx.fixed_value().value());
            } else if (ctx.value() != null) {
                fixed = false;
                value = parseValue(ctx.value());
            } else {
                LOG.severe("Weight is neither fixed nor learnable");
            }
            Weight weight = builder.weightFactory.construct(name, value, fixed);

            return weight;
        }

        public Value parseValue(NeuralogicParser.ValueContext ctx) {
            Value value = null;
            if (ctx.number() != null) {
                value = new ScalarValue(Float.parseFloat(ctx.number().getText()));
            } else if (ctx.vector() != null) {
                List<Double> vector = ctx.vector().number().stream().map(num -> Double.parseDouble(num.getText())).collect(Collectors.toList());
                value = new VectorValue(vector);
            } else {
                LOG.severe("Value is neither number nor vector: Could not parse numeric value from " + ctx.getText());
            }
            if (value == null) {
                LOG.severe("Error during parsing numeric value from " + ctx.getText());
            }
            return value;
        }
    }

    private class TermVisitor extends NeuralogicBaseVisitor<Term> {
        public VariableFactory variableFactory;

        @Override
        public Term visitTerm(@NotNull NeuralogicParser.TermContext ctx) {
            Term term;

            if (ctx.constant() != null) {
                term = builder.constantFactory.construct(ctx.getText());
            } else if (ctx.variable() != null)
                term = variableFactory.construct(ctx.getText());  //TODO check if correct over several rules with the same Variable names (no sharing wanted)
            else {
                LOG.severe("Term is neither Constant nor Variable");
                term = null;
            }
            return term;
        }
    }

    public class PredicateMetadataVisitor extends NeuralogicBaseVisitor<Pair<WeightedPredicate, Map<String, Object>>> {
        @Override
        public Pair<WeightedPredicate, Map<String, Object>> visitPredicate_metadata(@NotNull NeuralogicParser.Predicate_metadataContext ctx) {
            int arity = -1;
            try {
                arity = Integer.parseInt(ctx.predicate().INT().getText());
            } catch (Exception ex) {
                LOG.severe("Cannot parse arity of a predicate from " + ctx.getText());
            }
            WeightedPredicate predicate = builder.predicateFactory.construct(ctx.predicate().ATOMIC_NAME().getText(), arity, ctx.predicate().SPECIAL() != null);
            Map<String, Object> metadata = ctx.metadata_list().accept(new MetadataListVisitor());
            return new Pair(predicate, metadata);
        }
    }

    public class WeightMetadataVisitor extends NeuralogicBaseVisitor<Pair<Weight, Map<String, Object>>> {
        @Override
        public Pair<Weight, Map<String, Object>> visitWeight_metadata(@NotNull NeuralogicParser.Weight_metadataContext ctx) {
            Weight weight = builder.weightFactory.construct(ctx.ATOMIC_NAME().getText());
            Map<String, Object> metadata = ctx.metadata_list().accept(new MetadataListVisitor());
            return new Pair(weight, metadata);
        }
    }

    public class PredicateOffsetVisitor extends NeuralogicBaseVisitor<Pair<WeightedPredicate, Weight>> {
        @Override
        public Pair<WeightedPredicate, Weight> visitPredicate_offset(@NotNull NeuralogicParser.Predicate_offsetContext ctx) {
            int arity = -1;
            try {
                arity = Integer.parseInt(ctx.predicate().INT().getText());
            } catch (Exception ex) {
                LOG.severe("Cannot parse arity of a predicate from " + ctx.getText());
            }
            WeightedPredicate predicate = builder.predicateFactory.construct(ctx.predicate().ATOMIC_NAME().getText(), arity, ctx.predicate().SPECIAL() != null);
            Weight offset = ctx.weight().accept(new WeightVisitor());
            predicate.offset = offset;
            return new Pair(predicate, offset);
        }
    }

    private class MetadataListVisitor extends NeuralogicBaseVisitor<Map<String, Object>> {
        @Override
        public Map<String, Object> visitMetadata_list(@NotNull NeuralogicParser.Metadata_listContext ctx) {
            Map<String, Object> metadata = new LinkedHashMap<>();
            for (NeuralogicParser.Metadata_valContext paramVal : ctx.metadata_val()) {
                String parameter = paramVal.ATOMIC_NAME(0).getText();
                String valueText = paramVal.ATOMIC_NAME(1).getText();
                Object value;
                if (paramVal.DOLLAR() != null) {
                    value = builder.weightFactory.construct(valueText);
                } else {
                    value = builder.constantFactory.construct(valueText);
                }
                metadata.put(parameter, value);
            }
            return metadata;
        }
    }
}
