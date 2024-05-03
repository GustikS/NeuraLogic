// Generated from /src/main/java/cz/cvut/fel/ida/logic/parsing/antlr/grammars/Neuralogic.g4 by ANTLR 4.13.1
package cz.cvut.fel.ida.logic.parsing.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link NeuralogicParser}.
 */
public interface NeuralogicListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#templateFile}.
	 * @param ctx the parse tree
	 */
	void enterTemplateFile(NeuralogicParser.TemplateFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#templateFile}.
	 * @param ctx the parse tree
	 */
	void exitTemplateFile(NeuralogicParser.TemplateFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#templateLine}.
	 * @param ctx the parse tree
	 */
	void enterTemplateLine(NeuralogicParser.TemplateLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#templateLine}.
	 * @param ctx the parse tree
	 */
	void exitTemplateLine(NeuralogicParser.TemplateLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#examplesFile}.
	 * @param ctx the parse tree
	 */
	void enterExamplesFile(NeuralogicParser.ExamplesFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#examplesFile}.
	 * @param ctx the parse tree
	 */
	void exitExamplesFile(NeuralogicParser.ExamplesFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#liftedExample}.
	 * @param ctx the parse tree
	 */
	void enterLiftedExample(NeuralogicParser.LiftedExampleContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#liftedExample}.
	 * @param ctx the parse tree
	 */
	void exitLiftedExample(NeuralogicParser.LiftedExampleContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(NeuralogicParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(NeuralogicParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#queriesFile}.
	 * @param ctx the parse tree
	 */
	void enterQueriesFile(NeuralogicParser.QueriesFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#queriesFile}.
	 * @param ctx the parse tree
	 */
	void exitQueriesFile(NeuralogicParser.QueriesFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#fact}.
	 * @param ctx the parse tree
	 */
	void enterFact(NeuralogicParser.FactContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#fact}.
	 * @param ctx the parse tree
	 */
	void exitFact(NeuralogicParser.FactContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(NeuralogicParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(NeuralogicParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#termList}.
	 * @param ctx the parse tree
	 */
	void enterTermList(NeuralogicParser.TermListContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#termList}.
	 * @param ctx the parse tree
	 */
	void exitTermList(NeuralogicParser.TermListContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(NeuralogicParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(NeuralogicParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(NeuralogicParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(NeuralogicParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(NeuralogicParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(NeuralogicParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(NeuralogicParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(NeuralogicParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(NeuralogicParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(NeuralogicParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#metadataVal}.
	 * @param ctx the parse tree
	 */
	void enterMetadataVal(NeuralogicParser.MetadataValContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#metadataVal}.
	 * @param ctx the parse tree
	 */
	void exitMetadataVal(NeuralogicParser.MetadataValContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#metadataList}.
	 * @param ctx the parse tree
	 */
	void enterMetadataList(NeuralogicParser.MetadataListContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#metadataList}.
	 * @param ctx the parse tree
	 */
	void exitMetadataList(NeuralogicParser.MetadataListContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#lrnnRule}.
	 * @param ctx the parse tree
	 */
	void enterLrnnRule(NeuralogicParser.LrnnRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#lrnnRule}.
	 * @param ctx the parse tree
	 */
	void exitLrnnRule(NeuralogicParser.LrnnRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#predicateOffset}.
	 * @param ctx the parse tree
	 */
	void enterPredicateOffset(NeuralogicParser.PredicateOffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#predicateOffset}.
	 * @param ctx the parse tree
	 */
	void exitPredicateOffset(NeuralogicParser.PredicateOffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#predicateMetadata}.
	 * @param ctx the parse tree
	 */
	void enterPredicateMetadata(NeuralogicParser.PredicateMetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#predicateMetadata}.
	 * @param ctx the parse tree
	 */
	void exitPredicateMetadata(NeuralogicParser.PredicateMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#weightMetadata}.
	 * @param ctx the parse tree
	 */
	void enterWeightMetadata(NeuralogicParser.WeightMetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#weightMetadata}.
	 * @param ctx the parse tree
	 */
	void exitWeightMetadata(NeuralogicParser.WeightMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#templateMetadata}.
	 * @param ctx the parse tree
	 */
	void enterTemplateMetadata(NeuralogicParser.TemplateMetadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#templateMetadata}.
	 * @param ctx the parse tree
	 */
	void exitTemplateMetadata(NeuralogicParser.TemplateMetadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(NeuralogicParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(NeuralogicParser.WeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#fixedValue}.
	 * @param ctx the parse tree
	 */
	void enterFixedValue(NeuralogicParser.FixedValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#fixedValue}.
	 * @param ctx the parse tree
	 */
	void exitFixedValue(NeuralogicParser.FixedValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(NeuralogicParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(NeuralogicParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(NeuralogicParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(NeuralogicParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(NeuralogicParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(NeuralogicParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#vector}.
	 * @param ctx the parse tree
	 */
	void enterVector(NeuralogicParser.VectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#vector}.
	 * @param ctx the parse tree
	 */
	void exitVector(NeuralogicParser.VectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#sparseVector}.
	 * @param ctx the parse tree
	 */
	void enterSparseVector(NeuralogicParser.SparseVectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#sparseVector}.
	 * @param ctx the parse tree
	 */
	void exitSparseVector(NeuralogicParser.SparseVectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#matrix}.
	 * @param ctx the parse tree
	 */
	void enterMatrix(NeuralogicParser.MatrixContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#matrix}.
	 * @param ctx the parse tree
	 */
	void exitMatrix(NeuralogicParser.MatrixContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#sparseMatrix}.
	 * @param ctx the parse tree
	 */
	void enterSparseMatrix(NeuralogicParser.SparseMatrixContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#sparseMatrix}.
	 * @param ctx the parse tree
	 */
	void exitSparseMatrix(NeuralogicParser.SparseMatrixContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#dimensions}.
	 * @param ctx the parse tree
	 */
	void enterDimensions(NeuralogicParser.DimensionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#dimensions}.
	 * @param ctx the parse tree
	 */
	void exitDimensions(NeuralogicParser.DimensionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(NeuralogicParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(NeuralogicParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#element2d}.
	 * @param ctx the parse tree
	 */
	void enterElement2d(NeuralogicParser.Element2dContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#element2d}.
	 * @param ctx the parse tree
	 */
	void exitElement2d(NeuralogicParser.Element2dContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#negation}.
	 * @param ctx the parse tree
	 */
	void enterNegation(NeuralogicParser.NegationContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#negation}.
	 * @param ctx the parse tree
	 */
	void exitNegation(NeuralogicParser.NegationContext ctx);
	/**
	 * Enter a parse tree produced by {@link NeuralogicParser#impliedBy}.
	 * @param ctx the parse tree
	 */
	void enterImpliedBy(NeuralogicParser.ImpliedByContext ctx);
	/**
	 * Exit a parse tree produced by {@link NeuralogicParser#impliedBy}.
	 * @param ctx the parse tree
	 */
	void exitImpliedBy(NeuralogicParser.ImpliedByContext ctx);
}