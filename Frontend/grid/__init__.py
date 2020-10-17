import os
from os import listdir

# static naming conventions
examples = "examples"
queries = "queries"
jarname = "NeuraLogic.jar"


# jarname = "/home/XXXXX/neuralogic/NeuraLogic.jar"


# %% experiment-specific setup = single file
class ExperimentSetup():

    def __init__(self, experiment_id, params="", template="",
                 dataset="jair/mutagenesis", memory_max="32g", walltime="48:00:00", template_per_dataset=False,
                 user="kuzelon2", python=False, python_env="pytorch14", python_script="run_experiment.py"):

        self.user = user
        self.experiment_id = experiment_id

        self.dataset = dataset
        self.dataset_id = dataset.replace("/", "_").replace(" ", "")

        self.python = python
        self.python_env = python_env
        self.python_script = python_script

        # template setup
        self.template = template
        if template:
            self.template_id = template.replace("/", "_").replace(" ", "")
        self.template_per_dataset = template_per_dataset

        # neuralogic params
        self.params = params
        self.params_id = params.replace("/", ".").replace(" ", "_")
        if self.params_id.startswith("-"):
            self.params_id = self.params_id[1:]

        # experiments parameters
        self.memory_min = "2g"
        self.memory_max = memory_max
        self.walltime = walltime

        self.scratch = "10mb"
        self.nodes = "1"
        self.tasks = "1"
        self.cpus = "1"

        self.server = ""  # only valid for Metacentrum
        self.partition = "XXX"  # only valid for RCI

        self.script_code = ""

        self.setup()
        self.finalize()

        self.script()

    def setup(self):
        print("ABSTRACT!!")

    def script(self):
        print("ABSTRACT!!")

    def finalize(self):
        # remote paths
        self.remote_path = self.server + "/home/" + self.user + "/neuralogic/"
        self.dataset_path_remote = self.remote_path + "datasets/" + self.dataset
        # self.jarpath_remote = self.remote_path + "experiments/" + self.experiment_id + "/artifacts/"
        self.root_path_remote = self.remote_path

        if self.template_per_dataset:
            self.template_path_remote = os.path.join(self.remote_path, "templates", self.dataset, self.template)
        else:
            self.template_path_remote = os.path.join(self.remote_path, "templates", self.template)

        self.export_path = os.path.join(self.remote_path, "experiments", self.experiment_id, "results", self.dataset,
                                        self.template_id, self.params_id)

    def finish_script(self):

        self.script_code += "cd " + self.root_path_remote + "\n"

        if self.python:
            self.script_code += "ml Anaconda3" + "\n"
            self.script_code += "source /mnt/appl/software/Anaconda3/2019.07/etc/profile.d/conda.sh" + "\n"  # this is because default use conda conda throws error in subshells
            self.script_code += "conda activate " + self.python_env + "\n"
            self.script_code += "python3 " + self.python_script + " -sd " + self.dataset_path_remote + self.params + " -out " + self.export_path
        else:
            decreased_memory = str(
                int(self.memory_max[0:self.memory_max.find("g")]) - 1) + "g"
            self.script_code += "java -XX:+UseSerialGC -XX:-BackgroundCompilation -XX:NewSize=2000m -Xms" + self.memory_min + " -Xmx" + decreased_memory + \
                                " -jar " + self.root_path_remote + jarname + " -sd " + self.dataset_path_remote + " -t " + self.template_path_remote + \
                                self.params + \
                                " -out " + self.export_path


class MetacentrumExperimentSetup(ExperimentSetup):

    def setup(self):
        self.server = "/storage/" + "praha1"  # always use just this metacentrum storage

    def script(self):
        self.script_code = "#!/bin/bash\n"
        self.script_code += "#PBS -l select=" + self.nodes + ":ncpus=" + self.cpus + ":mem=" + self.memory_max + ":scratch_local=" + self.scratch + "\n"
        self.script_code += "#PBS -l walltime=" + self.walltime + "\n\n"
        # self.script_code += "#PBS -q global@cerit-pbs.cerit-sc.cz\n\n"

        if not self.python:
            self.script_code += "module add jdk-8\n"
            self.script_code += "sleep 30\n"

        self.finish_script()


class RciExperimentSetup(ExperimentSetup):

    def setup(self):
        self.server = ""
        if "-" in self.walltime or self.walltime > "72:00:00":
            self.partition = "cpuextralong"
        elif "-" in self.walltime or self.walltime > "24:00:00":
            self.partition = "cpulong"
        elif self.walltime > "04:00:00":
            self.partition = "cpu"
        else:
            self.partition = "cpufast"

    def script(self):
        self.script_code = "#!/bin/bash\n"
        self.script_code += "#SBATCH --partition=" + self.partition + "\n"
        self.script_code += "#SBATCH --time=" + self.walltime + "\n"
        self.script_code += "#SBATCH --nodes=" + self.nodes + "\n"
        self.script_code += "#SBATCH --ntasks-per-node=" + self.tasks + "\n"
        self.script_code += "#SBATCH --cpus-per-task=" + self.cpus + "\n"
        self.script_code += "#SBATCH --mem=" + self.memory_max + "\n"

        if not self.python:
            self.script_code += "ml Java/1.8.0_202 \n"

        self.finish_script()


# %% parameter-range search setup

class GridSetup():
    # local paths
    local_datasets_path = "/home/gusta/data/datasets/"
    local_templates_path = "/home/gusta/data/templates/"
    local_output_path = "/home/gusta/data/experiments/"

    def __init__(self, experiment_id, param_ranges={}, datasets="jair", templates=[],
                 memory_max="32g", walltime="48:00:00", rci=False, template_per_dataset=True, user="souregus",
                 python=False, python_env="pytorch14", python_script="run_experiment.py"):

        self.user = user
        self.experiment_id = experiment_id
        self.output_path = self.local_output_path + self.experiment_id

        self.python = python
        self.python_env = "/home/" + user + "/neuralogic/anaconda/" + python_env
        self.python_script = python_script

        self.datasets, self.local_dataset_paths = self.load_datasets(datasets)
        self.template_per_dataset = template_per_dataset  # each dataset has own template?

        if templates:
            self.templates = self.load_templates(templates)
        else:
            self.templates = ["Default"]

        self.walltime = walltime
        self.memory_max = memory_max

        self.param_lists = []
        self.generate_grid(param_ranges, [], self.param_lists)

        # submit to rci or metacentrum?
        self.rci = rci

    def load_templates(self, templates):
        template_files = []
        if self.template_per_dataset:
            for dataset in self.datasets:
                for template in templates:
                    template_files.append(os.path.join(self.local_templates_path, dataset, template))
        else:
            if isinstance(templates, list):
                for template in templates:
                    template_files.append(os.path.join(template))
            else:
                for template in listdir(os.path.join(self.local_templates_path, templates)):
                    template_files.append(os.path.join(templates, template))
        return template_files

    def load_datasets(self, datasets):
        if isinstance(datasets, list):
            return datasets, [os.path.join(self.local_datasets_path, dataset) for dataset in datasets]
        dataset_list = []
        for dataset in listdir(os.path.join(self.local_datasets_path, datasets)):
            if not dataset.startswith("_"):
                dataset_list.append(os.path.join(datasets, dataset))
        return dataset_list, [os.path.join(self.local_datasets_path, dataset) for dataset in dataset_list]

    def generate_grid(self, param_ranges: {}, current_list, param_lists):
        if not param_ranges:
            param_lists.append("".join(current_list))
            return

        param_range = param_ranges.popitem()
        param = param_range[0]
        range = param_range[1]

        for val in range:
            current_list.insert(0, " -" + param + " " + str(val))
            self.generate_grid(param_ranges, current_list, param_lists)
            current_list.pop(0)
        param_ranges[param] = range

    def generate_experiments(self):
        experiments = []
        if self.template_per_dataset:
            for dataset in self.datasets:
                for template in self.templates:
                    for param_list in self.param_lists:
                        experiments.append(self.create_experiment(dataset, param_list, template))
        else:
            for dataset in self.datasets:
                for template in self.templates:
                    for param_list in self.param_lists:
                        experiments.append(self.create_experiment(dataset, param_list, template))
        return experiments

    def create_experiment(self, dataset, param_list, template):
        if self.rci:
            return RciExperimentSetup(self.experiment_id, param_list,
                                      template, dataset,
                                      self.memory_max, self.walltime,
                                      self.template_per_dataset,
                                      self.user,
                                      self.python,
                                      self.python_env,
                                      self.python_script)
        else:
            return MetacentrumExperimentSetup(self.experiment_id, param_list,
                                              template, dataset,
                                              self.memory_max, self.walltime,
                                              self.template_per_dataset,
                                              self.user,
                                              self.python,
                                              self.python_env,
                                              self.python_script)

    def export_experiments(self, experiments: [ExperimentSetup]):
        script_files = []
        for experiment in experiments:

            directory = os.path.join(self.output_path, "scripts")

            if not os.path.exists(directory):
                os.makedirs(directory)

            script_path = os.path.join(directory,
                                       experiment.dataset_id + "_" + experiment.template_id + "_" + experiment.params_id + ".sh")

            with open(script_path, 'w') as f:
                f.write(experiment.script_code)

            script_files.append(
                os.path.join(experiment.remote_path, "experiments", experiment.experiment_id, "scripts",
                             experiment.dataset_id + "_" + experiment.template_id + "_" + experiment.params_id + ".sh"))

        if self.rci:
            cmd = "sbatch "
        else:
            cmd = "qsub "

        gridlist = []
        for script in script_files:
            gridlist.append(cmd + script)

        with open(os.path.join(self.output_path, "scripts", "200__grid.sh"), 'a') as f:
            f.write("\n".join(gridlist))
            f.write("\n")

        dummy_name = self.dummy_experiment(directory, experiment)

        with open(os.path.join(self.output_path, "scripts", "000__dummytest.sh"), 'a') as f:
            f.write(cmd + os.path.join(experiment.remote_path, "experiments", experiment.experiment_id, "scripts",
                                       dummy_name))
            f.write("\n")

    def dummy_experiment(self, directory, experiment):
        experiment.memory_max = "20g"
        experiment.partition = "cpu"
        experiment.walltime = "00:10:00"
        experiment.params += " -ts 10"
        experiment.params += " -limit 10 "
        experiment.export_path += "_dummy"
        experiment.script()

        dummy_name = "100__" + experiment.dataset_id + "_" + experiment.template_id + "_" + experiment.params_id + ".sh"
        dummy_path = os.path.join(directory, dummy_name)
        with open(dummy_path, 'w') as f:
            f.write(experiment.script_code)
        return dummy_name
