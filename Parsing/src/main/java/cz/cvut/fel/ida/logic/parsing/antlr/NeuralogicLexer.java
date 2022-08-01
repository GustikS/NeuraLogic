// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/java/cz/cvut/fel/ida/logic/parsing/antlr/grammars/Neuralogic.g4 by ANTLR 4.8
package cz.cvut.fel.ida.logic.parsing.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NeuralogicLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, VARIABLE=3, INT=4, FLOAT=5, ATOMIC_NAME=6, IMPLIED_BY=7, 
		IMPLIED_BY2=8, ASSIGN=9, LCURL=10, RCURL=11, LANGLE=12, RANGLE=13, LBRACKET=14, 
		RBRACKET=15, LPAREN=16, RPAREN=17, COMMA=18, SLASH=19, CARET=20, TRUE=21, 
		DOLLAR=22, NEGATION=23, SOFTNEGATION=24, SPECIAL=25, PRIVATE=26, PIPE=27, 
		WS=28, COMMENT=29, MULTILINE_COMMENT=30;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
			"IMPLIED_BY2", "ASSIGN", "LCURL", "RCURL", "LANGLE", "RANGLE", "LBRACKET", 
			"RBRACKET", "LPAREN", "RPAREN", "COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", 
			"NEGATION", "SOFTNEGATION", "SPECIAL", "PRIVATE", "PIPE", "ALPHANUMERIC", 
			"ALPHA", "LCASE_LETTER", "UCASE_LETTER", "DIGIT", "BOL", "WS", "COMMENT", 
			"MULTILINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'.'", "':'", null, null, null, null, "':-'", "'<='", "'='", "'{'", 
			"'}'", "'<'", "'>'", "'['", "']'", "'('", "')'", "','", "'/'", "'^'", 
			"'true'", "'$'", "'!'", "'~'", "'@'", "'*'", "'|'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
			"IMPLIED_BY2", "ASSIGN", "LCURL", "RCURL", "LANGLE", "RANGLE", "LBRACKET", 
			"RBRACKET", "LPAREN", "RPAREN", "COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", 
			"NEGATION", "SOFTNEGATION", "SPECIAL", "PRIVATE", "PIPE", "WS", "COMMENT", 
			"MULTILINE_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2 \u00f1\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\3\2\3\2\3\3\3\3\3\4\3\4\7\4R\n\4\f\4\16"+
		"\4U\13\4\3\4\3\4\6\4Y\n\4\r\4\16\4Z\3\4\5\4^\n\4\3\5\5\5a\n\5\3\5\6\5"+
		"d\n\5\r\5\16\5e\3\6\5\6i\n\6\3\6\6\6l\n\6\r\6\16\6m\3\6\3\6\6\6r\n\6\r"+
		"\6\16\6s\3\6\3\6\5\6x\n\6\3\6\6\6{\n\6\r\6\16\6|\5\6\177\n\6\3\7\3\7\3"+
		"\7\7\7\u0084\n\7\f\7\16\7\u0087\13\7\5\7\u0089\n\7\3\b\3\b\3\b\3\t\3\t"+
		"\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3"+
		"\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\26\3"+
		"\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3"+
		"\35\5\35\u00bc\n\35\3\36\3\36\3\36\5\36\u00c1\n\36\3\37\3\37\3 \3 \3!"+
		"\3!\3\"\6\"\u00ca\n\"\r\"\16\"\u00cb\3#\6#\u00cf\n#\r#\16#\u00d0\3#\3"+
		"#\3$\3$\7$\u00d7\n$\f$\16$\u00da\13$\3$\5$\u00dd\n$\3$\3$\3%\3%\3%\3%"+
		"\3%\7%\u00e6\n%\f%\16%\u00e9\13%\3%\3%\3%\5%\u00ee\n%\3%\3%\3\u00e7\2"+
		"&\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\2;\2"+
		"=\2?\2A\2C\2E\36G\37I \3\2\f\4\2--//\4\2GGgg\4\2//aa\3\2c|\3\2C\\\3\2"+
		"\62;\4\2\f\f\16\17\5\2\13\f\17\17\"\"\4\2\f\f\17\17\4\3\f\f\17\17\2\u0101"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2E\3\2\2\2\2G\3"+
		"\2\2\2\2I\3\2\2\2\3K\3\2\2\2\5M\3\2\2\2\7]\3\2\2\2\t`\3\2\2\2\13h\3\2"+
		"\2\2\r\u0088\3\2\2\2\17\u008a\3\2\2\2\21\u008d\3\2\2\2\23\u0090\3\2\2"+
		"\2\25\u0092\3\2\2\2\27\u0094\3\2\2\2\31\u0096\3\2\2\2\33\u0098\3\2\2\2"+
		"\35\u009a\3\2\2\2\37\u009c\3\2\2\2!\u009e\3\2\2\2#\u00a0\3\2\2\2%\u00a2"+
		"\3\2\2\2\'\u00a4\3\2\2\2)\u00a6\3\2\2\2+\u00a8\3\2\2\2-\u00ad\3\2\2\2"+
		"/\u00af\3\2\2\2\61\u00b1\3\2\2\2\63\u00b3\3\2\2\2\65\u00b5\3\2\2\2\67"+
		"\u00b7\3\2\2\29\u00bb\3\2\2\2;\u00c0\3\2\2\2=\u00c2\3\2\2\2?\u00c4\3\2"+
		"\2\2A\u00c6\3\2\2\2C\u00c9\3\2\2\2E\u00ce\3\2\2\2G\u00d4\3\2\2\2I\u00e0"+
		"\3\2\2\2KL\7\60\2\2L\4\3\2\2\2MN\7<\2\2N\6\3\2\2\2OS\5? \2PR\59\35\2Q"+
		"P\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T^\3\2\2\2US\3\2\2\2VX\7a\2\2W"+
		"Y\59\35\2XW\3\2\2\2YZ\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[^\3\2\2\2\\^\7a\2\2"+
		"]O\3\2\2\2]V\3\2\2\2]\\\3\2\2\2^\b\3\2\2\2_a\t\2\2\2`_\3\2\2\2`a\3\2\2"+
		"\2ac\3\2\2\2bd\5A!\2cb\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2f\n\3\2\2"+
		"\2gi\t\2\2\2hg\3\2\2\2hi\3\2\2\2ik\3\2\2\2jl\5A!\2kj\3\2\2\2lm\3\2\2\2"+
		"mk\3\2\2\2mn\3\2\2\2no\3\2\2\2oq\7\60\2\2pr\5A!\2qp\3\2\2\2rs\3\2\2\2"+
		"sq\3\2\2\2st\3\2\2\2t~\3\2\2\2uw\t\3\2\2vx\t\2\2\2wv\3\2\2\2wx\3\2\2\2"+
		"xz\3\2\2\2y{\5A!\2zy\3\2\2\2{|\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\177\3\2\2"+
		"\2~u\3\2\2\2~\177\3\2\2\2\177\f\3\2\2\2\u0080\u0089\5+\26\2\u0081\u0085"+
		"\5=\37\2\u0082\u0084\59\35\2\u0083\u0082\3\2\2\2\u0084\u0087\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0089\3\2\2\2\u0087\u0085\3\2"+
		"\2\2\u0088\u0080\3\2\2\2\u0088\u0081\3\2\2\2\u0089\16\3\2\2\2\u008a\u008b"+
		"\7<\2\2\u008b\u008c\7/\2\2\u008c\20\3\2\2\2\u008d\u008e\7>\2\2\u008e\u008f"+
		"\7?\2\2\u008f\22\3\2\2\2\u0090\u0091\7?\2\2\u0091\24\3\2\2\2\u0092\u0093"+
		"\7}\2\2\u0093\26\3\2\2\2\u0094\u0095\7\177\2\2\u0095\30\3\2\2\2\u0096"+
		"\u0097\7>\2\2\u0097\32\3\2\2\2\u0098\u0099\7@\2\2\u0099\34\3\2\2\2\u009a"+
		"\u009b\7]\2\2\u009b\36\3\2\2\2\u009c\u009d\7_\2\2\u009d \3\2\2\2\u009e"+
		"\u009f\7*\2\2\u009f\"\3\2\2\2\u00a0\u00a1\7+\2\2\u00a1$\3\2\2\2\u00a2"+
		"\u00a3\7.\2\2\u00a3&\3\2\2\2\u00a4\u00a5\7\61\2\2\u00a5(\3\2\2\2\u00a6"+
		"\u00a7\7`\2\2\u00a7*\3\2\2\2\u00a8\u00a9\7v\2\2\u00a9\u00aa\7t\2\2\u00aa"+
		"\u00ab\7w\2\2\u00ab\u00ac\7g\2\2\u00ac,\3\2\2\2\u00ad\u00ae\7&\2\2\u00ae"+
		".\3\2\2\2\u00af\u00b0\7#\2\2\u00b0\60\3\2\2\2\u00b1\u00b2\7\u0080\2\2"+
		"\u00b2\62\3\2\2\2\u00b3\u00b4\7B\2\2\u00b4\64\3\2\2\2\u00b5\u00b6\7,\2"+
		"\2\u00b6\66\3\2\2\2\u00b7\u00b8\7~\2\2\u00b88\3\2\2\2\u00b9\u00bc\5;\36"+
		"\2\u00ba\u00bc\5A!\2\u00bb\u00b9\3\2\2\2\u00bb\u00ba\3\2\2\2\u00bc:\3"+
		"\2\2\2\u00bd\u00c1\t\4\2\2\u00be\u00c1\5=\37\2\u00bf\u00c1\5? \2\u00c0"+
		"\u00bd\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00bf\3\2\2\2\u00c1<\3\2\2\2"+
		"\u00c2\u00c3\t\5\2\2\u00c3>\3\2\2\2\u00c4\u00c5\t\6\2\2\u00c5@\3\2\2\2"+
		"\u00c6\u00c7\t\7\2\2\u00c7B\3\2\2\2\u00c8\u00ca\t\b\2\2\u00c9\u00c8\3"+
		"\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc"+
		"D\3\2\2\2\u00cd\u00cf\t\t\2\2\u00ce\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2"+
		"\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3"+
		"\b#\2\2\u00d3F\3\2\2\2\u00d4\u00d8\7\'\2\2\u00d5\u00d7\n\n\2\2\u00d6\u00d5"+
		"\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9"+
		"\u00dc\3\2\2\2\u00da\u00d8\3\2\2\2\u00db\u00dd\t\13\2\2\u00dc\u00db\3"+
		"\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00df\b$\3\2\u00dfH\3\2\2\2\u00e0\u00e1"+
		"\7\61\2\2\u00e1\u00e2\7,\2\2\u00e2\u00e7\3\2\2\2\u00e3\u00e6\5I%\2\u00e4"+
		"\u00e6\13\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e9\3"+
		"\2\2\2\u00e7\u00e8\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8\u00ed\3\2\2\2\u00e9"+
		"\u00e7\3\2\2\2\u00ea\u00eb\7,\2\2\u00eb\u00ee\7\61\2\2\u00ec\u00ee\7\2"+
		"\2\3\u00ed\u00ea\3\2\2\2\u00ed\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef"+
		"\u00f0\b%\3\2\u00f0J\3\2\2\2\31\2SZ]`ehmsw|~\u0085\u0088\u00bb\u00c0\u00cb"+
		"\u00d0\u00d8\u00dc\u00e5\u00e7\u00ed\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}