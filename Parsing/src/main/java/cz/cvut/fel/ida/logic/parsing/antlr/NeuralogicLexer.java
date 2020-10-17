// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/java/cz/cvut/fel/ida/logic/parsing/antlr/grammars/Neuralogic.g4 by ANTLR 4.8
package cz.cvut.fel.ida.logic.parsing.antlr;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NeuralogicLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, VARIABLE=2, INT=3, FLOAT=4, ATOMIC_NAME=5, IMPLIED_BY=6, ASSIGN=7, 
		LCURL=8, RCURL=9, LANGLE=10, RANGLE=11, LBRACKET=12, RBRACKET=13, LPAREN=14, 
		RPAREN=15, COMMA=16, SLASH=17, CARET=18, TRUE=19, DOLLAR=20, NEGATION=21, 
		SPECIAL=22, PRIVATE=23, WS=24, COMMENT=25, MULTILINE_COMMENT=26;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", "ASSIGN", 
			"LCURL", "RCURL", "LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", 
			"RPAREN", "COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", "SPECIAL", 
			"PRIVATE", "ALPHANUMERIC", "ALPHA", "LCASE_LETTER", "UCASE_LETTER", "DIGIT", 
			"BOL", "WS", "COMMENT", "MULTILINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'.'", null, null, null, null, "':-'", "'='", "'{'", "'}'", "'<'", 
			"'>'", "'['", "']'", "'('", "')'", "','", "'/'", "'^'", "'true'", "'$'", 
			"'~'", "'@'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
			"ASSIGN", "LCURL", "RCURL", "LANGLE", "RANGLE", "LBRACKET", "RBRACKET", 
			"LPAREN", "RPAREN", "COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", 
			"SPECIAL", "PRIVATE", "WS", "COMMENT", "MULTILINE_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\34\u00e0\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\3\2\3\2\3\3\3\3\7\3H\n\3\f\3\16\3K\13\3\3\3\3\3\6\3O\n\3\r\3\16"+
		"\3P\3\3\5\3T\n\3\3\4\5\4W\n\4\3\4\6\4Z\n\4\r\4\16\4[\3\5\5\5_\n\5\3\5"+
		"\6\5b\n\5\r\5\16\5c\3\5\3\5\6\5h\n\5\r\5\16\5i\3\5\3\5\5\5n\n\5\3\5\6"+
		"\5q\n\5\r\5\16\5r\5\5u\n\5\3\6\3\6\3\6\7\6z\n\6\f\6\16\6}\13\6\5\6\177"+
		"\n\6\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24"+
		"\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\5\31\u00ab"+
		"\n\31\3\32\3\32\3\32\5\32\u00b0\n\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\6\36\u00b9\n\36\r\36\16\36\u00ba\3\37\6\37\u00be\n\37\r\37\16\37\u00bf"+
		"\3\37\3\37\3 \3 \7 \u00c6\n \f \16 \u00c9\13 \3 \5 \u00cc\n \3 \3 \3!"+
		"\3!\3!\3!\3!\7!\u00d5\n!\f!\16!\u00d8\13!\3!\3!\3!\5!\u00dd\n!\3!\3!\3"+
		"\u00d6\2\"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\2\63\2\65\2\67\29"+
		"\2;\2=\32?\33A\34\3\2\f\4\2--//\4\2GGgg\4\2//aa\3\2c|\3\2C\\\3\2\62;\4"+
		"\2\f\f\16\17\5\2\13\f\17\17\"\"\4\2\f\f\17\17\4\3\f\f\17\17\2\u00f0\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2"+
		"\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\3C\3\2\2\2\5S\3\2\2\2\7V\3\2\2\2\t^"+
		"\3\2\2\2\13~\3\2\2\2\r\u0080\3\2\2\2\17\u0083\3\2\2\2\21\u0085\3\2\2\2"+
		"\23\u0087\3\2\2\2\25\u0089\3\2\2\2\27\u008b\3\2\2\2\31\u008d\3\2\2\2\33"+
		"\u008f\3\2\2\2\35\u0091\3\2\2\2\37\u0093\3\2\2\2!\u0095\3\2\2\2#\u0097"+
		"\3\2\2\2%\u0099\3\2\2\2\'\u009b\3\2\2\2)\u00a0\3\2\2\2+\u00a2\3\2\2\2"+
		"-\u00a4\3\2\2\2/\u00a6\3\2\2\2\61\u00aa\3\2\2\2\63\u00af\3\2\2\2\65\u00b1"+
		"\3\2\2\2\67\u00b3\3\2\2\29\u00b5\3\2\2\2;\u00b8\3\2\2\2=\u00bd\3\2\2\2"+
		"?\u00c3\3\2\2\2A\u00cf\3\2\2\2CD\7\60\2\2D\4\3\2\2\2EI\5\67\34\2FH\5\61"+
		"\31\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JT\3\2\2\2KI\3\2\2\2LN\7"+
		"a\2\2MO\5\61\31\2NM\3\2\2\2OP\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QT\3\2\2\2RT"+
		"\7a\2\2SE\3\2\2\2SL\3\2\2\2SR\3\2\2\2T\6\3\2\2\2UW\t\2\2\2VU\3\2\2\2V"+
		"W\3\2\2\2WY\3\2\2\2XZ\59\35\2YX\3\2\2\2Z[\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2"+
		"\\\b\3\2\2\2]_\t\2\2\2^]\3\2\2\2^_\3\2\2\2_a\3\2\2\2`b\59\35\2a`\3\2\2"+
		"\2bc\3\2\2\2ca\3\2\2\2cd\3\2\2\2de\3\2\2\2eg\7\60\2\2fh\59\35\2gf\3\2"+
		"\2\2hi\3\2\2\2ig\3\2\2\2ij\3\2\2\2jt\3\2\2\2km\t\3\2\2ln\t\2\2\2ml\3\2"+
		"\2\2mn\3\2\2\2np\3\2\2\2oq\59\35\2po\3\2\2\2qr\3\2\2\2rp\3\2\2\2rs\3\2"+
		"\2\2su\3\2\2\2tk\3\2\2\2tu\3\2\2\2u\n\3\2\2\2v\177\5\'\24\2w{\5\65\33"+
		"\2xz\5\61\31\2yx\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|\177\3\2\2\2}{"+
		"\3\2\2\2~v\3\2\2\2~w\3\2\2\2\177\f\3\2\2\2\u0080\u0081\7<\2\2\u0081\u0082"+
		"\7/\2\2\u0082\16\3\2\2\2\u0083\u0084\7?\2\2\u0084\20\3\2\2\2\u0085\u0086"+
		"\7}\2\2\u0086\22\3\2\2\2\u0087\u0088\7\177\2\2\u0088\24\3\2\2\2\u0089"+
		"\u008a\7>\2\2\u008a\26\3\2\2\2\u008b\u008c\7@\2\2\u008c\30\3\2\2\2\u008d"+
		"\u008e\7]\2\2\u008e\32\3\2\2\2\u008f\u0090\7_\2\2\u0090\34\3\2\2\2\u0091"+
		"\u0092\7*\2\2\u0092\36\3\2\2\2\u0093\u0094\7+\2\2\u0094 \3\2\2\2\u0095"+
		"\u0096\7.\2\2\u0096\"\3\2\2\2\u0097\u0098\7\61\2\2\u0098$\3\2\2\2\u0099"+
		"\u009a\7`\2\2\u009a&\3\2\2\2\u009b\u009c\7v\2\2\u009c\u009d\7t\2\2\u009d"+
		"\u009e\7w\2\2\u009e\u009f\7g\2\2\u009f(\3\2\2\2\u00a0\u00a1\7&\2\2\u00a1"+
		"*\3\2\2\2\u00a2\u00a3\7\u0080\2\2\u00a3,\3\2\2\2\u00a4\u00a5\7B\2\2\u00a5"+
		".\3\2\2\2\u00a6\u00a7\7,\2\2\u00a7\60\3\2\2\2\u00a8\u00ab\5\63\32\2\u00a9"+
		"\u00ab\59\35\2\u00aa\u00a8\3\2\2\2\u00aa\u00a9\3\2\2\2\u00ab\62\3\2\2"+
		"\2\u00ac\u00b0\t\4\2\2\u00ad\u00b0\5\65\33\2\u00ae\u00b0\5\67\34\2\u00af"+
		"\u00ac\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00ae\3\2\2\2\u00b0\64\3\2\2"+
		"\2\u00b1\u00b2\t\5\2\2\u00b2\66\3\2\2\2\u00b3\u00b4\t\6\2\2\u00b48\3\2"+
		"\2\2\u00b5\u00b6\t\7\2\2\u00b6:\3\2\2\2\u00b7\u00b9\t\b\2\2\u00b8\u00b7"+
		"\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb"+
		"<\3\2\2\2\u00bc\u00be\t\t\2\2\u00bd\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2"+
		"\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c2"+
		"\b\37\2\2\u00c2>\3\2\2\2\u00c3\u00c7\7\'\2\2\u00c4\u00c6\n\n\2\2\u00c5"+
		"\u00c4\3\2\2\2\u00c6\u00c9\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7\u00c8\3\2"+
		"\2\2\u00c8\u00cb\3\2\2\2\u00c9\u00c7\3\2\2\2\u00ca\u00cc\t\13\2\2\u00cb"+
		"\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00ce\b \3\2\u00ce@\3\2\2\2\u00cf"+
		"\u00d0\7\61\2\2\u00d0\u00d1\7,\2\2\u00d1\u00d6\3\2\2\2\u00d2\u00d5\5A"+
		"!\2\u00d3\u00d5\13\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d3\3\2\2\2\u00d5"+
		"\u00d8\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d7\u00dc\3\2"+
		"\2\2\u00d8\u00d6\3\2\2\2\u00d9\u00da\7,\2\2\u00da\u00dd\7\61\2\2\u00db"+
		"\u00dd\7\2\2\3\u00dc\u00d9\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd\u00de\3\2"+
		"\2\2\u00de\u00df\b!\3\2\u00dfB\3\2\2\2\31\2IPSV[^cimrt{~\u00aa\u00af\u00ba"+
		"\u00bf\u00c7\u00cb\u00d4\u00d6\u00dc\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}