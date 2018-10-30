package networks.computation.evaluation.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class Progress {
    List<Results> progress;

    public Progress(){
        progress = new ArrayList<>();
    }

    public void addNext(Results next){
        progress.get(progress.size()-1).outputs = null; //delete the particular outputs from the past Results to save space (store only statistics)
        progress.add(next);
    }
}