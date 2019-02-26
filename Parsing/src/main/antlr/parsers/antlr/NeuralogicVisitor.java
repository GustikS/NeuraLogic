// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/Neuralogic.g4 by ANTLR 4.7.2
package parsers.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import parsing.antlr.NeuralogicParser;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link parsing.antlr.NeuralogicParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface NeuralogicVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateFile(parsing.antlr.NeuralogicParser.TemplateFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateLine(parsing.antlr.NeuralogicParser.TemplateLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#examplesFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExamplesFile(parsing.antlr.NeuralogicParser.ExamplesFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#liftedExample}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiftedExample(parsing.antlr.NeuralogicParser.LiftedExampleContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(parsing.antlr.NeuralogicParser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#queriesFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueriesFile(parsing.antlr.NeuralogicParser.QueriesFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#fact}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFact(parsing.antlr.NeuralogicParser.FactContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(parsing.antlr.NeuralogicParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#termList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermList(parsing.antlr.NeuralogicParser.TermListContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(parsing.antlr.NeuralogicParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(parsing.antlr.NeuralogicParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(parsing.antlr.NeuralogicParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicate(parsing.antlr.NeuralogicParser.PredicateContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConjunction(parsing.antlr.NeuralogicParser.ConjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#metadataVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadataVal(parsing.antlr.NeuralogicParser.MetadataValContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#metadataList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadataList(parsing.antlr.NeuralogicParser.MetadataListContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#lrnnRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLrnnRule(parsing.antlr.NeuralogicParser.LrnnRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicateOffset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicateOffset(parsing.antlr.NeuralogicParser.PredicateOffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#predicateMetadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicateMetadata(parsing.antlr.NeuralogicParser.PredicateMetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#weightMetadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeightMetadata(parsing.antlr.NeuralogicParser.WeightMetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#templateMetadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateMetadata(parsing.antlr.NeuralogicParser.TemplateMetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeight(parsing.antlr.NeuralogicParser.WeightContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#fixedValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFixedValue(parsing.antlr.NeuralogicParser.FixedValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(parsing.antlr.NeuralogicParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(parsing.antlr.NeuralogicParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(parsing.antlr.NeuralogicParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#vector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVector(parsing.antlr.NeuralogicParser.VectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#matrix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatrix(parsing.antlr.NeuralogicParser.MatrixContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#dimensions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDimensions(parsing.antlr.NeuralogicParser.DimensionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link parsing.antlr.NeuralogicParser#negation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegation(NeuralogicParser.NegationContext ctx);
}