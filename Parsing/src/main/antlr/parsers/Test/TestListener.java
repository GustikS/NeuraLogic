// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/Test.g4 by ANTLR 4.7
package parsers.Test;

//    package parsers.Test;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TestParser}.
 */
public interface TestListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TestParser#main}.
	 * @param ctx the parse tree
	 */
	void enterMain(TestParser.MainContext ctx);
	/**
	 * Exit a parse tree produced by {@link TestParser#main}.
	 * @param ctx the parse tree
	 */
	void exitMain(TestParser.MainContext ctx);
	/**
	 * Enter a parse tree produced by {@link TestParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(TestParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TestParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(TestParser.NameContext ctx);
}