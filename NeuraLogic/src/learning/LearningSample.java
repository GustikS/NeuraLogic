package learning;

import networks.evaluation.values.Value;

public interface LearningSample {
    //TODO should learning samples contain reference to Model?

    Double getImportance();
    String getId();
    Value getTarget();
    Query getQuery();

    default Value getValue(Model model) {
        return getQuery().evaluate(model);
    }
}