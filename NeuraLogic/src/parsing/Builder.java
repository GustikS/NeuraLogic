package parsing;

import constructs.factories.ConstantFactory;
import constructs.factories.PredicateFactory;
import constructs.factories.WeightFactory;
import settings.Settings;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public abstract class Builder<T> {
    private static final Logger LOG = Logger.getLogger(Builder.class.getName());

    // Constants are shared over the whole template
    public ConstantFactory constantFactory = new ConstantFactory();
    // Predicates are shared over the whole template
    public PredicateFactory predicateFactory = new PredicateFactory();
    // Weights are shared over the whole template
    public WeightFactory weightFactory = new WeightFactory();

    abstract T buildFrom(Reader reader, Settings settings) throws IOException;
}