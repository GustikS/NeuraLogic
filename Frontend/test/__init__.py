import os

project_path = "../../"  # path to Neuralogic project root
jar_path = "NeuraLogic/out/artifacts/NeuraLogic_jar/"
jar_file = "NeuraLogic.jar"

os.environ["JAVA_HOME"] = "/opt/miniconda3/envs/bet"
# os.environ["JAVA_HOME"] = "/usr/lib/jvm/java-8-oracle" # not working with Conda
os.environ['CLASSPATH'] = project_path + jar_path + jar_file

from jnius import autoclass  # this must be last import (after os.environ changes)


def jdk_connection_test():
    # %% connection to JDK

    Stack = autoclass('java.util.Stack')
    stack = Stack()
    stack.push('hello')
    stack.push('world')

    print(stack.pop())  # --> 'world'
    print(stack.pop())  # --> 'hello'


def neuralogic_connection_test():
    # %% connection to the Neuralogic project

    Setting = autoclass("settings.Settings")
    setting = Setting()
    print(setting)


def neuralogic_run_test():
    # %% able to run the project

    Main = autoclass("Main")

    e = project_path + "/NeuraLogic/resources/datasets/family/examples"
    t = project_path + "/NeuraLogic/resources/datasets/family/template"
    q = project_path + "/NeuraLogic/resources/datasets/family/queries"

    Main.main(["-e", e, "-t", t, "-q", q])


neuralogic_run_test()
