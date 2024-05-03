// Generated from /src/main/java/cz/cvut/fel/ida/logic/parsing/antlr/grammars/Neuralogic.g4 by ANTLR 4.13.1
package cz.cvut.fel.ida.logic.parsing.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link NeuralogicParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface NeuralogicVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#templateFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateFile(NeuralogicParser.TemplateFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#templateLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateLine(NeuralogicParser.TemplateLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#examplesFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExamplesFile(NeuralogicParser.ExamplesFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#liftedExample}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiftedExample(NeuralogicParser.LiftedExampleContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(NeuralogicParser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#queriesFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQueriesFile(NeuralogicParser.QueriesFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#fact}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFact(NeuralogicParser.FactContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(NeuralogicParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#termList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermList(NeuralogicParser.TermListContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(NeuralogicParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(NeuralogicParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(NeuralogicParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicate(NeuralogicParser.PredicateContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConjunction(NeuralogicParser.ConjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#metadataVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadataVal(NeuralogicParser.MetadataValContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#metadataList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetadataList(NeuralogicParser.MetadataListContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#lrnnRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLrnnRule(NeuralogicParser.LrnnRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#predicateOffset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicateOffset(NeuralogicParser.PredicateOffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#predicateMetadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicateMetadata(NeuralogicParser.PredicateMetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#weightMetadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeightMetadata(NeuralogicParser.WeightMetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#templateMetadata}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplateMetadata(NeuralogicParser.TemplateMetadataContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeight(NeuralogicParser.WeightContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#fixedValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFixedValue(NeuralogicParser.FixedValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(NeuralogicParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(NeuralogicParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(NeuralogicParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#vector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVector(NeuralogicParser.VectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#sparseVector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSparseVector(NeuralogicParser.SparseVectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#matrix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatrix(NeuralogicParser.MatrixContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#sparseMatrix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSparseMatrix(NeuralogicParser.SparseMatrixContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#dimensions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDimensions(NeuralogicParser.DimensionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(NeuralogicParser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#element2d}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement2d(NeuralogicParser.Element2dContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#negation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegation(NeuralogicParser.NegationContext ctx);
	/**
	 * Visit a parse tree produced by {@link NeuralogicParser#impliedBy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImpliedBy(NeuralogicParser.ImpliedByContext ctx);
}