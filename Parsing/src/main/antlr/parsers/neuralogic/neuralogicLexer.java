// Generated from /home/gusta/googledrive/Github/NeuraLogic/Parsing/src/main/antlr/neuralogic.g4 by ANTLR 4.7
package parsers.neuralogic;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class neuralogicLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, VARIABLE=4, INT=5, FLOAT=6, ATOMIC_NAME=7, IMPLIED_BY=8, 
		LANGLE=9, RANGLE=10, LBRACKET=11, RBRACKET=12, LPAREN=13, RPAREN=14, COMMA=15, 
		SLASH=16, CARET=17, TRUE=18, ALPHANUMERIC=19, ALPHA=20, WS=21, COMMENT=22, 
		MULTILINE_COMMENT=23;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
		"LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", 
		"SLASH", "CARET", "TRUE", "ALPHANUMERIC", "ALPHA", "LCASE_LETTER", "UCASE_LETTER", 
		"DIGIT", "BOL", "WS", "COMMENT", "MULTILINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'.'", "'@'", "'='", null, null, null, null, "':-'", "'<'", "'>'", 
		"'['", "']'", "'('", "')'", "','", "'/'", "'^'", "'true'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "VARIABLE", "INT", "FLOAT", "ATOMIC_NAME", "IMPLIED_BY", 
		"LANGLE", "RANGLE", "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", 
		"SLASH", "CARET", "TRUE", "ALPHANUMERIC", "ALPHA", "WS", "COMMENT", "MULTILINE_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\31\u00c9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\7"+
		"\5B\n\5\f\5\16\5E\13\5\3\5\3\5\6\5I\n\5\r\5\16\5J\3\5\5\5N\n\5\3\6\5\6"+
		"Q\n\6\3\6\6\6T\n\6\r\6\16\6U\3\7\5\7Y\n\7\3\7\6\7\\\n\7\r\7\16\7]\3\7"+
		"\3\7\6\7b\n\7\r\7\16\7c\3\7\3\7\5\7h\n\7\3\7\6\7k\n\7\r\7\16\7l\5\7o\n"+
		"\7\3\b\3\b\7\bs\n\b\f\b\16\bv\13\b\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3"+
		"\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\5\24\u0094\n\24\3\25\3\25\3\25\5\25\u0099\n"+
		"\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\6\31\u00a2\n\31\r\31\16\31\u00a3"+
		"\3\32\6\32\u00a7\n\32\r\32\16\32\u00a8\3\32\3\32\3\33\3\33\7\33\u00af"+
		"\n\33\f\33\16\33\u00b2\13\33\3\33\5\33\u00b5\n\33\3\33\3\33\3\34\3\34"+
		"\3\34\3\34\3\34\7\34\u00be\n\34\f\34\16\34\u00c1\13\34\3\34\3\34\3\34"+
		"\5\34\u00c6\n\34\3\34\3\34\3\u00bf\2\35\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\2"+
		"-\2/\2\61\2\63\27\65\30\67\31\3\2\f\4\2--//\4\2GGgg\4\2//aa\3\2c|\3\2"+
		"C\\\3\2\62;\4\2\f\f\16\17\5\2\13\f\17\17\"\"\4\2\f\f\17\17\4\3\f\f\17"+
		"\17\2\u00da\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2\63\3\2\2\2\2\65\3\2"+
		"\2\2\2\67\3\2\2\2\39\3\2\2\2\5;\3\2\2\2\7=\3\2\2\2\tM\3\2\2\2\13P\3\2"+
		"\2\2\rX\3\2\2\2\17p\3\2\2\2\21w\3\2\2\2\23z\3\2\2\2\25|\3\2\2\2\27~\3"+
		"\2\2\2\31\u0080\3\2\2\2\33\u0082\3\2\2\2\35\u0084\3\2\2\2\37\u0086\3\2"+
		"\2\2!\u0088\3\2\2\2#\u008a\3\2\2\2%\u008c\3\2\2\2\'\u0093\3\2\2\2)\u0098"+
		"\3\2\2\2+\u009a\3\2\2\2-\u009c\3\2\2\2/\u009e\3\2\2\2\61\u00a1\3\2\2\2"+
		"\63\u00a6\3\2\2\2\65\u00ac\3\2\2\2\67\u00b8\3\2\2\29:\7\60\2\2:\4\3\2"+
		"\2\2;<\7B\2\2<\6\3\2\2\2=>\7?\2\2>\b\3\2\2\2?C\5-\27\2@B\5\'\24\2A@\3"+
		"\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2DN\3\2\2\2EC\3\2\2\2FH\7a\2\2GI\5"+
		"\'\24\2HG\3\2\2\2IJ\3\2\2\2JH\3\2\2\2JK\3\2\2\2KN\3\2\2\2LN\7a\2\2M?\3"+
		"\2\2\2MF\3\2\2\2ML\3\2\2\2N\n\3\2\2\2OQ\t\2\2\2PO\3\2\2\2PQ\3\2\2\2QS"+
		"\3\2\2\2RT\5/\30\2SR\3\2\2\2TU\3\2\2\2US\3\2\2\2UV\3\2\2\2V\f\3\2\2\2"+
		"WY\t\2\2\2XW\3\2\2\2XY\3\2\2\2Y[\3\2\2\2Z\\\5/\30\2[Z\3\2\2\2\\]\3\2\2"+
		"\2][\3\2\2\2]^\3\2\2\2^_\3\2\2\2_a\7\60\2\2`b\5/\30\2a`\3\2\2\2bc\3\2"+
		"\2\2ca\3\2\2\2cd\3\2\2\2dn\3\2\2\2eg\t\3\2\2fh\t\2\2\2gf\3\2\2\2gh\3\2"+
		"\2\2hj\3\2\2\2ik\5/\30\2ji\3\2\2\2kl\3\2\2\2lj\3\2\2\2lm\3\2\2\2mo\3\2"+
		"\2\2ne\3\2\2\2no\3\2\2\2o\16\3\2\2\2pt\5+\26\2qs\5\'\24\2rq\3\2\2\2sv"+
		"\3\2\2\2tr\3\2\2\2tu\3\2\2\2u\20\3\2\2\2vt\3\2\2\2wx\7<\2\2xy\7/\2\2y"+
		"\22\3\2\2\2z{\7>\2\2{\24\3\2\2\2|}\7@\2\2}\26\3\2\2\2~\177\7]\2\2\177"+
		"\30\3\2\2\2\u0080\u0081\7_\2\2\u0081\32\3\2\2\2\u0082\u0083\7*\2\2\u0083"+
		"\34\3\2\2\2\u0084\u0085\7+\2\2\u0085\36\3\2\2\2\u0086\u0087\7.\2\2\u0087"+
		" \3\2\2\2\u0088\u0089\7\61\2\2\u0089\"\3\2\2\2\u008a\u008b\7`\2\2\u008b"+
		"$\3\2\2\2\u008c\u008d\7v\2\2\u008d\u008e\7t\2\2\u008e\u008f\7w\2\2\u008f"+
		"\u0090\7g\2\2\u0090&\3\2\2\2\u0091\u0094\5)\25\2\u0092\u0094\5/\30\2\u0093"+
		"\u0091\3\2\2\2\u0093\u0092\3\2\2\2\u0094(\3\2\2\2\u0095\u0099\t\4\2\2"+
		"\u0096\u0099\5+\26\2\u0097\u0099\5-\27\2\u0098\u0095\3\2\2\2\u0098\u0096"+
		"\3\2\2\2\u0098\u0097\3\2\2\2\u0099*\3\2\2\2\u009a\u009b\t\5\2\2\u009b"+
		",\3\2\2\2\u009c\u009d\t\6\2\2\u009d.\3\2\2\2\u009e\u009f\t\7\2\2\u009f"+
		"\60\3\2\2\2\u00a0\u00a2\t\b\2\2\u00a1\u00a0\3\2\2\2\u00a2\u00a3\3\2\2"+
		"\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\62\3\2\2\2\u00a5\u00a7"+
		"\t\t\2\2\u00a6\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8"+
		"\u00a9\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\b\32\2\2\u00ab\64\3\2\2"+
		"\2\u00ac\u00b0\7\'\2\2\u00ad\u00af\n\n\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b2"+
		"\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2"+
		"\u00b0\3\2\2\2\u00b3\u00b5\t\13\2\2\u00b4\u00b3\3\2\2\2\u00b5\u00b6\3"+
		"\2\2\2\u00b6\u00b7\b\33\3\2\u00b7\66\3\2\2\2\u00b8\u00b9\7\61\2\2\u00b9"+
		"\u00ba\7,\2\2\u00ba\u00bf\3\2\2\2\u00bb\u00be\5\67\34\2\u00bc\u00be\13"+
		"\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00bc\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf"+
		"\u00c0\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c5\3\2\2\2\u00c1\u00bf\3\2"+
		"\2\2\u00c2\u00c3\7,\2\2\u00c3\u00c6\7\61\2\2\u00c4\u00c6\7\2\2\3\u00c5"+
		"\u00c2\3\2\2\2\u00c5\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c8\b\34"+
		"\3\2\u00c88\3\2\2\2\30\2CJMPUX]cglnt\u0093\u0098\u00a3\u00a8\u00b0\u00b4"+
		"\u00bd\u00bf\u00c5\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}