package parsers.neuralogic;// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/Neuralogic.g4 by ANTLR 4.7
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NeuralogicParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, VARIABLE=2, INT=3, FLOAT=4, ATOMIC_NAME=5, IMPLIED_BY=6, ASSIGN=7, 
		LANGLE=8, RANGLE=9, LBRACKET=10, RBRACKET=11, LPAREN=12, RPAREN=13, COMMA=14, 
		SLASH=15, CARET=16, TRUE=17, DOLLAR=18, NEGATION=19, SPECIAL=20, WS=21, 
		COMMENT=22, MULTILINE_COMMENT=23;
	public static final int
		RULE_templateFile = 0, RULE_templateLine = 1, RULE_examplesFile = 2, RULE_liftedExample = 3, 
		RULE_label = 4, RULE_queriesFile = 5, RULE_fact = 6, RULE_atom = 7, RULE_termList = 8, 
		RULE_term = 9, RULE_variable = 10, RULE_constant = 11, RULE_predicate = 12, 
		RULE_conjunction = 13, RULE_metadataVal = 14, RULE_metadataList = 15, 
		RULE_lrnnRule = 16, RULE_predicateOffset = 17, RULE_predicateMetadata = 18, 
		RULE_weightMetadata = 19, RULE_weight = 20, RULE_fixedValue = 21, RULE_offset = 22, 
		RULE_value = 23, RULE_number = 24, RULE_vector = 25, RULE_negation = 26;
	public static final String[] ruleNames = {
		"templateFile", "templateLine", "examplesFile", "liftedExample", "label", 
		"queriesFile", "fact", "atom", "termList", "term", "variable", "constant", 
		"predicate", "conjunction", "metadataVal", "metadataList", "lrnnRule", 
		"predicateOffset", "predicateMetadata", "weightMetadata", "weight", "fixedValue", 
		"offset", "value", "number", "vector", "negation"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'.'", null, null, null, null, "':-'", "'='", "'<'", "'>'", "'['", 
		"']'", "'('", "')'", "','", "'/'", "'^'", "'true'", "'$'", "'~'", "'@'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", "ASSIGN", 
		"LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", 
		"SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", "SPECIAL", "WS", "COMMENT", 
		"MULTILINE_COMMENT"
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
	public String getGrammarFileName() { return "Neuralogic.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public NeuralogicParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class TemplateFileContext extends ParserRuleContext {
		public List<TemplateLineContext> templateLine() {
			return getRuleContexts(TemplateLineContext.class);
		}
		public TemplateLineContext templateLine(int i) {
			return getRuleContext(TemplateLineContext.class,i);
		}
		public TemplateFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterTemplateFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitTemplateFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitTemplateFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TemplateFileContext templateFile() throws RecognitionException {
		TemplateFileContext _localctx = new TemplateFileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_templateFile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0)) {
				{
				{
				setState(54);
				templateLine();
				}
				}
				setState(59);
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

	public static class TemplateLineContext extends ParserRuleContext {
		public LrnnRuleContext lrnnRule() {
			return getRuleContext(LrnnRuleContext.class,0);
		}
		public FactContext fact() {
			return getRuleContext(FactContext.class,0);
		}
		public ConjunctionContext conjunction() {
			return getRuleContext(ConjunctionContext.class,0);
		}
		public PredicateMetadataContext predicateMetadata() {
			return getRuleContext(PredicateMetadataContext.class,0);
		}
		public PredicateOffsetContext predicateOffset() {
			return getRuleContext(PredicateOffsetContext.class,0);
		}
		public WeightMetadataContext weightMetadata() {
			return getRuleContext(WeightMetadataContext.class,0);
		}
		public TemplateLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterTemplateLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitTemplateLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitTemplateLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TemplateLineContext templateLine() throws RecognitionException {
		TemplateLineContext _localctx = new TemplateLineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_templateLine);
		try {
			setState(68);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(60);
				lrnnRule();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(61);
				fact();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(62);
				conjunction();
				setState(63);
				match(T__0);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(65);
				predicateMetadata();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(66);
				predicateOffset();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(67);
				weightMetadata();
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

	public static class ExamplesFileContext extends ParserRuleContext {
		public List<LabelContext> label() {
			return getRuleContexts(LabelContext.class);
		}
		public LabelContext label(int i) {
			return getRuleContext(LabelContext.class,i);
		}
		public List<TerminalNode> IMPLIED_BY() { return getTokens(NeuralogicParser.IMPLIED_BY); }
		public TerminalNode IMPLIED_BY(int i) {
			return getToken(NeuralogicParser.IMPLIED_BY, i);
		}
		public List<LiftedExampleContext> liftedExample() {
			return getRuleContexts(LiftedExampleContext.class);
		}
		public LiftedExampleContext liftedExample(int i) {
			return getRuleContext(LiftedExampleContext.class,i);
		}
		public ExamplesFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_examplesFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterExamplesFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitExamplesFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitExamplesFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExamplesFileContext examplesFile() throws RecognitionException {
		ExamplesFileContext _localctx = new ExamplesFileContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_examplesFile);
		int _la;
		try {
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(74); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(70);
					label();
					setState(71);
					match(IMPLIED_BY);
					setState(72);
					liftedExample();
					}
					}
					setState(76); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(79); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(78);
					liftedExample();
					}
					}
					setState(81); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
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

	public static class LiftedExampleContext extends ParserRuleContext {
		public List<LrnnRuleContext> lrnnRule() {
			return getRuleContexts(LrnnRuleContext.class);
		}
		public LrnnRuleContext lrnnRule(int i) {
			return getRuleContext(LrnnRuleContext.class,i);
		}
		public List<ConjunctionContext> conjunction() {
			return getRuleContexts(ConjunctionContext.class);
		}
		public ConjunctionContext conjunction(int i) {
			return getRuleContext(ConjunctionContext.class,i);
		}
		public LiftedExampleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_liftedExample; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterLiftedExample(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitLiftedExample(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitLiftedExample(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiftedExampleContext liftedExample() throws RecognitionException {
		LiftedExampleContext _localctx = new LiftedExampleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_liftedExample);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(87); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(87);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(85);
					lrnnRule();
					}
					break;
				case 2:
					{
					setState(86);
					conjunction();
					}
					break;
				}
				}
				setState(89); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
			setState(91);
			match(T__0);
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

	public static class LabelContext extends ParserRuleContext {
		public ConjunctionContext conjunction() {
			return getRuleContext(ConjunctionContext.class,0);
		}
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			conjunction();
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

	public static class QueriesFileContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public List<TerminalNode> IMPLIED_BY() { return getTokens(NeuralogicParser.IMPLIED_BY); }
		public TerminalNode IMPLIED_BY(int i) {
			return getToken(NeuralogicParser.IMPLIED_BY, i);
		}
		public List<ConjunctionContext> conjunction() {
			return getRuleContexts(ConjunctionContext.class);
		}
		public ConjunctionContext conjunction(int i) {
			return getRuleContext(ConjunctionContext.class,i);
		}
		public QueriesFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queriesFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterQueriesFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitQueriesFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitQueriesFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueriesFileContext queriesFile() throws RecognitionException {
		QueriesFileContext _localctx = new QueriesFileContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_queriesFile);
		int _la;
		try {
			setState(110);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(99); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(95);
					atom();
					setState(96);
					match(IMPLIED_BY);
					setState(97);
					conjunction();
					}
					}
					setState(101); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(106); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(103);
					conjunction();
					setState(104);
					match(T__0);
					}
					}
					setState(108); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
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
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterFact(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitFact(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitFact(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactContext fact() throws RecognitionException {
		FactContext _localctx = new FactContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_fact);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			atom();
			setState(113);
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
		public NegationContext negation() {
			return getRuleContext(NegationContext.class,0);
		}
		public TermListContext termList() {
			return getRuleContext(TermListContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR))) != 0)) {
				{
				setState(115);
				weight();
				}
			}

			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NEGATION) {
				{
				setState(118);
				negation();
				}
			}

			setState(121);
			predicate();
			setState(123);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(122);
				termList();
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

	public static class TermListContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(NeuralogicParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(NeuralogicParser.RPAREN, 0); }
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(NeuralogicParser.COMMA, i);
		}
		public TermListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterTermList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitTermList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitTermList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermListContext termList() throws RecognitionException {
		TermListContext _localctx = new TermListContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_termList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			match(LPAREN);
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARIABLE) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME))) != 0)) {
				{
				setState(126);
				term();
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(127);
					match(COMMA);
					setState(128);
					term();
					}
					}
					setState(133);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(136);
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
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_term);
		try {
			setState(140);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case ATOMIC_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(138);
				constant();
				}
				break;
			case VARIABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
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
		public TerminalNode VARIABLE() { return getToken(NeuralogicParser.VARIABLE, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
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
		public TerminalNode ATOMIC_NAME() { return getToken(NeuralogicParser.ATOMIC_NAME, 0); }
		public TerminalNode INT() { return getToken(NeuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(NeuralogicParser.FLOAT, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_constant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
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
		public TerminalNode ATOMIC_NAME() { return getToken(NeuralogicParser.ATOMIC_NAME, 0); }
		public TerminalNode SPECIAL() { return getToken(NeuralogicParser.SPECIAL, 0); }
		public TerminalNode SLASH() { return getToken(NeuralogicParser.SLASH, 0); }
		public TerminalNode INT() { return getToken(NeuralogicParser.INT, 0); }
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SPECIAL) {
				{
				setState(146);
				match(SPECIAL);
				}
			}

			setState(149);
			match(ATOMIC_NAME);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLASH) {
				{
				setState(150);
				match(SLASH);
				setState(151);
				match(INT);
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

	public static class ConjunctionContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(NeuralogicParser.COMMA, i);
		}
		public ConjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterConjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitConjunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitConjunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConjunctionContext conjunction() throws RecognitionException {
		ConjunctionContext _localctx = new ConjunctionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_conjunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			atom();
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(155);
				match(COMMA);
				setState(156);
				atom();
				}
				}
				setState(161);
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

	public static class MetadataValContext extends ParserRuleContext {
		public List<TerminalNode> ATOMIC_NAME() { return getTokens(NeuralogicParser.ATOMIC_NAME); }
		public TerminalNode ATOMIC_NAME(int i) {
			return getToken(NeuralogicParser.ATOMIC_NAME, i);
		}
		public TerminalNode ASSIGN() { return getToken(NeuralogicParser.ASSIGN, 0); }
		public TerminalNode DOLLAR() { return getToken(NeuralogicParser.DOLLAR, 0); }
		public MetadataValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadataVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterMetadataVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitMetadataVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitMetadataVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MetadataValContext metadataVal() throws RecognitionException {
		MetadataValContext _localctx = new MetadataValContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_metadataVal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			match(ATOMIC_NAME);
			setState(163);
			match(ASSIGN);
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(164);
				match(DOLLAR);
				}
			}

			setState(167);
			match(ATOMIC_NAME);
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

	public static class MetadataListContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(NeuralogicParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(NeuralogicParser.RBRACKET, 0); }
		public List<MetadataValContext> metadataVal() {
			return getRuleContexts(MetadataValContext.class);
		}
		public MetadataValContext metadataVal(int i) {
			return getRuleContext(MetadataValContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(NeuralogicParser.COMMA, i);
		}
		public MetadataListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadataList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterMetadataList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitMetadataList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitMetadataList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MetadataListContext metadataList() throws RecognitionException {
		MetadataListContext _localctx = new MetadataListContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_metadataList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(LBRACKET);
			setState(178);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ATOMIC_NAME) {
				{
				setState(170);
				metadataVal();
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(171);
					match(COMMA);
					setState(172);
					metadataVal();
					}
					}
					setState(177);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(180);
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

	public static class LrnnRuleContext extends ParserRuleContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode IMPLIED_BY() { return getToken(NeuralogicParser.IMPLIED_BY, 0); }
		public ConjunctionContext conjunction() {
			return getRuleContext(ConjunctionContext.class,0);
		}
		public OffsetContext offset() {
			return getRuleContext(OffsetContext.class,0);
		}
		public MetadataListContext metadataList() {
			return getRuleContext(MetadataListContext.class,0);
		}
		public LrnnRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lrnnRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterLrnnRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitLrnnRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitLrnnRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LrnnRuleContext lrnnRule() throws RecognitionException {
		LrnnRuleContext _localctx = new LrnnRuleContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_lrnnRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			atom();
			setState(183);
			match(IMPLIED_BY);
			setState(184);
			conjunction();
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE) | (1L << LPAREN) | (1L << DOLLAR))) != 0)) {
				{
				setState(185);
				offset();
				}
			}

			setState(188);
			match(T__0);
			setState(190);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACKET) {
				{
				setState(189);
				metadataList();
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

	public static class PredicateOffsetContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
		public PredicateOffsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicateOffset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterPredicateOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitPredicateOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitPredicateOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateOffsetContext predicateOffset() throws RecognitionException {
		PredicateOffsetContext _localctx = new PredicateOffsetContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_predicateOffset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			predicate();
			setState(193);
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

	public static class PredicateMetadataContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public MetadataListContext metadataList() {
			return getRuleContext(MetadataListContext.class,0);
		}
		public PredicateMetadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicateMetadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterPredicateMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitPredicateMetadata(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitPredicateMetadata(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateMetadataContext predicateMetadata() throws RecognitionException {
		PredicateMetadataContext _localctx = new PredicateMetadataContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_predicateMetadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			predicate();
			setState(196);
			metadataList();
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

	public static class WeightMetadataContext extends ParserRuleContext {
		public TerminalNode DOLLAR() { return getToken(NeuralogicParser.DOLLAR, 0); }
		public TerminalNode ATOMIC_NAME() { return getToken(NeuralogicParser.ATOMIC_NAME, 0); }
		public MetadataListContext metadataList() {
			return getRuleContext(MetadataListContext.class,0);
		}
		public WeightMetadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weightMetadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterWeightMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitWeightMetadata(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitWeightMetadata(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightMetadataContext weightMetadata() throws RecognitionException {
		WeightMetadataContext _localctx = new WeightMetadataContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_weightMetadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			match(DOLLAR);
			setState(199);
			match(ATOMIC_NAME);
			setState(200);
			metadataList();
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
		public FixedValueContext fixedValue() {
			return getRuleContext(FixedValueContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode DOLLAR() { return getToken(NeuralogicParser.DOLLAR, 0); }
		public TerminalNode ATOMIC_NAME() { return getToken(NeuralogicParser.ATOMIC_NAME, 0); }
		public TerminalNode ASSIGN() { return getToken(NeuralogicParser.ASSIGN, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_weight);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(202);
				match(DOLLAR);
				setState(203);
				match(ATOMIC_NAME);
				setState(204);
				match(ASSIGN);
				}
			}

			setState(209);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANGLE:
				{
				setState(207);
				fixedValue();
				}
				break;
			case INT:
			case FLOAT:
			case LPAREN:
				{
				setState(208);
				value();
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

	public static class FixedValueContext extends ParserRuleContext {
		public TerminalNode LANGLE() { return getToken(NeuralogicParser.LANGLE, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode RANGLE() { return getToken(NeuralogicParser.RANGLE, 0); }
		public FixedValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fixedValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterFixedValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitFixedValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitFixedValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FixedValueContext fixedValue() throws RecognitionException {
		FixedValueContext _localctx = new FixedValueContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_fixedValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(LANGLE);
			setState(212);
			value();
			setState(213);
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
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OffsetContext offset() throws RecognitionException {
		OffsetContext _localctx = new OffsetContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
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

	public static class ValueContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public VectorContext vector() {
			return getRuleContext(VectorContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_value);
		try {
			setState(219);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(217);
				number();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(218);
				vector();
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

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(NeuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(NeuralogicParser.FLOAT, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
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
		public TerminalNode LPAREN() { return getToken(NeuralogicParser.LPAREN, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(NeuralogicParser.RPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(NeuralogicParser.COMMA, i);
		}
		public VectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterVector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitVector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitVector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VectorContext vector() throws RecognitionException {
		VectorContext _localctx = new VectorContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_vector);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			match(LPAREN);
			setState(224);
			number();
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(225);
				match(COMMA);
				setState(226);
				number();
				}
				}
				setState(231);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(232);
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

	public static class NegationContext extends ParserRuleContext {
		public TerminalNode NEGATION() { return getToken(NeuralogicParser.NEGATION, 0); }
		public NegationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterNegation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitNegation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitNegation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NegationContext negation() throws RecognitionException {
		NegationContext _localctx = new NegationContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			match(NEGATION);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\31\u00ef\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\7\2:\n\2\f\2\16\2=\13\2\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\5\3G\n\3\3\4\3\4\3\4\3\4\6\4M\n\4\r\4\16\4N\3\4\6"+
		"\4R\n\4\r\4\16\4S\5\4V\n\4\3\5\3\5\6\5Z\n\5\r\5\16\5[\3\5\3\5\3\6\3\6"+
		"\3\7\3\7\3\7\3\7\6\7f\n\7\r\7\16\7g\3\7\3\7\3\7\6\7m\n\7\r\7\16\7n\5\7"+
		"q\n\7\3\b\3\b\3\b\3\t\5\tw\n\t\3\t\5\tz\n\t\3\t\3\t\5\t~\n\t\3\n\3\n\3"+
		"\n\3\n\7\n\u0084\n\n\f\n\16\n\u0087\13\n\5\n\u0089\n\n\3\n\3\n\3\13\3"+
		"\13\5\13\u008f\n\13\3\f\3\f\3\r\3\r\3\16\5\16\u0096\n\16\3\16\3\16\3\16"+
		"\5\16\u009b\n\16\3\17\3\17\3\17\7\17\u00a0\n\17\f\17\16\17\u00a3\13\17"+
		"\3\20\3\20\3\20\5\20\u00a8\n\20\3\20\3\20\3\21\3\21\3\21\3\21\7\21\u00b0"+
		"\n\21\f\21\16\21\u00b3\13\21\5\21\u00b5\n\21\3\21\3\21\3\22\3\22\3\22"+
		"\3\22\5\22\u00bd\n\22\3\22\3\22\5\22\u00c1\n\22\3\23\3\23\3\23\3\24\3"+
		"\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\5\26\u00d0\n\26\3\26\3\26"+
		"\5\26\u00d4\n\26\3\27\3\27\3\27\3\27\3\30\3\30\3\31\3\31\5\31\u00de\n"+
		"\31\3\32\3\32\3\33\3\33\3\33\3\33\7\33\u00e6\n\33\f\33\16\33\u00e9\13"+
		"\33\3\33\3\33\3\34\3\34\3\34\2\2\35\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \"$&(*,.\60\62\64\66\2\4\3\2\5\7\3\2\5\6\2\u00f3\2;\3\2\2\2\4F\3\2"+
		"\2\2\6U\3\2\2\2\bY\3\2\2\2\n_\3\2\2\2\fp\3\2\2\2\16r\3\2\2\2\20v\3\2\2"+
		"\2\22\177\3\2\2\2\24\u008e\3\2\2\2\26\u0090\3\2\2\2\30\u0092\3\2\2\2\32"+
		"\u0095\3\2\2\2\34\u009c\3\2\2\2\36\u00a4\3\2\2\2 \u00ab\3\2\2\2\"\u00b8"+
		"\3\2\2\2$\u00c2\3\2\2\2&\u00c5\3\2\2\2(\u00c8\3\2\2\2*\u00cf\3\2\2\2,"+
		"\u00d5\3\2\2\2.\u00d9\3\2\2\2\60\u00dd\3\2\2\2\62\u00df\3\2\2\2\64\u00e1"+
		"\3\2\2\2\66\u00ec\3\2\2\28:\5\4\3\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3"+
		"\2\2\2<\3\3\2\2\2=;\3\2\2\2>G\5\"\22\2?G\5\16\b\2@A\5\34\17\2AB\7\3\2"+
		"\2BG\3\2\2\2CG\5&\24\2DG\5$\23\2EG\5(\25\2F>\3\2\2\2F?\3\2\2\2F@\3\2\2"+
		"\2FC\3\2\2\2FD\3\2\2\2FE\3\2\2\2G\5\3\2\2\2HI\5\n\6\2IJ\7\b\2\2JK\5\b"+
		"\5\2KM\3\2\2\2LH\3\2\2\2MN\3\2\2\2NL\3\2\2\2NO\3\2\2\2OV\3\2\2\2PR\5\b"+
		"\5\2QP\3\2\2\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TV\3\2\2\2UL\3\2\2\2UQ\3\2"+
		"\2\2V\7\3\2\2\2WZ\5\"\22\2XZ\5\34\17\2YW\3\2\2\2YX\3\2\2\2Z[\3\2\2\2["+
		"Y\3\2\2\2[\\\3\2\2\2\\]\3\2\2\2]^\7\3\2\2^\t\3\2\2\2_`\5\34\17\2`\13\3"+
		"\2\2\2ab\5\20\t\2bc\7\b\2\2cd\5\34\17\2df\3\2\2\2ea\3\2\2\2fg\3\2\2\2"+
		"ge\3\2\2\2gh\3\2\2\2hq\3\2\2\2ij\5\34\17\2jk\7\3\2\2km\3\2\2\2li\3\2\2"+
		"\2mn\3\2\2\2nl\3\2\2\2no\3\2\2\2oq\3\2\2\2pe\3\2\2\2pl\3\2\2\2q\r\3\2"+
		"\2\2rs\5\20\t\2st\7\3\2\2t\17\3\2\2\2uw\5*\26\2vu\3\2\2\2vw\3\2\2\2wy"+
		"\3\2\2\2xz\5\66\34\2yx\3\2\2\2yz\3\2\2\2z{\3\2\2\2{}\5\32\16\2|~\5\22"+
		"\n\2}|\3\2\2\2}~\3\2\2\2~\21\3\2\2\2\177\u0088\7\16\2\2\u0080\u0085\5"+
		"\24\13\2\u0081\u0082\7\20\2\2\u0082\u0084\5\24\13\2\u0083\u0081\3\2\2"+
		"\2\u0084\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0089"+
		"\3\2\2\2\u0087\u0085\3\2\2\2\u0088\u0080\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\u008a\3\2\2\2\u008a\u008b\7\17\2\2\u008b\23\3\2\2\2\u008c\u008f\5\30"+
		"\r\2\u008d\u008f\5\26\f\2\u008e\u008c\3\2\2\2\u008e\u008d\3\2\2\2\u008f"+
		"\25\3\2\2\2\u0090\u0091\7\4\2\2\u0091\27\3\2\2\2\u0092\u0093\t\2\2\2\u0093"+
		"\31\3\2\2\2\u0094\u0096\7\26\2\2\u0095\u0094\3\2\2\2\u0095\u0096\3\2\2"+
		"\2\u0096\u0097\3\2\2\2\u0097\u009a\7\7\2\2\u0098\u0099\7\21\2\2\u0099"+
		"\u009b\7\5\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\33\3\2\2"+
		"\2\u009c\u00a1\5\20\t\2\u009d\u009e\7\20\2\2\u009e\u00a0\5\20\t\2\u009f"+
		"\u009d\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2"+
		"\2\2\u00a2\35\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\7\7\2\2\u00a5\u00a7"+
		"\7\t\2\2\u00a6\u00a8\7\24\2\2\u00a7\u00a6\3\2\2\2\u00a7\u00a8\3\2\2\2"+
		"\u00a8\u00a9\3\2\2\2\u00a9\u00aa\7\7\2\2\u00aa\37\3\2\2\2\u00ab\u00b4"+
		"\7\f\2\2\u00ac\u00b1\5\36\20\2\u00ad\u00ae\7\20\2\2\u00ae\u00b0\5\36\20"+
		"\2\u00af\u00ad\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2"+
		"\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00ac\3\2\2\2\u00b4"+
		"\u00b5\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b7\7\r\2\2\u00b7!\3\2\2\2"+
		"\u00b8\u00b9\5\20\t\2\u00b9\u00ba\7\b\2\2\u00ba\u00bc\5\34\17\2\u00bb"+
		"\u00bd\5.\30\2\u00bc\u00bb\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00be\3\2"+
		"\2\2\u00be\u00c0\7\3\2\2\u00bf\u00c1\5 \21\2\u00c0\u00bf\3\2\2\2\u00c0"+
		"\u00c1\3\2\2\2\u00c1#\3\2\2\2\u00c2\u00c3\5\32\16\2\u00c3\u00c4\5*\26"+
		"\2\u00c4%\3\2\2\2\u00c5\u00c6\5\32\16\2\u00c6\u00c7\5 \21\2\u00c7\'\3"+
		"\2\2\2\u00c8\u00c9\7\24\2\2\u00c9\u00ca\7\7\2\2\u00ca\u00cb\5 \21\2\u00cb"+
		")\3\2\2\2\u00cc\u00cd\7\24\2\2\u00cd\u00ce\7\7\2\2\u00ce\u00d0\7\t\2\2"+
		"\u00cf\u00cc\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1\u00d4"+
		"\5,\27\2\u00d2\u00d4\5\60\31\2\u00d3\u00d1\3\2\2\2\u00d3\u00d2\3\2\2\2"+
		"\u00d4+\3\2\2\2\u00d5\u00d6\7\n\2\2\u00d6\u00d7\5\60\31\2\u00d7\u00d8"+
		"\7\13\2\2\u00d8-\3\2\2\2\u00d9\u00da\5*\26\2\u00da/\3\2\2\2\u00db\u00de"+
		"\5\62\32\2\u00dc\u00de\5\64\33\2\u00dd\u00db\3\2\2\2\u00dd\u00dc\3\2\2"+
		"\2\u00de\61\3\2\2\2\u00df\u00e0\t\3\2\2\u00e0\63\3\2\2\2\u00e1\u00e2\7"+
		"\16\2\2\u00e2\u00e7\5\62\32\2\u00e3\u00e4\7\20\2\2\u00e4\u00e6\5\62\32"+
		"\2\u00e5\u00e3\3\2\2\2\u00e6\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8"+
		"\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00eb\7\17\2\2"+
		"\u00eb\65\3\2\2\2\u00ec\u00ed\7\25\2\2\u00ed\67\3\2\2\2\36;FNSUY[gnpv"+
		"y}\u0085\u0088\u008e\u0095\u009a\u00a1\u00a7\u00b1\u00b4\u00bc\u00c0\u00cf"+
		"\u00d3\u00dd\u00e7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}