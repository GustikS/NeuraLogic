import csv
import os
from os import listdir
from os.path import isfile
import pprint

mypath = '/home/gusta/data/results/jair_diffcheck'
templatename = "template_vector_cross"

filename = "CrossvalidationPipeline"

csv_file = os.path.join(mypath, templatename + "__" + filename)


def get_files(path, filename, files):
    for d in listdir(path):
        if isfile(os.path.join(path, d)):
            if (d == filename):
                files.append(os.path.join(path, filename))

        else:
            get_files(os.path.join(path, d), filename, files)


# %%
pp = pprint.PrettyPrinter()

results = {}

train = {}
test = {}
majority = {}

files = []
get_files(mypath, filename, files)

for file in files:
    if templatename in file:
        name = file[file.find("results/") + 8: file.find("/export")]
        with open(file) as f:
            lines = f.readlines()
            for line in lines:
                if "accuracy:" in line:
                    acc = line[line.find("accuracy: ") + 10: line.find("% (maj")]
                    maj = line[line.find("maj. ") + 5: line.find("%)(best")]

                    majority[name] = maj
                    if name in train:
                        results[name + "-test"] = line
                        test[name] = acc
                    else:
                        train[name] = acc
                        results[name + "-train"] = line

print("TRAIN---------")
pp.pprint(train)
print("TEST---------")
pp.pprint(test)
print("RESULTS----------")
pp.pprint(results)

all_res = []

for data, train in train.items():
    all_res.append(data + "," + majority[data] + "," + train + "," + test[data])

with open(csv_file + '_results.csv', 'w') as f:
    f.write("dataset, majority, train, test\n")
    f.write("\n".join(all_res))
