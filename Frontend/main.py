import sys
# sys.argv.append("--dynet-viz")    #the package is broken
import dynet_config

# Declare GPU as the default device type
# dynet_config.set_gpu()
# Set some parameters manualy
from dynetcon.deserialization import Deserializer

dynet_config.set(mem=4, random_seed=9)
# Initialize dynet import using above configuration in the current scope
import dynet as dy

dyparams = dy.DynetParams()

dy.init()

from dynetcon.learner import Learner
from neuralogic import lrnn
import neuralogic as nl


def main(argv):
    print(sys.argv, len(sys.argv))

    args = []
    for k,v in argv.items():
        args.append(k)
        args.append(v)

    neural_samples, neural_model, logic_model = lrnn.ground_NNs(args)

    deserializer = Deserializer(neural_model)

    samples = [deserializer.deserializeSample(sample) for sample in neural_samples]

    learner = Learner(deserializer)
    learner.learn(samples)

    sys.exit(0)
