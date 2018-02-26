// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/neuralogic.g4 by ANTLR 4.7
package parsers.neuralogic;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link neuralogicParser}.
 */
public interface neuralogicListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#template_file}.
	 * @param ctx the parse tree
	 */
	void enterTemplate_file(neuralogicParser.Template_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#template_file}.
	 * @param ctx the parse tree
	 */
	void exitTemplate_file(neuralogicParser.Template_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#template_line}.
	 * @param ctx the parse tree
	 */
	void enterTemplate_line(neuralogicParser.Template_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#template_line}.
	 * @param ctx the parse tree
	 */
	void exitTemplate_line(neuralogicParser.Template_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#examples_file}.
	 * @param ctx the parse tree
	 */
	void enterExamples_file(neuralogicParser.Examples_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#examples_file}.
	 * @param ctx the parse tree
	 */
	void exitExamples_file(neuralogicParser.Examples_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#fact}.
	 * @param ctx the parse tree
	 */
	void enterFact(neuralogicParser.FactContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#fact}.
	 * @param ctx the parse tree
	 */
	void exitFact(neuralogicParser.FactContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(neuralogicParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(neuralogicParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#term_list}.
	 * @param ctx the parse tree
	 */
	void enterTerm_list(neuralogicParser.Term_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#term_list}.
	 * @param ctx the parse tree
	 */
	void exitTerm_list(neuralogicParser.Term_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(neuralogicParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(neuralogicParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(neuralogicParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(neuralogicParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(neuralogicParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(neuralogicParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(neuralogicParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(neuralogicParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#special_predicate}.
	 * @param ctx the parse tree
	 */
	void enterSpecial_predicate(neuralogicParser.Special_predicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#special_predicate}.
	 * @param ctx the parse tree
	 */
	void exitSpecial_predicate(neuralogicParser.Special_predicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(neuralogicParser.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#conjunction}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(neuralogicParser.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#metadata_val}.
	 * @param ctx the parse tree
	 */
	void enterMetadata_val(neuralogicParser.Metadata_valContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#metadata_val}.
	 * @param ctx the parse tree
	 */
	void exitMetadata_val(neuralogicParser.Metadata_valContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#metadata_list}.
	 * @param ctx the parse tree
	 */
	void enterMetadata_list(neuralogicParser.Metadata_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#metadata_list}.
	 * @param ctx the parse tree
	 */
	void exitMetadata_list(neuralogicParser.Metadata_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#lrnn_rule}.
	 * @param ctx the parse tree
	 */
	void enterLrnn_rule(neuralogicParser.Lrnn_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#lrnn_rule}.
	 * @param ctx the parse tree
	 */
	void exitLrnn_rule(neuralogicParser.Lrnn_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#predicate_offset}.
	 * @param ctx the parse tree
	 */
	void enterPredicate_offset(neuralogicParser.Predicate_offsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#predicate_offset}.
	 * @param ctx the parse tree
	 */
	void exitPredicate_offset(neuralogicParser.Predicate_offsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#predicate_metadata}.
	 * @param ctx the parse tree
	 */
	void enterPredicate_metadata(neuralogicParser.Predicate_metadataContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#predicate_metadata}.
	 * @param ctx the parse tree
	 */
	void exitPredicate_metadata(neuralogicParser.Predicate_metadataContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(neuralogicParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(neuralogicParser.WeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#fixed_weight}.
	 * @param ctx the parse tree
	 */
	void enterFixed_weight(neuralogicParser.Fixed_weightContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#fixed_weight}.
	 * @param ctx the parse tree
	 */
	void exitFixed_weight(neuralogicParser.Fixed_weightContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(neuralogicParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(neuralogicParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(neuralogicParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(neuralogicParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link neuralogicParser#vector}.
	 * @param ctx the parse tree
	 */
	void enterVector(neuralogicParser.VectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link neuralogicParser#vector}.
	 * @param ctx the parse tree
	 */
	void exitVector(neuralogicParser.VectorContext ctx);
}