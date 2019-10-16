

def getRawNetwork(sample):
    return sample.query.evidence


def transformNetwork(network):
    pass

def getLabel(sample):
    return sample.target.value
