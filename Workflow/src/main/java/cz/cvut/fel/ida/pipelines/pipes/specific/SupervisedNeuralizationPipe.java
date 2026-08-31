package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.building.Neuralizer;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static cz.cvut.fel.ida.pipelines.utils.WorkflowUtils.consecutiveGroupsIterator;

public class SupervisedNeuralizationPipe extends Pipe<Stream<GroundingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(SupervisedNeuralizationPipe.class.getName());
    private Neuralizer neuralizer;

    private final SampleProgressLog progress;

    public SupervisedNeuralizationPipe(Settings settings, Neuralizer neuralizer) {
        super("SupervisedNeuralizationPipe", settings);
        this.progress = new SampleProgressLog(LOG, settings, "Neuralization", "neurons");
        this.neuralizer = neuralizer;
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<GroundingSample> groundingSampleStream) throws Exception {
        if (settings.groundingMode == Settings.GroundingMode.GLOBAL) {
            List<GroundingSample> groundingSamples = Utilities.terminateSampleStream(groundingSampleStream);
            GroundTemplate groundTemplate = groundingSamples.get(0).groundingWrap.getGroundTemplate();
            List<NeuralProcessingSample> neuralizedSamples = neuralizer.neuralize(groundTemplate, groundingSamples);
            DetailedNetwork detailedNetwork = neuralizedSamples.get(0).detailedNetwork;
            progress.sample(() -> "GLOBAL " + detailedNetwork, detailedNetwork.getNeuronCount());
            return neuralizedSamples.stream();
        } else if (!settings.oneQueryPerExample) {
            Stream<List<GroundingSample>> groupStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(consecutiveGroupsIterator(groundingSampleStream.iterator(), a -> a.groundingWrap.getGroundTemplate()), Spliterator.ORDERED), false);
            Stream<NeuralProcessingSample> flatStream = groupStream.flatMap(list -> {
                if (list.isEmpty()) {
                    return Stream.empty();
                }

                GroundTemplate groundTemplate = list.get(0).groundingWrap.getGroundTemplate();
                List<NeuralProcessingSample> neuralizedSamples = neuralizer.neuralize(groundTemplate, list);
                DetailedNetwork shared = neuralizedSamples.get(0).detailedNetwork;
                progress.sample(() -> "shared over " + list.size() + " queries: " + shared, shared.getNeuronCount());

                return neuralizedSamples.stream();
            });

            return flatStream.onClose(progress::summary);
        } else {
            return groundingSampleStream
                    .flatMap(sample -> neuralizer.neuralize(sample).stream())
                    .peek(s -> progress.sample(s::toString, s.detailedNetwork.getNeuronCount()))
                    .onClose(progress::summary);
        }
    }
}
