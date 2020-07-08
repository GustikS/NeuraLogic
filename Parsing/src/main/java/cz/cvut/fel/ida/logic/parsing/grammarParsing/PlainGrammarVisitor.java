package cz.cvut.fel.ida.logic.parsing.grammarParsing;

import org.jetbrains.annotations.NotNull;
import cz.cvut.fel.ida.algebra.utils.metadata.Parameter;
import cz.cvut.fel.ida.algebra.utils.metadata.ParameterValue;
import cz.cvut.fel.ida.algebra.values.*;
import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.logic.Term;
import cz.cvut.fel.ida.logic.constructs.Conjunction;
import cz.cvut.fel.ida.logic.constructs.WeightedPredicate;
import cz.cvut.fel.ida.logic.constructs.building.LogicSourceBuilder;
import cz.cvut.fel.ida.logic.constructs.building.factories.VariableFactory;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.BodyAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.logic.constructs.template.components.WeightedRule;
import cz.cvut.fel.ida.logic.constructs.template.metadata.RuleMetadata;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicBaseVisitor;
import cz.cvut.fel.ida.logic.parsing.antlr.NeuralogicParser;
import cz.cvut.fel.ida.utils.generic.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Contains logic of construction of logical structures while walking/visiting parse trees of logic programs (templates or samples).
 * <p>
 * Rules and atom are constructed independently. Predicates, constants and variables are shared via factory methods.
 */
public class PlainGrammarVisitor extends GrammarVisitor {
    private static final Logger LOG = Logger.getLogger(PlainGrammarVisitor.class.getName());

    public PlainGrammarVisitor(LogicSourceBuilder builder) {
        super(builder);
    }


    public class RuleLineVisitor extends NeuralogicBaseVisitor<WeightedRule> {
        VariableFactory variableFactory;

        @Override
        public WeightedRule visitLrnnRule(@NotNull NeuralogicParser.LrnnRuleContext ctx) {
            // Variable factory gets initialized here - variable scope is per rule
            variableFactory = new VariableFactory();

            WeightedRule rule = new WeightedRule();

            rule.setOriginalString(ctx.getText());

            AtomVisitor headVisitor = new AtomVisitor();
            headVisitor.variableFactory = this.variableFactory;
            // we hack it through BodyAtom here to pass the weight of the rule
            BodyAtom headAtom = ctx.atom().accept(headVisitor);
            Weight weight = headAtom.getConjunctWeight();//rule weight

            if (weight == null) {
                rule.setWeight(Weight.unitWeight);
            } else {
                rule.setWeight(weight);
            }

            rule.setHead(new HeadAtom(headAtom));

            AtomConjunctionVisitor bodyVisitor = new AtomConjunctionVisitor();
            bodyVisitor.variableFactory = this.variableFactory;
            rule.setBody(ctx.conjunction().accept(bodyVisitor));

            Weight offset = null;
            if (ctx.offset() != null) {
                offset = ctx.offset().accept(new WeightVisitor());
            } else {
                if (builder.settings.ruleAdaptiveOffset) {
                    if (builder.settings.defaultRuleOffsetsLearnable)
                        offset = builder.weightFactory.construct(new ScalarValue(-rule.getBody().size()), false, true); //todo next the offset dimensions should be inferred here (ScalarValue is not enough if learnable)
                    else
                        offset = builder.weightFactory.construct(new ScalarValue(-rule.getBody().size()), true, true);
                }
            }
            rule.setOffset(offset);
            rule.setMetadata(ctx.metadataList() != null ? new RuleMetadata(builder.settings, ctx.metadataList().accept(new MetadataListVisitor())) : null); //rule metadata are set directly here, as they cannot appear at arbitrary place as opposed to the other metadata, which are processed in a later stage

            return rule;
        }
    }


    public class AtomConjunctionVisitor extends NeuralogicBaseVisitor<List<BodyAtom>> {
        VariableFactory variableFactory;

        @Override
        public List<BodyAtom> visitConjunction(@NotNull NeuralogicParser.ConjunctionContext ctx) {
            AtomVisitor atomVisitor = new AtomVisitor();
            atomVisitor.variableFactory = this.variableFactory;
            List<BodyAtom> atomList = ctx.atom()
                    .stream()
                    .map(atom -> atom.accept(atomVisitor))
                    .collect(Collectors.toList());
            return atomList;
        }
    }

    public class AtomVisitor extends NeuralogicBaseVisitor<BodyAtom> {
        VariableFactory variableFactory;

        @Override
        public BodyAtom visitAtom(@NotNull NeuralogicParser.AtomContext ctx) {

            TermVisitor termVisitor = new TermVisitor();
            termVisitor.variableFactory = this.variableFactory;
            List<Term> terms;
            if (ctx.termList() != null) {
                terms = ctx.termList().term()
                        .stream()
                        .map(term -> term.accept(termVisitor))
                        .collect(Collectors.toList());
            } else {
                terms = new ArrayList<>(0);
            }

            WeightedPredicate predicate = ctx.predicate().accept(new PredicateVisitor(terms.size()));
            Weight weight = ctx.weight() != null ? ctx.weight().accept(new WeightVisitor()) : null;

            BodyAtom bodyAtom = new BodyAtom(predicate, terms, ctx.negation() != null, weight);
            bodyAtom.originalString = ctx.getText();

            return bodyAtom;
        }
    }

    public class LiftedExampleVisitor extends NeuralogicBaseVisitor<LiftedExample> {
        VariableFactory variableFactory = new VariableFactory();

        @Override
        public LiftedExample visitLiftedExample(@NotNull NeuralogicParser.LiftedExampleContext ctx) {
            FactConjunctionVisitor factConjunctionVisitor = new FactConjunctionVisitor();
            factConjunctionVisitor.variableFactory = this.variableFactory;
            RuleLineVisitor ruleLineVisitor = new RuleLineVisitor();
            ruleLineVisitor.variableFactory = this.variableFactory;

            List<Conjunction> conjunctions = ctx.conjunction()
                    .stream()
                    .map(conj -> conj.accept(factConjunctionVisitor))
                    .collect(Collectors.toList());

            List<WeightedRule> rules = ctx.lrnnRule()
                    .stream()
                    .map(conj -> conj.accept(ruleLineVisitor))
                    .collect(Collectors.toList());

            LiftedExample liftedExample = new LiftedExample(conjunctions, rules);
            LOG.info("Example extracted: " + liftedExample);
            return liftedExample;
        }
    }

    public class LabelVisitor extends NeuralogicBaseVisitor<Conjunction> {
        public VariableFactory variableFactory;

        @Override
        public Conjunction visitLabel(NeuralogicParser.LabelContext ctx) {
            FactConjunctionVisitor factConjunctionVisitor = new FactConjunctionVisitor();
            Conjunction label = ctx.conjunction().accept(factConjunctionVisitor);
            return label;
        }
    }

    public class FactConjunctionVisitor extends NeuralogicBaseVisitor<Conjunction> {
        public VariableFactory variableFactory;

        @Override
        public Conjunction visitConjunction(@NotNull NeuralogicParser.ConjunctionContext ctx) {
            FactVisitor factVisitor = new FactVisitor();
            factVisitor.variableFactory = this.variableFactory;
            List<ValuedFact> conjunction = ctx.atom()
                    .stream()
                    .map(atom -> atom.accept(factVisitor))
                    .collect(Collectors.toList());
            return new Conjunction(conjunction);
        }
    }

    public class FactVisitor extends NeuralogicBaseVisitor<ValuedFact> {
        public VariableFactory variableFactory;

        public FactVisitor(){
            variableFactory = new VariableFactory();
        }

        @Override
        public ValuedFact visitFact(@NotNull NeuralogicParser.FactContext ctx) {
            return visitAtom(ctx.atom());
        }

        @Override
        public ValuedFact visitAtom(@NotNull NeuralogicParser.AtomContext ctx) {
            TermVisitor termVisitor = new TermVisitor();
            termVisitor.variableFactory = this.variableFactory;
            List<Term> terms;
            if (ctx.termList() != null) {
                terms = ctx.termList().term()
                        .stream()
                        .map(term -> term.accept(termVisitor))
                        .collect(Collectors.toList());
            } else {
                terms = new ArrayList<>(0);
            }

            WeightedPredicate predicate = ctx.predicate().accept(new PredicateVisitor(terms.size()));

            Weight weight = null;
            if (ctx.weight() != null) {
                weight = ctx.weight().accept(new WeightVisitor());
            }
//            if (weight == null) {
//                weight = builder.weightFactory.construct("factWeight", builder.settings.defaultFactValue, true, true);  //todo next custom weights should have some flag
//            }

            ValuedFact fact = new ValuedFact(predicate, terms, ctx.negation() != null, weight);
            fact.originalString = ctx.getText();

            return fact;
        }
    }

    private class PredicateVisitor extends NeuralogicBaseVisitor<WeightedPredicate> {
        int arity = -1;

        public PredicateVisitor(int arity) {
            this.arity = arity;
        }

        @Override
        public WeightedPredicate visitPredicate(@NotNull NeuralogicParser.PredicateContext ctx) {
            if (ctx.INT() != null) {
                try {
                    arity = Integer.parseInt(ctx.INT().getText());
                } catch (Exception ex) {
                    LOG.severe("Cannot parse arity of a predicate from " + ctx.getText());
                }
            }
            WeightedPredicate predicate = builder.predicateFactory.construct(ctx.ATOMIC_NAME().getText(), arity, ctx.SPECIAL() != null);

            return predicate;
        }
    }

    private class WeightVisitor extends NeuralogicBaseVisitor<Weight> {
        @Override
        public Weight visitWeight(@NotNull NeuralogicParser.WeightContext ctx) {
            Pair<Boolean, Value> value = null;
            boolean fixed = false;
            if (ctx.fixedValue() != null) {
                fixed = true;
                value = parseValue(ctx.fixedValue().value());
            } else if (ctx.value() != null) {
                fixed = false;
                value = parseValue(ctx.value());
            } else {
                LOG.severe("Weight is neither fixed nor learnable");
            }
            Weight weight;
            if (ctx.ATOMIC_NAME() != null) {
                String name = ctx.ATOMIC_NAME().getText();
                weight = builder.weightFactory.construct(name, value.s, fixed, value.r);
            } else {
                weight = builder.weightFactory.construct(value.s, fixed, value.r);
            }
            return weight;
        }

        public Pair<Boolean, Value> parseValue(NeuralogicParser.ValueContext ctx) {
            Value value = null;
            boolean isInitialized = true;
            if (ctx.number() != null) {
                value = new ScalarValue(Float.parseFloat(ctx.number().getText()));
            } else if (ctx.vector() != null) {
                List<Double> vector = ctx.vector().number().stream().map(num -> Double.parseDouble(num.getText())).collect(Collectors.toList());
                value = new VectorValue(vector);
//                ((VectorValue) value).rowOrientation = true;    //we treat vector in the template as row vectors (i.e. as 1xN matrices) so that they can be multiplied with normal (column) vectors   -WRONG! this is confusing, just keep the default column orientation everywhere....
            } else if (ctx.matrix() != null) {
                List<List<Double>> vectors = new ArrayList<>();
                for (NeuralogicParser.VectorContext vectorContext : ctx.matrix().vector()) {
                    List<Double> vector = ctx.vector().number().stream().map(num -> Double.parseDouble(num.getText())).collect(Collectors.toList());
                    vectors.add(vector);
                }
                value = new MatrixValue(vectors);
            } else if (ctx.dimensions() != null) {
                isInitialized = false;
                List<Integer> dims = ctx.dimensions().number().stream().map(num -> Integer.parseInt(num.getText())).collect(Collectors.toList());
                if (dims.size() == 1) {
                    if (dims.get(0) == 1)
                        value = new ScalarValue();
                    else {
                        value = new VectorValue(dims.get(0));
//                        ((VectorValue) value).rowOrientation = true;    //we treat vector in the template as row vectors (i.e. as 1xN matrices) so that they can be multiplied with normal (column) vectors   -WRONG
                    }
                } else if (dims.size() == 2) {
                    if (dims.get(0) == 1) {
                        value = new VectorValue(dims.get(1));
                        ((VectorValue) value).rowOrientation = true;
                    } else if (dims.get(1) == 1) {
                        value = new VectorValue(dims.get(0));
                        ((VectorValue) value).rowOrientation = false;
                    } else
                        value = new MatrixValue(dims.get(0), dims.get(1));
                }
            } else {
                LOG.severe("Value is neither number nor vector: Could not parse numeric value from " + ctx.getText());
            }
            if (value == null) {
                LOG.severe("Error during constructs.building numeric value from " + ctx.getText());
            }
            return new Pair<>(isInitialized, value);
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
        public Pair<WeightedPredicate, Map<String, Object>> visitPredicateMetadata(@NotNull NeuralogicParser.PredicateMetadataContext ctx) {
            int arity = -1;
            try {
                arity = Integer.parseInt(ctx.predicate().INT().getText());
            } catch (Exception ex) {
                LOG.severe("Cannot parse arity of a predicate from " + ctx.getText());
            }
            WeightedPredicate predicate = builder.predicateFactory.construct(ctx.predicate().ATOMIC_NAME().getText(), arity, ctx.predicate().SPECIAL() != null);
            Map<String, Object> metadata = ctx.metadataList().accept(new MetadataListVisitor());
            return new Pair(predicate, metadata);
        }
    }

    public class WeightMetadataVisitor extends NeuralogicBaseVisitor<Pair<Weight, Map<String, Object>>> {
        @Override
        public Pair<Weight, Map<String, Object>> visitWeightMetadata(@NotNull NeuralogicParser.WeightMetadataContext ctx) {
            Weight weight = builder.weightFactory.construct(ctx.ATOMIC_NAME().getText());
            Map<String, Object> metadata = ctx.metadataList().accept(new MetadataListVisitor());
            return new Pair(weight, metadata);
        }
    }

    public class PredicateOffsetVisitor extends NeuralogicBaseVisitor<Pair<WeightedPredicate, Weight>> {
        @Override
        public Pair<WeightedPredicate, Weight> visitPredicateOffset(@NotNull NeuralogicParser.PredicateOffsetContext ctx) {
            int arity = -1;
            try {
                arity = Integer.parseInt(ctx.predicate().INT().getText());
            } catch (Exception ex) {
                LOG.severe("Cannot parse arity of a predicate from " + ctx.getText());
            }
            WeightedPredicate predicate = builder.predicateFactory.construct(ctx.predicate().ATOMIC_NAME().getText(), arity, ctx.predicate().SPECIAL() != null);
            Weight offset = ctx.weight().accept(new WeightVisitor());
            predicate.weight = offset;
            return new Pair(predicate, offset);
        }
    }

    /**
     * Metadata are parsed as a mere String->Object mappings.
     * The extraction of the particular {@link Parameter} to {@link ParameterValue} mappings
     * is done during postprocessing/building of logiacl constructs as it may require more complex recognition logic than just parsing.
     */
    private class MetadataListVisitor extends NeuralogicBaseVisitor<Map<String, Object>> {
        @Override
        public Map<String, Object> visitMetadataList(@NotNull NeuralogicParser.MetadataListContext ctx) {
            Map<String, Object> metadata = new LinkedHashMap<>();
            for (NeuralogicParser.MetadataValContext paramVal : ctx.metadataVal()) {
                String parameter = paramVal.ATOMIC_NAME(0).getText();
                String valueText = null;
                if (paramVal.ATOMIC_NAME(1) != null) {
                    valueText = paramVal.ATOMIC_NAME(1).getText();
                }
                Object value;
                if (paramVal.DOLLAR() != null) {
                    value = builder.weightFactory.construct(valueText);
                } else if (paramVal.value() != null) {
                    value = new WeightVisitor().parseValue(paramVal.value()).s;
                } else {
                    value = new StringValue(valueText);
                }
                metadata.put(parameter, value);
            }
            return metadata;
        }
    }

    public class TemplateMetadataVisitor extends NeuralogicBaseVisitor<Map<String, Object>> {
        @Override
        public Map<String, Object> visitTemplateMetadata(@NotNull NeuralogicParser.TemplateMetadataContext ctx) {
            Map<String, Object> metadata = ctx.metadataList().accept(new MetadataListVisitor());
            return metadata;
        }
    }
}
