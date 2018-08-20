package constructs.building;

import constructs.building.factories.ConstantFactory;
import constructs.building.factories.WeightedPredicateFactory;
import constructs.building.factories.WeightFactory;
import neuralogic.grammarParsing.PlainParseTree;
import org.antlr.v4.runtime.ParserRuleContext;
import settings.Settings;

import java.io.Reader;
import java.util.logging.Logger;

public abstract class LogicSourceBuilder<I extends PlainParseTree<? extends ParserRuleContext>, O> {
    private static final Logger LOG = Logger.getLogger(LogicSourceBuilder.class.getName());

    Settings settings;

    // Constants are shared over the whole template
    public ConstantFactory constantFactory = new ConstantFactory();
    // Predicates are shared over the whole template
    public WeightedPredicateFactory predicateFactory = new WeightedPredicateFactory();
    // Weights are shared over the whole template
    public WeightFactory weightFactory = new WeightFactory();
    //variable factories are typically just used locally

    public void setFactoriesFrom(LogicSourceBuilder other){
        this.constantFactory = other.constantFactory;
        this.predicateFactory = other.predicateFactory;
        this.weightFactory = other.weightFactory;
    }

    abstract I parseTreeFrom(Reader reader);

    abstract O buildFrom(I parseTree);
}