from py4j.java_gateway import get_field

from dynetcon.deserialization import Deserializer
from neuralogic import gtw, neuralogic, settings, main


# %%

def prepare_args(input: [str]):
    str_class = neuralogic.String
    args = gtw.new_array(str_class, len(input))

    for i, arg in enumerate(input):
        args[i] = arg

    return args


def ground_NNs(args):
    sources = main.getSources(prepare_args(args), settings)

    nnbuilder = neuralogic.pipelines.building.End2endNNBuilder(settings, sources)
    pipeline = nnbuilder.buildPipeline()
    result = pipeline.execute(sources)

    samples = get_field(get_field(result, 's'), 's')
    samples = list(samples)
    logic = get_field(get_field(get_field(result, 's'), 'r'), 'r')
    neuralParams = get_field(get_field(get_field(result, 's'), 'r'), 's')

    return samples, list(neuralParams), logic