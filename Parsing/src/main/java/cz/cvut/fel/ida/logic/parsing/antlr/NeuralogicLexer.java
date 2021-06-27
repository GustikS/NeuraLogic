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
		ASSIGN=8, LCURL=9, RCURL=10, LANGLE=11, RANGLE=12, LBRACKET=13, RBRACKET=14, 
		LPAREN=15, RPAREN=16, COMMA=17, SLASH=18, CARET=19, TRUE=20, DOLLAR=21, 
		NEGATION=22, SPECIAL=23, PRIVATE=24, PIPE=25, WS=26, COMMENT=27, MULTILINE_COMMENT=28;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
			"ASSIGN", "LCURL", "RCURL", "LANGLE", "RANGLE", "LBRACKET", "RBRACKET", 
			"LPAREN", "RPAREN", "COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", 
			"SPECIAL", "PRIVATE", "PIPE", "ALPHANUMERIC", "ALPHA", "LCASE_LETTER", 
			"UCASE_LETTER", "DIGIT", "BOL", "WS", "COMMENT", "MULTILINE_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\36\u00e8\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\3\2\3\2\3\3\3\3\3\4\3\4\7\4N\n\4\f\4\16\4Q\13\4"+
		"\3\4\3\4\6\4U\n\4\r\4\16\4V\3\4\5\4Z\n\4\3\5\5\5]\n\5\3\5\6\5`\n\5\r\5"+
		"\16\5a\3\6\5\6e\n\6\3\6\6\6h\n\6\r\6\16\6i\3\6\3\6\6\6n\n\6\r\6\16\6o"+
		"\3\6\3\6\5\6t\n\6\3\6\6\6w\n\6\r\6\16\6x\5\6{\n\6\3\7\3\7\3\7\7\7\u0080"+
		"\n\7\f\7\16\7\u0083\13\7\5\7\u0085\n\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\33\3\33\5\33\u00b3\n\33\3\34\3\34\3\34\5\34"+
		"\u00b8\n\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \6 \u00c1\n \r \16 \u00c2"+
		"\3!\6!\u00c6\n!\r!\16!\u00c7\3!\3!\3\"\3\"\7\"\u00ce\n\"\f\"\16\"\u00d1"+
		"\13\"\3\"\5\"\u00d4\n\"\3\"\3\"\3#\3#\3#\3#\3#\7#\u00dd\n#\f#\16#\u00e0"+
		"\13#\3#\3#\3#\5#\u00e5\n#\3#\3#\3\u00de\2$\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+"+
		"\27-\30/\31\61\32\63\33\65\2\67\29\2;\2=\2?\2A\34C\35E\36\3\2\f\4\2--"+
		"//\4\2GGgg\4\2//aa\3\2c|\3\2C\\\3\2\62;\4\2\f\f\16\17\5\2\13\f\17\17\""+
		"\"\4\2\f\f\17\17\4\3\f\f\17\17\2\u00f8\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2A\3"+
		"\2\2\2\2C\3\2\2\2\2E\3\2\2\2\3G\3\2\2\2\5I\3\2\2\2\7Y\3\2\2\2\t\\\3\2"+
		"\2\2\13d\3\2\2\2\r\u0084\3\2\2\2\17\u0086\3\2\2\2\21\u0089\3\2\2\2\23"+
		"\u008b\3\2\2\2\25\u008d\3\2\2\2\27\u008f\3\2\2\2\31\u0091\3\2\2\2\33\u0093"+
		"\3\2\2\2\35\u0095\3\2\2\2\37\u0097\3\2\2\2!\u0099\3\2\2\2#\u009b\3\2\2"+
		"\2%\u009d\3\2\2\2\'\u009f\3\2\2\2)\u00a1\3\2\2\2+\u00a6\3\2\2\2-\u00a8"+
		"\3\2\2\2/\u00aa\3\2\2\2\61\u00ac\3\2\2\2\63\u00ae\3\2\2\2\65\u00b2\3\2"+
		"\2\2\67\u00b7\3\2\2\29\u00b9\3\2\2\2;\u00bb\3\2\2\2=\u00bd\3\2\2\2?\u00c0"+
		"\3\2\2\2A\u00c5\3\2\2\2C\u00cb\3\2\2\2E\u00d7\3\2\2\2GH\7\60\2\2H\4\3"+
		"\2\2\2IJ\7<\2\2J\6\3\2\2\2KO\5;\36\2LN\5\65\33\2ML\3\2\2\2NQ\3\2\2\2O"+
		"M\3\2\2\2OP\3\2\2\2PZ\3\2\2\2QO\3\2\2\2RT\7a\2\2SU\5\65\33\2TS\3\2\2\2"+
		"UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2WZ\3\2\2\2XZ\7a\2\2YK\3\2\2\2YR\3\2\2\2"+
		"YX\3\2\2\2Z\b\3\2\2\2[]\t\2\2\2\\[\3\2\2\2\\]\3\2\2\2]_\3\2\2\2^`\5=\37"+
		"\2_^\3\2\2\2`a\3\2\2\2a_\3\2\2\2ab\3\2\2\2b\n\3\2\2\2ce\t\2\2\2dc\3\2"+
		"\2\2de\3\2\2\2eg\3\2\2\2fh\5=\37\2gf\3\2\2\2hi\3\2\2\2ig\3\2\2\2ij\3\2"+
		"\2\2jk\3\2\2\2km\7\60\2\2ln\5=\37\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3"+
		"\2\2\2pz\3\2\2\2qs\t\3\2\2rt\t\2\2\2sr\3\2\2\2st\3\2\2\2tv\3\2\2\2uw\5"+
		"=\37\2vu\3\2\2\2wx\3\2\2\2xv\3\2\2\2xy\3\2\2\2y{\3\2\2\2zq\3\2\2\2z{\3"+
		"\2\2\2{\f\3\2\2\2|\u0085\5)\25\2}\u0081\59\35\2~\u0080\5\65\33\2\177~"+
		"\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082"+
		"\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0084|\3\2\2\2\u0084}\3\2\2\2\u0085"+
		"\16\3\2\2\2\u0086\u0087\7<\2\2\u0087\u0088\7/\2\2\u0088\20\3\2\2\2\u0089"+
		"\u008a\7?\2\2\u008a\22\3\2\2\2\u008b\u008c\7}\2\2\u008c\24\3\2\2\2\u008d"+
		"\u008e\7\177\2\2\u008e\26\3\2\2\2\u008f\u0090\7>\2\2\u0090\30\3\2\2\2"+
		"\u0091\u0092\7@\2\2\u0092\32\3\2\2\2\u0093\u0094\7]\2\2\u0094\34\3\2\2"+
		"\2\u0095\u0096\7_\2\2\u0096\36\3\2\2\2\u0097\u0098\7*\2\2\u0098 \3\2\2"+
		"\2\u0099\u009a\7+\2\2\u009a\"\3\2\2\2\u009b\u009c\7.\2\2\u009c$\3\2\2"+
		"\2\u009d\u009e\7\61\2\2\u009e&\3\2\2\2\u009f\u00a0\7`\2\2\u00a0(\3\2\2"+
		"\2\u00a1\u00a2\7v\2\2\u00a2\u00a3\7t\2\2\u00a3\u00a4\7w\2\2\u00a4\u00a5"+
		"\7g\2\2\u00a5*\3\2\2\2\u00a6\u00a7\7&\2\2\u00a7,\3\2\2\2\u00a8\u00a9\7"+
		"\u0080\2\2\u00a9.\3\2\2\2\u00aa\u00ab\7B\2\2\u00ab\60\3\2\2\2\u00ac\u00ad"+
		"\7,\2\2\u00ad\62\3\2\2\2\u00ae\u00af\7~\2\2\u00af\64\3\2\2\2\u00b0\u00b3"+
		"\5\67\34\2\u00b1\u00b3\5=\37\2\u00b2\u00b0\3\2\2\2\u00b2\u00b1\3\2\2\2"+
		"\u00b3\66\3\2\2\2\u00b4\u00b8\t\4\2\2\u00b5\u00b8\59\35\2\u00b6\u00b8"+
		"\5;\36\2\u00b7\u00b4\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b6\3\2\2\2\u00b8"+
		"8\3\2\2\2\u00b9\u00ba\t\5\2\2\u00ba:\3\2\2\2\u00bb\u00bc\t\6\2\2\u00bc"+
		"<\3\2\2\2\u00bd\u00be\t\7\2\2\u00be>\3\2\2\2\u00bf\u00c1\t\b\2\2\u00c0"+
		"\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2"+
		"\2\2\u00c3@\3\2\2\2\u00c4\u00c6\t\t\2\2\u00c5\u00c4\3\2\2\2\u00c6\u00c7"+
		"\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"\u00ca\b!\2\2\u00caB\3\2\2\2\u00cb\u00cf\7\'\2\2\u00cc\u00ce\n\n\2\2\u00cd"+
		"\u00cc\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2"+
		"\2\2\u00d0\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d2\u00d4\t\13\2\2\u00d3"+
		"\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d6\b\"\3\2\u00d6D\3\2\2\2"+
		"\u00d7\u00d8\7\61\2\2\u00d8\u00d9\7,\2\2\u00d9\u00de\3\2\2\2\u00da\u00dd"+
		"\5E#\2\u00db\u00dd\13\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd"+
		"\u00e0\3\2\2\2\u00de\u00df\3\2\2\2\u00de\u00dc\3\2\2\2\u00df\u00e4\3\2"+
		"\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e2\7,\2\2\u00e2\u00e5\7\61\2\2\u00e3"+
		"\u00e5\7\2\2\3\u00e4\u00e1\3\2\2\2\u00e4\u00e3\3\2\2\2\u00e5\u00e6\3\2"+
		"\2\2\u00e6\u00e7\b#\3\2\u00e7F\3\2\2\2\31\2OVY\\adiosxz\u0081\u0084\u00b2"+
		"\u00b7\u00c2\u00c7\u00cf\u00d3\u00dc\u00de\u00e4\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}