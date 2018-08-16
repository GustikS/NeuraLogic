package constructs.building;

import constructs.example.LiftedExample;
import constructs.example.LogicSample;
import constructs.example.QueryAtom;
import constructs.example.ValuedFact;
import constructs.template.Atom;
import learning.LearningSample;
import neuralogic.grammarParsing.PlainParseTree;
import org.antlr.v4.runtime.ParserRuleContext;
import settings.Settings;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * To get labeleb samples from example file (parsable source)
 */
public abstract class SamplesBuilder<I extends PlainParseTree<? extends ParserRuleContext>, O> extends LogicSourceBuilder<I, Stream<O>> {
    private static final Logger LOG = Logger.getLogger(SamplesBuilder.class.getName());
    protected final Settings settings;

    final String prefix;
    int queryCounter = 0;

    public SamplesBuilder(Settings settings) {
        this.settings = settings;
        this.prefix = settings.sampleIdPrefix;
    }

    public Stream<LogicSample> buildSamplesFrom(I parseTree) {
        return buildFrom(parseTree).flatMap(this::sampleFrom);
    }

    protected abstract Stream<LogicSample> sampleFrom(O pair);


    /**
     * Stream TERMINATING OPERATION because of hashmap during merging!
     * Do not use separate (queries,examples) file if full streaming is desirable
     *
     * @param queries
     * @param examples
     * @return
     */
    public static Stream<LogicSample> merge2streams(Stream<LogicSample> queries, Stream<LogicSample> examples) {
        if (queries.isParallel() || examples.isParallel()) {
            return Stream.concat(queries, examples).collect(Collectors.toConcurrentMap(LearningSample::getId, q -> q, SamplesBuilder::merge2samples)).values().stream();
        } else {
            return Stream.concat(queries, examples).collect(Collectors.toMap(LearningSample::getId, q -> q, SamplesBuilder::merge2samples)).values().stream();
        }
    }

    /**
     * Combined a separate query (wrapped in LogicSample) and example (wrapped in LogicSample) into a single LogicSample
     * Due to possible parallelism, it is not certain in what order they will come (i.e. q1,q2 are interchangebale)
     * @param q1
     * @param q2
     * @return
     */
    private static LogicSample merge2samples(LogicSample q1, LogicSample q2) {
        LogicSample example = q1.query.evidence != null ? q1 : q2;
        LogicSample query = q1.target != null ? q1 : q2;

        if (query == example) {
            LOG.severe("Example-Query merging inconsistency: " + q1 + " + " + q2);
        }
        query.query.evidence = example.query.evidence;
        return query;
    }

    public QueryAtom createQueryAtom(String id, ValuedFact f, LiftedExample example) {
        return new QueryAtom(prefix + id, queryCounter++, settings.defaultSampleImportance, new Atom(f.literal, f.weightedPredicate), example);
    }

    public QueryAtom createQueryAtom(String id, double importance, ValuedFact f) {
        return new QueryAtom(prefix + id, queryCounter++, importance, new Atom(f.literal, f.weightedPredicate));
    }

    public QueryAtom createQueryAtom(String id, ValuedFact f) {
        return new QueryAtom(prefix + id, queryCounter++, settings.defaultSampleImportance, new Atom(f.literal, f.weightedPredicate));
    }
}