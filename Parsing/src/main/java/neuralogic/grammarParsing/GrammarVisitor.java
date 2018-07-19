package neuralogic.grammarParsing;

import building.LogicBuilder;

public abstract class GrammarVisitor {

    /**
     * LogicBuilder creates basic logic building blocks (variables, predicates, contants) as the visitor walks the parse tree
     */
    LogicBuilder builder;

    public GrammarVisitor(LogicBuilder builder) {
        this.builder = builder;
    }
}
