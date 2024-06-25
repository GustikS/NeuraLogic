package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.logic.constructs.example.LiftedExample;
import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.building.Neuralizer;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Utilities;
import cz.cvut.fel.ida.utils.math.collections.MultiList;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SupervisedNeuralizationPipe extends Pipe<Stream<GroundingSample>, Stream<NeuralProcessingSample>> {
    private static final Logger LOG = Logger.getLogger(SupervisedNeuralizationPipe.class.getName());
    private Neuralizer neuralizer;

    public SupervisedNeuralizationPipe(Settings settings, Neuralizer neuralizer) {
        super("SupervisedNeuralizationPipe", settings);
        this.neuralizer = neuralizer;
    }

    @Override
    public Stream<NeuralProcessingSample> apply(Stream<GroundingSample> groundingSampleStream) throws Exception {
        if (settings.groundingMode == Settings.GroundingMode.GLOBAL) {
            List<GroundingSample> groundingSamples = Utilities.terminateSampleStream(groundingSampleStream);
            GroundTemplate groundTemplate = groundingSamples.get(0).groundingWrap.getGroundTemplate();
            LOG.info("Neuralizing GLOBAL sample " + groundTemplate.toString());
            List<NeuralProcessingSample> neuralizedSamples = neuralizer.neuralize(groundTemplate, groundingSamples);
            DetailedNetwork detailedNetwork = neuralizedSamples.get(0).detailedNetwork;
            LOG.info("GLOBAL NeuralNet created: " + detailedNetwork.toString());
            return neuralizedSamples.stream();
        } else if (!settings.oneQueryPerExample) {
            List<GroundingSample> groundingSamples = Utilities.terminateSampleStream(groundingSampleStream);
            List<NeuralProcessingSample> allSamples = new LinkedList<>();
            MultiList<GroundTemplate, GroundingSample> sampleMap = new MultiList<>();
            for (GroundingSample groundingSample : groundingSamples) {  // merge samples with the same example/grounding
                sampleMap.put(groundingSample.groundingWrap.getGroundTemplate(), groundingSample);
            }
            for (Map.Entry<GroundTemplate, List<GroundingSample>> entry : sampleMap.entrySet()) {
                GroundTemplate groundTemplate = entry.getKey();
                List<GroundingSample> samples = entry.getValue();
                LOG.info("Neuralizing sample with multiple queries " + groundTemplate.toString());
                List<NeuralProcessingSample> neuralizedSamples = neuralizer.neuralize(groundTemplate, samples);
                LOG.info("SHARED NeuralNet created: " + neuralizedSamples.get(0).detailedNetwork.toString());
                allSamples.addAll(neuralizedSamples);
            }
            return allSamples.stream();
        } else {
            return groundingSampleStream
                    .peek(s -> LOG.info("Neuralizing sample " + s.toString()))
                    .flatMap(sample -> neuralizer.neuralize(sample).stream())
                    .peek(s -> LOG.info("NeuralNet created: " + s.toString()));
        }
    }
}
