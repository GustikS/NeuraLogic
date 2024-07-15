/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.logic.features.treeliker.impl;

import cz.cvut.fel.ida.logic.features.treeliker.*;
import cz.cvut.fel.ida.utils.math.Sugar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation class for computing domains of Blocks and Joins.
 * 
 * @author Ondra
 */
public class DomainComputing {
    
    private Dataset examples;
    
    /**
     * Creates a new instance of class DomainComputing.
     * @param examples the set of examples for which we will want to compute the domains.
     */
    public DomainComputing(Dataset examples){
        this.examples = examples;
    }
    
    /**
     * Computes term domains for an object HavingTermDomain for all examples in the dataset.
     * @param htd object HavingTermDomain for all examples in the dataset
     * @return computed domains
     */
    public Domain[] computeTermDomains(HavingTermDomain htd){
        Domain[] domains = new Domain[examples.countExamples()];
        int index = 0;
        Dataset localExamples = examples.shallowCopy();
        localExamples.reset();
        while (localExamples.hasNextExample()){
            Example example = localExamples.nextExample();
            domains[index] = htd.termDomain(example);
            index++;
        }
        return domains;
    }

    /**
     * Computes literal-domains for an object HavingLiteralDomain for all examples in the dataset.
     * @param hld object HavingLiteralDomain for all examples in the dataset
     * @return computed literal-domains
     */
    public Domain[] computeLiteralDomains(HavingLiteralDomain hld){
        Domain[] domains = new Domain[examples.countExamples()];
        int index = 0;
        Dataset localExamples = examples.shallowCopy();
        localExamples.reset();
        while (localExamples.hasNextExample()){
            Example example = localExamples.nextExample();
            domains[index] = hld.literalDomain(example);
            index++;
        }
        return domains;
    }

    /**
     * Computes term-domains for a set of objects HavingTermDomain for all examples in the dataset.
     * It uses multiple threads to exploit modern multi-core processors.
     * @param candidates collection of objects HavingTermDomain for all examples in the dataset
     * @return computed domains
     */
    public ConcurrentHashMap<Integer,Domain[]> computeTermDomainsInParallel(Collection<? extends HavingTermDomain> candidates){
        ConcurrentHashMap<Integer,Domain[]> domains = new ConcurrentHashMap<Integer,Domain[]>();
        for (HavingTermDomain htd : candidates) {
            domains.put(htd.id(), new Domain[examples.countExamples()]);
        }
        List<Runnable> tasks = new ArrayList<Runnable>();
        Dataset[] exs = this.examples.split(Runtime.getRuntime().availableProcessors());
        for (Dataset es : exs){
            tasks.add(new TermDomainsComputation(candidates, es, domains));
        }
        Sugar.runInParallel(tasks);
        return domains;
    }

    /**
     * Computes literal-domains for a set of objects HavingLiteralDomain for all examples in the dataset.
     * It uses multiple threads to exploit modern multi-core processors.
     * @param candidates collection of objects HavingLiteralDomain for all examples in the dataset
     * @return computed domains
     */
    public ConcurrentHashMap<Integer,Domain[]> computeLiteralDomainsInParallel(Collection<? extends HavingLiteralDomain> candidates){
        ConcurrentHashMap<Integer,Domain[]> domains = new ConcurrentHashMap<Integer,Domain[]>();
        for (HavingLiteralDomain hld : candidates) {
            domains.put(hld.id(), new Domain[examples.countExamples()]);
        }
        List<Runnable> tasks = new ArrayList<Runnable>();
        Dataset[] exs = this.examples.split(Runtime.getRuntime().availableProcessors());
        for (Dataset es : exs){
            tasks.add(new LiteralDomainsComputation(candidates, es, domains));
        }
        Sugar.runInParallel(tasks);
        return domains;
    }
    
    private void computeTermDomains(Collection<? extends HavingTermDomain> candidates, Dataset examples, Map<Integer,Domain[]> domains){
        Dataset localExamples = examples.shallowCopy();
        localExamples.reset();
        while (localExamples.hasNextExample()){
            Example example = localExamples.nextExample();
            for (HavingTermDomain hld : candidates){
                if (domains.get(hld.id())[localExamples.currentIndex()] == null){
                    Domain domain = hld.termDomain(example);
                    domains.get(hld.id())[localExamples.currentIndex()] = domain;
                }
            }
        }
    }

    private void computeLiteralDomains(Collection<? extends HavingLiteralDomain> candidates, Dataset examples, Map<Integer,Domain[]> domains){
        Dataset localExamples = examples.shallowCopy();
        localExamples.reset();
        while (localExamples.hasNextExample()){
            Example example = localExamples.nextExample();
            for (HavingLiteralDomain hld : candidates){
                if (domains.get(hld.id())[localExamples.currentIndex()] == null){
                    Domain domain = hld.literalDomain(example);
                    domains.get(hld.id())[localExamples.currentIndex()] = domain;
                }
            }
        }
    }
    
    private class TermDomainsComputation implements Runnable {

        private Map<Integer,Domain[]> doms;

        private Collection<? extends HavingTermDomain> cands;

        private Dataset exs;

        public TermDomainsComputation(Collection<? extends HavingTermDomain> candidates, Dataset examples, Map<Integer,Domain[]> output){
            this.cands = candidates;
            this.exs = examples;
            this.doms = output;
        }

        public void run() {
            computeTermDomains(this.cands, this.exs, this.doms);
        }
    }

    private class LiteralDomainsComputation implements Runnable {

        private Map<Integer,Domain[]> doms;

        private Collection<? extends HavingLiteralDomain> cands;

        private Dataset exs;

        public LiteralDomainsComputation(Collection<? extends HavingLiteralDomain> candidates, Dataset examples, Map<Integer,Domain[]> output){
            this.cands = candidates;
            this.exs = examples;
            this.doms = output;
        }

        public void run() {
            computeLiteralDomains(this.cands, this.exs, this.doms);
        }

    }
}
