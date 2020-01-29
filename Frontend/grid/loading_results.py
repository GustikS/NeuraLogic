import json
import os
import statistics
from datetime import timedelta

import isodate as isodate
import numpy as np
import pandas as pd
from os import listdir
from os.path import isfile

import matplotlib.pyplot as plt

# todo - direct sftp https://stackoverflow.com/questions/432385/sftp-in-python-platform-independent
# with https://pypi.org/project/pysftp/
from pandas.core.dtypes.common import is_numeric_dtype

results_path = '/home/gusta/data/results/iso'


class ExperimentResults:

    def __init__(self, id, dataset, template, params, jsons):
        self.id = id
        self.dataset = dataset
        self.template = template
        self.params = params
        self.jsons = jsons

        self.records = self.load(jsons)

    def load(self, jsons):
        records = {}

        for file in jsons:
            with open(file) as f:
                try:
                    loaded = json.load(f)
                except:
                    continue
                fname = file.split("/")[-1].split(".")[0]
                records[fname] = loaded
        return records


class Loader:

    def __init__(self, id, results_path, filter_pos=[], filter_neg=[]):
        self.results_path = results_path
        self.id = id

        self.filter_neg = filter_neg
        self.filter_pos = filter_pos

    def load_dataframe(self, metrics, posprocess=True):
        experiments = self.load_experiments()
        filter = Filter(metrics)
        results = filter.filter(experiments)

        if posprocess:
            filter.postprocess(results)

        data = pd.DataFrame.from_dict(results, orient='index')
        data.sort_index(inplace=True)
        return data

    def get_jsons(self, path, files, filter_pos=[], filter_neg=[]):
        for d in listdir(path):
            if d in filter_neg:
                continue
            filename = os.path.join(path, d)
            if isfile(filename):
                if not filter_pos or d in filter_pos:
                    if filename.endswith(".json"):
                        files.append(filename)
            else:
                self.get_jsons(filename, files, filter_pos, filter_neg)

    def load_experiments(self):
        jsons = []
        self.get_jsons(self.results_path, jsons, self.filter_pos, self.filter_neg)

        json_map = {}
        for file in jsons:
            key = str(file).split("/")[1:-1]
            if key[-1] == "export":
                key = key[:-1]
            key = tuple(key)
            jsons = json_map.get(key, [])
            jsons.append(file)
            json_map[key] = jsons

        experiments = []
        for path, results in json_map.items():
            params = path[-1]
            template = path[-2]
            dataset = path[-3]
            if "dummy" in params:
                continue
            experiments.append(ExperimentResults(self.id, dataset, template, params, results))

        return experiments


class Filter:
    convert = {
        "train_acc": ["NeuralTrainTestPipeline", "training", "bestResults", "training", "bestAccuracy"],
        "test_acc": ["NeuralTrainTestPipeline", "testing", "bestAccuracy"],
        "xval_train_acc": ["CrossvalidationPipeline", "training", "bestResults", "training", "bestAccuracy"],
        "xval_test_acc": ["CrossvalidationPipeline", "testing", "bestAccuracy"],
        "compression": [["CompressionPipe", "allNeuronCount"],
                        ["CompressionPipe", "compressedNeuronCount"],
                        ["CompressionPipe", "timing", "totalTimeTaken"]],
        "pruning": [["NetworkPruningPipe", "allNeurons"],
                    ["NetworkPruningPipe", "prunedNeurons"],
                    ["NetworkPruningPipe", "timing", "totalTimeTaken"]],
        "train_time": ["NeuralTrainingPipe", "timing", "totalTimeTaken"]
    }

    def __init__(self, metrics):
        self.metrics = metrics
        self.metrics_udpated = []
        self.fields = []

        for metric in metrics:
            convert = self.convert[metric]
            flatten = []
            if isinstance(convert[0], list):
                for sslist in convert:
                    self.fields.append(sslist)
                    self.metrics_udpated.append(metric + "_" + sslist[-1])
            else:
                self.fields.append(convert)
                self.metrics_udpated.append(metric)

    def filter(self, experiments):
        results = {}
        exp_key = 0
        for experiment in experiments:
            base_cols = {"experiment": experiment.id, "dataset": experiment.dataset, "template": experiment.template}
            param_cols = self.parse_params(experiment.params)

            columns = {**base_cols, **param_cols}

            for path, metric in zip(self.fields, self.metrics_udpated):
                recs = experiment.records
                recs = self.extract(path, recs)
                columns[metric] = recs

            results[exp_key] = columns
            exp_key += 1

        return results

    def extract(self, path, recs):
        if not path:
            return recs
        if isinstance(recs, list):
            extracted = [self.extract(path, fold) for fold in recs]
        else:
            field = path.pop(0)
            try:
                next = recs[field]
                extracted = self.extract(path, next)
            except:
                extracted = None
            path.insert(0, field)
        return extracted

    def parse_params(self, paramstring):
        par_map = {}

        pairs = paramstring.split("-")
        for pair in pairs:
            try:
                par, val = pair.split("_")
                # if val == "dummy":
                #     continue
            except:
                par = "params"
                res_pars = par_map.get(par, [])
                res_pars.append(pair)
                val = res_pars
            par_map[par] = val
        return par_map

    def postprocess(self, results):
        for k, v in results.items():
            if isinstance(v, list):
                agg_v = self.aggregate(v)
                results[k] = agg_v
            elif isinstance(v, dict):
                self.postprocess(v)

    def aggregate(self, folds):
        try:
            avg, std = self.agg_stats(folds)
        except:
            try:
                folds = [isodate.parse_duration(fold).total_seconds() for fold in folds]
                avg, std = self.agg_stats(folds)
                # avg = str(timedelta(seconds=avg))
                # std = str(timedelta(seconds=std))
                return f'{avg:9.2f} +- {std:f}'
            except:
                pass
            return folds
        return f'{avg:9.2f} +- {std:f}'

    def agg_stats(self, folds):
        avg = statistics.mean(folds)
        std = statistics.stdev(folds)
        return avg, std


class Plotter:

    def __init__(self, dataframe: pd.DataFrame, x=None, ys=None):
        if x is None:
            x = range(len(dataframe.index))
        self.x = x
        if ys is None:
            ys = list(dataframe.columns.values)
        self.ys = ys
        self.dataframe = dataframe

    def plot_all(self, row=None, cols=None, xlabel="", ylabel="", legend=""):
        if row is None:
            row = self.x
        if cols is None:
            cols = self.ys

        good_cols = []
        for col in cols:
            if is_numeric_dtype(self.dataframe[col]):
                good_cols.append(col)
            elif "+-" in self.dataframe[col][0]:
                good_cols.append(col)

        sidex = sidey = int(np.sqrt(len(good_cols)))
        if sidex ** 2 < len(good_cols):
            sidex += int((len(good_cols) - sidex ** 2) / sidex) + 1

        fig, axs = plt.subplots(sidex, sidey)
        fig.set_size_inches(20, 11)
        axes = axs.ravel()

        for i, col in enumerate(good_cols):
            ax = axes[i]
            leg = legend if legend else col
            col = self.dataframe[col]
            if is_numeric_dtype(col):
                self.normal_plot(col, row, ax, xlabel=xlabel, ylabel=ylabel, legend=leg)
            else:
                try:
                    self.conf_plot(col, row, ax, xlabel=xlabel, ylabel=ylabel, legend=leg)
                except:
                    pass

        plt.gcf().tight_layout()
        plt.show()

    def normal_plot(self, y, x=None, axes=None, color='b', xlabel="", ylabel="", legend=""):
        if axes == None:
            axes = plt.gca()
        if x is None:
            x = self.x

        axes.plot(x, y, color, label=legend, linewidth=2)
        axes.set_xlabel(xlabel)
        axes.set_ylabel(ylabel)
        axes.set_title(legend)
        axes.legend()
        axes.grid()

    def conf_plot(self, plusminus, x=None, axes=None, color='b', xlabel="", ylabel="", legend=""):
        if axes == None:
            axes = plt.gca()
        if x is None:
            x = self.x
        if "+-" in plusminus[0]:
            tuples = [res.split("+-") for res in plusminus]
            means = np.array([float(tuple[0]) for tuple in tuples])
            stds = np.array([float(tuple[1]) for tuple in tuples])
        elif isinstance(plusminus[0], list):
            # unprocessed folds
            return
        else:
            return

        ub = means + stds
        lb = means - stds

        axes.fill_between(x, ub, lb, color=color, alpha=.3)
        # plot the mean on top
        axes.plot(x, means, color, label=legend)
        axes.set_xlabel(xlabel)
        axes.set_ylabel(ylabel)
        axes.legend()
        axes.set_title(legend)
        axes.grid()


# %%

metrics = ["train_acc", "test_acc", "xval_train_acc", "xval_test_acc", "pruning", "compression", "train_time"]

experiments_id = "icml_gnns"
experiments_id = "icml_lrnn"
# experiments_id = "icml_KB"

loader = Loader(experiments_id, os.path.join(results_path, experiments_id))
data = loader.load_dataframe(metrics)

# %% REPAIR

params = data['params'].to_list()
iso = [par[1].replace("iso", "") for par in params]
iso = [int(i) for i in iso]
sort_index = np.argsort(iso)

data = data.reindex(sort_index)
iso = [iso[i] for i in sort_index]

pltr = Plotter(data)
pltr.plot_all()

# %%

pltr.conf_plot(data['train_acc'],color='r',legend="train")
pltr.conf_plot(data['test_acc'],color='b',legend="test", ylabel="accuracy", xlabel="digits")
plt.show()

# pltr = Plotter(data)
# pltr.conf_plot(data['compression_compressedNeuronCount'], xlabel="digits", ylabel="neurons", legend="reduced",
#                color="b")
# pltr.conf_plot(data['compression_allNeuronCount'], xlabel="digits", ylabel="all", legend="all", color="r")
#
# plt.show()
