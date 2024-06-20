package cz.cvut.fel.ida.neuralogic.cli.datasets.relational.planning;

import cz.cvut.fel.ida.neuralogic.cli.Main;
import cz.cvut.fel.ida.pipelines.Pipeline;
import cz.cvut.fel.ida.pipelines.utils.WorkflowUtils;
import cz.cvut.fel.ida.setup.Settings;
import cz.cvut.fel.ida.utils.generic.Benchmarking;
import cz.cvut.fel.ida.utils.generic.Pair;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.Utilities;

import java.time.Duration;

public class Planning {

    @TestAnnotations.Slow
    public void blocksworld() throws Exception {

        String dataset = "relational/planning/blocksworld";
        String[] args = Utilities.getDatasetArgs(dataset, "");

        Settings settings = Settings.forSlowTest();
        settings.shuffleBeforeTraining = false;

        Pair<Pipeline, ?> results = Main.main(args, settings);
    }
}
