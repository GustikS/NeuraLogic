package networks.computation.evaluation.results;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gusta on 8.3.17.
 */
public class Progress {

    Restart currentRestart;

    List<Restart> restarts;

    public Results bestResults;

    public Progress(){
        currentRestart = new Restart();
        restarts = new LinkedList<>();
    }

    public void addOnlineResults(Results next){
        currentRestart.onlineResults.get(currentRestart.onlineResults.size()-1).evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        currentRestart.onlineResults.add(next);
    }

    public void addTrueResults(Results next){
        currentRestart.trueResults.get(currentRestart.trueResults.size()-1).evaluations = null; //delete the particular outputs from the past Results to save space (store only statistics)
        currentRestart.trueResults.add(next);
    }

    public void nextRestart(){
        restarts.add(currentRestart);
        currentRestart = new Restart();
    }

    public Results getCurrentOnlineResults() {
        return currentRestart.onlineResults.get(currentRestart.onlineResults.size()-1);
    }

    public Results getLastTrueResults() {
        return currentRestart.trueResults.get(currentRestart.trueResults.size()-1);
    }

    private class Restart {
        List<Results> onlineResults = new LinkedList<>();
        List<Results> trueResults = new LinkedList<>();
    }
}