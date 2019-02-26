// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/Neuralogic.g4 by ANTLR 4.7.2
package parsers.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import parsing.antlr.NeuralogicParser;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link parsing.antlr.NeuralogicParser}.
 */
public interface NeuralogicListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateFile}.
	 * @param ctx the parse tree
	 */
	void enterTemplateFile(parsing.antlr.NeuralogicParser.TemplateFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateFile}.
	 * @param ctx the parse tree
	 */
	void exitTemplateFile(parsing.antlr.NeuralogicParser.TemplateFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateLine}.
	 * @param ctx the parse tree
	 */
	void enterTemplateLine(parsing.antlr.NeuralogicParser.TemplateLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateLine}.
	 * @param ctx the parse tree
	 */
	void exitTemplateLine(parsing.antlr.NeuralogicParser.TemplateLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#examplesFile}.
	 * @param ctx the parse tree
	 */
	void enterExamplesFile(parsing.antlr.NeuralogicParser.ExamplesFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#examplesFile}.
	 * @param ctx the parse tree
	 */
	void exitExamplesFile(parsing.antlr.NeuralogicParser.ExamplesFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#liftedExample}.
	 * @param ctx the parse tree
	 */
	void enterLiftedExample(parsing.antlr.NeuralogicParser.LiftedExampleContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#liftedExample}.
	 * @param ctx the parse tree
	 */
	void exitLiftedExample(parsing.antlr.NeuralogicParser.LiftedExampleContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(parsing.antlr.NeuralogicParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(parsing.antlr.NeuralogicParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#queriesFile}.
	 * @param ctx the parse tree
	 */
	void enterQueriesFile(parsing.antlr.NeuralogicParser.QueriesFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#queriesFile}.
	 * @param ctx the parse tree
	 */
	void exitQueriesFile(parsing.antlr.NeuralogicParser.QueriesFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#fact}.
	 * @param ctx the parse tree
	 */
	void enterFact(parsing.antlr.NeuralogicParser.FactContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#fact}.
	 * @param ctx the parse tree
	 */
	void exitFact(parsing.antlr.NeuralogicParser.FactContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(parsing.antlr.NeuralogicParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(parsing.antlr.NeuralogicParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#termList}.
	 * @param ctx the parse tree
	 */
	void enterTermList(parsing.antlr.NeuralogicParser.TermListContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#termList}.
	 * @param ctx the parse tree
	 */
	void exitTermList(parsing.antlr.NeuralogicParser.TermListContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(parsing.antlr.NeuralogicParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(parsing.antlr.NeuralogicParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(parsing.antlr.NeuralogicParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(parsing.antlr.NeuralogicParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(parsing.antlr.NeuralogicParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(parsing.antlr.NeuralogicParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(parsing.antlr.NeuralogicParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(parsing.antlr.NeuralogicParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(parsing.antlr.NeuralogicParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(parsing.antlr.NeuralogicParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#metadataVal}.
	 * @param ctx the parse tree
	 */
	void enterMetadataVal(parsing.antlr.NeuralogicParser.MetadataValContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#metadataVal}.
	 * @param ctx the parse tree
	 */
	void exitMetadataVal(parsing.antlr.NeuralogicParser.MetadataValContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#metadataList}.
	 * @param ctx the parse tree
	 */
	void enterMetadataList(parsing.antlr.NeuralogicParser.MetadataListContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#metadataList}.
	 * @param ctx the parse tree
	 */
	void exitMetadataList(parsing.antlr.NeuralogicParser.MetadataListContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#lrnnRule}.
	 * @param ctx the parse tree
	 */
	void enterLrnnRule(parsing.antlr.NeuralogicParser.LrnnRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#lrnnRule}.
	 * @param ctx the parse tree
	 */
	void exitLrnnRule(parsing.antlr.NeuralogicParser.LrnnRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicateOffset}.
	 * @param ctx the parse tree
	 */
	void enterPredicateOffset(parsing.antlr.NeuralogicParser.PredicateOffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicateOffset}.
	 * @param ctx the parse tree
	 */
	void exitPredicateOffset(parsing.antlr.NeuralogicParser.PredicateOffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicateMetadata}.
	 * @param ctx the parse tree
	 */
	void enterPredicateMetadata(parsing.antlr.NeuralogicParser.PredicateMetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicateMetadata}.
	 * @param ctx the parse tree
	 */
	void exitPredicateMetadata(parsing.antlr.NeuralogicParser.PredicateMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#weightMetadata}.
	 * @param ctx the parse tree
	 */
	void enterWeightMetadata(parsing.antlr.NeuralogicParser.WeightMetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#weightMetadata}.
	 * @param ctx the parse tree
	 */
	void exitWeightMetadata(parsing.antlr.NeuralogicParser.WeightMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateMetadata}.
	 * @param ctx the parse tree
	 */
	void enterTemplateMetadata(parsing.antlr.NeuralogicParser.TemplateMetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateMetadata}.
	 * @param ctx the parse tree
	 */
	void exitTemplateMetadata(parsing.antlr.NeuralogicParser.TemplateMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(parsing.antlr.NeuralogicParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(parsing.antlr.NeuralogicParser.WeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#fixedValue}.
	 * @param ctx the parse tree
	 */
	void enterFixedValue(parsing.antlr.NeuralogicParser.FixedValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#fixedValue}.
	 * @param ctx the parse tree
	 */
	void exitFixedValue(parsing.antlr.NeuralogicParser.FixedValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(parsing.antlr.NeuralogicParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(parsing.antlr.NeuralogicParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(parsing.antlr.NeuralogicParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(parsing.antlr.NeuralogicParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(parsing.antlr.NeuralogicParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(parsing.antlr.NeuralogicParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#vector}.
	 * @param ctx the parse tree
	 */
	void enterVector(parsing.antlr.NeuralogicParser.VectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#vector}.
	 * @param ctx the parse tree
	 */
	void exitVector(parsing.antlr.NeuralogicParser.VectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#matrix}.
	 * @param ctx the parse tree
	 */
	void enterMatrix(parsing.antlr.NeuralogicParser.MatrixContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#matrix}.
	 * @param ctx the parse tree
	 */
	void exitMatrix(parsing.antlr.NeuralogicParser.MatrixContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#dimensions}.
	 * @param ctx the parse tree
	 */
	void enterDimensions(parsing.antlr.NeuralogicParser.DimensionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#dimensions}.
	 * @param ctx the parse tree
	 */
	void exitDimensions(parsing.antlr.NeuralogicParser.DimensionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link parsing.antlr.NeuralogicParser#negation}.
	 * @param ctx the parse tree
	 */
	void enterNegation(parsing.antlr.NeuralogicParser.NegationContext ctx);
	/**
	 * Exit a parse tree produced by {@link parsing.antlr.NeuralogicParser#negation}.
	 * @param ctx the parse tree
	 */
	void exitNegation(NeuralogicParser.NegationContext ctx);
}