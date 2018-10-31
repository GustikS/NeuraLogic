package networks.computation.evaluation.results;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class Progress {
    List<Results> currentProgress;

    List<List<Results>> restarts;

    public Progress(){
        currentProgress = new LinkedList<>();
    }

    public void nextResult(Results next){
        currentProgress.get(currentProgress.size()-1).outputs = null; //delete the particular outputs from the past Results to save space (store only statistics)
        currentProgress.add(next);
    }

    public void nextRestart(){
        restarts.add(currentProgress);
        currentProgress = new LinkedList<>();
    }
}