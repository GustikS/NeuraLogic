package pipelines.bulding;

import constructs.template.Template;
import constructs.template.templates.GraphTemplate;
import grounding.GroundTemplate;
import grounding.Grounder;
import ida.utils.tuples.Pair;
import constructs.example.LogicSample;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.IdentityGenPipe;
import settings.Settings;
import training.NeuralSample;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class GroundingBuilder extends AbstractPipelineBuilder<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> {
    private static final Logger LOG = Logger.getLogger(GroundingBuilder.class.getName());

    Grounder grounder;

    public GroundingBuilder(Settings settings) {
        super(settings);
        grounder = Grounder.getGrounder(settings);
    }

    @Override
    public Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> buildPipeline() {
        Pipeline<Pair<Template, Stream<LogicSample>>, Stream<NeuralSample>> pipeline = new Pipeline<>("GroundingPipeline");

        Pipe<Pair<Template, Stream<LogicSample>>, Stream<Pair<Template, LogicSample>>> templateReducing;
        if (settings.supervisedTemplateGraphPruning) {
            templateReducing = pipeline.registerStart(new Pipe<Pair<Template, Stream<LogicSample>>, Stream<Pair<Template, LogicSample>>>("SupervisedTemplateReducing") {
                @Override
                public Stream<Pair<Template, LogicSample>> apply(Pair<Template, Stream<LogicSample>> templateSamples) {
                    return templateSamples.s.map(sample -> new Pair<>(templateSamples.r.prune(sample.query), sample));
                }
            });
        } else {
            templateReducing = pipeline.registerStart(new Pipe<Pair<Template, Stream<LogicSample>>, Stream<Pair<Template, LogicSample>>>("SimpleTemplateSamplePairing") {
                @Override
                public Stream<Pair<Template, LogicSample>> apply(Pair<Template, Stream<LogicSample>> templateSamples) {
                    return templateSamples.s.map(sample -> new Pair<>(templateSamples.r, sample));
                }
            });
        }

        Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<Template, LogicSample>>> parallelPipe;
        if (settings.parallelGrounding) {
             parallelPipe = pipeline.register(new Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<Template, LogicSample>>>("ParallelizationPipe") {
                @Override
                public Stream<Pair<Template, LogicSample>> apply(Stream<Pair<Template, LogicSample>> pairStream) {
                    return pairStream.parallel();
                }
            });
            templateReducing.connectAfter(parallelPipe);
        } else {
            parallelPipe = new IdentityGenPipe<>();
        }

        Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>> groundingPipe = pipeline.register(new Pipe<Stream<Pair<Template, LogicSample>>, Stream<Pair<LogicSample, GroundTemplate>>>("GroundingPipe") {
            @Override
            public Stream<Pair<LogicSample, GroundTemplate>> apply(Stream<Pair<Template, LogicSample>> pairStream) {
                return pairStream.map(pair -> new Pair<>(pair.s, grounder.groundRulesAndFacts(pair.s.query.evidence, pair.r)));
            }
        });
        parallelPipe.connectAfter(groundingPipe);

        if (settings.supervisedGroundTemplatePruning) {
            Pipe<Stream<Pair<LogicSample, GroundTemplate>>, Stream<Pair<LogicSample, GroundTemplate>>> groundReducingPipe = new Pipe<Stream<Pair<LogicSample, GroundTemplate>>, Stream<Pair<LogicSample, GroundTemplate>>>("SupervisedGroundReducingPipe") {
                @Override
                public Stream<Pair<LogicSample, GroundTemplate>> apply(Stream<Pair<LogicSample, GroundTemplate>> pairStream) {
                    return pairStream.map(p -> new Pair<>(p.r, p.s.prune(p.r.query)));
                }
            };
            groundingPipe.connectAfter(groundReducingPipe);
        }

        //todo next

        if (settings.sequentialGrounding) {

            if (settings.sharedGroundings) {

            }
            //todo poresit shared a sequential groundings zde na urovni tvorby pipeline
        } else {

        }
        return null;
    }

}
