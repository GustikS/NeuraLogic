package pipeline.prepared.full;

import building.SamplesBuilder;
import building.TemplateBuilder;
import com.sun.istack.internal.NotNull;
import constructs.example.LiftedExample;
import constructs.template.Template;
import grounding.Grounder;
import grounding.bottomUp.BottomUp;
import ida.utils.tuples.Pair;
import learning.LearningSample;
import neuralogic.examples.PlainExamplesParseTree;
import neuralogic.examples.PlainExamplesParseTreeExtractor;
import neuralogic.queries.PlainQueriesParseTree;
import neuralogic.template.PlainTemplateParseTree;
import pipeline.Branch;
import pipeline.Merge;
import pipeline.Pipe;
import pipeline.Pipeline;
import pipeline.prepared.pipes.IdentityGenPipe;
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

        Branch<Sources, PlainTemplateParseTree, Sources> sourceBrach = register(new Branch<Sources, PlainTemplateParseTree, Sources>("TemplateBranch") {
            @Override
            protected Pair<PlainTemplateParseTree, Sources> branch(Sources sources) {
                return new Pair<PlainTemplateParseTree, Sources>(sources.templateParseTree, sources);
            }
        });
        start.output = sourceBrach;
        sourceBrach.input = start;  //not necessary
        Pipe<PlainTemplateParseTree, Template> templateBuildingPipe;
        sourceBrach.output1 = register(templateBuildingPipe = new Pipe<PlainTemplateParseTree, Template>("TemplateBuildingPipe") {

            @Override
            public Template apply(PlainTemplateParseTree plainTemplateParseTree) {
                TemplateBuilder templateBuilder = new TemplateBuilder(settings);
                return templateBuilder.buildFrom(plainTemplateParseTree);
            }
        });

        Pipe<Template, Template> templateProcessingPipe;
        templateBuildingPipe.output = register(templateProcessingPipe = new TemplateProcessingPipe(settings, "TemplateProcessingPipe"));


        Branch<Sources, PlainExamplesParseTree, PlainQueriesParseTree> samplesBranch;

        sourceBrach.output2 = register(samplesBranch = new Branch<Sources, PlainExamplesParseTree, PlainQueriesParseTree>("SamplesBranch") {
            @Override
            protected Pair<PlainExamplesParseTree, PlainQueriesParseTree> branch(Sources sources) {
                return new Pair<PlainExamplesParseTree, PlainQueriesParseTree>(sources.trainExamplesParseTree, sources.trainQueriesParseTree);
            }
        });

        Pipe<PlainExamplesParseTree, Stream<LiftedExample>> exampleBuildingPipe;
        samplesBranch.output1 = register(exampleBuildingPipe = new Pipe<PlainExamplesParseTree, Stream<LiftedExample>>("ExampleBuildingPipe") {
            @Override
            public Stream<LiftedExample> apply(PlainExamplesParseTree plainExamplesParseTree) {
                PlainExamplesParseTreeExtractor plainExamplesParseTreeExtractor = new PlainExamplesParseTreeExtractor();
            }
        })


        Merge<PlainExamplesParseTree, PlainQueriesParseTree, Stream<LearningSample>> samplesMerge;
        samplesBranch.output2 = register(samplesMerge = new Pipe<PlainQueriesParseTree, Stream<LearningSample>>("QueriesProcessingPipe") {

            @Override
            public Stream<LearningSample> apply(PlainQueriesParseTree plainQueriesParseTree) {
                SamplesBuilder learningSamplesBuilder = new SamplesBuilder(settings);
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
        templateBuildingPipe.output = mergeGround;

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
