// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/java/cz/cvut/fel/ida/logic/parsing/antlr/grammars/Neuralogic.g4 by ANTLR 4.8
package cz.cvut.fel.ida.logic.parsing.antlr;
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
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, VARIABLE=3, INT=4, FLOAT=5, ATOMIC_NAME=6, IMPLIED_BY=7, 
		ASSIGN=8, LCURL=9, RCURL=10, LANGLE=11, RANGLE=12, LBRACKET=13, RBRACKET=14, 
		LPAREN=15, RPAREN=16, COMMA=17, SLASH=18, CARET=19, TRUE=20, DOLLAR=21, 
		NEGATION=22, SPECIAL=23, PRIVATE=24, PIPE=25, WS=26, COMMENT=27, MULTILINE_COMMENT=28;
	public static final int
		RULE_templateFile = 0, RULE_templateLine = 1, RULE_examplesFile = 2, RULE_liftedExample = 3, 
		RULE_label = 4, RULE_queriesFile = 5, RULE_fact = 6, RULE_atom = 7, RULE_termList = 8, 
		RULE_term = 9, RULE_variable = 10, RULE_constant = 11, RULE_predicate = 12, 
		RULE_conjunction = 13, RULE_metadataVal = 14, RULE_metadataList = 15, 
		RULE_lrnnRule = 16, RULE_predicateOffset = 17, RULE_predicateMetadata = 18, 
		RULE_weightMetadata = 19, RULE_templateMetadata = 20, RULE_weight = 21, 
		RULE_fixedValue = 22, RULE_offset = 23, RULE_value = 24, RULE_number = 25, 
		RULE_vector = 26, RULE_sparseVector = 27, RULE_matrix = 28, RULE_sparseMatrix = 29, 
		RULE_dimensions = 30, RULE_element = 31, RULE_element2d = 32, RULE_negation = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"templateFile", "templateLine", "examplesFile", "liftedExample", "label", 
			"queriesFile", "fact", "atom", "termList", "term", "variable", "constant", 
			"predicate", "conjunction", "metadataVal", "metadataList", "lrnnRule", 
			"predicateOffset", "predicateMetadata", "weightMetadata", "templateMetadata", 
			"weight", "fixedValue", "offset", "value", "number", "vector", "sparseVector", 
			"matrix", "sparseMatrix", "dimensions", "element", "element2d", "negation"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'.'", "':'", null, null, null, null, "':-'", "'='", "'{'", "'}'", 
			"'<'", "'>'", "'['", "']'", "'('", "')'", "','", "'/'", "'^'", "'true'", 
			"'$'", "'~'", "'@'", "'*'", "'|'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
			"ASSIGN", "LCURL", "RCURL", "LANGLE", "RANGLE", "LBRACKET", "RBRACKET", 
			"LPAREN", "RPAREN", "COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", 
			"SPECIAL", "PRIVATE", "PIPE", "WS", "COMMENT", "MULTILINE_COMMENT"
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
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LCURL) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL) | (1L << PRIVATE))) != 0)) {
				{
				{
				setState(68);
				templateLine();
				}
				}
				setState(73);
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
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				lrnnRule();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
				fact();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(76);
				conjunction();
				setState(77);
				match(T__0);
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(79);
				predicateMetadata();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(80);
				predicateOffset();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(81);
				weightMetadata();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(82);
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
			setState(98);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(89); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(85);
					label();
					setState(86);
					match(IMPLIED_BY);
					setState(87);
					liftedExample();
					}
					}
					setState(91); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LCURL) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL) | (1L << PRIVATE))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(94); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(93);
					liftedExample();
					}
					}
					setState(96); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LCURL) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL) | (1L << PRIVATE))) != 0) );
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
			setState(102); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(102);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(100);
					lrnnRule();
					}
					break;
				case 2:
					{
					setState(101);
					conjunction();
					}
					break;
				}
				}
				setState(104); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LCURL) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL) | (1L << PRIVATE))) != 0) );
			setState(106);
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
			setState(108);
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
			setState(126);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(115); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(110);
					atom();
					setState(111);
					match(IMPLIED_BY);
					setState(112);
					conjunction();
					setState(113);
					match(T__0);
					}
					}
					setState(117); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LCURL) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL) | (1L << PRIVATE))) != 0) );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(122); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(119);
					conjunction();
					setState(120);
					match(T__0);
					}
					}
					setState(124); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME) | (1L << LCURL) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR) | (1L << NEGATION) | (1L << SPECIAL) | (1L << PRIVATE))) != 0) );
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
			setState(128);
			atom();
			setState(129);
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
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << FLOAT) | (1L << LCURL) | (1L << LANGLE) | (1L << LBRACKET) | (1L << DOLLAR))) != 0)) {
				{
				setState(131);
				weight();
				}
			}

			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NEGATION) {
				{
				setState(134);
				negation();
				}
			}

			setState(137);
			predicate();
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(138);
				termList();
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
			setState(141);
			match(LPAREN);
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VARIABLE) | (1L << INT) | (1L << FLOAT) | (1L << ATOMIC_NAME))) != 0)) {
				{
				setState(142);
				term();
				setState(147);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(143);
					match(COMMA);
					setState(144);
					term();
					}
					}
					setState(149);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(152);
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
			setState(156);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case ATOMIC_NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				constant();
				}
				break;
			case VARIABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(155);
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
			setState(158);
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
			setState(160);
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
		public TerminalNode PRIVATE() { return getToken(NeuralogicParser.PRIVATE, 0); }
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
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PRIVATE) {
				{
				setState(162);
				match(PRIVATE);
				}
			}

			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SPECIAL) {
				{
				setState(165);
				match(SPECIAL);
				}
			}

			setState(168);
			match(ATOMIC_NAME);
			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLASH) {
				{
				setState(169);
				match(SLASH);
				setState(170);
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
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			atom();
			setState(178);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(174);
					match(COMMA);
					setState(175);
					atom();
					}
					} 
				}
				setState(180);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
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
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
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
			setState(181);
			match(ATOMIC_NAME);
			setState(182);
			match(ASSIGN);
			setState(188);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case FLOAT:
			case LCURL:
			case LBRACKET:
				{
				setState(183);
				value();
				}
				break;
			case ATOMIC_NAME:
			case DOLLAR:
				{
				{
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOLLAR) {
					{
					setState(184);
					match(DOLLAR);
					}
				}

				setState(187);
				match(ATOMIC_NAME);
				}
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
			setState(190);
			match(LBRACKET);
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ATOMIC_NAME) {
				{
				setState(191);
				metadataVal();
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(192);
					match(COMMA);
					setState(193);
					metadataVal();
					}
					}
					setState(198);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(201);
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
		public TerminalNode COMMA() { return getToken(NeuralogicParser.COMMA, 0); }
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
			setState(203);
			atom();
			setState(204);
			match(IMPLIED_BY);
			setState(205);
			conjunction();
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(206);
				match(COMMA);
				setState(207);
				offset();
				}
			}

			setState(210);
			match(T__0);
			setState(212);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(211);
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
			setState(214);
			predicate();
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
			setState(217);
			predicate();
			setState(218);
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
			setState(220);
			match(DOLLAR);
			setState(221);
			match(ATOMIC_NAME);
			setState(222);
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
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterTemplateMetadata(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitTemplateMetadata(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitTemplateMetadata(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TemplateMetadataContext templateMetadata() throws RecognitionException {
		TemplateMetadataContext _localctx = new TemplateMetadataContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_templateMetadata);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
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
		enterRule(_localctx, 42, RULE_weight);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(226);
				match(DOLLAR);
				setState(227);
				match(ATOMIC_NAME);
				setState(228);
				match(ASSIGN);
				}
			}

			setState(233);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LANGLE:
				{
				setState(231);
				fixedValue();
				}
				break;
			case INT:
			case FLOAT:
			case LCURL:
			case LBRACKET:
				{
				setState(232);
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
		enterRule(_localctx, 44, RULE_fixedValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(LANGLE);
			setState(236);
			value();
			setState(237);
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
		enterRule(_localctx, 46, RULE_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
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
		public SparseVectorContext sparseVector() {
			return getRuleContext(SparseVectorContext.class,0);
		}
		public VectorContext vector() {
			return getRuleContext(VectorContext.class,0);
		}
		public SparseMatrixContext sparseMatrix() {
			return getRuleContext(SparseMatrixContext.class,0);
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
		enterRule(_localctx, 48, RULE_value);
		try {
			setState(247);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(241);
				number();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(242);
				sparseVector();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(243);
				vector();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(244);
				sparseMatrix();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(245);
				matrix();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(246);
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
		enterRule(_localctx, 50, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
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
		public TerminalNode LBRACKET() { return getToken(NeuralogicParser.LBRACKET, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public TerminalNode RBRACKET() { return getToken(NeuralogicParser.RBRACKET, 0); }
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
		enterRule(_localctx, 52, RULE_vector);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(LBRACKET);
			setState(252);
			number();
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(253);
				match(COMMA);
				setState(254);
				number();
				}
				}
				setState(259);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(260);
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

	public static class SparseVectorContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(NeuralogicParser.LBRACKET, 0); }
		public TerminalNode INT() { return getToken(NeuralogicParser.INT, 0); }
		public TerminalNode PIPE() { return getToken(NeuralogicParser.PIPE, 0); }
		public TerminalNode RBRACKET() { return getToken(NeuralogicParser.RBRACKET, 0); }
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(NeuralogicParser.COMMA, i);
		}
		public SparseVectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sparseVector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterSparseVector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitSparseVector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitSparseVector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SparseVectorContext sparseVector() throws RecognitionException {
		SparseVectorContext _localctx = new SparseVectorContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_sparseVector);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			match(LBRACKET);
			setState(263);
			match(INT);
			setState(264);
			match(PIPE);
			setState(274);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RBRACKET:
				{
				}
				break;
			case INT:
			case LPAREN:
				{
				setState(266);
				element();
				setState(271);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(267);
					match(COMMA);
					setState(268);
					element();
					}
					}
					setState(273);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(276);
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

	public static class MatrixContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(NeuralogicParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(NeuralogicParser.RBRACKET, 0); }
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
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterMatrix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitMatrix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitMatrix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatrixContext matrix() throws RecognitionException {
		MatrixContext _localctx = new MatrixContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_matrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			match(LBRACKET);
			setState(280); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(279);
				vector();
				}
				}
				setState(282); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LBRACKET );
			setState(284);
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

	public static class SparseMatrixContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(NeuralogicParser.LBRACKET, 0); }
		public List<TerminalNode> INT() { return getTokens(NeuralogicParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(NeuralogicParser.INT, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(NeuralogicParser.COMMA, i);
		}
		public TerminalNode PIPE() { return getToken(NeuralogicParser.PIPE, 0); }
		public TerminalNode RBRACKET() { return getToken(NeuralogicParser.RBRACKET, 0); }
		public List<Element2dContext> element2d() {
			return getRuleContexts(Element2dContext.class);
		}
		public Element2dContext element2d(int i) {
			return getRuleContext(Element2dContext.class,i);
		}
		public SparseMatrixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sparseMatrix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterSparseMatrix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitSparseMatrix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitSparseMatrix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SparseMatrixContext sparseMatrix() throws RecognitionException {
		SparseMatrixContext _localctx = new SparseMatrixContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_sparseMatrix);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(286);
			match(LBRACKET);
			setState(287);
			match(INT);
			setState(288);
			match(COMMA);
			setState(289);
			match(INT);
			setState(290);
			match(PIPE);
			setState(300);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RBRACKET:
				{
				}
				break;
			case INT:
			case LPAREN:
				{
				setState(292);
				element2d();
				setState(297);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(293);
					match(COMMA);
					setState(294);
					element2d();
					}
					}
					setState(299);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(302);
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

	public static class DimensionsContext extends ParserRuleContext {
		public TerminalNode LCURL() { return getToken(NeuralogicParser.LCURL, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public TerminalNode RCURL() { return getToken(NeuralogicParser.RCURL, 0); }
		public List<TerminalNode> COMMA() { return getTokens(NeuralogicParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(NeuralogicParser.COMMA, i);
		}
		public DimensionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dimensions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DimensionsContext dimensions() throws RecognitionException {
		DimensionsContext _localctx = new DimensionsContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_dimensions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(LCURL);
			setState(305);
			number();
			setState(310);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(306);
				match(COMMA);
				setState(307);
				number();
				}
				}
				setState(312);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(313);
			match(RCURL);
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

	public static class ElementContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(NeuralogicParser.INT, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(NeuralogicParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(NeuralogicParser.RPAREN, 0); }
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_element);
		try {
			setState(324);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(315);
				match(INT);
				setState(316);
				match(T__1);
				setState(317);
				number();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(318);
				match(LPAREN);
				setState(319);
				match(INT);
				setState(320);
				match(T__1);
				setState(321);
				number();
				setState(322);
				match(RPAREN);
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

	public static class Element2dContext extends ParserRuleContext {
		public List<TerminalNode> INT() { return getTokens(NeuralogicParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(NeuralogicParser.INT, i);
		}
		public TerminalNode COMMA() { return getToken(NeuralogicParser.COMMA, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(NeuralogicParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(NeuralogicParser.RPAREN, 0); }
		public Element2dContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element2d; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).enterElement2d(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NeuralogicListener ) ((NeuralogicListener)listener).exitElement2d(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NeuralogicVisitor ) return ((NeuralogicVisitor<? extends T>)visitor).visitElement2d(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Element2dContext element2d() throws RecognitionException {
		Element2dContext _localctx = new Element2dContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_element2d);
		try {
			setState(339);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(326);
				match(INT);
				setState(327);
				match(COMMA);
				setState(328);
				match(INT);
				setState(329);
				match(T__1);
				setState(330);
				number();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(331);
				match(LPAREN);
				setState(332);
				match(INT);
				setState(333);
				match(COMMA);
				setState(334);
				match(INT);
				setState(335);
				match(T__1);
				setState(336);
				number();
				setState(337);
				match(RPAREN);
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
		enterRule(_localctx, 66, RULE_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(341);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\36\u015a\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\7\2H\n\2\f\2\16\2K\13\2\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\5\3V\n\3\3\4\3\4\3\4\3\4\6\4\\\n\4\r\4\16\4]\3\4\6\4a\n\4"+
		"\r\4\16\4b\5\4e\n\4\3\5\3\5\6\5i\n\5\r\5\16\5j\3\5\3\5\3\6\3\6\3\7\3\7"+
		"\3\7\3\7\3\7\6\7v\n\7\r\7\16\7w\3\7\3\7\3\7\6\7}\n\7\r\7\16\7~\5\7\u0081"+
		"\n\7\3\b\3\b\3\b\3\t\5\t\u0087\n\t\3\t\5\t\u008a\n\t\3\t\3\t\5\t\u008e"+
		"\n\t\3\n\3\n\3\n\3\n\7\n\u0094\n\n\f\n\16\n\u0097\13\n\5\n\u0099\n\n\3"+
		"\n\3\n\3\13\3\13\5\13\u009f\n\13\3\f\3\f\3\r\3\r\3\16\5\16\u00a6\n\16"+
		"\3\16\5\16\u00a9\n\16\3\16\3\16\3\16\5\16\u00ae\n\16\3\17\3\17\3\17\7"+
		"\17\u00b3\n\17\f\17\16\17\u00b6\13\17\3\20\3\20\3\20\3\20\5\20\u00bc\n"+
		"\20\3\20\5\20\u00bf\n\20\3\21\3\21\3\21\3\21\7\21\u00c5\n\21\f\21\16\21"+
		"\u00c8\13\21\5\21\u00ca\n\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\5\22\u00d3"+
		"\n\22\3\22\3\22\5\22\u00d7\n\22\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25"+
		"\3\25\3\25\3\26\3\26\3\27\3\27\3\27\5\27\u00e8\n\27\3\27\3\27\5\27\u00ec"+
		"\n\27\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\5\32"+
		"\u00fa\n\32\3\33\3\33\3\34\3\34\3\34\3\34\7\34\u0102\n\34\f\34\16\34\u0105"+
		"\13\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35\7\35\u0110\n\35\f"+
		"\35\16\35\u0113\13\35\5\35\u0115\n\35\3\35\3\35\3\36\3\36\6\36\u011b\n"+
		"\36\r\36\16\36\u011c\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\7\37\u012a\n\37\f\37\16\37\u012d\13\37\5\37\u012f\n\37\3\37\3\37"+
		"\3 \3 \3 \3 \7 \u0137\n \f \16 \u013a\13 \3 \3 \3!\3!\3!\3!\3!\3!\3!\3"+
		"!\3!\5!\u0147\n!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5"+
		"\"\u0156\n\"\3#\3#\3#\2\2$\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$"+
		"&(*,.\60\62\64\668:<>@BD\2\4\3\2\6\b\3\2\6\7\2\u0166\2I\3\2\2\2\4U\3\2"+
		"\2\2\6d\3\2\2\2\bh\3\2\2\2\nn\3\2\2\2\f\u0080\3\2\2\2\16\u0082\3\2\2\2"+
		"\20\u0086\3\2\2\2\22\u008f\3\2\2\2\24\u009e\3\2\2\2\26\u00a0\3\2\2\2\30"+
		"\u00a2\3\2\2\2\32\u00a5\3\2\2\2\34\u00af\3\2\2\2\36\u00b7\3\2\2\2 \u00c0"+
		"\3\2\2\2\"\u00cd\3\2\2\2$\u00d8\3\2\2\2&\u00db\3\2\2\2(\u00de\3\2\2\2"+
		"*\u00e2\3\2\2\2,\u00e7\3\2\2\2.\u00ed\3\2\2\2\60\u00f1\3\2\2\2\62\u00f9"+
		"\3\2\2\2\64\u00fb\3\2\2\2\66\u00fd\3\2\2\28\u0108\3\2\2\2:\u0118\3\2\2"+
		"\2<\u0120\3\2\2\2>\u0132\3\2\2\2@\u0146\3\2\2\2B\u0155\3\2\2\2D\u0157"+
		"\3\2\2\2FH\5\4\3\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2J\3\3\2\2\2"+
		"KI\3\2\2\2LV\5\"\22\2MV\5\16\b\2NO\5\34\17\2OP\7\3\2\2PV\3\2\2\2QV\5&"+
		"\24\2RV\5$\23\2SV\5(\25\2TV\5*\26\2UL\3\2\2\2UM\3\2\2\2UN\3\2\2\2UQ\3"+
		"\2\2\2UR\3\2\2\2US\3\2\2\2UT\3\2\2\2V\5\3\2\2\2WX\5\n\6\2XY\7\t\2\2YZ"+
		"\5\b\5\2Z\\\3\2\2\2[W\3\2\2\2\\]\3\2\2\2][\3\2\2\2]^\3\2\2\2^e\3\2\2\2"+
		"_a\5\b\5\2`_\3\2\2\2ab\3\2\2\2b`\3\2\2\2bc\3\2\2\2ce\3\2\2\2d[\3\2\2\2"+
		"d`\3\2\2\2e\7\3\2\2\2fi\5\"\22\2gi\5\34\17\2hf\3\2\2\2hg\3\2\2\2ij\3\2"+
		"\2\2jh\3\2\2\2jk\3\2\2\2kl\3\2\2\2lm\7\3\2\2m\t\3\2\2\2no\5\34\17\2o\13"+
		"\3\2\2\2pq\5\20\t\2qr\7\t\2\2rs\5\34\17\2st\7\3\2\2tv\3\2\2\2up\3\2\2"+
		"\2vw\3\2\2\2wu\3\2\2\2wx\3\2\2\2x\u0081\3\2\2\2yz\5\34\17\2z{\7\3\2\2"+
		"{}\3\2\2\2|y\3\2\2\2}~\3\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\u0081\3\2\2"+
		"\2\u0080u\3\2\2\2\u0080|\3\2\2\2\u0081\r\3\2\2\2\u0082\u0083\5\20\t\2"+
		"\u0083\u0084\7\3\2\2\u0084\17\3\2\2\2\u0085\u0087\5,\27\2\u0086\u0085"+
		"\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0089\3\2\2\2\u0088\u008a\5D#\2\u0089"+
		"\u0088\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008d\5\32"+
		"\16\2\u008c\u008e\5\22\n\2\u008d\u008c\3\2\2\2\u008d\u008e\3\2\2\2\u008e"+
		"\21\3\2\2\2\u008f\u0098\7\21\2\2\u0090\u0095\5\24\13\2\u0091\u0092\7\23"+
		"\2\2\u0092\u0094\5\24\13\2\u0093\u0091\3\2\2\2\u0094\u0097\3\2\2\2\u0095"+
		"\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0099\3\2\2\2\u0097\u0095\3\2"+
		"\2\2\u0098\u0090\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009a\3\2\2\2\u009a"+
		"\u009b\7\22\2\2\u009b\23\3\2\2\2\u009c\u009f\5\30\r\2\u009d\u009f\5\26"+
		"\f\2\u009e\u009c\3\2\2\2\u009e\u009d\3\2\2\2\u009f\25\3\2\2\2\u00a0\u00a1"+
		"\7\5\2\2\u00a1\27\3\2\2\2\u00a2\u00a3\t\2\2\2\u00a3\31\3\2\2\2\u00a4\u00a6"+
		"\7\32\2\2\u00a5\u00a4\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a8\3\2\2\2"+
		"\u00a7\u00a9\7\31\2\2\u00a8\u00a7\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00aa"+
		"\3\2\2\2\u00aa\u00ad\7\b\2\2\u00ab\u00ac\7\24\2\2\u00ac\u00ae\7\6\2\2"+
		"\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\33\3\2\2\2\u00af\u00b4"+
		"\5\20\t\2\u00b0\u00b1\7\23\2\2\u00b1\u00b3\5\20\t\2\u00b2\u00b0\3\2\2"+
		"\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\35"+
		"\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7\u00b8\7\b\2\2\u00b8\u00be\7\n\2\2\u00b9"+
		"\u00bf\5\62\32\2\u00ba\u00bc\7\27\2\2\u00bb\u00ba\3\2\2\2\u00bb\u00bc"+
		"\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00bf\7\b\2\2\u00be\u00b9\3\2\2\2\u00be"+
		"\u00bb\3\2\2\2\u00bf\37\3\2\2\2\u00c0\u00c9\7\17\2\2\u00c1\u00c6\5\36"+
		"\20\2\u00c2\u00c3\7\23\2\2\u00c3\u00c5\5\36\20\2\u00c4\u00c2\3\2\2\2\u00c5"+
		"\u00c8\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00ca\3\2"+
		"\2\2\u00c8\u00c6\3\2\2\2\u00c9\u00c1\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca"+
		"\u00cb\3\2\2\2\u00cb\u00cc\7\20\2\2\u00cc!\3\2\2\2\u00cd\u00ce\5\20\t"+
		"\2\u00ce\u00cf\7\t\2\2\u00cf\u00d2\5\34\17\2\u00d0\u00d1\7\23\2\2\u00d1"+
		"\u00d3\5\60\31\2\u00d2\u00d0\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d4\3"+
		"\2\2\2\u00d4\u00d6\7\3\2\2\u00d5\u00d7\5 \21\2\u00d6\u00d5\3\2\2\2\u00d6"+
		"\u00d7\3\2\2\2\u00d7#\3\2\2\2\u00d8\u00d9\5\32\16\2\u00d9\u00da\5,\27"+
		"\2\u00da%\3\2\2\2\u00db\u00dc\5\32\16\2\u00dc\u00dd\5 \21\2\u00dd\'\3"+
		"\2\2\2\u00de\u00df\7\27\2\2\u00df\u00e0\7\b\2\2\u00e0\u00e1\5 \21\2\u00e1"+
		")\3\2\2\2\u00e2\u00e3\5 \21\2\u00e3+\3\2\2\2\u00e4\u00e5\7\27\2\2\u00e5"+
		"\u00e6\7\b\2\2\u00e6\u00e8\7\n\2\2\u00e7\u00e4\3\2\2\2\u00e7\u00e8\3\2"+
		"\2\2\u00e8\u00eb\3\2\2\2\u00e9\u00ec\5.\30\2\u00ea\u00ec\5\62\32\2\u00eb"+
		"\u00e9\3\2\2\2\u00eb\u00ea\3\2\2\2\u00ec-\3\2\2\2\u00ed\u00ee\7\r\2\2"+
		"\u00ee\u00ef\5\62\32\2\u00ef\u00f0\7\16\2\2\u00f0/\3\2\2\2\u00f1\u00f2"+
		"\5,\27\2\u00f2\61\3\2\2\2\u00f3\u00fa\5\64\33\2\u00f4\u00fa\58\35\2\u00f5"+
		"\u00fa\5\66\34\2\u00f6\u00fa\5<\37\2\u00f7\u00fa\5:\36\2\u00f8\u00fa\5"+
		"> \2\u00f9\u00f3\3\2\2\2\u00f9\u00f4\3\2\2\2\u00f9\u00f5\3\2\2\2\u00f9"+
		"\u00f6\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00f8\3\2\2\2\u00fa\63\3\2\2"+
		"\2\u00fb\u00fc\t\3\2\2\u00fc\65\3\2\2\2\u00fd\u00fe\7\17\2\2\u00fe\u0103"+
		"\5\64\33\2\u00ff\u0100\7\23\2\2\u0100\u0102\5\64\33\2\u0101\u00ff\3\2"+
		"\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104"+
		"\u0106\3\2\2\2\u0105\u0103\3\2\2\2\u0106\u0107\7\20\2\2\u0107\67\3\2\2"+
		"\2\u0108\u0109\7\17\2\2\u0109\u010a\7\6\2\2\u010a\u0114\7\33\2\2\u010b"+
		"\u0115\3\2\2\2\u010c\u0111\5@!\2\u010d\u010e\7\23\2\2\u010e\u0110\5@!"+
		"\2\u010f\u010d\3\2\2\2\u0110\u0113\3\2\2\2\u0111\u010f\3\2\2\2\u0111\u0112"+
		"\3\2\2\2\u0112\u0115\3\2\2\2\u0113\u0111\3\2\2\2\u0114\u010b\3\2\2\2\u0114"+
		"\u010c\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0117\7\20\2\2\u01179\3\2\2\2"+
		"\u0118\u011a\7\17\2\2\u0119\u011b\5\66\34\2\u011a\u0119\3\2\2\2\u011b"+
		"\u011c\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011e\3\2"+
		"\2\2\u011e\u011f\7\20\2\2\u011f;\3\2\2\2\u0120\u0121\7\17\2\2\u0121\u0122"+
		"\7\6\2\2\u0122\u0123\7\23\2\2\u0123\u0124\7\6\2\2\u0124\u012e\7\33\2\2"+
		"\u0125\u012f\3\2\2\2\u0126\u012b\5B\"\2\u0127\u0128\7\23\2\2\u0128\u012a"+
		"\5B\"\2\u0129\u0127\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2\2\2\u012b"+
		"\u012c\3\2\2\2\u012c\u012f\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u0125\3\2"+
		"\2\2\u012e\u0126\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0131\7\20\2\2\u0131"+
		"=\3\2\2\2\u0132\u0133\7\13\2\2\u0133\u0138\5\64\33\2\u0134\u0135\7\23"+
		"\2\2\u0135\u0137\5\64\33\2\u0136\u0134\3\2\2\2\u0137\u013a\3\2\2\2\u0138"+
		"\u0136\3\2\2\2\u0138\u0139\3\2\2\2\u0139\u013b\3\2\2\2\u013a\u0138\3\2"+
		"\2\2\u013b\u013c\7\f\2\2\u013c?\3\2\2\2\u013d\u013e\7\6\2\2\u013e\u013f"+
		"\7\4\2\2\u013f\u0147\5\64\33\2\u0140\u0141\7\21\2\2\u0141\u0142\7\6\2"+
		"\2\u0142\u0143\7\4\2\2\u0143\u0144\5\64\33\2\u0144\u0145\7\22\2\2\u0145"+
		"\u0147\3\2\2\2\u0146\u013d\3\2\2\2\u0146\u0140\3\2\2\2\u0147A\3\2\2\2"+
		"\u0148\u0149\7\6\2\2\u0149\u014a\7\23\2\2\u014a\u014b\7\6\2\2\u014b\u014c"+
		"\7\4\2\2\u014c\u0156\5\64\33\2\u014d\u014e\7\21\2\2\u014e\u014f\7\6\2"+
		"\2\u014f\u0150\7\23\2\2\u0150\u0151\7\6\2\2\u0151\u0152\7\4\2\2\u0152"+
		"\u0153\5\64\33\2\u0153\u0154\7\22\2\2\u0154\u0156\3\2\2\2\u0155\u0148"+
		"\3\2\2\2\u0155\u014d\3\2\2\2\u0156C\3\2\2\2\u0157\u0158\7\30\2\2\u0158"+
		"E\3\2\2\2(IU]bdhjw~\u0080\u0086\u0089\u008d\u0095\u0098\u009e\u00a5\u00a8"+
		"\u00ad\u00b4\u00bb\u00be\u00c6\u00c9\u00d2\u00d6\u00e7\u00eb\u00f9\u0103"+
		"\u0111\u0114\u011c\u012b\u012e\u0138\u0146\u0155";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}