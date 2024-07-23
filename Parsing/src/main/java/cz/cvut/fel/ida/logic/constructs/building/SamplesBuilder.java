package cz.cvut.fel.ida.logic.constructs.building;

import cz.cvut.fel.ida.learning.LearningSample;
import cz.cvut.fel.ida.logic.constructs.building.factories.WeightFactory;
import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.constructs.example.LogicSample;
import cz.cvut.fel.ida.logic.constructs.example.QueryAtom;
import cz.cvut.fel.ida.logic.constructs.example.ValuedFact;
import cz.cvut.fel.ida.logic.constructs.template.components.HeadAtom;
import cz.cvut.fel.ida.logic.parsing.grammarParsing.PlainParseTree;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.Utilities;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * //todo pridat query-to-multiple examples correspondence?? mozna...(spis ne)
 */
public abstract class SamplesBuilder<I extends PlainParseTree<? extends ParserRuleContext>, O> extends LogicSourceBuilder<I, Stream<O>> {
    private static final Logger LOG = Logger.getLogger(SamplesBuilder.class.getName());

    public static int counter = 0;

    final String prefix;
    int queryCounter = 0;

    public SamplesBuilder(Settings settings) {
        super(settings, new WeightFactory(settings.inferred.maxWeightCount));   //Weights in samples can also be learnable now -> continuing from the last weight index
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
    public Stream<LogicSample> merge2streams(Stream<LogicSample> queries, Stream<LogicSample> examples) {
        if (settings.queriesAlignedWithExamples) {
            return Utilities.zipStreams(queries, examples, SamplesBuilder::merge2samples);
        }

        LOG.warning("Could not infer whether queries are directly aligned with examples, will need to consume (iterate) the stream first to align them!");

        Map<String, Pair<LiftedExample, List<LogicSample>>> map;
        if (queries.isParallel() || examples.isParallel()) {
            if (settings.oneQueryPerExample && settings.groundingMode != Settings.GroundingMode.GLOBAL) {
                ConcurrentMap<String, LogicSample> sampleMap = Stream.concat(queries, examples).collect(Collectors.toConcurrentMap(LearningSample::getId, q -> q, SamplesBuilder::merge2samples));
                examples.close();
                queries.close();
                return getSortedLogicSampleStream(sampleMap);
            }
            map = new ConcurrentHashMap<>();
        } else {
            if (settings.oneQueryPerExample && settings.groundingMode != Settings.GroundingMode.GLOBAL) {
                Map<String, LogicSample> sampleMap = Stream.concat(queries, examples).collect(Collectors.toMap(LearningSample::getId, q -> q, SamplesBuilder::merge2samples));
                examples.close();
                queries.close();
                return getSortedLogicSampleStream(sampleMap);
            }
            map = new TreeMap<>();
        }
        //the remaining 1 example to Many queries solution
        examples.forEach(
                ls -> map.put(ls.getId(), new Pair<>(ls.query.evidence, new ArrayList<>()))
        );

        //this is where the examples get finally calculated
        if (map.size() == 0) {
            LOG.severe("There are no learning examples created!");
        } else if (map.size() == 1) {
            LOG.info("A single large example created. All queries will refer to it.");
            Pair<LiftedExample, List<LogicSample>> pair = map.values().iterator().next();
            queries.forEach(ls -> {
                ls.query.evidence = pair.r;
                List<LogicSample> qs = pair.s;
                qs.add(ls);
                LOG.finest("Extracted Sample: " + ls);  //in KBC mode,  there are too many samples and all exacly the same, excet the query, so the report is not interesting, only clutters the console output
            });
        } else {
            LOG.info("Multiple examples created. Will try to match them against the provided queries");
            queries.forEach(ls -> {
                Pair<LiftedExample, List<LogicSample>> pair = map.get(ls.getId());
                ls.query.evidence = pair.r;
                List<LogicSample> qs = pair.s;
                qs.add(ls);
                LOG.fine("Extracted Sample: " + ls);
            });
        }

        examples.close();
        queries.close();

        counter = map.size(); // just store/expose it for outside access (e.g. FileDataset progress-bar reading)

//        settings.inferred.maxWeightCount = weightFactory.getIndex();

        return map.values().stream().map(pair -> pair.s.stream()).flatMap(f -> f);
    }

    /**
     * Sorting of the assembled samples to ensure determinism
     *
     * @param sampleMap
     * @return
     */
    private Stream<LogicSample> getSortedLogicSampleStream(Map<String, LogicSample> sampleMap) {
        LOG.info("Consumed 2 input streams of " + sampleMap.size() + " samples (queries+examples)");
        ArrayList<LogicSample> logicSamples = new ArrayList<>(sampleMap.values());
        logicSamples.sort(LogicSample::compare);
        return logicSamples.stream();
    }

    /**
     * Combined a separate query (wrapped in LogicSample) and example (wrapped in LogicSample) into a single LogicSample
     * Due to possible parallelism, it is not certain in what order they will come (i.e. q1,q2 are interchangebale)
     *
     * @param q1
     * @param q2
     * @return
     */
    private static LogicSample merge2samples(LogicSample q1, LogicSample q2) {
        LogicSample example = q1.query.evidence != null ? q1 : q2;
        LogicSample query = q1.isQueryOnly ? q1 : q2;

        if (query == example) {
            LOG.severe("Example-Query merging inconsistency: " + q1 + " + " + q2);
        }
        query.query.evidence = example.query.evidence;
        LOG.info("Example and query have been merged into Sample: " + query);
        return query;
    }

    public QueryAtom createQueryAtom(String id, ValuedFact f, LiftedExample example) {
        HeadAtom headAtom;
        if (f == null)
            headAtom = null;
        else
            headAtom = new HeadAtom(f.offsettedPredicate, f.literal.termList());

        return new QueryAtom(prefix + id, queryCounter++, settings.defaultSampleImportance, headAtom, example);
    }

    public QueryAtom createQueryAtom(String id, double importance, ValuedFact f) {
        return new QueryAtom(prefix + id, queryCounter++, importance, new HeadAtom(f.offsettedPredicate, f.literal.termList()));
    }

    public QueryAtom createQueryAtom(String id, ValuedFact f) {
        return new QueryAtom(prefix + id, queryCounter++, settings.defaultSampleImportance, new HeadAtom(f.offsettedPredicate, f.literal.termList()));
    }
}