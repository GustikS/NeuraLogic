package cz.cvut.fel.ida.logic.features.generation;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.features.treeliker.Example;
import cz.cvut.fel.ida.logic.features.treeliker.PredicateDefinition;
import cz.cvut.fel.ida.logic.features.treeliker.Preprocessor;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.*;

/**
 * [[preprocessed templates], [preprocessed global constants], dataset]
 */
public class PreprocessedInput {

    private final List<Clause> originalData;

    private final List<Set<PredicateDefinition>> templates;
    private final List<PredicateDefinition> globalConstants;
    private final NoClassMemBasedDataset dataset;


    public PreprocessedInput(String template, List<Clause> dataset) {
        this.originalData = new ArrayList<>(dataset);

        this.templates = new ArrayList<>();
        this.globalConstants = new ArrayList<>();
        this.dataset = new NoClassMemBasedDataset();

        Set<PredicateDefinition> literalDefinitions = PredicateDefinition.parseDefinition(template);

        detectGlobalConstants(literalDefinitions);
        Set<PredicateDefinition> modifiedTemplate = preprocessData(literalDefinitions);
        buildConnectedComponents(modifiedTemplate);
    }

    public List<Clause> getOriginalData() {
        return originalData;
    }

    public List<Set<PredicateDefinition>> getTemplates() {
        return templates;
    }

    public List<PredicateDefinition> getGlobalConstants() {
        return globalConstants;
    }

    public NoClassMemBasedDataset getDataset() {
        return dataset;
    }

    private void detectGlobalConstants(Set<PredicateDefinition> literalDefinitions) {
        for (PredicateDefinition def : literalDefinitions) {
            if (def.isGlobalConstant()) {
                globalConstants.add(def);
            }
        }
    }

    private Set<PredicateDefinition> preprocessData(Set<PredicateDefinition> template) {
        Set<PredicateDefinition> modifiedTemplate = new LinkedHashSet<PredicateDefinition>();
        template = preprocessDefinitions(template);

        for (Clause clause : originalData) {
            Clause reachableExample = Preprocessor.reachableLiterals(template, globalConstants, clause);
            Set<Literal> literals = new LinkedHashSet<>();
            Set<String> constants = new LinkedHashSet<>();

            for (PredicateDefinition def : template) {
                Collection<Literal> eLiterals = reachableExample.getLiteralsByPredicate(def.stringPredicate());
                PredicateDefinition cloned = def.cloneDefinition();

                for (int i = 0; i < def.modes().length; i++) {
                    if (def.modes()[i] == PredicateDefinition.CONSTANT) {
                        cloned.setMode(PredicateDefinition.OUTPUT, i);
                        for (Literal constLit : eLiterals) {
                            StringBuilder sb = new StringBuilder();
                            try {
                                sb.append(constLit.get(i).toString());
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.err.println("Problem with " + reachableExample);
                            }
                            constants.add(constLit.get(i).toString());
                            sb.append("(+");
                            sb.append(def.stringType(i));
                            sb.append("[1]), ");
                            PredicateDefinition constantDefinition = Sugar.chooseOne(PredicateDefinition.parseDefinition(sb.toString()));
                            if (constantDefinition != null) {
                                boolean prune = false;
                                if (!prune) {
                                    constantDefinition.setConstant(true);
                                    modifiedTemplate.add(constantDefinition);
                                }
                            }
                            Literal newLiteral = new Literal(constLit.get(i).toString(), 1);
                            newLiteral.set(constLit.get(i), 0);
                            literals.add(newLiteral);
                        }
                    }
                }

                modifiedTemplate.add(cloned);
            }

            literals.addAll(reachableExample.literals());
            this.dataset.addExample(new Example(new Clause(literals)));
        }

        return modifiedTemplate;
    }

    private Set<PredicateDefinition> preprocessDefinitions(Set<PredicateDefinition> definitions) {
        Set<PredicateDefinition> retVal = new HashSet<PredicateDefinition>();

        for (PredicateDefinition def : definitions) {
            PredicateDefinition clone = def.cloneDefinition();
            int[] modes = def.modes();

            for (int i = 0; i < def.arity(); i++) {
                if (modes[i] == PredicateDefinition.IDENTIFIER || modes[i] == PredicateDefinition.CLASS) {
                    clone.setMode(PredicateDefinition.IGNORE, i);
                }
            }

            retVal.add(clone);
        }

        return retVal;
    }

    private void buildConnectedComponents(Set<PredicateDefinition> modifiedTemplate) {
        List<PredicateDefinition> outputOnlyLiteralDefinitions = new ArrayList<PredicateDefinition>();
        Set<PredicateDefinition> remainingLiteralDefinitions = new LinkedHashSet<PredicateDefinition>();
        for (PredicateDefinition def : modifiedTemplate) {
            if (def.isOutputOnly()) {
                outputOnlyLiteralDefinitions.add(def);
            } else {
                remainingLiteralDefinitions.add(def);
            }
        }

        for (PredicateDefinition output : outputOnlyLiteralDefinitions) {
            this.templates.add(Sugar.setFromCollections(connectedComponent(output, remainingLiteralDefinitions), Sugar.set(output)));
        }
    }

    private Set<PredicateDefinition> connectedComponent(PredicateDefinition output, Set<PredicateDefinition> other){
        Set<PredicateDefinition> closed = new HashSet<PredicateDefinition>();
        MultiMap<String,PredicateDefinition> inputToPredicate = new MultiMap<String,PredicateDefinition>();
        for (PredicateDefinition def : other){
            String inputType = def.stringType(def.input());
            inputToPredicate.put(inputType, def);
        }
        Stack<PredicateDefinition> open = new Stack<PredicateDefinition>();
        open.push(output);
        while (open.size() > 0){
            PredicateDefinition def = open.pop();
            closed.add(def);
            for (int i = 0; i < def.modes().length; i++){
                if (def.modes()[i] == PredicateDefinition.OUTPUT){
                    for (PredicateDefinition d : inputToPredicate.get(def.stringType(i))){
                        if (!closed.contains(d)){
                            open.push(d);
                        }
                    }
                }
            }
        }
        return closed;
    }
}
