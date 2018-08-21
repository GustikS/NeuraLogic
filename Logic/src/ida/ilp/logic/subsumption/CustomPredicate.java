package ida.ilp.logic.subsumption;

import ida.ilp.logic.Term;

/**
 * Created by ondrejkuzelka on 11/06/16.
 */
public interface CustomPredicate {

    public String name();

    public boolean isSatisfiable(Term...arguments);

}
