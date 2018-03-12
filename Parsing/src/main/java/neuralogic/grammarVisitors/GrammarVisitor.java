package neuralogic.grammarVisitors;

import parsing.Builder;

public abstract class GrammarVisitor {

    /**
     * Builder creates basic logic building blocks (variables, predicates, contants) as the visitor walks the parse tree
     */
    Builder builder;

    public GrammarVisitor(Builder builder) {
        this.builder = builder;
    }
}
