import os

project_path = "../../"  # path to Neuralogic project root
jar_path = "NeuraLogic/out/artifacts/NeuraLogic_jar/"
jar_file = "NeuraLogic.jar"

# os.environ["JAVA_HOME"] = "/opt/miniconda3/envs/bet"  # for pyjnius
os.environ["JAVA_HOME"] = "/usr/lib/jvm/java-8-oracle" # not working with pyjnius
os.environ['CLASSPATH'] = project_path + jar_path + jar_file