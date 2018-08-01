package pipelines.prepared.full;

import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.Example;
import constructs.example.LogicSample;
import learning.Query;
import pipelines.Pipeline;
import settings.Settings;
import utils.Utilities;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by gusta on 26.3.17.
 */
public class StandardLearningPipeline extends Pipeline {
    @Override
    public void execute(Settings settings) {
        {
            Pair<Optional<Template>, Stream<LogicSample>> resources = initLearningPipeline(settings);
        }
    }







    public Pair<Optional<Template>, Stream<LogicSample>> initLearningPipeline(Settings settings) {

        Stream<LogicSample> samples = settings.sources.queriesPath == null ? streamLearningSamples(settings.sources.examplesFileReader, settings)
                : streamLearningSamples(settings.sources.examplesFileReader, settings.sources.queriesFileReader, settings);

        Optional<Template> template = null;
        try {
            template = Optional.of(settings.templatePath.isEmpty() ? null : getTemplate(settings.sources.templateFileReader, settings));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(template, samples);
    }


    Template getTemplate(FileReader templatePath, Settings settings) throws IOException {

        Template template = settings.sources.tp.parseTemplate(templatePath).preprocess(settings);
        return template;
    }

    private Stream<LogicSample> streamLearningSamples(FileReader examplesPath, Settings settings) {
    }

    /**
     * zipStreams
     *
     * @param examplesPath
     * @param queriesPath
     * @param settings
     * @return
     */
    Stream<LogicSample> streamLearningSamples(FileReader examplesPath, FileReader queriesPath, Settings settings) {

        Stream<Example> exampleStream = settings.sources.ep.parseExamples(examplesPath);
        Stream<List<Query>> queryStream = null;
        try {
            queryStream = settings.sources.qp.parseQueries(queriesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stream<LogicSample> zipStream = Utilities.zipStreams(exampleStream, queryStream, (example, queries) -> mergeQueriesWithExample(example, queries)).flatMap(Collection::stream);

        return zipStream;
    }

    private List<LogicSample> mergeQueriesWithExample(Example example, List<Query> queries) {
        List<LogicSample> list = new ArrayList<>();
        for (Query query : queries) {
            list.add(new LogicSample(query, example));
        }
        return list;
    }
}
