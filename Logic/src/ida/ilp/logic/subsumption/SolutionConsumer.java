package ida.ilp.logic.subsumption;

import ida.ilp.logic.Term;

/**
 * Created by ondrejkuzelka on 12/06/16.
 */
public interface SolutionConsumer {

    public void solution(Term[] template, Term[] solution);

}
