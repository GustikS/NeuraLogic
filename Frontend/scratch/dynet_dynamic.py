import dynet as dy


# create training instances, as before
def create_xor_instances(num_rounds=20000):
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
    output = dy.logistic(dy.transpose(V) * dy.average([dy.logistic((W * x))]))
    loss = dy.squared_distance(output, y)
    return loss, output


m2 = dy.ParameterCollection()
W = m2.add_parameters((8, 2))
V = m2.add_parameters((8))
b = m2.add_parameters((8))
trainer = dy.SimpleSGDTrainer(m2)

seen_instances = 0
total_loss = 0
iter = 0
for question, answer in zip(questions, answers):
    loss = create_xor_network(W, V, b, question, answer)
    if seen_instances % 4 == 0:
        seen_instances = 0
        total_loss = 0
        iter += 1
    seen_instances += 1
    total_loss += loss[0].value()
    loss[0].backward()
    trainer.update()
    if (seen_instances > 1 and seen_instances % 4 == 0):
        print(iter, "average loss is:", total_loss / seen_instances)


#%%
print("0,1", create_xor_network(W, V, b, [0, 1], 1)[1].value())
print("1,0", create_xor_network(W, V, b, [1, 0], 1)[1].value())
print("0,0", create_xor_network(W, V, b, [0, 0], 0)[1].value())
print("1,1", create_xor_network(W, V, b, [1, 1], 0)[1].value())

dy.print_text_graphviz()