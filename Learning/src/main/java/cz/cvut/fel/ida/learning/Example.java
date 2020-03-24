package cz.cvut.fel.ida.learning;

import cz.cvut.fel.ida.utils.exporting.Exportable;

/**
 * Created by gusta on 25.3.17.
 */
public interface Example extends Exportable {
    String getId();
    Integer getNeuronCount();
}