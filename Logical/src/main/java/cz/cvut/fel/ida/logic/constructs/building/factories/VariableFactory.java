package cz.cvut.fel.ida.logic.constructs.building.factories;

import cz.cvut.fel.ida.logic.Variable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * To keep track of created str2var in some local scope (for variable sharing)
 * Created by gusta on 5.3.18.
 */
public class VariableFactory {
    private static final Logger LOG = Logger.getLogger(VariableFactory.class.getName());

    private Map<String, Variable> str2var;
    private Map<Variable, Variable> var2var;

    public VariableFactory() {
        str2var = new HashMap<>();
        var2var = new HashMap<>();
    }

    public VariableFactory(Collection<Variable> vars) {
        str2var = vars.stream().collect(Collectors.toMap(Variable::toString, Function.identity()));
        var2var = vars.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
    }

    /**
     * Variable types are not part of its unique signature
     * - i.e. use different variable names for different types
     *
     * @param name
     * @param type
     * @return
     */
    public Variable construct(String name, String type) {
        Variable result = str2var.get(name);
        if (result == null) {
            result = Variable.construct(name, type);
            str2var.put(name, result);
            var2var.put(result, result);
        } else {
            result.setType(type);   // for mixing-up typed and untyped variables, the typed version wins
        }
        return result;
    }

    public Variable construct(String from) {
        if (from.contains(":")) {
            final String[] split = from.split(":");
            return construct(split[1], split[0]);
        }
        Variable result = str2var.get(from);
        if (result == null) {
            result = Variable.construct(from);
            str2var.put(from, result);
            var2var.put(result, result);
        }
        return result;
    }

    public Variable construct(Variable from) {
        Variable result = var2var.get(from);
        if (result == null) {
            str2var.put(result.toString(), result);
            var2var.put(result, result);
        }
        return from;
    }
}