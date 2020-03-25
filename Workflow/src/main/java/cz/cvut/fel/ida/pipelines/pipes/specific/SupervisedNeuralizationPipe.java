package cz.cvut.fel.ida.pipelines.pipes.specific;

import cz.cvut.fel.ida.logic.grounding.GroundTemplate;
import cz.cvut.fel.ida.logic.grounding.GroundingSample;
import cz.cvut.fel.ida.neural.networks.structure.building.NeuralProcessingSample;
import cz.cvut.fel.ida.neural.networks.structure.building.Neuralizer;
import cz.cvut.fel.ida.neural.networks.structure.components.types.DetailedNetwork;
import cz.cvut.fel.ida.pipelines.Pipe;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.util.List;
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
        } else {
            return groundingSampleStream
                    .peek(s -> LOG.info("Neuralizing sample " + s.toString()))
                    .map(sample -> neuralizer.neuralize(sample).stream())
                    .flatMap(f -> f)
                    .peek(s -> LOG.info("NeuralNet created: " + s.toString()));
        }
    }
}
