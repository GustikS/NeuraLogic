package cz.cvut.fel.ida.neural.networks.structure.metadata;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;

import java.util.ArrayList;

/**
 * Object to store any extra info (not necessarily present in all neurons) to save space.
 * Metadata is a single Null if there's nothing (true for majority of neurons).
 */
@Deprecated
public class NeuronMetadata {
    //todo add embedding expansion info
    //activation function?

    ArrayList<Pair<WeightedNeuron, Weight>> parents;
}