import neuralogic as nl

import os
os.environ["JAVA_HOME"] = "/opt/miniconda3/envs/bet"

from jnius import autoclass, PythonJavaClass  # this must be last import (after os.environ changes)

Settings = autoclass("settings.Settings")
settings = Settings()

# %%

Main = autoclass("Main")
Main.initLogging()

t = nl.project_path + "/NeuraLogic/resources/datasets/family/template"
e = nl.project_path + "/NeuraLogic/resources/datasets/family/examples"
q = nl.project_path + "/NeuraLogic/resources/datasets/family/queries"

sources = Main.getSources(["-e", e, "-t", t, "-q", q], settings)

#%%

# Test = autoclass("End2endNNBuilderTest")
# test = Test()
# test.simple()

# %%

NNbuilder = autoclass("pipelines.building.End2endNNBuilder")

nnbuilder = NNbuilder(settings, sources)

pipeline = nnbuilder.buildPipeline()
if pipeline.ID is None:
    print("problem")
else:
    print("...built")
    print(pipeline.ID)
    print(pipeline.start)

# settings.root = pipeline

result = pipeline.execute(sources)

nets = []
for net in result.s.s:
    nets.append(net.query.evidence)
    print(net.query.toString())
    print(net.target)