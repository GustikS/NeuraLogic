// Generated from /src/main/java/cz/cvut/fel/ida/logic/parsing/antlr/grammars/Neuralogic.g4 by ANTLR 4.13.1
package cz.cvut.fel.ida.logic.parsing.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class NeuralogicParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, VARIABLE = 3, TYPE = 4, INT = 5, FLOAT = 6, ATOMIC_NAME = 7, IMPLIED_BY = 8,
            IMPLIED_BY2 = 9, ASSIGN = 10, LCURL = 11, RCURL = 12, LANGLE = 13, RANGLE = 14, LBRACKET = 15,
            RBRACKET = 16, LPAREN = 17, RPAREN = 18, COMMA = 19, SLASH = 20, CARET = 21, TRUE = 22,
            DOLLAR = 23, NEGATION = 24, SOFTNEGATION = 25, SPECIAL = 26, PRIVATE = 27, PIPE = 28,
            WS = 29, COMMENT = 30, MULTILINE_COMMENT = 31;
    public static final int
            RULE_templateFile = 0, RULE_templateLine = 1, RULE_examplesFile = 2, RULE_liftedExample = 3,
            RULE_label = 4, RULE_queriesFile = 5, RULE_fact = 6, RULE_atom = 7, RULE_termList = 8,
            RULE_term = 9, RULE_variable = 10, RULE_constant = 11, RULE_predicate = 12,
            RULE_conjunction = 13, RULE_metadataVal = 14, RULE_metadataList = 15,
            RULE_lrnnRule = 16, RULE_predicateOffset = 17, RULE_predicateMetadata = 18,
            RULE_weightMetadata = 19, RULE_templateMetadata = 20, RULE_weight = 21,
            RULE_fixedValue = 22, RULE_offset = 23, RULE_value = 24, RULE_number = 25,
            RULE_vector = 26, RULE_sparseVector = 27, RULE_matrix = 28, RULE_sparseMatrix = 29,
            RULE_dimensions = 30, RULE_element = 31, RULE_element2d = 32, RULE_negation = 33,
            RULE_impliedBy = 34;

    private static String[] makeRuleNames() {
        return new String[]{
                "templateFile", "templateLine", "examplesFile", "liftedExample", "label",
                "queriesFile", "fact", "atom", "termList", "term", "variable", "constant",
                "predicate", "conjunction", "metadataVal", "metadataList", "lrnnRule",
                "predicateOffset", "predicateMetadata", "weightMetadata", "templateMetadata",
                "weight", "fixedValue", "offset", "value", "number", "vector", "sparseVector",
                "matrix", "sparseMatrix", "dimensions", "element", "element2d", "negation",
                "impliedBy"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'.'", "':'", null, null, null, null, null, "':-'", "'<='", "'='",
                "'{'", "'}'", "'<'", "'>'", "'['", "']'", "'('", "')'", "','", "'/'",
                "'^'", "'true'", "'$'", "'!'", "'~'", "'@'", "'*'", "'|'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, "VARIABLE", "TYPE", "INT", "FLOAT", "ATOMIC_NAME",
                "IMPLIED_BY", "IMPLIED_BY2", "ASSIGN", "LCURL", "RCURL", "LANGLE", "RANGLE",
                "LBRACKET", "RBRACKET", "LPAREN", "RPAREN", "COMMA", "SLASH", "CARET",
                "TRUE", "DOLLAR", "NEGATION", "SOFTNEGATION", "SPECIAL", "PRIVATE", "PIPE",
                "WS", "COMMENT", "MULTILINE_COMMENT"
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
    public String getGrammarFileName() {
        return "Neuralogic.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public NeuralogicParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TemplateFileContext extends ParserRuleContext {
        public List<TemplateLineContext> templateLine() {
            return getRuleContexts(TemplateLineContext.class);
        }

        public TemplateLineContext templateLine(int i) {
            return getRuleContext(TemplateLineContext.class, i);
        }

        public TemplateFileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_templateFile;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterTemplateFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitTemplateFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitTemplateFile(this);
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
                setState(73);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 260090080L) != 0)) {
                    {
                        {
                            setState(70);
                            templateLine();
                        }
                    }
                    setState(75);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TemplateLineContext extends ParserRuleContext {
        public LrnnRuleContext lrnnRule() {
            return getRuleContext(LrnnRuleContext.class, 0);
        }

        public FactContext fact() {
            return getRuleContext(FactContext.class, 0);
        }

        public ConjunctionContext conjunction() {
            return getRuleContext(ConjunctionContext.class, 0);
        }

        public PredicateMetadataContext predicateMetadata() {
            return getRuleContext(PredicateMetadataContext.class, 0);
        }

        public PredicateOffsetContext predicateOffset() {
            return getRuleContext(PredicateOffsetContext.class, 0);
        }

        public WeightMetadataContext weightMetadata() {
            return getRuleContext(WeightMetadataContext.class, 0);
        }

        public TemplateMetadataContext templateMetadata() {
            return getRuleContext(TemplateMetadataContext.class, 0);
        }

        public TemplateLineContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_templateLine;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterTemplateLine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitTemplateLine(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitTemplateLine(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TemplateLineContext templateLine() throws RecognitionException {
        TemplateLineContext _localctx = new TemplateLineContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_templateLine);
        try {
            setState(85);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(76);
                    lrnnRule();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(77);
                    fact();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    {
                        setState(78);
                        conjunction();
                        setState(79);
                        match(T__0);
                    }
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(81);
                    predicateMetadata();
                }
                break;
                case 5:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(82);
                    predicateOffset();
                }
                break;
                case 6:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(83);
                    weightMetadata();
                }
                break;
                case 7:
                    enterOuterAlt(_localctx, 7);
                {
                    setState(84);
                    templateMetadata();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExamplesFileContext extends ParserRuleContext {
        public List<LabelContext> label() {
            return getRuleContexts(LabelContext.class);
        }

        public LabelContext label(int i) {
            return getRuleContext(LabelContext.class, i);
        }

        public List<ImpliedByContext> impliedBy() {
            return getRuleContexts(ImpliedByContext.class);
        }

        public ImpliedByContext impliedBy(int i) {
            return getRuleContext(ImpliedByContext.class, i);
        }

        public List<LiftedExampleContext> liftedExample() {
            return getRuleContexts(LiftedExampleContext.class);
        }

        public LiftedExampleContext liftedExample(int i) {
            return getRuleContext(LiftedExampleContext.class, i);
        }

        public ExamplesFileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_examplesFile;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterExamplesFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitExamplesFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitExamplesFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExamplesFileContext examplesFile() throws RecognitionException {
        ExamplesFileContext _localctx = new ExamplesFileContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_examplesFile);
        int _la;
        try {
            setState(100);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(91);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(87);
                                label();
                                setState(88);
                                impliedBy();
                                setState(89);
                                liftedExample();
                            }
                        }
                        setState(93);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 260090080L) != 0));
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(96);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(95);
                                liftedExample();
                            }
                        }
                        setState(98);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 260090080L) != 0));
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LiftedExampleContext extends ParserRuleContext {
        public List<LrnnRuleContext> lrnnRule() {
            return getRuleContexts(LrnnRuleContext.class);
        }

        public LrnnRuleContext lrnnRule(int i) {
            return getRuleContext(LrnnRuleContext.class, i);
        }

        public List<ConjunctionContext> conjunction() {
            return getRuleContexts(ConjunctionContext.class);
        }

        public ConjunctionContext conjunction(int i) {
            return getRuleContext(ConjunctionContext.class, i);
        }

        public LiftedExampleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_liftedExample;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterLiftedExample(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitLiftedExample(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitLiftedExample(this);
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
                    setState(104);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            setState(104);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 5, _ctx)) {
                                case 1: {
                                    setState(102);
                                    lrnnRule();
                                }
                                break;
                                case 2: {
                                    setState(103);
                                    conjunction();
                                }
                                break;
                            }
                        }
                        setState(106);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 260090080L) != 0));
                    setState(108);
                    match(T__0);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LabelContext extends ParserRuleContext {
        public ConjunctionContext conjunction() {
            return getRuleContext(ConjunctionContext.class, 0);
        }

        public LabelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_label;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterLabel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitLabel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitLabel(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LabelContext label() throws RecognitionException {
        LabelContext _localctx = new LabelContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_label);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(110);
                conjunction();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class QueriesFileContext extends ParserRuleContext {
        public List<AtomContext> atom() {
            return getRuleContexts(AtomContext.class);
        }

        public AtomContext atom(int i) {
            return getRuleContext(AtomContext.class, i);
        }

        public List<ImpliedByContext> impliedBy() {
            return getRuleContexts(ImpliedByContext.class);
        }

        public ImpliedByContext impliedBy(int i) {
            return getRuleContext(ImpliedByContext.class, i);
        }

        public List<ConjunctionContext> conjunction() {
            return getRuleContexts(ConjunctionContext.class);
        }

        public ConjunctionContext conjunction(int i) {
            return getRuleContext(ConjunctionContext.class, i);
        }

        public QueriesFileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_queriesFile;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterQueriesFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitQueriesFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitQueriesFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final QueriesFileContext queriesFile() throws RecognitionException {
        QueriesFileContext _localctx = new QueriesFileContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_queriesFile);
        int _la;
        try {
            setState(128);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(117);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(112);
                                atom();
                                setState(113);
                                impliedBy();
                                setState(114);
                                conjunction();
                                setState(115);
                                match(T__0);
                            }
                        }
                        setState(119);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 260090080L) != 0));
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(124);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(121);
                                conjunction();
                                setState(122);
                                match(T__0);
                            }
                        }
                        setState(126);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 260090080L) != 0));
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FactContext extends ParserRuleContext {
        public AtomContext atom() {
            return getRuleContext(AtomContext.class, 0);
        }

        public FactContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fact;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterFact(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitFact(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor) return ((NeuralogicVisitor<? extends T>) visitor).visitFact(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FactContext fact() throws RecognitionException {
        FactContext _localctx = new FactContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_fact);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(130);
                atom();
                setState(131);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AtomContext extends ParserRuleContext {
        public PredicateContext predicate() {
            return getRuleContext(PredicateContext.class, 0);
        }

        public WeightContext weight() {
            return getRuleContext(WeightContext.class, 0);
        }

        public NegationContext negation() {
            return getRuleContext(NegationContext.class, 0);
        }

        public TermListContext termList() {
            return getRuleContext(TermListContext.class, 0);
        }

        public AtomContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_atom;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterAtom(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitAtom(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor) return ((NeuralogicVisitor<? extends T>) visitor).visitAtom(this);
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
                setState(134);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8431712L) != 0)) {
                    {
                        setState(133);
                        weight();
                    }
                }

                setState(137);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == NEGATION || _la == SOFTNEGATION) {
                    {
                        setState(136);
                        negation();
                    }
                }

                setState(139);
                predicate();
                setState(141);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == LPAREN) {
                    {
                        setState(140);
                        termList();
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TermListContext extends ParserRuleContext {
        public TerminalNode LPAREN() {
            return getToken(NeuralogicParser.LPAREN, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(NeuralogicParser.RPAREN, 0);
        }

        public List<TermContext> term() {
            return getRuleContexts(TermContext.class);
        }

        public TermContext term(int i) {
            return getRuleContext(TermContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(NeuralogicParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(NeuralogicParser.COMMA, i);
        }

        public TermListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_termList;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterTermList(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitTermList(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitTermList(this);
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
                setState(143);
                match(LPAREN);
                setState(152);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 248L) != 0)) {
                    {
                        setState(144);
                        term();
                        setState(149);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(145);
                                    match(COMMA);
                                    setState(146);
                                    term();
                                }
                            }
                            setState(151);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(154);
                match(RPAREN);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TermContext extends ParserRuleContext {
        public ConstantContext constant() {
            return getRuleContext(ConstantContext.class, 0);
        }

        public VariableContext variable() {
            return getRuleContext(VariableContext.class, 0);
        }

        public TermContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_term;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterTerm(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitTerm(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor) return ((NeuralogicVisitor<? extends T>) visitor).visitTerm(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TermContext term() throws RecognitionException {
        TermContext _localctx = new TermContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_term);
        try {
            setState(158);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 15, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(156);
                    constant();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(157);
                    variable();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class VariableContext extends ParserRuleContext {
        public TerminalNode VARIABLE() {
            return getToken(NeuralogicParser.VARIABLE, 0);
        }

        public TerminalNode TYPE() {
            return getToken(NeuralogicParser.TYPE, 0);
        }

        public VariableContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_variable;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterVariable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitVariable(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitVariable(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VariableContext variable() throws RecognitionException {
        VariableContext _localctx = new VariableContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_variable);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(161);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TYPE) {
                    {
                        setState(160);
                        match(TYPE);
                    }
                }

                setState(163);
                match(VARIABLE);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ConstantContext extends ParserRuleContext {
        public TerminalNode ATOMIC_NAME() {
            return getToken(NeuralogicParser.ATOMIC_NAME, 0);
        }

        public TerminalNode TYPE() {
            return getToken(NeuralogicParser.TYPE, 0);
        }

        public TerminalNode INT() {
            return getToken(NeuralogicParser.INT, 0);
        }

        public TerminalNode FLOAT() {
            return getToken(NeuralogicParser.FLOAT, 0);
        }

        public ConstantContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_constant;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterConstant(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitConstant(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitConstant(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ConstantContext constant() throws RecognitionException {
        ConstantContext _localctx = new ConstantContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_constant);
        int _la;
        try {
            setState(177);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 20, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(166);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    if (_la == TYPE) {
                        {
                            setState(165);
                            match(TYPE);
                        }
                    }

                    setState(168);
                    match(ATOMIC_NAME);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(170);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    if (_la == TYPE) {
                        {
                            setState(169);
                            match(TYPE);
                        }
                    }

                    setState(172);
                    match(INT);
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(174);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    if (_la == TYPE) {
                        {
                            setState(173);
                            match(TYPE);
                        }
                    }

                    setState(176);
                    match(FLOAT);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PredicateContext extends ParserRuleContext {
        public TerminalNode ATOMIC_NAME() {
            return getToken(NeuralogicParser.ATOMIC_NAME, 0);
        }

        public TerminalNode PRIVATE() {
            return getToken(NeuralogicParser.PRIVATE, 0);
        }

        public TerminalNode SPECIAL() {
            return getToken(NeuralogicParser.SPECIAL, 0);
        }

        public TerminalNode SLASH() {
            return getToken(NeuralogicParser.SLASH, 0);
        }

        public TerminalNode INT() {
            return getToken(NeuralogicParser.INT, 0);
        }

        public PredicateContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_predicate;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterPredicate(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitPredicate(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitPredicate(this);
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
                setState(180);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == PRIVATE) {
                    {
                        setState(179);
                        match(PRIVATE);
                    }
                }

                setState(183);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPECIAL) {
                    {
                        setState(182);
                        match(SPECIAL);
                    }
                }

                setState(185);
                match(ATOMIC_NAME);
                setState(188);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SLASH) {
                    {
                        setState(186);
                        match(SLASH);
                        setState(187);
                        match(INT);
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ConjunctionContext extends ParserRuleContext {
        public List<AtomContext> atom() {
            return getRuleContexts(AtomContext.class);
        }

        public AtomContext atom(int i) {
            return getRuleContext(AtomContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(NeuralogicParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(NeuralogicParser.COMMA, i);
        }

        public ConjunctionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_conjunction;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterConjunction(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitConjunction(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitConjunction(this);
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
                setState(190);
                atom();
                setState(195);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 24, _ctx);
                while (_alt != 2 && _alt != ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(191);
                                match(COMMA);
                                setState(192);
                                atom();
                            }
                        }
                    }
                    setState(197);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 24, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MetadataValContext extends ParserRuleContext {
        public List<TerminalNode> ATOMIC_NAME() {
            return getTokens(NeuralogicParser.ATOMIC_NAME);
        }

        public TerminalNode ATOMIC_NAME(int i) {
            return getToken(NeuralogicParser.ATOMIC_NAME, i);
        }

        public TerminalNode ASSIGN() {
            return getToken(NeuralogicParser.ASSIGN, 0);
        }

        public ValueContext value() {
            return getRuleContext(ValueContext.class, 0);
        }

        public TerminalNode DOLLAR() {
            return getToken(NeuralogicParser.DOLLAR, 0);
        }

        public MetadataValContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_metadataVal;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterMetadataVal(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitMetadataVal(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitMetadataVal(this);
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
                setState(198);
                match(ATOMIC_NAME);
                setState(199);
                match(ASSIGN);
                setState(205);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case INT:
                    case FLOAT:
                    case LCURL:
                    case LBRACKET: {
                        setState(200);
                        value();
                    }
                    break;
                    case ATOMIC_NAME:
                    case DOLLAR: {
                        {
                            setState(202);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DOLLAR) {
                                {
                                    setState(201);
                                    match(DOLLAR);
                                }
                            }

                            setState(204);
                            match(ATOMIC_NAME);
                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MetadataListContext extends ParserRuleContext {
        public TerminalNode LBRACKET() {
            return getToken(NeuralogicParser.LBRACKET, 0);
        }

        public TerminalNode RBRACKET() {
            return getToken(NeuralogicParser.RBRACKET, 0);
        }

        public List<MetadataValContext> metadataVal() {
            return getRuleContexts(MetadataValContext.class);
        }

        public MetadataValContext metadataVal(int i) {
            return getRuleContext(MetadataValContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(NeuralogicParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(NeuralogicParser.COMMA, i);
        }

        public MetadataListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_metadataList;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterMetadataList(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitMetadataList(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitMetadataList(this);
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
                setState(207);
                match(LBRACKET);
                setState(216);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == ATOMIC_NAME) {
                    {
                        setState(208);
                        metadataVal();
                        setState(213);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(209);
                                    match(COMMA);
                                    setState(210);
                                    metadataVal();
                                }
                            }
                            setState(215);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(218);
                match(RBRACKET);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LrnnRuleContext extends ParserRuleContext {
        public AtomContext atom() {
            return getRuleContext(AtomContext.class, 0);
        }

        public ImpliedByContext impliedBy() {
            return getRuleContext(ImpliedByContext.class, 0);
        }

        public ConjunctionContext conjunction() {
            return getRuleContext(ConjunctionContext.class, 0);
        }

        public TerminalNode COMMA() {
            return getToken(NeuralogicParser.COMMA, 0);
        }

        public OffsetContext offset() {
            return getRuleContext(OffsetContext.class, 0);
        }

        public MetadataListContext metadataList() {
            return getRuleContext(MetadataListContext.class, 0);
        }

        public LrnnRuleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_lrnnRule;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterLrnnRule(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitLrnnRule(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitLrnnRule(this);
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
                setState(220);
                atom();
                setState(221);
                impliedBy();
                setState(222);
                conjunction();
                setState(225);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COMMA) {
                    {
                        setState(223);
                        match(COMMA);
                        setState(224);
                        offset();
                    }
                }

                setState(227);
                match(T__0);
                setState(229);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 30, _ctx)) {
                    case 1: {
                        setState(228);
                        metadataList();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PredicateOffsetContext extends ParserRuleContext {
        public PredicateContext predicate() {
            return getRuleContext(PredicateContext.class, 0);
        }

        public WeightContext weight() {
            return getRuleContext(WeightContext.class, 0);
        }

        public PredicateOffsetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_predicateOffset;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterPredicateOffset(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitPredicateOffset(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitPredicateOffset(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PredicateOffsetContext predicateOffset() throws RecognitionException {
        PredicateOffsetContext _localctx = new PredicateOffsetContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_predicateOffset);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(231);
                predicate();
                setState(232);
                weight();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PredicateMetadataContext extends ParserRuleContext {
        public PredicateContext predicate() {
            return getRuleContext(PredicateContext.class, 0);
        }

        public MetadataListContext metadataList() {
            return getRuleContext(MetadataListContext.class, 0);
        }

        public PredicateMetadataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_predicateMetadata;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterPredicateMetadata(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitPredicateMetadata(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitPredicateMetadata(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PredicateMetadataContext predicateMetadata() throws RecognitionException {
        PredicateMetadataContext _localctx = new PredicateMetadataContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_predicateMetadata);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(234);
                predicate();
                setState(235);
                metadataList();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class WeightMetadataContext extends ParserRuleContext {
        public TerminalNode DOLLAR() {
            return getToken(NeuralogicParser.DOLLAR, 0);
        }

        public TerminalNode ATOMIC_NAME() {
            return getToken(NeuralogicParser.ATOMIC_NAME, 0);
        }

        public MetadataListContext metadataList() {
            return getRuleContext(MetadataListContext.class, 0);
        }

        public WeightMetadataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weightMetadata;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterWeightMetadata(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitWeightMetadata(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitWeightMetadata(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WeightMetadataContext weightMetadata() throws RecognitionException {
        WeightMetadataContext _localctx = new WeightMetadataContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_weightMetadata);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(237);
                match(DOLLAR);
                setState(238);
                match(ATOMIC_NAME);
                setState(239);
                metadataList();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TemplateMetadataContext extends ParserRuleContext {
        public MetadataListContext metadataList() {
            return getRuleContext(MetadataListContext.class, 0);
        }

        public TemplateMetadataContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_templateMetadata;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterTemplateMetadata(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitTemplateMetadata(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitTemplateMetadata(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TemplateMetadataContext templateMetadata() throws RecognitionException {
        TemplateMetadataContext _localctx = new TemplateMetadataContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_templateMetadata);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(241);
                metadataList();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class WeightContext extends ParserRuleContext {
        public FixedValueContext fixedValue() {
            return getRuleContext(FixedValueContext.class, 0);
        }

        public ValueContext value() {
            return getRuleContext(ValueContext.class, 0);
        }

        public TerminalNode DOLLAR() {
            return getToken(NeuralogicParser.DOLLAR, 0);
        }

        public TerminalNode ATOMIC_NAME() {
            return getToken(NeuralogicParser.ATOMIC_NAME, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(NeuralogicParser.ASSIGN, 0);
        }

        public WeightContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weight;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterWeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitWeight(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitWeight(this);
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
                setState(246);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DOLLAR) {
                    {
                        setState(243);
                        match(DOLLAR);
                        setState(244);
                        match(ATOMIC_NAME);
                        setState(245);
                        match(ASSIGN);
                    }
                }

                setState(250);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case LANGLE: {
                        setState(248);
                        fixedValue();
                    }
                    break;
                    case INT:
                    case FLOAT:
                    case LCURL:
                    case LBRACKET: {
                        setState(249);
                        value();
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FixedValueContext extends ParserRuleContext {
        public TerminalNode LANGLE() {
            return getToken(NeuralogicParser.LANGLE, 0);
        }

        public ValueContext value() {
            return getRuleContext(ValueContext.class, 0);
        }

        public TerminalNode RANGLE() {
            return getToken(NeuralogicParser.RANGLE, 0);
        }

        public FixedValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fixedValue;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterFixedValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitFixedValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitFixedValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FixedValueContext fixedValue() throws RecognitionException {
        FixedValueContext _localctx = new FixedValueContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_fixedValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(252);
                match(LANGLE);
                setState(253);
                value();
                setState(254);
                match(RANGLE);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class OffsetContext extends ParserRuleContext {
        public WeightContext weight() {
            return getRuleContext(WeightContext.class, 0);
        }

        public OffsetContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_offset;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterOffset(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitOffset(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitOffset(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OffsetContext offset() throws RecognitionException {
        OffsetContext _localctx = new OffsetContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_offset);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(256);
                weight();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ValueContext extends ParserRuleContext {
        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public SparseVectorContext sparseVector() {
            return getRuleContext(SparseVectorContext.class, 0);
        }

        public VectorContext vector() {
            return getRuleContext(VectorContext.class, 0);
        }

        public SparseMatrixContext sparseMatrix() {
            return getRuleContext(SparseMatrixContext.class, 0);
        }

        public MatrixContext matrix() {
            return getRuleContext(MatrixContext.class, 0);
        }

        public DimensionsContext dimensions() {
            return getRuleContext(DimensionsContext.class, 0);
        }

        public ValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_value;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValueContext value() throws RecognitionException {
        ValueContext _localctx = new ValueContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_value);
        try {
            setState(264);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 33, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(258);
                    number();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(259);
                    sparseVector();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(260);
                    vector();
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(261);
                    sparseMatrix();
                }
                break;
                case 5:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(262);
                    matrix();
                }
                break;
                case 6:
                    enterOuterAlt(_localctx, 6);
                {
                    setState(263);
                    dimensions();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NumberContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NeuralogicParser.INT, 0);
        }

        public TerminalNode FLOAT() {
            return getToken(NeuralogicParser.FLOAT, 0);
        }

        public NumberContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_number;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterNumber(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitNumber(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitNumber(this);
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
                setState(266);
                _la = _input.LA(1);
                if (!(_la == INT || _la == FLOAT)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class VectorContext extends ParserRuleContext {
        public TerminalNode LBRACKET() {
            return getToken(NeuralogicParser.LBRACKET, 0);
        }

        public List<NumberContext> number() {
            return getRuleContexts(NumberContext.class);
        }

        public NumberContext number(int i) {
            return getRuleContext(NumberContext.class, i);
        }

        public TerminalNode RBRACKET() {
            return getToken(NeuralogicParser.RBRACKET, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(NeuralogicParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(NeuralogicParser.COMMA, i);
        }

        public VectorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vector;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterVector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitVector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitVector(this);
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
                setState(268);
                match(LBRACKET);
                setState(269);
                number();
                setState(274);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(270);
                            match(COMMA);
                            setState(271);
                            number();
                        }
                    }
                    setState(276);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(277);
                match(RBRACKET);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SparseVectorContext extends ParserRuleContext {
        public TerminalNode LBRACKET() {
            return getToken(NeuralogicParser.LBRACKET, 0);
        }

        public TerminalNode INT() {
            return getToken(NeuralogicParser.INT, 0);
        }

        public TerminalNode PIPE() {
            return getToken(NeuralogicParser.PIPE, 0);
        }

        public TerminalNode RBRACKET() {
            return getToken(NeuralogicParser.RBRACKET, 0);
        }

        public List<ElementContext> element() {
            return getRuleContexts(ElementContext.class);
        }

        public ElementContext element(int i) {
            return getRuleContext(ElementContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(NeuralogicParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(NeuralogicParser.COMMA, i);
        }

        public SparseVectorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sparseVector;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterSparseVector(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitSparseVector(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitSparseVector(this);
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
                setState(279);
                match(LBRACKET);
                setState(280);
                match(INT);
                setState(281);
                match(PIPE);
                setState(291);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case RBRACKET: {
                    }
                    break;
                    case INT:
                    case LPAREN: {
                        setState(283);
                        element();
                        setState(288);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(284);
                                    match(COMMA);
                                    setState(285);
                                    element();
                                }
                            }
                            setState(290);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(293);
                match(RBRACKET);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MatrixContext extends ParserRuleContext {
        public TerminalNode LBRACKET() {
            return getToken(NeuralogicParser.LBRACKET, 0);
        }

        public TerminalNode RBRACKET() {
            return getToken(NeuralogicParser.RBRACKET, 0);
        }

        public List<VectorContext> vector() {
            return getRuleContexts(VectorContext.class);
        }

        public VectorContext vector(int i) {
            return getRuleContext(VectorContext.class, i);
        }

        public MatrixContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matrix;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterMatrix(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitMatrix(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitMatrix(this);
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
                setState(295);
                match(LBRACKET);
                setState(297);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(296);
                            vector();
                        }
                    }
                    setState(299);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == LBRACKET);
                setState(301);
                match(RBRACKET);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SparseMatrixContext extends ParserRuleContext {
        public TerminalNode LBRACKET() {
            return getToken(NeuralogicParser.LBRACKET, 0);
        }

        public List<TerminalNode> INT() {
            return getTokens(NeuralogicParser.INT);
        }

        public TerminalNode INT(int i) {
            return getToken(NeuralogicParser.INT, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(NeuralogicParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(NeuralogicParser.COMMA, i);
        }

        public TerminalNode PIPE() {
            return getToken(NeuralogicParser.PIPE, 0);
        }

        public TerminalNode RBRACKET() {
            return getToken(NeuralogicParser.RBRACKET, 0);
        }

        public List<Element2dContext> element2d() {
            return getRuleContexts(Element2dContext.class);
        }

        public Element2dContext element2d(int i) {
            return getRuleContext(Element2dContext.class, i);
        }

        public SparseMatrixContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sparseMatrix;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterSparseMatrix(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitSparseMatrix(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitSparseMatrix(this);
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
                setState(303);
                match(LBRACKET);
                setState(304);
                match(INT);
                setState(305);
                match(COMMA);
                setState(306);
                match(INT);
                setState(307);
                match(PIPE);
                setState(317);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case RBRACKET: {
                    }
                    break;
                    case INT:
                    case LPAREN: {
                        setState(309);
                        element2d();
                        setState(314);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(310);
                                    match(COMMA);
                                    setState(311);
                                    element2d();
                                }
                            }
                            setState(316);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(319);
                match(RBRACKET);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DimensionsContext extends ParserRuleContext {
        public TerminalNode LCURL() {
            return getToken(NeuralogicParser.LCURL, 0);
        }

        public List<NumberContext> number() {
            return getRuleContexts(NumberContext.class);
        }

        public NumberContext number(int i) {
            return getRuleContext(NumberContext.class, i);
        }

        public TerminalNode RCURL() {
            return getToken(NeuralogicParser.RCURL, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(NeuralogicParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(NeuralogicParser.COMMA, i);
        }

        public DimensionsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dimensions;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterDimensions(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitDimensions(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitDimensions(this);
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
                setState(321);
                match(LCURL);
                setState(322);
                number();
                setState(327);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(323);
                            match(COMMA);
                            setState(324);
                            number();
                        }
                    }
                    setState(329);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(330);
                match(RCURL);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ElementContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(NeuralogicParser.INT, 0);
        }

        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public TerminalNode LPAREN() {
            return getToken(NeuralogicParser.LPAREN, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(NeuralogicParser.RPAREN, 0);
        }

        public ElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_element;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitElement(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ElementContext element() throws RecognitionException {
        ElementContext _localctx = new ElementContext(_ctx, getState());
        enterRule(_localctx, 62, RULE_element);
        try {
            setState(341);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case INT:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(332);
                    match(INT);
                    setState(333);
                    match(T__1);
                    setState(334);
                    number();
                }
                break;
                case LPAREN:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(335);
                    match(LPAREN);
                    setState(336);
                    match(INT);
                    setState(337);
                    match(T__1);
                    setState(338);
                    number();
                    setState(339);
                    match(RPAREN);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class Element2dContext extends ParserRuleContext {
        public List<TerminalNode> INT() {
            return getTokens(NeuralogicParser.INT);
        }

        public TerminalNode INT(int i) {
            return getToken(NeuralogicParser.INT, i);
        }

        public TerminalNode COMMA() {
            return getToken(NeuralogicParser.COMMA, 0);
        }

        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public TerminalNode LPAREN() {
            return getToken(NeuralogicParser.LPAREN, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(NeuralogicParser.RPAREN, 0);
        }

        public Element2dContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_element2d;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterElement2d(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitElement2d(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitElement2d(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Element2dContext element2d() throws RecognitionException {
        Element2dContext _localctx = new Element2dContext(_ctx, getState());
        enterRule(_localctx, 64, RULE_element2d);
        try {
            setState(356);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case INT:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(343);
                    match(INT);
                    setState(344);
                    match(COMMA);
                    setState(345);
                    match(INT);
                    setState(346);
                    match(T__1);
                    setState(347);
                    number();
                }
                break;
                case LPAREN:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(348);
                    match(LPAREN);
                    setState(349);
                    match(INT);
                    setState(350);
                    match(COMMA);
                    setState(351);
                    match(INT);
                    setState(352);
                    match(T__1);
                    setState(353);
                    number();
                    setState(354);
                    match(RPAREN);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NegationContext extends ParserRuleContext {
        public TerminalNode NEGATION() {
            return getToken(NeuralogicParser.NEGATION, 0);
        }

        public TerminalNode SOFTNEGATION() {
            return getToken(NeuralogicParser.SOFTNEGATION, 0);
        }

        public NegationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_negation;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterNegation(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitNegation(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitNegation(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NegationContext negation() throws RecognitionException {
        NegationContext _localctx = new NegationContext(_ctx, getState());
        enterRule(_localctx, 66, RULE_negation);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(358);
                _la = _input.LA(1);
                if (!(_la == NEGATION || _la == SOFTNEGATION)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ImpliedByContext extends ParserRuleContext {
        public TerminalNode IMPLIED_BY() {
            return getToken(NeuralogicParser.IMPLIED_BY, 0);
        }

        public TerminalNode IMPLIED_BY2() {
            return getToken(NeuralogicParser.IMPLIED_BY2, 0);
        }

        public ImpliedByContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_impliedBy;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).enterImpliedBy(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NeuralogicListener) ((NeuralogicListener) listener).exitImpliedBy(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NeuralogicVisitor)
                return ((NeuralogicVisitor<? extends T>) visitor).visitImpliedBy(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ImpliedByContext impliedBy() throws RecognitionException {
        ImpliedByContext _localctx = new ImpliedByContext(_ctx, getState());
        enterRule(_localctx, 68, RULE_impliedBy);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(360);
                _la = _input.LA(1);
                if (!(_la == IMPLIED_BY || _la == IMPLIED_BY2)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN =
            "\u0004\u0001\u001f\u016b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007" +
                    "\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007" +
                    "\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007" +
                    "\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007" +
                    "\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007" +
                    "\"\u0001\u0000\u0005\u0000H\b\u0000\n\u0000\f\u0000K\t\u0000\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0003\u0001V\b\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0004\u0002\\\b\u0002\u000b\u0002\f\u0002]\u0001" +
                    "\u0002\u0004\u0002a\b\u0002\u000b\u0002\f\u0002b\u0003\u0002e\b\u0002" +
                    "\u0001\u0003\u0001\u0003\u0004\u0003i\b\u0003\u000b\u0003\f\u0003j\u0001" +
                    "\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0004\u0005v\b\u0005\u000b\u0005\f\u0005" +
                    "w\u0001\u0005\u0001\u0005\u0001\u0005\u0004\u0005}\b\u0005\u000b\u0005" +
                    "\f\u0005~\u0003\u0005\u0081\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0003\u0007\u0087\b\u0007\u0001\u0007\u0003\u0007\u008a\b" +
                    "\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u008e\b\u0007\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0005\b\u0094\b\b\n\b\f\b\u0097\t\b\u0003\b\u0099\b" +
                    "\b\u0001\b\u0001\b\u0001\t\u0001\t\u0003\t\u009f\b\t\u0001\n\u0003\n\u00a2" +
                    "\b\n\u0001\n\u0001\n\u0001\u000b\u0003\u000b\u00a7\b\u000b\u0001\u000b" +
                    "\u0001\u000b\u0003\u000b\u00ab\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b" +
                    "\u00af\b\u000b\u0001\u000b\u0003\u000b\u00b2\b\u000b\u0001\f\u0003\f\u00b5" +
                    "\b\f\u0001\f\u0003\f\u00b8\b\f\u0001\f\u0001\f\u0001\f\u0003\f\u00bd\b" +
                    "\f\u0001\r\u0001\r\u0001\r\u0005\r\u00c2\b\r\n\r\f\r\u00c5\t\r\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00cb\b\u000e\u0001\u000e" +
                    "\u0003\u000e\u00ce\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0005\u000f\u00d4\b\u000f\n\u000f\f\u000f\u00d7\t\u000f\u0003\u000f\u00d9" +
                    "\b\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0003\u0010\u00e2\b\u0010\u0001\u0010\u0001\u0010\u0003" +
                    "\u0010\u00e6\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00f7" +
                    "\b\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00fb\b\u0015\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018" +
                    "\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018" +
                    "\u0109\b\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a" +
                    "\u0001\u001a\u0005\u001a\u0111\b\u001a\n\u001a\f\u001a\u0114\t\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0005\u001b\u011f\b\u001b\n\u001b\f\u001b" +
                    "\u0122\t\u001b\u0003\u001b\u0124\b\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001c\u0001\u001c\u0004\u001c\u012a\b\u001c\u000b\u001c\f\u001c\u012b" +
                    "\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d" +
                    "\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0005\u001d" +
                    "\u0139\b\u001d\n\u001d\f\u001d\u013c\t\u001d\u0003\u001d\u013e\b\u001d" +
                    "\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e" +
                    "\u0005\u001e\u0146\b\u001e\n\u001e\f\u001e\u0149\t\u001e\u0001\u001e\u0001" +
                    "\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0156\b\u001f\u0001" +
                    " \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001" +
                    " \u0001 \u0001 \u0003 \u0165\b \u0001!\u0001!\u0001\"\u0001\"\u0001\"" +
                    "\u0000\u0000#\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016" +
                    "\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BD\u0000\u0003\u0001\u0000\u0005" +
                    "\u0006\u0001\u0000\u0018\u0019\u0001\u0000\b\t\u017c\u0000I\u0001\u0000" +
                    "\u0000\u0000\u0002U\u0001\u0000\u0000\u0000\u0004d\u0001\u0000\u0000\u0000" +
                    "\u0006h\u0001\u0000\u0000\u0000\bn\u0001\u0000\u0000\u0000\n\u0080\u0001" +
                    "\u0000\u0000\u0000\f\u0082\u0001\u0000\u0000\u0000\u000e\u0086\u0001\u0000" +
                    "\u0000\u0000\u0010\u008f\u0001\u0000\u0000\u0000\u0012\u009e\u0001\u0000" +
                    "\u0000\u0000\u0014\u00a1\u0001\u0000\u0000\u0000\u0016\u00b1\u0001\u0000" +
                    "\u0000\u0000\u0018\u00b4\u0001\u0000\u0000\u0000\u001a\u00be\u0001\u0000" +
                    "\u0000\u0000\u001c\u00c6\u0001\u0000\u0000\u0000\u001e\u00cf\u0001\u0000" +
                    "\u0000\u0000 \u00dc\u0001\u0000\u0000\u0000\"\u00e7\u0001\u0000\u0000" +
                    "\u0000$\u00ea\u0001\u0000\u0000\u0000&\u00ed\u0001\u0000\u0000\u0000(" +
                    "\u00f1\u0001\u0000\u0000\u0000*\u00f6\u0001\u0000\u0000\u0000,\u00fc\u0001" +
                    "\u0000\u0000\u0000.\u0100\u0001\u0000\u0000\u00000\u0108\u0001\u0000\u0000" +
                    "\u00002\u010a\u0001\u0000\u0000\u00004\u010c\u0001\u0000\u0000\u00006" +
                    "\u0117\u0001\u0000\u0000\u00008\u0127\u0001\u0000\u0000\u0000:\u012f\u0001" +
                    "\u0000\u0000\u0000<\u0141\u0001\u0000\u0000\u0000>\u0155\u0001\u0000\u0000" +
                    "\u0000@\u0164\u0001\u0000\u0000\u0000B\u0166\u0001\u0000\u0000\u0000D" +
                    "\u0168\u0001\u0000\u0000\u0000FH\u0003\u0002\u0001\u0000GF\u0001\u0000" +
                    "\u0000\u0000HK\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000IJ\u0001" +
                    "\u0000\u0000\u0000J\u0001\u0001\u0000\u0000\u0000KI\u0001\u0000\u0000" +
                    "\u0000LV\u0003 \u0010\u0000MV\u0003\f\u0006\u0000NO\u0003\u001a\r\u0000" +
                    "OP\u0005\u0001\u0000\u0000PV\u0001\u0000\u0000\u0000QV\u0003$\u0012\u0000" +
                    "RV\u0003\"\u0011\u0000SV\u0003&\u0013\u0000TV\u0003(\u0014\u0000UL\u0001" +
                    "\u0000\u0000\u0000UM\u0001\u0000\u0000\u0000UN\u0001\u0000\u0000\u0000" +
                    "UQ\u0001\u0000\u0000\u0000UR\u0001\u0000\u0000\u0000US\u0001\u0000\u0000" +
                    "\u0000UT\u0001\u0000\u0000\u0000V\u0003\u0001\u0000\u0000\u0000WX\u0003" +
                    "\b\u0004\u0000XY\u0003D\"\u0000YZ\u0003\u0006\u0003\u0000Z\\\u0001\u0000" +
                    "\u0000\u0000[W\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000][\u0001" +
                    "\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^e\u0001\u0000\u0000\u0000" +
                    "_a\u0003\u0006\u0003\u0000`_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000" +
                    "\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000ce\u0001\u0000" +
                    "\u0000\u0000d[\u0001\u0000\u0000\u0000d`\u0001\u0000\u0000\u0000e\u0005" +
                    "\u0001\u0000\u0000\u0000fi\u0003 \u0010\u0000gi\u0003\u001a\r\u0000hf" +
                    "\u0001\u0000\u0000\u0000hg\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000" +
                    "\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000kl\u0001\u0000" +
                    "\u0000\u0000lm\u0005\u0001\u0000\u0000m\u0007\u0001\u0000\u0000\u0000" +
                    "no\u0003\u001a\r\u0000o\t\u0001\u0000\u0000\u0000pq\u0003\u000e\u0007" +
                    "\u0000qr\u0003D\"\u0000rs\u0003\u001a\r\u0000st\u0005\u0001\u0000\u0000" +
                    "tv\u0001\u0000\u0000\u0000up\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000" +
                    "\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000x\u0081\u0001" +
                    "\u0000\u0000\u0000yz\u0003\u001a\r\u0000z{\u0005\u0001\u0000\u0000{}\u0001" +
                    "\u0000\u0000\u0000|y\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000" +
                    "~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0081" +
                    "\u0001\u0000\u0000\u0000\u0080u\u0001\u0000\u0000\u0000\u0080|\u0001\u0000" +
                    "\u0000\u0000\u0081\u000b\u0001\u0000\u0000\u0000\u0082\u0083\u0003\u000e" +
                    "\u0007\u0000\u0083\u0084\u0005\u0001\u0000\u0000\u0084\r\u0001\u0000\u0000" +
                    "\u0000\u0085\u0087\u0003*\u0015\u0000\u0086\u0085\u0001\u0000\u0000\u0000" +
                    "\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u0089\u0001\u0000\u0000\u0000" +
                    "\u0088\u008a\u0003B!\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u0089\u008a" +
                    "\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008d" +
                    "\u0003\u0018\f\u0000\u008c\u008e\u0003\u0010\b\u0000\u008d\u008c\u0001" +
                    "\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u000f\u0001" +
                    "\u0000\u0000\u0000\u008f\u0098\u0005\u0011\u0000\u0000\u0090\u0095\u0003" +
                    "\u0012\t\u0000\u0091\u0092\u0005\u0013\u0000\u0000\u0092\u0094\u0003\u0012" +
                    "\t\u0000\u0093\u0091\u0001\u0000\u0000\u0000\u0094\u0097\u0001\u0000\u0000" +
                    "\u0000\u0095\u0093\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000" +
                    "\u0000\u0096\u0099\u0001\u0000\u0000\u0000\u0097\u0095\u0001\u0000\u0000" +
                    "\u0000\u0098\u0090\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000" +
                    "\u0000\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009b\u0005\u0012\u0000" +
                    "\u0000\u009b\u0011\u0001\u0000\u0000\u0000\u009c\u009f\u0003\u0016\u000b" +
                    "\u0000\u009d\u009f\u0003\u0014\n\u0000\u009e\u009c\u0001\u0000\u0000\u0000" +
                    "\u009e\u009d\u0001\u0000\u0000\u0000\u009f\u0013\u0001\u0000\u0000\u0000" +
                    "\u00a0\u00a2\u0005\u0004\u0000\u0000\u00a1\u00a0\u0001\u0000\u0000\u0000" +
                    "\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000" +
                    "\u00a3\u00a4\u0005\u0003\u0000\u0000\u00a4\u0015\u0001\u0000\u0000\u0000" +
                    "\u00a5\u00a7\u0005\u0004\u0000\u0000\u00a6\u00a5\u0001\u0000\u0000\u0000" +
                    "\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000" +
                    "\u00a8\u00b2\u0005\u0007\u0000\u0000\u00a9\u00ab\u0005\u0004\u0000\u0000" +
                    "\u00aa\u00a9\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000" +
                    "\u00ab\u00ac\u0001\u0000\u0000\u0000\u00ac\u00b2\u0005\u0005\u0000\u0000" +
                    "\u00ad\u00af\u0005\u0004\u0000\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000" +
                    "\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000" +
                    "\u00b0\u00b2\u0005\u0006\u0000\u0000\u00b1\u00a6\u0001\u0000\u0000\u0000" +
                    "\u00b1\u00aa\u0001\u0000\u0000\u0000\u00b1\u00ae\u0001\u0000\u0000\u0000" +
                    "\u00b2\u0017\u0001\u0000\u0000\u0000\u00b3\u00b5\u0005\u001b\u0000\u0000" +
                    "\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000" +
                    "\u00b5\u00b7\u0001\u0000\u0000\u0000\u00b6\u00b8\u0005\u001a\u0000\u0000" +
                    "\u00b7\u00b6\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000" +
                    "\u00b8\u00b9\u0001\u0000\u0000\u0000\u00b9\u00bc\u0005\u0007\u0000\u0000" +
                    "\u00ba\u00bb\u0005\u0014\u0000\u0000\u00bb\u00bd\u0005\u0005\u0000\u0000" +
                    "\u00bc\u00ba\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000" +
                    "\u00bd\u0019\u0001\u0000\u0000\u0000\u00be\u00c3\u0003\u000e\u0007\u0000" +
                    "\u00bf\u00c0\u0005\u0013\u0000\u0000\u00c0\u00c2\u0003\u000e\u0007\u0000" +
                    "\u00c1\u00bf\u0001\u0000\u0000\u0000\u00c2\u00c5\u0001\u0000\u0000\u0000" +
                    "\u00c3\u00c1\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000\u0000" +
                    "\u00c4\u001b\u0001\u0000\u0000\u0000\u00c5\u00c3\u0001\u0000\u0000\u0000" +
                    "\u00c6\u00c7\u0005\u0007\u0000\u0000\u00c7\u00cd\u0005\n\u0000\u0000\u00c8" +
                    "\u00ce\u00030\u0018\u0000\u00c9\u00cb\u0005\u0017\u0000\u0000\u00ca\u00c9" +
                    "\u0001\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\u00cc" +
                    "\u0001\u0000\u0000\u0000\u00cc\u00ce\u0005\u0007\u0000\u0000\u00cd\u00c8" +
                    "\u0001\u0000\u0000\u0000\u00cd\u00ca\u0001\u0000\u0000\u0000\u00ce\u001d" +
                    "\u0001\u0000\u0000\u0000\u00cf\u00d8\u0005\u000f\u0000\u0000\u00d0\u00d5" +
                    "\u0003\u001c\u000e\u0000\u00d1\u00d2\u0005\u0013\u0000\u0000\u00d2\u00d4" +
                    "\u0003\u001c\u000e\u0000\u00d3\u00d1\u0001\u0000\u0000\u0000\u00d4\u00d7" +
                    "\u0001\u0000\u0000\u0000\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d5\u00d6" +
                    "\u0001\u0000\u0000\u0000\u00d6\u00d9\u0001\u0000\u0000\u0000\u00d7\u00d5" +
                    "\u0001\u0000\u0000\u0000\u00d8\u00d0\u0001\u0000\u0000\u0000\u00d8\u00d9" +
                    "\u0001\u0000\u0000\u0000\u00d9\u00da\u0001\u0000\u0000\u0000\u00da\u00db" +
                    "\u0005\u0010\u0000\u0000\u00db\u001f\u0001\u0000\u0000\u0000\u00dc\u00dd" +
                    "\u0003\u000e\u0007\u0000\u00dd\u00de\u0003D\"\u0000\u00de\u00e1\u0003" +
                    "\u001a\r\u0000\u00df\u00e0\u0005\u0013\u0000\u0000\u00e0\u00e2\u0003." +
                    "\u0017\u0000\u00e1\u00df\u0001\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000" +
                    "\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000\u0000\u00e3\u00e5\u0005\u0001" +
                    "\u0000\u0000\u00e4\u00e6\u0003\u001e\u000f\u0000\u00e5\u00e4\u0001\u0000" +
                    "\u0000\u0000\u00e5\u00e6\u0001\u0000\u0000\u0000\u00e6!\u0001\u0000\u0000" +
                    "\u0000\u00e7\u00e8\u0003\u0018\f\u0000\u00e8\u00e9\u0003*\u0015\u0000" +
                    "\u00e9#\u0001\u0000\u0000\u0000\u00ea\u00eb\u0003\u0018\f\u0000\u00eb" +
                    "\u00ec\u0003\u001e\u000f\u0000\u00ec%\u0001\u0000\u0000\u0000\u00ed\u00ee" +
                    "\u0005\u0017\u0000\u0000\u00ee\u00ef\u0005\u0007\u0000\u0000\u00ef\u00f0" +
                    "\u0003\u001e\u000f\u0000\u00f0\'\u0001\u0000\u0000\u0000\u00f1\u00f2\u0003" +
                    "\u001e\u000f\u0000\u00f2)\u0001\u0000\u0000\u0000\u00f3\u00f4\u0005\u0017" +
                    "\u0000\u0000\u00f4\u00f5\u0005\u0007\u0000\u0000\u00f5\u00f7\u0005\n\u0000" +
                    "\u0000\u00f6\u00f3\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000\u0000" +
                    "\u0000\u00f7\u00fa\u0001\u0000\u0000\u0000\u00f8\u00fb\u0003,\u0016\u0000" +
                    "\u00f9\u00fb\u00030\u0018\u0000\u00fa\u00f8\u0001\u0000\u0000\u0000\u00fa" +
                    "\u00f9\u0001\u0000\u0000\u0000\u00fb+\u0001\u0000\u0000\u0000\u00fc\u00fd" +
                    "\u0005\r\u0000\u0000\u00fd\u00fe\u00030\u0018\u0000\u00fe\u00ff\u0005" +
                    "\u000e\u0000\u0000\u00ff-\u0001\u0000\u0000\u0000\u0100\u0101\u0003*\u0015" +
                    "\u0000\u0101/\u0001\u0000\u0000\u0000\u0102\u0109\u00032\u0019\u0000\u0103" +
                    "\u0109\u00036\u001b\u0000\u0104\u0109\u00034\u001a\u0000\u0105\u0109\u0003" +
                    ":\u001d\u0000\u0106\u0109\u00038\u001c\u0000\u0107\u0109\u0003<\u001e" +
                    "\u0000\u0108\u0102\u0001\u0000\u0000\u0000\u0108\u0103\u0001\u0000\u0000" +
                    "\u0000\u0108\u0104\u0001\u0000\u0000\u0000\u0108\u0105\u0001\u0000\u0000" +
                    "\u0000\u0108\u0106\u0001\u0000\u0000\u0000\u0108\u0107\u0001\u0000\u0000" +
                    "\u0000\u01091\u0001\u0000\u0000\u0000\u010a\u010b\u0007\u0000\u0000\u0000" +
                    "\u010b3\u0001\u0000\u0000\u0000\u010c\u010d\u0005\u000f\u0000\u0000\u010d" +
                    "\u0112\u00032\u0019\u0000\u010e\u010f\u0005\u0013\u0000\u0000\u010f\u0111" +
                    "\u00032\u0019\u0000\u0110\u010e\u0001\u0000\u0000\u0000\u0111\u0114\u0001" +
                    "\u0000\u0000\u0000\u0112\u0110\u0001\u0000\u0000\u0000\u0112\u0113\u0001" +
                    "\u0000\u0000\u0000\u0113\u0115\u0001\u0000\u0000\u0000\u0114\u0112\u0001" +
                    "\u0000\u0000\u0000\u0115\u0116\u0005\u0010\u0000\u0000\u01165\u0001\u0000" +
                    "\u0000\u0000\u0117\u0118\u0005\u000f\u0000\u0000\u0118\u0119\u0005\u0005" +
                    "\u0000\u0000\u0119\u0123\u0005\u001c\u0000\u0000\u011a\u0124\u0001\u0000" +
                    "\u0000\u0000\u011b\u0120\u0003>\u001f\u0000\u011c\u011d\u0005\u0013\u0000" +
                    "\u0000\u011d\u011f\u0003>\u001f\u0000\u011e\u011c\u0001\u0000\u0000\u0000" +
                    "\u011f\u0122\u0001\u0000\u0000\u0000\u0120\u011e\u0001\u0000\u0000\u0000" +
                    "\u0120\u0121\u0001\u0000\u0000\u0000\u0121\u0124\u0001\u0000\u0000\u0000" +
                    "\u0122\u0120\u0001\u0000\u0000\u0000\u0123\u011a\u0001\u0000\u0000\u0000" +
                    "\u0123\u011b\u0001\u0000\u0000\u0000\u0124\u0125\u0001\u0000\u0000\u0000" +
                    "\u0125\u0126\u0005\u0010\u0000\u0000\u01267\u0001\u0000\u0000\u0000\u0127" +
                    "\u0129\u0005\u000f\u0000\u0000\u0128\u012a\u00034\u001a\u0000\u0129\u0128" +
                    "\u0001\u0000\u0000\u0000\u012a\u012b\u0001\u0000\u0000\u0000\u012b\u0129" +
                    "\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000\u012c\u012d" +
                    "\u0001\u0000\u0000\u0000\u012d\u012e\u0005\u0010\u0000\u0000\u012e9\u0001" +
                    "\u0000\u0000\u0000\u012f\u0130\u0005\u000f\u0000\u0000\u0130\u0131\u0005" +
                    "\u0005\u0000\u0000\u0131\u0132\u0005\u0013\u0000\u0000\u0132\u0133\u0005" +
                    "\u0005\u0000\u0000\u0133\u013d\u0005\u001c\u0000\u0000\u0134\u013e\u0001" +
                    "\u0000\u0000\u0000\u0135\u013a\u0003@ \u0000\u0136\u0137\u0005\u0013\u0000" +
                    "\u0000\u0137\u0139\u0003@ \u0000\u0138\u0136\u0001\u0000\u0000\u0000\u0139" +
                    "\u013c\u0001\u0000\u0000\u0000\u013a\u0138\u0001\u0000\u0000\u0000\u013a" +
                    "\u013b\u0001\u0000\u0000\u0000\u013b\u013e\u0001\u0000\u0000\u0000\u013c" +
                    "\u013a\u0001\u0000\u0000\u0000\u013d\u0134\u0001\u0000\u0000\u0000\u013d" +
                    "\u0135\u0001\u0000\u0000\u0000\u013e\u013f\u0001\u0000\u0000\u0000\u013f" +
                    "\u0140\u0005\u0010\u0000\u0000\u0140;\u0001\u0000\u0000\u0000\u0141\u0142" +
                    "\u0005\u000b\u0000\u0000\u0142\u0147\u00032\u0019\u0000\u0143\u0144\u0005" +
                    "\u0013\u0000\u0000\u0144\u0146\u00032\u0019\u0000\u0145\u0143\u0001\u0000" +
                    "\u0000\u0000\u0146\u0149\u0001\u0000\u0000\u0000\u0147\u0145\u0001\u0000" +
                    "\u0000\u0000\u0147\u0148\u0001\u0000\u0000\u0000\u0148\u014a\u0001\u0000" +
                    "\u0000\u0000\u0149\u0147\u0001\u0000\u0000\u0000\u014a\u014b\u0005\f\u0000" +
                    "\u0000\u014b=\u0001\u0000\u0000\u0000\u014c\u014d\u0005\u0005\u0000\u0000" +
                    "\u014d\u014e\u0005\u0002\u0000\u0000\u014e\u0156\u00032\u0019\u0000\u014f" +
                    "\u0150\u0005\u0011\u0000\u0000\u0150\u0151\u0005\u0005\u0000\u0000\u0151" +
                    "\u0152\u0005\u0002\u0000\u0000\u0152\u0153\u00032\u0019\u0000\u0153\u0154" +
                    "\u0005\u0012\u0000\u0000\u0154\u0156\u0001\u0000\u0000\u0000\u0155\u014c" +
                    "\u0001\u0000\u0000\u0000\u0155\u014f\u0001\u0000\u0000\u0000\u0156?\u0001" +
                    "\u0000\u0000\u0000\u0157\u0158\u0005\u0005\u0000\u0000\u0158\u0159\u0005" +
                    "\u0013\u0000\u0000\u0159\u015a\u0005\u0005\u0000\u0000\u015a\u015b\u0005" +
                    "\u0002\u0000\u0000\u015b\u0165\u00032\u0019\u0000\u015c\u015d\u0005\u0011" +
                    "\u0000\u0000\u015d\u015e\u0005\u0005\u0000\u0000\u015e\u015f\u0005\u0013" +
                    "\u0000\u0000\u015f\u0160\u0005\u0005\u0000\u0000\u0160\u0161\u0005\u0002" +
                    "\u0000\u0000\u0161\u0162\u00032\u0019\u0000\u0162\u0163\u0005\u0012\u0000" +
                    "\u0000\u0163\u0165\u0001\u0000\u0000\u0000\u0164\u0157\u0001\u0000\u0000" +
                    "\u0000\u0164\u015c\u0001\u0000\u0000\u0000\u0165A\u0001\u0000\u0000\u0000" +
                    "\u0166\u0167\u0007\u0001\u0000\u0000\u0167C\u0001\u0000\u0000\u0000\u0168" +
                    "\u0169\u0007\u0002\u0000\u0000\u0169E\u0001\u0000\u0000\u0000+IU]bdhj" +
                    "w~\u0080\u0086\u0089\u008d\u0095\u0098\u009e\u00a1\u00a6\u00aa\u00ae\u00b1" +
                    "\u00b4\u00b7\u00bc\u00c3\u00ca\u00cd\u00d5\u00d8\u00e1\u00e5\u00f6\u00fa" +
                    "\u0108\u0112\u0120\u0123\u012b\u013a\u013d\u0147\u0155\u0164";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}