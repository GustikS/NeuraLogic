// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/Neuralogic.g4 by ANTLR 4.7.2
package parsers.neuralogic;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NeuralogicLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, VARIABLE=2, INT=3, FLOAT=4, ATOMIC_NAME=5, IMPLIED_BY=6, ASSIGN=7, 
		LANGLE=8, RANGLE=9, LBRACKET=10, RBRACKET=11, LPAREN=12, RPAREN=13, COMMA=14, 
		SLASH=15, CARET=16, TRUE=17, DOLLAR=18, NEGATION=19, SPECIAL=20, WS=21, 
		COMMENT=22, MULTILINE_COMMENT=23;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", "ASSIGN", 
			"LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", 
			"SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", "SPECIAL", "ALPHANUMERIC", 
			"ALPHA", "LCASE_LETTER", "UCASE_LETTER", "DIGIT", "BOL", "WS", "COMMENT", 
			"MULTILINE_COMMENT"
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


	public NeuralogicLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Neuralogic.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\31\u00d4\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\7\3B\n\3\f\3\16\3E\13\3\3\3\3\3\6\3I\n\3\r\3\16\3J\3\3\5\3N\n\3\3\4"+
		"\5\4Q\n\4\3\4\6\4T\n\4\r\4\16\4U\3\5\5\5Y\n\5\3\5\6\5\\\n\5\r\5\16\5]"+
		"\3\5\3\5\6\5b\n\5\r\5\16\5c\3\5\3\5\5\5h\n\5\3\5\6\5k\n\5\r\5\16\5l\5"+
		"\5o\n\5\3\6\3\6\3\6\7\6t\n\6\f\6\16\6w\13\6\5\6y\n\6\3\7\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20"+
		"\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25"+
		"\3\26\3\26\5\26\u009f\n\26\3\27\3\27\3\27\5\27\u00a4\n\27\3\30\3\30\3"+
		"\31\3\31\3\32\3\32\3\33\6\33\u00ad\n\33\r\33\16\33\u00ae\3\34\6\34\u00b2"+
		"\n\34\r\34\16\34\u00b3\3\34\3\34\3\35\3\35\7\35\u00ba\n\35\f\35\16\35"+
		"\u00bd\13\35\3\35\5\35\u00c0\n\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\7"+
		"\36\u00c9\n\36\f\36\16\36\u00cc\13\36\3\36\3\36\3\36\5\36\u00d1\n\36\3"+
		"\36\3\36\3\u00ca\2\37\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\2-\2/\2\61\2\63\2\65"+
		"\2\67\279\30;\31\3\2\f\4\2--//\4\2GGgg\4\2//aa\3\2c|\3\2C\\\3\2\62;\4"+
		"\2\f\f\16\17\5\2\13\f\17\17\"\"\4\2\f\f\17\17\4\3\f\f\17\17\2\u00e4\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2"+
		"\2\3=\3\2\2\2\5M\3\2\2\2\7P\3\2\2\2\tX\3\2\2\2\13x\3\2\2\2\rz\3\2\2\2"+
		"\17}\3\2\2\2\21\177\3\2\2\2\23\u0081\3\2\2\2\25\u0083\3\2\2\2\27\u0085"+
		"\3\2\2\2\31\u0087\3\2\2\2\33\u0089\3\2\2\2\35\u008b\3\2\2\2\37\u008d\3"+
		"\2\2\2!\u008f\3\2\2\2#\u0091\3\2\2\2%\u0096\3\2\2\2\'\u0098\3\2\2\2)\u009a"+
		"\3\2\2\2+\u009e\3\2\2\2-\u00a3\3\2\2\2/\u00a5\3\2\2\2\61\u00a7\3\2\2\2"+
		"\63\u00a9\3\2\2\2\65\u00ac\3\2\2\2\67\u00b1\3\2\2\29\u00b7\3\2\2\2;\u00c3"+
		"\3\2\2\2=>\7\60\2\2>\4\3\2\2\2?C\5\61\31\2@B\5+\26\2A@\3\2\2\2BE\3\2\2"+
		"\2CA\3\2\2\2CD\3\2\2\2DN\3\2\2\2EC\3\2\2\2FH\7a\2\2GI\5+\26\2HG\3\2\2"+
		"\2IJ\3\2\2\2JH\3\2\2\2JK\3\2\2\2KN\3\2\2\2LN\7a\2\2M?\3\2\2\2MF\3\2\2"+
		"\2ML\3\2\2\2N\6\3\2\2\2OQ\t\2\2\2PO\3\2\2\2PQ\3\2\2\2QS\3\2\2\2RT\5\63"+
		"\32\2SR\3\2\2\2TU\3\2\2\2US\3\2\2\2UV\3\2\2\2V\b\3\2\2\2WY\t\2\2\2XW\3"+
		"\2\2\2XY\3\2\2\2Y[\3\2\2\2Z\\\5\63\32\2[Z\3\2\2\2\\]\3\2\2\2][\3\2\2\2"+
		"]^\3\2\2\2^_\3\2\2\2_a\7\60\2\2`b\5\63\32\2a`\3\2\2\2bc\3\2\2\2ca\3\2"+
		"\2\2cd\3\2\2\2dn\3\2\2\2eg\t\3\2\2fh\t\2\2\2gf\3\2\2\2gh\3\2\2\2hj\3\2"+
		"\2\2ik\5\63\32\2ji\3\2\2\2kl\3\2\2\2lj\3\2\2\2lm\3\2\2\2mo\3\2\2\2ne\3"+
		"\2\2\2no\3\2\2\2o\n\3\2\2\2py\5#\22\2qu\5/\30\2rt\5+\26\2sr\3\2\2\2tw"+
		"\3\2\2\2us\3\2\2\2uv\3\2\2\2vy\3\2\2\2wu\3\2\2\2xp\3\2\2\2xq\3\2\2\2y"+
		"\f\3\2\2\2z{\7<\2\2{|\7/\2\2|\16\3\2\2\2}~\7?\2\2~\20\3\2\2\2\177\u0080"+
		"\7>\2\2\u0080\22\3\2\2\2\u0081\u0082\7@\2\2\u0082\24\3\2\2\2\u0083\u0084"+
		"\7]\2\2\u0084\26\3\2\2\2\u0085\u0086\7_\2\2\u0086\30\3\2\2\2\u0087\u0088"+
		"\7*\2\2\u0088\32\3\2\2\2\u0089\u008a\7+\2\2\u008a\34\3\2\2\2\u008b\u008c"+
		"\7.\2\2\u008c\36\3\2\2\2\u008d\u008e\7\61\2\2\u008e \3\2\2\2\u008f\u0090"+
		"\7`\2\2\u0090\"\3\2\2\2\u0091\u0092\7v\2\2\u0092\u0093\7t\2\2\u0093\u0094"+
		"\7w\2\2\u0094\u0095\7g\2\2\u0095$\3\2\2\2\u0096\u0097\7&\2\2\u0097&\3"+
		"\2\2\2\u0098\u0099\7\u0080\2\2\u0099(\3\2\2\2\u009a\u009b\7B\2\2\u009b"+
		"*\3\2\2\2\u009c\u009f\5-\27\2\u009d\u009f\5\63\32\2\u009e\u009c\3\2\2"+
		"\2\u009e\u009d\3\2\2\2\u009f,\3\2\2\2\u00a0\u00a4\t\4\2\2\u00a1\u00a4"+
		"\5/\30\2\u00a2\u00a4\5\61\31\2\u00a3\u00a0\3\2\2\2\u00a3\u00a1\3\2\2\2"+
		"\u00a3\u00a2\3\2\2\2\u00a4.\3\2\2\2\u00a5\u00a6\t\5\2\2\u00a6\60\3\2\2"+
		"\2\u00a7\u00a8\t\6\2\2\u00a8\62\3\2\2\2\u00a9\u00aa\t\7\2\2\u00aa\64\3"+
		"\2\2\2\u00ab\u00ad\t\b\2\2\u00ac\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae"+
		"\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\66\3\2\2\2\u00b0\u00b2\t\t\2"+
		"\2\u00b1\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4"+
		"\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b6\b\34\2\2\u00b68\3\2\2\2\u00b7"+
		"\u00bb\7\'\2\2\u00b8\u00ba\n\n\2\2\u00b9\u00b8\3\2\2\2\u00ba\u00bd\3\2"+
		"\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd"+
		"\u00bb\3\2\2\2\u00be\u00c0\t\13\2\2\u00bf\u00be\3\2\2\2\u00c0\u00c1\3"+
		"\2\2\2\u00c1\u00c2\b\35\3\2\u00c2:\3\2\2\2\u00c3\u00c4\7\61\2\2\u00c4"+
		"\u00c5\7,\2\2\u00c5\u00ca\3\2\2\2\u00c6\u00c9\5;\36\2\u00c7\u00c9\13\2"+
		"\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c7\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca"+
		"\u00cb\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00d0\3\2\2\2\u00cc\u00ca\3\2"+
		"\2\2\u00cd\u00ce\7,\2\2\u00ce\u00d1\7\61\2\2\u00cf\u00d1\7\2\2\3\u00d0"+
		"\u00cd\3\2\2\2\u00d0\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3\b\36"+
		"\3\2\u00d3<\3\2\2\2\31\2CJMPUX]cglnux\u009e\u00a3\u00ae\u00b3\u00bb\u00bf"+
		"\u00c8\u00ca\u00d0\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}