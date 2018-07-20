package building;

import constructs.example.GroundExample;
import neuralogic.grammarParsing.PlainParseTree;
import parsers.neuralogic.NeuralogicParser;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ExamplesBuilder extends LogicSourceBuilder<NeuralogicParser.ExamplesFileContext, Stream<GroundExample>> {
    private static final Logger LOG = Logger.getLogger(ExamplesBuilder.class.getName());

    @Override
    public Stream<GroundExample> buildFrom(Reader reader) throws IOException {
        return null;
    }

    @Override
    public Stream<GroundExample> buildFrom(PlainParseTree<NeuralogicParser.ExamplesFileContext> parseTree) throws IOException {
        //TODO
        return null;
    }
}
