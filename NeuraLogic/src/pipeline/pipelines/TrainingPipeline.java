package pipeline.pipelines;

import com.sun.istack.internal.NotNull;
import constructs.template.Template;
import grounding.Grounder;
import grounding.bottomUp.BottomUp;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import neuralogic.queries.PlainQueriesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import neuralogic.template.TemplateParseTreeExtractor;
import parsing.LearningSamplesBuilder;
import parsing.TemplateBuilder;
import pipeline.*;
import pipeline.pipelines.prepared.IdentityGenPipe;
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingPipeline extends Pipeline<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(TrainingPipeline.class.getName());

    public TrainingPipeline(@NotNull Settings settings) {
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
                //TODO create trainQueries/trainExamples
                return null;
            }
        });

        Merge<Stream<LearningSample>, Template, Pair<Template, Stream<LearningSample>>> mergeGround =
                register(new Merge<Stream<LearningSample>, Template, Pair<Template, Stream<LearningSample>>>("MergingGrounding") {
            @Override
            protected Pair<Template, Stream<LearningSample>> merge(Stream<LearningSample> input1, Template input2) {
                Grounder grounder = new BottomUp();
                //TODO ground
                return null;
            }
        });

        mergeGround.input1 = queriesProcessingPipe;
        mergeGround.input2 = templateProcessingPipe;
        queriesProcessingPipe.output = mergeGround;
        templateProcessingPipe.output = mergeGround;

        Pipe<Pair<Template, Stream<LearningSample>>, Results> trainingPipe;
        mergeGround.output = register(trainingPipe = new Pipe<Pair<Template, Stream<LearningSample>>, Results>("TrainingPipe") {
            @Override
            public Results apply(Pair<Template, Stream<LearningSample>> templateStreamPair) {
                //TODO train
                return null;
            }
        });

        terminals.add(trainingPipe);

    }
}
