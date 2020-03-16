/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cz.cvut.fel.ida.logic.special;

import cz.cvut.fel.ida.logic.Clause;
import cz.cvut.fel.ida.logic.Literal;
import cz.cvut.fel.ida.logic.Variable;
import cz.cvut.fel.ida.logic.subsumption.Matching;
import cz.cvut.fel.ida.utils.math.Sugar;
import cz.cvut.fel.ida.utils.math.collections.Counters;
import cz.cvut.fel.ida.utils.math.collections.MultiMap;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.security.MessageDigest;

/**
 * Created by kuzelkao_cardiff on 27/08/15.
 */
public class IsoClauseWrapper {

    private Clause originalClause;

    private Clause enrichedClause;

    public static BigInteger HASH_MOD = BigInteger.ONE.shiftLeft(32);

    private int hashCode;

    public IsoClauseWrapper(Clause clause){
        this.originalClause = clause;
        this.enrichedClause = this.preprocess(clause);
        this.hashCode = computeHashCode(this.enrichedClause);
    }

    public IsoClauseWrapper(Collection<Literal> literals){
        this(new Clause(literals));
    }

    private static int computeHashCode(Clause c){
        List<String> strs = new ArrayList<String>();
        for (Literal l : c.literals()){
            strs.add(l.predicateName());
        }
        Collections.sort(strs);
        StringBuilder sb = new StringBuilder();
        for (String str : strs){
            sb.append(str).append("@");
        }
        return hash(sb.toString()).hashCode();
    }

    private static Clause preprocess(Clause clause){
        Set<Literal> newLiterals = new HashSet<Literal>();
        Set<Literal> preprocessedLiterals = transformLiterals(clause.literals());
        Set<Literal> fingerPrints = initialFingerPrints(clause.literals());
        List<Integer> oldUnique = null;
        List<Integer> unique = uniquenessSpectrum(fingerPrints);
        do {
            oldUnique = unique;
            Set<Literal> newFingerprints = extendFingerprints(preprocessedLiterals, fingerPrints);
            unique = uniquenessSpectrum(newFingerprints);
            fingerPrints = newFingerprints;
        } while (!oldUnique.equals(unique));
        return new Clause(Sugar.iterable(preprocessedLiterals, fingerPrints));
    }

    private static List<Integer> uniquenessSpectrum(Set<Literal> fingerPrints){
        Counters<String> counters = new Counters<String>();
        for (Literal l : fingerPrints){
            counters.increment(l.predicateName());
        }
        List<Integer> counts = Sugar.listFromCollections(counters.counts());
        Collections.sort(counts);
        return counts;
    }

    private static Set<Literal> transformLiterals(Set<Literal> literals){
        Set<Literal> retVal = new HashSet<Literal>();
        for (Literal l : literals){
            int numVariables = 0;
            StringBuilder pred = new StringBuilder("~");
            if (l.isNegated()){
                pred.append("!");
            }
            for (int i = 0; i < l.arity(); i++){
                if (l.get(i) instanceof Variable){
                    numVariables++;
                } else {
                    pred.append(l.get(i)).append("@").append(i).append("@");
                }
            }
            pred.append(l.predicateName());
            Literal newLiteral = new Literal(pred.toString(), numVariables);
            int j = 0;
            for (int i = 0; i < l.arity(); i++){
                if (l.get(i) instanceof Variable) {
                    newLiteral.set(l.get(i), j++);
                }
            }
            retVal.add(newLiteral);
        }
        return retVal;
    }

    private static MultiMap<Variable,Variable> buildNeighbourhoods(Set<Literal> literals){
        MultiMap<Variable,Variable> neighbours = new MultiMap<Variable,Variable>();
        for (Literal l : literals){
            for (int i = 0; i < l.arity(); i++){
                if (l.get(i) instanceof Variable) {
                    for (int j = 0; j < l.arity(); j++) {
                        if (i != j && l.get(j) instanceof Variable){
                            neighbours.put((Variable)l.get(i), (Variable)l.get(j));
                        }
                    }
                }
            }
        }
        return neighbours;
    }

    private static Set<Literal> initialFingerPrints(Set<Literal> literals){
        Clause orig = new Clause(literals);
        Set<Literal> retVal = new HashSet<Literal>();
        for (Variable v : orig.variables()){
            List<String> inLitSignatures = new ArrayList<String>();
            for (Literal l : orig.getLiteralsByTerm(v)){
                StringBuilder sb = new StringBuilder();
                sb.append(l.predicateName()).append("@").append(l.arity()).append("@");
                for (int i = 0; i < l.arity(); i++){
                    if (l.get(i).equals(v)){
                        sb.append(i).append("@");
                    }
                }
                inLitSignatures.add(sb.toString());
            }
            Collections.sort(inLitSignatures);
            StringBuilder sb = new StringBuilder();
            for (String s : inLitSignatures){
                sb.append(s).append("@");
            }
            retVal.add(new Literal(hash(sb.toString()), v));
        }
        return retVal;
    }

    private static Set<Literal> extendFingerprints(Set<Literal> literals, Set<Literal> fingerprints){
        Map<Variable,Literal> fpsMap = new HashMap<Variable,Literal>();
        MultiMap<Variable,Variable> neighbours = buildNeighbourhoods(literals);
        for (Literal l : fingerprints){
            fpsMap.put((Variable) l.get(0), l);
        }
        Set<Literal> retVal = new HashSet<Literal>();
        for (Literal l : fingerprints){
            List<String> neighbourFps = new ArrayList<String>();
            Variable v = (Variable)l.get(0);
            for (Variable neighbour : neighbours.get(v)){
                String fp = fpsMap.get(neighbour).predicateName();
                neighbourFps.add(fp);
            }
            Collections.sort(neighbourFps);
            StringBuilder sb = new StringBuilder();
            for (String neighbourFp : neighbourFps){
                sb.append(neighbourFp);
            }
            sb.append(fpsMap.get(v).predicateName());
            retVal.add(new Literal(hash(sb.toString()), v));
        }
        return retVal;
    }

    private static String hash(String str){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            return new BigInteger(1, messageDigest.digest()).mod(HASH_MOD).toString(16);
        } catch (NoSuchAlgorithmException nsae) {
            return String.valueOf(str.hashCode());
        }
    }

    public int hashCode(){
        return this.hashCode;
    }

    public boolean equals(Object o){
        if (o instanceof IsoClauseWrapper){
            long m1 = System.nanoTime();
            IsoClauseWrapper icw = (IsoClauseWrapper)o;
            if (icw.hashCode() != this.hashCode() || icw.originalClause.countLiterals() != this.originalClause.countLiterals() ||
                    !icw.enrichedClause.predicates().equals(this.enrichedClause.predicates()) ||
                    icw.originalClause.variables().size() != this.originalClause.variables().size()){
                return false;
            }
            if (icw.originalClause.literals().equals(this.originalClause.literals())){
                return true;
            }
            Matching m = new Matching();
            m.setSubsumptionMode(Matching.OI_SUBSUMPTION);
            boolean result = m.subsumption(icw.enrichedClause, this.enrichedClause);
            return result;
        } else {
            return false;
        }
    }

    public Clause getOriginalClause(){
        return this.originalClause;
    }

    public String toString(){
        return this.enrichedClause.toString();
    }

    public static void main(String[] args){
        Clause c = Clause.parse("!b(Y,Z), b(Z,W), b(W,Y), b(W,A), c(x,y)");
        Clause d = Clause.parse("!b(Y,Z), b(Z,W), b(W,Y), b(W,A)");
        IsoClauseWrapper icw = new IsoClauseWrapper(c);
        System.out.println(icw.enrichedClause);
        System.out.println(c.equals(d));
    }
}
