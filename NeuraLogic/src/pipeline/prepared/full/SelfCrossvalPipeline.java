package pipeline.prepared.full;

import com.sun.istack.internal.NotNull;
import constructs.template.Template;
import grounding.Grounder;
import grounding.bottomUp.BottomUp;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import neuralogic.queries.PlainQueriesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.TemplateParseTreeExtractor;
import constructs.building.SamplesBuilder;
import constructs.building.TemplateBuilder;
import pipeline.*;
import pipeline.prepared.pipes.IdentityGenPipe;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelfCrossvalPipeline extends Pipeline<Sources, Results> {
    // TODO - create pipeline within pipeline - for each fold !? reuse existing pipeline and just aggregate their terminals at the end
    private static final Logger LOG = Logger.getLogger(SelfCrossvalPipeline.class.getName());

    public SelfCrossvalPipeline(@NotNull Settings settings) {
        this.settings = settings;

        Pipe<Sources, Sources> start;
        starts = Collections.singletonList(register(start = new IdentityGenPipe<>("IdentityGenPipe")));

        Branch<Sources, PlainTemplateParseTree, PlainQueriesParseTree> sourceBrach = register(new Branch<Sources, PlainTemplateParseTree, PlainQueriesParseTree>("SourceBranch") {
            @Override
            protected Pair<PlainTemplateParseTree, PlainQueriesParseTree> branch(Sources sources) {
                return new Pair<PlainTemplateParseTree, PlainQueriesParseTree>(sources.templateParseTree, sources.trainQueriesParseTree);
            }
        });
        start.output = sourceBrach;
        sourceBrach.input = start;  //not necessary
        Pipe<PlainTemplateParseTree, Template> templateProcessingPipe;
        sourceBrach.output1 = register(templateProcessingPipe = new Pipe<PlainTemplateParseTree, Template>("TemplateProcessingPipe") {

            @Override
            public Template apply(PlainTemplateParseTree plainTemplateParseTree) {
                TemplateBuilder templateBuilder = new TemplateBuilder(settings);
                TemplateParseTreeExtractor templateParseTreeExtractor = templateBuilder.getTemplateParseTreeExtractor();
                return templateBuilder.buildFrom(plainTemplateParseTree, templateParseTreeExtractor);
            }
        });

        Pipe<PlainQueriesParseTree, Stream<LogicSample>> queriesProcessingPipe;
        sourceBrach.output2 = register(queriesProcessingPipe = new Pipe<PlainQueriesParseTree, Stream<LogicSample>>("QueriesProcessingPipe") {

            @Override
            public Stream<LogicSample> apply(PlainQueriesParseTree plainQueriesParseTree) {
                SamplesBuilder learningSamplesBuilder = new SamplesBuilder(settings);
                //learningSamplesBuilder.buildFrom();
                //TODO create trainQueries/trainExamples
                return null;
            }
        });

        MultiBranch<Stream<LogicSample>, Stream<LogicSample>> crossvalBranch;
        queriesProcessingPipe.output = register(crossvalBranch = new MultiBranch<Stream<LogicSample>, Stream<LogicSample>>("CrossvalBranch") {

            @Override
            public Stream<Stream<LogicSample>> apply(Stream<LogicSample> learningSampleStream) {
                List<LogicSample> originalList = learningSampleStream.collect(Collectors.toList());
                int partitionSize = settings.foldsCount;
                List<Stream<LogicSample>> partitions = new LinkedList<>();
                for (int i = 0; i < originalList.size(); i += partitionSize) {
                    partitions.add(originalList.subList(i, Math.min(i + partitionSize, originalList.size())).stream());
                }
                Stream<Stream<LogicSample>> stream = partitions.stream();
                return stream;
            }
        });

        Merge<Stream<Stream<LogicSample>>, Template, Pair<Stream<Stream<LogicSample>>, Template>> mergeGround =
                register(new Merge<Stream<Stream<LogicSample>>, Template, Pair<Stream<Stream<LogicSample>>, Template>>("MergingGrounding") {
                    @Override
                    protected Pair<Stream<Stream<LogicSample>>, Template> merge(Stream<Stream<LogicSample>> sampleFolds, Template template) {
                        Grounder grounder = new BottomUp();
                        Stream<Stream<LogicSample>> streamStream = sampleFolds.map(fold -> fold.map(sample -> grounder.ground(sample, template)));
                        return new Pair<>(streamStream, template);
                    }
                });

        mergeGround.input1 = crossvalBranch;
        mergeGround.input2 = templateProcessingPipe;
        queriesProcessingPipe.output = mergeGround;
        templateProcessingPipe.output = mergeGround;

        Pipe<Pair<Stream<Stream<LogicSample>>, Template>, Results> trainingPipe;
        mergeGround.output = register(trainingPipe = new Pipe<Pair<Stream<Stream<LogicSample>>,Template>, Results>("TrainingPipe") {
            @Override
            public Results apply(Pair<Stream<Stream<LogicSample>>, Template> streamTemplatePair) {
                return null;
            }
        });

        terminals.add(trainingPipe);

    }
}
