package neuralogic;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import parsers.neuralogic.neuralogicListener;
import parsers.neuralogic.neuralogicParser;

import java.util.logging.Logger;

/**
 * Created by gusta on 27.2.18.
 */
public class TemplateListener implements neuralogicListener {

    private static final Logger LOG = Logger.getLogger(TemplateListener.class.getName());

    @Override
    public void enterTemplate_file(neuralogicParser.Template_fileContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTemplate_file(neuralogicParser.Template_fileContext ctx) {

    }

    @Override
    public void enterTemplate_line(neuralogicParser.Template_lineContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTemplate_line(neuralogicParser.Template_lineContext ctx) {

    }

    @Override
    public void enterExamples_file(neuralogicParser.Examples_fileContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitExamples_file(neuralogicParser.Examples_fileContext ctx) {

    }

    @Override
    public void enterFact(neuralogicParser.FactContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitFact(neuralogicParser.FactContext ctx) {

    }

    @Override
    public void enterAtom(neuralogicParser.AtomContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitAtom(neuralogicParser.AtomContext ctx) {

    }

    @Override
    public void enterTerm_list(neuralogicParser.Term_listContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTerm_list(neuralogicParser.Term_listContext ctx) {

    }

    @Override
    public void enterTerm(neuralogicParser.TermContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitTerm(neuralogicParser.TermContext ctx) {

    }

    @Override
    public void enterVariable(neuralogicParser.VariableContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitVariable(neuralogicParser.VariableContext ctx) {

    }

    @Override
    public void enterConstant(neuralogicParser.ConstantContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitConstant(neuralogicParser.ConstantContext ctx) {

    }

    @Override
    public void enterPredicate(neuralogicParser.PredicateContext ctx) {
        LOG.info(ctx.getText());
    }

    @Override
    public void exitPredicate(neuralogicParser.PredicateContext ctx) {

    }

    @Override
    public void enterSpecial_predicate(neuralogicParser.Special_predicateContext ctx) {

    }

    @Override
    public void exitSpecial_predicate(neuralogicParser.Special_predicateContext ctx) {

    }

    @Override
    public void enterConjunction(neuralogicParser.ConjunctionContext ctx) {

    }

    @Override
    public void exitConjunction(neuralogicParser.ConjunctionContext ctx) {

    }

    @Override
    public void enterMetadata_val(neuralogicParser.Metadata_valContext ctx) {

    }

    @Override
    public void exitMetadata_val(neuralogicParser.Metadata_valContext ctx) {

    }

    @Override
    public void enterMetadata_list(neuralogicParser.Metadata_listContext ctx) {

    }

    @Override
    public void exitMetadata_list(neuralogicParser.Metadata_listContext ctx) {

    }

    @Override
    public void enterLrnn_rule(neuralogicParser.Lrnn_ruleContext ctx) {

    }

    @Override
    public void exitLrnn_rule(neuralogicParser.Lrnn_ruleContext ctx) {

    }

    @Override
    public void enterPredicate_offset(neuralogicParser.Predicate_offsetContext ctx) {

    }

    @Override
    public void exitPredicate_offset(neuralogicParser.Predicate_offsetContext ctx) {

    }

    @Override
    public void enterPredicate_metadata(neuralogicParser.Predicate_metadataContext ctx) {

    }

    @Override
    public void exitPredicate_metadata(neuralogicParser.Predicate_metadataContext ctx) {

    }

    @Override
    public void enterWeight(neuralogicParser.WeightContext ctx) {

    }

    @Override
    public void exitWeight(neuralogicParser.WeightContext ctx) {

    }

    @Override
    public void enterFixed_weight(neuralogicParser.Fixed_weightContext ctx) {

    }

    @Override
    public void exitFixed_weight(neuralogicParser.Fixed_weightContext ctx) {

    }

    @Override
    public void enterOffset(neuralogicParser.OffsetContext ctx) {

    }

    @Override
    public void exitOffset(neuralogicParser.OffsetContext ctx) {

    }

    @Override
    public void enterNumber(neuralogicParser.NumberContext ctx) {

    }

    @Override
    public void exitNumber(neuralogicParser.NumberContext ctx) {

    }

    @Override
    public void enterVector(neuralogicParser.VectorContext ctx) {

    }

    @Override
    public void exitVector(neuralogicParser.VectorContext ctx) {

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
