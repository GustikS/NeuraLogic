package cz.cvut.fel.ida.logic.constructs.building;

import cz.cvut.fel.ida.logic.constructs.building.factories.ConstantFactory;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightedPredicateFactory;
import org.antlr.v4.runtime.ParserRuleContext;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.PlainParseTree;
import cz.cvut.fel.ida.setup.Settings;

import java.io.Reader;
import java.util.function.Function;
import java.util.logging.Logger;

public abstract class LogicSourceBuilder<I extends PlainParseTree<? extends ParserRuleContext>, O> {
    private static final Logger LOG = Logger.getLogger(LogicSourceBuilder.class.getName());

    public Settings settings;

    /**
     * check whether there is a logical (hard) negation in the source
     */
    public boolean negationDetected = false;

    /**
     * Call this to rebuild a specific pipeline if an inconsistency with settings is detected at runtime
     */
    public Function<String, Boolean> rebuildCallback;

    // Constants are shared over the whole logic source
    public ConstantFactory constantFactory = new ConstantFactory();
    // Predicates are shared over the whole logic source
    public WeightedPredicateFactory predicateFactory = new WeightedPredicateFactory();
    // Weights are shared over the whole logic source
    public WeightFactory weightFactory;

    public LogicSourceBuilder(Settings settings, WeightFactory weightFactory) {
        this.settings = settings;
        this.weightFactory  = weightFactory;
    }

    //variable factories are typically just used locally (variables shared only within clauses)

    public void setFactoriesFrom(LogicSourceBuilder other) {
        this.constantFactory = other.constantFactory;
        this.predicateFactory = other.predicateFactory;
        this.weightFactory = other.weightFactory;
    }

    abstract I parseTreeFrom(Reader reader);

    abstract O buildFrom(I parseTree);

    /**
     * Sometimes it is useful to store reference to the outer pipe within which we work
     */
    public void setRebuildCallback(Function<String, Boolean> callback) {
        this.rebuildCallback = callback;
    }
}