// Generated from /home/gusta/googledrive/Github/NeuraLogic/NeuraLogic/Logical/src/parsing/antlr/grammars/Neuralogic.g4 by ANTLR 4.7.2
package cz.cvut.fel.ida.logic.parsing.antlr;
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
		LCURL=8, RCURL=9, LANGLE=10, RANGLE=11, LBRACKET=12, RBRACKET=13, LPAREN=14, 
		RPAREN=15, COMMA=16, SLASH=17, CARET=18, TRUE=19, DOLLAR=20, NEGATION=21, 
		SPECIAL=22, WS=23, COMMENT=24, MULTILINE_COMMENT=25;
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
			"ALPHANUMERIC", "ALPHA", "LCASE_LETTER", "UCASE_LETTER", "DIGIT", "BOL", 
			"WS", "COMMENT", "MULTILINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'.'", null, null, null, null, "':-'", "'='", "'{'", "'}'", "'<'", 
			"'>'", "'['", "']'", "'('", "')'", "','", "'/'", "'^'", "'true'", "'$'", 
			"'~'", "'@'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
			"ASSIGN", "LCURL", "RCURL", "LANGLE", "RANGLE", "LBRACKET", "RBRACKET", 
			"LPAREN", "RPAREN", "COMMA", "SLASH", "CARET", "TRUE", "DOLLAR", "NEGATION", 
			"SPECIAL", "WS", "COMMENT", "MULTILINE_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\33\u00dc\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \3\2\3\2\3\3\3\3\7\3F\n\3\f\3\16\3I\13\3\3\3\3\3\6\3M\n\3\r\3\16\3N\3"+
		"\3\5\3R\n\3\3\4\5\4U\n\4\3\4\6\4X\n\4\r\4\16\4Y\3\5\5\5]\n\5\3\5\6\5`"+
		"\n\5\r\5\16\5a\3\5\3\5\6\5f\n\5\r\5\16\5g\3\5\3\5\5\5l\n\5\3\5\6\5o\n"+
		"\5\r\5\16\5p\5\5s\n\5\3\6\3\6\3\6\7\6x\n\6\f\6\16\6{\13\6\5\6}\n\6\3\7"+
		"\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3"+
		"\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3"+
		"\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\5\30\u00a7\n\30\3\31\3\31"+
		"\3\31\5\31\u00ac\n\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\6\35\u00b5\n"+
		"\35\r\35\16\35\u00b6\3\36\6\36\u00ba\n\36\r\36\16\36\u00bb\3\36\3\36\3"+
		"\37\3\37\7\37\u00c2\n\37\f\37\16\37\u00c5\13\37\3\37\5\37\u00c8\n\37\3"+
		"\37\3\37\3 \3 \3 \3 \3 \7 \u00d1\n \f \16 \u00d4\13 \3 \3 \3 \5 \u00d9"+
		"\n \3 \3 \3\u00d2\2!\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\2\61\2\63\2\65"+
		"\2\67\29\2;\31=\32?\33\3\2\f\4\2--//\4\2GGgg\4\2//aa\3\2c|\3\2C\\\3\2"+
		"\62;\4\2\f\f\16\17\5\2\13\f\17\17\"\"\4\2\f\f\17\17\4\3\f\f\17\17\2\u00ec"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2;\3\2\2"+
		"\2\2=\3\2\2\2\2?\3\2\2\2\3A\3\2\2\2\5Q\3\2\2\2\7T\3\2\2\2\t\\\3\2\2\2"+
		"\13|\3\2\2\2\r~\3\2\2\2\17\u0081\3\2\2\2\21\u0083\3\2\2\2\23\u0085\3\2"+
		"\2\2\25\u0087\3\2\2\2\27\u0089\3\2\2\2\31\u008b\3\2\2\2\33\u008d\3\2\2"+
		"\2\35\u008f\3\2\2\2\37\u0091\3\2\2\2!\u0093\3\2\2\2#\u0095\3\2\2\2%\u0097"+
		"\3\2\2\2\'\u0099\3\2\2\2)\u009e\3\2\2\2+\u00a0\3\2\2\2-\u00a2\3\2\2\2"+
		"/\u00a6\3\2\2\2\61\u00ab\3\2\2\2\63\u00ad\3\2\2\2\65\u00af\3\2\2\2\67"+
		"\u00b1\3\2\2\29\u00b4\3\2\2\2;\u00b9\3\2\2\2=\u00bf\3\2\2\2?\u00cb\3\2"+
		"\2\2AB\7\60\2\2B\4\3\2\2\2CG\5\65\33\2DF\5/\30\2ED\3\2\2\2FI\3\2\2\2G"+
		"E\3\2\2\2GH\3\2\2\2HR\3\2\2\2IG\3\2\2\2JL\7a\2\2KM\5/\30\2LK\3\2\2\2M"+
		"N\3\2\2\2NL\3\2\2\2NO\3\2\2\2OR\3\2\2\2PR\7a\2\2QC\3\2\2\2QJ\3\2\2\2Q"+
		"P\3\2\2\2R\6\3\2\2\2SU\t\2\2\2TS\3\2\2\2TU\3\2\2\2UW\3\2\2\2VX\5\67\34"+
		"\2WV\3\2\2\2XY\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\b\3\2\2\2[]\t\2\2\2\\[\3\2"+
		"\2\2\\]\3\2\2\2]_\3\2\2\2^`\5\67\34\2_^\3\2\2\2`a\3\2\2\2a_\3\2\2\2ab"+
		"\3\2\2\2bc\3\2\2\2ce\7\60\2\2df\5\67\34\2ed\3\2\2\2fg\3\2\2\2ge\3\2\2"+
		"\2gh\3\2\2\2hr\3\2\2\2ik\t\3\2\2jl\t\2\2\2kj\3\2\2\2kl\3\2\2\2ln\3\2\2"+
		"\2mo\5\67\34\2nm\3\2\2\2op\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2ri\3\2"+
		"\2\2rs\3\2\2\2s\n\3\2\2\2t}\5\'\24\2uy\5\63\32\2vx\5/\30\2wv\3\2\2\2x"+
		"{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z}\3\2\2\2{y\3\2\2\2|t\3\2\2\2|u\3\2\2\2"+
		"}\f\3\2\2\2~\177\7<\2\2\177\u0080\7/\2\2\u0080\16\3\2\2\2\u0081\u0082"+
		"\7?\2\2\u0082\20\3\2\2\2\u0083\u0084\7}\2\2\u0084\22\3\2\2\2\u0085\u0086"+
		"\7\177\2\2\u0086\24\3\2\2\2\u0087\u0088\7>\2\2\u0088\26\3\2\2\2\u0089"+
		"\u008a\7@\2\2\u008a\30\3\2\2\2\u008b\u008c\7]\2\2\u008c\32\3\2\2\2\u008d"+
		"\u008e\7_\2\2\u008e\34\3\2\2\2\u008f\u0090\7*\2\2\u0090\36\3\2\2\2\u0091"+
		"\u0092\7+\2\2\u0092 \3\2\2\2\u0093\u0094\7.\2\2\u0094\"\3\2\2\2\u0095"+
		"\u0096\7\61\2\2\u0096$\3\2\2\2\u0097\u0098\7`\2\2\u0098&\3\2\2\2\u0099"+
		"\u009a\7v\2\2\u009a\u009b\7t\2\2\u009b\u009c\7w\2\2\u009c\u009d\7g\2\2"+
		"\u009d(\3\2\2\2\u009e\u009f\7&\2\2\u009f*\3\2\2\2\u00a0\u00a1\7\u0080"+
		"\2\2\u00a1,\3\2\2\2\u00a2\u00a3\7B\2\2\u00a3.\3\2\2\2\u00a4\u00a7\5\61"+
		"\31\2\u00a5\u00a7\5\67\34\2\u00a6\u00a4\3\2\2\2\u00a6\u00a5\3\2\2\2\u00a7"+
		"\60\3\2\2\2\u00a8\u00ac\t\4\2\2\u00a9\u00ac\5\63\32\2\u00aa\u00ac\5\65"+
		"\33\2\u00ab\u00a8\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00aa\3\2\2\2\u00ac"+
		"\62\3\2\2\2\u00ad\u00ae\t\5\2\2\u00ae\64\3\2\2\2\u00af\u00b0\t\6\2\2\u00b0"+
		"\66\3\2\2\2\u00b1\u00b2\t\7\2\2\u00b28\3\2\2\2\u00b3\u00b5\t\b\2\2\u00b4"+
		"\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2"+
		"\2\2\u00b7:\3\2\2\2\u00b8\u00ba\t\t\2\2\u00b9\u00b8\3\2\2\2\u00ba\u00bb"+
		"\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd"+
		"\u00be\b\36\2\2\u00be<\3\2\2\2\u00bf\u00c3\7\'\2\2\u00c0\u00c2\n\n\2\2"+
		"\u00c1\u00c0\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4"+
		"\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c8\t\13\2\2"+
		"\u00c7\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00ca\b\37\3\2\u00ca>\3"+
		"\2\2\2\u00cb\u00cc\7\61\2\2\u00cc\u00cd\7,\2\2\u00cd\u00d2\3\2\2\2\u00ce"+
		"\u00d1\5? \2\u00cf\u00d1\13\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00cf\3\2"+
		"\2\2\u00d1\u00d4\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3"+
		"\u00d8\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d5\u00d6\7,\2\2\u00d6\u00d9\7\61"+
		"\2\2\u00d7\u00d9\7\2\2\3\u00d8\u00d5\3\2\2\2\u00d8\u00d7\3\2\2\2\u00d9"+
		"\u00da\3\2\2\2\u00da\u00db\b \3\2\u00db@\3\2\2\2\31\2GNQTY\\agkpry|\u00a6"+
		"\u00ab\u00b6\u00bb\u00c3\u00c7\u00d0\u00d2\u00d8\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}