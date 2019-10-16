import neuralogic as nl
from py4j.java_gateway import JavaGateway, GatewayParameters

import sys

gateway = JavaGateway.launch_gateway(classpath=nl.project_path + nl.jar_path + nl.jar_file, redirect_stdout=sys.stdout) # auto_field=True changed within the library itself!
# gateway = JavaGateway(gateway_parameters=GatewayParameters(auto_field=True))


# %%

settings = gateway.jvm.settings.Settings()

res = settings.validate()

t = nl.project_path + "/NeuraLogic/resources/datasets/family/template"
e = nl.project_path + "/NeuraLogic/resources/datasets/family/examples"
q = nl.project_path + "/NeuraLogic/resources/datasets/family/queries"

str_class = gateway.jvm.String
args = gateway.new_array(str_class, 6)

args[0] = "-e"
args[1] = e
args[2] = "-t"
args[3] = t
args[4] = "-q"
args[5] = q

main = gateway.jvm.Main
aa = main.test(args)

# main.main(args)
main.initLogging()
sources = gateway.jvm.Main.getSources(args, settings)

# %%


nnbuilder = gateway.jvm.pipelines.building.End2endNNBuilder(settings, sources)

pipeline = nnbuilder.buildPipeline()

result = pipeline.execute(sources)

nets = []
for net in result.s.s:
    nets.append(net.query.evidence)
    print(net.query.toString())
    print(net.target.value)

# %%

print(net.query.evidence.allNeuronsTopologic[2].id)

print("neuron cislo " + str(net.query.evidence.allNeuronsTopologic[7].index))
print(net.query.evidence.allNeuronsTopologic[7].getWeights()[0].name)
print(net.query.evidence.allNeuronsTopologic[7].getWeights()[0].value.getClass())
print(net.query.evidence.allNeuronsTopologic[7].getWeights()[0].value.value)
