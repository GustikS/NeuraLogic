package pipelines;

import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class RecurrentPipe<I1,I2,O1,O2,T> extends Block {
    private static final Logger LOG = Logger.getLogger(RecurrentPipe.class.getName());

    public ConnectAfter<I1> initInput;
    public ConnectAfter<I2> recurrentInput;
    public ConnectBefore<O1> finalOutput;
    public ConnectBefore<O2> recurrentOutput;

    /**
     * Test on this condition to switch to final output
     */
    Predicate<T> condition;
}
