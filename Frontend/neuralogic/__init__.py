import os

project_path = "../"  # path to Neuralogic project root
jar_path = "NeuraLogic/out/artifacts/NeuraLogic_jar/"
jar_file = "NeuraLogic.jar"

# os.environ["JAVA_HOME"] = "/opt/miniconda3/envs/bet"  # for pyjnius
os.environ["JAVA_HOME"] = "/usr/lib/jvm/java-8-oracle"  # not working with pyjnius
os.environ['CLASSPATH'] = project_path + jar_path + jar_file

# %%

from py4j.java_gateway import JavaGateway, GatewayParameters

import sys
from datetime import datetime

gtw = JavaGateway.launch_gateway(classpath=project_path + jar_path + jar_file, redirect_stdout=sys.stdout,
                                 redirect_stderr=sys.stdout)  # auto_field=True changed within the library itself!
# gateway = JavaGateway(gateway_parameters=GatewayParameters(auto_field=True))

settings = gtw.jvm.settings.Settings()

neuralogic = gtw.jvm

main = neuralogic.Main
print(main.testConnection(str(datetime.now())))
main.initLogging()
