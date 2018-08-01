package learning;

import networks.evaluation.values.Value;

public interface LearningSample {
    //should learning samples contain reference to Model - probably not (Structure learning)

    Double getImportance();
    String getId();
    Value getTarget();
    Query getQuery();
    Example getExample();

    default Value getValue(Model model) {
        return getQuery().evaluate(model);
    }
}