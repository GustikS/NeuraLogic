import json
import os
import statistics
import sys
import time
from datetime import timedelta

import isodate as isodate
import numpy as np
import pandas as pd
from os import listdir
from os.path import isfile

import matplotlib
import matplotlib.pyplot as plt

from matplotlib.font_manager import FontProperties

from pandas.core.dtypes.common import is_numeric_dtype

# todo make plot drawing more efficient by just updating the plots data instead of new plot

# %%

convert = {
    "train_acc": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "bestAccuracy"],
                  ["NeuralTrainingPipe", "progress", "bestResults", "training", "bestAccuracy"]
                  ],
    "train_disp": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "dispersion"],
                   ["NeuralTrainingPipe", "progress", "bestResults", "training", "dispersion"]
                   ],
    "train_aucroc": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "AUCroc"],
                     ["NeuralTrainingPipe", "progress", "bestResults", "training", "AUCroc"]
                     ],
    "train_aucpr": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "AUCpr"],
                    ["NeuralTrainingPipe", "progress", "bestResults", "training", "AUCpr"]
                    ],

    "test_acc": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "bestAccuracy"],
                 ["NeuralTrainingPipe", "progress", "bestResults", "training", "bestAccuracy"]
                 ],
    "test_disp": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "dispersion"],
                  ["NeuralTrainingPipe", "progress", "bestResults", "training", "dispersion"]
                  ],
    "test_aucroc": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "AUCroc"],
                    ["NeuralTrainingPipe", "progress", "bestResults", "training", "AUCroc"]
                    ],
    "test_aucpr": [["NeuralTrainTestPipeline", "training", "bestResults", "training", "AUCpr"],
                   ["NeuralTrainingPipe", "progress", "bestResults", "training", "AUCpr"]
                   ],

    "test_acc": ["NeuralTrainTestPipeline", "testing", "bestAccuracy"],
    "xval_train_acc": ["CrossvalidationPipeline", "training", "bestResults", "training", "bestAccuracy"],
    "xval_test_acc": ["CrossvalidationPipeline", "testing", "bestAccuracy"],
    "compression": [["CompressionPipe", "allNeuronCount"],
                    ["CompressionPipe", "compressedNeuronCount"],
                    ["CompressionPipe", "preventedByIsoCheck"],
                    ["CompressionPipe", "timing", "totalTimeTaken"]],
    "pruning": [["NetworkPruningPipe", "allNeurons"],
                ["NetworkPruningPipe", "prunedNeurons"],
                ["NetworkPruningPipe", "timing", "totalTimeTaken"]],
    "train_time": ["NeuralTrainingPipe", "timing", "totalTimeTaken"],
    "progress_train": [
        ["accuracy"],
        ["majorityErr"],
        ["dispersion"],
        ["bestAccuracy"],
        ["error", "value"],
        ["AUCroc"],
        ["AUCpr"]
    ],
    "progress_val": [
        ["validation", "accuracy"],
        ["validation", "majorityErr"],
        ["validation", "dispersion"],
        ["validation", "bestAccuracy"],
        ["validation", "error", "value"],
        ["validation", "AUCroc"],
        ["validation", "AUCpr"]
    ]
}


# %%

class ExperimentResults:

    def __init__(self, id, dataset, template, params, records):
        self.id = id
        self.dataset = dataset
        self.template = template
        self.params = params
        self.records = records


class Loader:

    def __init__(self, id, results_path=None, login=None, filter_pos="", filter_neg="training", split_std=False):
        self.sftp = None
        if login:
            results_path = login[0]
            self.password = login[1]

        self.split_std = split_std
        self.results_path = self.check_path(os.path.join(results_path, id))
        self.id = id

        self.filter_neg = filter_neg
        self.filter_pos = filter_pos

    def listdir(self, path):
        if self.sftp:
            return self.sftp.listdir(path)
        else:
            return listdir(path)

    def open(self, path, flags):
        if self.sftp:
            return self.sftp.open(path, flags)
        else:
            return open(path, flags)

    def isfile(self, path):
        if self.sftp:
            return self.sftp.isfile(path)
        else:
            return os.path.isfile(path)

    def check_path(self, results_path):
        if "sftp://" in results_path:
            split = results_path.split("/")
            user = split[4]
            host = split[2].split("@")[1]
            self.setup_sftp(host, user)
            results_path = results_path[results_path.find(".cz") + 3:]

        # if "results" in results_path:
        return results_path
        # else:
        #     return os.path.join(results_path, "results")

    def setup_sftp(self, host, username):
        import pysftp
        if not hasattr(self, 'password'):
            from grid.credentials import Credentials as cred
            self.password = cred().get_cred(host)[1]
        self.sftp = pysftp.Connection(host, username=username, password=self.password)

    def get_jsons(self, mypath, files, filter_pos="", filter_neg=""):
        print(mypath)
        for d in self.listdir(mypath):
            if filter_neg and d.startswith(filter_neg):
                continue
            filename = os.path.join(mypath, d)
            if self.isfile(filename):
                if not filter_pos or d.startswith(filter_pos):
                    if filename.endswith(".json"):
                        files.append(filename)
            else:
                self.get_jsons(filename, files, filter_pos, filter_neg)

    def load_experiments(self):
        jsons = []
        self.get_jsons(self.results_path, jsons, self.filter_pos, self.filter_neg)
        print("\n\n")

        self.json_map = {}
        for file in jsons:
            key = str(file).split("/")[1:-1]
            if key[-1] == "export":
                key = key[:-1]
            key = tuple(key)
            jsons = self.json_map.get(key, [])
            jsons.append(file)
            self.json_map[key] = jsons

        experiments = []
        for path, results in self.json_map.items():
            params = path[-1]
            template = path[-2]
            dataset = path[-3]
            if "dummy" in params:
                continue

            records = self.load_records(results)
            if records:
                experiments.append(ExperimentResults(self.id, dataset, template, params, records))

        return experiments

    def load_dataframe(self,
                       metrics=convert.keys(), posprocess=True):
        experiments = self.load_experiments()
        if experiments:
            filter = Filter(metrics, self.split_std)
            self.results = filter.filter(experiments)

            if posprocess:
                processed = {}
                filter.postprocess(self.results, processed)
                self.results = processed

            data = pd.DataFrame.from_dict(self.results, orient='index')
            data = data.apply(pd.to_numeric, errors='ignore')
            data = data.dropna(1, how="all")
            data.sort_index(inplace=True)
            return data
        else:
            return None

    def load_records(self, jsons):
        records = {}

        for file in jsons:
            with self.open(file, "r") as f:
                try:
                    loaded = json.load(f)
                except:
                    continue
                fname = file.split("/")[-1].split(".")[0]
                records[fname] = loaded
        return records


class FileObserver(Loader):

    def __init__(self, path=None, login=None, id="", filter_pos="", filter_neg=""):
        super().__init__(id, results_path=path, login=login, filter_pos=filter_pos, filter_neg=filter_neg)

    def observe(self, filepath):
        if not filepath.startswith("/home"):
            filepath = self.results_path + "/" + filepath
        self.file = self.open(filepath, "r")

    def read_increment(self):
        lines = self.file.read()
        if isinstance(lines, bytes):
            lines = lines.decode("utf-8")
        return lines

    def loop(self, filepath, function):
        self.observe(filepath)
        alllines = []
        while True:
            lines = self.read_increment()
            if lines:
                alllines += lines
                function(alllines)
            time.sleep(2)


class ProgressObserver(FileObserver):

    def __init__(self, id, path=None, login=None, metrics=["progress_train", "progress_val"], seconds=1,
                 plot_all=False, filter_pos="training", filter_neg=""):
        super().__init__(path, login, id, filter_pos=filter_pos, filter_neg=filter_neg)
        self.plot_all = plot_all
        self.seconds = seconds
        self.metrics = metrics
        self.filter_pos = "training"
        self.filter = Filter(metrics)

    def observe_progress(self, dir=""):
        plt.ion()
        if dir:
            self.results_path = dir

        try:
            data = self.load_dataframe()
            if data:
                print(data.transpose().to_string())
                # finished restart if successful
                pltr = Plotter(data)
                pltr.plot_all()
                # plt.figure()
        except:
            pass

        srted = sorted(self.json_map.keys())
        if not srted:
            print("No progress files detected!")
            os._exit(1)
        key = srted[-1]
        files = sorted(self.json_map[key])
        if self.plot_all:
            for f in files[0:-1]:
                self.plot_file(f)
                plt.figure()

        last_restart = files[-1]
        extracted = self.plot_file(last_restart)
        plt.show(block=False)

        buffer = "["
        i = 0
        while True:
            lines = self.read_increment()
            i += 1
            if lines:
                print("update" + str(i))
                buffer += lines
                try:
                    next = json.loads(buffer[:-3] + "]")
                    buffer = "["
                except:
                    continue
                for field in self.filter.fields:
                    extracted[tuple(field)] += self.filter.extract(field, next)
                self.plot(extracted)
            else:
                time.sleep(self.seconds)

    def plot_file(self, file):
        self.file = self.open(file, "r")
        self.filename = file.split("/")[-1].split(".json")[0]
        self.fullfilename = file

        lines = self.file.read()
        while not lines and self.second > 0:
            time.sleep(self.seconds)
            lines = self.file.read()

        if isinstance(lines, bytes):
            lines = lines.decode("utf-8")

        try:
            progress = json.loads(lines)
        except:
            lines = lines[:-2] + "]"
            progress = json.loads(lines)
        extracted = {}
        for field in self.filter.fields:
            extracted[tuple(field)] = self.filter.extract(field, progress)
        self.plot(extracted)
        plt.show()
        return extracted

    def plot(self, progress: {}):
        pltr = Plotter()
        plt.gca().clear()
        restarted_colors = False
        for metric, series in progress.items():
            # series1 = np.array(series).astype(np.double)
            # s1mask = np.isfinite(series1)
            # if not s1mask.any():
            #     continue
            series = series[1:]

            series1 = np.array(series).astype(float)
            xs = np.arange(len(series))

            mask = np.isnan(series1)
            if mask.all():
                continue

            idx = np.where(~mask, np.arange(len(mask)), 0)
            np.maximum.accumulate(idx, axis=0, out=idx)
            out = series1[idx]

            if not metric[0].startswith("val"):
                pltr.normal_plot(out, xs, legend=metric[0])
            else:
                if not restarted_colors:
                    restarted_colors = True
                    plt.gca().set_prop_cycle(plt.rcParams['axes.prop_cycle'])
                pltr.normal_plot(out, xs, style="--", legend=metric[0] + "-" + metric[1])

            plt.annotate('%0.5f' % out[-1], xy=(1, out[-1]), xytext=(8, 0),
                         xycoords=('axes fraction', 'data'), textcoords='offset points')

        fontP = FontProperties()
        fontP.set_size('small')
        plt.title(self.fullfilename)
        plt.xlim((0, len(xs)))
        plt.legend(loc="upper center", bbox_to_anchor=(0.5, -0.1),
                   fancybox=True, shadow=True, ncol=2, prop=fontP)

        plt.tight_layout()
        # plt.show(block=False)
        plt.draw()
        if self.seconds > 0:
            self.mypause(self.seconds)

    def mypause(self, interval):
        backend = plt.rcParams['backend']
        if backend in matplotlib.rcsetup.interactive_bk:
            figManager = matplotlib._pylab_helpers.Gcf.get_active()
            if figManager is not None:
                canvas = figManager.canvas
                if canvas.figure.stale:
                    canvas.draw()
                canvas.start_event_loop(interval)
                return


class Filter:

    def __init__(self, metrics, split_std=False):
        self.split_std = split_std
        self.metrics = metrics
        self.metrics_udpated = []
        self.fields = []

        for metric in metrics:
            conv = convert[metric]
            flatten = []
            if isinstance(conv[0], list):
                for sslist in conv:
                    self.fields.append(sslist)
                    self.metrics_udpated.append(metric + "_" + sslist[-1])
            else:
                self.fields.append(conv)
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

        if paramstring.startswith("_"):
            paramstring = paramstring[1:]

        if "_" in paramstring and "log_" not in paramstring:
            split = paramstring.split("_")
            for i in range(0, len(split), 2):
                par, val = split[i][1:], split[i + 1]
                par_map[par] = val
                if not par or not val:
                    continue

            if len(split) == 0:
                par = "params"
                res_pars = par_map.get(par, [])
                res_pars.append(par)
                val = res_pars

        return par_map

    def postprocess(self, results, processed):
        for k, v in results.items():
            if isinstance(v, list):
                agg_v = self.aggregate(v)
                if self.split_std:
                    try:
                        split = agg_v.split("+-")
                    except:
                        split = [0, 0]
                    processed[k + "_mean"] = split[0]
                    processed[k + "_std"] = split[1]
                else:
                    processed[k] = agg_v
            elif isinstance(v, dict):
                processed[k] = {}
                self.postprocess(v, processed[k])
            else:
                processed[k] = v

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

    def __init__(self, dataframe: pd.DataFrame = None, x=None, ys=None):
        if dataframe is None:
            return
        if x is None:
            x = range(len(dataframe.index))
        self.x = x
        if ys is None:
            ys = list(dataframe.columns.values)
        self.ys = ys
        self.dataframe = dataframe

    def plot_all(self, row=None, cols=None, xlabel="", ylabel="", legend=""):
        plt.figure()
        if row is None:
            row = self.x
        if cols is None:
            cols = self.ys

        good_cols = []
        for col in cols:
            if is_numeric_dtype(self.dataframe[col]):
                good_cols.append(col)
            else:
                try:
                    if "+-" in self.dataframe[col][0]:
                        good_cols.append(col)
                except:
                    pass

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

    def normal_plot(self, y, x=None, axes=None, color=None, xlabel="", ylabel="", legend="", style='-', title=None,
                    save=None):
        if axes == None:
            axes = plt.gca()
        if x is None:
            x = self.x
        if title is None:
            title = legend

        if not color:
            if len(y) == 1:
                axes.bar(x, y)
            else:
                axes.plot(x, y, style, label=legend, linewidth=1)

        else:
            if len(y) == 1:
                axes.bar(x, y)
            else:
                axes.plot(x, y, style, color, label=legend, linewidth=1)

        axes.set_xlabel(xlabel)
        axes.set_ylabel(ylabel)
        axes.set_title(title)
        # axes.legend()
        axes.grid()

        if save:
            self.save(save)

    def conf_plot(self, plusminus, x=None, axes=None, color='b', xlabel="", ylabel="", legend="", title=None,
                  save=None):
        if axes == None:
            axes = plt.gca()
        if x is None:
            x = self.x
        if "+-" in plusminus.values[0]:
            tuples = [res.split("+-") for res in plusminus]
            means = np.array([float(tuple[0]) for tuple in tuples])
            stds = np.array([float(tuple[1]) for tuple in tuples])
        elif isinstance(plusminus.values[0], list):
            # unprocessed folds
            return
        else:
            return
        if title is None:
            title = legend

        self.area_plot(x, means, stds, axes, color, legend)

        axes.set_xlabel(xlabel)
        axes.set_ylabel(ylabel)
        axes.legend()
        axes.set_title(legend)
        axes.grid()

        if save:
            self.save(save)

    def area_plot(self, x, means, stds, axes, color=None, legend=None):
        ub = means + stds
        lb = means - stds
        if not color:
            axes.fill_between(x, ub, lb, alpha=.3)
            # plot the mean on top
            axes.plot(x, means, label=legend)
        else:
            axes.fill_between(x, ub, lb, color=color, alpha=.3)
            # plot the mean on top
            axes.plot(x, means, color, label=legend)

    def show(self):
        plt.show()

    def save(self, name="default_fig", figure=None):
        if figure is None:
            figure = plt.gcf()

        figure.savefig(name + ".pdf", bbox_inches='tight')
        self.show()


def observe(mypath, seconds=1):
    matplotlib.use("Qt5Agg")
    po = ProgressObserver("", path=mypath, seconds=seconds)
    po.observe_progress()


def analyse(mypath, plot_progress=True):
    loader = Loader("", mypath)
    data = loader.load_dataframe()
    print(data.transpose().to_string())
    pltr = Plotter(data)
    pltr.plot_all()

    if plot_progress:
        po = ProgressObserver("", mypath, seconds=0)

        jsons = []
        po.get_jsons(po.results_path, jsons, po.filter_pos, po.filter_neg)
        for js in jsons:
            plt.figure(figsize=(16, 12))
            po.plot_file(js)

    return data


# this is for calling as an online progress observer from external process (Java) / or console
if __name__ == '__main__':
    sys.path.append("/home/gusta/googledrive/Github/NeuraLogic/Frontend")

    # import grid.credentials as cred
    #

    # # if "kuzelon2" in mypath:
    # #     login = cred.ondra_rci
    # # elif "souregus" in mypath:
    # #     login = cred.gusta_rci
    # # else:
    # #     login = cred.gusta_local

    mypath = sys.argv[1]
    seconds = 1
    if len(sys.argv) > 2:
        seconds = int(sys.argv[2])

    observe(mypath, seconds)
