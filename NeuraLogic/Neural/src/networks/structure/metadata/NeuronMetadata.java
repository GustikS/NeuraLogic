package networks.structure.metadata;

import ida.utils.tuples.Pair;
import networks.structure.WeightedNeuron;
import networks.structure.Weight;

import java.util.ArrayList;

/**
 * Object to store any extra info (not necessarily present in all neurons) to save space.
 * Metadata is a single Null if there's nothing (true for majority of neurons).
 */
public class NeuronMetadata {
    //todo add embedding expansion info
    //activation function?

    ArrayList<Pair<WeightedNeuron,Weight>> parents;
}