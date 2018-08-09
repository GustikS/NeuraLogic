package learning.crossvalidation;

import com.sun.istack.internal.Nullable;
import learning.LearningSample;

import java.util.List;
import java.util.logging.Logger;

public class Fold<T extends LearningSample> {
    private static final Logger LOG = Logger.getLogger(Fold.class.getName());

    int id;
    public List<T> train;
    public List<T> test;

    @Nullable
    TrainTestResults results;

    public Fold(int id){
        this.id = id;
    }
}