import os
from os import listdir

# static naming conventions
examples = "examples"
queries = "queries"
jarname = "NeuraLogic.jar"

# %% experiment-specific setup

experiment_id = "jair_revived"

# locally
datasets_path = '/home/gusta/data/jair'
output_path = "/home/gusta/data/metacentrum/" + experiment_id

# remotely
server = "praha1"
remote_path = "/home/souregus/neuralogic/"
datasets_path_remote = remote_path + "datasets/jair"
jarpath_remote = remote_path + "experiments/" + experiment_id + "/artifacts/"

# experiments parameters
memory_min = "4g"
memory_max = "32g"
walltime = "48:00:00"

memory = memory_max + "b"
scratch = "50mb"
nodes = "1"
tasks = "1"
cpus = "1"
partition = "longjobs"

# template setup
template_file = "template_vector_cross"
template_id = template_file

# neuralogic params
params = "-opt sgd -lr 0.3 -iso 12 -xval 5"

# %% datasets

datasets = []

for dataset in listdir(datasets_path):
    datasets.append(dataset)

# %% individual scripts

scripts = []

params_id = params.replace("-", "_").replace(".", "_").replace(" ", "")

for dataset in datasets:

    template_path = os.path.join(remote_path, "experiments", experiment_id, "templates", dataset, template_file)
    examples_path = os.path.join(datasets_path_remote, dataset, examples)
    queries_path = os.path.join(datasets_path_remote, dataset, queries)

    export_path = os.path.join(remote_path, "experiments", experiment_id, "results", dataset, template_file, params_id)

    script = "#!/bin/bash\n"
    script += "#SBATCH --partition=" + partition + "\n"
    script += "#SBATCH --time=" + walltime + "\n"

    script += "#SBATCH --nodes=" + nodes + "\n"
    script += "#SBATCH --ntasks-per-node=" + tasks + "\n"
    script += "#SBATCH --cpus-per-task=" + cpus + "\n"

    script += "#SBATCH --mem=" + memory_max + "\n"

    script += "cd " + jarpath_remote + "\n"
    script += "ml Java/1.8.0_202 \n"

    script += "java -XX:+UseSerialGC -XX:-BackgroundCompilation -XX:NewSize=2000m -Xms" + memory_min + " -Xmx" + memory_max + \
              " -jar " + jarname + " -t " + template_path + " -e " + examples_path + " -q " + queries_path + \
              " -out " + export_path + " " \
              + params

    directory = os.path.join(output_path, "scripts", template_id, params_id)
    if not os.path.exists(directory):
        os.makedirs(directory)

    script_path = os.path.join(directory, dataset + "_" + template_file + "_" + params_id + "_script.sh")
    with open(script_path, 'w') as f:
        f.write(script)

    scripts.append(os.path.join(remote_path, "experiments", experiment_id, "scripts", template_id, params_id,
                                dataset + "_" + template_file + "_" + params_id + "_script.sh"))

# %% the main qsub script

qsub = []

for script in scripts:
    qsub.append(
        "sbatch " + script
    )

with open(os.path.join(output_path, "scripts", template_id, params_id, "000__qsub.sh"), 'w') as f:
    f.write("\n".join(qsub))
