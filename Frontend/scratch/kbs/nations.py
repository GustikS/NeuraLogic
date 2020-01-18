import os

template_name = "embeddings2"

path = "/home/gusta/googledrive/Github/NeuraLogic/NeuraLogic/resources/datasets/relational/kbs/nations/old_Version/nations.txt"
export = "/home/gusta/googledrive/Github/NeuraLogic/NeuraLogic/resources/datasets/relational/kbs/nations/"

nations = set()
relations = set()
attributes = set()

with open(path) as f:
    for line in f.readlines():
        split = line.split(",")
        if line.startswith("RR"):
            relations.add(split[0][3:])
            nations.add(split[1])
            nations.add(split[2][:-2])
        elif line.startswith("R"):
            nations.add(split[0][2:])
            attributes.add(split[1][:-2])


#%% template generations

template = []

for nation in nations:
    template.append("{3} embed_nation("+nation.lower()+") :- nation("+nation.lower()+").")

for attribute in attributes:
    template.append("{3} embed_att(" + attribute.lower() + ") :- att(" + attribute.lower() + ").")

for relation in relations:
    template.append("{3} embed_rel(" + relation.lower() + ") :- rel(" + relation.lower() + ").")

with open(os.path.join(export,template_name), "w") as f:
    f.write("\n".join(template))