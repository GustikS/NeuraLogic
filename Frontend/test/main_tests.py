import neuralogic as nl
from main import main


def family():
    args = {}
    args["-t"] = nl.project_path + "/NeuraLogic/resources/datasets/family/template"
    args["-e"] = nl.project_path + "/NeuraLogic/resources/datasets/family/examples"
    args["-q"] = nl.project_path + "/NeuraLogic/resources/datasets/family/queries"

    main(args)

def xor():
    args = {}
    args["-t"] = nl.project_path + "/NeuraLogic/resources/datasets/neural/xor/naive/template.txt"
    args["-e"] = nl.project_path + "/NeuraLogic/resources/datasets/neural/xor/naive/trainExamples.txt"

    main(args)

def xor_vector():
    args = {}
    args["-t"] = nl.project_path + "/NeuraLogic/resources/datasets/neural/xor/vectorized/template.txt"
    args["-e"] = nl.project_path + "/NeuraLogic/resources/datasets/neural/xor/vectorized/trainExamples.txt"

    main(args)

xor_vector()