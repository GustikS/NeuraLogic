package pipelines;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.Example;
import learning.LearningSample;
import learning.Query;
import settings.Settings;
import utils.Utilities;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by gusta on 14.3.17.
 */
public abstract class Pipeline {


    public static Pipeline buildFrom(Settings settings) {
        //TODO build different pipeline based on settings
    }

    public abstract void execute(Settings settings);

    public Pair<Optional<Template>, Stream<LearningSample>> initLearningPipeline(Settings settings) {

        Stream<LearningSample> samples = settings.sf.queriesPath == null ? streamLearningSamples(settings.sf.examplesFileReader, settings)
                : streamLearningSamples(settings.sf.examplesFileReader, settings.sf.queriesFileReader, settings);

        Optional<Template> template = Optional.of(settings.templatePath.isEmpty() ? null : getTemplate(settings.sf.templateFileReader, settings));
        return new Pair<>(template, samples);
    }


    Template getTemplate(FileReader templatePath, Settings settings) {

        Template template = settings.sf.tp.parseTemplate(templatePath).preprocess(settings);
        return template;
    }

    private Stream<LearningSample> streamLearningSamples(FileReader examplesPath, Settings settings) {
    }

    /**
     * zipStreams
     *
     * @param examplesPath
     * @param queriesPath
     * @param settings
     * @return
     */
    Stream<LearningSample> streamLearningSamples(FileReader examplesPath, FileReader queriesPath, Settings settings) {

        Stream<Example> exampleStream = settings.sf.ep.parseExamples(examplesPath);
        Stream<List<Query>> queryStream = settings.sf.qp.parseQueries(queriesPath);
        Stream<LearningSample> zipStream = Utilities.zipStreams(exampleStream, queryStream, (example, queries) -> mergeQueriesWithExample(example, queries)).flatMap(Collection::stream);

        return zipStream;
    }

    private List<LearningSample> mergeQueriesWithExample(Example example, List<Query> queries) {
        List<LearningSample> list = new ArrayList<>();
        for (Query query : queries) {
            list.add(new LearningSample(query, example));
        }
        return list;
    }
}