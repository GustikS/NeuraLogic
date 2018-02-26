// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/neuralogic.g4 by ANTLR 4.7
package parsers.neuralogic;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class neuralogicParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, VARIABLE=4, INT=5, FLOAT=6, ATOMIC_NAME=7, IMPLIED_BY=8, 
		LANGLE=9, RANGLE=10, LBRACKET=11, RBRACKET=12, LPAREN=13, RPAREN=14, COMMA=15, 
		SLASH=16, CARET=17, TRUE=18, DOLLAR=19, WS=20, COMMENT=21, MULTILINE_COMMENT=22;
	public static final int
		RULE_template_file = 0, RULE_template_line = 1, RULE_examples_file = 2, 
		RULE_fact = 3, RULE_atom = 4, RULE_term_list = 5, RULE_term = 6, RULE_variable = 7, 
		RULE_constant = 8, RULE_predicate = 9, RULE_special_predicate = 10, RULE_conjunction = 11, 
		RULE_metadata_val = 12, RULE_metadata_list = 13, RULE_lrnn_rule = 14, 
		RULE_predicate_offset = 15, RULE_predicate_metadata = 16, RULE_weight = 17, 
		RULE_fixed_weight = 18, RULE_offset = 19, RULE_number = 20, RULE_vector = 21;
	public static final String[] ruleNames = {
		"template_file", "template_line", "examples_file", "fact", "atom", "term_list", 
		"term", "variable", "constant", "predicate", "special_predicate", "conjunction", 
		"metadata_val", "metadata_list", "lrnn_rule", "predicate_offset", "predicate_metadata", 
		"weight", "fixed_weight", "offset", "number", "vector"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'.'", "'@'", "'='", null, null, null, null, "':-'", "'<'", "'>'", 
		"'['", "']'", "'('", "')'", "','", "'/'", "'^'", "'true'", "'$'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
		"LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", 
		"SLASH", "CARET", "TRUE", "DOLLAR", "WS", "COMMENT", "MULTILINE_COMMENT"
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
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR))) != 0)) {
				{
				{
				setState(44);
				template_line();
				}
				}
				setState(49);
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
			setState(54);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(50);
				lrnn_rule();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(51);
				fact();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(52);
				predicate_metadata();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(53);
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
			setState(71);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(57); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(56);
					lrnn_rule();
					}
					}
					setState(59); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(61);
					fact();
					}
					}
					setState(64); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR))) != 0) );
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(67); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(66);
					conjunction();
					}
					}
					setState(69); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR))) != 0) );
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
			setState(73);
			atom();
			setState(74);
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
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR))) != 0)) {
				{
				setState(76);
				weight();
				}
			}

			setState(79);
			predicate();
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(80);
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
			setState(83);
			match(LPAREN);
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARIABLE) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME))) != 0)) {
				{
				setState(84);
				term();
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(85);
					match(COMMA);
					setState(86);
					term();
					}
					}
					setState(91);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(94);
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
			setState(98);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case ATOMIC_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				constant();
				}
				break;
			case VARIABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(97);
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
			setState(100);
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
			setState(102);
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
			setState(106);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ATOMIC_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(104);
				match(ATOMIC_NAME);
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(105);
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
			setState(108);
			match(T__1);
			setState(109);
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
			setState(111);
			atom();
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(112);
				match(COMMA);
				setState(113);
				atom();
				}
				}
				setState(118);
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
			setState(119);
			match(ATOMIC_NAME);
			setState(120);
			match(T__2);
			setState(121);
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
			setState(123);
			match(LBRACKET);
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ATOMIC_NAME) {
				{
				setState(124);
				metadata_val();
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(125);
					match(COMMA);
					setState(126);
					metadata_val();
					}
					}
					setState(131);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(134);
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
			setState(136);
			atom();
			setState(137);
			match(IMPLIED_BY);
			setState(138);
			conjunction();
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR))) != 0)) {
				{
				setState(139);
				offset();
				}
			}

			setState(142);
			match(T__0);
			setState(144);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(143);
				metadata_list();
				}
				break;
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
			setState(146);
			predicate();
			setState(147);
			match(SLASH);
			setState(148);
			match(INT);
			setState(149);
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
			setState(151);
			predicate();
			setState(152);
			match(SLASH);
			setState(153);
			match(INT);
			setState(154);
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
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode LBRACKET() { return getToken(neuralogicParser.LBRACKET, 0); }
		public VectorContext vector() {
			return getRuleContext(VectorContext.class,0);
		}
		public TerminalNode RBRACKET() { return getToken(neuralogicParser.RBRACKET, 0); }
		public TerminalNode DOLLAR() { return getToken(neuralogicParser.DOLLAR, 0); }
		public TerminalNode ATOMIC_NAME() { return getToken(neuralogicParser.ATOMIC_NAME, 0); }
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
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(156);
				match(DOLLAR);
				setState(157);
				match(ATOMIC_NAME);
				setState(158);
				match(T__2);
				}
			}

			setState(167);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANGLE:
				{
				setState(161);
				fixed_weight();
				}
				break;
			case INT:
			case FLOAT:
				{
				setState(162);
				number();
				}
				break;
			case LBRACKET:
				{
				setState(163);
				match(LBRACKET);
				setState(164);
				vector();
				setState(165);
				match(RBRACKET);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class Fixed_weightContext extends ParserRuleContext {
		public TerminalNode LANGLE() { return getToken(neuralogicParser.LANGLE, 0); }
		public TerminalNode RANGLE() { return getToken(neuralogicParser.RANGLE, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public VectorContext vector() {
			return getRuleContext(VectorContext.class,0);
		}
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(LANGLE);
			setState(172);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(170);
				number();
				}
				break;
			case 2:
				{
				setState(171);
				vector();
				}
				break;
			}
			setState(174);
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
			setState(176);
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

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(neuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(neuralogicParser.FLOAT, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
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

	public static class VectorContext extends ParserRuleContext {
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(neuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(neuralogicParser.COMMA, i);
		}
		public VectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).enterVector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof neuralogicListener ) ((neuralogicListener)listener).exitVector(this);
		}
	}

	public final VectorContext vector() throws RecognitionException {
		VectorContext _localctx = new VectorContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_vector);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			number();
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(181);
				match(COMMA);
				setState(182);
				number();
				}
				}
				setState(187);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\30\u00bf\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\7\2\60\n\2\f\2"+
		"\16\2\63\13\2\3\3\3\3\3\3\3\3\5\39\n\3\3\4\6\4<\n\4\r\4\16\4=\3\4\6\4"+
		"A\n\4\r\4\16\4B\3\4\6\4F\n\4\r\4\16\4G\5\4J\n\4\3\5\3\5\3\5\3\6\5\6P\n"+
		"\6\3\6\3\6\5\6T\n\6\3\7\3\7\3\7\3\7\7\7Z\n\7\f\7\16\7]\13\7\5\7_\n\7\3"+
		"\7\3\7\3\b\3\b\5\be\n\b\3\t\3\t\3\n\3\n\3\13\3\13\5\13m\n\13\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\7\ru\n\r\f\r\16\rx\13\r\3\16\3\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\17\7\17\u0082\n\17\f\17\16\17\u0085\13\17\5\17\u0087\n\17\3\17\3"+
		"\17\3\20\3\20\3\20\3\20\5\20\u008f\n\20\3\20\3\20\5\20\u0093\n\20\3\21"+
		"\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\5\23\u00a2"+
		"\n\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00aa\n\23\3\24\3\24\3\24\5\24"+
		"\u00af\n\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\7\27\u00ba\n"+
		"\27\f\27\16\27\u00bd\13\27\3\27\2\2\30\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,\2\4\3\2\7\t\3\2\7\b\2\u00c1\2\61\3\2\2\2\48\3\2\2\2\6I"+
		"\3\2\2\2\bK\3\2\2\2\nO\3\2\2\2\fU\3\2\2\2\16d\3\2\2\2\20f\3\2\2\2\22h"+
		"\3\2\2\2\24l\3\2\2\2\26n\3\2\2\2\30q\3\2\2\2\32y\3\2\2\2\34}\3\2\2\2\36"+
		"\u008a\3\2\2\2 \u0094\3\2\2\2\"\u0099\3\2\2\2$\u00a1\3\2\2\2&\u00ab\3"+
		"\2\2\2(\u00b2\3\2\2\2*\u00b4\3\2\2\2,\u00b6\3\2\2\2.\60\5\4\3\2/.\3\2"+
		"\2\2\60\63\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2\62\3\3\2\2\2\63\61\3\2\2"+
		"\2\649\5\36\20\2\659\5\b\5\2\669\5\"\22\2\679\5 \21\28\64\3\2\2\28\65"+
		"\3\2\2\28\66\3\2\2\28\67\3\2\2\29\5\3\2\2\2:<\5\36\20\2;:\3\2\2\2<=\3"+
		"\2\2\2=;\3\2\2\2=>\3\2\2\2>J\3\2\2\2?A\5\b\5\2@?\3\2\2\2AB\3\2\2\2B@\3"+
		"\2\2\2BC\3\2\2\2CJ\3\2\2\2DF\5\30\r\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH"+
		"\3\2\2\2HJ\3\2\2\2I;\3\2\2\2I@\3\2\2\2IE\3\2\2\2J\7\3\2\2\2KL\5\n\6\2"+
		"LM\7\3\2\2M\t\3\2\2\2NP\5$\23\2ON\3\2\2\2OP\3\2\2\2PQ\3\2\2\2QS\5\24\13"+
		"\2RT\5\f\7\2SR\3\2\2\2ST\3\2\2\2T\13\3\2\2\2U^\7\17\2\2V[\5\16\b\2WX\7"+
		"\21\2\2XZ\5\16\b\2YW\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\_\3\2\2\2"+
		"][\3\2\2\2^V\3\2\2\2^_\3\2\2\2_`\3\2\2\2`a\7\20\2\2a\r\3\2\2\2be\5\22"+
		"\n\2ce\5\20\t\2db\3\2\2\2dc\3\2\2\2e\17\3\2\2\2fg\7\6\2\2g\21\3\2\2\2"+
		"hi\t\2\2\2i\23\3\2\2\2jm\7\t\2\2km\5\26\f\2lj\3\2\2\2lk\3\2\2\2m\25\3"+
		"\2\2\2no\7\4\2\2op\5\24\13\2p\27\3\2\2\2qv\5\n\6\2rs\7\21\2\2su\5\n\6"+
		"\2tr\3\2\2\2ux\3\2\2\2vt\3\2\2\2vw\3\2\2\2w\31\3\2\2\2xv\3\2\2\2yz\7\t"+
		"\2\2z{\7\5\2\2{|\5\22\n\2|\33\3\2\2\2}\u0086\7\r\2\2~\u0083\5\32\16\2"+
		"\177\u0080\7\21\2\2\u0080\u0082\5\32\16\2\u0081\177\3\2\2\2\u0082\u0085"+
		"\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0087\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0086~\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088\3\2\2\2"+
		"\u0088\u0089\7\16\2\2\u0089\35\3\2\2\2\u008a\u008b\5\n\6\2\u008b\u008c"+
		"\7\n\2\2\u008c\u008e\5\30\r\2\u008d\u008f\5(\25\2\u008e\u008d\3\2\2\2"+
		"\u008e\u008f\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0092\7\3\2\2\u0091\u0093"+
		"\5\34\17\2\u0092\u0091\3\2\2\2\u0092\u0093\3\2\2\2\u0093\37\3\2\2\2\u0094"+
		"\u0095\5\24\13\2\u0095\u0096\7\22\2\2\u0096\u0097\7\7\2\2\u0097\u0098"+
		"\5$\23\2\u0098!\3\2\2\2\u0099\u009a\5\24\13\2\u009a\u009b\7\22\2\2\u009b"+
		"\u009c\7\7\2\2\u009c\u009d\5\34\17\2\u009d#\3\2\2\2\u009e\u009f\7\25\2"+
		"\2\u009f\u00a0\7\t\2\2\u00a0\u00a2\7\5\2\2\u00a1\u009e\3\2\2\2\u00a1\u00a2"+
		"\3\2\2\2\u00a2\u00a9\3\2\2\2\u00a3\u00aa\5&\24\2\u00a4\u00aa\5*\26\2\u00a5"+
		"\u00a6\7\r\2\2\u00a6\u00a7\5,\27\2\u00a7\u00a8\7\16\2\2\u00a8\u00aa\3"+
		"\2\2\2\u00a9\u00a3\3\2\2\2\u00a9\u00a4\3\2\2\2\u00a9\u00a5\3\2\2\2\u00aa"+
		"%\3\2\2\2\u00ab\u00ae\7\13\2\2\u00ac\u00af\5*\26\2\u00ad\u00af\5,\27\2"+
		"\u00ae\u00ac\3\2\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b1"+
		"\7\f\2\2\u00b1\'\3\2\2\2\u00b2\u00b3\5$\23\2\u00b3)\3\2\2\2\u00b4\u00b5"+
		"\t\3\2\2\u00b5+\3\2\2\2\u00b6\u00bb\5*\26\2\u00b7\u00b8\7\21\2\2\u00b8"+
		"\u00ba\5*\26\2\u00b9\u00b7\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb\u00b9\3\2"+
		"\2\2\u00bb\u00bc\3\2\2\2\u00bc-\3\2\2\2\u00bd\u00bb\3\2\2\2\27\618=BG"+
		"IOS[^dlv\u0083\u0086\u008e\u0092\u00a1\u00a9\u00ae\u00bb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}