import os
from os import listdir
from os.path import isfile, join

mypath = '/home/gusta/data/jair'

experiment_id = "jair_revived"
template_output_path = '/home/gusta/data/experiments/' + experiment_id + "/templates"

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
