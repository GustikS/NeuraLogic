import dynet as dy

# create a parameter collection and add the parameters.
m = dy.ParameterCollection()
W = m.add_parameters((8, 2))
V = m.add_parameters((1, 8))
b = m.add_parameters((8))

dy.renew_cg()  # new computation graph. not strictly needed here, but good practice.

# %%

x = dy.vecInput(2)  # an input vector of size 2. Also an expression.
output = dy.logistic(V * (dy.logistic((W * x))))

x.set([0, 0])
print(output.value())

y = dy.scalarInput(0)  # this will hold the correct answer
loss = dy.binary_log_loss(output, y)

# %%

trainer = dy.SimpleSGDTrainer(m)

# %%

x.set([1, 0])
y.set(1)
loss_value = loss.value()  # this performs a forward through the network.
print("the loss before step is:", loss_value)

# now do an optimization step
loss.backward()  # compute the gradients
trainer.update()

# see how it affected the loss:
loss_value = loss.value(recalculate=True)  # recalculate=True means "don't use precomputed value"
print("the loss after step is:", loss_value)


# %%


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

# %%

total_loss = 0
seen_instances = 0
for question, answer in zip(questions, answers):
    x.set(question)
    y.set(answer)
    seen_instances += 1
    total_loss += loss.value()
    loss.backward()
    trainer.update()
    if (seen_instances > 1 and seen_instances % 100 == 0):
        print("average loss is:", total_loss / seen_instances)

# %%

x.set([0, 1])
print("0,1", output.value())

x.set([1, 0])
print("1,0", output.value())

x.set([0, 0])
print("0,0", output.value())

x.set([1, 1])
print("1,1", output.value())
