import sys
# sys.argv.append("--dynet-viz")    #the package is broken
import dynet_config

# Declare GPU as the default device type
# dynet_config.set_gpu()
# Set some parameters manualy
dynet_config.set(mem=4, random_seed=9)
# Initialize dynet import using above configuration in the current scope
import dynet as dy
dyparams = dy.DynetParams()

dy.init()

from dynetcon.learner import Learner
from neuralogic import nn_creation
import neuralogic as nl


if __name__ == '__main__':
    print(sys.argv, len(sys.argv))

    t = nl.project_path + "/NeuraLogic/resources/datasets/family/template"
    e = nl.project_path + "/NeuraLogic/resources/datasets/family/examples"
    q = nl.project_path + "/NeuraLogic/resources/datasets/family/queries"

    args = ["-e", e, "-t", t, "-q", q]
    neural_samples, neural_model, logic_model = nn_creation.create_NNs(args)

    print(logic_model)

    learner = Learner(neural_model)
    learner.learn(neural_samples)

    sys.exit(0)
