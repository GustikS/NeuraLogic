import neuralogic as nl

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

    e = nl.project_path + "/NeuraLogic/resources/datasets/family/examples"
    t = nl.project_path + "/NeuraLogic/resources/datasets/family/template"
    q = nl.project_path + "/NeuraLogic/resources/datasets/family/queries"

    Main.main(["-e", e, "-t", t, "-q", q])


neuralogic_run_test()
