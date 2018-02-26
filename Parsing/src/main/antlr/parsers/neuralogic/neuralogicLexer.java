// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/neuralogic.g4 by ANTLR 4.7
package parsers.neuralogic;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class neuralogicLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, VARIABLE=4, INT=5, FLOAT=6, ATOMIC_NAME=7, IMPLIED_BY=8, 
		LANGLE=9, RANGLE=10, LBRACKET=11, RBRACKET=12, LPAREN=13, RPAREN=14, COMMA=15, 
		SLASH=16, CARET=17, TRUE=18, DOLLAR=19, WS=20, COMMENT=21, MULTILINE_COMMENT=22;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
		"LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", 
		"SLASH", "CARET", "TRUE", "DOLLAR", "ALPHANUMERIC", "ALPHA", "LCASE_LETTER", 
		"UCASE_LETTER", "DIGIT", "BOL", "WS", "COMMENT", "MULTILINE_COMMENT"
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


	public neuralogicLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "neuralogic.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\30\u00cd\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\7\5D\n\5\f\5\16\5G\13\5\3\5\3\5\6\5K\n\5\r\5\16\5L\3\5\5\5P\n"+
		"\5\3\6\5\6S\n\6\3\6\6\6V\n\6\r\6\16\6W\3\7\5\7[\n\7\3\7\6\7^\n\7\r\7\16"+
		"\7_\3\7\3\7\6\7d\n\7\r\7\16\7e\3\7\3\7\5\7j\n\7\3\7\6\7m\n\7\r\7\16\7"+
		"n\5\7q\n\7\3\b\3\b\7\bu\n\b\f\b\16\bx\13\b\3\t\3\t\3\t\3\n\3\n\3\13\3"+
		"\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\5\25\u0098\n\25\3\26\3\26"+
		"\3\26\5\26\u009d\n\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\6\32\u00a6\n"+
		"\32\r\32\16\32\u00a7\3\33\6\33\u00ab\n\33\r\33\16\33\u00ac\3\33\3\33\3"+
		"\34\3\34\7\34\u00b3\n\34\f\34\16\34\u00b6\13\34\3\34\5\34\u00b9\n\34\3"+
		"\34\3\34\3\35\3\35\3\35\3\35\3\35\7\35\u00c2\n\35\f\35\16\35\u00c5\13"+
		"\35\3\35\3\35\3\35\5\35\u00ca\n\35\3\35\3\35\3\u00c3\2\36\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\2+\2-\2/\2\61\2\63\2\65\26\67\279\30\3\2\f\4\2--//\4\2GGgg"+
		"\4\2//aa\3\2c|\3\2C\\\3\2\62;\4\2\f\f\16\17\5\2\13\f\17\17\"\"\4\2\f\f"+
		"\17\17\4\3\f\f\17\17\2\u00dc\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\3;\3\2\2\2\5=\3\2\2\2\7?\3\2\2\2\tO\3\2\2\2\13R\3"+
		"\2\2\2\rZ\3\2\2\2\17r\3\2\2\2\21y\3\2\2\2\23|\3\2\2\2\25~\3\2\2\2\27\u0080"+
		"\3\2\2\2\31\u0082\3\2\2\2\33\u0084\3\2\2\2\35\u0086\3\2\2\2\37\u0088\3"+
		"\2\2\2!\u008a\3\2\2\2#\u008c\3\2\2\2%\u008e\3\2\2\2\'\u0093\3\2\2\2)\u0097"+
		"\3\2\2\2+\u009c\3\2\2\2-\u009e\3\2\2\2/\u00a0\3\2\2\2\61\u00a2\3\2\2\2"+
		"\63\u00a5\3\2\2\2\65\u00aa\3\2\2\2\67\u00b0\3\2\2\29\u00bc\3\2\2\2;<\7"+
		"\60\2\2<\4\3\2\2\2=>\7B\2\2>\6\3\2\2\2?@\7?\2\2@\b\3\2\2\2AE\5/\30\2B"+
		"D\5)\25\2CB\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2FP\3\2\2\2GE\3\2\2\2"+
		"HJ\7a\2\2IK\5)\25\2JI\3\2\2\2KL\3\2\2\2LJ\3\2\2\2LM\3\2\2\2MP\3\2\2\2"+
		"NP\7a\2\2OA\3\2\2\2OH\3\2\2\2ON\3\2\2\2P\n\3\2\2\2QS\t\2\2\2RQ\3\2\2\2"+
		"RS\3\2\2\2SU\3\2\2\2TV\5\61\31\2UT\3\2\2\2VW\3\2\2\2WU\3\2\2\2WX\3\2\2"+
		"\2X\f\3\2\2\2Y[\t\2\2\2ZY\3\2\2\2Z[\3\2\2\2[]\3\2\2\2\\^\5\61\31\2]\\"+
		"\3\2\2\2^_\3\2\2\2_]\3\2\2\2_`\3\2\2\2`a\3\2\2\2ac\7\60\2\2bd\5\61\31"+
		"\2cb\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2fp\3\2\2\2gi\t\3\2\2hj\t\2\2"+
		"\2ih\3\2\2\2ij\3\2\2\2jl\3\2\2\2km\5\61\31\2lk\3\2\2\2mn\3\2\2\2nl\3\2"+
		"\2\2no\3\2\2\2oq\3\2\2\2pg\3\2\2\2pq\3\2\2\2q\16\3\2\2\2rv\5-\27\2su\5"+
		")\25\2ts\3\2\2\2ux\3\2\2\2vt\3\2\2\2vw\3\2\2\2w\20\3\2\2\2xv\3\2\2\2y"+
		"z\7<\2\2z{\7/\2\2{\22\3\2\2\2|}\7>\2\2}\24\3\2\2\2~\177\7@\2\2\177\26"+
		"\3\2\2\2\u0080\u0081\7]\2\2\u0081\30\3\2\2\2\u0082\u0083\7_\2\2\u0083"+
		"\32\3\2\2\2\u0084\u0085\7*\2\2\u0085\34\3\2\2\2\u0086\u0087\7+\2\2\u0087"+
		"\36\3\2\2\2\u0088\u0089\7.\2\2\u0089 \3\2\2\2\u008a\u008b\7\61\2\2\u008b"+
		"\"\3\2\2\2\u008c\u008d\7`\2\2\u008d$\3\2\2\2\u008e\u008f\7v\2\2\u008f"+
		"\u0090\7t\2\2\u0090\u0091\7w\2\2\u0091\u0092\7g\2\2\u0092&\3\2\2\2\u0093"+
		"\u0094\7&\2\2\u0094(\3\2\2\2\u0095\u0098\5+\26\2\u0096\u0098\5\61\31\2"+
		"\u0097\u0095\3\2\2\2\u0097\u0096\3\2\2\2\u0098*\3\2\2\2\u0099\u009d\t"+
		"\4\2\2\u009a\u009d\5-\27\2\u009b\u009d\5/\30\2\u009c\u0099\3\2\2\2\u009c"+
		"\u009a\3\2\2\2\u009c\u009b\3\2\2\2\u009d,\3\2\2\2\u009e\u009f\t\5\2\2"+
		"\u009f.\3\2\2\2\u00a0\u00a1\t\6\2\2\u00a1\60\3\2\2\2\u00a2\u00a3\t\7\2"+
		"\2\u00a3\62\3\2\2\2\u00a4\u00a6\t\b\2\2\u00a5\u00a4\3\2\2\2\u00a6\u00a7"+
		"\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\64\3\2\2\2\u00a9"+
		"\u00ab\t\t\2\2\u00aa\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00aa\3\2"+
		"\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00af\b\33\2\2\u00af"+
		"\66\3\2\2\2\u00b0\u00b4\7\'\2\2\u00b1\u00b3\n\n\2\2\u00b2\u00b1\3\2\2"+
		"\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b8"+
		"\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7\u00b9\t\13\2\2\u00b8\u00b7\3\2\2\2"+
		"\u00b9\u00ba\3\2\2\2\u00ba\u00bb\b\34\3\2\u00bb8\3\2\2\2\u00bc\u00bd\7"+
		"\61\2\2\u00bd\u00be\7,\2\2\u00be\u00c3\3\2\2\2\u00bf\u00c2\59\35\2\u00c0"+
		"\u00c2\13\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c5\3"+
		"\2\2\2\u00c3\u00c4\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c4\u00c9\3\2\2\2\u00c5"+
		"\u00c3\3\2\2\2\u00c6\u00c7\7,\2\2\u00c7\u00ca\7\61\2\2\u00c8\u00ca\7\2"+
		"\2\3\u00c9\u00c6\3\2\2\2\u00c9\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb"+
		"\u00cc\b\35\3\2\u00cc:\3\2\2\2\30\2ELORWZ_einpv\u0097\u009c\u00a7\u00ac"+
		"\u00b4\u00b8\u00c1\u00c3\u00c9\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}