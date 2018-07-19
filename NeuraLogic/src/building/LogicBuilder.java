package building;

import constructs.factories.ConstantFactory;
import constructs.factories.PredicateFactory;
import constructs.factories.WeightFactory;
import settings.Settings;
import settings.Sources;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

public abstract class LogicBuilder<T> {
    private static final Logger LOG = Logger.getLogger(LogicBuilder.class.getName());

    Settings settings;

    // Constants are shared over the whole template
    public ConstantFactory constantFactory = new ConstantFactory();
    // Predicates are shared over the whole template
    public PredicateFactory predicateFactory = new PredicateFactory();
    // Weights are shared over the whole template
    public WeightFactory weightFactory = new WeightFactory();

    @Deprecated
    public abstract T buildFrom(Reader reader) throws IOException;

    public abstract T buildFrom(Sources sources) throws IOException;
}