from grid import *

# %%

grid = GridSetup(experiment_id="grid_test",
                 param_ranges={"iso": [12], "opt": ["sgd"], "lr":[0.3], "xval":[5]},
                 datasets="jair",
                 templates=["template_vector_cross"],
                 walltime="10:00:00",
                 memory_max="8g",
                 rci=True)

experiments = grid.generate_experiments()
grid.export_experiments(experiments)