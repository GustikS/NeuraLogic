package grounding.bottomUp;

import ida.ilp.logic.*;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.Sugar;
import ida.utils.collections.MultiMap;
import ida.utils.tuples.Pair;

import java.util.Collection;
import java.util.List;

public class HerbrandModelTest {

    public static void main(String[] args) {
        List<Clause> rules = Sugar.list(
                Clause.parse("holdsK(S,P,O), !holdsL1(S,P,O)"),
                Clause.parse("similarK1s(A,B), !similar(A,B)"),
                Clause.parse("similarK1p(A,B), !similar(A,B)"),
                Clause.parse("similarK1o(A,B), !similar(A,B)"),
                Clause.parse("holdsL1(S,P,O), !similarK1s(S,concept_mammal_micinka),!similarK1p(P,generalizations),!similarK1o(O,concept_mammal_cats)"),
                Clause.parse("holdsK(S,P,O), !holdsL2(S,P,O)"),
                Clause.parse("similarK2s(A,B), !similar(A,B)"),
                Clause.parse("similarK2p(A,B),!similar(A,B)"),
                Clause.parse("similarK2o(A,B), !similar(A,B)"),
                Clause.parse("holdsL2(S,P,O),!similarK2s(S,concept_mammal_cats),!similarK2p(P,concept_mammalinducesemotion),!similarK2o(O,concept_emotion_fear)"),
                Clause.parse("holdsK(S,P,O),!holdsL3(S,P,O)"),
                Clause.parse("similarK3s(A,B),!similar(A,B)"),
                Clause.parse("similarK3p(A,B),!similar(A,B)"),
                Clause.parse("similarK3o(A,B),!similar(A,B)"),
                Clause.parse("holdsL3(S,P,O),!similarK3s(S,concept_mammal_tiger),!similarK3p(P,concept_mammalsuchasmammal),!similarK3o(O,concept_mammal_bear)"),
                Clause.parse("holdsK(A,B,C),!holdsLrek(A,B,C)"),
                Clause.parse("holdsLrek(A,B,C),!holdsK(A,generalizations,X), !holdsK(X,B,C)"),
                Clause.parse("finalL(dummy), !holdsK(S,P,O)"),
                Clause.parse("similar(X,X)")
        );

        Clause ground = Clause.parse("exists(concept_mammal_micinka,generalizations,concept_mammal_tiger,concept_mammalinducesemotion,concept_mammal_cats,concept_emotion_fear,concept_mammal_bear,concept_mammalsuchasmammal,concept_animalistypeofanimal)");

        for (Literal l : ground.literals()) {
            rules.add(new Clause(l));
        }

        int repeats = 1;
        MultiMap<Predicate, Literal> herbrand = null;
        long t1 = System.nanoTime();
        for (int i = 0; i < repeats; i++) {
            HerbrandModel bug = new HerbrandModel();
            herbrand = bug.inferModel(rules);
        }
        long t2 = System.nanoTime();
        Collection<Literal> literals = Sugar.flatten(herbrand.values());
        for (Literal c : literals) {
            System.out.println(c);
        }
        System.out.println(herbrand);
        System.out.println("Time: " + (t2 - t1) / (1e6 * repeats) + "ms");

        Matching m = new Matching();
        Clause rule = Clause.parse("holdsLrek(A,B,C), holdsK(A,generalizations,X), holdsK(X,B,C)");
        Pair<Term[], List<Term[]>> pair = m.allSubstitutions(rule, new Clause(literals));
        for (Term[] substitution : pair.s) {
            System.out.println(LogicUtils.substitute(rule, pair.r, substitution));
        }
    }

}