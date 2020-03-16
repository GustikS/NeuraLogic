package cz.cvut.fel.ida.logic.subsumption;

import cz.cvut.fel.ida.logic.Term;

/**
 * Created by ondrejkuzelka on 11/06/16.
 */
public interface CustomPredicate {

    public String name();

    public boolean isSatisfiable(Term...arguments);

}
