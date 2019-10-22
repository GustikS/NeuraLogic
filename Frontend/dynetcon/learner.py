import dynet as dy

from dynetcon.deserialization import Deserializer


class Learner:

    def __init__(self, deserializer:Deserializer, epochae=10000, printouts=10):
        self.printouts = printouts
        self.deserializer = deserializer

        self.epochae = epochae
        self.trainer = dy.SimpleSGDTrainer(self.deserializer.dynet_model, learning_rate = 0.1)

    def learn(self, samples):

        for iter in range(self.epochae):
            if (iter > 0 and iter % self.printouts == 0):
                print(iter, " average loss is:", total_loss / seen_instances)
            seen_instances = 0
            total_loss = 0
            for sample in samples:
                dy.renew_cg(immediate_compute=False, check_validity=False)

                label = dy.scalarInput(sample.target)
                graph_output = self.deserializer.buildSample(sample.neurons)

                loss = dy.squared_distance(graph_output, label)

                seen_instances += 1
                total_loss += loss.value()
                loss.backward()
                self.trainer.update()

        self.print_outputs(samples)

    def print_outputs(self, samples):
        for sample in samples:
            dy.renew_cg(immediate_compute=False, check_validity=False)

            graph_output = self.deserializer.buildSample(sample.neurons)
            label = dy.scalarInput(sample.target)
            loss = dy.squared_distance(graph_output, label)

            # dy.print_text_graphviz()

            print(f'label: {label.value()}, output: {graph_output.value()}')
