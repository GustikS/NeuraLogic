from grid import *

# %%

grid = GridSetup(experiment_id="iso_kinships",
                 param_ranges={"iso": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14], "opt": ["adam"], "lr": [0.01],
                               "xval": [5], "ts": [1000]},
                 datasets=["KB/kinships"],
                 templates=["KB/kinships/template_embeddings"],
                 walltime="20:00:00",
                 memory_max="20g",
                 rci=True,
                 template_per_dataset=False)

experiments = grid.generate_experiments()
grid.export_experiments(experiments)

# %%

# grid = GridSetup(experiment_id="neuralogic_lrnn",
#                  param_ranges={"prune": [-1], "iso": [-1], "opt": ["sgd"], "lr": [0.3], "ts": [3000], "xval": [5]},
#                  datasets="jair",
#                  templates=["jair/template_unified_bad"],
#                  walltime="320:00:00",
#                  memory_max="100g",
#                  rci=False,
#                  template_per_dataset=False)
#
# experiments = grid.generate_experiments()
# grid.export_experiments(experiments)
