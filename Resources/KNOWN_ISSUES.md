# Known issues and open questions

Everything found so far, whether fixed or not, so that nothing gets re-derived or re-reported. Items came
either from an audit of this repository or from a set of standalone reproducers written against
`neuralogic==0.9.0` from a deep-knowledge-tracing project; the provenance is noted where it matters, because
the ones from the reproducer suite were seen through the Python bindings and are marked accordingly.

Status words are used strictly: **measured** means it was reproduced and the numbers are in the commit,
**reasoned** means it follows from reading the code and was never run.

## Fixed

| What | Where | Commit |
| --- | --- | --- |
| `mvn` could not build the project at all - `Logical` used `FactNeuron` without declaring `Neural`, so the reactor died before `Neural`, `Neuralization`, `Parsing`, `Workflow` and `CLI` | `Logical/pom.xml` | `7e9a4c54` |
| A minibatch update was not the sum of its samples' updates, varied between identical runs, and threw with enough sharing. Samples were run through a parallel stream while sharing neuron computation state | `MiniBatchTrainer` | `52ddaf8a` |
| The batch accumulator aliased the first contributing trainer's gradient buffer and summed into it in place | `MiniBatchTrainer` | `52ddaf8a` |
| Evaluating or validating at batch size > 1 trained the model - it went through `learnFromSample`, which backpropagates and steps the optimizer. Measured: a single test pass moved the weights by `3.5e-3` | `MiniBatchTrainer` | `95eb007e` |
| Exporting a model died on `NotSerializableException: Clause` - grounding caches added to `Template` were never marked transient | `Template`, `BottomUp` | `42a11fc6` |
| Grounding depended on what had run before in the same JVM. `Literal.setTerms` did not invalidate the cached hashCode, and stratified negation reuses one static `Literal` as a key, so after the first call it probed the wrong bucket and stopped pruning. Measured: 7 inferred atoms instead of 6 on `debug/leaves` | `Literal`, `HerbrandModel.TupleNotIn` | `05da43a9` |
| Query importance was stored on `Query`, passed to `QueryNeuron`, exposed through `getImportance`, and read by nothing. Training was bit-identical at importance 0.5 and 1.0 | `Backpropagation`, `Result`, `Evaluation` | `5e3f4ca1`, `522c5c7f` |
| The mean error divided by the sample count rather than the total weight, so scaling every importance scaled the reported mean | `ClassificationResults` | `ac606157` |
| Tests could not reflect into `java.io` on JDK 16+ | `pom.xml` surefire | `3c7c4e3c` |
| `state_dict()` tested `weight.isLearnable` without calling it, so fixed weights were reported as learnable and the internal fixed `ONE` leaked at index `-1` | PyNeuraLogic `neural_module.py` | `fae064b` on `gustiks-bugfixes-ai` |
| Torch bridge `zero_grad`, rectangular tensor sync, and rule-form importance construction | PyNeuraLogic | upstream PR #68 |
| Output function inference replaced a transformation the template had stated, so a queried head could not say it is already the final quantity and an output that is a mean of probabilities could not be written. Templates that state nothing are unaffected | `NeuralProcessingSample`, `NeuronFactory` | `7719c9dc` |

The first five landed on `release`; the rest are on `bugfixes-ai`.

## Open - correctness

**Parallel grounding cannot be turned on.** Forced off in `Settings.infer()` (`fa4c30d9`). The grounder keeps
its working state on the shared `Template`: one `HerbrandModel` that every example adds rules and facts to and
then clears again, plus lazily built `clause`, `clauseE`, `atomMapCache` and the fact neuron caches, all
guarded by plain null checks. Stratified negation additionally goes through one static `Literal` used as a
mutable key. **Measured**: three runs over mutagenesis gave three different outcomes - a misleading
`Query [predict] not matched anywhere in the template`, and twice an `ArrayIndexOutOfBoundsException`. To allow
it again the grounder needs that state per thread or per sample rather than per template.

**Parallel training cannot be turned on.** Forced off in the same commit. `StatesBuilder.makeParallel` is meant
to give each trainer index its own computation state per neuron, but **measured** zero such states on the
networks that actually get trained, even with the flag on - so every index resolved to the same state.
`State.Neural.getComputationView(int)` defaults to returning that single state, which is why an unprepared
network raced silently instead of failing. Restoring it needs the composite states built for the trained
networks and sized to the training batch size rather than to `minibatchSize` as of neuralization time.

**`MiniBatchTrainer.evaluateAndBackprop` has no null-query-neuron guard.** `SequentialListTrainer.learnEpoch`
skips samples whose `query.neuron` is null, and `Neuralizer` does create such samples for examples without a
query head. The minibatch path has no such check. **Measured**: given such a sample, the sequential trainer
skips it and the minibatch one throws `NullPointerException: Cannot invoke AtomNeurons.getComputationView(int)
because outputNeuron is null`.

**`calculateErrorValue()` is not idempotent.** `ClassificationResults` rewrites the outputs in place while
computing its metrics - `loadBinaryMetrics` applies a sigmoid and `loadMulticlassMetrics` a softmax when
`squishLastLayer` is set. `recalculate()` computes the error first, so the stored value is right, but any later
call answers differently. **Measured**: outputs `0, 1, 0.7` became `0.5, 0.73, 0.67`. Fixing it means deciding
whether the metrics should work on copies, which changes what reporting does.

**Importing `Sources` from JSON fails on JDK 16+.** Gson reads the private fields of `java.io.File`. The
surefire flag fixes the build only; at runtime this still needs the same `--add-opens` or a Gson type adapter
for `File`.

## Open - design

**`Neuralizer.getDetailedNetwork` infers a lifecycle from data.** Whether to build the fact neurons is decided
by asking if the shared `neuronFactory.neuronMaps` happens to be empty. That is right for a process that
grounds one template once, and wrong the second time a workflow runs in the same JVM. An explicit piece of
state owned by the template or the run would say what is meant.

**`HerbrandModel.TupleNotIn` keeps a static `Literal` as its lookup key** and rewrites `predicate.name` and
`arity` on it per call. Correct today only because the key is strictly transient and the hash is now
invalidated. One instance per predicate exists anyway, so this could simply be an instance field.

**`SubsumptionEngineJ2` holds static scratch `Term[]` arrays** filled in place and handed to
`CustomPredicate.isSatisfiable`. Only one implementation exists and it does not retain them, so this is safe
today, but it makes the matcher non-reentrant.

**`MultiMap.get()` returns a shared mutable empty set** for missing keys. Nothing mutates it today; anything
that did would make that map answer every later miss with those elements.

**`IntegerSet.union` returns one of its arguments** when the other is empty, and the grounder shares instances
on that basis (`ClauseE.copy` passes `allTerms` and `predicates` straight through). Sound only while
`IntegerSet` has no mutators, which it currently does not.

**`BaseNeuron.equals` compares only `index`**, while `SameQueryAggregationPipe` synthesises a lookup key from
`query.position` - a different numbering space. A collision would silently merge two samples. Narrow, and
**reasoned** rather than seen.

**`NeuralNetwork.hasSharedNeurons` is written and never read**, and is only set inside the `parallelTraining`
branch that is now off.

## Open - from the reproducer suite

Characterised in a separate project through the Python bindings, and **re-run against this branch**: the ones
below still reproduce, while `query_importance_rule`, both torch bridge cases, `state_dict_learnable_filter`,
`internal_one_state` and `lossy_compression_diagnostic` no longer do. Each of the survivors needs an owner's
decision on the intended semantics before it can be called a bug.

**A hidden selector preserves the forward value but its fact gets no gradient.** Differentiation semantics
question.

**`ONE * vector` logs SEVERE** although it is a correct differentiable identity - `VectorValue.elementMultiplyBy`
complains about incompatible dimensions. Diagnostic noise on a supported operation.

**Pruning and compression emit an explicit lossy-compression warning** on inputs where the compression was
measured to be exact. Unclassified.

**Validation through `Trainer.fit` changes Adam state despite restoring weights.** It was tempting to call
this the same root cause as batched evaluation training the model, fixed in `95eb007e`. It is not: re-run
against this branch, it still reproduces. Cause unknown.

**Query importance still does not reach the Torch bridge.** `Backpropagate(NeuralSample, Value)` takes a
gradient the caller computed, so `NeuralModule._backprop` bypasses the weighting deliberately. Whether the
bridge should apply it is a decision, not an oversight.

## Open - diagnostics

**A target of the wrong shape is only found out during backpropagation.** A rule with a `1 x 2` head weight
over a two-element body produces a scalar, so a vector target cannot fit - and nothing says so until the
backward pass throws `Incompatible dimensions of algebraic operation - scalar increment by vector`, which
names neither the query nor the target. **Measured** on a weighted head with a vector target, in both body
orders. Comparing the shape of the target against the shape the queried neuron produces, at build time, would
turn this into one clear message.

## Ideas rather than defects

**Combining a body of mixed shapes tries the impossible direction first.** `ElementProduct`, `Sum` and
`Average` take the accumulator's shape from the *first* body atom (`inputs.get(0).clone()`), so a body of
scalar-then-vector attempts an in-place widening, throws, is caught, and redone out of place, while
vector-then-scalar goes straight through. **Measured**: the outcome is the same either way - identical forward
values and identical weights after a step, for both ELPRODUCT and SUM, weighted and plain heads - so this
costs a thrown exception and a retry, not correctness. Picking the widest input to start from would avoid the
detour.


**Deciding between logits and probabilities is spread over four places.** Whether a queried output ends up
raw or squashed is settled by `inferOutputFcns`, by `squishLastLayer`, by `infer()` choosing between
`CROSSENTROPY` and `SOFTENTROPY`, and finally by the rewrite in `NeuralProcessingSample` as each sample's
network is finalised. The intent is good - a template should not have to know what the loss expects - but the
decision is hard to follow and harder to predict, and the three branches of that final rewrite each respect a
different set of transformations: regression skips `null`, `Identity` and `ReLu`, softentropy skips `null` and
`Identity`, crossentropy skips only `Softmax` and `Sigmoid`. Pulling it into one decision in one place would
make it explainable. Nothing is broken, so this is an improvement rather than a fix.

## Deliberately not issues

- **Query-only constants do not enter an example's Herbrand domain.** Treated as the grounding contract; the
  example has to seed the constant.
- **A valued selector adds the logical `ONE` under `SUM`.** Correct LRNN numeric semantics; the reproducer
  that found it was a template mistake.
- **`Literal`'s hash is plainly multiplicative** rather than multiply-add. The better variant was reverted in
  Jan 2026 for a breakage that turned out to be an off-by-two in high arity matching, fixed in the very next
  commit - but **measured** on 38400 literals of arity up to 12 the difference is 0.22% collisions against 0%,
  worst bucket 3 against 1, and nothing at arity 4 and below. Not worth changing a hash in the grounding core.
- **`Weight.isLearnable` as a primitive with a `learnableSet` flag** was checked and is sound; the only direct
  write to the field is its own constructor.

## A trap worth knowing about

The reproducer cases call `neuralogic.initialize()` without a jar path, so they load the jar bundled inside
whichever `neuralogic` package is first on `PYTHONPATH`. Pointing `PYTHONPATH` at a frontend checkout
therefore silently swaps the backend too - a checkout ships the released jar. That produced a convincing but
entirely false "the newer frontend breaks batch accumulation" reading here, until the same comparison was run
with the jar passed explicitly and both frontends came out exact. Pass the jar explicitly when comparing.

## A note on coverage

The optimisation work merged in June 2026 spans 40 commits, 62 files and about 3000 added lines - and touched
two test files for a net of minus three lines. `IntegerSet`, `MultiMap`, `ValueToIndex`, `VectorSet` and
`Counters` were rewritten with no tests of their own; `CollectionPropertiesTest` now checks them
differentially against `java.util`. The `Neural` module, which holds the whole training engine, had exactly
one test before this branch.
