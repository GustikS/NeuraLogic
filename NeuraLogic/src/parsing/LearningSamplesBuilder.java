package parsing;

import constructs.example.GroundExample;
import learning.Example;
import learning.LearningSample;
import learning.Query;
import parsers.neuralogic.NeuralogicParser;
import settings.Settings;

import java.util.List;
import java.util.logging.Logger;

public class LearningSamplesBuilder {
    private static final Logger LOG = Logger.getLogger(LearningSamplesBuilder.class.getName());

    public LearningSamplesBuilder(Settings settings){
        //TODO extract from settings what version/format of examples this will be (single vs. multiple files)
    }

    public List<Example> getLearningSamples() {

    }

    /**
     * Jointly parsing both query-label and example
     */
    public class SampleBuilder extends Builder<List<LearningSample>> {

        @Override
        protected List<LearningSample> buildFrom(NeuralogicParser parseTree, Settings settings) {
            return null;
        }
    }

    /**
     * Parsing separate example facts
     */
    public class ExampleBuilder extends Builder<List<GroundExample>> {

        @Override
        protected List<GroundExample> buildFrom(NeuralogicParser parseTree, Settings settings) {
            return null;
        }
    }

    /**
     * Parsing separate query-labels
     */
    public class QueryBuilder extends Builder<List<Query>> {

        @Override
        protected List<Query> buildFrom(NeuralogicParser parseTree, Settings settings) {
            return null;
        }
    }
}