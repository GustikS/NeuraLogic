package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.logic.subsumption.HerbrandModel;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpecialVarargPredicatesTest {

    @TestAnnotations.Fast
    void alldiff(){

        Clause example = Clause.parse(" bond(d1, d2), bond(d2, d1), bond(d2, d3), bond(d3, d2), bond(d3,d4), bond(d4, d1).");
        LinkedHashSet<Literal> facts = example.literals();

        List<HornClause> template = new ArrayList<>();
        HornClause finalRule;
        template.add(finalRule = new HornClause(Clause.parse(" ring(A,B,C,D), !bond(A,B), !bond(B,C), !bond(C,D), !bond(D,A), !@alldiff(A,B,C,D)")));

        HerbrandModel herbrandModel = new HerbrandModel();
        MultiMap<Predicate, Literal> predicateLiteralMultiMap = herbrandModel.inferModel(template, facts);
        cz.cvut.fel.ida.utils.generic.Pair<Term[], List<Term[]>> groundingSubstitutions = herbrandModel.groundingSubstitutions(finalRule);
        assertEquals(groundingSubstitutions.s.size(), 4);

    }

}