// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/Neuralogic.g4 by ANTLR 4.7.2
package parsers.antlr;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import parsing.antlr.NeuralogicListener;
import parsing.antlr.NeuralogicVisitor;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NeuralogicParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

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
		RULE_weightMetadata = 19, RULE_templateMetadata = 20, RULE_weight = 21, 
		RULE_fixedValue = 22, RULE_offset = 23, RULE_value = 24, RULE_number = 25, 
		RULE_vector = 26, RULE_matrix = 27, RULE_dimensions = 28, RULE_negation = 29;
	private static String[] makeRuleNames() {
		return new String[] {
			"templateFile", "templateLine", "examplesFile", "liftedExample", "label", 
			"queriesFile", "fact", "atom", "termList", "term", "variable", "constant", 
			"predicate", "conjunction", "metadataVal", "metadataList", "lrnnRule", 
			"predicateOffset", "predicateMetadata", "weightMetadata", "templateMetadata", 
			"weight", "fixedValue", "offset", "value", "number", "vector", "matrix", 
			"dimensions", "negation"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'.'", null, null, null, null, "':-'", "'='", "'<'", "'>'", "'['", 
			"']'", "'('", "')'", "','", "'/'", "'^'", "'true'", "'$'", "'~'", "'@'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
			"ASSIGN", "LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", 
			"COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", "SPECIAL", "WS", 
			"COMMENT", "MULTILINE_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterTemplateFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitTemplateFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitTemplateFile(this);
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
			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0)) {
				{
				{
				setState(60);
				templateLine();
				}
				}
				setState(65);
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
		public TemplateMetadataContext templateMetadata() {
			return getRuleContext(TemplateMetadataContext.class,0);
		}
		public TemplateLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterTemplateLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitTemplateLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitTemplateLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TemplateLineContext templateLine() throws RecognitionException {
		TemplateLineContext _localctx = new TemplateLineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_templateLine);
		try {
			setState(75);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(66);
				lrnnRule();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
				fact();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(68);
				conjunction();
				setState(69);
				match(T__0);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(71);
				predicateMetadata();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(72);
				predicateOffset();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(73);
				weightMetadata();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(74);
				templateMetadata();
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
		public List<TerminalNode> IMPLIED_BY() { return getTokens(parsing.antlr.NeuralogicParser.IMPLIED_BY); }
		public TerminalNode IMPLIED_BY(int i) {
			return getToken(parsing.antlr.NeuralogicParser.IMPLIED_BY, i);
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterExamplesFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitExamplesFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitExamplesFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExamplesFileContext examplesFile() throws RecognitionException {
		ExamplesFileContext _localctx = new ExamplesFileContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_examplesFile);
		int _la;
		try {
			setState(90);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(81); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(77);
					label();
					setState(78);
					match(IMPLIED_BY);
					setState(79);
					liftedExample();
					}
					}
					setState(83); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(86); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(85);
					liftedExample();
					}
					}
					setState(88); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterLiftedExample(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitLiftedExample(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitLiftedExample(this);
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
			setState(94); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(94);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(92);
					lrnnRule();
					}
					break;
				case 2:
					{
					setState(93);
					conjunction();
					}
					break;
				}
				}
				setState(96); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
			setState(98);
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
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
		public List<TerminalNode> IMPLIED_BY() { return getTokens(parsing.antlr.NeuralogicParser.IMPLIED_BY); }
		public TerminalNode IMPLIED_BY(int i) {
			return getToken(parsing.antlr.NeuralogicParser.IMPLIED_BY, i);
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterQueriesFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitQueriesFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitQueriesFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueriesFileContext queriesFile() throws RecognitionException {
		QueriesFileContext _localctx = new QueriesFileContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_queriesFile);
		int _la;
		try {
			setState(117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(106); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(102);
					atom();
					setState(103);
					match(IMPLIED_BY);
					setState(104);
					conjunction();
					}
					}
					setState(108); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(113); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(110);
					conjunction();
					setState(111);
					match(T__0);
					}
					}
					setState(115); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL))) != 0) );
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterFact(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitFact(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitFact(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactContext fact() throws RecognitionException {
		FactContext _localctx = new FactContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_fact);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			atom();
			setState(120);
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitAtom(this);
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
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR))) != 0)) {
				{
				setState(122);
				weight();
				}
			}

			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NEGATION) {
				{
				setState(125);
				negation();
				}
			}

			setState(128);
			predicate();
			setState(130);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(129);
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
		public TerminalNode LPAREN() { return getToken(parsing.antlr.NeuralogicParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(parsing.antlr.NeuralogicParser.RPAREN, 0); }
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(parsing.antlr.NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(parsing.antlr.NeuralogicParser.COMMA, i);
		}
		public TermListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterTermList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitTermList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitTermList(this);
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
			setState(132);
			match(LPAREN);
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARIABLE) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME))) != 0)) {
				{
				setState(133);
				term();
				setState(138);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(134);
					match(COMMA);
					setState(135);
					term();
					}
					}
					setState(140);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(143);
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_term);
		try {
			setState(147);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case ATOMIC_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(145);
				constant();
				}
				break;
			case VARIABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(146);
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
		public TerminalNode VARIABLE() { return getToken(parsing.antlr.NeuralogicParser.VARIABLE, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
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
		public TerminalNode ATOMIC_NAME() { return getToken(parsing.antlr.NeuralogicParser.ATOMIC_NAME, 0); }
		public TerminalNode INT() { return getToken(parsing.antlr.NeuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(parsing.antlr.NeuralogicParser.FLOAT, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitConstant(this);
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
			setState(151);
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
		public TerminalNode ATOMIC_NAME() { return getToken(parsing.antlr.NeuralogicParser.ATOMIC_NAME, 0); }
		public TerminalNode SPECIAL() { return getToken(parsing.antlr.NeuralogicParser.SPECIAL, 0); }
		public TerminalNode SLASH() { return getToken(parsing.antlr.NeuralogicParser.SLASH, 0); }
		public TerminalNode INT() { return getToken(parsing.antlr.NeuralogicParser.INT, 0); }
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitPredicate(this);
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
			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SPECIAL) {
				{
				setState(153);
				match(SPECIAL);
				}
			}

			setState(156);
			match(ATOMIC_NAME);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLASH) {
				{
				setState(157);
				match(SLASH);
				setState(158);
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
		public List<TerminalNode> COMMA() { return getTokens(parsing.antlr.NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(parsing.antlr.NeuralogicParser.COMMA, i);
		}
		public ConjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterConjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitConjunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitConjunction(this);
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
			setState(161);
			atom();
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(162);
				match(COMMA);
				setState(163);
				atom();
				}
				}
				setState(168);
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
		public List<TerminalNode> ATOMIC_NAME() { return getTokens(parsing.antlr.NeuralogicParser.ATOMIC_NAME); }
		public TerminalNode ATOMIC_NAME(int i) {
			return getToken(parsing.antlr.NeuralogicParser.ATOMIC_NAME, i);
		}
		public TerminalNode ASSIGN() { return getToken(parsing.antlr.NeuralogicParser.ASSIGN, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode DOLLAR() { return getToken(parsing.antlr.NeuralogicParser.DOLLAR, 0); }
		public MetadataValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadataVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterMetadataVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitMetadataVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitMetadataVal(this);
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
			setState(169);
			match(ATOMIC_NAME);
			setState(170);
			match(ASSIGN);
			setState(176);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ATOMIC_NAME:
			case DOLLAR:
				{
				{
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOLLAR) {
					{
					setState(171);
					match(DOLLAR);
					}
				}

				setState(174);
				match(ATOMIC_NAME);
				}
				}
				break;
			case INT:
			case FLOAT:
			case LBRACKET:
			case LPAREN:
				{
				setState(175);
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

	public static class MetadataListContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(parsing.antlr.NeuralogicParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(parsing.antlr.NeuralogicParser.RBRACKET, 0); }
		public List<MetadataValContext> metadataVal() {
			return getRuleContexts(MetadataValContext.class);
		}
		public MetadataValContext metadataVal(int i) {
			return getRuleContext(MetadataValContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(parsing.antlr.NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(parsing.antlr.NeuralogicParser.COMMA, i);
		}
		public MetadataListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metadataList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterMetadataList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitMetadataList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitMetadataList(this);
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
			setState(178);
			match(LBRACKET);
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ATOMIC_NAME) {
				{
				setState(179);
				metadataVal();
				setState(184);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(180);
					match(COMMA);
					setState(181);
					metadataVal();
					}
					}
					setState(186);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(189);
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
		public TerminalNode IMPLIED_BY() { return getToken(parsing.antlr.NeuralogicParser.IMPLIED_BY, 0); }
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterLrnnRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitLrnnRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitLrnnRule(this);
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
			setState(191);
			atom();
			setState(192);
			match(IMPLIED_BY);
			setState(193);
			conjunction();
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LANGLE) | (1L << LBRACKET) | (1L << LPAREN) | (1L << DOLLAR))) != 0)) {
				{
				setState(194);
				offset();
				}
			}

			setState(197);
			match(T__0);
			setState(199);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(198);
				metadataList();
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterPredicateOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitPredicateOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitPredicateOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateOffsetContext predicateOffset() throws RecognitionException {
		PredicateOffsetContext _localctx = new PredicateOffsetContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_predicateOffset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			predicate();
			setState(202);
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterPredicateMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitPredicateMetadata(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitPredicateMetadata(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateMetadataContext predicateMetadata() throws RecognitionException {
		PredicateMetadataContext _localctx = new PredicateMetadataContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_predicateMetadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			predicate();
			setState(205);
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
		public TerminalNode DOLLAR() { return getToken(parsing.antlr.NeuralogicParser.DOLLAR, 0); }
		public TerminalNode ATOMIC_NAME() { return getToken(parsing.antlr.NeuralogicParser.ATOMIC_NAME, 0); }
		public MetadataListContext metadataList() {
			return getRuleContext(MetadataListContext.class,0);
		}
		public WeightMetadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weightMetadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterWeightMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitWeightMetadata(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitWeightMetadata(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightMetadataContext weightMetadata() throws RecognitionException {
		WeightMetadataContext _localctx = new WeightMetadataContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_weightMetadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(DOLLAR);
			setState(208);
			match(ATOMIC_NAME);
			setState(209);
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

	public static class TemplateMetadataContext extends ParserRuleContext {
		public MetadataListContext metadataList() {
			return getRuleContext(MetadataListContext.class,0);
		}
		public TemplateMetadataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_templateMetadata; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterTemplateMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitTemplateMetadata(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitTemplateMetadata(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TemplateMetadataContext templateMetadata() throws RecognitionException {
		TemplateMetadataContext _localctx = new TemplateMetadataContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_templateMetadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
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
		public TerminalNode DOLLAR() { return getToken(parsing.antlr.NeuralogicParser.DOLLAR, 0); }
		public TerminalNode ATOMIC_NAME() { return getToken(parsing.antlr.NeuralogicParser.ATOMIC_NAME, 0); }
		public TerminalNode ASSIGN() { return getToken(parsing.antlr.NeuralogicParser.ASSIGN, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_weight);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(213);
				match(DOLLAR);
				setState(214);
				match(ATOMIC_NAME);
				setState(215);
				match(ASSIGN);
				}
			}

			setState(220);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANGLE:
				{
				setState(218);
				fixedValue();
				}
				break;
			case INT:
			case FLOAT:
			case LBRACKET:
			case LPAREN:
				{
				setState(219);
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
		public TerminalNode LANGLE() { return getToken(parsing.antlr.NeuralogicParser.LANGLE, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode RANGLE() { return getToken(parsing.antlr.NeuralogicParser.RANGLE, 0); }
		public FixedValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fixedValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterFixedValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitFixedValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitFixedValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FixedValueContext fixedValue() throws RecognitionException {
		FixedValueContext _localctx = new FixedValueContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_fixedValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			match(LANGLE);
			setState(223);
			value();
			setState(224);
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
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OffsetContext offset() throws RecognitionException {
		OffsetContext _localctx = new OffsetContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
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
		public MatrixContext matrix() {
			return getRuleContext(MatrixContext.class,0);
		}
		public DimensionsContext dimensions() {
			return getRuleContext(DimensionsContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_value);
		try {
			setState(232);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(228);
				number();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(229);
				vector();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(230);
				matrix();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(231);
				dimensions();
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

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(parsing.antlr.NeuralogicParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(parsing.antlr.NeuralogicParser.FLOAT, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
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
		public TerminalNode LPAREN() { return getToken(parsing.antlr.NeuralogicParser.LPAREN, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(parsing.antlr.NeuralogicParser.RPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(parsing.antlr.NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(parsing.antlr.NeuralogicParser.COMMA, i);
		}
		public VectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterVector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitVector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitVector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VectorContext vector() throws RecognitionException {
		VectorContext _localctx = new VectorContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_vector);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			match(LPAREN);
			setState(237);
			number();
			setState(242);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(238);
				match(COMMA);
				setState(239);
				number();
				}
				}
				setState(244);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(245);
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

	public static class MatrixContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(parsing.antlr.NeuralogicParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(parsing.antlr.NeuralogicParser.RPAREN, 0); }
		public List<VectorContext> vector() {
			return getRuleContexts(VectorContext.class);
		}
		public VectorContext vector(int i) {
			return getRuleContext(VectorContext.class,i);
		}
		public MatrixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matrix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterMatrix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitMatrix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitMatrix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatrixContext matrix() throws RecognitionException {
		MatrixContext _localctx = new MatrixContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_matrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			match(LPAREN);
			setState(249); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(248);
				vector();
				}
				}
				setState(251); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LPAREN );
			setState(253);
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

	public static class DimensionsContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(parsing.antlr.NeuralogicParser.LBRACKET, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public TerminalNode RBRACKET() { return getToken(parsing.antlr.NeuralogicParser.RBRACKET, 0); }
		public List<TerminalNode> COMMA() { return getTokens(parsing.antlr.NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(parsing.antlr.NeuralogicParser.COMMA, i);
		}
		public DimensionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dimensions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).exitDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((parsing.antlr.NeuralogicVisitor<? extends T>)visitor).visitDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DimensionsContext dimensions() throws RecognitionException {
		DimensionsContext _localctx = new DimensionsContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_dimensions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(LBRACKET);
			setState(256);
			number();
			setState(261);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(257);
				match(COMMA);
				setState(258);
				number();
				}
				}
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(264);
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

	public static class NegationContext extends ParserRuleContext {
		public TerminalNode NEGATION() { return getToken(parsing.antlr.NeuralogicParser.NEGATION, 0); }
		public NegationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((parsing.antlr.NeuralogicListener)listener).enterNegation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof parsing.antlr.NeuralogicListener) ((NeuralogicListener)listener).exitNegation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof parsing.antlr.NeuralogicVisitor) return ((NeuralogicVisitor<? extends T>)visitor).visitNegation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NegationContext negation() throws RecognitionException {
		NegationContext _localctx = new NegationContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\31\u010f\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2\7\2@"+
		"\n\2\f\2\16\2C\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3N\n\3\3\4\3"+
		"\4\3\4\3\4\6\4T\n\4\r\4\16\4U\3\4\6\4Y\n\4\r\4\16\4Z\5\4]\n\4\3\5\3\5"+
		"\6\5a\n\5\r\5\16\5b\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\6\7m\n\7\r\7\16\7"+
		"n\3\7\3\7\3\7\6\7t\n\7\r\7\16\7u\5\7x\n\7\3\b\3\b\3\b\3\t\5\t~\n\t\3\t"+
		"\5\t\u0081\n\t\3\t\3\t\5\t\u0085\n\t\3\n\3\n\3\n\3\n\7\n\u008b\n\n\f\n"+
		"\16\n\u008e\13\n\5\n\u0090\n\n\3\n\3\n\3\13\3\13\5\13\u0096\n\13\3\f\3"+
		"\f\3\r\3\r\3\16\5\16\u009d\n\16\3\16\3\16\3\16\5\16\u00a2\n\16\3\17\3"+
		"\17\3\17\7\17\u00a7\n\17\f\17\16\17\u00aa\13\17\3\20\3\20\3\20\5\20\u00af"+
		"\n\20\3\20\3\20\5\20\u00b3\n\20\3\21\3\21\3\21\3\21\7\21\u00b9\n\21\f"+
		"\21\16\21\u00bc\13\21\5\21\u00be\n\21\3\21\3\21\3\22\3\22\3\22\3\22\5"+
		"\22\u00c6\n\22\3\22\3\22\5\22\u00ca\n\22\3\23\3\23\3\23\3\24\3\24\3\24"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\27\5\27\u00db\n\27\3\27\3\27"+
		"\5\27\u00df\n\27\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\32\5\32"+
		"\u00eb\n\32\3\33\3\33\3\34\3\34\3\34\3\34\7\34\u00f3\n\34\f\34\16\34\u00f6"+
		"\13\34\3\34\3\34\3\35\3\35\6\35\u00fc\n\35\r\35\16\35\u00fd\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\7\36\u0106\n\36\f\36\16\36\u0109\13\36\3\36\3\36"+
		"\3\37\3\37\3\37\2\2 \2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:<\2\4\3\2\5\7\3\2\5\6\2\u0116\2A\3\2\2\2\4M\3\2\2\2\6\\\3\2"+
		"\2\2\b`\3\2\2\2\nf\3\2\2\2\fw\3\2\2\2\16y\3\2\2\2\20}\3\2\2\2\22\u0086"+
		"\3\2\2\2\24\u0095\3\2\2\2\26\u0097\3\2\2\2\30\u0099\3\2\2\2\32\u009c\3"+
		"\2\2\2\34\u00a3\3\2\2\2\36\u00ab\3\2\2\2 \u00b4\3\2\2\2\"\u00c1\3\2\2"+
		"\2$\u00cb\3\2\2\2&\u00ce\3\2\2\2(\u00d1\3\2\2\2*\u00d5\3\2\2\2,\u00da"+
		"\3\2\2\2.\u00e0\3\2\2\2\60\u00e4\3\2\2\2\62\u00ea\3\2\2\2\64\u00ec\3\2"+
		"\2\2\66\u00ee\3\2\2\28\u00f9\3\2\2\2:\u0101\3\2\2\2<\u010c\3\2\2\2>@\5"+
		"\4\3\2?>\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2B\3\3\2\2\2CA\3\2\2\2DN"+
		"\5\"\22\2EN\5\16\b\2FG\5\34\17\2GH\7\3\2\2HN\3\2\2\2IN\5&\24\2JN\5$\23"+
		"\2KN\5(\25\2LN\5*\26\2MD\3\2\2\2ME\3\2\2\2MF\3\2\2\2MI\3\2\2\2MJ\3\2\2"+
		"\2MK\3\2\2\2ML\3\2\2\2N\5\3\2\2\2OP\5\n\6\2PQ\7\b\2\2QR\5\b\5\2RT\3\2"+
		"\2\2SO\3\2\2\2TU\3\2\2\2US\3\2\2\2UV\3\2\2\2V]\3\2\2\2WY\5\b\5\2XW\3\2"+
		"\2\2YZ\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[]\3\2\2\2\\S\3\2\2\2\\X\3\2\2\2]\7"+
		"\3\2\2\2^a\5\"\22\2_a\5\34\17\2`^\3\2\2\2`_\3\2\2\2ab\3\2\2\2b`\3\2\2"+
		"\2bc\3\2\2\2cd\3\2\2\2de\7\3\2\2e\t\3\2\2\2fg\5\34\17\2g\13\3\2\2\2hi"+
		"\5\20\t\2ij\7\b\2\2jk\5\34\17\2km\3\2\2\2lh\3\2\2\2mn\3\2\2\2nl\3\2\2"+
		"\2no\3\2\2\2ox\3\2\2\2pq\5\34\17\2qr\7\3\2\2rt\3\2\2\2sp\3\2\2\2tu\3\2"+
		"\2\2us\3\2\2\2uv\3\2\2\2vx\3\2\2\2wl\3\2\2\2ws\3\2\2\2x\r\3\2\2\2yz\5"+
		"\20\t\2z{\7\3\2\2{\17\3\2\2\2|~\5,\27\2}|\3\2\2\2}~\3\2\2\2~\u0080\3\2"+
		"\2\2\177\u0081\5<\37\2\u0080\177\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0082"+
		"\3\2\2\2\u0082\u0084\5\32\16\2\u0083\u0085\5\22\n\2\u0084\u0083\3\2\2"+
		"\2\u0084\u0085\3\2\2\2\u0085\21\3\2\2\2\u0086\u008f\7\16\2\2\u0087\u008c"+
		"\5\24\13\2\u0088\u0089\7\20\2\2\u0089\u008b\5\24\13\2\u008a\u0088\3\2"+
		"\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d"+
		"\u0090\3\2\2\2\u008e\u008c\3\2\2\2\u008f\u0087\3\2\2\2\u008f\u0090\3\2"+
		"\2\2\u0090\u0091\3\2\2\2\u0091\u0092\7\17\2\2\u0092\23\3\2\2\2\u0093\u0096"+
		"\5\30\r\2\u0094\u0096\5\26\f\2\u0095\u0093\3\2\2\2\u0095\u0094\3\2\2\2"+
		"\u0096\25\3\2\2\2\u0097\u0098\7\4\2\2\u0098\27\3\2\2\2\u0099\u009a\t\2"+
		"\2\2\u009a\31\3\2\2\2\u009b\u009d\7\26\2\2\u009c\u009b\3\2\2\2\u009c\u009d"+
		"\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u00a1\7\7\2\2\u009f\u00a0\7\21\2\2"+
		"\u00a0\u00a2\7\5\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\33"+
		"\3\2\2\2\u00a3\u00a8\5\20\t\2\u00a4\u00a5\7\20\2\2\u00a5\u00a7\5\20\t"+
		"\2\u00a6\u00a4\3\2\2\2\u00a7\u00aa\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9"+
		"\3\2\2\2\u00a9\35\3\2\2\2\u00aa\u00a8\3\2\2\2\u00ab\u00ac\7\7\2\2\u00ac"+
		"\u00b2\7\t\2\2\u00ad\u00af\7\24\2\2\u00ae\u00ad\3\2\2\2\u00ae\u00af\3"+
		"\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b3\7\7\2\2\u00b1\u00b3\5\62\32\2\u00b2"+
		"\u00ae\3\2\2\2\u00b2\u00b1\3\2\2\2\u00b3\37\3\2\2\2\u00b4\u00bd\7\f\2"+
		"\2\u00b5\u00ba\5\36\20\2\u00b6\u00b7\7\20\2\2\u00b7\u00b9\5\36\20\2\u00b8"+
		"\u00b6\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2"+
		"\2\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd\u00b5\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c0\7\r\2\2\u00c0!\3\2\2\2"+
		"\u00c1\u00c2\5\20\t\2\u00c2\u00c3\7\b\2\2\u00c3\u00c5\5\34\17\2\u00c4"+
		"\u00c6\5\60\31\2\u00c5\u00c4\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c7\3"+
		"\2\2\2\u00c7\u00c9\7\3\2\2\u00c8\u00ca\5 \21\2\u00c9\u00c8\3\2\2\2\u00c9"+
		"\u00ca\3\2\2\2\u00ca#\3\2\2\2\u00cb\u00cc\5\32\16\2\u00cc\u00cd\5,\27"+
		"\2\u00cd%\3\2\2\2\u00ce\u00cf\5\32\16\2\u00cf\u00d0\5 \21\2\u00d0\'\3"+
		"\2\2\2\u00d1\u00d2\7\24\2\2\u00d2\u00d3\7\7\2\2\u00d3\u00d4\5 \21\2\u00d4"+
		")\3\2\2\2\u00d5\u00d6\5 \21\2\u00d6+\3\2\2\2\u00d7\u00d8\7\24\2\2\u00d8"+
		"\u00d9\7\7\2\2\u00d9\u00db\7\t\2\2\u00da\u00d7\3\2\2\2\u00da\u00db\3\2"+
		"\2\2\u00db\u00de\3\2\2\2\u00dc\u00df\5.\30\2\u00dd\u00df\5\62\32\2\u00de"+
		"\u00dc\3\2\2\2\u00de\u00dd\3\2\2\2\u00df-\3\2\2\2\u00e0\u00e1\7\n\2\2"+
		"\u00e1\u00e2\5\62\32\2\u00e2\u00e3\7\13\2\2\u00e3/\3\2\2\2\u00e4\u00e5"+
		"\5,\27\2\u00e5\61\3\2\2\2\u00e6\u00eb\5\64\33\2\u00e7\u00eb\5\66\34\2"+
		"\u00e8\u00eb\58\35\2\u00e9\u00eb\5:\36\2\u00ea\u00e6\3\2\2\2\u00ea\u00e7"+
		"\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00e9\3\2\2\2\u00eb\63\3\2\2\2\u00ec"+
		"\u00ed\t\3\2\2\u00ed\65\3\2\2\2\u00ee\u00ef\7\16\2\2\u00ef\u00f4\5\64"+
		"\33\2\u00f0\u00f1\7\20\2\2\u00f1\u00f3\5\64\33\2\u00f2\u00f0\3\2\2\2\u00f3"+
		"\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f7\3\2"+
		"\2\2\u00f6\u00f4\3\2\2\2\u00f7\u00f8\7\17\2\2\u00f8\67\3\2\2\2\u00f9\u00fb"+
		"\7\16\2\2\u00fa\u00fc\5\66\34\2\u00fb\u00fa\3\2\2\2\u00fc\u00fd\3\2\2"+
		"\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0100"+
		"\7\17\2\2\u01009\3\2\2\2\u0101\u0102\7\f\2\2\u0102\u0107\5\64\33\2\u0103"+
		"\u0104\7\20\2\2\u0104\u0106\5\64\33\2\u0105\u0103\3\2\2\2\u0106\u0109"+
		"\3\2\2\2\u0107\u0105\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u010a\3\2\2\2\u0109"+
		"\u0107\3\2\2\2\u010a\u010b\7\r\2\2\u010b;\3\2\2\2\u010c\u010d\7\25\2\2"+
		"\u010d=\3\2\2\2!AMUZ\\`bnuw}\u0080\u0084\u008c\u008f\u0095\u009c\u00a1"+
		"\u00a8\u00ae\u00b2\u00ba\u00bd\u00c5\u00c9\u00da\u00de\u00ea\u00f4\u00fd"+
		"\u0107";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}