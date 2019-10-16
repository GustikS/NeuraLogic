from neuralogic import gtw, neuralogic, settings, main


# %%

def prepare_args(input: [str]):
    str_class = neuralogic.String
    args = gtw.new_array(str_class, 6)

    for i, arg in enumerate(input):
        args[i] = arg

    return args


def create_NNs(args):
    sources = main.getSources(prepare_args(args), settings)

    nnbuilder = neuralogic.pipelines.building.End2endNNBuilder(settings, sources)
    pipeline = nnbuilder.buildPipeline()
    result = pipeline.execute(sources)

    logic = result.s.r.r
    neural = result.s.r.s

    samples = []
    for sample in result.s.s:
        samples.append(sample)

    return samples, neural, logic