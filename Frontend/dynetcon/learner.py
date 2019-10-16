from dynetcon import NetworkBuilder, ModelWeights
import neuralogic.transformations as trans
import dynet as dy


class Learner:

    def __init__(self, lrnn_model, epochae=100):
        self.model = ModelWeights(lrnn_model)

        self.epochae = epochae
        self.trainer = dy.SimpleSGDTrainer(self.model.dynet_model)

        self.network_builder = NetworkBuilder(self.model)

    def learn(self, lrnn_samples):

        seen_instances = 0
        total_loss = 0
        for iter in range(self.epochae):
            for lrnn_sample in lrnn_samples:
                dy.renew_cg()

                label = dy.scalarInput(trans.getLabel(lrnn_sample))
                graph_output = self.network_builder.build_network(lrnn_sample)

                loss = dy.binary_log_loss(graph_output, label)

                loss.forward()
                loss.backward()
                self.trainer.update()
