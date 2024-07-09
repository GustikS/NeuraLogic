package cz.cvut.fel.ida.neuralogic.revised.unsorted;

import cz.cvut.fel.ida.logic.*;
import cz.cvut.fel.ida.logic.subsumption.HerbrandModel;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.logic.subsumption.SubsumptionEngineJ2;
import cz.cvut.fel.ida.utils.generic.TestAnnotations;
import cz.cvut.fel.ida.utils.generic.tuples.Pair;
import cz.cvut.fel.ida.utils.math.Sugar;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.generic.Benchmarking.benchmarkFast;
import static cz.cvut.fel.ida.utils.generic.Benchmarking.getMeanTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HerbrandModelTest {
    private static final Logger LOG = Logger.getLogger(HerbrandModelTest.class.getName());

    @TestAnnotations.Fast
    public void testMatchingNegations() {
        Matching m = new Matching(Sugar.list(Clause.parse("p(a,b)")));
        HornClause hc = new HornClause(Clause.parse("q(X), !p(X,Y)"));
        LOG.fine(hc.toString());

        Pair<Term[], List<Term[]>> substA = m.allSubstitutions(hc.toClause(), 0, Integer.MAX_VALUE);
        assertEquals(substA.s.size(), 0);
        LOG.fine(substA.s.size() + "\thc.toClause()\t" + hc.toClause());
        for (Term[] img : substA.s) {
            LOG.finer(LogicUtils.substitute(hc.toClause(), substA.r, img).toString());
        }

        Pair<Term[], List<Term[]>> substB = m.allSubstitutions(LogicUtils.flipSigns(hc.toClause()), 0, Integer.MAX_VALUE);
        LOG.fine(substB.s.size() + "\tLogicUtils.flipSings(hc.toClause())\t" + LogicUtils.flipSigns(hc.toClause()));
        assertEquals(substB.s.size(), 1);
        for (Term[] img : substB.s) {
            LOG.fine(LogicUtils.substitute(LogicUtils.flipSigns(hc.toClause()), substB.r, img).toString());
        }
    }

    /**
     * The subsumption engine ignores ground negations!
     */
    @TestAnnotations.Fast
    public void testGroundNegations() {
        Matching m = new Matching();
//        final Clause example = Clause.parse("body(d1), exists(d1)");
//        final Clause rule = Clause.parse("body(A), !exists(A)");
        final Clause example = Clause.parse("body(d1), exists()");
        final Clause rule = Clause.parse("body(A), exists()");

        Pair<Term[], List<Term[]>> substB = m.allSubstitutions(rule, example, Integer.MAX_VALUE);
        System.out.printf(substB.toString());
    }

    @TestAnnotations.Medium
    public void simpleSpeedTest() {
        BenchmarkState benchmarkState = new BenchmarkState();
        benchmarkState.setUp();
        long l = System.currentTimeMillis();
        testSubstitutionSpeedOLD(benchmarkState);
        long l1 = System.currentTimeMillis() - l;
        LOG.info("time old: " + l1);

        long l2 = System.currentTimeMillis();
        testSubstitutionSpeedNEW(benchmarkState);
        long l3 = System.currentTimeMillis() - l2;
        LOG.info("time new: " + l3);
    }

    @TestAnnotations.Slow
    public void benchmarkSpeedtest() throws RunnerException {
        Collection<RunResult> oldResults = benchmarkFast(getClass().getName() + ".testSubstitutionSpeedOLD");
        long oldTime = getMeanTime(oldResults, TimeUnit.MILLISECONDS);

        Collection<RunResult> newResults = benchmarkFast(getClass().getName() + ".testSubstitutionSpeedNEW");
        long newTime = getMeanTime(newResults, TimeUnit.MILLISECONDS);

        LOG.fine("old= " + oldTime + "ms vs " + newTime + "ms =new");
        assertTrue(oldTime > newTime);
    }

    @Benchmark
    public void testSubstitutionSpeedOLD(BenchmarkState benchmarkState) {

        for (int i = 0; i < 1000; i++) {
            for (Term[] terms : benchmarkState.groundingSubstitutions.s) {
                groundWithMapOLD(benchmarkState.testData.finalRule, benchmarkState.groundingSubstitutions.r, terms);
            }
        }
    }

    @Benchmark
    public void testSubstitutionSpeedNEW(BenchmarkState benchmarkState) {

        for (int i = 0; i < 1000; i++) {
            for (Term[] terms : benchmarkState.groundingSubstitutions.s) {
                ground(benchmarkState.testData.finalRule, terms);
            }
        }
    }

    private HornClause ground(HornClause hc, Term[] terms) {

        Literal groundHead = hc.head().subsCopy(terms);

        Set<Literal> literals = hc.body().literals();
        List<Literal> groundBody = new ArrayList<>(literals.size());
        for (Literal literal : literals) {
            groundBody.add(literal.subsCopy(terms));
        }

        return new HornClause(groundHead, new Clause(groundBody));
    }

    @Deprecated
    public HornClause groundWithMapNEW(HornClause hc, Term[] variables, Term[] terms) {

        Map<Term, Term> var2term = new HashMap<Term, Term>();
        for (int i = 0; i < variables.length; i++) {
            var2term.put(variables[i], terms[i]);
        }

        Literal groundHead = hc.head().subsCopy(var2term);

        Set<Literal> literals = hc.body().literals();
        List<Literal> groundBody = new ArrayList<>(literals.size());
        for (Literal literal : literals) {
            groundBody.add(literal.subsCopy(var2term));
        }

        return new HornClause(groundHead, new Clause(groundBody));
    }

    public HornClause groundWithMapOLD(HornClause hc, Term[] variables, Term[] terms) {

        Literal groundHead = LogicUtils.substitute(hc.head(), variables, terms);

        Set<Literal> literals = hc.body().literals();
        List<Literal> groundBody = new ArrayList<>(literals.size());
        for (Literal literal : literals) {
            groundBody.add(LogicUtils.substitute(literal, variables, terms));
        }

        return new HornClause(groundHead, new Clause(groundBody));
    }


    @State(Scope.Benchmark)
    public static class BenchmarkState {

        CreateTestData testData;
        Pair<Term[], List<Term[]>> groundingSubstitutions;

        @Setup(Level.Trial)
        public void setUp() {
            LOG.fine("Setting up the common benchmark state.");
            testData = new CreateTestData().invoke();
            groundingSubstitutions = testData.getGroundingSubstitutions();
        }
    }


    private static class CreateTestData {
        private HornClause finalRule;
        private cz.cvut.fel.ida.utils.generic.Pair<Term[], List<Term[]>> groundingSubstitutions;

        public HornClause getFinalRule() {
            return finalRule;
        }

        public cz.cvut.fel.ida.utils.generic.Pair<Term[], List<Term[]>> getGroundingSubstitutions() {
            return groundingSubstitutions;
        }

        public CreateTestData invoke() {
            Clause example = Clause.parse(" bond(d59_23, d59_5, 0), h_3(d59_23), c_22(d59_5), b1(0), bond(d59_5, d59_23, 0), bond(d59_20, d59_19, 1), c_21(d59_20), c_21(d59_19), b7(1), bond(d59_19, d59_20, 1), bond(d59_10, d59_3, 2), c_27(d59_10), c_27(d59_3), b7(2), bond(d59_3, d59_10, 2), bond(d59_14, d59_10, 3), c_25(d59_14), b7(3), bond(d59_10, d59_14, 3), bond(d59_4, d59_3, 4), c_22(d59_4), b7(4), bond(d59_3, d59_4, 4), bond(d59_10, d59_9, 5), c_27(d59_9), b7(5), bond(d59_9, d59_10, 5), bond(d59_15, d59_28, 6), c_22(d59_15), h_3(d59_28), b1(6), bond(d59_28, d59_15, 6), bond(d59_12, d59_13, 7), c_27(d59_12), c_27(d59_13), b7(7), bond(d59_13, d59_12, 7), bond(d59_8, d59_9, 8), c_22(d59_8), b7(8), bond(d59_9, d59_8, 8), bond(d59_7, d59_25, 9), c_22(d59_7), h_3(d59_25), b1(9), bond(d59_25, d59_7, 9), bond(d59_30, d59_17, 10), h_3(d59_30), c_22(d59_17), b1(10), bond(d59_17, d59_30, 10), bond(d59_15, d59_12, 11), b7(11), bond(d59_12, d59_15, 11), bond(d59_2, d59_1, 12), c_27(d59_2), c_22(d59_1), b7(12), bond(d59_1, d59_2, 12), bond(d59_17, d59_16, 13), c_22(d59_16), b7(13), bond(d59_16, d59_17, 13), bond(d59_31, d59_19, 14), h_3(d59_31), b1(14), bond(d59_19, d59_31, 14), bond(d59_9, d59_11, 15), c_22(d59_11), b7(15), bond(d59_11, d59_9, 15), bond(d59_3, d59_2, 16), b7(16), bond(d59_2, d59_3, 16), bond(d59_27, d59_11, 17), h_3(d59_27), b1(17), bond(d59_11, d59_27, 17), bond(d59_22, d59_4, 18), h_3(d59_22), b1(18), bond(d59_4, d59_22, 18), bond(d59_7, d59_8, 19), b7(19), bond(d59_8, d59_7, 19), bond(d59_24, d59_6, 20), h_3(d59_24), c_22(d59_6), b1(20), bond(d59_6, d59_24, 20), bond(d59_15, d59_16, 21), b7(21), bond(d59_16, d59_15, 21), bond(d59_7, d59_2, 22), b7(22), bond(d59_2, d59_7, 22), bond(d59_18, d59_20, 23), c_26(d59_18), b7(23), bond(d59_20, d59_18, 23), bond(d59_18, d59_17, 24), b7(24), bond(d59_17, d59_18, 24), bond(d59_18, d59_13, 25), b7(25), bond(d59_13, d59_18, 25), bond(d59_32, d59_33, 26), n_38(d59_32), o_40(d59_33), b2(26), bond(d59_33, d59_32, 26), bond(d59_5, d59_6, 27), b7(27), bond(d59_6, d59_5, 27), bond(d59_6, d59_1, 28), b7(28), bond(d59_1, d59_6, 28), bond(d59_19, d59_14, 29), b7(29), bond(d59_14, d59_19, 29), bond(d59_29, d59_16, 30), h_3(d59_29), b1(30), bond(d59_16, d59_29, 30), bond(d59_11, d59_12, 31), b7(31), bond(d59_12, d59_11, 31), bond(d59_14, d59_13, 32), b7(32), bond(d59_13, d59_14, 32), bond(d59_26, d59_8, 33), h_3(d59_26), b1(33), bond(d59_8, d59_26, 33), bond(d59_32, d59_34, 34), o_40(d59_34), b2(34), bond(d59_34, d59_32, 34), bond(d59_32, d59_20, 35), b1(35), bond(d59_20, d59_32, 35), bond(d59_1, d59_21, 36), h_3(d59_21), b1(36), bond(d59_21, d59_1, 36), bond(d59_5, d59_4, 37), b7(37), bond(d59_4, d59_5, 37)");
            LinkedHashSet<Literal> facts = example.literals();

            List<HornClause> rules = new ArrayList<>();
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_27(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_25(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_29(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_42(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_40(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !i_95(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !f_92(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !h_1(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !h_3(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_195(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !h_8(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_19(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_230(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_232(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_50(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !n_36(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_52(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !n_34(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !n_32(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !cl_93(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_22(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !n_38(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_26(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_28(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_49(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !br_94(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_45(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_41(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_10(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_14(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_194(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_16(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !n_35(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !o_51(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !n_31(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A1(A), !c_21(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_27(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_25(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_29(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_42(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_40(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !i_95(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !f_92(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !h_1(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !h_3(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_195(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !h_8(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_19(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_230(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_232(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_50(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !n_36(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_52(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !n_34(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !n_32(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !cl_93(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_22(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !n_38(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_26(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_28(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_49(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !br_94(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_45(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_41(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_10(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_14(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_194(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_16(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !n_35(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !o_51(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !n_31(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A2(A), !c_21(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_27(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_25(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_29(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_42(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_40(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !i_95(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !f_92(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !h_1(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !h_3(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_195(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !h_8(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_19(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_230(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_232(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_50(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !n_36(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_52(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !n_34(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !n_32(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !cl_93(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_22(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !n_38(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_26(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_28(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_49(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !br_94(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_45(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_41(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_10(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_14(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_194(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_16(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !n_35(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !o_51(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !n_31(A)")));
            rules.add(new HornClause(Clause.parse(" atomKappa_A3(A), !c_21(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A1(A), !b1(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A1(A), !b2(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A1(A), !b3(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A1(A), !b4(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A1(A), !b5(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A1(A), !b7(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A2(A), !b1(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A2(A), !b2(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A2(A), !b3(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A2(A), !b4(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A2(A), !b5(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A2(A), !b7(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A3(A), !b1(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A3(A), !b2(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A3(A), !b3(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A3(A), !b4(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A3(A), !b5(A)")));
            rules.add(new HornClause(Clause.parse(" bondKappa_A3(A), !b7(A)")));

            rules.add(finalRule = new HornClause(Clause.parse("lambda_A0(a), !atomKappa_A1(A), !bond(A,B,B0), !bondKappa_A1(B0), !atomKappa_A1(B), !bond(B,C,B1), !bondKappa_A1(B1), !atomKappa_A1(C)")));

            HerbrandModel herbrandModel = new HerbrandModel(facts, rules);
            final Collection<Literal> inferedAtoms = herbrandModel.inferAtoms();

            groundingSubstitutions = herbrandModel.groundingSubstitutions(new Clause(finalRule.getLiterals()));
            return this;
        }
    }
}