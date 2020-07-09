# NeuraLogic 

![Generic badge](https://img.shields.io/badge/Release-0.2.1-blue.svg)
![Generic badge](https://img.shields.io/badge/Licence-MIT-green.svg)
![Generic badge](https://img.shields.io/badge/Java-1.8-orange.svg)


_NeuraLogic is a framework for combining **relational** and **deep** learning through a form of **differentiable logic programming**. It is an official implementation of the **[Lifted Relational Neural Networks](#papers)** concept._


---
At the core there is a custom language you can use to write differentiable programs encoding your learning scenarios, similarly to classic Deep Learning (DL) frameworks (e.g. TensorFlow). However, the language follows a [logic programming](https://en.wikipedia.org/wiki/Logic_programming) paradigm and is **declarative** in nature (it's similar to [Datalog](https://en.wikipedia.org/wiki/Datalog)). This means that instead of directly encoding the computation graph, you just declare:

1. the inputs (and their numeric values, if any)
    - i.e. the observed facts/data = objects, structures, knowledge graphs, relational databases, ...
    - e.g. `atom(oxygen_1)`, `0.3 stable(oxygen)`, `8 protons(oxygen)`, `1.7 energy(oxygen,leve2)`, `[1.2,0,-1] features(oxygen)`, `[[0,2.1],[1.7,-1]] bond(oxygen_1,hydrogen_2)`
1. the outputs (and their expected values - for supervised learning)
    - i.e. the queries = classification labels, regression targets, ...
    - e.g. `1 class`, `4.7 target(molecule_3)`, `0 relation(carbon,xenon,fluor)`
1. a set of rules applicable in your domain (and their learnable parameters)
    - i.e. the generic knowledge/bias which you want to use. It does not have to be explicit.
        - this is how you can encode diverse deep learning models, but also relational background knowledge and other constructs.
    - e.g. `0.99: covalent(B) :- oxygen(X), hydrogen(Y), bond(X,Y,B).` or just `embed(X) :- W_1 embed(Y), bond(X,Y,_).`

### Example
Consider a simple program for learning with molecular data<sup>[1](#myfootnote1)</sup>, encoding a generic idea that some hidden representation (predicate `h(.)`) of any chemical atom (variable `X`) is somewhat dependent on the other atoms (`a(Y)`) adjacent to it (`b(X,Y)`), with a parameterized rule as:
`````prolog
W_h1 h(X) :- W_a a(Y), W_b b(X,Y).
`````
Additionally, let's assume that representation of a molecule (`q`) follows from representations of all the contained atoms (`h(X)`), i.e.:
```prolog
W_q q :- W_h2 h(X).
```
These 2 rules, parameterized with the tensors `W_*`'s, then form a learning program, which can be directly used to classify molecules. Actually, it directly encodes a popular idea known as [Graph Neural Networks](https://arxiv.org/pdf/1901.00596.pdf).
Execution of this program (or "template") for 2 input molecule samples will generate 2 parameterized differentiable computation graphs as follows:

![Template2Neural Grounding](.github/img/example_template.png)

Each computation node in the graphs is associated with some (differentiable) activation function defined by a user (or settings). 
The parameters `W_*` in the program are then automatically optimized to reflect the expected output values (`A_q`) through gradient descent.

For detailed syntax and semantics, please see [papers](#papers) on "**Lifted Relational Neural Networks**".


<a name="myfootnote1">1</a>: _Note that NeuraLogic is by no means designed or limited to learning with chemical data/models/knowledge, but we use it as an example domain here for consistency._

### Use Cases
While the framework can be used to encode anything from MLPs, CNNs, RNNs, etc., it is **not** well suited for classic deep learning with regular data based on large homogeneous tensor operations.
The framework is rather meant for efficient encoding of deep **relational** learning scenarios<sup>[2](#myfootnote2)</sup>, i.e. using dynamic (convolutional) neural networks to learn from data with irregular structure(s). 
That is why we exploit the declarative language with first-order expressiveness, as it allows for compact encoding of complex relational scenarios (similarly to how [Prolog](https://en.wikipedia.org/wiki/Prolog) can be elegant for relational problems, while not so much for classic programming).

The framework is mostly optimized for quick, high-level prototyping of learning scenarios with sparse, irregular, relational data and complex, dynamically structured models. Particularly, you may find it beneficial for encoding various:
  - Graph neural networks
    - where you can use it to go well [beyond the existing models](todo)
  - Knowledge base completions
    - with [complex models](https://link.springer.com/chapter/10.1007/978-3-319-63342-8_9) requiring e.g. chained inference
  - learning with Relational background knowledge/bias
    - which can be as expressive as the [models themselves](https://www.jair.org/index.php/jair/article/view/11203)
  - approaches from Neural-symbolic integration
    - for combining (fuzzy) [logic inference with neural networks](http://arg.ciirc.cvut.cz/slides/2016-Sourek-LRNN.pdf)
  - and other more crazy ideas, such as learning with
    - hypergraphs and ontologies
        - see [example constructs](todo)
    - recursion
        - e.g. for [latent type hierarchies](https://link.springer.com/chapter/10.1007/978-3-319-63342-8_9)
    - and generic [latent logic programs](https://link.springer.com/chapter/10.1007/978-3-319-78090-0_10)


<a name="myfootnote2">2</a>: _if you come from deep learning background, you may have heard terms such as "geometric deep learning" or "graph representation learning". Note also that this framework is not designed/limited to graphs only._

### Getting started

##### Requirements

- Java >= 1.8 
    - tested with both OpenJDK (v1.8.0_222) and Oracle JDK (v1.8.0_151)
    - if you don't have Java in your system already, it is enough to get the runtime environment (JRE). 
        - Get it either from [Oracle](https://www.oracle.com/java/technologies/javase-jre8-downloads.html) or [OpenJDK](https://adoptopenjdk.net/index.html?variant=openjdk8&jvmVariant=hotspot)
- for developers - you can clone and open this project directly in [IntelliJ IDEA](https://www.jetbrains.com/idea/)

#### Running examples

1. download a [release](https://github.com/GustikS/NeuraLogic/releases) into some directory `DIR`
    - or build from source with [Maven](https://maven.apache.org/) or [IntelliJ IDEA](https://www.jetbrains.com/idea/)
1. clone this repository (or just download the Resources/datasets directory) within `DIR`
    - `git clone https://github.com/GustikS/NeuraLogic`
1. try some trivial examples from terminal in `DIR`
    1. a simple XOR problem
        - scalar: `java -jar NeuraLogic.jar -sd ./NeuraLogic/Resources/datasets/neural/xor/naive`
        - vectorized: `java -jar NeuraLogic.jar -sd ./NeuraLogic/Resources/datasets/neural/xor/vectorized`
    1. a simple relational problem (Example 2 from [this paper](https://www.jair.org/index.php/jair/article/view/11203))
        - `java -jar NeuraLogic.jar -sd ./NeuraLogic/Resources/datasets/simple/family`
    1. molecule classification problem (mutagenesis)
        - `java -jar NeuraLogic.jar -sd ./NeuraLogic/Resources/datasets/relational/molecules/mutagenesis`
    1. knowledge-base completion problem
        - `java -jar NeuraLogic.jar -sd ./NeuraLogic/Resources/datasets/relational/kbs/nations`
1. you can check various exported settings and results in `DIR/target`
    1. if you have [Graphviz](https://graphviz.org/) installed (`which dot`), you can use it to observe the internal computation structures in debug mode:
        - `java -jar NeuraLogic.jar -sd ./NeuraLogic/Resources/datasets/neural/xor/naive -debug all`
            - this should show a graph of the 1) worflow, 2) template, 3) inference graphs, 4) neural networks + weight updates, and 5) final learned template
        - `java -jar NeuraLogic.jar -sd ./NeuraLogic/Resources/datasets/simple/family -iso -1 -prune -1 -debug all`
            - note we turn off network pruning and compression here so that you can observe direct correspondence to the original example  


### Usage
```
usage: java -jar NeuraLogic.jar 

 -lc,--logColors <INT>                   colored output on console {0,INT} (default: 1)
 -sf,--sourcesFile <FILE>                path to json Sources specification file (default: sources.json)
 -sd,--sourcesDir <DIR>                  path to directory with all the standardly-named Source files for learning (default: .)
 -t,--template <FILE>                    Template file containing weighted rules for leaning (default: template.txt)
 -q,--trainQueries <FILE>                file containing labeled training Queries (default: trainQueries.txt)
 -e,--trainExamples <FILE>               file containing, possibly labeled, input training Facts (default: trainExamples.txt)
 -vq,--valQueries <FILE>                 file containing labeled validation Queries (default: valQueries.txt)
 -ve,--valExamples <FILE>                file containing, possibly labeled, input validation Facts (default: valExamples.txt)
 -te,--testExamples <FILE>               file with, possibly labeled, test Examples (default: testExamples.txt)
 -tq,--testQueries <FILE>                file with test Queries (default: testQueries.txt)
 -fp,--foldPrefix <STRING>               folds folder names prefix (default: fold_)
 -xval,--crossvalidation <INT>           number of folds to split for crossvalidation (default: 5)
 -set,--settingsFile <FILE>              path to json file with all the Settings (default: settings.json)
 -out,--outputFolder <DIR>               output folder for logging and exporting (default: ./target/out)
 -mode,--pipelineMode <ENUM>             main mode of the program {complete, neuralization, debug} (default: complete)
 -debug,--debugMode <ENUM>               debug some objects within the Pipeline during the run {template, grounding, neuralization, samples, model, all} (default: all)
 -lim,--limitExamples <INT>              limit examples to some smaller number, used e.g. for debugging {-1,INT} (default: -1)
 -seed,--randomSeed <INT>                int seed for random generator (default: 0)
 -gm,--groundingMode <ENUM>              groundings mode {independent, sequential, global} (default: independent)
 -dist,--distribution <ENUM>             distribution for weight initialization {uniform, normal, longtail, constant} (default: uniform)
 -init,--initialization <ENUM>           algorithm for weight initialization {simple, glorot, he} (default: simple)
 -opt,--optimizer <ENUM>                 optimization algorithm {sgd, adam} (default: ADAM)
 -lr,--learningRate <FLOAT>              initial learning rate (default: 0.001)
 -ts,--trainingSteps <INT>               cumulative number of epochae in neural training (default: 3000)
 -rec,--recalculationEpocha <INT>        recalculate true training and validation error+stats every {INT} epochae (default: 10)
 -decay,--learnRateDecay <FLOAT>         learning rate decay geometric coefficient {-1,FLOAT} (default: 0.95)
 -decays,--decaySteps <INT>              learning rate decays every {-1,INT} steps (default: 100)
 -preft,--preferTraining <INT>           turn on to force best training model selection as opposed to (default) selecting best validation error model {0,1} (default: 0)
 -atomf,--atomFunction <ENUM>            activation function for atom neurons {sigmoid,tanh,relu,identity,...} (default: tanh)
 -rulef,--ruleFunction <ENUM>            activation function for rule neurons {sigmoid,tanh,relu,identity,...} (default: tanh)
 -aggf,--aggFunction <ENUM>              aggregation function for aggregation neurons {avg,max,sum,...} (default: avg)
 -em,--evaluationMode <ENUM>             evaluation metrics are either for {regression, classification, kbc} (default: classification)
 -ef,--errorFunction <ENUM>              type of error function {MSE, XEnt} (default: AVG(SQUARED_DIFF))
 -iso,--isoCompression <INT>             iso-value network compression (lifting), number of decimal digits (default: 12)
 -isoinits,--isoInitializations <INT>    number of iso-value initializations for network compression (default: 1)
 -isocheck,--losslessCompression <INT>   lossless compression isomorphism extra check? {0,1} (default: 0)
 -prune,--chainPruning <INT>             linear chain network pruning {0,1} (default: 1)
```

### Modules

The project follows the standard [Maven structure](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html), and you can build it from the parent [pom.xml](./pom.xml) file. It consists of the following modules:

| Module        | Description                                                            |
|---------------|------------------------------------------------------------------------|
| Algebra       | value definitions (scalar/vector/matrix...) with respective mathematical operations and functions |
| CLI           | simple command line interface to the framework, based on Apache commons' [CLI](http://commons.apache.org/proper/commons-cli/) |
| Drawing       | visualization of templates, groundings/inference and neural networks, based on DOT language ([Graphviz](https://graphviz.org/))                 |
| Frontend      | Python scripts for calling some high-level functionalities, reading results, and exporting neural nets to [Dynet](http://dynet.io/), based on [Py4J](https://www.py4j.org/) (in progress)|
| Learning      | high-level (supervised) machine learning definitions and functions |
| Logging       | simple logging + (color) formatting, based on the default java.util.logging library |
| Logic         | subsumption engine providing efficient first order logic grounding/inference, credit to [Ondrej Kuzelka](https://github.com/supertweety) |
| Logical       | the logical part of the integration containing logic-based structures and computation - i.e. weighted logic grounding, extension of the Logic module |
| Neural        | the neural part of the integration containing neural-based structures and computation - i.e. neural nets processing, backprop and classic DL stuff |
| Neuralization | the process of conversion from the logical to the neural structures |
| Parsing       | definition and parsing of the NeuraLogic language into internal representation, based on [ANTLR](https://www.antlr.org/)  |
| Pipelines     | high-level abstraction library for creating generic execution graphs, suitable for ML workflows (custom made) |
| Resources     | test resources, templates, datasets, etc.  |
| Settings      | central configuration/validation of all settings and input sources (files) |
| Utilities     | generic utilities (maths, java DIY, etc.), utilizing [Gson](https://github.com/google/gson) for serialization and [JMH](https://openjdk.java.net/projects/code-tools/jmh/) for microbenchmarking |
| Workflow      | specific building blocks for typical ML worflows used with this framework, based on the Pipelines module |

### Upcoming

- Documentation wiki
- Python API
    - a more user friendly frontend
    - plus integration to popular DL libraries
- Lambda calculus support in the language
- Structure learning module from the previous version

### Disclaimer
This is a second generation of the framework<sup>[3](#myfootnote2)</sup>, but it is still work in progress.
The framework is meant for academic purposes, developed at [Intelligent Data Analysis lab](https://ida.fel.cvut.cz/) at Czech Technical University in Prague.
In case of any questions or anything interesting, feel free to contact me at *souregus@gmail.com*, but please do not expect any sort of professional software support. If you are looking for something more conservative and user friendly, consider [PyG](https://pytorch-geometric.readthedocs.io/en/latest/notes/installation.html) or [DGL](https://www.dgl.ai/pages/start.html) for the graph-based learning use cases.

<a name="myfootnote3">3</a>: _for reference, you can find previous implementation (2014-2018) in [this repository](https://github.com/GustikS/Neurologic)_

#### Related work
###### Repositories

For experiments from the paper "Beyond GNNs with LRNNs", please see a separate repository: https://github.com/GustikS/GNNwLRNNs

###### Papers

googling "**Lifted Relational Neural Networks**" should give you a [short paper](http://ceur-ws.org/Vol-1583/CoCoNIPS_2015_paper_7.pdf) from a NIPS workshop on cognitive computation 2015,
or [long version](https://www.jair.org/index.php/jair/article/view/11203)  in Journal of Artificial Intelligence Research, 2018. 

Further, you can find applications and extensions, including declarative [learning scenarios](https://link.springer.com/chapter/10.1007/978-3-319-63342-8_9) and inductive [structure learning](https://link.springer.com/chapter/10.1007/978-3-319-78090-0_10) of the programs.

###### Videos
[NIPS CoCo workshop 2015](https://www.bilibili.com/video/av66440677/)

[Prague Machine learning meetups 2017](https://www.youtube.com/watch?v=LHj8M8SV1zA)

###### Presentations

[Prague Automated Reasoning Seminar 2016](http://arg.ciirc.cvut.cz/slides/2016-Sourek-LRNN.pdf)