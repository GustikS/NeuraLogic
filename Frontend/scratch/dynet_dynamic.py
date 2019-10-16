import dynetcon as dy


# create training instances, as before
def create_xor_instances(num_rounds=2000):
    questions = []
    answers = []
    for round in range(num_rounds):
        for x1 in 0, 1:
            for x2 in 0, 1:
                answer = 0 if x1 == x2 else 1
                questions.append((x1, x2))
                answers.append(answer)
    return questions, answers


questions, answers = create_xor_instances()


# create a network for the xor problem given input and output
def create_xor_network(W, V, b, inputs, expected_answer):
    dy.renew_cg()  # new computation graph
    x = dy.vecInput(len(inputs))
    x.set(inputs)
    y = dy.scalarInput(expected_answer)
    output = dy.logistic(V * (dy.tanh((W * x) + b)))
    loss = dy.binary_log_loss(output, y)
    return loss


m2 = dy.ParameterCollection()
W = m2.add_parameters((8, 2))
V = m2.add_parameters((1, 8))
b = m2.add_parameters((8))
trainer = dy.SimpleSGDTrainer(m2)

seen_instances = 0
total_loss = 0
for question, answer in zip(questions, answers):
    loss = create_xor_network(W, V, b, question, answer)
    seen_instances += 1
    total_loss += loss.value()
    loss.backward()
    trainer.update()
    if (seen_instances > 1 and seen_instances % 100 == 0):
        print("average loss is:", total_loss / seen_instances)
