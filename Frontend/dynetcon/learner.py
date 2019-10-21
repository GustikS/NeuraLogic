from dynetcon import NetworkBuilder, ModelWeights
import neuralogic.transformations as trans
import dynet as dy


class Learner:

    def __init__(self, lrnn_model, epochae=10):
        self.model = ModelWeights(lrnn_model)

        self.epochae = epochae
        self.trainer = dy.SimpleSGDTrainer(self.model.dynet_model, learning_rate = 0.1)

        self.network_builder = NetworkBuilder(self.model)

    def learn(self, lrnn_samples):

        seen_instances = 0
        total_loss = 0
        for iter in range(self.epochae):
            for lrnn_sample in lrnn_samples:
                dy.renew_cg(immediate_compute=False, check_validity = False)

                label = dy.scalarInput(trans.getLabel(lrnn_sample))
                graph_output = self.network_builder.build_network(lrnn_sample)

                loss = dy.squared_distance(graph_output, label)

                seen_instances += 1
                total_loss += loss.scalar_value()
                loss.backward()
                self.trainer.update()

                if (seen_instances > 1 and seen_instances % 10 == 0):
                    print("average loss is:", total_loss / seen_instances)

        self.print_outputs(lrnn_samples)

    def print_outputs(self, lrnn_samples):
        for lrnn_sample in lrnn_samples:
            dy.renew_cg(immediate_compute=True, check_validity=True)
            label = dy.scalarInput(trans.getLabel(lrnn_sample))
            graph_output = self.network_builder.build_network(lrnn_sample)

            dy.print_text_graphviz()

            print(f'label: {label.value()}, output: {graph_output.value()}')