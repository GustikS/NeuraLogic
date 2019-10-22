import json
import dynet as dy

from py4j.java_gateway import get_field

#todo split into deserializer (slow preprocessing) and builder (fast expresion building)
class Deserializer:

    def __init__(self, neuralModel):
        self.dynet_model = dy.ParameterCollection()
        self.parameters = self.deserialize_weights(neuralModel)

    def buildSample(self, neurons: ['Neuron']):
        for neuron in neurons:
            neuron.build_expr()
        return neuron.expr

    def deserialize_weights(self, neuralModel):
        weights = [0] * len(neuralModel)
        for weight in neuralModel:
            w = Weight(weight)
            w.postprocess()
            dim = tuple(get_field(weight,'dimensions'))
            if not dim:
                dim = (1,)
            w.expr = self.dynet_model.add_parameters(dim)
            weights[w.index] = w.expr
        return weights

    def deserializeSample(self, sample):
        sample = Sample(sample)
        sample.postprocess(self.parameters)
        return sample


class Sample(object):
    __slots__ = ('id', 'target', 'outputNeuron', 'neurons')

    def __init__(self, sample):
        self.id = get_field(sample, 'id')
        self.target = get_field(sample, 'target')
        self.outputNeuron = get_field(sample, 'neuron')
        self.neurons = self.deserializeNetwork(get_field(sample, 'network'))

    def deserializeNetwork(self, network):
        neurons = []
        for i, neuron in enumerate(network):
            neurons.append(Neuron(neuron, i))
            if get_field(neuron, 'index') == self.outputNeuron:
                break
        return neurons

    def postprocess(self, all_weights):
        self.target = json.loads(self.target)
        for neuron in self.neurons:
            neuron.postprocess(self.neurons, all_weights)
        self.outputNeuron = self.neurons[-1]


class Neuron(object):
    __slots__ = ("index", "name", "weighted", "activation", "pooling", "inputs", "weights", "offset", "value", "expr")

    def __init__(self, neuron, index):
        self.index = index
        self.name = get_field(neuron, 'name')

        self.weighted = get_field(neuron, 'weighted')

        self.activation = get_field(neuron, 'activation')
        self.inputs = get_field(neuron, 'inputs')

        self.weights = get_field(neuron, 'weights')
        self.offset = get_field(neuron, 'offset')

        self.value = get_field(neuron, 'value')
        self.pooling = get_field(neuron, 'pooling')

        self.expr = None

    def postprocess(self, all_neurons, all_weights):
        if self.activation:
            self.activation = self.get_activation(self.activation)

        if self.inputs:
            new_inputs = [None] * len(self.inputs)
            for i in range(len(self.inputs)):
                new_inputs[i] = all_neurons[self.inputs[i]]
            self.inputs = new_inputs

        if self.weights:
            new_weights = [None] * len(self.weights)
            for i in range(len(self.weights)):
                new_weights[i] = all_weights[self.weights[i]]
            self.weights = new_weights

        if self.offset:
            self.offset = all_weights[self.offset]

        if self.value:
            self.value = json.loads(self.value)

    def get_activation(self, name):
        if name == "Sigmoid":
            return dy.logistic
        elif name == "Average":
            return dy.average
        elif name == "Maximum":
            return dy.emax()
        elif name == "ReLu":
            return dy.rectify

    def build_expr(self):
        out = None
        if self.inputs:
            if self.weights:
                for w, i in zip(self.weights, self.inputs):
                    if not out:
                        out = w * i.expr
                    else:
                        out += w * i.expr
            else:
                for i in self.inputs:
                    if not out:
                        out = i.expr
                    else:
                        out += i.expr
        else:  # fact neurons
            out = self.get_value()

        if self.offset:
            out += self.offset

        if self.activation:
            if self.pooling:
                out = list(out)
            self.expr = self.activation(out)
        else:  # fact neurons
            self.expr = out

    def get_value(self):
        return self.get_input_tensor(self.value)

    def get_input_tensor(self, value):
        try:
            dim = len(value)
        except:
            dim = 1 #Scalar has not len()
        if dim == 1:
            return dy.scalarInput(value)
        elif dim > 1:
            res = dy.vecInput(dim)
            res.set(value)
            return res
        else:
            res = dy.inputMatrix(dim)
            res.set(value)
            return res


class Weight(object):
    __slots__ = ("index", "name", "dimensions", "value", "expr")

    def __init__(self, weight):
        self.index = get_field(weight, 'index')
        self.name = get_field(weight, 'name')
        self.dimensions = get_field(weight, 'dimensions')
        self.value = get_field(weight, 'value')
        self.expr = None

    def postprocess(self):
        self.dimensions = list(self.dimensions)
        self.value = json.loads(self.value)
