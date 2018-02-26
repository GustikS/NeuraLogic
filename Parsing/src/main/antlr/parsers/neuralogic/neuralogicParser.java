// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/neuralogic.g4 by ANTLR 4.7
package parsers.neuralogic;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class neuralogicParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, VARIABLE=4, INT=5, FLOAT=6, ATOMIC_NAME=7, IMPLIED_BY=8, 
		LANGLE=9, RANGLE=10, LBRACKET=11, RBRACKET=12, LPAREN=13, RPAREN=14, COMMA=15, 
		SLASH=16, CARET=17, TRUE=18, ALPHANUMERIC=19, ALPHA=20, WS=21, COMMENT=22, 
		MULTILINE_COMMENT=23;
	public static final int
		RULE_template_file = 0, RULE_template_line = 1, RULE_examples_file = 2, 
		RULE_fact = 3, RULE_atom = 4, RULE_term_list = 5, RULE_term = 6, RULE_variable = 7, 
		RULE_constant = 8, RULE_predicate = 9, RULE_special_predicate = 10, RULE_conjunction = 11, 
		RULE_metadata_val = 12, RULE_metadata_list = 13, RULE_lrnn_rule = 14, 
		RULE_predicate_offset = 15, RULE_predicate_metadata = 16, RULE_weight = 17, 
		RULE_fixed_weight = 18, RULE_offset = 19;
	public static final String[] ruleNames = {
		"template_file", "template_line", "examples_file", "fact", "atom", "term_list", 
		"term", "variable", "constant", "predicate", "special_predicate", "conjunction", 
		"metadata_val", "metadata_list", "lrnn_rule", "predicate_offset", "predicate_metadata", 
		"weight", "fixed_weight", "offset"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'.'", "'@'", "'='", null, null, null, null, "':-'", "'<'", "'>'", 
		"'['", "']'", "'('", "')'", "','", "'/'", "'^'", "'true'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
		"LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", 
		"SLASH", "CARET", "TRUE", "ALPHANUMERIC", "ALPHA", "WS", "COMMENT", "MULTILINE_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "neuralogic.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public neuralogicParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Template_fileContext extends ParserRuleContext {
		public List<Template_lineContext> template_line() {
			return getRuleContexts(Template_lineContext.class);
		}
		public Template_lineContext template_line(int i) {
			return getRuleContext(Template_lineContext.class,i);
		}
		public Template_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_template_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterTemplate_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitTemplate_file(this);
		}
	}

	public final Template_fileContext template_file() throws RecognitionException {
		Template_fileContext _localctx = new Template_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_template_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE))) != 0)) {
				{
				{
				setState(40);
				template_line();
				}
				}
				setState(45);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Template_lineContext extends ParserRuleContext {
		public Lrnn_ruleContext lrnn_rule() {
			return getRuleContext(Lrnn_ruleContext.class,0);
		}
		public FactContext fact() {
			return getRuleContext(FactContext.class,0);
		}
		public Predicate_metadataContext predicate_metadata() {
			return getRuleContext(Predicate_metadataContext.class,0);
		}
		public Predicate_offsetContext predicate_offset() {
			return getRuleContext(Predicate_offsetContext.class,0);
		}
		public Template_lineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_template_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterTemplate_line(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitTemplate_line(this);
		}
	}

	public final Template_lineContext template_line() throws RecognitionException {
		Template_lineContext _localctx = new Template_lineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_template_line);
		try {
			setState(50);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(46);
				lrnn_rule();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(47);
				fact();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(48);
				predicate_metadata();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(49);
				predicate_offset();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Examples_fileContext extends ParserRuleContext {
		public List<Lrnn_ruleContext> lrnn_rule() {
			return getRuleContexts(Lrnn_ruleContext.class);
		}
		public Lrnn_ruleContext lrnn_rule(int i) {
			return getRuleContext(Lrnn_ruleContext.class,i);
		}
		public List<FactContext> fact() {
			return getRuleContexts(FactContext.class);
		}
		public FactContext fact(int i) {
			return getRuleContext(FactContext.class,i);
		}
		public List<ConjunctionContext> conjunction() {
			return getRuleContexts(ConjunctionContext.class);
		}
		public ConjunctionContext conjunction(int i) {
			return getRuleContext(ConjunctionContext.class,i);
		}
		public Examples_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_examples_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterExamples_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitExamples_file(this);
		}
	}

	public final Examples_fileContext examples_file() throws RecognitionException {
		Examples_fileContext _localctx = new Examples_fileContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_examples_file);
		int _la;
		try {
			setState(67);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(53); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(52);
					lrnn_rule();
					}
					}
					setState(55); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(58); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(57);
					fact();
					}
					}
					setState(60); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE))) != 0) );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(63); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(62);
					conjunction();
					}
					}
					setState(65); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE))) != 0) );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FactContext extends ParserRuleContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public FactContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fact; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterFact(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitFact(this);
		}
	}

	public final FactContext fact() throws RecognitionException {
		FactContext _localctx = new FactContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_fact);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			atom();
			setState(70);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
		public Term_listContext term_list() {
			return getRuleContext(Term_listContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE))) != 0)) {
				{
				setState(72);
				weight();
				}
			}

			setState(75);
			predicate();
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(76);
				term_list();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Term_listContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(neuralogicParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(neuralogicParser.RPAREN, 0); }
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(neuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(neuralogicParser.COMMA, i);
		}
		public Term_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterTerm_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitTerm_list(this);
		}
	}

	public final Term_listContext term_list() throws RecognitionException {
		Term_listContext _localctx = new Term_listContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_term_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(LPAREN);
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARIABLE) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME))) != 0)) {
				{
				setState(80);
				term();
				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(81);
					match(COMMA);
					setState(82);
					term();
					}
					}
					setState(87);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(90);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_term);
		try {
			setState(94);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case ATOMIC_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(92);
				constant();
				}
				break;
			case VARIABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(93);
				variable();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode VARIABLE() { return getToken(neuralogicParser.VARIABLE, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitVariable(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(VARIABLE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantContext extends ParserRuleContext {
		public TerminalNode ATOMIC_NAME() { return getToken(neuralogicParser.ATOMIC_NAME, 0); }
		public TerminalNode INT() { return getToken(neuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(neuralogicParser.FLOAT, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitConstant(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_constant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public TerminalNode ATOMIC_NAME() { return getToken(neuralogicParser.ATOMIC_NAME, 0); }
		public Special_predicateContext special_predicate() {
			return getRuleContext(Special_predicateContext.class,0);
		}
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitPredicate(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_predicate);
		try {
			setState(102);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ATOMIC_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(100);
				match(ATOMIC_NAME);
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(101);
				special_predicate();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Special_predicateContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public Special_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_special_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterSpecial_predicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitSpecial_predicate(this);
		}
	}

	public final Special_predicateContext special_predicate() throws RecognitionException {
		Special_predicateContext _localctx = new Special_predicateContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_special_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(T__1);
			setState(105);
			predicate();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConjunctionContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(neuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(neuralogicParser.COMMA, i);
		}
		public ConjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterConjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitConjunction(this);
		}
	}

	public final ConjunctionContext conjunction() throws RecognitionException {
		ConjunctionContext _localctx = new ConjunctionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_conjunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			atom();
			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(108);
				match(COMMA);
				setState(109);
				atom();
				}
				}
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Metadata_valContext extends ParserRuleContext {
		public TerminalNode ATOMIC_NAME() { return getToken(neuralogicParser.ATOMIC_NAME, 0); }
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public Metadata_valContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadata_val; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterMetadata_val(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitMetadata_val(this);
		}
	}

	public final Metadata_valContext metadata_val() throws RecognitionException {
		Metadata_valContext _localctx = new Metadata_valContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_metadata_val);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(ATOMIC_NAME);
			setState(116);
			match(T__2);
			setState(117);
			constant();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Metadata_listContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(neuralogicParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(neuralogicParser.RBRACKET, 0); }
		public List<Metadata_valContext> metadata_val() {
			return getRuleContexts(Metadata_valContext.class);
		}
		public Metadata_valContext metadata_val(int i) {
			return getRuleContext(Metadata_valContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(neuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(neuralogicParser.COMMA, i);
		}
		public Metadata_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadata_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterMetadata_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitMetadata_list(this);
		}
	}

	public final Metadata_listContext metadata_list() throws RecognitionException {
		Metadata_listContext _localctx = new Metadata_listContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_metadata_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(LBRACKET);
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ATOMIC_NAME) {
				{
				setState(120);
				metadata_val();
				setState(125);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(121);
					match(COMMA);
					setState(122);
					metadata_val();
					}
					}
					setState(127);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(130);
			match(RBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Lrnn_ruleContext extends ParserRuleContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode IMPLIED_BY() { return getToken(neuralogicParser.IMPLIED_BY, 0); }
		public ConjunctionContext conjunction() {
			return getRuleContext(ConjunctionContext.class,0);
		}
		public OffsetContext offset() {
			return getRuleContext(OffsetContext.class,0);
		}
		public Metadata_listContext metadata_list() {
			return getRuleContext(Metadata_listContext.class,0);
		}
		public Lrnn_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lrnn_rule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterLrnn_rule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitLrnn_rule(this);
		}
	}

	public final Lrnn_ruleContext lrnn_rule() throws RecognitionException {
		Lrnn_ruleContext _localctx = new Lrnn_ruleContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_lrnn_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			atom();
			setState(133);
			match(IMPLIED_BY);
			setState(134);
			conjunction();
			setState(136);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE))) != 0)) {
				{
				setState(135);
				offset();
				}
			}

			setState(138);
			match(T__0);
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACKET) {
				{
				setState(139);
				metadata_list();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Predicate_offsetContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode SLASH() { return getToken(neuralogicParser.SLASH, 0); }
		public TerminalNode INT() { return getToken(neuralogicParser.INT, 0); }
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
		public Predicate_offsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate_offset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterPredicate_offset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitPredicate_offset(this);
		}
	}

	public final Predicate_offsetContext predicate_offset() throws RecognitionException {
		Predicate_offsetContext _localctx = new Predicate_offsetContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_predicate_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			predicate();
			setState(143);
			match(SLASH);
			setState(144);
			match(INT);
			setState(145);
			weight();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Predicate_metadataContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode SLASH() { return getToken(neuralogicParser.SLASH, 0); }
		public TerminalNode INT() { return getToken(neuralogicParser.INT, 0); }
		public Metadata_listContext metadata_list() {
			return getRuleContext(Metadata_listContext.class,0);
		}
		public Predicate_metadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate_metadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterPredicate_metadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitPredicate_metadata(this);
		}
	}

	public final Predicate_metadataContext predicate_metadata() throws RecognitionException {
		Predicate_metadataContext _localctx = new Predicate_metadataContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_predicate_metadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			predicate();
			setState(148);
			match(SLASH);
			setState(149);
			match(INT);
			setState(150);
			metadata_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WeightContext extends ParserRuleContext {
		public Fixed_weightContext fixed_weight() {
			return getRuleContext(Fixed_weightContext.class,0);
		}
		public TerminalNode INT() { return getToken(neuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(neuralogicParser.FLOAT, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitWeight(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_weight);
		int _la;
		try {
			setState(154);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANGLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(152);
				fixed_weight();
				}
				break;
			case INT:
			case FLOAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(153);
				_la = _input.LA(1);
				if ( !(_la==INT || _la==FLOAT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fixed_weightContext extends ParserRuleContext {
		public TerminalNode LANGLE() { return getToken(neuralogicParser.LANGLE, 0); }
		public TerminalNode RANGLE() { return getToken(neuralogicParser.RANGLE, 0); }
		public TerminalNode INT() { return getToken(neuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(neuralogicParser.FLOAT, 0); }
		public Fixed_weightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fixed_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterFixed_weight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitFixed_weight(this);
		}
	}

	public final Fixed_weightContext fixed_weight() throws RecognitionException {
		Fixed_weightContext _localctx = new Fixed_weightContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_fixed_weight);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(LANGLE);
			setState(157);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==FLOAT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(158);
			match(RANGLE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OffsetContext extends ParserRuleContext {
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
		public OffsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_offset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitOffset(this);
		}
	}

	public final OffsetContext offset() throws RecognitionException {
		OffsetContext _localctx = new OffsetContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			weight();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\31\u00a5\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\3\2\7\2,\n\2\f\2\16\2/\13\2\3\3\3\3\3\3"+
		"\3\3\5\3\65\n\3\3\4\6\48\n\4\r\4\16\49\3\4\6\4=\n\4\r\4\16\4>\3\4\6\4"+
		"B\n\4\r\4\16\4C\5\4F\n\4\3\5\3\5\3\5\3\6\5\6L\n\6\3\6\3\6\5\6P\n\6\3\7"+
		"\3\7\3\7\3\7\7\7V\n\7\f\7\16\7Y\13\7\5\7[\n\7\3\7\3\7\3\b\3\b\5\ba\n\b"+
		"\3\t\3\t\3\n\3\n\3\13\3\13\5\13i\n\13\3\f\3\f\3\f\3\r\3\r\3\r\7\rq\n\r"+
		"\f\r\16\rt\13\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\7\17~\n\17\f\17"+
		"\16\17\u0081\13\17\5\17\u0083\n\17\3\17\3\17\3\20\3\20\3\20\3\20\5\20"+
		"\u008b\n\20\3\20\3\20\5\20\u008f\n\20\3\21\3\21\3\21\3\21\3\21\3\22\3"+
		"\22\3\22\3\22\3\22\3\23\3\23\5\23\u009d\n\23\3\24\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\2\2\26\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(\2\4\3\2"+
		"\7\t\3\2\7\b\2\u00a5\2-\3\2\2\2\4\64\3\2\2\2\6E\3\2\2\2\bG\3\2\2\2\nK"+
		"\3\2\2\2\fQ\3\2\2\2\16`\3\2\2\2\20b\3\2\2\2\22d\3\2\2\2\24h\3\2\2\2\26"+
		"j\3\2\2\2\30m\3\2\2\2\32u\3\2\2\2\34y\3\2\2\2\36\u0086\3\2\2\2 \u0090"+
		"\3\2\2\2\"\u0095\3\2\2\2$\u009c\3\2\2\2&\u009e\3\2\2\2(\u00a2\3\2\2\2"+
		"*,\5\4\3\2+*\3\2\2\2,/\3\2\2\2-+\3\2\2\2-.\3\2\2\2.\3\3\2\2\2/-\3\2\2"+
		"\2\60\65\5\36\20\2\61\65\5\b\5\2\62\65\5\"\22\2\63\65\5 \21\2\64\60\3"+
		"\2\2\2\64\61\3\2\2\2\64\62\3\2\2\2\64\63\3\2\2\2\65\5\3\2\2\2\668\5\36"+
		"\20\2\67\66\3\2\2\289\3\2\2\29\67\3\2\2\29:\3\2\2\2:F\3\2\2\2;=\5\b\5"+
		"\2<;\3\2\2\2=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?F\3\2\2\2@B\5\30\r\2A@\3\2"+
		"\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2DF\3\2\2\2E\67\3\2\2\2E<\3\2\2\2EA\3"+
		"\2\2\2F\7\3\2\2\2GH\5\n\6\2HI\7\3\2\2I\t\3\2\2\2JL\5$\23\2KJ\3\2\2\2K"+
		"L\3\2\2\2LM\3\2\2\2MO\5\24\13\2NP\5\f\7\2ON\3\2\2\2OP\3\2\2\2P\13\3\2"+
		"\2\2QZ\7\17\2\2RW\5\16\b\2ST\7\21\2\2TV\5\16\b\2US\3\2\2\2VY\3\2\2\2W"+
		"U\3\2\2\2WX\3\2\2\2X[\3\2\2\2YW\3\2\2\2ZR\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2"+
		"\\]\7\20\2\2]\r\3\2\2\2^a\5\22\n\2_a\5\20\t\2`^\3\2\2\2`_\3\2\2\2a\17"+
		"\3\2\2\2bc\7\6\2\2c\21\3\2\2\2de\t\2\2\2e\23\3\2\2\2fi\7\t\2\2gi\5\26"+
		"\f\2hf\3\2\2\2hg\3\2\2\2i\25\3\2\2\2jk\7\4\2\2kl\5\24\13\2l\27\3\2\2\2"+
		"mr\5\n\6\2no\7\21\2\2oq\5\n\6\2pn\3\2\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2"+
		"\2s\31\3\2\2\2tr\3\2\2\2uv\7\t\2\2vw\7\5\2\2wx\5\22\n\2x\33\3\2\2\2y\u0082"+
		"\7\r\2\2z\177\5\32\16\2{|\7\21\2\2|~\5\32\16\2}{\3\2\2\2~\u0081\3\2\2"+
		"\2\177}\3\2\2\2\177\u0080\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2"+
		"\2\u0082z\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085"+
		"\7\16\2\2\u0085\35\3\2\2\2\u0086\u0087\5\n\6\2\u0087\u0088\7\n\2\2\u0088"+
		"\u008a\5\30\r\2\u0089\u008b\5(\25\2\u008a\u0089\3\2\2\2\u008a\u008b\3"+
		"\2\2\2\u008b\u008c\3\2\2\2\u008c\u008e\7\3\2\2\u008d\u008f\5\34\17\2\u008e"+
		"\u008d\3\2\2\2\u008e\u008f\3\2\2\2\u008f\37\3\2\2\2\u0090\u0091\5\24\13"+
		"\2\u0091\u0092\7\22\2\2\u0092\u0093\7\7\2\2\u0093\u0094\5$\23\2\u0094"+
		"!\3\2\2\2\u0095\u0096\5\24\13\2\u0096\u0097\7\22\2\2\u0097\u0098\7\7\2"+
		"\2\u0098\u0099\5\34\17\2\u0099#\3\2\2\2\u009a\u009d\5&\24\2\u009b\u009d"+
		"\t\3\2\2\u009c\u009a\3\2\2\2\u009c\u009b\3\2\2\2\u009d%\3\2\2\2\u009e"+
		"\u009f\7\13\2\2\u009f\u00a0\t\3\2\2\u00a0\u00a1\7\f\2\2\u00a1\'\3\2\2"+
		"\2\u00a2\u00a3\5$\23\2\u00a3)\3\2\2\2\24-\649>CEKOWZ`hr\177\u0082\u008a"+
		"\u008e\u009c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}