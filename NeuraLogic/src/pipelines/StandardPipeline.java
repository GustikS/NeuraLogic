package pipelines;

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
import settings.Settings;
import settings.Sources;
import training.results.Results;

import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class StandardPipeline extends Pipeline<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(StandardPipeline.class.getName());

    public StandardPipeline(@NotNull Settings settings) {
        this.settings = settings;

        Pipe<Sources, Sources> start;
        starts = Collections.singletonList(register(start = new SourceGenPipe<>("SourceGenPipe")));

        Branch<Sources, PlainTemplateParseTree, PlainQueriesParseTree> sourceBrach = register(new Branch<Sources, PlainTemplateParseTree, PlainQueriesParseTree>("SourceBranch") {
            @Override
            protected Pair<PlainTemplateParseTree, PlainQueriesParseTree> branch(Sources sources) {
                return new Pair<PlainTemplateParseTree, PlainQueriesParseTree>(sources.templateParseTree, sources.trainQueriesParseTree);
            }
        });
        start.output = sourceBrach;

        sourceBrach.input = start;  //not necessary
        sourceBrach.output1 = register(new Pipe<PlainTemplateParseTree, Template>("TemplateProcessingPipe") {

            @Override
            public Template apply(PlainTemplateParseTree plainTemplateParseTree) {
                TemplateBuilder templateBuilder = new TemplateBuilder(settings);
                TemplateParseTreeExtractor templateParseTreeExtractor = templateBuilder.getTemplateParseTreeExtractor();
                return templateBuilder.buildFrom(plainTemplateParseTree, templateParseTreeExtractor);
            }
        });

        sourceBrach.output2 = register(new Pipe<PlainQueriesParseTree, Stream<LearningSample>>("QueriesProcessingPipe") {

            @Override
            public Stream<LearningSample> apply(PlainQueriesParseTree plainQueriesParseTree) {
                LearningSamplesBuilder learningSamplesBuilder = new LearningSamplesBuilder(settings);
                learningSamplesBuilder.buildFrom();
                //TODO create queries/examples
                return null;
            }
        });

        Merge<Stream<LearningSample>, Template, Pair<Template, Stream<LearningSample>>> mergeGround = register(new Merge<Stream<LearningSample>, Template, Pair<Template, Stream<LearningSample>>>("MergingGrounding") {
            @Override
            protected Pair<Template, Stream<LearningSample>> merge(Stream<LearningSample> input1, Template input2) {
                Grounder grounder = new BottomUp();
                //TODO ground
                return null;
            }
        });

        mergeGround.output = register(new Pipe<Pair<Template,Stream<LearningSample>>, Results>("TrainingPipe") {
            @Override
            public Results apply(Pair<Template, Stream<LearningSample>> templateStreamPair) {
                //TODO train
                return null;
            }
        });

    }
}