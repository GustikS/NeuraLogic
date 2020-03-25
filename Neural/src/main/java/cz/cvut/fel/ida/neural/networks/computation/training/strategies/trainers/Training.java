package cz.cvut.fel.ida.neural.networks.computation.training.strategies.trainers;

import cz.cvut.fel.ida.neural.networks.computation.training.strategies.debugging.NeuralDebugging;
import cz.cvut.fel.ida.utils.exporting.Exportable;

public interface Training extends Exportable {

    void setupDebugger(NeuralDebugging trainingDebugger);
}
