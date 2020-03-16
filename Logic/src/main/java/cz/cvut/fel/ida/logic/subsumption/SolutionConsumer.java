package cz.cvut.fel.ida.logic.subsumption;

import cz.cvut.fel.ida.logic.Term;

/**
 * Created by ondrejkuzelka on 12/06/16.
 */
public interface SolutionConsumer {

    public void solution(Term[] template, Term[] solution);

}
