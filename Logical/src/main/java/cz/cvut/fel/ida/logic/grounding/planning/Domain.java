package cz.cvut.fel.ida.logic.grounding.planning;

import cz.cvut.fel.ida.logic.Constant;
import cz.cvut.fel.ida.logic.Predicate;

import java.util.Map;
import java.util.Set;

public class Domain {
    Set<Constant> objects;
    Set<Predicate> predicates;
    /**
     * type -> supertype
     */
    Map<String, String> types;
    Map<Constant, String> objectTypes;

}
