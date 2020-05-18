import os
from os import listdir
from os.path import isfile, join

mypath = '/home/gusta/data/datasets/jair'

template_name = "template_unified_bad"

template_output_path = "/home/gusta/data/templates/jair/"

dim = (3, 1)

# %%


dimF = "{" + str(dim[0]) + "," + str(dim[1]) + "}"
dimR = "{" + str(dim[1]) + "," + str(dim[0]) + "}"

atoms = set()
bonds = set()

for d in listdir(mypath):

    with open(os.path.join(mypath, d, "atoms")) as f:
        lines = f.readlines()
        for line in lines:
            atoms.add(line[0:-3])

    with open(os.path.join(mypath, d, "bonds")) as f:
        lines = f.readlines()
        for line in lines:
            bonds.add(line[0:-3])

# %%

template = []
for i in range(1,4):
    for atom in atoms:
        template.append("{1} atomKappa_A" + str(i) + "(A) :- " + atom + "(A).")

    for bond in bonds:
        template.append("{1} bondKappa_A" + str(i) + "(B) :- " + bond + "(B).")

with open(os.path.join(template_output_path, template_name), 'w') as f:
    f.write("\n".join(template))

# %%

template = []
for atom in atoms:
    template.append(dimF + " atom_embed(A) :- " + atom + "(A).")

template.append("atom_embed/1 " + dimF)

for bond in bonds:
    template.append(dimF + " bond_embed(B) :- " + bond + "(B).")

template.append("bond_embed/1 " + dimF)

template.append(
    dimR + " predict :- atom_embed(A), bond(A,B,X), bond_embed(X), atom_embed(B), bond(B,C,Y), bond_embed(Y), atom_embed(C).")

template.append("predict/0 {1}")

directory = os.path.join(template_output_path)
if not os.path.exists(directory):
    os.makedirs(directory)

with open(os.path.join(directory, template_name), 'w') as f:
    f.write("\n".join(template))
