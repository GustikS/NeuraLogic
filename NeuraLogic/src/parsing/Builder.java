package parsing;

import constructs.factories.ConstantFactory;
import constructs.factories.PredicateFactory;
import constructs.factories.WeightFactory;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import parsers.neuralogic.NeuralogicLexer;
import parsers.neuralogic.NeuralogicParser;
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

    protected NeuralogicParser getNeuralogicParser(Reader reader) throws IOException {
        NeuralogicLexer lexer = new NeuralogicLexer(CharStreams.fromReader(reader));
        TokenStream tokens = new CommonTokenStream(lexer);
        return new NeuralogicParser(tokens);
    }

    public T buildFrom(Reader reader, Settings settings) throws IOException {

        NeuralogicParser parseTree = getNeuralogicParser(reader);
        return buildFrom(parseTree, settings);

    }

    protected abstract T buildFrom(NeuralogicParser parseTree, Settings settings);
}