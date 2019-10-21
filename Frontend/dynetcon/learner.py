from dynetcon import NetworkBuilder, ModelWeights
import neuralogic.transformations as trans
import dynet as dy


class Learner:

    def __init__(self, lrnn_model, epochae=2000):
        self.model = ModelWeights(lrnn_model)

        self.epochae = epochae
        self.trainer = dy.SimpleSGDTrainer(self.model.dynet_model, learning_rate = 0.1)

        self.network_builder = NetworkBuilder(self.model)

    def learn(self, lrnn_samples):

        for iter in range(self.epochae):
            if (iter > 0 and iter % 10 == 0):
                print(iter, " average loss is:", total_loss / seen_instances)
            seen_instances = 0
            total_loss = 0
            for lrnn_sample in lrnn_samples:
                dy.renew_cg(immediate_compute=False, check_validity=False)

                label = dy.scalarInput(trans.getLabel(lrnn_sample))
                graph_output = self.network_builder.build_network(lrnn_sample)

                loss = dy.squared_distance(graph_output, label)

                seen_instances += 1
                total_loss += loss.value()
                loss.backward()
                self.trainer.update()

        self.print_outputs(lrnn_samples)

    def print_outputs(self, lrnn_samples):
        for lrnn_sample in lrnn_samples:
            dy.renew_cg(immediate_compute=False, check_validity=False)

            graph_output = self.network_builder.build_network(lrnn_sample)
            label = dy.scalarInput(trans.getLabel(lrnn_sample))
            loss = dy.squared_distance(graph_output, label)

            # dy.print_text_graphviz()

            print(f'label: {label.value()}, output: {graph_output.value()}')
