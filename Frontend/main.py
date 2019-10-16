from dynetcon.learner import Learner
from neuralogic import nn_creation
import neuralogic as nl

if __name__ == '__main__':

    t = nl.project_path + "/NeuraLogic/resources/datasets/family/template"
    e = nl.project_path + "/NeuraLogic/resources/datasets/family/examples"
    q = nl.project_path + "/NeuraLogic/resources/datasets/family/queries"

    args = ["-e", e, "-t", t, "-q", q]
    neural_samples, neural_model, logic_model = nn_creation.create_NNs(args)

    print(logic_model)

    learner = Learner(neural_model)
    learner.learn(neural_samples)