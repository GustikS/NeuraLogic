package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.logic.subsumption.HerbrandModel;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpecialVarargPredicatesTest {

    @TestAnnotations.Fast
    void alldiff() {

        Clause example = Clause.parse(" bond(d1, d2), bond(d2, d1), bond(d2, d3), bond(d3, d2), bond(d3,d4), bond(d4, d1).");
        LinkedHashSet<Literal> facts = example.literals();

        List<HornClause> template = new ArrayList<>();
        HornClause finalRule;
        template.add(finalRule = new HornClause(Clause.parse(" ring(A,B,C,D), !bond(A,B), !bond(B,C), !bond(C,D), !bond(D,A), !@alldiff(A,B,C,D)")));

        test(facts, template, finalRule, 4);
        template.remove(finalRule);
        template.add(finalRule = new HornClause(Clause.parse(" ring(A,B,C,D), !bond(A,B), !bond(B,C), !bond(C,D), !bond(D,A)")));
        test(facts, template, finalRule, 12);
    }

    @TestAnnotations.Fast
    void leq() {

        Clause example = Clause.parse(" bond(d1, d2), num(1), num(2), num(3).");
        LinkedHashSet<Literal> facts = example.literals();

        List<HornClause> template = new ArrayList<>();
        HornClause finalRule;
        template.add(finalRule = new HornClause(Clause.parse(" lessEq(A,B), !@leq(A,B)")));

        test(facts, template, finalRule, 15);
    }

    @TestAnnotations.Fast
    void le() {

        Clause example = Clause.parse(" bond(d1, d2), num(1), num(2), num(3).");
        LinkedHashSet<Literal> facts = example.literals();

        List<HornClause> template = new ArrayList<>();
        HornClause finalRule;
        template.add(finalRule = new HornClause(Clause.parse(" less(A,B), !@lt(A,B)")));

        test(facts, template, finalRule, 10);
    }

    @TestAnnotations.Fast
    void eq() {

        Clause example = Clause.parse(" bond(d1, d2), num(1), num(2), num(3).");
        LinkedHashSet<Literal> facts = example.literals();

        List<HornClause> template = new ArrayList<>();
        HornClause finalRule;
        template.add(finalRule = new HornClause(Clause.parse(" less(A,B), !@eq(A,B)")));

        test(facts, template, finalRule, 5);       // 5 * 4 others
    }

    @TestAnnotations.Fast
    void neq() {

        Clause example = Clause.parse(" bond(d1, d2), num(1), num(2), num(3).");
        LinkedHashSet<Literal> facts = example.literals();

        List<HornClause> template = new ArrayList<>();
        HornClause finalRule;
        template.add(finalRule = new HornClause(Clause.parse(" less(A,B), !@neq(A,B)")));

        test(facts, template, finalRule, 20);       // 5 * 4 others
    }

    @TestAnnotations.Fast
    void next() {

        Clause example = Clause.parse(" bond(d1, d2), num(1), num(2), num(3).");
        LinkedHashSet<Literal> facts = example.literals();

        List<HornClause> template = new ArrayList<>();
        HornClause finalRule;
        template.add(finalRule = new HornClause(Clause.parse(" nextOne(A,B), !@next(A,B)")));

        test(facts, template, finalRule, 2);       // (1,2) (2,3)
    }

    private void test(LinkedHashSet<Literal> facts, List<HornClause> rules, HornClause finalRule, int number) {
        HerbrandModel herbrandModel = new HerbrandModel(facts, rules);
        final Collection<Literal> inferedAtoms = herbrandModel.inferAtoms();
        cz.cvut.fel.ida.utils.generic.Pair<Term[], List<Term[]>> groundingSubstitutions = herbrandModel.groundingSubstitutions(new Clause(finalRule.getLiterals()));
        assertEquals(number, groundingSubstitutions.s.size());
    }

}