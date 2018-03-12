package neuralogic.alternatives;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import parsers.neuralogic.NeuralogicListener;
import parsers.neuralogic.NeuralogicParser;

import java.util.logging.Logger;

/**
 * Created by gusta on 27.2.18.
 */
public class TemplateListener implements NeuralogicListener {

    private static final Logger LOG = Logger.getLogger(TemplateListener.class.getName());

    @Override
    public void enterTemplate_file(NeuralogicParser.Template_fileContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTemplate_file(NeuralogicParser.Template_fileContext ctx) {

    }

    @Override
    public void enterTemplate_line(NeuralogicParser.Template_lineContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTemplate_line(NeuralogicParser.Template_lineContext ctx) {

    }

    @Override
    public void enterExamples_file(NeuralogicParser.Examples_fileContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitExamples_file(NeuralogicParser.Examples_fileContext ctx) {

    }

    @Override
    public void enterFact(NeuralogicParser.FactContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitFact(NeuralogicParser.FactContext ctx) {

    }

    @Override
    public void enterAtom(NeuralogicParser.AtomContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitAtom(NeuralogicParser.AtomContext ctx) {

    }

    @Override
    public void enterTerm_list(NeuralogicParser.Term_listContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTerm_list(NeuralogicParser.Term_listContext ctx) {

    }

    @Override
    public void enterTerm(NeuralogicParser.TermContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTerm(NeuralogicParser.TermContext ctx) {

    }

    @Override
    public void enterVariable(NeuralogicParser.VariableContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitVariable(NeuralogicParser.VariableContext ctx) {

    }

    @Override
    public void enterConstant(NeuralogicParser.ConstantContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitConstant(NeuralogicParser.ConstantContext ctx) {

    }

    @Override
    public void enterPredicate(NeuralogicParser.PredicateContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitPredicate(NeuralogicParser.PredicateContext ctx) {

    }

    @Override
    public void enterSpecial_predicate(NeuralogicParser.Special_predicateContext ctx) {

    }

    @Override
    public void exitSpecial_predicate(NeuralogicParser.Special_predicateContext ctx) {

    }

    @Override
    public void enterConjunction(NeuralogicParser.ConjunctionContext ctx) {

    }

    @Override
    public void exitConjunction(NeuralogicParser.ConjunctionContext ctx) {

    }

    @Override
    public void enterMetadata_val(NeuralogicParser.Metadata_valContext ctx) {

    }

    @Override
    public void exitMetadata_val(NeuralogicParser.Metadata_valContext ctx) {

    }

    @Override
    public void enterMetadata_list(NeuralogicParser.Metadata_listContext ctx) {

    }

    @Override
    public void exitMetadata_list(NeuralogicParser.Metadata_listContext ctx) {

    }

    @Override
    public void enterLrnn_rule(NeuralogicParser.Lrnn_ruleContext ctx) {

    }

    @Override
    public void exitLrnn_rule(NeuralogicParser.Lrnn_ruleContext ctx) {

    }

    @Override
    public void enterPredicate_offset(NeuralogicParser.Predicate_offsetContext ctx) {

    }

    @Override
    public void exitPredicate_offset(NeuralogicParser.Predicate_offsetContext ctx) {

    }

    @Override
    public void enterPredicate_metadata(NeuralogicParser.Predicate_metadataContext ctx) {

    }

    @Override
    public void exitPredicate_metadata(NeuralogicParser.Predicate_metadataContext ctx) {

    }

    @Override
    public void enterWeight_metadata(NeuralogicParser.Weight_metadataContext ctx) {

    }

    @Override
    public void exitWeight_metadata(NeuralogicParser.Weight_metadataContext ctx) {

    }

    @Override
    public void enterWeight(NeuralogicParser.WeightContext ctx) {

    }

    @Override
    public void exitWeight(NeuralogicParser.WeightContext ctx) {

    }

    @Override
    public void enterFixed_value(NeuralogicParser.Fixed_valueContext ctx) {

    }

    @Override
    public void exitFixed_value(NeuralogicParser.Fixed_valueContext ctx) {

    }

    @Override
    public void enterFixed_weight(NeuralogicParser.Fixed_weightContext ctx) {

    }

    @Override
    public void exitFixed_weight(NeuralogicParser.Fixed_weightContext ctx) {

    }

    @Override
    public void enterOffset(NeuralogicParser.OffsetContext ctx) {

    }

    @Override
    public void exitOffset(NeuralogicParser.OffsetContext ctx) {

    }

    @Override
    public void enterValue(NeuralogicParser.ValueContext ctx) {

    }

    @Override
    public void exitValue(NeuralogicParser.ValueContext ctx) {

    }

    @Override
    public void enterNumber(NeuralogicParser.NumberContext ctx) {

    }

    @Override
    public void exitNumber(NeuralogicParser.NumberContext ctx) {

    }

    @Override
    public void enterVector(NeuralogicParser.VectorContext ctx) {

    }

    @Override
    public void exitVector(NeuralogicParser.VectorContext ctx) {

    }

    @Override
    public void enterNegation(NeuralogicParser.NegationContext ctx) {

    }

    @Override
    public void exitNegation(NeuralogicParser.NegationContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode node) {

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

    }
}
