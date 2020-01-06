import csv
import os
from os import listdir
from os.path import isfile
import pprint

mypath = '/home/gusta/data/results'

filename = "CrossvalidationPipeline"
templatename = "template_vector_cross4"

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

files = []
get_files(mypath, filename, files)

for file in files:
    if templatename in file:
        name = file[file.find("results/") + 8: file.find("/template")]
        with open(file) as f:
            lines = f.readlines()
            for line in lines:
                if "accuracy:" in line:
                    acc = line[line.find("accuracy: ") + 10: line.find("% (maj")]
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

with open(csv_file + '_train.csv', 'w') as f:
    w = csv.writer(f)
    w.writerows(train.items())

with open(csv_file + '_test.csv', 'w') as f:
    w = csv.writer(f)
    w.writerows(test.items())
