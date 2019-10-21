from py4j.protocol import Py4JError

from neuralogic import transformations
import dynet as dy


class ModelWeights():

    def __init__(self, lrnn_model):
        self.lrnn_model = lrnn_model
        self.dynet_model = dy.Model()

        self.weights = self.extract_weights(lrnn_model)

    def extract_weights(self, lrnn_model):
        weights = {}
        for weight in lrnn_model.weights:
            weights[weight.name] = self.dynet_model.add_parameters(tuple(weight.value.size()))

        return weights

    def _size(self, dims):
        if not dims:
            return 1
        else:
            return list(dims)

    def size(self, value):
        if type(value) == float:
            return 1
        elif type(value) == list:
            if type(value[0]) == list:
                return (len(value[0]), len(value))
            return len(value)


class NetworkBuilder():

    def __init__(self, model_weights):
        self.model_weights = model_weights
        self.network_index = 0

    def build_network(self, lrnn_sample):

        self.model_weights.weights["zeroWeight"] = dy.constant((1,), 0)
        self.model_weights.weights["unitWeight"] = dy.constant((1,), 1)

        lrnn_network = transformations.getRawNetwork(lrnn_sample)
        output_neuron_index = lrnn_sample.query.neuron.index

        neuron_indices = {}
        neuron_outputs = {}
        topo_neurons = lrnn_network.allNeuronsTopologic
        for i, neuron in enumerate(topo_neurons):
            neuron_indices[neuron.index] = i
            expr = self.build_neuron(neuron, neuron_outputs, neuron_indices)
            if output_neuron_index == neuron.index:
                return expr

    def build_neuron(self, neuron, neuron_outputs, neuron_indices):
        index = neuron_indices[neuron.index]
        name = neuron.id

        inputs = neuron.getInputs()
        input_count = inputs.size()

        if input_count == 0:  # factNeuron
            fact_value = self.get_input_tensor(neuron.getRawState().getValue())
            offset = neuron.offset
            if offset is not None:
                m_offset = self.model_weights.weights[offset.name]
                expr = fact_value + m_offset
            else:
                expr = fact_value
            neuron_outputs[index] = expr
            return expr

        input_exprs = []
        for input in inputs:
            input_exprs.append(neuron_outputs[neuron_indices[input.index]])
        # input_exprs = dy.concatenate(input_exprs)

        agg = neuron.getAggregation().getName()
        if agg is not None:
            activation = self.getActivation(agg)
        else:
            activation = None

        try:  # weighted neuron?
            weights = neuron.getWeights()
            offset = neuron.getOffset()

            m_offset = self.model_weights.weights[offset.name]
            m_weights = []
            for weight in weights:
                m_weights.append(self.model_weights.weights[weight.name])
            m_weights = dy.concatenate(m_weights)
            m_inputs = dy.concatenate(input_exprs)
            if m_weights.dim() == m_inputs.dim():
                m_weights = dy.transpose(m_weights)
            expr = activation[1]((m_weights * m_inputs) + m_offset)

        except Py4JError as ex:  # unweighted neuron
            if activation and activation[0]:
                expr = activation[1](dy.esum(input_exprs))
            else:
                expr = activation[1](input_exprs)

        neuron_outputs[index] = expr
        return expr

    def get_input_tensor(self, value):
        if value.getClass().getSimpleName() == "FactValue":
            value = value.value
        else:
            value = list(value.values)
        dim = self.model_weights.size(value)
        if dim == 1:
            return dy.scalarInput(value)
        elif dim > 1:
            return dy.vecInput(dim)
        else:
            return dy.inputMatrix(dim)

    def getActivation(self, name):
        # TODO make this a more robust mapping
        if name == "Sigmoid":
            return (True, dy.logistic)
        elif name == "AVG":
            return (False, dy.average)
        elif name == "MAX":
            return (False, dy.emax())
        elif name == "ReLu":
            return (True, dy.rectify)
