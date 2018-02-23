// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/Test.g4 by ANTLR 4.7
package parsers.Test;

//    package parsers.Test;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link TestVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class TestBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements TestVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitMain(TestParser.MainContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitName(TestParser.NameContext ctx) { return visitChildren(ctx); }
}