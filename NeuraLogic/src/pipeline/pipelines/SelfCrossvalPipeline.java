package pipeline.pipelines;

import com.sun.istack.internal.NotNull;
import constructs.template.Template;
import grounding.Grounder;
import grounding.bottomUp.BottomUp;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import neuralogic.examples.PlainQueriesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.TemplateParseTreeExtractor;
import parsing.LearningSamplesBuilder;
import parsing.TemplateBuilder;
import pipeline.*;
import pipeline.pipelines.pipes.IdentityGenPipe;
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

        Pipe<PlainQueriesParseTree, Stream<LearningSample>> queriesProcessingPipe;
        sourceBrach.output2 = register(queriesProcessingPipe = new Pipe<PlainQueriesParseTree, Stream<LearningSample>>("QueriesProcessingPipe") {

            @Override
            public Stream<LearningSample> apply(PlainQueriesParseTree plainQueriesParseTree) {
                LearningSamplesBuilder learningSamplesBuilder = new LearningSamplesBuilder(settings);
                //learningSamplesBuilder.buildFrom();
                //TODO create queries/examples
                return null;
            }
        });

        MultiBranch<Stream<LearningSample>, Stream<LearningSample>> crossvalBranch;
        queriesProcessingPipe.output = register(crossvalBranch = new MultiBranch<Stream<LearningSample>, Stream<LearningSample>>("CrossvalBranch") {

            @Override
            public Stream<Stream<LearningSample>> apply(Stream<LearningSample> learningSampleStream) {
                List<LearningSample> originalList = learningSampleStream.collect(Collectors.toList());
                int partitionSize = settings.foldsCount;
                List<Stream<LearningSample>> partitions = new LinkedList<>();
                for (int i = 0; i < originalList.size(); i += partitionSize) {
                    partitions.add(originalList.subList(i, Math.min(i + partitionSize, originalList.size())).stream());
                }
                Stream<Stream<LearningSample>> stream = partitions.stream();
                return stream;
            }
        });

        Merge<Stream<Stream<LearningSample>>, Template, Pair<Stream<Stream<LearningSample>>, Template>> mergeGround =
                register(new Merge<Stream<Stream<LearningSample>>, Template, Pair<Stream<Stream<LearningSample>>, Template>>("MergingGrounding") {
                    @Override
                    protected Pair<Stream<Stream<LearningSample>>, Template> merge(Stream<Stream<LearningSample>> sampleFolds, Template template) {
                        Grounder grounder = new BottomUp();
                        Stream<Stream<LearningSample>> streamStream = sampleFolds.map(fold -> fold.map(sample -> grounder.ground(sample, template)));
                        return new Pair<>(streamStream, template);
                    }
                });

        mergeGround.input1 = crossvalBranch;
        mergeGround.input2 = templateProcessingPipe;
        queriesProcessingPipe.output = mergeGround;
        templateProcessingPipe.output = mergeGround;

        Pipe<Pair<Stream<Stream<LearningSample>>, Template>, Results> trainingPipe;
        mergeGround.output = register(trainingPipe = new Pipe<Pair<Stream<Stream<LearningSample>>,Template>, Results>("TrainingPipe") {
            @Override
            public Results apply(Pair<Stream<Stream<LearningSample>>, Template> streamTemplatePair) {
                return null;
            }
        });

        terminals.add(trainingPipe);

    }
}
