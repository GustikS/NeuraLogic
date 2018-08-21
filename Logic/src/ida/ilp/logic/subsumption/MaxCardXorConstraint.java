/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ida.ilp.logic.subsumption;

import ida.utils.VectorUtils;
import ida.utils.collections.IntegerSet;
import ida.utils.collections.ValueToIndex;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by ondrejkuzelka on 20/12/16.
 */
public class MaxCardXorConstraint implements GlobalConstraint {

    //key = index, value = id
    private ValueToIndex<Integer> termIdsToIndices = new ValueToIndex<Integer>();

    private boolean[] certainOnes;

    private boolean[] certainZeros;

    //[A,b], i.e. the last column is the right-hand side of the equation
    private BitSet[] system;

    private final static int SWAP = 1, ADD = 2, SET = 3, STOP = 4;

    private int maxCard;

    private int numConstants = -1;

    //[op,i,j] ~ perform op on i-th and j-th row
    //[SWAP,i,j]
    //[ADD,i,j] ~ add i-th row to j-th row
    //[SET,row,column,value]
    private Stack<int[]> history = new Stack<int[]>();

    private Stack<IntegerSet> termsInDomainsHistory = new Stack<IntegerSet>();

    private MaxCardXorConstraint(int maxCard, IntegerSet allRelevantTerms){
        this.maxCard = maxCard;
        this.certainOnes = new boolean[allRelevantTerms.size()];
        this.certainZeros = new boolean[allRelevantTerms.size()];
        this.termsInDomainsHistory.push(allRelevantTerms);
        int[] allRelevant = allRelevantTerms.values();
        for (int i = 0; i < allRelevant.length; i++){
            termIdsToIndices.put(i, allRelevant[i]);
        }
    }

    public MaxCardXorConstraint(int maxCard, int numConstants, IntegerSet allRelevantTerms, boolean[][] leftHandSystem, boolean[] rightHandSystem, SubsumptionEngineJ2.ClauseC clauseC){
        this(maxCard, allRelevantTerms);
        this.setXorSystem(leftHandSystem, rightHandSystem);
        this.numConstants = numConstants;
    }

    private void setXorSystem(boolean[][] left, boolean[] right){
        BitSet[] s = new BitSet[left.length];
        for (int i = 0; i < left.length; i++) {
            s[i] = VectorUtils.toBitSet(left[i]);
            s[i].set(left[i].length, right[i]);
        }
        this.system = s;
    }

    public IntegerSet[] propagate(IntegerSet[] newDomains){
//        System.out.println(Sugar.objectArrayToString(newDomains));
        this.history.push(new int[]{STOP});
        IntegerSet termsInDomains = IntegerSet.union(newDomains);
        IntegerSet oldTermsInDomains = termsInDomainsHistory.peek();
        this.termsInDomainsHistory.push(termsInDomains);
        IntegerSet toBeSetToFalse = IntegerSet.difference(oldTermsInDomains, termsInDomains);
        for (int tbstf : toBeSetToFalse.values()){
            int termIndex = this.termIdsToIndices.getIndex(tbstf);
            if (!set_simple(termIndex, false)){
                return null;
            }
        }
        for (int i = 0; i < newDomains.length; i++){
            if (newDomains[i].size() == 1){
                int termInDomain = newDomains[i].values()[0];
                if (this.termIdsToIndices.containsValue(termInDomain)) {
                    int termInDomainIndex = this.termIdsToIndices.getIndex(termInDomain);
                    if (!certainOnes[termInDomainIndex]) {
                        if (!set_simple(termInDomainIndex, true)) {
                            return null;
                        }
                    }
                }
            }
        }
        if (!transformToRowEchelon()){
            return null;
        }

//        int numForcedOnes = 0;
//        for (int i = 0; i < system.length; i++){
//            int forcedOne = forcedOne(i);
//            if (forcedOne != -1){
//                numForcedOnes++;
//            }
//        }
//        if (numForcedOnes > newDomains.length+numConstants){
//            return null;
//        }

        Set<Integer> forcedZerosSet = new HashSet<Integer>();
        for (int i = 0; i < system.length; i++){
            int forcedZero = forcedZero(i);
            if (forcedZero != -1){
                forcedZerosSet.add(forcedZero);
            }
        }
        IntegerSet forcedZeros = IntegerSet.createIntegerSet(forcedZerosSet);

        IntegerSet[] updatedDomains = new IntegerSet[newDomains.length];
        for (int i = 0; i < newDomains.length; i++){
            updatedDomains[i] = IntegerSet.difference(newDomains[i], forcedZeros);
        }
        return updatedDomains;
    }

    private boolean set_simple(int termIndex, boolean value){
        if (value) {
            this.certainOnes[termIndex] = true;
        }
        if (!value){
            this.certainZeros[termIndex] = false;
        }
        for (int i = 0; i < this.system.length; i++){
            if (this.system[i].get(termIndex)) {
                history.add(new int[]{SET, i, termIndex, value ? 1 : 0});
                this.system[i].set(termIndex, false);
                this.system[i].set(certainOnes.length, this.system[i].get(certainOnes.length) ^ value);
                if (!isRowConsistent(i)){
                    return false;
                }
            }
        }
        return true;
    }

    public void undoPropagation(){
        long m1 = System.nanoTime();
        int[] h;
        while ((h = history.pop()) != null && h[0] != STOP){
            switch (h[0]){
                case SWAP:
                    swap(h[2], h[1], false);
                    break;
                case ADD:
                    add(h[1], h[2], false);
                    break;
                case SET:
                    this.certainOnes[h[2]] = false;
                    this.certainZeros[h[2]] = false;
                    this.system[h[1]].set(h[2], true);
                    this.system[h[1]].set(certainOnes.length, this.system[h[1]].get(certainOnes.length) ^ (h[3] == 1));
                    break;
            }
        }
        this.termsInDomainsHistory.pop();
        long m2 = System.nanoTime();
        System.out.println("undoing propagation: "+(m2-m1)/1e6);
    }

    private boolean transformToRowEchelon(){
        for (int i = 0; i < system.length; i++) {
            int col = -1;
            int selected = 0;
            for (int j = i; j < system.length; j++) {
                int c = system[j].nextSetBit(0);
                //0 = 1
                if (c == certainOnes.length){
                    return false;
                }
                if (c != -1 && (c < col || col == -1)) {
                    selected = j;
                    col = c;
                }
            }
            if (col == -1){
                break;
            } else {
                if (selected > i){
                    swap(i, selected, true);
                }
                for (int j = i+1; j < system.length; j++){
                    if (system[j].get(col)){
                        add(i, j, true);
                    }
                }
            }
        }
        for (int i = 0; i < system.length; i++){
            if (!isRowConsistent(i)){
                return false;
            }
            int forcedOne = forcedOne(i);
            if (forcedOne != -1 && !this.certainOnes[forcedOne]){
                set_simple(forcedOne, true);
            }
        }
        return true;
    }

    private int forcedOne(int row){
        int firstOne = system[row].nextSetBit(0);
        if (firstOne == -1){
            return -1;
        }
        int secondOne = system[row].nextSetBit(firstOne+1);
        if (secondOne == certainOnes.length){
            return firstOne;
        }
        return -1;
    }

    private int forcedZero(int row){
        int firstOne = system[row].nextSetBit(0);
        if (firstOne == -1){
            return -1;
        }
        int secondOne = system[row].nextSetBit(firstOne+1);
        if (secondOne == -1){
            return firstOne;
        }
        return -1;
    }

    private void swap(int i, int j, boolean history){
        if (i != j){
            if (history) {
                this.history.add(new int[]{SWAP, i, j});
            }
            BitSet temp = this.system[i];
            this.system[i] = this.system[j];
            this.system[j] = temp;
        }
    }

    private void add(int what, int to, boolean history){
        if (history) {
            this.history.add(new int[]{ADD, what, to});
        }
        this.system[to].xor(this.system[what]);
    }

    private boolean isRowConsistent(int row){
        int firstOne = system[row].nextSetBit(0);
        //0 = 1
        if (firstOne == certainOnes.length){
            return false;
        }
        //0 = 0
        if (firstOne == -1){
            return true;
        }
        return true;
    }

    public boolean needsStrongPropagation(){
        return true;
    }

    public static void main(String[] args){
//        MaxCardXorConstraint constraint = new MaxCardXorConstraint();
//        boolean[][] b = new boolean[][]{
//                {true,true,false,true},
//                {true,false,false,true},
//                {false,true,true,true}
//        };
//        constraint.system = VectorUtils.toBitSets(b);
//        constraint.numVariables = 3;
//        constraint.maxCard = 2;
//        Boolean suc = constraint.transformToRowEchelon();
//        System.out.println(suc);
//        System.out.println(Sugar.objectArrayToString(constraint.system));
    }
}
