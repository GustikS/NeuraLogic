package networks.structure.lrnnTypes;

import networks.evaluation.values.Value;
import networks.structure.Weight;

/**
 * Useless interface, duplicate for Neron - needed as just a formal grouping for Atom and Fact neurons (which are pretty much the same)
 * todo - move to separate super-interface common to Neuron and this
 */
public interface AtomFact {

    Value evaluate();

    Weight getOffset();

    Value gradient();

    String getId();
}