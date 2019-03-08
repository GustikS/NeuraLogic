package grounding.bottomUp;

import ida.ilp.logic.Clause;
import ida.ilp.logic.HornClause;
import ida.ilp.logic.LogicUtils;
import ida.ilp.logic.Term;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.Sugar;
import ida.utils.tuples.Pair;
import org.junit.Test;

import java.util.List;

public class HerbrandModelTest {

    @Test
    public void testMatching() {
        Matching m = new Matching(Sugar.list(Clause.parse("p(a,b)")));
        HornClause hc = new HornClause(Clause.parse("q(X), !p(X,Y)"));
        System.out.println(hc);

        Pair<Term[], List<Term[]>> substA = m.allSubstitutions(hc.toClause(), 0, Integer.MAX_VALUE);
        System.out.println(substA.s.size() + "\thc.toClause()\t" + hc.toClause());
        for (Term[] img : substA.s) {
            System.out.println(LogicUtils.substitute(hc.toClause(), substA.r, img));
        }

        Pair<Term[], List<Term[]>> substB = m.allSubstitutions(LogicUtils.flipSigns(hc.toClause()), 0, Integer.MAX_VALUE);
        System.out.println(substB.s.size() + "\tLogicUtils.flipSings(hc.toClause())\t" + LogicUtils.flipSigns(hc.toClause()));
        for (Term[] img : substB.s) {
            System.out.println(LogicUtils.substitute(LogicUtils.flipSigns(hc.toClause()), substB.r, img));
        }
    }

}