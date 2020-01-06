import os
from os import listdir
from os.path import isfile, join

mypath = '/home/gusta/data/jair'

experiment_id = "jair_revived"
template_output_path = '/home/gusta/data/experiments/' + experiment_id + "/templates"

# for d in listdir(mypath):
#     os.remove(os.path.join(mypath,d,"embeddings"))
    # os.rename(os.path.join(mypath,d,"bonds_repaired"),os.path.join(mypath,d,"bonds"))
# os.rename(os.path.join(mypath, d, "samples"), os.path.join(mypath, d, "examples"))

# %%
#
# for d in listdir(mypath):
#     i = 0
#
#     bonds = []
#     with open(os.path.join(mypath, d, "bonds")) as f:
#         lines = f.readlines()
#         for line in lines:
#             bonds.append("b_" + line)
#
#     with open(os.path.join(mypath, d, "bonds_repaired"), 'w') as f:
#         f.write("".join(bonds))
#
# # %%
#
# for d in listdir(mypath):
#     i = 0
#     examples = []
#     with open(os.path.join(mypath, d, "queries")) as f:
#         lines = f.readlines()
#         for line in lines:
#             q,e = line.split(" :- ")
#             examples.append("e" + str(i) +" :- " + e)
#             i += 1
#
#     with open(os.path.join(mypath, d, "queries"), 'w') as f:
#         f.write("".join(examples))


#%%

# for d in listdir(mypath):
#     i = 0
#     queries = []
#     examples = []
#     with open(os.path.join(mypath, d, "examples")) as f:
#         lines = f.readlines()
#         for line in lines:
#             i += 1
#             queries.append("e" + str(i) + " :- " + line[0:3] + " predict" + ".")
#
#             examples.append("e" + str(i) + " :- " + line[4:])
#
#     with open(os.path.join(mypath, d, "examples"), 'w') as f:
#         f.write("".join(examples))
#
#     with open(os.path.join(mypath, d, "queries"), 'w') as f:
#         f.write("\n".join(queries))

    # os.rename(os.path.join(mypath,d), os.path.join(mypath,d[12:]))
    # for f in listdir(os.path.join(mypath, d)):
    #     if f != "examples":
    #         os.rename(os.path.join(mypath, d, f), os.path.join(mypath, d, f[2:]))
        # if f != "examples" and f != "1_atoms" and f!= "1_bonds" and f!= "1_other":
        # os.remove(os.path.join(mypath,d,f))

# %%

for d in listdir(mypath):
    i = 0

    bonds = []
    with open(os.path.join(mypath, d, "bonds")) as f:
        lines = f.readlines()
        for line in lines:
            bonds.append(line[0:-3])

    examples = []
    with open(os.path.join(mypath, d, "examples")) as f:
        lines = f.readlines()
        for line in lines:
            for b in bonds:
                line = line.replace(" " + b + "(", " b_" + b + "(")
            examples.append(line)

    with open(os.path.join(mypath, d, "examples_repaired"), 'w') as f:
        f.write("".join(examples))

# %% TEMPLATE GENERATION

dim = "{3,1}"

for d in listdir(mypath):

    atoms = []
    bonds = []

    with open(os.path.join(mypath, d, "atoms")) as f:
        lines = f.readlines()
        for line in lines:
            atoms.append(line[0:-3])

    with open(os.path.join(mypath, d, "bonds")) as f:
        lines = f.readlines()
        for line in lines:
            bonds.append(line[0:-3])

    template = []
    for atom in atoms:
        template.append(dim + " atom_embed(A) :- " + atom + "(A).")

    template.append("atom_embed/1 " + dim)

    for bond in bonds:
        template.append(dim + " bond_embed(B) :- " + bond + "(B).")

    template.append("bond_embed/1 " + dim)

    with open(os.path.join(mypath, d, "embeddings"), 'w') as f:
        f.write("\n".join(template))


    template.append(
        "{1,243} predict :- atom_embed(A), bond(A,B,X), bond_embed(X), atom_embed(B), bond(B,C,Y), bond_embed(Y), atom_embed(C), <-7>.  [activation=crossproduct-sigmoid]")
    template.append("predict/0 {1}")

    directory = os.path.join(template_output_path, d)
    if not os.path.exists(directory):
        os.makedirs(directory)

    with open(os.path.join(directory, "template_vector_cross"), 'w') as f:
        f.write("\n".join(template))
